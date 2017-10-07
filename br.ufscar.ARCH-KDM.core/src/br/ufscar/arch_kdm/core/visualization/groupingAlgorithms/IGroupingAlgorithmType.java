/**
 * @author André
 * 
 */
package br.ufscar.arch_kdm.core.visualization.groupingAlgorithms;

import java.util.List;

import br.ufscar.arch_kdm.core.visualization.model.Drift;

/**
 * @author André
 *
 */
public interface IGroupingAlgorithmType {

	Object configAlgo();
	Object configAlgoDefault();
	List<Drift> execAlgo(Object wizard, Object config);
	
}
