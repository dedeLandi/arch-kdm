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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.modisco.util.atl.core.internal.AtlLaunchHelper;
import org.eclipse.modisco.util.atl.core.internal.AtlLaunchHelper.ModelInfo;

@SuppressWarnings({ "restriction" })
public class KdmToUmlConverter {
	public static final String KDM_MM_URI = "platform:/plugin/org.eclipse.gmt.modisco.omg.kdm/model/kdm.ecore";
	//public static final String UML_MM_URI = "http://www.eclipse.org/uml2/2.1.0/UML"; //$NON-NLS-1$
	public static final String UML_MM_URI = "platform:/plugin/org.eclipse.uml2.uml/model/UML.ecore"; //$NON-NLS-1$
	//private static final String ATL_MM_PATH = "http://www.eclipse.org/gmt/2005/ATL"; //$NON-NLS-1$

	public Resource[] getUML2ModelFromKDMModel(final Resource kdmModel, final boolean structureOnly)
			throws IOException, ATLCoreException {
		URL transformation;
		Resource[] result = null;
		if (structureOnly) {
			transformation = this.getClass().getResource("/resources/ArchKDM2UML_StructureView.asm"); //$NON-NLS-1$
			result = getUML2ModelFromKDMModelWithCustomTransformation(kdmModel, structureOnly,
					transformation);
		} else {
			transformation = this.getClass().getResource("/resources/ArchKDM2UML_CodeView.asm"); //$NON-NLS-1$
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

		AtlLaunchHelper atlHelper = new AtlLaunchHelper();
		List<Resource> results = atlHelper.runTransformation(transformation, inputModels,
				outputModels);

		Resource[] resultsArray = new Resource[results.size()];
		results.toArray(resultsArray);
		return resultsArray;
	}
}
