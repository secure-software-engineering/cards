package de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions;

import java.lang.annotation.Repeatable;

@Repeatable(PortAssumptions.class)
public @interface PortAssumption {

	String dataGroup();

}
