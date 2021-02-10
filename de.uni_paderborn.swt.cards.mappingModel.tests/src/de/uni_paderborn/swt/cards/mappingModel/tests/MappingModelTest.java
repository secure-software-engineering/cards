package de.uni_paderborn.swt.cards.mappingModel.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import com.google.inject.Injector;

import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataGroup;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Sanitizer;
import de.uni_paderborn.swt.cards.dsl.tmdsl.TmdslFactory;
import de.uni_paderborn.swt.cards.mappingModel.ComponentMapping;
import de.uni_paderborn.swt.cards.mappingModel.GroupFilterMapping;
import de.uni_paderborn.swt.cards.mappingModel.InPortMapping;
import de.uni_paderborn.swt.cards.mappingModel.Mapping;
import de.uni_paderborn.swt.cards.mappingModel.MappingModelFactory;
import de.uni_paderborn.swt.cards.mappingModel.MappingModelHelper;
import de.uni_paderborn.swt.cards.mappingModel.OutPortMapping;
import de.uni_paderborn.swt.cards.mappingModel.SanitizerMapping;
import de.uni_paderborn.swt.cards.mappingModel.SinkMapping;
import de.uni_paderborn.swt.cards.mappingModel.SourceMapping;
import de.uni_paderborn.swt.cards.dsl.TMDslStandaloneSetup;

class MappingModelTest {
	static Model model = null;
	
	@BeforeAll
	static void setup() {
		try {
			Injector injector = new TMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
			XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
			resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
			
			String path = "models/MyTmdsl.tmdsl";
			File file = new File(path);
			
			URI emfURI = URI.createFileURI(file.getAbsolutePath());
			
			Resource resource = resourceSet.getResource(emfURI, true);
			model = (Model) resource.getContents().get(0);
	

		} catch (StackOverflowError e) {
			fail();
		}
	}

	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct missing mappings for components")
	void testComponents() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		for (Component component : model.getSystem().getComponents()) {
			int addedElements = 0;
			List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, component);
			
			assert(list.stream().anyMatch(e -> e instanceof ComponentMapping && ((ComponentMapping) e).getComponent().equals(component)));
			assert(list.stream().anyMatch(e -> e instanceof SinkMapping && ((SinkMapping) e).getComponent().equals(component)));
			addedElements += 2;
			if (component instanceof AtomicComponent) {
				for (DataType dt : ((AtomicComponent) component).getSource4Data()) {
					assert(list.stream().anyMatch(e -> e instanceof SourceMapping && ((SourceMapping) e).getComponent().equals(component) && ((SourceMapping) e).getDataType().equals(dt)));
					addedElements++;
				}
			}
			assert(list.size() == addedElements);
		}
	}

	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct missing mappings for ports")
	void testPorts() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		for (Port port : EcoreUtil2.getAllContentsOfType(model, Port.class)) {
			int addedElements = 0;
			List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, port);
			
			if (port.getPortType() == PortType.IN || port.getPortType() == PortType.INOUT) {
				assert(list.stream().anyMatch(e -> e instanceof InPortMapping && ((InPortMapping) e).getPort().equals(port)));
				addedElements++;
			}
			if (port.getPortType() == PortType.OUT || port.getPortType() == PortType.INOUT) {
				assert(list.stream().anyMatch(e -> e instanceof OutPortMapping && ((OutPortMapping) e).getPort().equals(port)));
				addedElements++;
			}
			
			assert(list.size() == addedElements);
		}
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct missing mappings for sanitizers")
	void testSanitizer() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		for (Sanitizer sanitizer: EcoreUtil2.getAllContentsOfType(model, Sanitizer.class)) {
			List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, sanitizer);
			assert(list.size() == 1);
			assert(list.stream().anyMatch(e -> e instanceof SanitizerMapping && ((SanitizerMapping) e).getSanitizer().equals(sanitizer)));
		}
	}	
	
	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct missing mappings for DataGroups")
	void testGroups() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		for (DataGroup group: EcoreUtil2.getAllContentsOfType(model, DataGroup.class)) {
			List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, group);
			assert(list.size() == 1);
			assert(list.stream().anyMatch(e -> e instanceof GroupFilterMapping && ((GroupFilterMapping) e).getDataGroup().equals(group)));
		}
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct missing mappings for model")
	void testModel() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, model);
		int addedElements = 0;
		for (DataGroup group: EcoreUtil2.getAllContentsOfType(model, DataGroup.class)) {
			addedElements++;
			assert(list.stream().anyMatch(e -> e instanceof GroupFilterMapping && ((GroupFilterMapping) e).getDataGroup().equals(group)));
		}
		for (Sanitizer sanitizer: EcoreUtil2.getAllContentsOfType(model, Sanitizer.class)) {
			addedElements++;
			assert(list.stream().anyMatch(e -> e instanceof SanitizerMapping && ((SanitizerMapping) e).getSanitizer().equals(sanitizer)));
		}
		for (Port port : EcoreUtil2.getAllContentsOfType(model, Port.class)) {
			if (port.getPortType() == PortType.IN || port.getPortType() == PortType.INOUT) {
				assert(list.stream().anyMatch(e -> e instanceof InPortMapping && ((InPortMapping) e).getPort().equals(port)));
				addedElements++;
			}
			if (port.getPortType() == PortType.OUT || port.getPortType() == PortType.INOUT) {
				assert(list.stream().anyMatch(e -> e instanceof OutPortMapping && ((OutPortMapping) e).getPort().equals(port)));
				addedElements++;
			}
		}
		for (Component component : model.getSystem().getComponents()) {
			assert(list.stream().anyMatch(e -> e instanceof ComponentMapping && ((ComponentMapping) e).getComponent().equals(component)));
			assert(list.stream().anyMatch(e -> e instanceof SinkMapping && ((SinkMapping) e).getComponent().equals(component)));
			addedElements += 2;
			if (component instanceof AtomicComponent) {
				for (DataType dt : ((AtomicComponent) component).getSource4Data()) {
					assert(list.stream().anyMatch(e -> e instanceof SourceMapping && ((SourceMapping) e).getComponent().equals(component) && ((SourceMapping) e).getDataType().equals(dt)));
					addedElements++;
				}
			}
		}
		assert(list.size() == addedElements);
	}	
	
	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct obsolete mappings for component")
	void testObsoleteComponent() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, model);
		
		mappingModel.getMappings().addAll(list);
		
		List<Component> componentsList = new ArrayList<Component>(model.getSystem().getComponents());
		int removedElements = 0;
		for (Component component : componentsList) {
			model.getSystem().getComponents().remove(component);
			list = MappingModelHelper.cleanMappingModel(mappingModel, model);
			assert(list.stream().anyMatch(e -> e instanceof ComponentMapping && ((ComponentMapping) e).getComponent().equals(component)));
			assert(list.stream().anyMatch(e -> e instanceof SinkMapping && ((SinkMapping) e).getComponent().equals(component)));
			removedElements += 2;
			if (component instanceof AtomicComponent) {
				for (DataType dt : ((AtomicComponent) component).getSource4Data()) {
					assert(list.stream().anyMatch(e -> e instanceof SourceMapping && ((SourceMapping) e).getComponent().equals(component) && ((SourceMapping) e).getDataType().equals(dt)));
					removedElements++;
				}
			}
			for (Port port : component.getPorts()) {
				if (port.getPortType() == PortType.IN || port.getPortType() == PortType.INOUT) {
					assert(list.stream().anyMatch(e -> e instanceof InPortMapping && ((InPortMapping) e).getPort().equals(port)));
					removedElements++;
				}
				if (port.getPortType() == PortType.OUT || port.getPortType() == PortType.INOUT) {
					assert(list.stream().anyMatch(e -> e instanceof OutPortMapping && ((OutPortMapping) e).getPort().equals(port)));
					removedElements++;
				}
			}
			assert(list.size() == removedElements);
		}
	}	
	
	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct obsolete mappings for ports")
	void testObsoletePorts() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, model);
		
		mappingModel.getMappings().addAll(list);
		int removedElements = 0;

		for (Component component : model.getSystem().getComponents()) {
			List<Port> portsList = new ArrayList<Port>(component.getPorts());
			for (Port port : portsList) {
				component.getPorts().remove(port);
				list = MappingModelHelper.cleanMappingModel(mappingModel, model);
				if (port.getPortType() == PortType.IN || port.getPortType() == PortType.INOUT) {
					assert(list.stream().anyMatch(e -> e instanceof InPortMapping && ((InPortMapping) e).getPort().equals(port)));
					removedElements++;
				}
				if (port.getPortType() == PortType.OUT || port.getPortType() == PortType.INOUT) {
					assert(list.stream().anyMatch(e -> e instanceof OutPortMapping && ((OutPortMapping) e).getPort().equals(port)));
					removedElements++;
				}
				//check that no additional elements were removed
				assert(list.size() == removedElements);
			}
		}
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct obsolete mappings for sanitizer")
	void testObsoleteSanitizer() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, model);
		
		mappingModel.getMappings().addAll(list);
		
		List<Sanitizer> sanitizerList = EcoreUtil2.getAllContentsOfType(model, Sanitizer.class);
		
		int removedElements = 0;
		
		for (Sanitizer sanitizer : sanitizerList) {
			model.getSecurityDefinitions().getSanitizers().remove(sanitizer);
			list = MappingModelHelper.cleanMappingModel(mappingModel, model);
			assert(list.stream().anyMatch(e -> e instanceof SanitizerMapping && ((SanitizerMapping) e).getSanitizer().equals(sanitizer)));
			removedElements++;
			
			//check that no additional elements were removed
			assert(list.size() == removedElements);
		}
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct obsolete mappings for datagroup")
	void testObsoleteDataGroup() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, model);
		
		mappingModel.getMappings().addAll(list);
		
		List<DataGroup> dataGroupList = EcoreUtil2.getAllContentsOfType(model, DataGroup.class);
		
		int removedElements = 0;
		
		for (DataGroup dataGroup : dataGroupList) {
			model.getGroups().getDataGroups().remove(dataGroup);
			list = MappingModelHelper.cleanMappingModel(mappingModel, model);
			assert(list.stream().anyMatch(e -> e instanceof GroupFilterMapping && ((GroupFilterMapping) e).getDataGroup().equals(dataGroup)));
			removedElements++;
			
			//check that no additional elements were removed
			assert(list.size() == removedElements);
		}
	}

	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct obsolete mappings for empty model")
	void testObsoleteModel() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		
		Model model = EcoreUtil.copy(MappingModelTest.model);
		
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		
		List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, model);
		
		mappingModel.getMappings().addAll(list);
		
		List<DataGroup> dataGroupList = EcoreUtil2.getAllContentsOfType(model, DataGroup.class);
		List<Sanitizer> sanitizerList = EcoreUtil2.getAllContentsOfType(model, Sanitizer.class);
		List<Component> componentList = EcoreUtil2.getAllContentsOfType(model, Component.class);
		
		int removedElements = 0;
		model = TmdslFactory.eINSTANCE.createModel();
		model.setSystem(TmdslFactory.eINSTANCE.createSystem());
		model.setGroups(TmdslFactory.eINSTANCE.createGroups());
		model.setSecurityDefinitions(TmdslFactory.eINSTANCE.createSecurityDefinitions());
		list = MappingModelHelper.cleanMappingModel(mappingModel, model);
		
		for (DataGroup dataGroup : dataGroupList) {
			assert(list.stream().anyMatch(e -> e instanceof GroupFilterMapping && ((GroupFilterMapping) e).getDataGroup().equals(dataGroup)));
			removedElements++;
		}
		for (Sanitizer sanitizer : sanitizerList) {
			assert(list.stream().anyMatch(e -> e instanceof SanitizerMapping && ((SanitizerMapping) e).getSanitizer().equals(sanitizer)));
			removedElements++;
		}
		
		for (Component component : componentList) {
			assert(list.stream().anyMatch(e -> e instanceof ComponentMapping && ((ComponentMapping) e).getComponent().equals(component)));
			assert(list.stream().anyMatch(e -> e instanceof SinkMapping && ((SinkMapping) e).getComponent().equals(component)));
			removedElements += 2;
			if (component instanceof AtomicComponent) {
				for (DataType dt : ((AtomicComponent) component).getSource4Data()) {
					assert(list.stream().anyMatch(e -> e instanceof SourceMapping && ((SourceMapping) e).getComponent().equals(component) && ((SourceMapping) e).getDataType().equals(dt)));
					removedElements++;
				}
			}
			List<Port> portsList = new ArrayList<Port>(component.getPorts());
			for (Port port : portsList) {
				if (port.getPortType() == PortType.IN || port.getPortType() == PortType.INOUT) {
					assert(list.stream().anyMatch(e -> e instanceof InPortMapping && ((InPortMapping) e).getPort().equals(port)));
					removedElements++;
				}
				if (port.getPortType() == PortType.OUT || port.getPortType() == PortType.INOUT) {
					assert(list.stream().anyMatch(e -> e instanceof OutPortMapping && ((OutPortMapping) e).getPort().equals(port)));
					removedElements++;
				}
			}
		}
		assert(list.size() == removedElements);
	}

	@org.junit.jupiter.api.Test
	@DisplayName("MappingModelHelper lists correct mappings for all elements")
	void testGetMapping() {
		//check if setup worked
		assertEquals(model.getSystem().getName(), "System0");
		
		Model model = EcoreUtil.copy(MappingModelTest.model);
		de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = MappingModelFactory.eINSTANCE.createModel();
		List<Mapping> list = MappingModelHelper.expandMappingModel(mappingModel, model);
		mappingModel.getMappings().addAll(list);	
		
		for (Component component : model.getSystem().getComponents()) {
			int addedElements = 0;
			list = MappingModelHelper.getMapping(mappingModel, component);
			
			assert(list.stream().anyMatch(e -> e instanceof ComponentMapping && ((ComponentMapping) e).getComponent().equals(component)));
			assert(list.stream().anyMatch(e -> e instanceof SinkMapping && ((SinkMapping) e).getComponent().equals(component)));
			addedElements += 2;
			if (component instanceof AtomicComponent) {
				for (DataType dt : ((AtomicComponent) component).getSource4Data()) {
					assert(list.stream().anyMatch(e -> e instanceof SourceMapping && ((SourceMapping) e).getComponent().equals(component) && ((SourceMapping) e).getDataType().equals(dt)));
					addedElements++;
				}
			}
			assert(list.size() == addedElements);
		}
		
		for (DataGroup group: EcoreUtil2.getAllContentsOfType(model, DataGroup.class)) {
			list = MappingModelHelper.getMapping(mappingModel, group);
			assert(list.size() == 1);
			assert(list.stream().anyMatch(e -> e instanceof GroupFilterMapping && ((GroupFilterMapping) e).getDataGroup().equals(group)));
		}
		
		for (Sanitizer sanitizer: EcoreUtil2.getAllContentsOfType(model, Sanitizer.class)) {
			list = MappingModelHelper.getMapping(mappingModel, sanitizer);
			assert(list.size() == 1);
			assert(list.stream().anyMatch(e -> e instanceof SanitizerMapping && ((SanitizerMapping) e).getSanitizer().equals(sanitizer)));
		}
		
		for (Port port : EcoreUtil2.getAllContentsOfType(model, Port.class)) {
			int addedElements = 0;
			list = MappingModelHelper.getMapping(mappingModel, port);
			
			if (port.getPortType() == PortType.IN || port.getPortType() == PortType.INOUT) {
				assert(list.stream().anyMatch(e -> e instanceof InPortMapping && ((InPortMapping) e).getPort().equals(port)));
				addedElements++;
			}
			if (port.getPortType() == PortType.OUT || port.getPortType() == PortType.INOUT) {
				assert(list.stream().anyMatch(e -> e instanceof OutPortMapping && ((OutPortMapping) e).getPort().equals(port)));
				addedElements++;
			}
			
			assert(list.size() == addedElements);
		}
	}
}
