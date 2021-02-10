package de.uni_paderborn.swt.cards.dsl.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import de.uni_paderborn.swt.cards.dsl.analyzer.RestrictionAnalyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation;
import de.uni_paderborn.swt.cards.dsl.tmdsl.AllowGroupRestriction;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupPreventRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPartPreventRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPreventRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PreventGroupRestriction;

class RestrictionAnalyzerTest {
	static Model model = null;
	static List<RestrictionViolation> violations = null;

	@BeforeAll
	static void setup() {
		try {
			Injector injector = new TMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
			XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
			resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

			String path = "models/RestrictionsTest.tmdsl";
			File file = new File(path);

			URI emfURI = URI.createFileURI(file.getAbsolutePath());

			Resource resource = resourceSet.getResource(emfURI, true);
			model = (Model) resource.getContents().get(0);

			//check if setup worked
			assertEquals("System0", model.getSystem().getName());
			assertEquals(14, model.getSystem().getComponents().size());
			assertEquals(6, model.getSystem().getDatatypes().size());

			List<ComponentResult> analysisResults = Analyzer.analyzeModel(model);
			violations = RestrictionAnalyzer.checkRestrictions(model.getRestriction(), analysisResults);

			assertNotNull(analysisResults);
			assertNotEquals(true, analysisResults.isEmpty());
		} catch (StackOverflowError e) {
			fail();
		}
	}
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Allow Restriction")
	void testAllowRestriction() {
		List<RestrictionViolation> vio = violations.stream().filter(e -> e.getViolatedRestriction() instanceof AllowGroupRestriction).collect(Collectors.toList());
		assertEquals(6, vio.size());
		
		// Prevent A_DT, B_DT, C_DT from componentGroups Sink3_CG, Sink2_CG, Sink1_CG except Sink_2, Sink_1 unless sanitized by Sanitizer
		assert(vio.stream().anyMatch(
				e -> e.getDp().getType().getName().equals("A_DT") &&
					e.getStackDerivedName().equals("System0.Allow_CC.Sink_3Part.Sink_3") &&
					e.getViolatedRefinement() instanceof ComponentGroupPreventRefinement &&
					((ComponentGroupPreventRefinement) e.getViolatedRefinement()).getComponentGroup().size() == 3 &&
					((ComponentGroupPreventRefinement) e.getViolatedRefinement()).getSanitizerOption().getSanitizer().getName().equals("Sanitizer") &&
					((ComponentGroupPreventRefinement) e.getViolatedRefinement()).getDataAsset().size() == 3
				));
		
		// Prevent A_DT, B_DT, C_DT from componentPart Sink_3Part unless sanitized by Sanitizer
		assert(vio.stream().anyMatch(
				e -> e.getDp().getType().getName().equals("A_DT") &&
					e.getStackDerivedName().equals("System0.Allow_CC.Sink_3Part.Sink_3") &&
					e.getViolatedRefinement() instanceof ComponentPartPreventRefinement &&
					((ComponentPartPreventRefinement) e.getViolatedRefinement()).getComponentPart().get(0).getName().equals("Sink_3Part") &&
					((ComponentPartPreventRefinement) e.getViolatedRefinement()).getSanitizerOption().getSanitizer().getName().equals("Sanitizer") &&
					((ComponentPartPreventRefinement) e.getViolatedRefinement()).getDataAsset().size() == 3
				));
		
		// Prevent A_DT, B_DT, C_DT from component Sink_3 unless sanitized by Sanitizer
		assert(vio.stream().anyMatch(
				e -> e.getDp().getType().getName().equals("A_DT") &&
					e.getStackDerivedName().equals("System0.Allow_CC.Sink_3Part.Sink_3") &&
					e.getViolatedRefinement() instanceof ComponentPreventRefinement &&
					((ComponentPreventRefinement) e.getViolatedRefinement()).getComponent().get(0).getName().equals("Sink_3") &&
					((ComponentPreventRefinement) e.getViolatedRefinement()).getSanitizerOption().getSanitizer().getName().equals("Sanitizer") &&
					((ComponentPreventRefinement) e.getViolatedRefinement()).getDataAsset().size() == 3
				));
		
		// Prevent A_DT, B_DT, C_DT from componentGroups Sink1_CG, Sink2_CG, Sink3_CG except Sink_2, Sink_3
		assert(vio.stream().anyMatch(
				e -> e.getDp().getType().getName().equals("A_DT") &&
					e.getStackDerivedName().equals("System0.Allow_CC.Sink_1Part.Sink_1") &&
					e.getViolatedRefinement() instanceof ComponentGroupPreventRefinement &&
					((ComponentGroupPreventRefinement) e.getViolatedRefinement()).getComponentGroup().size() == 3 &&
					((ComponentGroupPreventRefinement) e.getViolatedRefinement()).getSanitizerOption() == null &&
					((ComponentGroupPreventRefinement) e.getViolatedRefinement()).getDataAsset().size() == 3
				));
		
		// Prevent A_DT, B_DT, C_DT from componentPart Sink_1Part
		assert(vio.stream().anyMatch(
				e -> e.getDp().getType().getName().equals("A_DT") &&
					e.getStackDerivedName().equals("System0.Allow_CC.Sink_1Part.Sink_1") &&
					e.getViolatedRefinement() instanceof ComponentPartPreventRefinement &&
					((ComponentPartPreventRefinement) e.getViolatedRefinement()).getComponentPart().get(0).getName().equals("Sink_1Part") &&
					((ComponentPartPreventRefinement) e.getViolatedRefinement()).getSanitizerOption() == null &&
					((ComponentPartPreventRefinement) e.getViolatedRefinement()).getDataAsset().size() == 3
				));
		
		// Prevent A_DT, B_DT, C_DT from component Sink_1
		assert(vio.stream().anyMatch(
				e -> e.getDp().getType().getName().equals("A_DT") &&
					e.getStackDerivedName().equals("System0.Allow_CC.Sink_1Part.Sink_1") &&
					e.getViolatedRefinement() instanceof ComponentPreventRefinement &&
					((ComponentPreventRefinement) e.getViolatedRefinement()).getComponent().get(0).getName().equals("Sink_1") &&
					((ComponentPreventRefinement) e.getViolatedRefinement()).getSanitizerOption() == null &&
					((ComponentPreventRefinement) e.getViolatedRefinement()).getDataAsset().size() == 3
				));
	}
	
	
	@org.junit.jupiter.api.Test
	@DisplayName("Test Prevent Restriction")
	void testPreventRestriction() {
		List<RestrictionViolation> vio = violations.stream().filter(e -> e.getViolatedRestriction() instanceof PreventGroupRestriction).collect(Collectors.toList());
		assertEquals(14, vio.size());
		
		Map<String, String> dataAndStackDerivedName = new HashMap<String, String>();
		
		dataAndStackDerivedName.put("F_DT", "System0.F");
		dataAndStackDerivedName.put("D_DT", "System0.Prevent_CC.DPart.D");
		dataAndStackDerivedName.put("D_DT", "System0.Prevent_CC.Sink_3Part.Sink_3");
		dataAndStackDerivedName.put("D_DT", "System0.D");
		dataAndStackDerivedName.put("E_DT", "System0.E");
		dataAndStackDerivedName.put("D_DT", "System0.Prevent_CC.D_SanPart.D_San");
		dataAndStackDerivedName.put("D_DT", "System0.Prevent_CC.Sink_4Part.Sink_4");
		dataAndStackDerivedName.put("E_DT", "System0.Prevent_CC.Sink_4Part.Sink_4");
		dataAndStackDerivedName.put("F_DT", "System0.Prevent_CC.Sink_4Part.Sink_4");
		dataAndStackDerivedName.put("D_DT", "System0.D_San");
		dataAndStackDerivedName.put("D_DT", "System0.Prevent_CC.DPart3.D");
		dataAndStackDerivedName.put("D_DT", "System0.Prevent_CC.DPart2.D");
		dataAndStackDerivedName.put("E_DT", "System0.Prevent_CC.EPart.E");
		dataAndStackDerivedName.put("F_DT", "System0.Prevent_CC.FPart.F");
		
		for (String key : dataAndStackDerivedName.keySet()) {
			assert(violations.stream().anyMatch(e -> e.getDp().getType().getName().equals(key) && e.getStackDerivedName().equals(dataAndStackDerivedName.get(key))));
		}
	}
	
	
}