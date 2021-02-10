package de.uni_paderborn.swt.cards.dsl.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import com.google.inject.Injector;

import de.uni_paderborn.swt.cards.dsl.TMDslStandaloneSetup;
import de.uni_paderborn.swt.cards.dsl.analyzer.Analyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.DataPoint;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.Step;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;

class AnalyzerTest {
	static Model model = null;
	static List<ComponentResult> analysisResults = null;
	static int counter = 0;
	
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
	
			//check if setup worked
			assertEquals("System0", model.getSystem().getName());
			assertEquals(15, model.getSystem().getComponents().size());
			assertEquals(6, model.getSystem().getDatatypes().size());
			
			analysisResults = Analyzer.analyzeModel(model);
			
			assertNotNull(analysisResults);
			assertNotEquals(true, analysisResults.isEmpty());
		} catch (StackOverflowError e) {
			fail();
		}
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: A")
	void testTraceA() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: B")
	void testTraceB() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: C")
	void testTraceC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: D")
	void testTraceD() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.D")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: E")
	void testTraceE() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.E")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: F")
	void testTraceF() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.F")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("F_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Single_CC")
	void testTraceSingle_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Single_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Single_CC.Part_A.A")
	void testTraceSingle_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Single_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(1, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Y_CC")
	void testTraceY_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Y_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Y.Part_A.A_CC")
	void testTraceY_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Y_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(4, trace_0.size());
		
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("F_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Y.Part_B.B_CC")
	void testTraceY_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Y_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(4, trace_0.size());
		
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("F_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Y.Part_F.F_CC")
	void testTraceY_CC_Part_F_F() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Y_CC.Part_F.F")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(2, trace_0.size());
		
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("F_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Y.Part_C.C_CC")
	void testTraceY_CC_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Y_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(6, trace_0.size());
		List<Step> trace_1 = cr.getTraces().get(1);
		assertEquals(6, trace_1.size());
		
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(3, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("F_DT")));
		
		List<DataPoint> types_1 = cr.getTypes().get(1);
		assertEquals(3, types_1.size());
		assert(types_1.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_1.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_1.stream().anyMatch(e -> e.getType().getName().equals("F_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Duo_CC")
	void testTraceDuo_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Duo_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Duo_CC.Part_A.A")
	void testTraceDuo_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Duo_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(1, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Duo_CC.Part_B.B")
	void testTraceDuo_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Duo_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(2, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_CC")
	void testTraceTriple_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_CC.Part_A.A")
	void testTraceTriple_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(1, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_CC.Part_B.B")
	void testTraceTriple_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(2, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_CC.Part_C.C")
	void testTraceTriple_CC_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(4, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(3, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_IO_CC")
	void testTraceTriple_IO_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_IO_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_IO_CC.Part_A.A")
	void testTraceTriple_IO_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_IO_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(2, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_IO_CC.Part_B.B")
	void testTraceTriple_IO_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_IO_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(4, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Triple_IO_CC.Part_C.C")
	void testTraceTriple_IO_CC_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Triple_IO_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(6, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(3, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Loop_CC")
	void testTraceLoop_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Loop_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Loop_CC.Part_A.A")
	void testTraceLoop_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Loop_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(8, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Loop_CC.Part_B.B")
	void testTraceLoop_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Loop_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(8, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Loop_CC.Part_C.C")
	void testTraceLoop_CC_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Loop_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(8, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Loop_CC.Part_D.D")
	void testTraceLoop_CC_Part_D_D() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Loop_CC.Part_D.D")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(8, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Inwards_Mapping_CC")
	void testTraceInwards_Mapping_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Inwards_Mapping_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Inwards_Mapping_CC.Part_D.D")
	void testTraceInwards_Mapping_CC_Part_D_D() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Inwards_Mapping_CC.Part_D.D")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(1, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC")
	void testTraceInwards_Mapping_CC_Part_Triple_IO_CC_Triple_IO_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_A.A")
	void testTraceInwards_Mapping_CC_Part_Triple_IO_CC_Triple_IO_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(4, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_B.B")
	void testTraceInwards_Mapping_CC_Part_Triple_IO_CC_Triple_IO_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(6, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(3, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_C.C")
	void testTraceInwards_Mapping_CC_Part_Triple_IO_CC_Triple_IO_CC_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.Inwards_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(8, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC")
	void testTraceIn_Out_Mapping_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_D.D")
	void testTraceIn_Out_Mapping_CC_Part_D_D() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_D.D")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(1, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Triple_IO_CC_Part.Triple_IO_CC")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_Triple_IO_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_A.A")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_Triple_IO_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(4, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_B.B")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_Triple_IO_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(6, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(3, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_C.C")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_Triple_IO_CC_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(8, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Triple_IO_CC_Part.Triple_IO_CC")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_2_Triple_IO_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC_2.Triple_IO_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_Triple_IO_CC_2.Triple_IO_CC.Part_A.A")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_Triple_IO_CC_2_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC_2.Triple_IO_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(14, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_Triple_IO_CC_2.Triple_IO_CC.Part_B.B")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_Triple_IO_CC_2_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC_2.Triple_IO_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(16, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_Triple_IO_CC.Triple_IO_CC_2.Part_C.C")
	void testTraceIn_Out_Mapping_CC_Triple_IO_CCPart_Triple_IO_CC_2_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_Triple_IO_CC_2.Triple_IO_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(18, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(4, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_CC.Part_E.E")
	void testTraceIn_Out_Mapping_CC_Part_E_E() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_CC.Part_E.E")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(22, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC")
	void testTraceIn_Out_Mapping_Loop_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Part_D.D")
	void testTraceIn_Out_Mapping_Loop_CC_Part_D_D() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.DPart.D")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Triple_IO_CC_Part.Triple_IO_CC")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_Triple_IO_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart.Triple_IO_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_A.A")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_Triple_IO_CC_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart.Triple_IO_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_B.B")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_Triple_IO_CC_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart.Triple_IO_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Part_Triple_IO_CC.Triple_IO_CC.Part_C.C")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_Triple_IO_CC_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart.Triple_IO_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Triple_IO_CC_Part.Triple_IO_CC")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_2_Triple_IO_CC() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart_2.Triple_IO_CC")).collect(Collectors.toList());
		assertEquals(0, crs.size());
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Triple_IO_CCPart_2.Triple_IO_CC.Part_A.A")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_Triple_IO_CC_2_Part_A_A() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart_2.Triple_IO_CC.Part_A.A")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTypes().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Triple_IO_CCPart_2.Triple_IO_CC.Part_B.B")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_Triple_IO_CC_2_Part_B_B() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart_2.Triple_IO_CC.Part_B.B")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Part_Triple_IO_CC.Triple_IO_CC_2.Part_C.C")
	void testTraceIn_Out_Mapping_Loop_CC_Triple_IO_CCPart_Triple_IO_CC_2_Part_C_C() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.Triple_IO_CCPart_2.Triple_IO_CC.Part_C.C")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Trace: In_Out_Mapping_Loop_CC.Part_E.E")
	void testTraceIn_Out_Mapping_Loop_CC_Part_E_E() {
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.In_Out_Mapping_Loop_CC.EPart.E")).collect(Collectors.toList());
		counter++;
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(cr.getTypes().size(), cr.getTraces().size());
		
		for(List<DataPoint> pts : cr.getTypes()) {
			for (DataPoint dp : pts) {
				assert(dp.getSanitizers().isEmpty());
			}
		}
		
		List<Step> trace_0 = cr.getTraces().get(0);
		assertEquals(24, trace_0.size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(5, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("D_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("E_DT")));
	}
	
	@AfterAll
	static void checkEverythingWasTested() {
		assertEquals(analysisResults.size(), counter);
	}
}
