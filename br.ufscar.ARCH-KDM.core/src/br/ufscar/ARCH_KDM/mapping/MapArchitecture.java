package br.ufscar.ARCH_KDM.mapping;

import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.ARCH_KDM.util.GenericMethods;
import br.ufscar.kdm_manager.core.readers.relationshipReader.enums.KDMTypeRelations;

public class MapArchitecture {

	private StructureModel structureToMap;
//	private Segment segmentToMap;
	

	public MapArchitecture() {
		super();
	}
	
	public MapArchitecture(StructureModel completeMap) {
		this.structureToMap = completeMap;
	}

//	/**
//	 * @param completeMap
//	 * @param segmentActualArchitecture
//	 */
//	public MapArchitecture(StructureModel completeMap, Segment segmentActualArchitecture) {
//		this.structureToMap = completeMap;
//		this.segmentToMap = segmentActualArchitecture;
//	}

	public StructureModel cleanAggregateds() {
		for (AbstractStructureElement parentElement : this.structureToMap.getStructureElement()) {
			cleanAggregateds(parentElement);
		}
		return this.structureToMap;
	}
	
	private void cleanAggregateds(AbstractStructureElement parentElement) {
		parentElement.getInAggregated().clear();
		parentElement.getOutAggregated().clear();
		parentElement.getAggregated().clear();
		for (AbstractStructureElement childElement : parentElement.getStructureElement()) {
			cleanAggregateds(childElement);
		}
	}
	
	public void mapInitialArchitecture(AbstractStructureElement abstractStructureElement, KDMEntity codeElement) {
		abstractStructureElement.getImplementation().add(codeElement);
	}

	public StructureModel mapCompleteArchitecture() {
		for (AbstractStructureElement abstractStructureElement : this.structureToMap.getStructureElement()) {
			
			mapCompleteArchitecture(abstractStructureElement);
			
		}
		return this.structureToMap;
	}

	/**
	 * @author Landi
	 * @param abstractStructureElement
	 */
	private void mapCompleteArchitecture(AbstractStructureElement elementToMap) {
		
		for (KDMEntity kdmEntity : elementToMap.getImplementation()) {
			
			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsCalls = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.CALLS, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsCalls);

			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsCreates = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.CREATES, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsCreates);

			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsReads = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.READS, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsReads);

			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsUsesType = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.USES_TYPE, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsUsesType);
			
			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsWrites = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.WRITES, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsWrites);
			
			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsExtends = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.EXTENDS, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsExtends);
			
			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsHasType = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.HAS_TYPE, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsHasType);
			
			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsHasValue = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.HAS_VALUE, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsHasValue);
			
			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsImplements = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.IMPLEMENTS, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsImplements);
			
			Map<AbstractStructureElement, List<KDMRelationship>> allRelationsImports = MapRelationshipOfArchElement.getRelationFrom(KDMTypeRelations.IMPORTS, kdmEntity);
			this.insertOrUpdateAggregated(elementToMap, allRelationsImports);
					
			
		}
		
		for (AbstractStructureElement childElementToMap : elementToMap.getStructureElement()) {
			this.mapCompleteArchitecture(childElementToMap);
		}
		
	}

	/**
	 * 
	 * @author Landi
	 * @param from
	 * @param allRelations
	 */
	private void insertOrUpdateAggregated(AbstractStructureElement from, Map<AbstractStructureElement, List<KDMRelationship>> allRelations) {
		
		for (AbstractStructureElement to : allRelations.keySet()) {
			
			AggregatedRelationship aggregatedRelationship = this.existsAggregatedBetween(from, to);
			if(aggregatedRelationship != null){
				GenericMethods.updateAggreagatedWith(aggregatedRelationship, allRelations.get(to));
			}else{
				GenericMethods.createAggreagatedWith(to, from, allRelations.get(to));
			}
			
		}
	}

	/**
	 * @author Landi
	 * @param from
	 * @param to
	 * @return
	 */
	private AggregatedRelationship existsAggregatedBetween(AbstractStructureElement from, AbstractStructureElement to) {

		for (AggregatedRelationship aggregated : from.getAggregated()) {
			if(aggregated.getFrom().equals(from) && aggregated.getTo().equals(to)){
				return aggregated;
			}
		}
		
		return null;
	}

}
