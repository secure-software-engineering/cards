package de.uni_paderborn.swt.cards.codeGen.classes

class AbstractCompositeComponentGenerator {
	static def generate(String classesPackage) {
		'''
			package «classesPackage»;
			
			public abstract class AbstractCompositeComponent extends AbstractComponent {
			
				public AbstractCompositeComponent() {
					super();
					this.initializePorts();
					this.initializeComponentParts();
					this.setupConnections();
				}
				
				/**
				 * This method will be called repeatedly inside the Thread.run() method. 
				 * Use this to implement your component logic.
				 */
				public abstract void processComponent();
				public abstract void setupConnections();
				protected abstract void startChildComponents();
				protected abstract void stopChildComponents();
				protected abstract void handlePortMappings();
				
				/**
				 * Use this method to register all componentParts of this composite component.
				 *
				 */
				public abstract void initializeComponentParts();
				
				@Override
				public void run() {
					this.running = true;
					
					this.startChildComponents();
					
					while (this.running) {
						handlePortMappings();
						this.processComponent();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					this.stopChildComponents();
				}
			}

		'''
	}
}
