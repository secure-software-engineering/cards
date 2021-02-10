package de.uni_paderborn.swt.cards.codeGen.classes

class AbstractAtomicComponentGenerator {
	static def generate(String classesPackage) {
		'''
			package «classesPackage»;
			
			public abstract class AbstractAtomicComponent extends AbstractComponent {
				
				public AbstractAtomicComponent() {
					this.initializePorts();
				}
				
				/**
				 * This method will be called repeatedly inside the Thread.run() method. 
				 * Use this to implement your component logic.
				 */
				public abstract void doSomething();
				
				
				@Override
				public void run() {
					this.running = true;
					
					while (this.running) {
						this.doSomething();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		'''
	}
}
