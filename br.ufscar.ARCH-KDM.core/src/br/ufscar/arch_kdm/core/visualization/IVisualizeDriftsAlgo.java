package br.ufscar.arch_kdm.core.visualization;

import java.util.List;

import br.ufscar.arch_kdm.core.visualization.model.Drift;

public interface IVisualizeDriftsAlgo {

	/**
	 * @author Landi
	 * @param kDMViolationsPath
	 */
	void setModelViolatingPath(String kDMViolationsPath);

	/**
	 * @author Landi
	 * @param optionsAlgo
	 */
	void setAlgorithmOptions(String optionsAlgo);

	/**
	 * @author Landi
	 * @return
	 */
	List<Drift> getDrifts();

}