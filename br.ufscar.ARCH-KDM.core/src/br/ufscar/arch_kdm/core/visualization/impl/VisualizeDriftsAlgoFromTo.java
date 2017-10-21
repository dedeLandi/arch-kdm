package br.ufscar.arch_kdm.core.visualization.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.arch_kdm.core.visualization.IVisualizeDriftsAlgo;
import br.ufscar.arch_kdm.core.visualization.model.Drift;
import br.ufscar.kdm_manager.core.executionEngines.atlEngine.abstractions.ATLExecutionEngine;
import br.ufscar.kdm_manager.core.executionEngines.atlEngine.factory.ATLExecutionEngineFactory;
import br.ufscar.kdm_manager.core.filters.validateFilter.factory.KDMValidateFilterJavaFactory;
import br.ufscar.kdm_manager.core.filters.validateFilter.interfaces.KDMValidateFilter;
import br.ufscar.kdm_manager.core.readers.modelReader.factory.KDMModelReaderJavaFactory;

public class VisualizeDriftsAlgoFromTo implements IVisualizeDriftsAlgo {

	private String kdmInPath;
	
	private boolean generateUML = false;
	
	private boolean generateUMLCode = false;

	private boolean generateUMLStructure = false;
	

	@Override
	public void setModelViolatingPath(String kDMViolationsPath) {
		this.kdmInPath = kDMViolationsPath;
	}

	@Override
	public void setAlgorithmOptions(String optionsAlgo) {
		System.out.println(optionsAlgo);
		String types[] = optionsAlgo.split(";");
		System.out.println(Boolean.parseBoolean(types[0].split(":")[1]));
		generateUML = Boolean.parseBoolean(types[0].split(":")[1]);
		System.out.println(Boolean.parseBoolean(types[1].split(":")[1]));
		generateUMLCode = Boolean.parseBoolean(types[1].split(":")[1]);
		System.out.println(Boolean.parseBoolean(types[2].split(":")[1]));
		generateUMLStructure = Boolean.parseBoolean(types[2].split(":")[1]);
	}

	@Override
	public List<Drift> getDrifts() {

		Segment segmentViolation = GenericMethods.readSegmentFromPath(this.kdmInPath);

		KDMValidateFilter<?, ?> filter = KDMValidateFilterJavaFactory.eINSTANCE.createValidateFilterNameOfKDMFramework("violations");
		Map<String, List<StructureModel>> violationsMap = KDMModelReaderJavaFactory.eINSTANCE.createKDMStructureModelReaderWithFilter(filter).getAllFromSegment(segmentViolation);

		StructureModel violationsModel = null;

		for (String key : violationsMap.keySet()) {
			violationsModel = violationsMap.get(key).get(0);
		}

		if(violationsModel == null) return new ArrayList<>();

		splitAggregatedByRelatedRelationships(violationsModel.getStructureElement());
		this.kdmInPath = this.kdmInPath.replace(".xmi", "-ArchKDM2UML.kdm");

		GenericMethods.serializeSegment("file:///"+this.kdmInPath, segmentViolation);

		if(generateUML){
			generateUML();
		}

		return new ArrayList<>();
	}

	public static void splitAggregatedByRelatedRelationships(EList<AbstractStructureElement> eList) {
		for (AbstractStructureElement abstractStructureElement: eList) {
			ArrayList<AggregatedGroup> aggregatedsGroupsList = new ArrayList<AggregatedGroup>();

			for (AggregatedRelationship aggregatedRelationship: abstractStructureElement.getAggregated()) {
				ArrayList<AggregatedRelationship> aggregatedsGroup = new ArrayList<AggregatedRelationship>();

				for (int i = 0; i < aggregatedRelationship.getRelation().size(); i++) {
					KDMRelationship relation_1 = aggregatedRelationship.getRelation().get(i);

					for (int j = i; j < aggregatedRelationship.getRelation().size(); j++) {
						KDMRelationship relation_2 = aggregatedRelationship.getRelation().get(j);

						if (isRelated(relation_1, relation_2)) {
							AggregatedRelationship aggregated = getRelationshipGroup(relation_1, aggregatedsGroup);
							aggregated = (aggregated != null)?aggregated:getRelationshipGroup(relation_2, aggregatedsGroup);

							if (aggregated == null) {
								aggregated = CoreFactory.eINSTANCE.createAggregatedRelationship();
								aggregated.setFrom(aggregatedRelationship.getFrom());
								aggregated.setTo(aggregatedRelationship.getTo());
								aggregatedsGroup.add(aggregated);
							}

							if (!aggregated.getRelation().contains(relation_1))
								aggregated.getRelation().add(0, relation_1);
							if (!aggregated.getRelation().contains(relation_2))
								aggregated.getRelation().add(0, relation_2);
							aggregated.setDensity(aggregated.getRelation().size());
						}
					}
				}

				AggregatedGroup group = new AggregatedGroup((AbstractStructureElement) aggregatedRelationship.getFrom(), (AbstractStructureElement) aggregatedRelationship.getTo(), aggregatedsGroup);
				aggregatedsGroupsList.add(group);

				group.getFrom().getOutAggregated().remove(aggregatedRelationship);
				group.getTo().getInAggregated().remove(aggregatedRelationship);
			}

			abstractStructureElement.getAggregated().clear();
			for(AggregatedGroup group: aggregatedsGroupsList)
				for(AggregatedRelationship aggregated: group.getAggregatedGroup()) {
					abstractStructureElement.getAggregated().add(aggregated);
					group.getFrom().getOutAggregated().add(aggregated);	
					group.getTo().getInAggregated().add(aggregated);	
				}

			if (abstractStructureElement.getStructureElement().size() > 0)
				splitAggregatedByRelatedRelationships(abstractStructureElement.getStructureElement());
		}
	}

	private static boolean isRelated(KDMRelationship relation_1, KDMRelationship relation_2) {
		return getClassUnit(relation_1.getTo()) == getClassUnit(relation_2.getTo()) && getClassUnit(relation_1.getFrom()) == getClassUnit(relation_2.getFrom());
	}

	private static AggregatedRelationship getRelationshipGroup(KDMRelationship relation, ArrayList<AggregatedRelationship> groups) {
		for (AggregatedRelationship group: groups)
			if (group.getRelation().contains(relation))
				return group;

		return null;
	}

	//	private static boolean isRelated(KDMEntity originalCodeElement_1, KDMEntity originalCodeElement_2) {
	//	return isParent(originalCodeElement_1, originalCodeElement_2) || isParent(originalCodeElement_2, originalCodeElement_1);
	//}

	//private static boolean isParent(EObject originalObject_1, EObject originalObject_2) {
	//	// TODO Auto-generated method stub
	//	EObject object = originalObject_2;
	//	
	//	while (object != null) {
	//		if (object == originalObject_1)
	//			return true;
	//		
	//		object = object.eContainer();
	//	};
	//	
	//	return false;
	//}

	private static ClassUnit getClassUnit(EObject object) {
		if (!(object instanceof Segment))
			if (object instanceof ClassUnit)
				return (ClassUnit) object;
			else			
				return getClassUnit(object.eContainer());

		return null;
	}

	/**
	 * @author Gasparini
	 * @param codeModel 
	 * @param structureModel
	 * Split an AggregatedRelationship into AggregatedRelationships, each containing only related KDMRelationships.
	 */
	 static private class AggregatedGroup {
		public AggregatedGroup(AbstractStructureElement from, AbstractStructureElement to, ArrayList<AggregatedRelationship> aggregatedGroup) {
			super();
			this.from = from;
			this.to = to;
			this.aggregatedGroup = aggregatedGroup;
		}
		AbstractStructureElement from, to;
		ArrayList<AggregatedRelationship> aggregatedGroup;	
		
		public AbstractStructureElement getFrom() {
			return from;
		}
		public AbstractStructureElement getTo() {
			return to;
		}
		public ArrayList<AggregatedRelationship> getAggregatedGroup() {
			return aggregatedGroup;
		}
	}

	private void generateUML() {
		if(generateUMLCode){
			generateCodeView();
		}
		if(generateUMLStructure){
			generateStructureView();
		}

	}

	private void generateStructureView() {
		Map<String, Object[]> metamodel = new HashMap<String, Object[]>();

		Object metamodelData[] = new Object[1];
		metamodelData[0]="platform:/plugin/org.eclipse.gmt.modisco.omg.kdm/model/kdm.ecore";
		metamodel.put("kdm", metamodelData);

		Object metamodelData2[] = new Object[1];
		metamodelData2[0]="platform:/plugin/org.eclipse.uml2.uml/model/UML.ecore";
		metamodel.put("uml", metamodelData2);

		Map<String, Map<String, String>> metamodelsInModels = new HashMap<String, Map<String, String>>();

		Map<String, String> KDMmodelsIn = new HashMap<>();
		KDMmodelsIn.put("kdmInput", kdmInPath);
		metamodelsInModels.put("kdm", KDMmodelsIn);

		Map<String, Map<String, String>> metamodelsOutModels = new HashMap<String, Map<String, String>>();

		Map<String, String> KDMmodelsOut = new HashMap<>();
		KDMmodelsOut.put("umlOutput", kdmInPath.replace(".kdm", "-structureView.uml"));
		metamodelsOutModels.put("uml", KDMmodelsOut);

		Map<String, Map<String, String>> transformationModule = new HashMap<>();

		Map<String, String> module = new HashMap<>();
		URL asmFile;
		//		asmFile = new URL("file://C:\\JavaLab\\workspace\\ArchKDM2UML\\ArchKDM2UML_CodeView.asm");
		asmFile = this.getClass().getResource("/resources/ArchKDM2UML_StructureView.asm");

		module.put("ArchKDM2UML_StructureView", asmFile.toString());

		transformationModule.put("transformation", module);

		ATLExecutionEngine<Map<String, String>, Object[]> atlRuns = ATLExecutionEngineFactory.eINSTANCE.createATLExecutionEngineASMModiscoCompiler();
		atlRuns.setMetamodelData(metamodel).setModelsInData(metamodelsInModels).setModelsOutData(metamodelsOutModels).setTransformationModule(transformationModule).configureATLEngine();
		atlRuns.launch();

	}

	private void generateCodeView() {
		Map<String, Object[]> metamodel = new HashMap<String, Object[]>();

		Object metamodelData[] = new Object[1];
		metamodelData[0]="platform:/plugin/org.eclipse.gmt.modisco.omg.kdm/model/kdm.ecore";
		metamodel.put("kdm", metamodelData);

		Object metamodelData2[] = new Object[1];
		metamodelData2[0]="platform:/plugin/org.eclipse.uml2.uml/model/UML.ecore";
		metamodel.put("uml", metamodelData2);

		Map<String, Map<String, String>> metamodelsInModels = new HashMap<String, Map<String, String>>();

		Map<String, String> KDMmodelsIn = new HashMap<>();
		KDMmodelsIn.put("kdmInput", kdmInPath);
		metamodelsInModels.put("kdm", KDMmodelsIn);

		Map<String, Map<String, String>> metamodelsOutModels = new HashMap<String, Map<String, String>>();

		Map<String, String> KDMmodelsOut = new HashMap<>();
		KDMmodelsOut.put("umlOutput", kdmInPath.replace(".kdm", "-codeView.uml"));
		metamodelsOutModels.put("uml", KDMmodelsOut);

		Map<String, Map<String, String>> transformationModule = new HashMap<>();

		Map<String, String> module = new HashMap<>();
		URL asmFile;
		//		asmFile = new URL("file://C:\\JavaLab\\workspace\\ArchKDM2UML\\ArchKDM2UML_CodeView.asm");
		asmFile = this.getClass().getResource("/resources/ArchKDM2UML_CodeView.asm");

		module.put("ArchKDM2UML_CodeView", asmFile.toString());

		transformationModule.put("transformation", module);

		ATLExecutionEngine<Map<String, String>, Object[]> atlRuns = ATLExecutionEngineFactory.eINSTANCE.createATLExecutionEngineASMModiscoCompiler();
		atlRuns.setMetamodelData(metamodel).setModelsInData(metamodelsInModels).setModelsOutData(metamodelsOutModels).setTransformationModule(transformationModule).configureATLEngine();
		atlRuns.launch();
	}

}
