package de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions;

import java.lang.annotation.Repeatable;

@Repeatable(ComponentFlowSanitizationAssumptions.class)
public @interface ComponentFlowSanitizationAssumption {

	String dataGroup();

	String sanitizer();


}
