package de.uni_paderborn.swt.cards.codeGen.library.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(Components.class)
public @interface Component {

	String value();

}
