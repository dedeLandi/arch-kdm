/**
 * @author André
 * 
 */
package br.ufscar.arch_kdm.ui.visualization.groupingTypes;

import java.util.List;

import br.ufscar.arch_kdm.core.visualization.VisualizeDrifts;
import br.ufscar.arch_kdm.core.visualization.groupingAlgorithms.IGroupingAlgorithmType;
import br.ufscar.arch_kdm.core.visualization.model.Drift;
import br.ufscar.arch_kdm.ui.util.InterfaceGenericMethods;
import br.ufscar.arch_kdm.ui.wizards.ArchKDMWizard;

/**
 * @author André
 *
 */
public enum GroupingAlgorithmTypes implements IGroupingAlgorithmType{

	GROUPING_BY_TO_FROM("Grouping TO-FROM"){

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
			// TODO Auto-generated method stub
			return null;
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

			VisualizeDrifts visualizeDrifts = new VisualizeDrifts();
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
	 * @author André
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
