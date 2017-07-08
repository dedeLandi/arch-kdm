/**
 * @author André
 * 
 */
package br.ufscar.arch_kdm.core.visualization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.arch_kdm.core.visualization.model.Drift;
import br.ufscar.arch_kdm.core.visualization.model.Violations;
import br.ufscar.kdm_manager.core.executionEngines.machineLearningEngine.clusteringWekaEngine.factory.MLClusteringWekaEngineFactory;
import br.ufscar.kdm_manager.core.executionEngines.machineLearningEngine.clusteringWekaEngine.interfaces.MLClusteringWekaEngine;
import br.ufscar.kdm_manager.core.executionEngines.metricsEngine.textMetricEngine.factory.TextMetricsEngineFactory;
import br.ufscar.kdm_manager.core.executionEngines.metricsEngine.textMetricEngine.interfaces.TextMestricEngine;
import br.ufscar.kdm_manager.core.filters.validateFilter.factory.KDMValidateFilterJavaFactory;
import br.ufscar.kdm_manager.core.filters.validateFilter.interfaces.KDMValidateFilter;
import br.ufscar.kdm_manager.core.readers.modelReader.factory.KDMModelReaderJavaFactory;
import br.ufscar.kdm_manager.core.readers.structureReader.factory.KDMStructureReaderJavaFactory;
import br.ufscar.kdm_manager.core.recovers.recoverCodeHierarchy.factory.KDMRecoverCodeHierarchyJavaFactory;
import br.ufscar.kdm_manager.core.recovers.recoverRelationshipLoC.factory.KDMRecoverLoCRelationshipFactory;
import br.ufscar.kdm_manager.core.recovers.recoverRelationshipLoC.interfaces.KDMRecoverGenericLoCRelationship;

/**
 * @author André
 *
 */
public class VisualizeDrifts {

	private String kDMViolationsPath;
	private String optionsAlgo;
	private String arffPath;
	private String clusterResultsPath;
	private List<String> inputWekaFile;
	private List<String> outputWekaFile;
	private List<KDMRelationship> allViolations;

	/**
	 * @author Landi
	 * @param kDMViolationsPath
	 */
	public void setModelViolatingPath(String kDMViolationsPath) {
		this.kDMViolationsPath = kDMViolationsPath;
	}

	/**
	 * @author Landi
	 * @param optionsAlgo
	 */
	public void setAlgorithmOptions(String optionsAlgo) {
		this.optionsAlgo = optionsAlgo;
	}

	/**
	 * @author Landi
	 * @return
	 */
	public List<Drift> getDrifts() {

		generateSimilarityMatrix();

		executeClustering();

		Map<String, List<KDMRelationship>> clusters = readClustering();

		List<Drift> drifts = generateDrifts(clusters);

		return drifts;
	}

	/**
	 * @author Landi
	 * @param clusters
	 * @return
	 */
	private List<Drift> generateDrifts(Map<String, List<KDMRelationship>> clusters) {
		List<Drift> drifts = new ArrayList<>();
		int numberDrift = 1;
		for (String key : clusters.keySet()) {

			List<KDMRelationship> violations = clusters.get(key);

			Drift drift = createDrift("Drift " + numberDrift, violations);

			for (KDMRelationship relationship : violations) {

				String action = relationship.eClass().getName();
				Violations violation = new Violations(action, drift, relationship);
				KDMRecoverGenericLoCRelationship<KDMRelationship> possibleLoC = KDMRecoverLoCRelationshipFactory.eINSTANCE.createRecoverLoCRelationship();
				violation.setPossibleLoC(possibleLoC.getLoCPatternFromRelationship(relationship));
				drift.addViolation(violation);
			}

			drifts.add(drift);

			numberDrift++;
		}
		return drifts;
	}

	/**
	 * @author Landi
	 * @param string
	 * @param violations
	 * @return
	 */
	private Drift createDrift(String name, List<KDMRelationship> violations) {
		Drift drift = new Drift(name);

		String packagePath = "";
		String className = "";
		String methodName = "";

		List<String> allViolationsPath = new ArrayList<>();
		for (KDMRelationship relationship : violations) {
			String violationInstancePath = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(relationship);
			allViolationsPath.add(violationInstancePath);
		}

		String FirstPath = allViolationsPath.get(0);
		String[] elements = FirstPath.split("\\.");

		for (int i = 0; i < elements.length; i++) {

			boolean allElementsEqual = false;

			for (String path : allViolationsPath) {
				String[] elementsTocompare = path.split("\\.");
				if(elements[i].equalsIgnoreCase(elementsTocompare[i])){
					allElementsEqual = true;
				}else{
					allElementsEqual = false;
					break;
				}
			}

			if(allElementsEqual){

				if(elements[i].contains("Package")){
					String elem = elements[i].split("-")[0];
					elem = elem.split("]")[1];
					packagePath = packagePath.concat(elem  + ".");
				}
				if(elements[i].contains("ClassUnit")){
					String elem = elements[i].split("-")[0];
					elem = elem.split("]")[1];
					className = className.concat(elem  + ".");
				}
				if(elements[i].contains("MethodUnit")){
					String elem = elements[i].split("-")[0];
					elem = elem.split("]")[1];
					methodName = methodName.concat(elem  + ".");
				}

			}

		}

		drift.setPackagePath(packagePath);
		drift.setClassName(className);
		drift.setMethodName(methodName);

		String possibleLoC = "fazer no KDM MAnager um parser";
		drift.setPossibleLoC(possibleLoC);

		return drift;
	}

	/**
	 * @author Landi
	 * @return
	 */
	private Map<String, List<KDMRelationship>> readClustering() {
		Map<String, List<KDMRelationship>> clusters = null;

		this.outputWekaFile = GenericMethods.readFile(this.clusterResultsPath);

		if(this.outputWekaFile != null){

			List<String> instanceLines = getInstanceLines(this.inputWekaFile);

			List<String> clustersLines = getInstanceLines(this.outputWekaFile);

			clusters = inputClustersInMap(clustersLines);

			KDMRelationship relationInstance = null;
			int instanceNumber = -1;
			for (String instanceLine : instanceLines) {

				if(instanceLine.contains("% NameInstance:")){
					relationInstance = null;
					instanceNumber++;
					relationInstance = findRelationByName(instanceLine);
				}else{

					for (String clusterLine : clustersLines) {
						String[] items = clusterLine.split(",");

						if(Integer.parseInt(items[0]) == instanceNumber){
							String cluster = items[items.length -1];

							List<KDMRelationship> vio = clusters.get(cluster);
							vio.add(relationInstance);
							clusters.put(cluster, vio);
							break;
						}

					}

				}
			}

		}

		return clusters;
	}

	/**
	 * @author Landi
	 * @param clustersLines
	 * @return
	 */
	private Map<String, List<KDMRelationship>> inputClustersInMap(List<String> clustersLines) {
		Map<String, List<KDMRelationship>> map = new HashMap<>();

		for (String line : clustersLines) {
			if(!line.equalsIgnoreCase("@data")){
				String[] items = line.split(",");
				String cluster = items[items.length -1];
				List<KDMRelationship> violations = new ArrayList<>();
				map.put(cluster, violations);
			}
		}
		return map;
	}

	/**
	 * @author Landi
	 * @param string
	 * @return
	 */
	private KDMRelationship findRelationByName(String name) {
		for (KDMRelationship relation : this.allViolations) {
			String nameInstanceRelation = "% NameInstance:" + relation.eClass().getName()+"_"+relation.hashCode();
			if(name.contains(nameInstanceRelation )){
				return relation;
			}
		}
		return null;
	}

	/**
	 * @author Landi
	 * @param wekaFile 
	 * @return
	 */
	private List<String> getInstanceLines(List<String> wekaFile) {
		List<String> lines = new ArrayList<>();
		boolean instanceLine = false;
		for (String line : wekaFile) {
			if(line.contains("@data")){
				instanceLine = true;
			}else if(instanceLine){
				lines.add(line);
			}
		}
		return lines;
	}

	/**
	 * @author Landi
	 */
	private void executeClustering() {
		MLClusteringWekaEngine<?> dbscan = MLClusteringWekaEngineFactory.eINSTANCE.createMLClusteringEngineWekaDBScan();

		System.out.println();

		dbscan.setDataToClustering(this.arffPath).setAlgorithmOptions(this.optionsAlgo).configureCluster();

		dbscan.clusterizingData();

		this.clusterResultsPath = this.arffPath.replace(".arff", "_out.arff");

		dbscan.saveResults(this.clusterResultsPath);

	}

	/**
	 * @author Landi
	 */
	private void generateSimilarityMatrix() {
		allViolations = new ArrayList<>();
		inputWekaFile = new ArrayList<String>();

		try { 

			//header of the arff file to clusterizing using the weka api
			inputWekaFile.add("@relation clusterizingDrifts\n");
			inputWekaFile.add("\n");

			Segment segmentViolation = GenericMethods.readSegmentFromPath(this.kDMViolationsPath);

			KDMValidateFilter<?, ?> filter = KDMValidateFilterJavaFactory.eINSTANCE.createValidateFilterNameOfKDMFramework("violations");
			Map<String, List<StructureModel>> violationsMap = KDMModelReaderJavaFactory.eINSTANCE.createKDMStructureModelReaderWithFilter(filter).getAllFromSegment(segmentViolation);

			for (String key : violationsMap.keySet()) {

				List<AggregatedRelationship> aggregateds = KDMStructureReaderJavaFactory.eINSTANCE.createKDMAggregatedRelationshipReader().getAllFrom(violationsMap.get(key).get(0));

				//iterating in the aggregateds that have at least one violation
				for (AggregatedRelationship aggregatedRelationship : aggregateds) {

					//iterating in the violations
					for (KDMRelationship violation : aggregatedRelationship.getRelation()) {
						//putting each violation as one attribute to make the similarity matrix
						inputWekaFile.add("@attribute " + violation.eClass().getName()+"_"+violation.hashCode() + " numeric\n");
						//getting only the violations to make easier the the process
						allViolations.add(violation);
					}
				}
			}

			//making the similarity matrix
			inputWekaFile.add("\n");
			inputWekaFile.add("@data\n");

			for (KDMRelationship violationInstance : allViolations) {
				//putting the instance name as comment in the matrix
				inputWekaFile.add("% NameInstance:" + violationInstance.eClass().getName()+"_"+violationInstance.hashCode() + " \n");

				//path of the violation
				String violationInstancePath = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(violationInstance);

				//putting the instance name as comment in the matrix
				//textoArquivoWeka.add("% PathInstance:" + violationInstancePath + " \n");

				String dataMetric = "";
				//iterating with all violations to calculate the similarity with them
				for (KDMRelationship violationToCompare : allViolations) {
					//path of the violation to compare
					String violationToComparePath = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(violationToCompare);

					TextMestricEngine<?, ?> metric = TextMetricsEngineFactory.eINSTANCE.createTextMetricLongestCommonSubsequence();

					dataMetric = dataMetric.concat(""+metric.applyMetric(violationInstancePath, violationToComparePath));

					if(!violationToCompare.equals(allViolations.get(allViolations.size()-1))){
						dataMetric = dataMetric.concat(",");
					}
				}
				inputWekaFile.add(dataMetric+"\n");
			}

			//saving the arff file to use of input at the clustering algorithm
			this.arffPath = this.kDMViolationsPath.replace(".xmi","_Weka.arff");
			FileWriter arquivo = new FileWriter(new File(this.arffPath));  
			for (String text : inputWekaFile) {
				arquivo.write(text);  
			}
			arquivo.close();  

		} catch (IOException e) {  
			e.printStackTrace();  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}

}
//
//public static void main(String[] args) {
//	List<String> inputWekaFile = new ArrayList<String>();
//	List<KDMRelationship> allViolations = new ArrayList<>();
//
//	inputWekaFile.add("@relation clusterizingDrifts\n");
//	inputWekaFile.add("\n");
//
//	//-----------------------------------------------------------------------------------------------------------------
//	//--------------------- Reading drifts
//	String kdmPath = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.core"
//			+ "\\src\\br\\ufscar\\arch_kdm\\core\\tests\\SystemExampleMVC-SimplesComDesvios_kdm-hastype-violations.xmi";
//
//	Segment segmentViolation = GenericMethods.readSegmentFromPath(kdmPath);
//
//	KDMValidateFilter<?, ?> filter = KDMValidateFilterJavaFactory.eINSTANCE.createValidateFilterNameOfKDMFramework("violations");
//	Map<String, List<StructureModel>> violationsMap = KDMModelReaderJavaFactory.eINSTANCE.createKDMStructureModelReaderWithFilter(filter ).getAllFromSegment(segmentViolation);
//
//	for (String key : violationsMap.keySet()) {
//		System.out.println("Structure Model name: " + key);
//		List<AggregatedRelationship> aggregateds = KDMStructureReaderJavaFactory.eINSTANCE.createKDMAggregatedRelationshipReader().getAllFrom(violationsMap.get(key).get(0));
//		System.out.println("--------------------------------------------------------------");
//		System.out.println("----------------------Violations------------------------------");
//		System.out.println("--------------------------------------------------------------");
//		for (AggregatedRelationship aggregatedRelationship : aggregateds) {
//			System.out.println("Aggregated from " + aggregatedRelationship.getFrom().getName() + 
//					" to " + aggregatedRelationship.getTo().getName() +
//					" with " + aggregatedRelationship.getDensity() + " violations");
//			for (KDMRelationship violation : aggregatedRelationship.getRelation()) {
//				inputWekaFile.add("@attribute " + violation.eClass().getName()+"_"+violation.hashCode() + " numeric\n");
//				allViolations.add(violation);
//
//			}
//
//		}
//		inputWekaFile.add("\n");
//		inputWekaFile.add("@data\n");
//		for (KDMRelationship violationInstance : allViolations) {
//			inputWekaFile.add("% NameInstance:" + violationInstance.eClass().getName()+"_"+violationInstance.hashCode() + " \n");
//			String violationInstancePath = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(violationInstance);
//			inputWekaFile.add("% PathInstance:" + violationInstancePath + " \n");
//			String dataMetric = "";
//			for (KDMRelationship violationToCompare : allViolations) {
//				String violationToComparePath = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(violationToCompare);
//				//					MetricLCS lcs = new MetricLCS();
//				//					dataMetric = dataMetric.concat(""+lcs.distance(violationInstancePath, violationToComparePath));
//
//				if(!violationToCompare.equals(allViolations.get(allViolations.size()-1))){
//					dataMetric = dataMetric.concat(",");
//				}
//			}
//			inputWekaFile.add(dataMetric+"\n");
//
//		}
//		System.out.println("--------------------------------------------------------------");
//
//		for (AggregatedRelationship aggregatedRelationship : aggregateds) {
//			System.out.println("--------------------------------------------------------------");
//			System.out.println("----------------------Violations from " + aggregatedRelationship.getFrom().getName() + 
//					" to " + aggregatedRelationship.getTo().getName() + "------------------------------");
//			System.out.println("--------------------------------------------------------------");
//
//			for (KDMRelationship violation : aggregatedRelationship.getRelation()) {
//				System.out.println("Violation type: " + violation.eClass().getName());
//				String violationPath = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(violation);
//				System.out.println("Violation path: " + violationPath);
//				for (KDMRelationship violationToCompare : aggregatedRelationship.getRelation()) {
//					String violationPathToCompare = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(violationToCompare);
//					System.out.println("	Violation: " + violationPathToCompare);
//					//						MetricLCS lcs = new MetricLCS();
//					//						System.out.println("	Metric LCS: " + lcs.distance(violationPath, violationPathToCompare));
//				}
//
//			}
//
//			System.out.println("--------------------------------------------------------------");
//		}
//
//	}
//
//
//	String teste1 = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.ui\\src\\br\\ufscar\\arch_kdm\\ui\\tests\\SystemExampleMVC-SimplesComDesvios_kdm-violations.xmi"; 
//	String teste2 = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.ui\\src\\br\\ufscar\\";
//	String teste3 = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\\br\\ufscar\\arch_kdm\\ui\\tests\\SystemExampleMVC-SimplesComDesvios_kdm-violations.xmi";
//
//	System.out.println("---------------------  Longest common subsequence ");
//	//		MetricLCS lcs = new MetricLCS();
//	//		System.out.println("-> distance");
//	//		System.out.println("1x1 " + lcs.distance(teste1, teste1));
//	//		System.out.println("1x2 " + lcs.distance(teste1, teste2));
//	//		System.out.println("2x1 " + lcs.distance(teste2, teste1));
//	//		System.out.println("1x3 " + lcs.distance(teste1, teste3));
//
//
//
//
//
//	FileWriter arquivo;  
//
//	try {  
//		arquivo = new FileWriter(new File("C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.core\\src\\br\\ufscar\\arch_kdm\\core\\tests\\entradaWeka.arff"));  
//		for (String text : inputWekaFile) {
//			arquivo.write(text);  
//		}
//		arquivo.close();  
//	} catch (IOException e) {  
//		e.printStackTrace();  
//	} catch (Exception e) {  
//		e.printStackTrace();  
//	}  
//
//
//
//}
//