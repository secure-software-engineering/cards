package de.uni_paderborn.swt.cards.dsl.ui.wizards;

import java.io.File;
import java.util.Arrays;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ExportPage extends WizardPage {
	private Composite container;	
	protected File[] selectedFiles;
	protected String outputDirectory;

	public ExportPage() {
		super("Export Page");
		setTitle("Export Page");
		setDescription("Select model files for which you want to generate reports.");
	}

	@Override
    public void createControl(Composite parent) {
		setPageComplete(false);
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        
        
        CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(container);
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.setContentProvider(new WorkbenchContentProvider());
        treeViewer.setLabelProvider(new WorkbenchLabelProvider());
        treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
        treeViewer.setFilters(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				System.out.println(element.getClass());
				if (element instanceof org.eclipse.core.internal.resources.File) {
					System.out.println(((org.eclipse.core.internal.resources.File) element).getName());
					String fileName = ((org.eclipse.core.internal.resources.File) element).getName();
					int lastIndexOf = fileName.lastIndexOf(".");
					if (lastIndexOf == -1 )
						return false;
					String ext =  fileName.substring(lastIndexOf);
					if (ext.equals(".tmdsl"))
						return true;
					
				}
				if (element instanceof Project) {
					return true;
				}
				
				return false;
			}
        	
        });
        
        Label l = new Label(container, SWT.NONE);
        l.setText("Output Directory");
        
        GridLayout gd = new GridLayout(1, false);
        Composite comp = new Composite(container, SWT.NONE);
        comp.setLayout(gd);
        Text t = new Text(comp, SWT.BORDER);
        t.setEditable(false);
        t.setText("");
        Button b = new Button(comp, SWT.NONE);
        b.setText("Choose Directory");
        b.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
            	if (e.type == SWT.Selection) {
            		Shell shell = new Shell();
            		DirectoryDialog d = new DirectoryDialog(shell);
            		
            		t.setText(d.open());
            		outputDirectory = t.getText();
            		setPageComplete(selectedFiles.length > 0 && t.getText().length() > 0);
            		container.layout();
            		}
            	}
            }
          );
        
        treeViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				
				treeViewer.setSubtreeChecked(event.getElement(), event.getChecked());
				
				
				 selectedFiles = (File[]) Arrays.stream(treeViewer.getCheckedElements())
						.filter(o -> o instanceof org.eclipse.core.internal.resources.File)
						.map(f -> (org.eclipse.core.internal.resources.File) f)
						.map(f -> f.getRawLocation().toFile())
						.toArray(File[]::new);
				
				setPageComplete(selectedFiles.length > 0 && t.getText().length() > 0);
			}
		});
        

        
        
        // required to avoid an error in the system
        setControl(container);
    }
}
