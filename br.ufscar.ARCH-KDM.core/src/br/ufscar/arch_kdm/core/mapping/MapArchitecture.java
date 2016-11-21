package br.ufscar.arch_kdm.core.mapping;

import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.kdm_manager.core.readers.relationshipReader.enums.KDMTypeRelations;

public class MapArchitecture {

	private StructureModel structureToMap;
	

	public MapArchitecture() {
		super();
	}
	
	public MapArchitecture(StructureModel completeMap) {
		this.structureToMap = completeMap;
	}

	/**
	 * 
	 * @author Landi
	 * @param abstractStructureElement
	 * @param codeElement
	 */
	public void mapInitialArchitecture(AbstractStructureElement abstractStructureElement, KDMEntity codeElement) {
		abstractStructureElement.getImplementation().add(codeElement);
	}

	/**
	 * 
	 * @author Landi
	 * @return
	 */
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
			
			for (KDMTypeRelations typeRelationship : KDMTypeRelations.values()) {
				Map<AbstractStructureElement, List<KDMRelationship>> allRelations = 
						MapRelationshipOfArchElement.getRelationFrom(typeRelationship, kdmEntity, this.structureToMap);
				this.insertOrUpdateAggregated(elementToMap, allRelations);
			}
			
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
