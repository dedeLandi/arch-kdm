/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.util;

import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.kdm_manager.core.exceptions.KDMFileException;
import br.ufscar.kdm_manager.core.loads.factory.KDMFileReaderFactory;
import br.ufscar.kdm_manager.core.readers.modelReader.factory.KDMModelReaderJavaFactory;
import br.ufscar.kdm_manager.core.serializes.factory.KDMFileSerializeFactory;

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
	
}
