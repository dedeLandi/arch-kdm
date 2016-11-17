/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.util;

import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;

/**
 * @author Landi
 *
 */
public class GenericMethods {

	public static void createAggreagatedWith(AbstractStructureElement to, AbstractStructureElement from,
			List<KDMRelationship> relations) {
		
		AggregatedRelationship aggregatedRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
		aggregatedRelationship.setDensity(relations.size());
		aggregatedRelationship.setFrom(from);
		aggregatedRelationship.setTo(to);
		aggregatedRelationship.getRelation().addAll(relations);
		
		from.getAggregated().add(aggregatedRelationship);
	}
	
	public static void updateAggreagatedWith(AggregatedRelationship aggregatedRelationship, List<KDMRelationship> relations) {

		int density = relations.size() + aggregatedRelationship.getDensity();
		aggregatedRelationship.setDensity(density);
		aggregatedRelationship.getRelation().addAll(relations);

	}
	
}
