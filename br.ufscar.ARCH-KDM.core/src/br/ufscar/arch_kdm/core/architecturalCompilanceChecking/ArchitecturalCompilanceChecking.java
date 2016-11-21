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
	
	private static StringBuilder messageLog = new StringBuilder();
	

	/**
	 * 
	 * @param plannedArchitecture
	 * @param actualArchitectureMapped
	 * @param codeActualArchitecture
	 * @param monitorProgress
	 * @param namePlannedStructure 
	 * @param nameActualStructure 
	 */
	public ArchitecturalCompilanceChecking(Segment plannedArchitecture, Segment actualArchitectureMapped, CodeModel codeActualArchitecture, 
			IProgressMonitor monitorProgress, String namePlannedStructure, String nameActualStructure) {
		this.structurePlannedArchitecture = GenericMethods.getStructureArchitecture(namePlannedStructure, plannedArchitecture);
		this.structureActualArchitecture = GenericMethods.getStructureArchitecture(nameActualStructure, actualArchitectureMapped);
		this.codeActualArchitecture = codeActualArchitecture;
		
		this.monitorProgress = monitorProgress;
		
	}
	
	/**
	 * @author Landi
	 * @return
	 * @throws InterruptedException 
	 */
	public StructureModel executeAcc(){
		
		updateMonitor("Preparing the XMI to make the Architectural Compilance Checking...");
		structureDrifts = this.prepareAcc();
		
		updateMonitor("Executing the Architectural Compilance Checking...");
		structureDrifts = this.acc(structureDrifts, structurePlannedArchitecture);
		
		return structureDrifts;
	}

	/**
	 * @author Landi
	 * @return
	 * @throws InterruptedException 
	 */
	private StructureModel prepareAcc(){
		updateMonitor("Creating container...");
		structureDrifts = EcoreUtil.copy(this.structurePlannedArchitecture);
		structureDrifts.setName("violations");
		
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
	 * @throws InterruptedException 
	 */
	private StructureModel acc(StructureModel violations, StructureModel plannedArchitecture){
		
		updateMonitor("Recovering planned architecture...");
		List<AggregatedRelationship> aggregatedsThatCanExists = GenericMethods.getAllAggregateds(plannedArchitecture);
		
		updateMonitor("Recovering actual architecture...");
		List<AggregatedRelationship> aggregatedsActualArchitecture = GenericMethods.getAllAggregateds(violations);
		
		updateMonitor("Executing the ACC...");
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
	
	/**
	 * @author Landi
	 * @throws InterruptedException 
	 */
	public static void appendMessageToLog(String message){
		messageLog.append("\n");
		messageLog.append(message);
		messageLog.append("\n");
	}
	
	/**
	 * @author Landi
	 * @param text
	 * @throws InterruptedException
	 */
	private void updateMonitor(String text) {
		monitorProgress.subTask(text);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		monitorProgressPercent = monitorProgressPercent +10;
		monitorProgress.worked(monitorProgressPercent);
		appendMessageToLog(text);
	}
	
	
	/**
	 * @return the messageLog
	 */
	public StringBuilder getMessageLog() {
		return messageLog;
	}
}
