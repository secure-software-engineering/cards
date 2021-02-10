package de.uni_paderborn.swt.cards.design;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;

public class ExternalJavaActionChooseMappingModel implements IExternalJavaAction {

	@Override
	public boolean canExecute(Collection<? extends EObject> arg0) {
		return true;
	}
	
	/**
	 * Method to disable navigation in a open dialog, to restrict selection to files inside the directory.
	 * @param c dialog
	 */
	private void disableNav(Container c) {
		for (Component x : c.getComponents())
		  if (x instanceof JComboBox)
		    ((JComboBox)x).setEnabled(false);
		  else if (x instanceof JButton) {
		    String text = ((JButton)x).getText();
		    if (text == null || text.isEmpty())
		      ((JButton)x).setEnabled(false);
		    }
		  else if (x instanceof Container)
		    disableNav((Container)x);
		  }
	
	@Override
	public void execute(Collection<? extends EObject> arg0, Map<String, Object> arg1) {
		// object on which this external java action is called on
		EObject obj = (EObject) arg0.toArray()[0];
		
		// get location of model file of parameter EObject
		String file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(obj.eResource().getURI().toPlatformString(true))).getLocation().toString();
		
		// get directory of model file
		int fileNameIndex = file.lastIndexOf("/");
		String dir = file.substring(0, fileNameIndex) + "/";

		// open file chooser
		JFileChooser fc = new JFileChooser(dir);
		disableNav(fc);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Mapping model", "mappingmodel");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);

	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	fc.getSelectedFile().getName();
	    	String relativePath = fc.getSelectedFile().getAbsolutePath().replace(File.separator, "/").replace(dir, "");
	    	
	    	// get system parameter
			de.uni_paderborn.swt.cards.dsl.tmdsl.System system = (de.uni_paderborn.swt.cards.dsl.tmdsl.System) arg1.get("system");
			
			// set mapping model path
	    	system.setPath2MappingModel("\"" + relativePath + "\"");
	    }
	}
}
