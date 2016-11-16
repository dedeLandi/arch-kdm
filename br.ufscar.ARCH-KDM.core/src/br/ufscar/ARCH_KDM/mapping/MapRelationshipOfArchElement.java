/**
 * @author Landi
 * 
 */
package br.ufscar.ARCH_KDM.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.CatchUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.action.Reads;
import org.eclipse.gmt.modisco.omg.kdm.action.TryUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.UsesType;
import org.eclipse.gmt.modisco.omg.kdm.action.Writes;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.HasType;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.Imports;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;

import br.ufscar.kdm_manager.core.readers.relationshipReader.enums.KDMTypeRelations;
import br.ufscar.kdm_manager.core.readers.relationshipReader.factory.KDMRelationshipReaderJavaFactory;
import br.ufscar.kdm_manager.core.recovers.recoverHierarchy.factory.RecoverHierarchyJavaFactory;

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
			othersRelations.put(KDMTypeRelations.CALLS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.CREATES.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.READS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.USES_TYPE.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.WRITES.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.EXTENDS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.HAS_TYPE.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.HAS_VALUE.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.IMPLEMENTS.getName(), new ArrayList<KDMRelationship>());
			othersRelations.put(KDMTypeRelations.IMPORTS.getName(), new ArrayList<KDMRelationship>());
		}
	}
	
	public static Map<AbstractStructureElement, List<KDMRelationship>> getRelationFrom(KDMTypeRelations typeRelation, KDMEntity entityToAvaliate){
		initOtherRelations();
		
		switch (typeRelation) {
		
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
		}
		
	}
	
	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsImportsByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsImplementsByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsHasValueByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsHasTypeByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsExtendsByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsWritesByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsUsesTypeByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsReadsByTo(
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
	}

	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsCreatesByTo(KDMEntity entityToAvaliate) {
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
	}
	
	/**
	 * @author Landi
	 * @param entityToAvaliate
	 * @return
	 */
	private static Map<AbstractStructureElement, List<KDMRelationship>> getRelationsCallsByTo(KDMEntity entityToAvaliate) {
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
	}
	
	/**
	 * @author Landi
	 * @param <T>
	 * @param allRelationshipOf
	 * @return
	 */
	private static <T> Map<AbstractStructureElement, List<KDMRelationship>> createMapByTo(List<T> allRelationshipOf, KDMTypeRelations typeRelation) {
		Map<AbstractStructureElement, List<KDMRelationship>> map = new HashMap<AbstractStructureElement, List<KDMRelationship>>();
		
		for (T relation : allRelationshipOf) {
			AbstractStructureElement element = MapRelationshipOfArchElement.getArchitecturalElementFromOrTo(((KDMRelationship) relation).getTo());
			
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
	public static AbstractStructureElement getArchitecturalElementFromOrTo(KDMEntity kdmEntity) {
		
		if(kdmEntity instanceof StorableUnit){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((StorableUnit)kdmEntity);
		}else if(kdmEntity instanceof MethodUnit){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((MethodUnit)kdmEntity);
		}else if(kdmEntity instanceof ClassUnit){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((ClassUnit)kdmEntity);
		}else if(kdmEntity instanceof InterfaceUnit){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((InterfaceUnit)kdmEntity);
		}else if(kdmEntity instanceof EnumeratedType){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((EnumeratedType)kdmEntity);
		}else if(kdmEntity instanceof ActionElement){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((ActionElement)kdmEntity);
		}else if(kdmEntity instanceof TryUnit){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((TryUnit)kdmEntity);
		}else if(kdmEntity instanceof CatchUnit){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((CatchUnit)kdmEntity);
		}else if(kdmEntity instanceof BlockUnit){
			return RecoverHierarchyJavaFactory.eINSTANCE.createRecoverHierarchyFirstArchitecturalElement().getHierarchyOf((BlockUnit)kdmEntity);
		}
		
		return null;
	}

}
