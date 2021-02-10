package de.uni_paderborn.swt.cards.design;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

import de.uni_paderborn.swt.cards.mappingModel.impl.MappingModelFactoryImpl;

public class ExternalJavaActionCreateMappingModel implements IExternalJavaAction {

	@Override
	public boolean canExecute(Collection<? extends EObject> arg0) {
		return true;
	}

	@Override
	public void execute(Collection<? extends EObject> arg0, Map<String, Object> arg1) {		
		EObject obj = (EObject) arg0.toArray()[0];
		de.uni_paderborn.swt.cards.dsl.tmdsl.System system = (de.uni_paderborn.swt.cards.dsl.tmdsl.System) arg1.get("system");
		
		// build mappingModelPath relative to model file "dir/file.mappingmodel"
		// TODO make this pretty
		
		String[] pathParts = obj.eResource().getURI().toString().split("/");
		String fileName = pathParts[pathParts.length - 1];
		String withoutExt = fileName.substring(0,fileName.lastIndexOf("."));
		String mappingModelPath = "";
		
		for (int i = 2; i < pathParts.length - 1; i++) {
			mappingModelPath += pathParts[i] + "/";
		}
		mappingModelPath += withoutExt + ".mappingmodel";
		
		
		// create mappingModel
		URI mappingModel = URI.createPlatformResourceURI(mappingModelPath);
		ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.createResource(mappingModel);
		resource.getContents().add(MappingModelFactoryImpl.eINSTANCE.createModel());
		try {
			final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
			saveOptions.put(XMLResource.OPTION_ENCODING, "UTF-8");
			resource.save(saveOptions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// register mappingModel to sirius session
		SessionManager.INSTANCE.getSession(obj).addSemanticResource(mappingModel, new NullProgressMonitor());
		
		// set mappingModel path to system
		system.setPath2MappingModel("\""+ withoutExt + ".mappingmodel" +"\"");
	}
}
