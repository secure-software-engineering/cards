package de.uni_paderborn.swt.cards.design;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sirius.business.api.session.SessionManager;

import com.google.gson.Gson;

import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.DataPoint;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowSanitizationAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroup;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupAllowRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupPreventRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Groups;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption;
import de.uni_paderborn.swt.cards.dsl.validation.AnalysisSingleton;
import de.uni_paderborn.swt.cards.result.AnalysisResult;
import de.uni_paderborn.swt.cards.mappingModel.Mapping;
import de.uni_paderborn.swt.cards.mappingModel.MappingModelHelper;

/**
 * The services class used by VSM.
 */
public class Services {
	
	private static String getAssumptionId(EObject assumption) throws Exception {
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption) {
			ComponentAssumption compAss = (de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption) assumption;
			return compAss.getClass().getName() + ":" + compAss.getComp().getName() + ":" + compAss.getData().getName();
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption) {
			ComponentSanitizerAssumption compAss = (de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption) assumption;
			return compAss.getClass().getName() + ":" + compAss.getSanitizerComp().getName() + ":" + compAss.getSanitizedData().getName() + ":" + compAss.getSanitizer().getName();
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption) {
			PortAssumption compAss = (de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption) assumption;
			return compAss.getClass().getName() + ":" + compAss.getPort().getName() + ":" + compAss.getData().getName();
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption) {
			PortSanitizerAssumption compAss = (de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption) assumption;
			return compAss.getClass().getName() + ":" + compAss.getSanitizerPort().getName() + ":" + compAss.getSanitizedData().getName() + ":" + compAss.getSanitizer().getName();
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption) {
			ComponentFlowAssumption compAss = (de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption) assumption;
			return compAss.getClass().getName() + ":" + compAss.getComp().getName() + ":" + compAss.getData().getName();
		}
		if (assumption instanceof ComponentFlowSanitizationAssumption) {
			ComponentFlowSanitizationAssumption compAss = (de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowSanitizationAssumption) assumption;
			return compAss.getClass().getName() + ":" + compAss.getComp().getName() + ":" + compAss.getData().getName() + ":" + compAss.getSanitizer().getName();
		}
		throw new Exception("invalid object");
	}
	
	public String getAnalysisResult(Model m, Component c) {
		System.out.println("entered");
		
		IFile platformStringToTmdsl = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(EcoreUtil.getURI(m).toPlatformString(true)));

		String platformStringAnalysisResults = platformStringToTmdsl.getProject().getFile("analysisResults.json").getFullPath().toString();
		
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformStringAnalysisResults));
		
		System.out.println(f.getRawLocation());
		
		File file = new File(f.getRawLocation().toString());
		String analysisJson = "[]";
		try {
			analysisJson = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
		
		try {
			List<EObject> assumptions = new ArrayList<EObject>();
			
			assumptions.addAll(m.getAssumption().getComponentAssumptions().stream().filter(e -> e.getComp().equals(c)).collect(Collectors.toList()));
			assumptions.addAll(m.getAssumption().getComponentSanitzerAssumptions().stream().filter(e -> e.getSanitizerComp().equals(c)).collect(Collectors.toList()));
			assumptions.addAll(m.getAssumption().getFlowAssumptions().stream().filter(e -> e.getComp().equals(c)).collect(Collectors.toList()));
			assumptions.addAll(m.getAssumption().getSanitizersAssumptions().stream().filter(e -> e.getComp().equals(c)).collect(Collectors.toList()));
			assumptions.addAll(m.getAssumption().getPortAssumptions().stream().filter(e -> c.getPorts().contains(e.getPort())).collect(Collectors.toList()));
			assumptions.addAll(m.getAssumption().getPortSanitizerAssumptions().stream().filter(e -> c.getPorts().contains(e.getSanitizerPort())).collect(Collectors.toList()));
			
			System.out.println(assumptions.size());
			// load json
//			String jsonString = "[\r\n"
//					+ "	{\r\n"
//					+ "		timestamp: \"2021-01-06 10:39:20\",\r\n"
//					+ "		assumptionId: \"de.uni_paderborn.swt.cards.dsl.tmdsl.impl.ComponentFlowSanitizationAssumptionImpl:CashDeskPC:CreditCardInfo:CCSanitizer\",\r\n"
//					+ "		pass: false,\r\n"
//					+ "		violations: [\r\n"
//					+ "			\"Some Failure\",\r\n"
//					+ "			\"Another Failure\"\r\n"
//					+ "		]\r\n"
//					+ "	},\r\n"
//					+ "	{\r\n"
//					+ "		timestamp: \"2021-01-06 10:39:20\",\r\n"
//					+ "		assumptionId: \"de.uni_paderborn.swt.cards.dsl.tmdsl.impl.PortAssumptionImpl:pcLightDisplay:CreditCardInfo\",\r\n"
//					+ "		pass: true,\r\n"
//					+ "		violations: []\r\n"
//					+ "	}\r\n"
//					+ "]";
//			
			Gson gson = new Gson();
			AnalysisResult[] results = gson.fromJson(analysisJson, AnalysisResult[].class);
			
			System.out.println(results.length);
			
			
//			AnalysisResult[] results = new AnalysisResult[2];
//			
//			results[0] = new AnalysisResult();
//			results[0].setAssumptionId("de.uni_paderborn.swt.cards.dsl.tmdsl.impl.PortAssumptionImpl:pcLightDisplay:CreditCardInfo");
//			results[0].setPass(true);
//			results[0].setTimestamp("2021-01-06 10:39:20");
//			String[] constantResults = {};
//			results[0].setViolations(constantResults);
//			
//			results[1] = new AnalysisResult();
//			results[1].setAssumptionId("de.uni_paderborn.swt.cards.dsl.tmdsl.impl.ComponentFlowSanitizationAssumptionImpl:CashDeskPC:CreditCardInfo:CCSanitizer");
//			results[1].setPass(false);
//			results[1].setTimestamp("2021-01-06 10:39:20");
//			String[] constantResults2 = {"Some Failure", "Another Failure"};
//			results[1].setViolations(constantResults2);
//			
			String returnString = "";
			
			for (EObject ass : assumptions) {
				String assumptionId = getAssumptionId(ass);
				
				
				boolean analysisResultPresent = false;
				
				for (AnalysisResult result : results) {
					if (result.getAssumptionId().equals(assumptionId)) {
						analysisResultPresent = true;
						returnString += "{";
						
						returnString += result.getAssumptionId() + "\n";
						returnString += result.getTimestamp() + "\n";
						returnString += "passed: " +result.isPass() + "\n";
						if (!result.isPass()) {
							for (String vio : result.getViolations()) {
								returnString += vio + "\n";
							}
						}
						
						returnString += "}\n";
						
						
					}
				}
				
				if (!analysisResultPresent) {
					returnString += "{";
					
					returnString += assumptionId + "\n";
					returnString += "never ran" + "\n";
					
					returnString += "}\n";
				}
			}
			
			return returnString;
			
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("execption");
			return "";
		}
	}
	

	public boolean objectHasMapping(EObject obj) throws IOException {
		boolean hasMapping = false;
		Resource mappingModelResource = getMappingModelResource(obj);

		if (mappingModelResource == null) {
			return hasMapping;
		}
		return getMappings(obj).size() > 0;
	}

	public void savingService(EObject o) {
		System.out.println(o);
	}

	public List<Mapping> getMappings(EObject obj) throws IOException {
		Resource mappingModelResource = getMappingModelResource(obj);
		de.uni_paderborn.swt.cards.mappingModel.Model m = (de.uni_paderborn.swt.cards.mappingModel.Model) mappingModelResource
				.getContents().stream().filter(e -> e instanceof de.uni_paderborn.swt.cards.mappingModel.Model)
				.findFirst().get();

		Model tmdslModel = (Model) ancestors(obj).stream().filter(e -> e instanceof Model).findFirst().get();

		// clean up model
		
		List<Mapping> toDelete = MappingModelHelper.cleanMappingModel(m, tmdslModel);
		removeFromResourceUsingTransaction(m, toDelete);

		// add missing (empty) mappings to model
		List<Mapping> toAdd = MappingModelHelper.expandMappingModel(m, obj);
		addToResourceUsingTransaction(m, toAdd);

		// return mappings
		return MappingModelHelper.getMapping(m, obj);
	}



	/**
	 * Return mapping Model resource of system ancestor of EObject
	 * 
	 * @param obj EObject
	 * @return mapping model resource, registered in the same sirius session
	 */
	public static Resource getMappingModelResource(EObject obj) {
		// get platform uri for referenced mapping model
		IFile platformStringToTmdsl = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(EcoreUtil.getURI(obj).toPlatformString(true)));
		String relativePathToMappingModel = ((de.uni_paderborn.swt.cards.dsl.tmdsl.System) ancestors(obj)
				.stream().filter(e -> e instanceof Model).map(e -> (Model) e).findFirst().get().getSystem())
						.getPath2MappingModel().replace("\"", "").replace("mappingModel", "mappingmodel");

		String platformStringToMappingModel = platformStringToTmdsl.getProject().getFile(relativePathToMappingModel)
				.getFullPath().toString();
		URI mmUri = URI.createURI(platformStringToMappingModel, true);

		// get current sirius session
		Collection<Resource> sessionResources = SessionManager.INSTANCE.getSession(obj).getSemanticResources();
		for (Resource r : sessionResources) {
			if (r.getURI().toString().equals("platform:/resource" + mmUri.toString())) {
				return r;
			}
		}

		return null;
	}
	
	public static void addToResourceUsingTransaction(de.uni_paderborn.swt.cards.mappingModel.Model model,
			Mapping mapping) {
		TransactionalEditingDomain domain = SessionManager.INSTANCE.getSession(model).getTransactionalEditingDomain();

		domain.getCommandStack().execute(new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				model.getMappings().add(mapping);

			}
		});
	}
	
	public static void addToResourceUsingTransaction(de.uni_paderborn.swt.cards.mappingModel.Model model,
			List<Mapping> mapping) {
		TransactionalEditingDomain domain = SessionManager.INSTANCE.getSession(model).getTransactionalEditingDomain();

		domain.getCommandStack().execute(new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				model.getMappings().addAll(mapping);

			}
		});
	}

	public static void removeFromResourceUsingTransaction(de.uni_paderborn.swt.cards.mappingModel.Model model,
			Mapping mapping) {
		TransactionalEditingDomain domain = SessionManager.INSTANCE.getSession(model).getTransactionalEditingDomain();

		domain.getCommandStack().execute(new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				model.getMappings().remove(mapping);

			}
		});
	}

	public static void removeFromResourceUsingTransaction(de.uni_paderborn.swt.cards.mappingModel.Model model,
			List<Mapping> mapping) {
		TransactionalEditingDomain domain = SessionManager.INSTANCE.getSession(model).getTransactionalEditingDomain();

		domain.getCommandStack().execute(new RecordingCommand(domain) {

			@Override
			protected void doExecute() {
				model.getMappings().removeAll(mapping);

			}
		});
	}

	public String printComponentGroupPreventRefinement(EObject param) {
		ComponentGroupPreventRefinement cprr = (ComponentGroupPreventRefinement) param;
		String s = "";

		s += "Prevent " + cprr.getDataAsset().stream().map(myDataGroup -> myDataGroup.getName())
				.reduce((a, b) -> a + ", " + b).get();
		s += " from " + cprr.getComponentGroup().stream().map(myComponentGroup -> myComponentGroup.getName())
				.reduce((a, b) -> a + ", " + b).get();
		if (cprr.getExcludeComp().size() > 0)
			s += " except " + cprr.getExcludeComp().stream().map(excludeComp -> excludeComp.getName())
					.reduce((a, b) -> a + ", " + b).get();
		if (cprr.getSanitizerOption() != null)
			s += " unless sanitized by " + cprr.getSanitizerOption().getSanitizer().getName();
		return s;
	}

	public String printComponentGroupAllowRefinement(EObject param) {
		ComponentGroupAllowRefinement cprr = (ComponentGroupAllowRefinement) param;
		String s = "";

		s += "Allow " + cprr.getDataAsset().stream().map(myDataGroup -> myDataGroup.getName())
				.reduce((a, b) -> a + ", " + b).get();
		s += " for " + cprr.getComponentGroup().stream().map(myComponentGroup -> myComponentGroup.getName())
				.reduce((a, b) -> a + ", " + b).get();
		if (cprr.getExcludeComp().size() > 0)
			s += " except " + cprr.getExcludeComp().stream().map(excludeComp -> excludeComp.getName())
					.reduce((a, b) -> a + ", " + b).get();
		if (cprr.getSanitizerOption() != null)
			s += " if sanitized by " + cprr.getSanitizerOption().getSanitizer().getName();
		return s;
	}

	public Boolean componentHasAssumptions(Model model, Component c) {
		Assumption assumption = model.getAssumption();

		for (ComponentAssumption compAss : assumption.getComponentAssumptions()) {
			if (compAss.getComp().equals(c))
				return true;
		}

		for (ComponentSanitizerAssumption compSanAss : assumption.getComponentSanitzerAssumptions()) {
			if (compSanAss.getSanitizerComp().equals(c))
				return true;
		}

		for (ComponentFlowAssumption flowAss : assumption.getFlowAssumptions()) {
			if (flowAss.getComp().equals(c))
				return true;
		}

		for (ComponentFlowSanitizationAssumption flowSanAss : assumption.getSanitizersAssumptions()) {
			if (flowSanAss.getComp().equals(c))
				return true;
		}
		
		for (PortAssumption portAss : assumption.getPortAssumptions()) {
			if (c.getPorts().contains(portAss.getPort()))
				return true;
		}
		
		for (PortSanitizerAssumption portSanAss : assumption.getPortSanitizerAssumptions()) {
			if (c.getPorts().contains(portSanAss.getSanitizerPort()))
				return true;
		}
		
		return false;
	}

	public String getAssumptionsForComponent(Model model, Component c) {
		String s = "Assumptions:\n";

		Assumption assumption = model.getAssumption();

		for (ComponentAssumption compAss : assumption.getComponentAssumptions()) {
			if (compAss.getComp().equals(c)) {
				s += compAss.getComp().getName() + " neverOut " + compAss.getData().getName() + "\n";
			}
		}

		for (ComponentSanitizerAssumption compSanAss : assumption.getComponentSanitzerAssumptions()) {
			if (compSanAss.getSanitizerComp().equals(c)) {
				s += compSanAss.getSanitizerComp().getName() + " neverOut " + compSanAss.getSanitizedData().getName()
						+ " unless sanitized by " + compSanAss.getSanitizer().getName() + "\n";
			}
		}

		for (ComponentFlowAssumption flowAss : assumption.getFlowAssumptions()) {
			if (flowAss.getComp().equals(c)) {
				s += flowAss.getComp().getName() + " prevents DataFlow " + flowAss.getSource().getName() + " -> "
						+ flowAss.getTarget().getName();
				if (flowAss.getData() != null)
					s += " of " + flowAss.getData().getName();
				s += "\n";
			}
		}

		for (ComponentFlowSanitizationAssumption flowSanAss : assumption.getSanitizersAssumptions()) {
			if (flowSanAss.getComp().equals(c)) {
				s += flowSanAss.getComp().getName() + " prevents DataFlow " + flowSanAss.getSource().getName() + " -> "
						+ flowSanAss.getTarget().getName();
				if (flowSanAss.getData() != null)
					s += " of " + flowSanAss.getData().getName();
				s += " unless sanitized by " + flowSanAss.getSanitizer().getName() + "\n";
			}
		}
		
		for (PortAssumption portAss : assumption.getPortAssumptions()) {
			if (c.getPorts().contains(portAss.getPort())) {
				s += portAss.getPort().getName() + " neverOut";
				if (portAss.getData() != null) {
					s+= " data of group " + portAss.getData().getName();
				}
			}
		}
		
		for (PortSanitizerAssumption portSanAss : assumption.getPortSanitizerAssumptions()) {
			if (c.getPorts().contains(portSanAss.getSanitizerPort())) {
				s += portSanAss.getSanitizerPort().getName() + " neverOut";
				if (portSanAss.getSanitizedData() != null) {
					s+= " data of group " + portSanAss.getSanitizedData().getName();
				}
				
				if (portSanAss.getSanitizer() != null) {
					s+= " unless sanitized by " + portSanAss.getSanitizer().getName();
				}
			}
		}

		return s;
	}

	AnalysisSingleton analyzer = new AnalysisSingleton();

	public String getCapableDatatypes(Model model, Component c) {
		String s = "Capable Datatypes:\n";

		List<ComponentResult> analysis = analyzer.getAnalysis(model);
		Set<DataPoint> myData = new HashSet<DataPoint>();

		for (ComponentResult cr : analysis) {
			List<EObject> ancestors = ancestorsWithoutModel(c);
			if (cr.getStack().containsAll(ancestors) && ancestors.containsAll(cr.getStack())) {
				for (List<DataPoint> dp : cr.getTypes()) {
					myData.addAll(dp);
				}
			}
		}

		for (DataPoint dp : myData) {
			s += dp.toString() + "\n";
		}

		return s;
	}

	public boolean componentHasCapableDatatypes(Model model, Component c) {
		List<ComponentResult> analysis = analyzer.getAnalysis(model);

		Set<DataPoint> myData = new HashSet<DataPoint>();

		for (ComponentResult cr : analysis) {
			List<EObject> ancestors = ancestorsWithoutModel(c);
			if (cr.getStack().containsAll(ancestors) && ancestors.containsAll(cr.getStack())) {

				for (List<DataPoint> dp : cr.getTypes()) {
					myData.addAll(dp);
				}
			}
		}

		return myData.size() > 0;
	}
	
	public boolean componentIsGrouped(Model model, Component c) {
		Groups groups = model.getGroups();
		
		for (ComponentGroup cg : groups.getComponentGroups()) {
			if (cg.getGroupedComponents().contains(c)) 
				return true;
		}

		return false;
	}
	
	public String getComponentGroups(Model model, Component c) {
		String s = "Groups:\n";
		Groups groups = model.getGroups();
		
		for (ComponentGroup cg : groups.getComponentGroups()) {
			if (cg.getGroupedComponents().contains(c)) 
				s += cg.getName() + "\n";
		}

		return s;
	}

	public String getCapableDatatypes(Model model, ComponentPart c) {
		String s = "Capable Datatypes:\n";

		List<ComponentResult> analysis = analyzer.getAnalysis(model);

		for (ComponentResult cr : analysis) {
			List<EObject> ancestors = ancestorsWithoutModel(c);
			ancestors.add(c.getComponent());
			if (cr.getStack().containsAll(ancestors)) {
				s += "As " + cr.getStackDerivedName() + "\n";
				Set<DataPoint> myData = new HashSet<DataPoint>();
				for (List<DataPoint> dp : cr.getTypes()) {
					myData.addAll(dp);
				}
				for (DataPoint d : myData) {
					s += d.toString() + "\n";
				}
				s += "\n";
			}
		}

		return s;
	}

	public boolean componentHasCapableDatatypes(Model model, ComponentPart c) {
		List<ComponentResult> analysis = analyzer.getAnalysis(model);

		Set<DataPoint> myData = new HashSet<DataPoint>();

		for (ComponentResult cr : analysis) {
			List<EObject> ancestors = ancestorsWithoutModel(c);
			ancestors.add(c.getComponent());
			if (cr.getStack().containsAll(ancestors)) {

				for (List<DataPoint> dp : cr.getTypes()) {
					myData.addAll(dp);
				}
			}
		}

		return myData.size() > 0;
	}

	public static List<EObject> ancestors(EObject obj) {
		List<EObject> ancestors = new ArrayList<EObject>();
		while (obj != null) {
			ancestors.add(obj);
			obj = obj.eContainer();
		}
		return ancestors;
	}

	private static List<EObject> ancestorsWithoutModel(EObject obj) {
		List<EObject> list = ancestors(obj);
		return list.stream().filter(o -> !(o instanceof Model)).collect(Collectors.toList());
	}

}
