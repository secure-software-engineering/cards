package de.uni_paderborn.swt.cards.design;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.tools.api.ui.IExternalJavaAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart;
import de.uni_paderborn.swt.cards.dsl.tmdsl.CompositeComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType;
import de.uni_paderborn.swt.cards.codeGen.CodeGenerator;
import de.uni_paderborn.swt.cards.mappingModel.ComponentMapping;
import de.uni_paderborn.swt.cards.mappingModel.InPortMapping;
import de.uni_paderborn.swt.cards.mappingModel.Mapping;
import de.uni_paderborn.swt.cards.mappingModel.MappingModelHelper;
import de.uni_paderborn.swt.cards.mappingModel.OutPortMapping;
import de.uni_paderborn.swt.cards.mappingModel.SourceMapping;
import de.uni_paderborn.swt.cards.mappingModel.SinkMapping;

public class ExternalJavaActionCodeGeneration implements IExternalJavaAction {

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
		
		if (project.exists()) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String workspacePath = workspace.getRoot().getLocation().toFile().toString();
			String srcFolderPath = workspacePath + File.separator + project.getName() + File.separator + "src" + File.separator;
			String srcGenFolderPath = workspacePath + File.separator + project.getName() + File.separator + "src-gen" + File.separator;
			
			//fill mappings with values based on code generation
			fillMappingModel(srcFolderPath, packageName + ".impl", cc);	
			
			CodeGenerator.generate(srcFolderPath, srcGenFolderPath, packageName, cc, model.getAssumption());	
			
			//refresh workspace to show newly generated project
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} else {
			try {
				project.create(null);
				project.open(null);
				
				//set the Java nature
				IProjectDescription description = project.getDescription();
				description.setNatureIds(new String[] { JavaCore.NATURE_ID });
	
				//create the project
				project.setDescription(description, null);
				IJavaProject javaProject = JavaCore.create(project);
	
				//set the build path
				IClasspathEntry[] buildPath = {
			        JavaCore.newSourceEntry(project.getFullPath().append("src")),
			        JavaCore.newSourceEntry(project.getFullPath().append("src-gen"))
				};
				IClasspathEntry[] jre = PreferenceConstants.getDefaultJRELibrary();
				IClasspathEntry[] buildPaths = new IClasspathEntry[buildPath.length + jre.length];
				
				for (int i = 0; i < buildPath.length; i++) {
					buildPaths[i] = buildPath[i];
				}
				
				for (int i = 0; i < jre.length; i++) {
					buildPaths[buildPath.length + i] = jre[i];
				}
				
				javaProject.setRawClasspath(buildPaths, project.getFullPath().append(
				                "bin"), null);
				
	
				//create folder by using resources package
				IFolder sourcefolder = project.getFolder("src");
				sourcefolder.create(true, true, null);
				IFolder sourcegenfolder = project.getFolder("src-gen");
				sourcegenfolder.create(true, true, null);
	
				//Add folder to Java element
				IPackageFragmentRoot srcFolder = javaProject
				                .getPackageFragmentRoot(sourcefolder);
				//Add folder to Java element
				IPackageFragmentRoot srcGenFolder = javaProject
				                .getPackageFragmentRoot(sourcegenfolder);
				
				// generate code
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				String workspacePath = workspace.getRoot().getLocation().toFile().toString();
				String srcFolderPath = workspacePath + File.separator + project.getName() + File.separator + "src" + File.separator;
				String srcGenFolderPath = workspacePath + File.separator + project.getName() + File.separator + "src-gen" + File.separator;
				
				//fill mappings with values based on code generation
				fillMappingModel(srcFolderPath, packageName + ".impl", cc);
				
				CodeGenerator.generate(srcFolderPath, srcGenFolderPath, packageName, cc, model.getAssumption());	
				
				// copy library jar and edit build path
				IFolder libFolder = project.getFolder("lib");
				libFolder.create(true, true, null);
				
				Bundle bundle = Platform.getBundle( "de.uni_paderborn.swt.cards.codeGen" );
				InputStream stream = FileLocator.openStream( bundle, new Path("lib"+ File.separator + "tmdsl_annotations.jar"), false );
				IFile file = project.getFile( "lib" + File.separator +  "tmdsl_annotations.jar" );
				file.create( stream, true, null );
				addProjectLibrary(javaProject, new File(file.getLocation().toString()));
				
				//refresh workspace to show newly generated project
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				

				
			} catch (CoreException | IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Fill all mappings of the Composite Component Subtree with paths based on our code generation
	 * @param location location of the project
	 * @param packageName packageName of this composite Component
	 * @param cc CompositeComponent
	 */
	private void fillMappingModel(String location, String packageName, CompositeComponent cc) {
		Resource mappingModelResource = Services.getMappingModelResource(cc);
		
		de.uni_paderborn.swt.cards.mappingModel.Model m = ((de.uni_paderborn.swt.cards.mappingModel.Model) mappingModelResource.getContents().stream().filter(e -> e instanceof de.uni_paderborn.swt.cards.mappingModel.Model).findFirst().get());
		Model tmdslModel = (Model) Services.ancestors(cc).stream().filter(e -> e instanceof Model).findFirst().get();
		TransactionalEditingDomain domain = SessionManager.INSTANCE.getSession(m).getTransactionalEditingDomain();
		// clean up model
		List<Mapping> toDelete = MappingModelHelper.cleanMappingModel(m, tmdslModel);
		Services.removeFromResourceUsingTransaction(m, toDelete);
		
		System.out.println(packageName + ","+ location + ","+ cc);
		
		String packagePath = Arrays.stream(packageName.split("\\.")).reduce((a,b) -> a + File.separator + b).get();
		String dirPath = location + File.separator + packagePath + File.separator;
		String classesDirPath = dirPath + "classes" + File.separator;
		

		List<EObject> alreadyMapped = new ArrayList<EObject>();
		List<EObject> toMap = new ArrayList<EObject>();
		List<Mapping> mappings = m.getMappings();
		toMap.add(cc);
		
		while(toMap.size() > 0) {
			EObject o = toMap.get(0);
			toMap.remove(0);
			alreadyMapped.add(o);
			
			// add missing (empty) mappings to model
			List<Mapping> toAdd = MappingModelHelper.expandMappingModel(m, o);
			Services.addToResourceUsingTransaction(m, toAdd);
			
			if (o instanceof CompositeComponent) {
				CompositeComponent c = (CompositeComponent) o;

				String fileName = capitalize(c.getName()) + "_CC";
				ComponentMapping mapping = mappings.stream().filter(e -> e instanceof ComponentMapping).map(e -> (ComponentMapping) e).filter(e -> e.getComponent().equals(c)).findFirst().get();
				
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						mapping.setTargetClassName(packageName+ "." + fileName);
						mapping.setTargetMethodName("processComponent");
					}
				});
				
				SinkMapping sinkMapping = mappings.stream().filter(e -> e instanceof SinkMapping).map(e -> (SinkMapping) e).filter(e -> e.getComponent().equals(o)).findFirst().get();
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						sinkMapping.setTargetClassName(packageName+ "." + fileName);
						sinkMapping.setTargetMethodName("processComponent");
					}
				});
				
				for(Port p : c.getPorts()) {
					toAdd = MappingModelHelper.expandMappingModel(m, p);
					Services.addToResourceUsingTransaction(m, toAdd);
					if (p.getPortType() == PortType.IN) {
						InPortMapping portMapping = mappings.stream().filter(e -> e instanceof InPortMapping).map(e -> (InPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								portMapping.setTargetClassName(packageName+ "." + fileName);
								portMapping.setTargetMethodName("readData" + capitalize(p.getName()));
							}
						});

					}
					if (p.getPortType() == PortType.OUT) {
						OutPortMapping portMapping = mappings.stream().filter(e -> e instanceof OutPortMapping).map(e -> (OutPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								portMapping.setTargetClassName(packageName+ "." + fileName);
								portMapping.setTargetMethodName("writeData" + capitalize(p.getName()));
							}
						});

					}
					if (p.getPortType() == PortType.INOUT) {
						OutPortMapping outPortMapping = mappings.stream().filter(e -> e instanceof OutPortMapping).map(e -> (OutPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								outPortMapping.setTargetClassName(packageName+ "." + fileName);
								outPortMapping.setTargetMethodName("writeData" + capitalize(p.getName()));
							}
						});

						
						InPortMapping inPortMapping = mappings.stream().filter(e -> e instanceof InPortMapping).map(e -> (InPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								inPortMapping.setTargetClassName(packageName+ "." + fileName);
								inPortMapping.setTargetMethodName("readData" + capitalize(p.getName()));
							}
						});

					}
				}

				for (ComponentPart cp : c.getComponentParts()) {
					if (!alreadyMapped.contains(cp.getComponent())) {
						toMap.add(cp.getComponent());
					}
				}
			} else if (o instanceof AtomicComponent) {
				AtomicComponent c = (AtomicComponent) o;
				String fileName = capitalize(c.getName()) + "_AC";		
				
				ComponentMapping mapping = mappings.stream().filter(e -> e instanceof ComponentMapping).map(e -> (ComponentMapping) e).filter(e -> e.getComponent().equals(c)).findFirst().get();
				
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						mapping.setTargetClassName(packageName+ "."+fileName);
						mapping.setTargetMethodName("doSomething");
					}
				});

				
				SinkMapping sinkMapping = mappings.stream().filter(e -> e instanceof SinkMapping).map(e -> (SinkMapping) e).filter(e -> e.getComponent().equals(o)).findFirst().get();
				
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						sinkMapping.setTargetClassName(packageName+ "."+fileName);
						sinkMapping.setTargetMethodName("doSomething");
					}
				});

				
				for(Port p : c.getPorts()) {
					toAdd = MappingModelHelper.expandMappingModel(m, p);
					Services.addToResourceUsingTransaction(m, toAdd);
					if (p.getPortType() == PortType.IN) {
						InPortMapping portMapping = mappings.stream().filter(e -> e instanceof InPortMapping).map(e -> (InPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								portMapping.setTargetClassName(packageName+ "."+fileName);
								portMapping.setTargetMethodName("readData"+ capitalize(p.getName()));
							}
						});

					}
					if (p.getPortType() == PortType.OUT) {
						OutPortMapping portMapping = mappings.stream().filter(e -> e instanceof OutPortMapping).map(e -> (OutPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								portMapping.setTargetClassName(packageName+ "."+fileName);
								portMapping.setTargetMethodName("writeData"+ capitalize(p.getName()));
							}
						});

					}
					if (p.getPortType() == PortType.INOUT) {
						OutPortMapping outPortMapping = mappings.stream().filter(e -> e instanceof OutPortMapping).map(e -> (OutPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								outPortMapping.setTargetClassName(packageName+ "."+fileName);
								outPortMapping.setTargetMethodName("writeData"+ capitalize(p.getName()));
							}
						});


						InPortMapping inPortMapping = mappings.stream().filter(e -> e instanceof InPortMapping).map(e -> (InPortMapping) e).filter(e -> e.getPort().equals(p)).findFirst().get();
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								inPortMapping.setTargetClassName(packageName+ "."+fileName);
								inPortMapping.setTargetMethodName("readData"+ capitalize(p.getName()));
							}
						});
					}
				}
				
				for(DataType d : c.getSource4Data()) {
					SourceMapping sourceMapping = mappings.stream().filter(e -> e instanceof SourceMapping).map(e -> (SourceMapping) e).filter(e -> e.getDataType().equals(d)).findFirst().get();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							sourceMapping.setTargetClassName(packageName+ "."+fileName);
							sourceMapping.setTargetMethodName("generate"+capitalize(d.getName()));
						}
					});
					

				}
			}		
		}				
	}
	
	/**
	 * Capitalize a string
	 * @param str
	 * @return capitalized string
	 */
	public static String capitalize(String str) {
	    if(str == null || str.isEmpty()) {
	        return str;
	    }

	    return str.substring(0, 1).toUpperCase() + str.substring(1);
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
	
	private static void addProjectLibrary(IJavaProject jProject, File jarLibrary) throws IOException, URISyntaxException, MalformedURLException, CoreException {
	    // copy the jar file into the project
	    InputStream jarLibraryInputStream = new BufferedInputStream(new FileInputStream(jarLibrary));
	    IFile libFile = jProject.getProject().getFile(jarLibrary.getName());
	    libFile.create(jarLibraryInputStream, false, null);

	    // create a classpath entry for the library
	    IClasspathEntry relativeLibraryEntry = new org.eclipse.jdt.internal.core.ClasspathEntry(
	        IPackageFragmentRoot.K_BINARY,
	        IClasspathEntry.CPE_LIBRARY, libFile.getLocation(),
	        ClasspathEntry.INCLUDE_ALL, // inclusion patterns
	        ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
	        null, null, null, // specific output folder
	        false, // exported
	        ClasspathEntry.NO_ACCESS_RULES, false, // no access rules to combine
	        ClasspathEntry.NO_EXTRA_ATTRIBUTES);

	    // add the new classpath entry to the project's existing entries
	    IClasspathEntry[] oldEntries = jProject.getRawClasspath();
	    IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
	    System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
	    newEntries[oldEntries.length] = relativeLibraryEntry;
	    jProject.setRawClasspath(newEntries, null);
	}
	
}
