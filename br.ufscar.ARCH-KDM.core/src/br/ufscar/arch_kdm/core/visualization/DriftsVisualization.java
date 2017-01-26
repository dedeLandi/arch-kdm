/**
 * @author André
 * 
 */
package br.ufscar.arch_kdm.core.visualization;

import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.kdm_manager.core.complements.complementRelationship.factory.KDMComplementsRelationshipFactory;
import br.ufscar.kdm_manager.core.filters.validateFilter.factory.KDMValidateFilterJavaFactory;
import br.ufscar.kdm_manager.core.filters.validateFilter.interfaces.KDMValidateFilter;
import br.ufscar.kdm_manager.core.readers.modelReader.factory.KDMModelReaderJavaFactory;
import br.ufscar.kdm_manager.core.readers.structureReader.factory.KDMStructureReaderJavaFactory;
import br.ufscar.kdm_manager.core.recovers.recoverCodeHierarchy.factory.KDMRecoverCodeHierarchyJavaFactory;
import info.debatty.java.stringsimilarity.MetricLCS;

/**
 * @author André
 *
 */
public class DriftsVisualization {

	public static void main(String[] args) {
		//-----------------------------------------------------------------------------------------------------------------
		//---------------------Putting hasType in Segment
		String kdmPathHasType = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.core"
				+ "\\src\\br\\ufscar\\arch_kdm\\core\\tests\\SystemExampleMVC-SimplesComDesvios_kdm.xmi";
		
		Segment segmentOriginal = GenericMethods.readSegmentFromPath(kdmPathHasType);
		
		Segment segmentCompleto = KDMComplementsRelationshipFactory.eINSTANCE.createHasTypeComplements().complementsRelationOf(segmentOriginal);

		GenericMethods.serializeSegment(kdmPathHasType.replace(".xmi", "-hastype.xmi"), segmentCompleto);
		
		//-----------------------------------------------------------------------------------------------------------------
		
		//-----------------------------------------------------------------------------------------------------------------
		//--------------------- Reading drifts
		String kdmPath = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.core"
				+ "\\src\\br\\ufscar\\arch_kdm\\core\\tests\\SystemExampleMVC-SimplesComDesvios_kdm-hastype-violations.xmi";

		Segment segmentViolation = GenericMethods.readSegmentFromPath(kdmPath);

		
		
		KDMValidateFilter<?, ?> filter = KDMValidateFilterJavaFactory.eINSTANCE.createValidateFilterNameOfKDMFramework("violations");
		Map<String, List<StructureModel>> violationsMap = KDMModelReaderJavaFactory.eINSTANCE.createKDMStructureModelReaderWithFilter(filter ).getAllFromSegment(segmentViolation);
		
		for (String key : violationsMap.keySet()) {
			System.out.println("Structure Model name: " + key);
			List<AggregatedRelationship> aggregateds = KDMStructureReaderJavaFactory.eINSTANCE.createKDMAggregatedRelationshipReader().getAllFrom(violationsMap.get(key).get(0));
			System.out.println("--------------------------------------------------------------");
			System.out.println("----------------------Violations------------------------------");
			System.out.println("--------------------------------------------------------------");
			for (AggregatedRelationship aggregatedRelationship : aggregateds) {
				System.out.println("Aggregated from " + aggregatedRelationship.getFrom().getName() + 
						" to " + aggregatedRelationship.getTo().getName() +
						" with " + aggregatedRelationship.getDensity() + " violations");
			}
			System.out.println("--------------------------------------------------------------");
			
			for (AggregatedRelationship aggregatedRelationship : aggregateds) {
				System.out.println("--------------------------------------------------------------");
				System.out.println("----------------------Violations from " + aggregatedRelationship.getFrom().getName() + 
						" to " + aggregatedRelationship.getTo().getName() + "------------------------------");
				System.out.println("--------------------------------------------------------------");

				for (KDMRelationship violation : aggregatedRelationship.getRelation()) {
					System.out.println("Violation type: " + violation.eClass().getName());
					System.out.println("Violation path: " + KDMRecoverCodeHierarchyJavaFactory.eINSTANCE.createRecoverRelationshipHierarchyComplete().getHierarchyOf(violation));
				}
				
				System.out.println("--------------------------------------------------------------");
			}
			
		}
		
		
		String teste1 = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.ui\\src\\br\\ufscar\\arch_kdm\\ui\\tests\\SystemExampleMVC-SimplesComDesvios_kdm-violations.xmi"; 
		String teste2 = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\br.ufscar.ARCH-KDM.ui\\src\\br\\ufscar\\";
		String teste3 = "C:\\Java\\workspaceMestradoMars64\\arch-kdm\\\br\\ufscar\\arch_kdm\\ui\\tests\\SystemExampleMVC-SimplesComDesvios_kdm-violations.xmi";

		System.out.println("---------------------  Longest common subsequence ");
		MetricLCS lcs = new MetricLCS();
		System.out.println("-> distance");
		System.out.println("1x1 " + lcs.distance(teste1, teste1));
		System.out.println("1x2 " + lcs.distance(teste1, teste2));
		System.out.println("1x3 " + lcs.distance(teste1, teste3));

	}
}
