package de.uni_paderborn.swt.cards.analysisGen

import de.uni_paderborn.swt.cards.dsl.analyzer.Analyzer
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult
import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.CompositeComponent
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port
import de.uni_paderborn.swt.cards.mappingModel.ComponentMapping
import de.uni_paderborn.swt.cards.mappingModel.InPortMapping
import de.uni_paderborn.swt.cards.mappingModel.Mapping
import de.uni_paderborn.swt.cards.mappingModel.Model
import de.uni_paderborn.swt.cards.mappingModel.OutPortMapping
import de.uni_paderborn.swt.cards.mappingModel.SanitizerMapping
import de.uni_paderborn.swt.cards.mappingModel.SourceMapping
import java.io.File
import java.io.FileWriter
import java.util.ArrayList
import java.util.List
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataFlowAssumption
import org.eclipse.emf.ecore.EObject
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowSanitizationAssumption

class AnalysisGenerator {
	
	static def generate(String srcFolder, String srcGenFolder, String packageName, de.uni_paderborn.swt.cards.dsl.tmdsl.Model tmdslModel, Model mappingModel, Assumption assumption) {
		val packagePath = packageName.split("\\.").stream.reduce[a,b | a + File.separator + b].get;
		
		val dirPath = srcFolder + File.separator + packagePath + File.separator;
		var fileName = "Utility.java";
		val utilityContent = Utility.generate(packageName);
		writeToFile(dirPath, utilityContent, fileName, true);
		
		fileName = "ConsoleResultListener.java";
		val consoleResultListenerContent = ConsoleResultListener.generate(packageName);
		writeToFile(dirPath, consoleResultListenerContent, fileName, true);
		
		val modelAnalysisResult = Analyzer.analyzeModel(tmdslModel);
		
		val classNames = new ArrayList<String>();
		
		classNames.addAll(generateComponentFlowSanitizerAssumptions(srcFolder, packageName, mappingModel, assumption));
		classNames.addAll(generateComponentFlowAssumptions(srcFolder, packageName, mappingModel, assumption));
		
		classNames.addAll(generatePortSanitizerAssumptions(srcFolder, packageName, mappingModel, assumption, modelAnalysisResult));
		classNames.addAll(generatePortAssumptions(srcFolder, packageName, mappingModel, assumption, modelAnalysisResult));
		
		classNames.addAll(generateComponentSanitizerAssumptions(srcFolder, packageName, mappingModel, assumption, modelAnalysisResult));
		classNames.addAll(generateComponentAssumptions(srcFolder, packageName, mappingModel, assumption, modelAnalysisResult));
		
		val content = MainAnalysis.generate(packageName, classNames);
		fileName = "MainAnalysis.java";
		writeToFile(dirPath, content, fileName, true);
	}
	
	private static def generateComponentFlowSanitizerAssumptions(String srcFolder, String packageName, Model mappingModel, Assumption assumption) {
		val componentFlowSanitizerAssumptions = assumption.sanitizersAssumptions;
		val result = new ArrayList<String>();
		
		var i = 0;
		
		for (assump : componentFlowSanitizerAssumptions) {
			val compMapping = mappingModel.mappings
				.filter[m | m instanceof ComponentMapping]
				.map[m | m as ComponentMapping]
				.filter[m | m.component === assump.comp]
				.get(0);
				
			if (compMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val sourceMapping = mappingModel.mappings
				.filter[m | m instanceof InPortMapping]
				.map[m | m as InPortMapping]
				.filter[m | m.port === assump.source]
				.get(0);	
			
			if (sourceMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val targetMapping = mappingModel.mappings
				.filter[m | m instanceof OutPortMapping]
				.map[m | m as OutPortMapping]
				.filter[m | m.port === assump.target]
				.get(0);	
			
			if (targetMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val sanitizerMapping = mappingModel.mappings
				.filter[m | m instanceof SanitizerMapping]
				.map[m | m as SanitizerMapping]
				.filter[m | m.sanitizer === assump.sanitizer]
				.get(0);	
			
			if (sanitizerMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val className = "CompFlowSanAssumption" + i;
			val fileName = className + ".java";
			val content = ComponentFlowSanitizerAssumption.generate(packageName, className, compMapping, sourceMapping,targetMapping, sanitizerMapping, getAssumptionId(assump));
			
			val packagePath = packageName.split("\\.").stream.reduce[a,b | a + File.separator + b].get;
			val dirPath = srcFolder + File.separator + packagePath + File.separator;
			
			writeToFile(dirPath, content, fileName, true);
			result.add(className)
			i++;
		}
		
		return result;
	}
	
	private static def generateComponentFlowAssumptions(String srcFolder, String packageName, Model mappingModel, Assumption assumption) {
		val componentFlowAssumptions = assumption.flowAssumptions;
		val result = new ArrayList<String>();
		
		var i = 0;
		
		for (assump : componentFlowAssumptions) {
			val compMapping = mappingModel.mappings
				.filter[m | m instanceof ComponentMapping]
				.map[m | m as ComponentMapping]
				.filter[m | m.component === assump.comp]
				.get(0);
				
			if (compMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val sourceMapping = mappingModel.mappings
				.filter[m | m instanceof InPortMapping]
				.map[m | m as InPortMapping]
				.filter[m | m.port === assump.source]
				.get(0);	
			
			if (sourceMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val targetMapping = mappingModel.mappings
				.filter[m | m instanceof OutPortMapping]
				.map[m | m as OutPortMapping]
				.filter[m | m.port === assump.target]
				.get(0);	
			
			if (targetMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val className = "CompFlowAssumption"+i;
			val fileName = className + ".java";
			val content = ComponentFlowAssumption.generate(packageName, className, compMapping, sourceMapping,targetMapping, getAssumptionId(assump));
			
			val packagePath = packageName.split("\\.").stream.reduce[a,b | a + File.separator + b].get;
			val dirPath = srcFolder + File.separator + packagePath + File.separator;
			
			writeToFile(dirPath, content, fileName, true);
			result.add(className);
			i++;
		}
		return result;
	}
	
	private static def generatePortSanitizerAssumptions(String srcFolder, String packageName, Model mappingModel, Assumption assumption, List<ComponentResult> modelAnalysisResults) {
		val portSanitizerAssumptions = assumption.portSanitizerAssumptions;
		val result = new ArrayList<String>();
		
		var i = 0;
		
		for (assump : portSanitizerAssumptions) {
			val compMapping = mappingModel.mappings
				.filter[m | m instanceof ComponentMapping]
				.map[m | m as ComponentMapping]
				.filter[m | m.component.ports.contains(assump.sanitizerPort)]
				.get(0);
				
			if (compMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val interestingSourcePorts = new ArrayList<Port>;
			
			if (assump.sanitizedData === null || assump.sanitizedData.groupedData.size == 0) {
				interestingSourcePorts.addAll(compMapping.component.ports)
			} else {
				// for all model analysis results
				for (ComponentResult cr : modelAnalysisResults) {
					// if the result is about the component
					if (cr.stack.contains(compMapping.component)) {
						// for traces of that result
						for (var j = 0; j < cr.types.length; j++) {
							// if it contains data for this assumption
							if (cr.types.get(j).stream.anyMatch(dp | assump.sanitizedData.groupedData.contains(dp.type))) {
								// we add it to the taint sources
								interestingSourcePorts.add(cr.traces.get(j).get(0).p);
							}
						}
					}
				}
			}
			
			val sourceMappings = mappingModel.mappings
				.filter[m | m instanceof InPortMapping]
				.map[m | m as InPortMapping]
				.filter[m | m.port != assump.sanitizerPort && compMapping.component.ports.contains(m.port)]
				.filter[m | interestingSourcePorts.contains(m.port)]
				.map[m | m as Mapping]
				.toList;
			
			val generatorMethods = mappingModel.mappings
				.filter[m | m instanceof SourceMapping]
				.map[m | m as SourceMapping]
				.filter[m | m.component.ports.contains(assump.sanitizerPort)]
				.filter[m | assump.sanitizedData == null || assump.sanitizedData.groupedData.size == 0 || assump.sanitizedData.groupedData.contains(m.dataType)]
				.map[m | m as Mapping]
				.toList;
			
			if (generatorMethods.size > 0) {
				sourceMappings.addAll(generatorMethods);
			}
			
			
			if (sourceMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val targetMappings = mappingModel.mappings
				.filter[m | m instanceof OutPortMapping]
				.map[m | m as OutPortMapping]
				.filter[m | m.port === assump.sanitizerPort]
				.map[m | m as Mapping]
				.toList;	
			
			if (targetMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val sanitizerMapping = mappingModel.mappings
				.filter[m | m instanceof SanitizerMapping]
				.map[m | m as SanitizerMapping]
				.filter[m | m.sanitizer === assump.sanitizer]
				.get(0);	
			
			if (sanitizerMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val className = "PortSanitizerAssumption" + i;
			val fileName = className + ".java";
			val content = PortSanitizerAssumption.generate(packageName, className, compMapping, sourceMappings, targetMappings, sanitizerMapping, getAssumptionId(assump));
			
			val packagePath = packageName.split("\\.").stream.reduce[a,b | a + File.separator + b].get;
			val dirPath = srcFolder + File.separator + packagePath + File.separator;
			
			writeToFile(dirPath, content, fileName, true);
			result.add(className);
			i++;
		}
		return result;
	}
	
	private static def generatePortAssumptions(String srcFolder, String packageName, Model mappingModel, Assumption assumption, List<ComponentResult> modelAnalysisResults) {
		val portAssumptions = assumption.portAssumptions;
		val result = new ArrayList<String>();
		
		var i = 0;
		
		for (assump : portAssumptions) {
			val compMapping = mappingModel.mappings
				.filter[m | m instanceof ComponentMapping]
				.map[m | m as ComponentMapping]
				.filter[m | m.component.ports.contains(assump.port)]
				.get(0);
				
			if (compMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val interestingSourcePorts = new ArrayList<Port>;
			
			if (assump.data === null || assump.data.groupedData.size == 0) {
				interestingSourcePorts.addAll(compMapping.component.ports)
			} else {
				// for all model analysis results
				for (ComponentResult cr : modelAnalysisResults) {
					// if the result is about the component
					if (cr.stack.contains(compMapping.component)) {
						// for traces of that result
						for (var j = 0; j < cr.types.length; j++) {
							// if it contains data for this assumption
							if (cr.types.get(j).stream.anyMatch(dp | assump.data.groupedData.contains(dp.type))) {
								// we add it to the taint sources
								interestingSourcePorts.add(cr.traces.get(j).get(0).p);
							}
						}
					}
				}
			}
			
			val sourceMappings = mappingModel.mappings
				.filter[m | m instanceof InPortMapping]
				.map[m | m as InPortMapping]
				.filter[m | m.port != assump.port && compMapping.component.ports.contains(m.port)]
				.filter[m | interestingSourcePorts.contains(m.port)]
				.map[m | m as Mapping]
				.toList;
			
			val generatorMethods = mappingModel.mappings
				.filter[m | m instanceof SourceMapping]
				.map[m | m as SourceMapping]
				.filter[m | m.component.ports.contains(assump.port)]
				.filter[m | assump.data == null || assump.data.groupedData.size == 0 || assump.data.groupedData.contains(m.dataType)]
				.map[m | m as Mapping]
				.toList;
			
			if (generatorMethods.size > 0) {
				sourceMappings.addAll(generatorMethods);
			}
			
			
			if (sourceMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val targetMappings = mappingModel.mappings
				.filter[m | m instanceof OutPortMapping]
				.map[m | m as OutPortMapping]
				.filter[m | m.port === assump.port]
				.map[m | m as Mapping]
				.toList;	
			
			if (targetMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val className ="PortAssumption"+i; 
			val fileName = className + ".java";
			val content = PortAssumption.generate(packageName, className, compMapping, sourceMappings, targetMappings, getAssumptionId(assump));
			
			val packagePath = packageName.split("\\.").stream.reduce[a,b | a + File.separator + b].get;
			val dirPath = srcFolder + File.separator + packagePath + File.separator;
			
			writeToFile(dirPath, content, fileName, true);
			i++;
			result.add(className);
		}
		return result;
	}
	
	private static def generateComponentSanitizerAssumptions(String srcFolder, String packageName, Model mappingModel, Assumption assumption, List<ComponentResult> modelAnalysisResults) {
		val componentSanitizerAssumption = assumption.componentSanitzerAssumptions;
		val result = new ArrayList<String>();
		
		var i = 0;
		
		for (assump : componentSanitizerAssumption) {
			val compMapping = mappingModel.mappings
				.filter[m | m instanceof ComponentMapping]
				.map[m | m as ComponentMapping]
				.filter[m | m.component == assump.sanitizerComp]
				.get(0);
				
			if (compMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val interestingSourcePorts = new ArrayList<Port>;
			
			if (assump.sanitizedData === null || assump.sanitizedData.groupedData.size == 0) {
				interestingSourcePorts.addAll(compMapping.component.ports)
			} else {
				// for all model analysis results
				for (ComponentResult cr : modelAnalysisResults) {
					// if the result is about the component
					if (cr.stack.contains(compMapping.component)) {
						// for traces of that result
						for (var j = 0; j < cr.types.length; j++) {
							// if it contains data for this assumption
							if (cr.types.get(j).stream.anyMatch(dp | assump.sanitizedData.groupedData.contains(dp.type))) {
								// we add it to the taint sources
								interestingSourcePorts.add(cr.traces.get(j).get(0).p);
							}
						}
					}
				}
			}
			
			val sourceMappings = mappingModel.mappings
				.filter[m | m instanceof InPortMapping]
				.map[m | m as InPortMapping]
				.filter[m | compMapping.component.ports.contains(m.port)]
				.filter[m | interestingSourcePorts.contains(m.port)]
				.map[m | m as Mapping]
				.toList;
			
			val generatorMethods = mappingModel.mappings
				.filter[m | m instanceof SourceMapping]
				.map[m | m as SourceMapping]
				.filter[m | m.component == assump.sanitizerComp]
				.filter[m | assump.sanitizedData == null || assump.sanitizedData.groupedData.size == 0 || assump.sanitizedData.groupedData.contains(m.dataType)]
				.map[m | m as Mapping]
				.toList;
			
			if (generatorMethods.size > 0) {
				sourceMappings.addAll(generatorMethods);
			}
			
			
			if (sourceMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val targetMappings = mappingModel.mappings
				.filter[m | m instanceof OutPortMapping]
				.map[m | m as OutPortMapping]
				.filter[m | compMapping.component.ports.contains(m.port)]
				.map[m | m as Mapping]
				.toList;	
			
			if (targetMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val sanitizerMapping = mappingModel.mappings
				.filter[m | m instanceof SanitizerMapping]
				.map[m | m as SanitizerMapping]
				.filter[m | m.sanitizer === assump.sanitizer]
				.get(0);	
			
			if (sanitizerMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val className = "ComponentSanitizerAssumption"+i; 
			val fileName = className + ".java";
			val content = ComponentSanitizerAssumption.generate(packageName, className, compMapping, sourceMappings, targetMappings, sanitizerMapping, getAssumptionId(assump));
			
			val packagePath = packageName.split("\\.").stream.reduce[a,b | a + File.separator + b].get;
			val dirPath = srcFolder + File.separator + packagePath + File.separator;
			
			writeToFile(dirPath, content, fileName, true);
			result.add(className);
			i++;
		}
		return result;
	}
	
	private static def generateComponentAssumptions(String srcFolder, String packageName, Model mappingModel, Assumption assumption, List<ComponentResult> modelAnalysisResults) {
		val componentAssumption = assumption.componentAssumptions;
		val result = new ArrayList<String>();
		
		var i = 0;
		
		for (assump : componentAssumption) {
			if (assump.comp instanceof CompositeComponent) {
				println("Composite Component Assumptions are not supported by source code analysis.")
				return result;
			}
			
			val compMapping = mappingModel.mappings
				.filter[m | m instanceof ComponentMapping]
				.map[m | m as ComponentMapping]
				.filter[m | m.component == assump.comp]
				.get(0);
				
			if (compMapping === null) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val interestingSourcePorts = new ArrayList<Port>;
			
			
			if (assump.data === null || assump.data.groupedData.size == 0) {
				interestingSourcePorts.addAll(compMapping.component.ports)
			} else {
				// for all model analysis results
				for (ComponentResult cr : modelAnalysisResults) {
					// if the result is about the component
					if (cr.stack.contains(compMapping.component)) {
						// for traces of that result
						for (var j = 0; j < cr.types.length; j++) {
							// if it contains data for this assumption
							if (cr.types.get(j).stream.anyMatch(dp | assump.data.groupedData.contains(dp.type))) {
								// we add it to the taint sources
								interestingSourcePorts.add(cr.traces.get(j).get(0).p);
							}
						}
					}
				}
			}

			
			val sourceMappings = mappingModel.mappings
				.filter[m | m instanceof InPortMapping]
				.map[m | m as InPortMapping]
				.filter[m | compMapping.component.ports.contains(m.port)]
				.filter[m | interestingSourcePorts.contains(m.port)]
				.map[m | m as Mapping]
				.toList;
			
			val generatorMethods = mappingModel.mappings
				.filter[m | m instanceof SourceMapping]
				.map[m | m as SourceMapping]
				.filter[m | m.component == assump.comp]
				.filter[m | assump.data == null || assump.data.groupedData.size == 0 || assump.data.groupedData.contains(m.dataType)]
				.map[m | m as Mapping]
				.toList;
			
			if (generatorMethods.size > 0) {
				sourceMappings.addAll(generatorMethods);
			}
			
			
			if (sourceMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val targetMappings = mappingModel.mappings
				.filter[m | m instanceof OutPortMapping]
				.map[m | m as OutPortMapping]
				.filter[m | compMapping.component.ports.contains(m.port)]
				.map[m | m as Mapping]
				.toList;	
			
			if (targetMappings.size == 0) {
				throw new Exception("Mapping Model does not conform to tmdsl Model.");
			}
			
			val className = "ComponentAssumption"+i;
			val fileName = className + ".java";
			val content = ComponentAssumption.generate(packageName, className, compMapping, sourceMappings, targetMappings, getAssumptionId(assump));
			
			val packagePath = packageName.split("\\.").stream.reduce[a,b | a + File.separator + b].get;
			val dirPath = srcFolder + File.separator + packagePath + File.separator;
			
			writeToFile(dirPath, content, fileName, true);
			result.add(className);
			i++;
		}
		return result;
	}	

	/**
	 * Write content to file
	 */
	private static def writeToFile(String directory, CharSequence content, String fileName, boolean replace) {
		val d = new File(directory);
		d.mkdirs
		
		val filePath = directory + File.separator + fileName;
		val f = new File(filePath);
		if (f.exists && replace) {
			f.delete()
		}
		if (!f.exists) {
			f.createNewFile();
			val fw = new FileWriter(f);
			fw.append(content)
			fw.flush
			fw.close				
		}
	}
	
	private static def getAssumptionId(EObject assumption) {
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption) {
			val compAss = assumption as de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption;
			return compAss.class.name + ":" + compAss.comp.name + ":" + compAss.data.name;
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption) {
			val compAss = assumption as de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption;
			return compAss.class.name + ":" + compAss.sanitizerComp.name + ":" + compAss.sanitizedData.name + ":" + compAss.sanitizer.name;
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption) {
			val compAss = assumption as de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption;
			return compAss.class.name + ":" + compAss.port.name + ":" + compAss.data.name;
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption) {
			val compAss = assumption as de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption;
			return compAss.class.name + ":" + compAss.sanitizerPort.name + ":" + compAss.sanitizedData.name + ":" + compAss.sanitizer.name;
		}
		if (assumption instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption) {
			val compAss = assumption as de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption;
			return compAss.class.name + ":" + compAss.comp.name + ":" + compAss.data.name;
		}
		if (assumption instanceof ComponentFlowSanitizationAssumption) {
			val compAss = assumption as de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowSanitizationAssumption;
			return compAss.class.name + ":" + compAss.comp.name + ":" + compAss.data.name + ":" + compAss.sanitizer.name;
		}
		return "invalid object"
	}
}