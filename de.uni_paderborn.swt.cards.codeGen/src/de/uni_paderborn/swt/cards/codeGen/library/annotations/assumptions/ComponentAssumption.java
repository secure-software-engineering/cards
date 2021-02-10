package de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions;

import java.lang.annotation.Repeatable;

@Repeatable(ComponentAssumptions.class)
public @interface ComponentAssumption {

	String value();

}
