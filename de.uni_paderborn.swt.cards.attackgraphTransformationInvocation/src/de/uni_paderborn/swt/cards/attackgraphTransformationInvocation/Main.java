package de.uni_paderborn.swt.cards.attackgraphTransformationInvocation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.m2m.qvt.oml.BasicModelExtent;
import org.eclipse.m2m.qvt.oml.ExecutionContextImpl;
import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;
import org.eclipse.m2m.qvt.oml.ModelExtent;
import org.eclipse.m2m.qvt.oml.TransformationExecutor;
import org.eclipse.sirius.ui.business.api.dialect.DialectEditor;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

import attackgraph.AttackGraph;
import de.uni_paderborn.swt.cards.dsl.analyzer.RestrictionAssumptionResolver;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Restriction;
import de.uni_paderborn.swt.cards.dsl.tmdsl.TmdslFactory;
import de.uni_paderborn.swt.cards.dsl.TMDslStandaloneSetup;
import de.uni_paderborn.swt.cards.mag.AssumptionList;
import de.uni_paderborn.swt.cards.mag.MagFactory;

public class Main {

	public static void main() {
		
		try {
			Resource resource = loadTmdslResource();
			
			if (resource == null) return;

			Model model = (Model) resource.getContents().get(0);
			
			if (model == null) return;
			
			URI transformationURI = getTransformationURIFromPlugin();
			
			PlatformUI.getWorkbench().getProgressService().run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					List<AttackGraph> attackGraphs = transformTmdslToAttackgraphs(model, transformationURI, monitor);
					writeAttackGraphs(attackGraphs, model);
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<AttackGraph> transformTmdslToAttackgraphs(Model model, URI transformationURI, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor);
		subMonitor.setTaskName("AttackGraph Transformation");
		ArrayList<AttackGraph> result = new ArrayList<AttackGraph>();
		
		subMonitor.setWorkRemaining(100);
		
		SubMonitor childPowerSet = subMonitor.split(80);

		Map<EObject, List<List<EObject>>> map = RestrictionAssumptionResolver.getAssumptionsForRestriction(model, childPowerSet);
		
		
		subMonitor.setWorkRemaining(map.keySet().size());
		
		for (EObject key : map.keySet()) {
			SubMonitor child = subMonitor.split(10);
			child.setTaskName("Running Transformation for " + key);
			if(subMonitor.isCanceled()) {
				return null;
			}
			
			Restriction newRestriction = TmdslFactory.eINSTANCE.createRestriction();
			newRestriction.getRestrictions().add(key);
			
			ResourceSet rs = new ResourceSetImpl();
		    URI outUri = URI.createURI(model.eResource().getURI().trimSegments(1) +"/"+"test.mag");
		    Resource outResource = rs.createResource(outUri);
			
			de.uni_paderborn.swt.cards.mag.Model magModel = MagFactory.eINSTANCE.createModel();
			magModel.setRestriction(newRestriction);
			
			for (List<EObject> list : map.get(key)) {
				AssumptionList assumptionList = MagFactory.eINSTANCE.createAssumptionList();
				assumptionList.getAssumptions().addAll(list);
				magModel.getAssumptions().add(assumptionList);
				outResource.getContents().add(assumptionList);
			}

			outResource.getContents().add(newRestriction);
			outResource.getContents().add(magModel);

			
			List<EObject> output = transform(model, outResource, transformationURI);
			System.out.println("output" + output);
			AttackGraph ag = (AttackGraph) output.get(0);
			
			result.add(ag);
			child.done();
		}		
		
		return result;
	}
	
	public static void writeAttackGraphs(List<AttackGraph> attackGraphs, Model model) {
		int i = 0;
		
		for (AttackGraph ag : attackGraphs) {
			String fileName = model.getSystem().getName() + "_res_" + i +".attackgraph";
			
			ResourceSet resourceSet = new ResourceSetImpl();
		    URI outUri = URI.createURI(model.eResource().getURI().trimSegments(1) +"/"+fileName);
			Resource outResource = resourceSet.createResource(outUri);
			outResource.getContents().add(ag);
			try {
				outResource.save(Collections.emptyMap());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}
	
	public static Resource loadTmdslResource() {
		//load file in workspace
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart(); 
		
		IEditorPart editor = workbenchPart.getSite().getPage().getActiveEditor();
		URI emfURI = null;
		
		if (editor instanceof DialectEditor) {
			DialectEditor de = (DialectEditor) editor;
			DRepresentation dr = de.getRepresentation();
			if (dr instanceof DSemanticDecorator) {
				DSemanticDecorator dd = (DSemanticDecorator) dr;
				if (dd.getTarget() instanceof Model) {
					emfURI = dd.getTarget().eResource().getURI();
				}
			}
		} else {
			IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
			if (file == null) return null;
			if (!file.getFileExtension().equals("tmdsl")) return null;
			java.net.URI javaURI = file.getLocationURI();
			emfURI = URI.createURI(javaURI.toString());
		}
		//load model
		Injector injector = new TMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		
		Resource resource = resourceSet.getResource(emfURI, true);
		
		return resource;
	}
	
	public static List<EObject> transform(Model tmdslModel, Resource resource, URI transformationURI) {
		TransformationExecutor executor = new TransformationExecutor(transformationURI);

		// define the transformation input
		// a list of arbitrary in-memory EObjects may be passed
		ExecutionContextImpl context = new ExecutionContextImpl();
	
		EList<EObject> inObjects = new BasicEList<EObject>();
		inObjects.addAll(resource.getContents());

		// create the input extent with its initial contents
		ModelExtent input = new BasicModelExtent(inObjects);		
		// create an empty extent to catch the output
		ModelExtent output = new BasicModelExtent();

		// setup the execution environment details -> 
		// configuration properties, logger, monitor object etc.
		context = new ExecutionContextImpl();
		context.setConfigProperty("keepModeling", true);

		// run the transformation assigned to the executor with the given 
		// input and output and execution context -> ChangeTheWorld(in, out)
		// Remark: variable arguments count is supported
		ExecutionDiagnostic result = executor.execute(context, input, output);
		
		// check the result for success
		if(result.getSeverity() == Diagnostic.OK) {
			// the output objects got captured in the output extent
			List<EObject> outObjects = output.getContents();
			
			return outObjects;
		    
		} else {
			// turn the result diagnostic into status and send it to error log			
			IStatus status = BasicDiagnostic.toIStatus(result);
			System.out.println(status);
		}
		
		return null;
	}
	
	public static URI getTransformationURIFromPlugin() {
		Bundle bundle = Platform.getBundle( "de.uni_paderborn.swt.cards.attackgraphTransformation" );
		URL url = FileLocator.find(bundle, new Path("transforms/NewTransformation.qvto"));
		try {
			url = FileLocator.toFileURL(url);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URI transformationURI = URI.createFileURI(url.getPath());
		
		return transformationURI;
	}
	
}
