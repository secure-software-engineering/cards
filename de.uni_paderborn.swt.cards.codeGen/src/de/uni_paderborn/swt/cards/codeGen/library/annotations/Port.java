package de.uni_paderborn.swt.cards.codeGen.library.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(Ports.class)
public @interface Port {

	String name();

	String type();

}
