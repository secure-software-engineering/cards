package de.uni_paderborn.swt.cards.attackgraphTransformation.tests;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import com.google.inject.Injector;

import attackgraph.AND_ConnectorNode;
import attackgraph.AttackGraph;
import attackgraph.MitigationNode;
import attackgraph.OR_ConnectorNode;
import de.uni_paderborn.swt.cards.attackgraphTransformationInvocation.Main;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.TMDslStandaloneSetup;

class AttackGraphTransformationTest {
	static Model model = null;
	static List<AttackGraph> attackgraphs = null;
	
	@BeforeAll
	static void setup() {
		try {
			Injector injector = new TMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
			XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
			resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
			
			String path = "models/AttackGraphTransformationTest.tmdsl";
			File file = new File(path);
			
			URI emfURI = URI.createFileURI(file.getAbsolutePath());
			
			Resource resource = resourceSet.getResource(emfURI, true);
			model = (Model) resource.getContents().get(0);
			
			System.out.println(model.getSystem().getComponents().size());
			
			URI transformationURI = Main.getTransformationURIFromPlugin();

			attackgraphs = Main.transformTmdslToAttackgraphs(model, transformationURI, new NullProgressMonitor());

			assert(attackgraphs != null);
			assert(attackgraphs.size() > 0);
			System.out.println(attackgraphs.get(0).getRootNode());
			System.out.println(attackgraphs.get(1).getRootNode());
			
		} catch (StackOverflowError e) {
			fail();
		}
	}

	@org.junit.jupiter.api.Test
	@DisplayName("Test AllowGroupRestriction")
	void testAllowRestriction() {
		AttackGraph attackgraph = attackgraphs.stream().filter(e -> e.getRootNode().getName().startsWith("Allow")).collect(Collectors.toList()).get(0);
		
		assertEquals(13, attackgraph.eContents().stream().filter(e -> e instanceof MitigationNode).count());
		assertEquals(1, attackgraph.eContents().stream().filter(e -> e instanceof OR_ConnectorNode).count());
		assertEquals(81, attackgraph.eContents().stream().filter(e -> e instanceof AND_ConnectorNode).count());
		
		for (AND_ConnectorNode andNode : attackgraph.eContents().stream().filter(e -> e instanceof AND_ConnectorNode).map(e -> (AND_ConnectorNode) e).collect(Collectors.toList())) {
			assertEquals(4, andNode.getChildNode().size());
		}
	}

	@org.junit.jupiter.api.Test
	@DisplayName("Test PreventGroupRestriction")
	void testPreventRestriction() {
		AttackGraph attackgraph = attackgraphs.stream().filter(e -> e.getRootNode().getName().startsWith("Prevent")).collect(Collectors.toList()).get(0);
		
		assertEquals(7, attackgraph.eContents().stream().filter(e -> e instanceof MitigationNode).count());
		assertEquals(1, attackgraph.eContents().stream().filter(e -> e instanceof OR_ConnectorNode).count());
		assertEquals(4, attackgraph.eContents().stream().filter(e -> e instanceof AND_ConnectorNode).count());
		
		assertEquals(1, attackgraph.eContents().stream().filter(e -> e instanceof AND_ConnectorNode && ((AND_ConnectorNode) e).getChildNode().size() == 2).count());
		assertEquals(2, attackgraph.eContents().stream().filter(e -> e instanceof AND_ConnectorNode && ((AND_ConnectorNode) e).getChildNode().size() == 3).count());
		assertEquals(1, attackgraph.eContents().stream().filter(e -> e instanceof AND_ConnectorNode && ((AND_ConnectorNode) e).getChildNode().size() == 4).count());
	}
}
