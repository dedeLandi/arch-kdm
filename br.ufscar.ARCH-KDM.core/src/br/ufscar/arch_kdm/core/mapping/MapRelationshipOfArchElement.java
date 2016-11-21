/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.CatchUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.TryUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.kdm_manager.core.readers.relationshipReader.enums.KDMTypeRelations;
import br.ufscar.kdm_manager.core.recovers.recoverCodeHierarchy.factory.KDMRecoverCodeHierarchyJavaFactory;

/**
 * @author Landi
 *
 */
public class MapRelationshipOfArchElement {

	private static Map<String, List<KDMRelationship>> othersRelations = null;
	
	/**
	 * @author Landi
	 */
	private static void initOtherRelations() {
		if(othersRelations == null){
			othersRelations = new HashMap<String, List<KDMRelationship>>();
			for (KDMTypeRelations typeRelation : KDMTypeRelations.values()) {
				othersRelations.put(typeRelation.getName(), new ArrayList<KDMRelationship>());
			}
			
/*			othersRelations.put(KDMTypeRelations.CALLS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.CREATES.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.READS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.USES_TYPE.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.WRITES.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.EXTENDS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.HAS_TYPE.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.HAS_VALUE.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.IMPLEMENTS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.IMPORTS.getName(), new ArrayList<KDMRelationship>());*/
		}
	}
	
	/**
	 * 
	 * @author Landi
	 * @param typeRelation
	 * @param entityToAvaliate
	 * @return
	 */
	public static Map<AbstractStructureElement, List<KDMRelationship>> getRelationFrom(KDMTypeRelations typeRelation, KDMEntity entityToAvaliate, StructureModel structureToSearch){
		initOtherRelations();
		
		return MapRelationshipOfArchElement.getRelationsByTo(typeRelation, entityToAvaliate, structureToSearch);
		
		/*switch (typeRelation) {
		
		case CALLS:
			return MapRelationshipOfArchElement.getRelationsCallsByTo(entityToAvaliate);
		case CREATES:
			return MapRelationshipOfArchElement.getRelationsCreatesByTo(entityToAvaliate);
		case READS:
			return MapRelationshipOfArchElement.getRelationsReadsByTo(entityToAvaliate);
		case USES_TYPE:
			return MapRelationshipOfArchElement.getRelationsUsesTypeByTo(entityToAvaliate);
		case WRITES:
			return MapRelationshipOfArchElement.getRelationsWritesByTo(entityToAvaliate);
		
		case EXTENDS:
			return MapRelationshipOfArchElement.getRelationsExtendsByTo(entityToAvaliate);
		case HAS_TYPE:
			return MapRelationshipOfArchElement.getRelationsHasTypeByTo(entityToAvaliate);
		case HAS_VALUE:
			return MapRelationshipOfArchElement.getRelationsHasValueByTo(entityToAvaliate);
		case IMPLEMENTS:
			return MapRelationshipOfArchElement.getRelationsImplementsByTo(entityToAvaliate);
		case IMPORTS:
			return MapRelationshipOfArchElement.getRelationsImportsByTo(entityToAvaliate);

		default:
			return null;
		}*/
		
	}
	
	/**
	 * @author Landi
	 * @param typeRelation
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsByTo(KDMTypeRelations typeRelation,
			KDMEntity entityToAvaliate, StructureModel structureToSearch) {
		List<?> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = typeRelation.getReader().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = typeRelation.getReader().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = typeRelation.getReader().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = typeRelation.getReader().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, typeRelation, structureToSearch);
	}
	
	/**
	 * @author Landi
	 * @param <T>
	 * @param allRelationshipOf
	 * @return
	 */
	private static <T> Map<AbstractStructureElement, List<KDMRelationship>> createMapByTo(List<T> allRelationshipOf, KDMTypeRelations typeRelation, StructureModel structureToSearch) {
		Map<AbstractStructureElement, List<KDMRelationship>> map = new HashMap<AbstractStructureElement, List<KDMRelationship>>();
		
		for (T relation : allRelationshipOf) {
			AbstractStructureElement element = MapRelationshipOfArchElement.getArchitecturalElementFromOrTo(((KDMRelationship) relation).getTo(), structureToSearch);
			
			if(element != null){
				
				if(map.get(element) == null || map.get(element).size() == 0){
					//first group
					map.put(element, new ArrayList<KDMRelationship>());
					map.get(element).add((KDMRelationship) relation);
				}else{
					map.get(element).add((KDMRelationship) relation);
				}
				
			}else{
				//relation are not to the other architectural element of the system
				MapRelationshipOfArchElement.othersRelations.get(typeRelation.getName()).add((KDMRelationship) relation);
			}
		}
		
		return map;
	}
	
	/**
	 * @author Landi
	 * @param kdmEntity
	 * @return
	 */
	public static AbstractStructureElement getArchitecturalElementFromOrTo(KDMEntity kdmEntity, StructureModel structureToSearch) {
		
		AbstractStructureElement fromOrTo = null;
		if(kdmEntity instanceof StorableUnit){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((StorableUnit)kdmEntity);
		}else if(kdmEntity instanceof MethodUnit){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((MethodUnit)kdmEntity);
		}else if(kdmEntity instanceof ClassUnit){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((ClassUnit)kdmEntity);
		}else if(kdmEntity instanceof InterfaceUnit){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((InterfaceUnit)kdmEntity);
		}else if(kdmEntity instanceof EnumeratedType){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((EnumeratedType)kdmEntity);
		}else if(kdmEntity instanceof ActionElement){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((ActionElement)kdmEntity);
		}else if(kdmEntity instanceof TryUnit){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((TryUnit)kdmEntity);
		}else if(kdmEntity instanceof CatchUnit){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((CatchUnit)kdmEntity);
		}else if(kdmEntity instanceof BlockUnit){
			fromOrTo = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeStructureHierarchyFirstArchitecturalElement().getHierarchyOf((BlockUnit)kdmEntity);
		}
		
		if(fromOrTo != null){
			for (AbstractStructureElement abstractStructureElement : structureToSearch.getStructureElement()) {
				fromOrTo = getArchitecturalElementFromOrTo(fromOrTo, abstractStructureElement);
				if(fromOrTo != null){
					break;
				}
			}
		}
		return fromOrTo;
	}

	/**
	 * @author Landi
	 * @param kdmEntity
	 * @param abstractStructureElement
	 * @return 
	 */
	private static AbstractStructureElement getArchitecturalElementFromOrTo(AbstractStructureElement elementToSearch,
			AbstractStructureElement parentElement) {
		String pathElementToSearch = GenericMethods.getPathFromStructureElement(elementToSearch);
		String pathParentElement = GenericMethods.getPathFromStructureElement(parentElement);
		if(pathElementToSearch.equalsIgnoreCase(pathParentElement)){
			return parentElement;
		}
		
		AbstractStructureElement fromOrTo = null;
		for (AbstractStructureElement childElement : parentElement.getStructureElement()) {
			fromOrTo = getArchitecturalElementFromOrTo(elementToSearch, childElement);
			if(fromOrTo != null){
				break;
			}
		}
		return fromOrTo;
		
	}

	/**
	 * @return the othersRelations
	 */
	public static Map<String, List<KDMRelationship>> getOthersRelations() {
		return othersRelations;
	}
	
	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsImportsByTo(
			KDMEntity entityToAvaliate) {
		List<Imports> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImportsRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImportsRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImportsRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImportsRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.IMPORTS);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsImplementsByTo(
			KDMEntity entityToAvaliate) {
		List<Implements> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImplementsRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImplementsRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImplementsRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createImplementsRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.IMPLEMENTS);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsHasValueByTo(
			KDMEntity entityToAvaliate) {
		List<HasValue> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasValueRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasValueRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasValueRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasValueRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.HAS_VALUE);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsHasTypeByTo(
			KDMEntity entityToAvaliate) {
		List<HasType> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasTypeRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasTypeRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasTypeRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createHasTypeRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.HAS_TYPE);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsExtendsByTo(
			KDMEntity entityToAvaliate) {
		List<Extends> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createExtendsRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createExtendsRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createExtendsRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createExtendsRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.EXTENDS);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsWritesByTo(
			KDMEntity entityToAvaliate) {
		List<Writes> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createWritesRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createWritesRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createWritesRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createWritesRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.WRITES);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsUsesTypeByTo(
			KDMEntity entityToAvaliate) {
		List<UsesType> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createUsesTypeRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createUsesTypeRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createUsesTypeRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createUsesTypeRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.USES_TYPE);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsReadsByTo(
			KDMEntity entityToAvaliate) {
		List<Reads> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createReadsRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createReadsRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createReadsRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createReadsRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.READS);
	}*/

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
/*	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsCreatesByTo(KDMEntity entityToAvaliate) {
		List<Creates> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCreatesRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCreatesRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCreatesRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCreatesRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.CREATES);
	}*/
	
	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	/*private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsCallsByTo(KDMEntity entityToAvaliate) {
		List<Calls> allRelationshipOf = null;
		
		if(entityToAvaliate instanceof Package){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCallsRecover().getAllRelationshipOf((Package)entityToAvaliate);
		}else if(entityToAvaliate instanceof ClassUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCallsRecover().getAllRelationshipOf((ClassUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof InterfaceUnit){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCallsRecover().getAllRelationshipOf((InterfaceUnit)entityToAvaliate);
		}else if(entityToAvaliate instanceof EnumeratedType){
			allRelationshipOf = KDMRelationshipReaderJavaFactory.eINSTANCE.createCallsRecover().getAllRelationshipOf((EnumeratedType)entityToAvaliate);
		}
		
		return MapRelationshipOfArchElement.createMapByTo(allRelationshipOf, KDMTypeRelations.CALLS);
	}*/
	
}
