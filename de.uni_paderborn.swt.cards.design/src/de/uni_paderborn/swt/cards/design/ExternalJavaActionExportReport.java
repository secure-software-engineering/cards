package de.uni_paderborn.swt.cards.design;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.uni_paderborn.swt.cards.dsl.analyzer.Analyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.RestrictionAnalyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.export.ExportGenerator;

public class ExternalJavaActionExportReport implements IExternalJavaAction {

	@Override
	public boolean canExecute(Collection<? extends EObject> arg0) {
		return true;
	}

	@Override
	public void execute(Collection<? extends EObject> arg0, Map<String, Object> arg1) {		
		EObject obj = (EObject) arg0.toArray()[0];
		Model model = (Model) ancestors(obj).stream().filter(e -> e instanceof Model).findFirst().get();
				
		//open dialog
		Shell shell = new Shell();
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		fd.setFileName("report_" + model.getSystem().getName() + "_" + date + ".html");
		String[] ext = {"*.html"};
		fd.setFilterExtensions(ext);
		String filePath = fd.open();
		
		try {
			generateExport(model, filePath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}	

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
	
	/**
	 * Return a list of all ancestors of an EObject, where the first element is the parent and the last element is the root object.
	 * @param obj
	 * @return ancestor list
	 */
	private static List<EObject> ancestors(EObject obj) {
		List<EObject> ancestors = new ArrayList<EObject>();
		while (obj != null) {
			ancestors.add(obj);
			obj = obj.eContainer();
		}
		return ancestors;
	}
}
