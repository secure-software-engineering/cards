package de.uni_paderborn.swt.cards.codeGen.classes

class GenericOUTPortGenerator {
	static def generate(String classesPackage) {
		'''
		package «classesPackage»;
		
		import java.util.ArrayList;
		import java.util.List;
		import java.util.Observable;
		import java.util.Observer;
		
		public class GenericOUTPort extends Observable implements Observer  {
			String portName;
			
			private List<String> INportBuffer = new ArrayList<String>();
			private List<String> OUTportBuffer = new ArrayList<String>();
			
			public GenericOUTPort(String name) {
				this.portName = name;
			}
		
			/**
			 * This method is used to write data to the output buffer of the Port. This method should only be accessed from within the component.
			 * @param data - string to add to the buffer
			 */
			public void writeData(String data) {
				// optionally use a sanitizer here
				this.OUTportBuffer.add(data);
				
				// data is now on port, can be read by remote 
				this.setChanged();
				this.notifyObservers();
			}
		
			/**
			 * This method is used to read data from the input buffer of the Port. This method should only be accessed from within the component.
			 * Even though this is an OUTPort, we still need this for port mappings.
			 * @return data - first string of the buffer
			 */
			public String readData() {
				// only to be called from inside the component
				
				if (this.INportBuffer.size() > 0) {
					String data = this.INportBuffer.get(0);
					this.INportBuffer.remove(0);
					return data;
				}
				
				return null;
			}
		
			/**
			 * Specifies the behavior of the port when an observed remote port publishes new data. 
			 * To keep the code clean, this method should only receive data from the remote port and copy it to its own input buffer.
			 */
			@Override
			public void update(Observable o, Object arg) {		
				if (o instanceof GenericOUTPort) {
					GenericOUTPort port = (GenericOUTPort) o;
					String data = port.receiveDataFromPort();
					this.INportBuffer.add(data);
				} else if  (o instanceof GenericINPort) {
					GenericINPort port = (GenericINPort) o;
					String data = port.receiveDataFromPort();	
					this.INportBuffer.add(data);
				} else if  (o instanceof GenericINOUTPort) {
					GenericINOUTPort port = (GenericINOUTPort) o;
					String data = port.receiveDataFromPort();	
					this.INportBuffer.add(data);
				}
			}
		
			/**
			 * Method to read data from the output buffer of this port, to be used by remote ports.
			 * @return data - first String of the buffer
			 */
			public String receiveDataFromPort() {
				
				if (this.OUTportBuffer.size() > 0) {
					String data = this.OUTportBuffer.get(0);
					this.OUTportBuffer.remove(0);
					return data;
				}
				return null;
			}
			
			/**
			 * Method to write data to the input buffer of this port, to be used by remote ports.
			 * Even though this is a OUTPort, we still need this for port mappings, i.e. the OUTPort of a componentPart needs to read the buffer of another OUTPort, if they are mapped.
			 * @param data - String to add to the buffer
			 */
			public void sendDataToPort(String data) {
				this.INportBuffer.add(data);
			}
		
		}
		'''
	}
}