package de.uni_paderborn.swt.cards.dsl.ui.wizards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
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

public class ThreatModelingExportWizard extends Wizard implements INewWizard {
	
	protected ExportPage exPage;

	public ThreatModelingExportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		addPage(exPage);
	}
	
	@Override
	public boolean performFinish() {
		
		
		Injector injector = new TMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		
		for (File f : exPage.selectedFiles) {
			XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
			resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
			
			
			
			URI emfURI = URI.createURI("file:/" + f.toString());
			
			Resource resource = resourceSet.getResource(emfURI, true);
			Model model = (Model) resource.getContents().get(0);
			
			generateExport(model, exPage.outputDirectory);
			
			
		}
		
		//ExportGenerator exGen = new ExportGenerator(model, analysis, generateAssumptions, generateRestrictions, generateTrace);
		
		//System.out.println(exGen.generate());
		
		return true;
	}

	private void generateExport(Model model, String outputDirectory) {
		try {
			String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			File newFile = new File(outputDirectory + "/report_" + model.getSystem().getName() + "_" + date + ".html");
			
			newFile.createNewFile();
			
			FileWriter fw = new FileWriter(newFile);
			
			List<ComponentResult> analysisResults = Analyzer.analyzeModel(model);
			List<RestrictionViolation> violations = RestrictionAnalyzer.checkRestrictions(model.getRestriction(), analysisResults);
			
			fw.append(new ExportGenerator(model, analysisResults, violations).generate());
			
			fw.flush();
			
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		exPage = new ExportPage();		
	}

}
