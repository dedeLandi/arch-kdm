/*******************************************************************************
 * Copyright (c) 2011 Mia-Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugues Dubourg (Mia-Software) - initial API and implementation
 *    Gabriel Barbier (Mia-Software) - initial API and implementation
 *    Nicolas Bros (Mia-Software) - Bug 335003 - [Discoverer] : Existing Discoverers Refactoring based on new framework
 *******************************************************************************/

package br.ufscar.arch_kdm.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.infra.common.core.logging.MoDiscoLogger;
import org.eclipse.m2m.atl.common.ATLLaunchConstants;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IInjector;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.core.service.CoreService;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import org.eclipse.modisco.kdm.uml2converter.internal.KdmToUml2Activator;
import org.eclipse.modisco.kdm.uml2converter.internal.Messages;
import org.eclipse.modisco.util.atl.core.internal.AtlLaunchHelper;
import org.eclipse.modisco.util.atl.core.internal.AtlLaunchHelper.ModelInfo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

@SuppressWarnings({ "deprecation", "restriction" })
public class KdmToUmlConverter {

	public static final String MM_LOCATION = "resources/metamodels"; //$NON-NLS-1$
	public static final String TRANSFO_LOCATION = "resources"; //$NON-NLS-1$
	public static final String KDM_MM_URI = "platform:/plugin/org.eclipse.gmt.modisco.omg.kdm/model/kdm.ecore";
	//public static final String UML_MM_URI = "http://www.eclipse.org/uml2/2.1.0/UML"; //$NON-NLS-1$
	public static final String UML_MM_URI = "platform:/plugin/org.eclipse.uml2.uml/model/UML.ecore"; //$NON-NLS-1$
	//private static final String ATL_MM_PATH = "http://www.eclipse.org/gmt/2005/ATL"; //$NON-NLS-1$

	public Resource[] getUML2ModelFromKDMModel(final Resource kdmModel, final boolean structureOnly)
			throws IOException, ATLCoreException {
		URL transformation;
		Resource[] result = null;
		if (structureOnly) {
			transformation = this.getClass().getResource("ArchKDM2UML_StructureView.asm"); //$NON-NLS-1$
			result = getUML2ModelFromKDMModelWithCustomTransformation(kdmModel, structureOnly,
					transformation);
		} else {
			transformation = this.getClass().getResource("ArchKDM2UML_CodeView.asm"); //$NON-NLS-1$
			result = getUML2ModelFromKDMModelWithCustomTransformation(kdmModel, structureOnly,
					transformation);
		}

		return result;
	}

	public Resource[] getUML2ModelFromKDMModelWithCustomTransformation(
			final Resource kdmSourceModel, final boolean generateTraces, final URL transformation)
			throws IOException, ATLCoreException {

		final List<ModelInfo> inputModels = new ArrayList<ModelInfo>();
		final List<ModelInfo> outputModels = new ArrayList<ModelInfo>();

		URI umlTargetModelUri = URI.createURI("memory:/umlTargetModel"); //$NON-NLS-1$

		final ModelInfo inputModel = new ModelInfo(
				"kdmInput", kdmSourceModel.getURI(), kdmSourceModel, "kdm", //$NON-NLS-1$ //$NON-NLS-2$
				URI.createURI(KdmToUmlConverter.KDM_MM_URI));
		inputModels.add(inputModel);
		final ModelInfo outputModel = new ModelInfo(
				"umlOutput", umlTargetModelUri, kdmSourceModel, "uml", //$NON-NLS-1$ //$NON-NLS-2$
				URI.createURI(KdmToUmlConverter.UML_MM_URI));
		outputModels.add(outputModel);

		if (generateTraces) {
			final URL mmwMMURL = this.getClass().getResource(
					KdmToUmlConverter.MM_LOCATION + "/mmw_traceability.ecore"); //$NON-NLS-1$
			final ModelInfo traceModel = new ModelInfo("trace", umlTargetModelUri //$NON-NLS-1$
					.trimFileExtension().appendFileExtension("trace.amw"), null, "Trace", //$NON-NLS-1$ //$NON-NLS-2$
					URI.createURI(mmwMMURL.toString()));
			outputModels.add(traceModel);
		}

		AtlLaunchHelper atlHelper = new AtlLaunchHelper();
		List<Resource> results = atlHelper.runTransformation(transformation, inputModels,
				outputModels);

		Resource[] resultsArray = new Resource[results.size()];
		results.toArray(resultsArray);
		return resultsArray;
	}

	public final void basicExportKdmToUmlTransformation(final IFile file,
			final IProgressMonitor monitor) throws CoreException {
		final InputStream transfoFileStream = this.getClass().getResourceAsStream(
				KdmToUmlConverter.TRANSFO_LOCATION + "/ArchKDM2UML_CodeView.atl"); //$NON-NLS-1$
		if (file.exists()) {
			file.setContents(transfoFileStream, IResource.FORCE, monitor);
		} else {
			file.create(transfoFileStream, IResource.FORCE, monitor);
		}
	}

	public void exportKdmToUmlTransformation(final IPath pathParameter) {
		IPath path = pathParameter;
		if (path.getFileExtension() == null || !path.getFileExtension().equals("atl")) { //$NON-NLS-1$
			path = path.addFileExtension("atl"); //$NON-NLS-1$
		}
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		final IProject destinationProject = file.getProject();
		Job job = new Job(Messages.KDMtoUML2Converter_27) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					basicExportKdmToUmlTransformation(file, monitor);
				} catch (CoreException e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
		// when copy job is done, open the file
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							destinationProject.refreshLocal(IResource.DEPTH_INFINITE,
									new NullProgressMonitor());
							IWorkbenchPage page = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage();
							IDE.openEditor(page, file);
						} catch (Exception e) {
							MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
						}
					}
				});
			}
		});
		job.schedule();
	}

	public IFile instrumentAtlTranformationWithTraceability(final InputStream sourceTransformation,
			final URI atlFileUri) throws ATLCoreException {
		URI sourceModelUri = atlFileUri.trimFileExtension()
				.appendFragment("-ATL_source").appendFileExtension("ecore"); //$NON-NLS-1$ //$NON-NLS-2$
		URI targetModelUri = atlFileUri.trimFileExtension()
				.appendFragment("-ATL_target").appendFileExtension("ecore"); //$NON-NLS-1$ //$NON-NLS-2$

		// transforms ATL file into ATL model
		IModel sourceModel = AtlParser.getDefault().parseToModel(sourceTransformation);
		if (sourceModel instanceof EMFModel) {
			EMFModel ecoreSourceModel = (EMFModel) sourceModel;
			try {
				ecoreSourceModel.getResource().setURI(sourceModelUri);
				ecoreSourceModel.getResource().save(null);
			} catch (IOException e) {
				MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
			}
		}

		IModel targetModel = addTraceability(sourceModel, sourceModelUri, targetModelUri);

		if (targetModel instanceof EMFModel) {
			EMFModel ecoreTargetModel = (EMFModel) targetModel;
			try {
				ecoreTargetModel.getResource().setURI(targetModelUri);
				ecoreTargetModel.getResource().save(null);
			} catch (IOException e) {
				MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
			}

		}
		IFile result = null;
		// transforms ATL model into ATL file
		try {
			result = parseFromModel(targetModel, atlFileUri);
		} catch (IOException e) {
			MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
		}

		return result;
	}

	public final IModel addTraceability(final IModel sourceModel, final URI sourceModelUri,
			final URI targetModelUri) throws ATLCoreException {
		final URL transformation = this.getClass().getResource(
				KdmToUmlConverter.TRANSFO_LOCATION + "/ATL2WTracer.asm"); //$NON-NLS-1$

		Map<String, String> modelHandlers = new HashMap<String, String>();
		String atlMetaModelName = "ATL"; //$NON-NLS-1$
		modelHandlers.put(atlMetaModelName, "EMF"); //$NON-NLS-1$

		final Map<String, Object> options = new HashMap<String, Object>();
		options.put(ATLLaunchConstants.OPTION_MODEL_HANDLER, modelHandlers);
		options.put(ATLLaunchConstants.IS_REFINING, true);

		String launcherName = ATLLaunchConstants.EMF_VM_NAME;
		final ILauncher launcher = CoreService.getLauncher(launcherName);
		launcher.initialize(options);

		ModelFactory factory = CoreService.getModelFactory(launcher.getDefaultModelFactoryName());

		IInjector injector = CoreService.getInjector(factory.getDefaultInjectorName());
		IExtractor extractor = CoreService.getExtractor(factory.getDefaultExtractorName());

		// load meta model ATL
		IReferenceModel atlMM = factory.getBuiltInResource("ATL.ecore"); //$NON-NLS-1$
		// load model source
		Map<String, Object> modelOptions = new HashMap<String, Object>();
		String inModelName = "IN"; //$NON-NLS-1$
		modelOptions.put("modelName", inModelName); //$NON-NLS-1$
		modelOptions.put("path", sourceModelUri.toString()); //$NON-NLS-1$
		modelOptions.put("newModel", false); //$NON-NLS-1$
		IModel input = factory.newModel(atlMM, modelOptions);
		injector.inject(input, sourceModelUri.toString());
		launcher.addInModel(input, inModelName, atlMetaModelName);
		// load model cible
		modelOptions = new HashMap<String, Object>();
		inModelName = "OUT"; //$NON-NLS-1$
		modelOptions.put("modelName", inModelName); //$NON-NLS-1$
		modelOptions.put("path", targetModelUri.toString()); //$NON-NLS-1$
		modelOptions.put("newModel", true); //$NON-NLS-1$
		IModel outputInstance = factory.newModel(atlMM, modelOptions);
		launcher.addOutModel(outputInstance, inModelName, atlMetaModelName);

		/*
		 * Encapsulate ATL transformation into a new Thread to reset the Stack (avoid stack
		 * overflow)
		 */
		options.put("continueAfterError", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		options.put("printExecutionTime", "true"); //$NON-NLS-1$ //$NON-NLS-2$

		Job transformationThread = new Job(Messages.KDMtoUML2Converter_42) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				IStatus result = Status.OK_STATUS;
				try {
					launcher.launch(ILauncher.RUN_MODE, monitor, options,
							transformation.openStream());
				} catch (IOException e) {
					result = Status.CANCEL_STATUS;
					MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
				}

				return result;
			}
		};
		transformationThread.schedule();
		try {
			transformationThread.join();
		} catch (InterruptedException e) {
			MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
		}

		extractor.extract(outputInstance, targetModelUri.toString());

		return outputInstance;
	}

	public static final IFile parseFromModel(final IModel transformationModel, final URI atlFileUri)
			throws IOException {
		IFile file = null;
		IPath atlFilePath = null;
		if (atlFileUri.isPlatformResource()) {
			atlFilePath = new Path(atlFileUri.toPlatformString(false));
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(atlFilePath);
		} else {
			atlFilePath = new Path(atlFileUri.toFileString());
			file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(atlFilePath);
		}
		if (file != null) {
			try {
				AtlParser.getDefault().extract(transformationModel, file.getLocation().toString());
			} catch (Exception e) {
				MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
			}
			try {
				file.refreshLocal(0, new NullProgressMonitor());
			} catch (CoreException e) {
				MoDiscoLogger.logError(e, KdmToUml2Activator.getDefault());
			}
		} else {
			MoDiscoLogger.logError("Uri invalid", KdmToUml2Activator.getDefault()); //$NON-NLS-1$
		}
		return file;
	}
}
