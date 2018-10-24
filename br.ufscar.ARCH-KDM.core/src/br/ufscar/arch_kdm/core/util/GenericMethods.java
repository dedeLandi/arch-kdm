/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import br.ufscar.kdm_manager.core.complements.complementRelationship.factory.KDMComplementsRelationshipFactory;
import br.ufscar.kdm_manager.core.exceptions.KDMFileException;
import br.ufscar.kdm_manager.core.filters.validateFilter.factory.KDMValidateFilterJavaFactory;
import br.ufscar.kdm_manager.core.filters.validateFilter.interfaces.KDMValidateFilter;
import br.ufscar.kdm_manager.core.loads.factory.KDMFileReaderFactory;
import br.ufscar.kdm_manager.core.readers.modelReader.factory.KDMModelReaderJavaFactory;
import br.ufscar.kdm_manager.core.readers.structureReader.factory.KDMStructureReaderJavaFactory;
import br.ufscar.kdm_manager.core.recovers.recoverCodeHierarchy.factory.KDMRecoverCodeHierarchyJavaFactory;
import br.ufscar.kdm_manager.core.recovers.recoverStructureHierarchy.factory.KDMRecoverStructureHierarchyJavaFactory;
import br.ufscar.kdm_manager.core.serializes.factory.KDMFileSerializeFactory;

/**
 * @author Landi
 *
 */
public class GenericMethods {

	public static void createAggreagatedWith(AbstractStructureElement to, AbstractStructureElement from,
			List<KDMRelationship> relations) {

		AggregatedRelationship aggregatedRelationship = from.createAggregation(to);;
		aggregatedRelationship.setDensity(relations.size());
		aggregatedRelationship.getRelation().addAll(relations);

		from.getAggregated().add(aggregatedRelationship);

	}

	public static void updateAggreagatedWith(AggregatedRelationship aggregatedRelationship, List<KDMRelationship> relations) {

		int density = relations.size() + aggregatedRelationship.getDensity();
		aggregatedRelationship.setDensity(density);
		aggregatedRelationship.getRelation().addAll(relations);

	}

	/**
	 * @author Landi
	 */
	public static void serializeSegment(String kdmPath, Segment segmentToSerialize) {
		KDMFileSerializeFactory.eINSTANCE.createKDMFileSerializeFromSegment().serializeFromObject(kdmPath, segmentToSerialize);
	}

	/**
	 * @author Landi
	 * @param segment
	 * @return
	 */
	public static Map<String, List<CodeModel>> getAllCode(Segment segment) {
		return KDMModelReaderJavaFactory.eINSTANCE.createKDMCodeModelReader().getAllFromSegment(segment);
	}

	/**
	 * @author Landi
	 * @param segment
	 * @return
	 */
	public static Map<String, List<StructureModel>> getAllStructure(Segment segment) {
		return KDMModelReaderJavaFactory.eINSTANCE.createKDMStructureModelReader().getAllFromSegment(segment);
	}

	/**
	 * @author Landi
	 * @param path
	 * @return
	 */
	public static Segment readSegmentFromPath(String path) {
		try {
			return KDMFileReaderFactory.eINSTANCE.createKDMFileReaderToSegment().readFromPath(path);
		} catch (KDMFileException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @author Landi
	 * @param segment 
	 * @param string
	 * @return
	 */
	public static StructureModel getStructureArchitecture(String name, Segment segment) {
		KDMValidateFilter<?, ?> filter = KDMValidateFilterJavaFactory.eINSTANCE.createValidateFilterNameOfKDMFramework(name);
		Map<String, List<StructureModel>> allStructureModelActualArchitecture = KDMModelReaderJavaFactory.eINSTANCE.
				createKDMStructureModelReaderWithFilter(filter).getAllFromSegment(segment);

		if(allStructureModelActualArchitecture.keySet().size() == 1){
			for (String key : allStructureModelActualArchitecture.keySet()) {
				if(allStructureModelActualArchitecture.get(key).size() == 1){
					return allStructureModelActualArchitecture.get(key).get(0);
				}
			}
		}
		return null;
	}

	/**
	 * @author Landi
	 * @param kdmEntity
	 * @param originalCode
	 * @return
	 */
	public static KDMEntity getOriginalCodeElement(KDMEntity elementToSearch, CodeModel originalCode) {
		KDMEntity original = null;
		for (AbstractCodeElement originalElement : originalCode.getCodeElement()) {
			original = getOriginalCodeElement(elementToSearch, originalElement);
			if(original != null){
				break;
			}
		}
		return original;
	}

	/**
	 * @author Landi
	 * @param elementToSearch
	 * @param originalElement
	 * @return 
	 */
	private static KDMEntity getOriginalCodeElement(KDMEntity elementToSearch, AbstractCodeElement originalElement) {
		String pathElementToSearch = getPathFromCodeElement(elementToSearch);
		String pathOriginalElement = getPathFromCodeElement(originalElement);
		if(pathOriginalElement == null){
			return null;
		}
		if(pathElementToSearch.equalsIgnoreCase(pathOriginalElement)){
			return originalElement;			
		}
		KDMEntity original = null;
		if(originalElement instanceof Package){
			for (AbstractCodeElement child : ((Package) originalElement).getCodeElement()) {
				original = getOriginalCodeElement(elementToSearch, child);
				if (original != null) {
					break;
				}
			}
		}else if (originalElement instanceof ClassUnit) {
			for (CodeItem child : ((ClassUnit) originalElement).getCodeElement()) {
				original = getOriginalCodeElement(elementToSearch, child);
				if (original != null) {
					break;
				}
			}
		}else if (originalElement instanceof InterfaceUnit) {
			for (CodeItem child : ((InterfaceUnit) originalElement).getCodeElement()) {
				original = getOriginalCodeElement(elementToSearch, child);
				if (original != null) {
					break;
				}
			}
		}else if (originalElement instanceof EnumeratedType) {
			for (CodeItem child : ((EnumeratedType) originalElement).getCodeElement()) {
				original = getOriginalCodeElement(elementToSearch, child);
				if (original != null) {
					break;
				}
			}
		}
		return original;
	}

	/**
	 * @author Landi
	 * @param element
	 * @param originalStructure
	 * @return
	 */
	public static Collection<KDMEntity> getImplementationOf(AbstractStructureElement elementToSearch, StructureModel originalStructure) {
		Collection<KDMEntity> list = new ArrayList<>();

		for (AbstractStructureElement originalElement : originalStructure.getStructureElement()) {
			list.addAll(getImplementationOf(elementToSearch, originalElement));
			if(list.size() > 0){
				return list;
			}
		}
		return list;
	}

	/**
	 * @author Landi
	 * @param elementToSearch
	 * @param originalElement
	 * @return
	 */
	private static Collection<KDMEntity> getImplementationOf(AbstractStructureElement elementToSearch,
			AbstractStructureElement originalElement) {
		if(elementToSearch.getClass() == originalElement.getClass() && elementToSearch.getName().equalsIgnoreCase(originalElement.getName())){
			return originalElement.getImplementation();			
		}
		Collection<KDMEntity> list = new ArrayList<>();
		for (AbstractStructureElement child : originalElement.getStructureElement()) {
			list.addAll(getImplementationOf(elementToSearch, child));
		}
		return list;
	}


	/**
	 * @author Landi
	 * @param elementToSearch
	 * @return
	 */
	public static String getPathFromCodeElement(KDMEntity elementToSearch) {
		String path = null;
		boolean withHashCode = false;

		if(elementToSearch instanceof Package){
			path = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeHierarchyComplete(withHashCode).getHierarchyOf((Package)elementToSearch);
		}else if (elementToSearch instanceof ClassUnit) {
			path = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeHierarchyComplete(withHashCode).getHierarchyOf((ClassUnit)elementToSearch);
		}else if (elementToSearch instanceof InterfaceUnit) {
			path = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeHierarchyComplete(withHashCode).getHierarchyOf((InterfaceUnit)elementToSearch);
		}else if (elementToSearch instanceof EnumeratedType) {
			path = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeHierarchyComplete(withHashCode).getHierarchyOf((EnumeratedType)elementToSearch);
		}else if (elementToSearch instanceof MethodUnit) {
			path = KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverCodeHierarchyComplete(withHashCode).getHierarchyOf((MethodUnit)elementToSearch);
		}

		return path;
	}

	/**
	 * @author Landi
	 * @param from
	 * @param structureDrifts
	 * @return
	 */
	public static AbstractStructureElement getOriginalStructureElement(KDMEntity elementToSearch, StructureModel originalStructure) {
		AbstractStructureElement original = null;
		for (AbstractStructureElement originalElement : originalStructure.getStructureElement()) {
			original = getOriginalStructureElement((AbstractStructureElement)elementToSearch, originalElement);
			if(original != null){
				break;
			}
		}
		return original;
	}

	/**
	 * @author Landi
	 * @param elementToSearch
	 * @param originalElement
	 * @return
	 */
	private static AbstractStructureElement getOriginalStructureElement(AbstractStructureElement elementToSearch, AbstractStructureElement originalElement) {
		String pathElementToSearch = getPathFromStructureElement(elementToSearch);
		String pathOriginalElement = getPathFromStructureElement(originalElement);
		if(pathOriginalElement == null){
			return null;
		}
		if(pathElementToSearch.equalsIgnoreCase(pathOriginalElement)){
			return originalElement;			
		}
		AbstractStructureElement original = null;

		for (AbstractStructureElement child : originalElement.getStructureElement()) {
			original = getOriginalStructureElement(elementToSearch, child);
			if (original != null) {
				break;
			}
		}

		return original;
	}

	/**
	 * @author Landi
	 * @param elementToSearch
	 * @return
	 */
	public static String getPathFromStructureElement(AbstractStructureElement elementToSearch) {
		String path = null;

		path = KDMRecoverStructureHierarchyJavaFactory.eINSTANCE.createRecoverStructureHierarchyModel().getHierarchyOf(elementToSearch);

		return path;
	}

	/**
	 * @author Landi
	 * @param plannedArchitecture
	 * @return 
	 * @return
	 */
	public static List<AggregatedRelationship> getAllAggregateds(StructureModel model) {
		return KDMStructureReaderJavaFactory.eINSTANCE.createKDMAggregatedRelationshipReader().getAllFrom(model);
	}

	/**
	 * @author Landi
	 * @param from
	 * @param to
	 * @param aggregatedsActualArchitecture
	 * @return
	 */
	public static List<AggregatedRelationship> getSpecificAggregated(KDMEntity from, KDMEntity to,
			List<AggregatedRelationship> aggregatedsActualArchitecture) {

		List<AggregatedRelationship> aggregatedsFound = new ArrayList<AggregatedRelationship>();

		for (AggregatedRelationship aggregatedRelationship : aggregatedsActualArchitecture) {

			if(getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getFrom()).
					equalsIgnoreCase(getPathFromStructureElement((AbstractStructureElement) from)) 

					&& getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getTo()).
					equalsIgnoreCase(getPathFromStructureElement((AbstractStructureElement) to))){
				aggregatedsFound.add(aggregatedRelationship);
			}

		}

		return aggregatedsFound;
	}


	/**
	 * @author Landi
	 * @param structureModel 
	 * @param i
	 */
	public static void removeAggregatedRelationshipWithDensityEquals(StructureModel structureModel, int density) {

		for (AbstractStructureElement abstractStructureElement : structureModel.getStructureElement()) {

			removeAggregatedRelationshipWithDensityEquals(abstractStructureElement, density);

		}
	}

	/**
	 * @author Landi
	 * @param abstractStructureElement
	 * @param density
	 */
	private static void removeAggregatedRelationshipWithDensityEquals(AbstractStructureElement abstractStructureElement,
			int density) {

		for (Iterator<AggregatedRelationship> iterator = abstractStructureElement.getInAggregated().iterator(); iterator.hasNext();) {
			AggregatedRelationship aggregatedRelationship = (AggregatedRelationship) iterator.next();
			if(aggregatedRelationship.getDensity() == density){
				iterator.remove();
			}
		}
		for (Iterator<AggregatedRelationship> iterator = abstractStructureElement.getOutAggregated().iterator(); iterator.hasNext();) {
			AggregatedRelationship aggregatedRelationship = (AggregatedRelationship) iterator.next();
			if(aggregatedRelationship.getDensity() == density){
				iterator.remove();
			}
		}
		for (Iterator<AggregatedRelationship> iterator = abstractStructureElement.getAggregated().iterator(); iterator.hasNext();) {
			AggregatedRelationship aggregatedRelationship = (AggregatedRelationship) iterator.next();
			if(aggregatedRelationship.getDensity() == density){
				iterator.remove();
			}
		}

		if(abstractStructureElement.getStructureElement().size() > 0){
			for (AbstractStructureElement abstractStructureElementChild : abstractStructureElement.getStructureElement()) {
				removeAggregatedRelationshipWithDensityEquals(abstractStructureElementChild, density);
			}
		}
	}

	/**
	 * @author Landi
	 * @param violations
	 */
	public static void removeAggregatedRelationshipToFromEquals(StructureModel structureModel) {
		for (AbstractStructureElement abstractStructureElement : structureModel.getStructureElement()) {

			removeAggregatedRelationshipToFromEquals(abstractStructureElement);

		}
	}

	/**
	 * @author Landi
	 * @param abstractStructureElement
	 * @param density
	 */
	private static void removeAggregatedRelationshipToFromEquals(AbstractStructureElement abstractStructureElement) {

		for (Iterator<AggregatedRelationship> iterator = abstractStructureElement.getInAggregated().iterator(); iterator.hasNext();) {
			AggregatedRelationship aggregatedRelationship = (AggregatedRelationship) iterator.next();
			if(aggregatedRelationship.getFrom() == null || aggregatedRelationship.getTo() == null || 
					getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getFrom()).
					equalsIgnoreCase(getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getTo()))){
				iterator.remove();
			}
		}
		for (Iterator<AggregatedRelationship> iterator = abstractStructureElement.getOutAggregated().iterator(); iterator.hasNext();) {
			AggregatedRelationship aggregatedRelationship = (AggregatedRelationship) iterator.next();
			if(aggregatedRelationship.getFrom() == null || aggregatedRelationship.getTo() == null ||
					getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getFrom()).
					equalsIgnoreCase(getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getTo()))){
				iterator.remove();
			}
		}
		for (Iterator<AggregatedRelationship> iterator = abstractStructureElement.getAggregated().iterator(); iterator.hasNext();) {
			AggregatedRelationship aggregatedRelationship = (AggregatedRelationship) iterator.next();
			if(aggregatedRelationship.getFrom() == null || aggregatedRelationship.getTo() == null ||
					getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getFrom()).
					equalsIgnoreCase(getPathFromStructureElement((AbstractStructureElement) aggregatedRelationship.getTo()))){
				iterator.remove();
			}
		}
		if(abstractStructureElement.getStructureElement().size() > 0){
			for (AbstractStructureElement abstractStructureElementChild : abstractStructureElement.getStructureElement()) {
				removeAggregatedRelationshipToFromEquals(abstractStructureElementChild);
			}
		}
	}
	
	/**
	 * @author Landi
	 * @param text
	 */
	public static String updateInstanceToHasType(String kdmPath) {

		Segment segmentOriginal = GenericMethods.readSegmentFromPath(kdmPath);

		Segment segmentCompleto = KDMComplementsRelationshipFactory.eINSTANCE.createHasTypeComplements().complementsRelationOf(segmentOriginal);

		GenericMethods.serializeSegment(kdmPath.replace(".xmi", "-hastype.xmi"), segmentCompleto);

		return kdmPath.replace(".xmi", "-hastype.xmi");
	}

	/**
	 * @author Landi
	 */
	public static List<String> readFile(String path) {
		try{
			List<String> file = new ArrayList<>();
			BufferedReader input = new BufferedReader(new FileReader(path));
			// for each line
			for(String line = input.readLine(); line != null; line = input.readLine()) {
				file.add(line);
			}
			input.close();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
