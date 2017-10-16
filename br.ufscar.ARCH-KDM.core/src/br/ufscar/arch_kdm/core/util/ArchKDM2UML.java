package br.ufscar.arch_kdm.core.util;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.infra.common.core.internal.utils.ModelUtils;
import org.eclipse.m2m.atl.core.ATLCoreException;;

@SuppressWarnings("restriction")
public class ArchKDM2UML {
	private String inputFile;
	private String outputFile;
	private boolean structureOnly;

	private Resource basicConvertKDMModel(final URI sourceUri)
			throws IOException, ATLCoreException {
		final Resource sourceModel = ModelUtils.loadModel(sourceUri);

		final KdmToUmlConverter converter = new KdmToUmlConverter();

		Resource[] out = converter.getUML2ModelFromKDMModel(sourceModel, this.structureOnly);

		return out[0];
	}

	public ArchKDM2UML(String inputFile, String outputFile, boolean structureOnly) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.structureOnly = structureOnly;
	}
	
	public void run() {
		final URI sourceUri = URI.createFileURI(this.inputFile);

		Resource output = null;
		
		try {
			output = basicConvertKDMModel(sourceUri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ATLCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		output.setURI(URI.createFileURI(this.outputFile));
		try {
			output.save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
