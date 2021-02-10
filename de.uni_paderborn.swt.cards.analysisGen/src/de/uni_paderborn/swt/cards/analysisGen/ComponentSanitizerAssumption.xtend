package de.uni_paderborn.swt.cards.analysisGen

import de.uni_paderborn.swt.cards.mappingModel.ComponentMapping
import de.uni_paderborn.swt.cards.mappingModel.PortMapping
import de.uni_paderborn.swt.cards.mappingModel.SanitizerMapping
import java.util.List
import de.uni_paderborn.swt.cards.mappingModel.Mapping

class ComponentSanitizerAssumption {
	static def generate(
		String packageName, 
		String className, 
		ComponentMapping cMapping, 
		List<Mapping> sourceMappings, 
		List<Mapping> targetMappings, 
		SanitizerMapping sanitizerMapping,
		String assumptionId
	) {
		'''
		package «packageName»;
		
		import java.io.File;
		import java.util.ArrayList;
		import java.util.Arrays;
		import java.util.List;
		
		
		import de.fraunhofer.iem.secucheck.analysis.client.SecuCheckTaintAnalysisOutOfProcess;
		import de.fraunhofer.iem.secucheck.analysis.datastructures.DifferentTypedPair;
		import de.fraunhofer.iem.secucheck.analysis.query.OS;
		import de.fraunhofer.iem.secucheck.analysis.SecucheckAnalysis;
		import de.fraunhofer.iem.secucheck.analysis.SecucheckTaintAnalysis;
		import de.fraunhofer.iem.secucheck.analysis.query.CompositeTaintFlowQueryImpl;
		import de.fraunhofer.iem.secucheck.analysis.query.EntryPoint;
		import de.fraunhofer.iem.secucheck.analysis.query.InputParameter;
		import de.fraunhofer.iem.secucheck.analysis.query.MethodImpl;
		import de.fraunhofer.iem.secucheck.analysis.query.OutputParameter;
		import de.fraunhofer.iem.secucheck.analysis.query.ReportSite;
		import de.fraunhofer.iem.secucheck.analysis.query.ReturnValue;
		import de.fraunhofer.iem.secucheck.analysis.query.TaintFlowQueryImpl;
		import de.fraunhofer.iem.secucheck.analysis.result.AnalysisResultListener;
		import de.fraunhofer.iem.secucheck.analysis.result.CompositeTaintFlowQueryResult;
		import de.fraunhofer.iem.secucheck.analysis.result.SecucheckTaintAnalysisResult;
		import de.fraunhofer.iem.secucheck.analysis.result.TaintFlowQueryResult;
		
		public class «className» {
			
			static final String canonicalClassName = "«cMapping.targetClassName»";
			static final String assumptionId = "«assumptionId»";
			
			static final String[] sinkMethodNames = {
				«targetMappings.map[p | "\"" + p.targetMethodName + "\""].reduce[p1, p2| p1 +",\n" + p2]»
			};
			static final String[] sinkMethodSignatures = {
				«targetMappings.map[p | "canonicalClassName + \": void " + p.targetMethodName + "(java.lang.String data)" + "\""].reduce[p1, p2| p1 + ",\n" + p2]»
			};
			
			static final String sanitizerMethodName = "«sanitizerMapping.targetMethodName»";
			static final String sanitizerMethodSignature = canonicalClassName + ": java.lang.String «sanitizerMapping.targetMethodName»(java.lang.String data)";	
			
			static final String[] sourceMethodNames = {
				«sourceMappings.map[p | "\"" + p.targetMethodName + "\""].reduce[p1, p2| p1 + ",\n" + p2]»
			};
			static final String[] sourceMethodSignatures = {
				«sourceMappings.map[p | "canonicalClassName + \": java.lang.String " + p.targetMethodName + "()" + "\""].reduce[p1, p2| p1 + ",\n" + p2]»
			};
			
			public static void main(String[] args) {
				
				AnalysisResultListener resultListener = new ConsoleResultListener();
				SecucheckAnalysis secucheckAnalysis = new SecucheckTaintAnalysis();
			
				secucheckAnalysis.setOs(OS.Windows);
				secucheckAnalysis.setAnalysisEntryPoints(getEntryPoints());
				secucheckAnalysis.setApplicationClassPath(getAppClassPath());
				secucheckAnalysis.setSootClassPathJars(getSootClassPath());
				
		
						
				try {
					runAssumption1(secucheckAnalysis, resultListener);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		
			private static void runAssumption1(SecucheckAnalysis secucheckAnalysis, AnalysisResultListener resultListener) throws Exception {
				String msg = "Unsanitized flow from ...Port to ...Port in " + canonicalClassName;
				List<CompositeTaintFlowQueryImpl> compositeOfFirst = Utility.getInList(
						Utility.getCompositeOf(ReportSite.SourceAndSink, msg, getTaintFlowQuery()));
				
				runAnalysisQuery(secucheckAnalysis, compositeOfFirst, 1, null);
				
			}
			
			private static void runAnalysisQuery(SecucheckAnalysis secucheckAnalysis, 
					List<CompositeTaintFlowQueryImpl> composites, int queryNumber, 
					AnalysisResultListener resultListener) throws Exception {
				secucheckAnalysis.setListener(resultListener);
				SecucheckTaintAnalysisResult result = secucheckAnalysis.run(composites);
				System.out.println();
				System.out.println("Result-" + queryNumber + " size: " + result.size());
				if (result.size() > 0) {
					for (DifferentTypedPair<CompositeTaintFlowQueryImpl, CompositeTaintFlowQueryResult> r : result.getResults()) {
						String msg = r.getFirst().getReportMessage();
						System.out.print("Violation found: " + msg);
					}
					
				} else {
					System.out.println("No violations found.");
				}
			}
			
			private static TaintFlowQueryImpl getTaintFlowQuery() {
				TaintFlowQueryImpl taintFlowQuery = new TaintFlowQueryImpl();
				getSourceMethods().stream().forEach(taintFlowQuery::addFrom);
				taintFlowQuery.addNotThrough(getSanitizerMethod());
				getSinkMethods().stream().forEach(taintFlowQuery::addTo);
				return taintFlowQuery;
			}
			
			private static List<MethodImpl> getSinkMethods() {
				List<MethodImpl> result = new ArrayList<MethodImpl>();
				
				for (int i = 0; i < sinkMethodNames.length; i++) {
					result.add(getSinkMethod(sinkMethodNames[i], sinkMethodSignatures[i]));
				}
				
				return result;
			}
			

			private static MethodImpl getSinkMethod(String sinkMethodName, String sinkMethodSignature) {
				InputParameter input = new InputParameter();
				input.setNumber(0);
				
				List<InputParameter> inputs = new ArrayList<InputParameter>();
				inputs.add(input);
				
				List<OutputParameter> outputs = new ArrayList<OutputParameter>();
				ReturnValue returnValue = null;
				
				MethodImpl method = new MethodImpl();
				method.setName(sinkMethodName);
				method.setSignature(sinkMethodSignature);
				method.setInputParameters(inputs);
				method.setOutputParameters(outputs);
				method.setReturnValue(returnValue);
				return method;
			}
			
			private static MethodImpl getSanitizerMethod() {
				InputParameter input = new InputParameter();
				input.setNumber(0);
				
				List<InputParameter> inputs = new ArrayList<InputParameter>();
				inputs.add(input);
				
				List<OutputParameter> outputs = new ArrayList<OutputParameter>();
				ReturnValue returnValue = null;
				
				MethodImpl method = new MethodImpl();
				method.setName(sanitizerMethodName);
				method.setSignature(sanitizerMethodSignature);
				method.setInputParameters(inputs);
				method.setOutputParameters(outputs);
				method.setReturnValue(returnValue);
				return method;
			}
			
			private static List<MethodImpl> getSourceMethods() {
				List<MethodImpl> result = new ArrayList<MethodImpl>();
				
				for (int i = 0; i < sourceMethodNames.length; i++) {
					result.add(getSourceMethod(sourceMethodNames[i], sourceMethodSignatures[i]));
				}
				
				return result;
			}
			
			private static MethodImpl getSourceMethod(String sourceMethodName, String sourceMethodSignature) {
				List<InputParameter> inputs = new ArrayList<InputParameter>();
				List<OutputParameter> outputs = new ArrayList<OutputParameter>();
				ReturnValue returnValue = new ReturnValue();
				
				MethodImpl method = new MethodImpl();
				method.setName(sourceMethodName);
				method.setSignature(sourceMethodSignature);
				method.setInputParameters(inputs);
				method.setOutputParameters(outputs);
				method.setReturnValue(returnValue);
				return method;
			}

			private static String getAppClassPath() {
				return System.getProperty("user.dir") + File.separator + "bin";
			}
			
			private static String getSootClassPath() {		
				return 	System.getProperty("java.home") + File.separator + "lib" + File.separator +"rt.jar" ;
						
			}
			
			private static List<EntryPoint> getEntryPoints(){
				List<EntryPoint> entryPoints = new ArrayList<EntryPoint>();
			
				EntryPoint entryPoint = new EntryPoint();
				entryPoint.setCanonicalClassName(canonicalClassName);		
				entryPoint.setAllMethods(true);
				entryPoints.add(entryPoint);
				
				return entryPoints;
			}
		}
		'''
	}
}