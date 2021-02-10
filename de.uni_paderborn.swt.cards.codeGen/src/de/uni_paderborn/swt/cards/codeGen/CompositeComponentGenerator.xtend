package de.uni_paderborn.swt.cards.codeGen

import de.uni_paderborn.swt.cards.dsl.tmdsl.CompositeComponent
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart
import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortMapping
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortConnector
import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption

class CompositeComponentGenerator {
	def generate(String packageName, String classesPackage, CompositeComponent cc, Assumption assumption) {
		'''
		package «packageName»;
		
		«IF cc.ports.stream.anyMatch(p | p.portType == PortType.INOUT)»
			import «classesPackage».GenericINOUTPort;
		«ENDIF»
		«IF cc.ports.stream.anyMatch(p | p.portType == PortType.IN)»
			import «classesPackage».GenericINPort;
		«ENDIF»
		«IF cc.ports.stream.anyMatch(p | p.portType == PortType.OUT)»
			import «classesPackage».GenericOUTPort;
		«ENDIF»
		import «classesPackage».AbstractCompositeComponent;
		import de.uni_paderborn.swt.cards.codeGen.library.annotations.*;
		
		@Component("«cc.name»")
		public abstract class Abstract«cc.name.toFirstUpper»_CC extends AbstractCompositeComponent {
			
			«FOR Port port : cc.ports»
				@Port(name="«port.name»", type="«port.portType»")
				«IF (port.portType == PortType.IN)»
					private GenericINPort «port.name.toFirstLower»;
				«ENDIF»
				«IF (port.portType == PortType.OUT)»
					private GenericOUTPort «port.name.toFirstLower»;
				«ENDIF»
				«IF (port.portType == PortType.INOUT)»
					private GenericINOUTPort «port.name.toFirstLower»;
				«ENDIF»
			«ENDFOR»
			
			«FOR Port port : cc.ports»
				«IF (port.portType == PortType.IN)»
					public GenericINPort get«port.name.toFirstUpper»() {
						return «port.name.toFirstLower»;
					}
				«ENDIF»
				«IF (port.portType == PortType.OUT)»
					public GenericOUTPort get«port.name.toFirstUpper»() {
						return «port.name.toFirstLower»;
					}
				«ENDIF»
				«IF (port.portType == PortType.INOUT)»
					public GenericINOUTPort get«port.name.toFirstUpper»() {
						return «port.name.toFirstLower»;
					}
				«ENDIF»
			«ENDFOR»
			
			//ComponentParts
			«FOR ComponentPart part : cc.componentParts»
				@ComponentPart(name="«part.name»", component="«part.component.name»")
				protected Abstract«part.component.name.toFirstUpper»_«IF part.component instanceof AtomicComponent»AC«ELSE»CC«ENDIF» «part.name.toFirstLower»;
			«ENDFOR»

			@Override
			public void initializePorts() {				
				«FOR Port port : cc.ports»
					«IF (port.portType == PortType.IN)»
						«port.name.toFirstLower» = new GenericINPort("«port.name»");
					«ENDIF»
					«IF (port.portType == PortType.OUT)»
						«port.name.toFirstLower» = new GenericOUTPort("«port.name»");
					«ENDIF»
					«IF (port.portType == PortType.INOUT)»
						«port.name.toFirstLower» = new GenericINOUTPort("«port.name»");
					«ENDIF»
				«ENDFOR»
			}
			
			«FOR Port port : cc.ports»
				«IF (port.portType == PortType.IN || port.portType == PortType.INOUT)»
					public String readData«port.name.toFirstUpper»() {
						return «port.name.toFirstLower».readData();
					}
					
				«ENDIF»
				«IF (port.portType == PortType.OUT || port.portType == PortType.INOUT)»
					public void writeData«port.name.toFirstUpper»(String data) {
						«port.name.toFirstLower».writeData(data);
					}
					
				«ENDIF»
			«ENDFOR»
			«FOR PortConnector connector : cc.portconnectors»
			@PortConnector(name="«connector.name»", sourcePart="«connector.sourcePart.name»", sourcePort="«connector.source.port.name»", targetPart="«connector.targetPart.name»", targetPort="«connector.target.port.name»")
			«ENDFOR»
			@Override
			public void setupConnections() {
				«FOR PortConnector connector : cc.portconnectors»
					// Connector: «connector.name»
					«IF connector.source.port.portType == PortType.IN»
						«connector.targetPart.name.toFirstLower».get«connector.target.port.name.toFirstUpper»().addObserver(«connector.sourcePart.name.toFirstLower».get«connector.source.port.name.toFirstUpper»());
					«ENDIF»
					«IF connector.source.port.portType == PortType.OUT»
						«connector.sourcePart.name.toFirstLower».get«connector.source.port.name.toFirstUpper»().addObserver(«connector.targetPart.name.toFirstLower».get«connector.target.port.name.toFirstUpper»());
					«ENDIF»
					«IF connector.source.port.portType == PortType.INOUT»
						«IF connector.target.port.portType == PortType.IN»
							«connector.sourcePart.name.toFirstLower».get«connector.source.port.name.toFirstUpper»().addObserver(«connector.targetPart.name.toFirstLower».get«connector.target.port.name.toFirstUpper»());
						«ENDIF»
						«IF connector.target.port.portType == PortType.OUT»
							«connector.targetPart.name.toFirstLower».get«connector.target.port.name.toFirstUpper»().addObserver(«connector.sourcePart.name.toFirstLower».get«connector.source.port.name.toFirstUpper»());
						«ENDIF»		
						«IF connector.target.port.portType == PortType.INOUT»
							«connector.targetPart.name.toFirstLower».get«connector.target.port.name.toFirstUpper»().addObserver(«connector.sourcePart.name.toFirstLower».get«connector.source.port.name.toFirstUpper»());
							«connector.sourcePart.name.toFirstLower».get«connector.source.port.name.toFirstUpper»().addObserver(«connector.targetPart.name.toFirstLower».get«connector.target.port.name.toFirstUpper»());
						«ENDIF»
					«ENDIF»		
				«ENDFOR»
			}
			«FOR PortMapping pm : cc.portMappings»
			@PortMapping(name="«pm.name»", sourcePort="«pm.source.name»", targetPart="«pm.targetPart.name»", targetPort="«pm.target.port.name»")
			«ENDFOR»
			@Override
			protected void handlePortMappings() {
				«FOR PortMapping pm : cc.portMappings»
					// Port Mapping: «pm.name»
					«/* source is port of composite component*/»
					«IF pm.source.portType == PortType.IN»
						«/* read from source and write to target */»
						String s_«pm.source.name» = «pm.source.name.toFirstLower».readData();
						
						if (s_«pm.source.name» != null) {
							this.«pm.targetPart.name.toFirstLower».get«pm.target.port.name.toFirstUpper»().sendDataToPort(s_«pm.source.name»);
						}
					«ENDIF»
					«IF pm.source.portType == PortType.OUT»
						«/* read from target and write to source */»
						String s_«pm.targetPart.name»_«pm.target.port.name» = this.«pm.targetPart.name.toFirstLower».get«pm.target.port.name.toFirstUpper»().receiveDataFromPort();
						
						if (s_«pm.targetPart.name»_«pm.target.port.name» != null) {
							this.«pm.source.name.toFirstLower».writeData(s_«pm.targetPart.name»_«pm.target.port.name»);
						}
					«ENDIF»
					«IF pm.source.portType == PortType.INOUT»
						«IF pm.target.port.portType == PortType.IN»
						String s_«pm.targetPart.name»_«pm.target.port.name» = this.«pm.targetPart.name.toFirstLower».get«pm.target.port.name.toFirstUpper»().receiveDataFromPort();
						
						if (s_«pm.targetPart.name»_«pm.target.port.name» != null) {
							this.«pm.source.name.toFirstLower».writeData(s_«pm.targetPart.name»_«pm.target.port.name»);
						}						
						«ENDIF»
						«IF pm.target.port.portType == PortType.OUT»
						String s_«pm.source.name» = «pm.source.name.toFirstLower».readData();
						
						if (s_«pm.source.name» != null) {
							this.«pm.targetPart.name.toFirstLower».get«pm.target.port.name.toFirstUpper»().sendDataToPort(s_«pm.source.name»);
						}						
						«ENDIF»
						«IF pm.target.port.portType == PortType.INOUT»
						String s_«pm.source.name» = «pm.source.name.toFirstLower».readData();
						
						if (s_«pm.source.name» != null) {
							this.«pm.targetPart.name.toFirstLower».get«pm.target.port.name.toFirstUpper»().sendDataToPort(s_«pm.source.name»);
						}
						
						String s_«pm.targetPart.name»_«pm.target.port.name» = this.«pm.targetPart.name.toFirstLower».get«pm.target.port.name.toFirstUpper»().receiveDataFromPort();
						
						if (s_«pm.targetPart.name»_«pm.target.port.name» != null) {
							this.«pm.source.name.toFirstLower».writeData(s_«pm.targetPart.name»_«pm.target.port.name»);
						}						
						«ENDIF»
					«ENDIF»
				«ENDFOR»
			}
			
			@Override
			protected void startChildComponents() {
				«FOR ComponentPart part : cc.componentParts»
					this.«part.name.toFirstLower».start();
				«ENDFOR»
			}
			
			@Override
			protected void stopChildComponents() {
				«FOR ComponentPart part : cc.componentParts»
					this.«part.name.toFirstLower».stopComponentThread();
				«ENDFOR»
			}
		}
		'''
	}
}