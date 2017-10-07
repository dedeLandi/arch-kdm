package br.ufscar.arch_kdm.core.visualization.impl;

import br.ufscar.arch_kdm.core.visualization.IVisualizeDriftsAlgo;
import br.ufscar.arch_kdm.core.visualization.VisualizeDriftsFactory;

public class VisualizeDriftsFactoryImpl implements VisualizeDriftsFactory {

	private static VisualizeDriftsFactoryImpl singleton = null; 
	
	public static VisualizeDriftsFactoryImpl init() {
		if(singleton == null){
			singleton = new VisualizeDriftsFactoryImpl();
		}
		return singleton;
	}

	@Override
	public IVisualizeDriftsAlgo createVisualizeDriftsAlgoSimilarityMatrix() {
		return new VisualizeDriftsAlgoMatrixSimilarity();
	}

	@Override
	public IVisualizeDriftsAlgo createVisualizeDriftsAlgoToFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
