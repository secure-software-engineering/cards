package de.uni_paderborn.swt.cards.codeGen.library.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(ComponentParts.class)
public @interface ComponentPart {

	String component();

	String name();

}
