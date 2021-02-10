package de.uni_paderborn.swt.cards.design;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import de.uni_paderborn.swt.cards.dsl.tmdsl.CompositeComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.analysisGen.AnalysisGenerator;

public class ExternalJavaActionAnalysisGeneration implements IExternalJavaAction {
	
	@Override
	public boolean canExecute(Collection<? extends EObject> arg0) {
		return true;
	}
	
	@Override
	public void execute(Collection<? extends EObject> arg0, Map<String, Object> arg1) {		
		EObject obj = (EObject) arg0.toArray()[0];
		CompositeComponent cc = (CompositeComponent) obj;

		// get system of opened model
		de.uni_paderborn.swt.cards.dsl.tmdsl.System sys = (de.uni_paderborn.swt.cards.dsl.tmdsl.System) ancestors(cc).stream().filter(e -> e instanceof de.uni_paderborn.swt.cards.dsl.tmdsl.System).findFirst().get();
		Model model = (Model) ancestors(cc).stream().filter(e -> e instanceof Model).findFirst().get();
		
		// set project name
		String currentProjectName = getCurrentProjectName();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String projectName = currentProjectName + "." + sys.getName() + "." + cc.getName();
		IProject project = root.getProject(projectName);
		String packageName = projectName;
		packageName += ".analyses";
		if (project.exists()) {
			
			IJavaProject targetProject = JavaCore.create(project);
			
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String workspacePath = workspace.getRoot().getLocation().toFile().toString();
			String srcFolderPath = workspacePath + File.separator + project.getName() + File.separator + "src" + File.separator;
			String srcGenFolderPath = workspacePath + File.separator + project.getName() + File.separator + "src-gen" + File.separator;
			
			try {
				List<IJavaProject> projects = getJavaProjects();
				
				Resource mappingModelResource = Services.getMappingModelResource(cc);
				
				de.uni_paderborn.swt.cards.mappingModel.Model mappingModel = ((de.uni_paderborn.swt.cards.mappingModel.Model) mappingModelResource.getContents().stream().filter(e -> e instanceof de.uni_paderborn.swt.cards.mappingModel.Model).findFirst().get());
				
				AnalysisGenerator.generate(srcFolderPath, srcGenFolderPath, packageName, model, mappingModel, model.getAssumption());	
				
				//refresh workspace to show newly generated project
				try {
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		} else {
			throw new UnsupportedOperationException("Currently, analyses can only be generated for already (in the workspace!) existing projects");
		}
	}
	
	/**
	 * Returns the name of the currently opened project of the eclipse instance.
	 * @return project name
	 */
	public static String getCurrentProjectName() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
	            .getActiveWorkbenchWindow().getActivePage();

		IEditorPart activeEditor = activePage.getActiveEditor();
		IProject currentProject = null;
		
		if (activeEditor != null) {
		   IEditorInput input = activeEditor.getEditorInput();

		   currentProject = input.getAdapter(IProject.class);
		   if (currentProject == null) {
		      IResource resource = input.getAdapter(IResource.class);
		      if (resource != null) {
		    	  currentProject = resource.getProject();
		      }
		   }
		}
		
		return currentProject.getName();
	}
	
	public static List<IJavaProject> getJavaProjects() {
	      List<IJavaProject> projectList = new LinkedList<IJavaProject>();
	      try {
	         IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	         IProject[] projects = workspaceRoot.getProjects();
	         for(int i = 0; i < projects.length; i++) {
	            IProject project = projects[i];
	            if(project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
	               projectList.add(JavaCore.create(project));
	            }
	         }
	      }
	      catch(CoreException ce) {
	         ce.printStackTrace();
	      }
	      return projectList;
	   }
	
	/**
	 * Return a list of all ancestors of an EObject, where the first element is the parent and the last element is the root object.
	 * @param obj
	 * @return ancestor list
	 */
	private static List<EObject> ancestors(EObject obj) {
		List<EObject> ancestors = new ArrayList<EObject>();
		while (obj != null) {
			ancestors.add(obj);
			obj = obj.eContainer();
		}
		return ancestors;
	}
	
}
