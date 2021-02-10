package de.uni_paderborn.swt.cards.mappingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataGroup;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Sanitizer;
import de.uni_paderborn.swt.cards.mappingModel.impl.MappingModelFactoryImpl;

public class MappingModelHelper {
	
	/**
	 * Return all Mappings in MappingModel that belong to the EOject. 
	 * Returns ComponentMapping, SourceMapping and SinkMapping for Components, PortMappings for Ports, 
	 * SanitizerMappings for Sanitizers and GroupFilterMappings for DataGroups.
	 * @param m MappingModel model
	 * @param obj EObject element
	 * @return List of Mappings (can be empty)
	 */
	public static List<Mapping> getMapping(de.uni_paderborn.swt.cards.mappingModel.Model m, EObject obj) {
		if (obj instanceof Component) {
			return m.getMappings().stream()
					.filter(e -> ((e instanceof ComponentMapping && ((ComponentMapping) e).getComponent() == obj)
							|| (e instanceof SourceMapping && ((SourceMapping) e).getComponent() == obj)
							|| (e instanceof SinkMapping && ((SinkMapping) e).getComponent() == obj)))
					.collect(Collectors.toList());
		} else if (obj instanceof Port) {
			return m.getMappings().stream().filter(e -> e instanceof PortMapping).map(e -> (PortMapping) e)
					.filter(e -> e.getPort() == obj).collect(Collectors.toList());
		} else if (obj instanceof Sanitizer) {
			return m.getMappings().stream().filter(e -> e instanceof SanitizerMapping).map(e -> (SanitizerMapping) e)
					.filter(e -> e.getSanitizer() == obj).collect(Collectors.toList());
		} else if (obj instanceof DataGroup) {
			return m.getMappings().stream().filter(e -> e instanceof GroupFilterMapping)
					.map(e -> (GroupFilterMapping) e).filter(e -> e.getDataGroup() == obj).collect(Collectors.toList());
		}

		return new ArrayList<Mapping>();
	}
	
	/**
	 * Return a list of Mappings for an object, which should be included in the MappingModel.
	 * @param mappingModel 
	 * @param obj Object for which missing Mappings should be listed
	 * @return List of Mappings (can be empty)
	 */
	public static List<Mapping> expandMappingModel(de.uni_paderborn.swt.cards.mappingModel.Model mappingModel, EObject obj) {
		List<Mapping> toAdd = new ArrayList<Mapping>();
		
		if (obj instanceof Component) {
			if (!mappingModel.getMappings().stream().filter(e -> e instanceof ComponentMapping)
					.map(e -> (ComponentMapping) e)
					.anyMatch(e -> e.getComponent() != null && e.getComponent().equals(obj))) {
				ComponentMapping sm = MappingModelFactoryImpl.eINSTANCE.createComponentMapping();
				sm.setComponent((Component) obj);
				toAdd.add(sm);
			}
			if (obj instanceof AtomicComponent) {
				for (DataType dt : ((AtomicComponent) obj).getSource4Data()) {
					if (!mappingModel.getMappings().stream().filter(e -> e instanceof SourceMapping).map(e -> (SourceMapping) e)
							.anyMatch(e -> e.getComponent().equals(obj) && e.getDataType().equals(dt))) {
						SourceMapping sm = MappingModelFactoryImpl.eINSTANCE.createSourceMapping();
						sm.setComponent((Component) obj);
						sm.setDataType(dt);
						toAdd.add(sm);
					}
				}				
			}

			if (!mappingModel.getMappings().stream().filter(e -> e instanceof SinkMapping).map(e -> (SinkMapping) e)
					.anyMatch(e -> e.getComponent().equals(obj))) {
				SinkMapping sm = MappingModelFactoryImpl.eINSTANCE.createSinkMapping();
				sm.setComponent((Component) obj);
				toAdd.add(sm);
			}
		} else if (obj instanceof Port) {
			if (((Port) obj).getPortType() == PortType.IN || ((Port) obj).getPortType() == PortType.INOUT) {
				if (!mappingModel.getMappings().stream().filter(e -> e instanceof InPortMapping)
						.map(e -> (InPortMapping) e).anyMatch(e -> e.getPort().equals(obj))) {
					InPortMapping sm = MappingModelFactoryImpl.eINSTANCE.createInPortMapping();
					sm.setPort((Port) obj);
					toAdd.add(sm);
				}
			}
			if (((Port) obj).getPortType() == PortType.OUT || ((Port) obj).getPortType() == PortType.INOUT) {
				if (!mappingModel.getMappings().stream().filter(e -> e instanceof OutPortMapping)
						.map(e -> (OutPortMapping) e).anyMatch(e -> e.getPort().equals(obj))) {
					OutPortMapping sm = MappingModelFactoryImpl.eINSTANCE.createOutPortMapping();
					sm.setPort((Port) obj);
					toAdd.add(sm);
				}
			}
		} else if (obj instanceof Sanitizer) {
			if (!mappingModel.getMappings().stream().filter(e -> e instanceof SanitizerMapping)
					.map(e -> (SanitizerMapping) e).anyMatch(e -> e.getSanitizer().equals(obj))) {
				SanitizerMapping sm = MappingModelFactoryImpl.eINSTANCE.createSanitizerMapping();
				sm.setSanitizer((Sanitizer) obj);
				toAdd.add(sm);
			}
		} else if (obj instanceof DataGroup) {
			if (!mappingModel.getMappings().stream().filter(e -> e instanceof GroupFilterMapping)
					.map(e -> (GroupFilterMapping) e).anyMatch(e -> e.getDataGroup().equals(obj))) {
				GroupFilterMapping gfm = MappingModelFactoryImpl.eINSTANCE.createGroupFilterMapping();
				gfm.setDataGroup((DataGroup) obj);
				toAdd.add(gfm);
			}
		}
		
		return toAdd;
	}
	
	/**
	 * Lists missing Mappings for all elements in the tmdsl Model
	 * @param mappingModel
	 * @param tmdslModel
	 */
	public static List<Mapping> expandMappingModel(de.uni_paderborn.swt.cards.mappingModel.Model mappingModel, de.uni_paderborn.swt.cards.dsl.tmdsl.Model tmdslModel) {
		List<Mapping> toAdd = new ArrayList<Mapping>();
		
		tmdslModel.eAllContents().forEachRemaining(e -> toAdd.addAll(expandMappingModel(mappingModel, e)));
		
		return toAdd;
	}
	
	/**
	 * Lists all obsolete Mappings for given tmdsl Model.
	 * A mapping is obsolete, if one of the elements, that it maps to, has been deleted from the tmdsl model.
	 * @param m mappingModel model
	 * @param tmdslModel tmdslModel model
	 */
	public static List<Mapping> cleanMappingModel(de.uni_paderborn.swt.cards.mappingModel.Model m, Model tmdslModel) {
		// remove mappings without any components, ports, etc
		
		List<Mapping> toDelete = new ArrayList<Mapping>();

		List<ComponentMapping> componentMappings = m.getMappings().stream().filter(e -> e instanceof ComponentMapping)
				.map(e -> (ComponentMapping) e).collect(Collectors.toList());
		List<Component> components = tmdslModel.getSystem().getComponents();

		for (ComponentMapping cm : componentMappings) {
			if (cm.getComponent() == null || !components.contains(cm.getComponent())) {
				toDelete.add(cm);
			}
		}

		List<PortMapping> portMappings = m.getMappings().stream().filter(e -> e instanceof PortMapping)
				.map(e -> (PortMapping) e).collect(Collectors.toList());
		List<Port> ports = EcoreUtil2.getAllContentsOfType(tmdslModel, Port.class);

		for (PortMapping pm : portMappings) {
			if (pm.getPort() == null || !ports.contains(pm.getPort())) {
				toDelete.add(pm);
			} else if (pm instanceof InPortMapping && pm.getPort().getPortType() == PortType.OUT) {
				toDelete.add(pm);
			} else if (pm instanceof OutPortMapping && pm.getPort().getPortType() == PortType.IN) {
				toDelete.add(pm);
			}
		}

		List<SourceMapping> sourceMappings = m.getMappings().stream().filter(e -> e instanceof SourceMapping)
				.map(e -> (SourceMapping) e).collect(Collectors.toList());
		List<DataType> dataTypes = tmdslModel.getSystem().getDatatypes();

		for (SourceMapping sm : sourceMappings) {
			if (sm.getDataType() == null || !dataTypes.contains(sm.getDataType())) {
				toDelete.add(sm);
			} else if (sm.getComponent() == null || !components.contains(sm.getComponent())) {
				toDelete.add(sm);
			} else if (!((AtomicComponent) sm.getComponent()).getSource4Data().contains(sm.getDataType())) {
				toDelete.add(sm);
			}
		}

		List<SinkMapping> sinkMappings = m.getMappings().stream().filter(e -> e instanceof SinkMapping)
				.map(e -> (SinkMapping) e).collect(Collectors.toList());
		for (SinkMapping sm : sinkMappings) {
			if (sm.getComponent() == null || !components.contains(sm.getComponent())) {
				toDelete.add(sm);
			}
		}

		List<DataGroup> dataGroups = tmdslModel.getGroups().getDataGroups();
		List<GroupFilterMapping> groupFilterMappings = m.getMappings().stream()
				.filter(e -> e instanceof GroupFilterMapping).map(e -> (GroupFilterMapping) e)
				.collect(Collectors.toList());

		for (GroupFilterMapping gfm : groupFilterMappings) {
			if (gfm.getDataGroup() == null || !dataGroups.contains(gfm.getDataGroup())) {
				toDelete.add(gfm);
			}
		}

		List<Sanitizer> sanitizers = tmdslModel.getSecurityDefinitions().getSanitizers();
		List<SanitizerMapping> sanitizerMappings = m.getMappings().stream().filter(e -> e instanceof SanitizerMapping)
				.map(e -> (SanitizerMapping) e).collect(Collectors.toList());

		for (SanitizerMapping sm : sanitizerMappings) {
			if (sm.getSanitizer() == null || !sanitizers.contains(sm.getSanitizer())) {
				toDelete.add(sm);
			}
		}

		return toDelete;
	}
}
