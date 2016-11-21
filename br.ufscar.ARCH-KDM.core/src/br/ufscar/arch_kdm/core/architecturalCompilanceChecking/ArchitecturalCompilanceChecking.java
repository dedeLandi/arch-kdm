/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.architecturalCompilanceChecking;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.arch_kdm.core.mapping.MapArchitecture;
import br.ufscar.arch_kdm.core.util.GenericClean;
import br.ufscar.arch_kdm.core.util.GenericCopy;
import br.ufscar.arch_kdm.core.util.GenericMethods;

/**
 * @author Landi
 *
 */
public class ArchitecturalCompilanceChecking {
	
	private StructureModel structureDrifts = null;
	private StructureModel structureActualArchitecture = null;
	private StructureModel structurePlannedArchitecture = null;
	
	private CodeModel codeActualArchitecture = null;
	
	private IProgressMonitor monitorProgress;
	private int monitorProgressPercent = 0;
	

	/**
	 * 
	 * @param plannedArchitecture
	 * @param actualArchitectureMapped
	 * @param codeActualArchitecture
	 * @param monitorProgress
	 */
	public ArchitecturalCompilanceChecking(Segment plannedArchitecture, Segment actualArchitectureMapped, CodeModel codeActualArchitecture, IProgressMonitor monitorProgress) {
		this.structurePlannedArchitecture = GenericMethods.getStructureArchitecture("PlannedArchitecture", plannedArchitecture);
		this.structureActualArchitecture = GenericMethods.getStructureArchitecture("CompleteMap", actualArchitectureMapped);
		this.codeActualArchitecture = codeActualArchitecture;
		
		this.monitorProgress = monitorProgress;
	}
	
	/**
	 * @author Landi
	 * @return
	 * @throws InterruptedException 
	 */
	public StructureModel executeAcc() throws InterruptedException {
		
		updateMonitor("Preparing the XMI to make the Architectural Compilance Checking...");
		structureDrifts = this.prepareAcc();
		
		updateMonitor("Executing the Architectural Compilance Checking...");
		structureDrifts = this.acc(structureDrifts, structurePlannedArchitecture);
		
		return structureDrifts;
	}

	private void updateMonitor(String text) throws InterruptedException {
		monitorProgress.subTask(text);
		Thread.sleep(500);
		monitorProgressPercent = monitorProgressPercent +10;
		monitorProgress.worked(monitorProgressPercent);
	}

	/**
	 * @author Landi
	 * @return
	 * @throws InterruptedException 
	 */
	private StructureModel prepareAcc() throws InterruptedException {
		updateMonitor("Creating container...");
//		structureDrifts = StructureFactory.eINSTANCE.createStructureModel();
		structureDrifts = EcoreUtil.copy(this.structurePlannedArchitecture);
		structureDrifts.setName("violations");
		
//		updateMonitor("Copying the structural elements...");
//		Collection<AbstractStructureElement> elementsToCopy = this.structurePlannedArchitecture.getStructureElement();
//		Collection<AbstractStructureElement> copyOfAllStructureElements = EcoreUtil.copyAll(elementsToCopy);
//		if(copyOfAllStructureElements == null){
//			monitorProgress.subTask("Fail to copy elements from the actual architecture. Please, choose other XMI file.");
//			Thread.sleep(1500);
//			return null;
//		}
//		structureDrifts.getStructureElement().addAll(copyOfAllStructureElements);

		updateMonitor("Cleaning wrong aggregated relationships...");
		structureDrifts = GenericClean.cleanAggregateds(structureDrifts);
		
		
		updateMonitor("Updating the mapping of the architecture...");
		structureDrifts = GenericCopy.copyImplementation(structureDrifts, structureActualArchitecture, codeActualArchitecture);

		structureDrifts = new MapArchitecture(structureDrifts).mapCompleteArchitecture();
		
		return structureDrifts;
	}

	/**
	 * @author Landi
	 * @param violations
	 * @param plannedArchitecture
	 * @return
	 */
	private StructureModel acc(StructureModel violations, StructureModel plannedArchitecture) {
		
		List<AggregatedRelationship> aggregatedsThatCanExists = GenericMethods.getAllAggregateds(plannedArchitecture);
		
		List<AggregatedRelationship> aggregatedsActualArchitecture = GenericMethods.getAllAggregateds(violations);
		
		for (AggregatedRelationship aggregatedThatCanExist : aggregatedsThatCanExists) {
			KDMEntity from = aggregatedThatCanExist.getFrom();
			KDMEntity to = aggregatedThatCanExist.getTo();
			List<KDMRelationship> relationsThatCanExist = aggregatedThatCanExist.getRelation();
			
			List<AggregatedRelationship> aggregatedsToAvaliate = GenericMethods.getSpecificAggregated(from, to, aggregatedsActualArchitecture);
			
			for (AggregatedRelationship aggregatedToAvaliate : aggregatedsToAvaliate) {
				
				excludeRelationshipType(relationsThatCanExist, aggregatedToAvaliate);
				
			}
			
		}
		
		GenericMethods.removeAggregatedRelationshipWithDensityEquals(violations, 0);
		
		GenericMethods.removeAggregatedRelationshipToFromEquals(violations);
		
		return violations;
	}

	/**
	 * @author Landi
	 * @param relationsThatCanExist
	 * @param actualRelations
	 * @return 
	 */
	private void excludeRelationshipType(List<KDMRelationship> relationshipToExclude, AggregatedRelationship aggregatedToAvaliate) {
		
		for (KDMRelationship exclude : relationshipToExclude) {
			
			for (Iterator<KDMRelationship> iterator = aggregatedToAvaliate.getRelation().iterator(); iterator.hasNext();) {
				KDMRelationship kdmRelationship = (KDMRelationship) iterator.next();
				
				if(exclude.getClass().equals(kdmRelationship.getClass())){
					iterator.remove();
					int value = aggregatedToAvaliate.getDensity() - 1;
					aggregatedToAvaliate.setDensity(value);
				}
			}
			
		}
	}
	
}
