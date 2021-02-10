package de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions;

import java.lang.annotation.Repeatable;

@Repeatable(PortSanitizerAssumptions.class)
public @interface PortSanitizerAssumption {

	String dataGroup();

	String sanitizer();

}
