/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.architecturalCompilanceChecking;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
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
		
		monitorProgress.setTaskName("Preparing the XMI to make the Architectural Compilance Checking...");
		structureDrifts = this.prepareAcc();
		
		monitorProgress.setTaskName("Executing the Architectural Compilance Checking...");
		structureDrifts = this.acc(structureDrifts, structurePlannedArchitecture);
		
		//make the processing initiate here
		for (int i = 0; i < 10; i++) {
			Thread.sleep(500);
			monitorProgress.setTaskName("Task : " + (i*10));
			monitorProgress.worked(i*10);
		}

		return structureDrifts;
	}

	/**
	 * @author Landi
	 * @return
	 */
	private StructureModel prepareAcc() {
		monitorProgress.setTaskName("Creating container..");
		structureDrifts = StructureFactory.eINSTANCE.createStructureModel();
		structureDrifts.setName("violations");
		
		monitorProgress.setTaskName("Copying the structural elements...");
		Collection<AbstractStructureElement> elementsToCopy = this.structurePlannedArchitecture.getStructureElement();
		Collection<AbstractStructureElement> copyOfAllStructureElements = EcoreUtil.copyAll(elementsToCopy);
		if(copyOfAllStructureElements == null){
			monitorProgress.setTaskName("Fail to copy elements from the actual architecture. Please, choose other XMI file.");
			return null;
		}
		structureDrifts.getStructureElement().addAll(copyOfAllStructureElements);

		monitorProgress.setTaskName("Cleaning wrong aggregated relationships...");
		structureDrifts = GenericClean.cleanAggregateds(structureDrifts);
		
		monitorProgress.setTaskName("Updating the mapping of the architecture...");
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
		// TODO Auto-generated method stub
		return violations;
	}
	
}
