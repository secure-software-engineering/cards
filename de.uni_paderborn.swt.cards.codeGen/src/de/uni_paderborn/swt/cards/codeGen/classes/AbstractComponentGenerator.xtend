package de.uni_paderborn.swt.cards.codeGen.classes

class AbstractComponentGenerator {
	static def generate(String classesPackage) {
		'''
			package «classesPackage»;
			
			public abstract class AbstractComponent extends Thread {
				protected abstract void initializePorts();
				
				/**
				 * A method that is called _first_ in the constructor of this class.
				 */
				protected abstract void init();
				
				/**
				 * A method that is called when the thread execution of this component is stopped
				 */
				protected abstract void beforeStop();
				
				public AbstractComponent() {
					super();
					this.init();
				}
				
				public void stopComponentThread() {
					this.beforeStop();
					this.running = false;
				};
				
				protected boolean running = false;
			}
		'''
	}
}
