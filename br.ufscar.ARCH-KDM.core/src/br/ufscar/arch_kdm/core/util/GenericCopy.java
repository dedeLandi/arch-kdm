/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.util;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

/**
 * @author Landi
 *
 */
public class GenericCopy {

	/**
	 * @author Landi
	 * @param targetStructure
	 * @param originalStructure
	 * @return
	 */
	public static StructureModel copyImplementation(StructureModel targetStructure, StructureModel originalStructure, CodeModel originalCode) {

		for (AbstractStructureElement element : targetStructure.getStructureElement()) {
			GenericCopy.copyImplementation(element, originalStructure, originalCode);
		}

		return targetStructure;
	}

	/**
	 * @author Landi
	 * @param element
	 * @param originalStructure
	 */
	private static void copyImplementation(AbstractStructureElement element, StructureModel originalStructure, CodeModel originalCode) {

		Collection<? extends KDMEntity> implementation = GenericCopy.getCopyListOfCodeElements(GenericMethods.getImplementationOf(element, originalStructure), originalCode);
		element.getImplementation().addAll(implementation);

		for (AbstractStructureElement child : element.getStructureElement()) {
			copyImplementation(child, originalStructure, originalCode);
		}

	}

	/**
	 * @author Landi
	 * @param implementationOf
	 * @param originalCode 
	 * @return
	 */
	private static Collection<? extends KDMEntity> getCopyListOfCodeElements(Collection<KDMEntity> implementationOf, CodeModel originalCode) {

		Collection<KDMEntity> newList = new ArrayList<>();

		for (KDMEntity kdmEntity : implementationOf) {
			KDMEntity originalCodeElement = GenericMethods.getOriginalCodeElement(kdmEntity, originalCode);
			if(originalCodeElement != null){
				newList.add(originalCodeElement);
			}
		}

		return newList;
	}

}
