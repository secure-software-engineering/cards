package de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions;

import java.lang.annotation.Repeatable;

@Repeatable(ComponentFlowAssumptions.class)
public @interface ComponentFlowAssumption {

	String dataGroup() default "";
}
