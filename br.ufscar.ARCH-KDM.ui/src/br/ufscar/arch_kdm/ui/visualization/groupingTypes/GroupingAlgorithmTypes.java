/**
 * @author Andr�
 * 
 */
package br.ufscar.arch_kdm.ui.visualization.groupingTypes;

import java.util.ArrayList;
import java.util.List;

import br.ufscar.arch_kdm.core.util.ArchKDM2UML;
import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.arch_kdm.core.visualization.IVisualizeDriftsAlgo;
import br.ufscar.arch_kdm.core.visualization.VisualizeDriftsFactory;
import br.ufscar.arch_kdm.core.visualization.groupingAlgorithms.IGroupingAlgorithmType;
import br.ufscar.arch_kdm.core.visualization.model.Drift;
import br.ufscar.arch_kdm.ui.util.InterfaceGenericMethods;
import br.ufscar.arch_kdm.ui.wizards.ArchKDMWizard;

/**
 * @author Andr�
 *
 */
public enum GroupingAlgorithmTypes implements IGroupingAlgorithmType{

	GROUPING_BY_TO_FROM("Grouping TO-FROM"){
		boolean structure = true; 
		boolean code = true;
		
		@Override
		public Object configAlgo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object configAlgoDefault() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Drift> execAlgo(Object wizard, Object config) {
			GenericMethods.splitAggregatedByRelatedRelationships(((ArchKDMWizard) wizard).getStructureDrifts().getStructureElement());
			String KDMPath = ((ArchKDMWizard) wizard).getPathActualArchitecture().replace(".xmi", "-violations-ArchKDM2UML.kdm");
			GenericMethods.serializeSegment("file:///"+KDMPath, ((ArchKDMWizard) wizard).getSegmentActualArchitecture());
			
			ArchKDM2UML atl;
			
			if (code) {
				atl = new ArchKDM2UML(KDMPath, KDMPath.replace(".kdm", ".uml"), false);
				atl.run();
			}			
			if (structure) {
				atl = new ArchKDM2UML(KDMPath, KDMPath.replace(".kdm", ".uml"), true);
				atl.run();
			}
			
			return new ArrayList<Drift>();
		}
		
	},
	GROUPING_BY_PROXIMITY_MATRIX("Proximity Matrix"){
		
		@Override
		public Object configAlgo() {
			String textInterface = "Select the configuration of the DBScan Algo:";
			return InterfaceGenericMethods.dialogWhatAlgoConfiguration(textInterface, null);
		}
		
		@Override
		public List<Drift> execAlgo(Object wizard, Object config) {
			String optionsAlgo = (String) config;
			ArchKDMWizard wizardArchKDM = (ArchKDMWizard) wizard;
			String KDMViolationsPath = wizardArchKDM.getPathActualArchitecture().replace(".xmi", "-violations.xmi");

			IVisualizeDriftsAlgo visualizeDrifts = VisualizeDriftsFactory.INSTANCE.createVisualizeDriftsAlgoSimilarityMatrix();
			visualizeDrifts.setModelViolatingPath(KDMViolationsPath);
			visualizeDrifts.setAlgorithmOptions(optionsAlgo);

			List<Drift> drifts = visualizeDrifts.getDrifts();
			System.out.println(drifts.size());
			return drifts;
		}

		@Override
		public Object configAlgoDefault() {
			return "-E 0.45 -M 0";
		}
	};
	
	
	public String description;
	
	/**
	 * 
	 */
	private GroupingAlgorithmTypes(String description) {
		this.description = description;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @author Andr�
	 * @param text
	 */
	public static GroupingAlgorithmTypes getAlgo(String type) {
		for (GroupingAlgorithmTypes algo : GroupingAlgorithmTypes.values()) {
			if(algo.getDescription().equals(type)){
				return algo;
			}
		}
		return GROUPING_BY_PROXIMITY_MATRIX;
	}
	
}
