package br.ufscar.arch_kdm.core.visualization;

public interface VisualizeDriftsFactory {

	VisualizeDriftsFactory INSTANCE = br.ufscar.arch_kdm.core.visualization.impl.VisualizeDriftsFactoryImpl.init();
	
	IVisualizeDriftsAlgo createVisualizeDriftsAlgoSimilarityMatrix();
	
	IVisualizeDriftsAlgo createVisualizeDriftsAlgoToFrom();
	
}
