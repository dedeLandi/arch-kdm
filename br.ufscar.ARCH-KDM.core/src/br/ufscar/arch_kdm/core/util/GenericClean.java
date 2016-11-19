/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.util;

import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

/**
 * @author Landi
 *
 */
public class GenericClean {

	/**
	 * cleanAggregateds
	 * @author Landi
	 * @param structureToClean
	 * @return
	 */
	public static StructureModel cleanAggregateds(StructureModel structureToClean) {
		for (AbstractStructureElement parentElement : structureToClean.getStructureElement()) {
			GenericClean.cleanAggregateds(parentElement);
		}
		return structureToClean;
	}

	/**
	 * cleanAggregateds
	 * @author Landi
	 * @param parentElement
	 */
	private static void cleanAggregateds(AbstractStructureElement parentElement) {
		parentElement.getInAggregated().clear();
		parentElement.getOutAggregated().clear();
		parentElement.getAggregated().clear();
		for (AbstractStructureElement childElement : parentElement.getStructureElement()) {
			cleanAggregateds(childElement);
		}
	}

}
