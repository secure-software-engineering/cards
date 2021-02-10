package de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions;

import java.lang.annotation.Repeatable;

@Repeatable(ComponentSanitizerAssumptions.class)
public @interface ComponentSanitizerAssumption {

	String dataGroup();

	String sanitizer();

}
