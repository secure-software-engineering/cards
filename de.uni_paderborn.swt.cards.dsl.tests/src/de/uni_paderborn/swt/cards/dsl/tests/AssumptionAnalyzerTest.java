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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import com.google.inject.Injector;

import de.uni_paderborn.swt.cards.dsl.TMDslStandaloneSetup;
import de.uni_paderborn.swt.cards.dsl.analyzer.Analyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.DataPoint;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;

class AssumptionAnalyzerTest {
	static Model model = null;
	static List<ComponentResult> analysisResults = null;

	@BeforeAll
	static void setup() {
		try {
			Injector injector = new TMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
			XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
			resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

			String path = "models/assumptionTest.tmdsl";
			File file = new File(path);

			URI emfURI = URI.createFileURI(file.getAbsolutePath());

			Resource resource = resourceSet.getResource(emfURI, true);
			model = (Model) resource.getContents().get(0);

			//check if setup worked
			assertEquals("System0", model.getSystem().getName());
			assertEquals(15, model.getSystem().getComponents().size());
			assertEquals(3, model.getSystem().getDatatypes().size());

			analysisResults = Analyzer.analyzeModel(model);

			assertNotNull(analysisResults);
			assertNotEquals(true, analysisResults.isEmpty());
		} catch (StackOverflowError e) {
			fail();
		}
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Component Assumption")
	void testCompAss() {
		// B
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.CompAss.CompAss_BPart.B")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		
		// A
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.CompAss.CompAss_APart.A1")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Component Sanitizer Assumption")
	void testCompSanAss() {
		// B
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.CompSanAss.CompSanAss_B.B")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT") && e.getSanitizers().size() == 1));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		
		// A
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.CompSanAss.CompSanAss_APart.A2")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT") && e.getSanitizers().size() == 1));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Flow Assumption")
	void testFlowAss() {
		// C
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowAss.FlowAss_CPart.C")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(4, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		
		// A
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowAss.FlowAss_APart.A")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		
		// B1
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowAss.FlowAss_B1Part.B1")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		
		
		// C2
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowAss.FlowAss_CPart2.C")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(4, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		
		// A2
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowAss.FlowAss_APart2.A")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		
		// B2
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowAss.FlowAss_B1Part2.B1")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Flow Sanitizer Assumption")
	void testFlowSanAss() {
		// C
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowSanAss.FlowSanAss_CPart.C")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(4, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(3, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT") && e.getSanitizers().size() == 1));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT") && e.getSanitizers().size() == 1));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		
		// a
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowSanAss.FlowSanAss_APart.A")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		
		// b
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowSanAss.FlowSanAss_B2Part.B2")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		
		// c2
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowSanAss.FlowSanAss_CPart2.C")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(4, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(3, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT") && e.getSanitizers().size() == 1));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT") && e.getSanitizers().size() == 1));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("C_DT")));
		
		// a2
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowSanAss.FlowSanAss_APart2.A")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		
		// b2
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.FlowSanAss.FlowSanAss_B2Part2.B2")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Port Assumption")
	void testPortAss() {
		// B
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.PortAss.PortAss_BPart.B")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		
		// A
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.PortAss.PortAss_A3Part.A3")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Port Sanitizer Assumption")
	void testPortSanAss() {
		// B
		List<ComponentResult> crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.PortSanAss.PortSanAss_BPart.B")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		ComponentResult cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(2, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		List<DataPoint> types_0 = cr.getTypes().get(0);
		assertEquals(2, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT") && e.getSanitizers().size() == 1));
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("B_DT")));
		
		
		// A
		crs = analysisResults.stream().filter(e -> e.getStackDerivedName().equals("System0.PortSanAss.PortSanAss_A4Part.A4")).collect(Collectors.toList());
		assertEquals(1, crs.size());
		
		cr = crs.get(0);
		assertEquals(1, cr.getTraces().size());
		assertEquals(1, cr.getTraces().get(0).size());
		assertEquals(1, cr.getTypes().size());
		types_0 = cr.getTypes().get(0);
		assertEquals(1, types_0.size());
		assert(types_0.stream().anyMatch(e -> e.getType().getName().equals("A_DT")));
	}
}