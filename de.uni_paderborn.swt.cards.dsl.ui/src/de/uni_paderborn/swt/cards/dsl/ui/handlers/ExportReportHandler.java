package de.uni_paderborn.swt.cards.dsl.ui.handlers;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.ui.business.api.dialect.DialectEditor;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import de.uni_paderborn.swt.cards.dsl.TMDslStandaloneSetup;
import de.uni_paderborn.swt.cards.dsl.analyzer.Analyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.RestrictionAnalyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation;
import de.uni_paderborn.swt.cards.dsl.export.ExportGenerator;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;

public class ExportReportHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	private void generateExport(Model model, String fileName) throws IOException {
			File newFile = new File(fileName);
			
			newFile.createNewFile();
			
			FileWriter fw = new FileWriter(newFile);
			
			List<ComponentResult> analysisResults = Analyzer.analyzeModel(model);
			List<RestrictionViolation> violations = RestrictionAnalyzer.checkRestrictions(model.getRestriction(), analysisResults);
			
			fw.append(new ExportGenerator(model, analysisResults, violations).generate());
			
			fw.flush();
			
			fw.close();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = new Shell();
		
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
		Model model = (Model) resource.getContents().get(0);
		
		if (model == null) return null;
		

		//open dialog
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		fd.setFileName("report_" + model.getSystem().getName() + "_" + date + ".html");
		String[] ext = {"*.html"};
		fd.setFilterExtensions(ext);
		String filePath = fd.open();
		if (filePath == null) return null;
		try {
			generateExport(model, filePath);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
