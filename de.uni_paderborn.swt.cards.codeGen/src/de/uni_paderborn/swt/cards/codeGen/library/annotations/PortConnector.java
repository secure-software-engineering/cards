package de.uni_paderborn.swt.cards.codeGen.library.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(PortConnectors.class)
public @interface PortConnector {

	String name();

	String sourcePart();

	String sourcePort();

	String targetPart();

	String targetPort();

}