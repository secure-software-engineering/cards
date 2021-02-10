package de.uni_paderborn.swt.cards.codeGen

import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType
import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption

class AtomicComponentGenerator {
	def generate(String packageName, String classesPackage, AtomicComponent ac, Assumption assumption) {
		'''
		package «packageName»;
		
		«IF ac.ports.stream.anyMatch(p | p.portType == PortType.INOUT)»
			import «classesPackage».GenericINOUTPort;
		«ENDIF»
		«IF ac.ports.stream.anyMatch(p | p.portType == PortType.IN)»
			import «classesPackage».GenericINPort;
		«ENDIF»
		«IF ac.ports.stream.anyMatch(p | p.portType == PortType.OUT)»
			import «classesPackage».GenericOUTPort;
		«ENDIF»
		import «classesPackage».AbstractAtomicComponent;
		import de.uni_paderborn.swt.cards.codeGen.library.annotations.*;
		
		@Component("«ac.name»")
		public abstract class Abstract«ac.name.toFirstUpper»_AC extends AbstractAtomicComponent {
			«FOR Port port : ac.ports»
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
			
			«FOR Port port : ac.ports»
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
			
			«FOR DataType dt : ac.source4Data»
					@Source4Datatype("«dt.name»")
					protected abstract String generate«dt.name.toFirstUpper»();			
			«ENDFOR»
		
			@Override
			protected void initializePorts() {
				«FOR Port port : ac.ports»
					«IF (port.portType == PortType.IN)»
						this.«port.name.toFirstLower» = new GenericINPort("«port.name»");
					«ENDIF»
					«IF (port.portType == PortType.OUT)»
						this.«port.name.toFirstLower» = new GenericOUTPort("«port.name»");
					«ENDIF»
					«IF (port.portType == PortType.INOUT)»
						this.«port.name.toFirstLower» = new GenericINOUTPort("«port.name»");
					«ENDIF»
				«ENDFOR»				
			}
			
			«FOR Port port : ac.ports»
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
		}
		'''
	}
}