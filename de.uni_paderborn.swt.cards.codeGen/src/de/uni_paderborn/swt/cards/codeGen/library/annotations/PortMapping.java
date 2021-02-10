package de.uni_paderborn.swt.cards.codeGen.library.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(PortMappings.class)
public @interface PortMapping {

	String name();

	String sourcePort();

	String targetPart();

	String targetPort();

}
