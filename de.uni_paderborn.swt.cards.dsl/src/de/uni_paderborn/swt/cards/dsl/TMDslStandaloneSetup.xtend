/*
 * generated by Xtext 2.18.0.M3
 */
package de.uni_paderborn.swt.cards.dsl


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class TMDslStandaloneSetup extends TMDslStandaloneSetupGenerated {

	def static void doSetup() {
		new TMDslStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}
