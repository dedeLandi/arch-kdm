/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.architecturalCompilanceChecking;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import br.ufscar.kdm_manager.core.filters.validateFilter.factory.ValidateFilterJavaFactory;
import br.ufscar.kdm_manager.core.filters.validateFilter.interfaces.ValidateFilter;
import br.ufscar.kdm_manager.core.readers.modelReader.factory.KDMModelReaderJavaFactory;

/**
 * @author Landi
 *
 */
public class ArchitecturalCompilanceChecking {
	
	private Segment plannedArchitecture;
	private Segment actualArchitectureMapped;
	
	private StructureModel structureDrifts = null;
	
	private IProgressMonitor monitorProgress;

	/**
	 * 
	 */
	public ArchitecturalCompilanceChecking(Segment plannedArchitecture, Segment actualArchitectureMapped, IProgressMonitor monitorProgress) {
		this.plannedArchitecture = plannedArchitecture;
		this.actualArchitectureMapped = actualArchitectureMapped;
		this.monitorProgress = monitorProgress;
	}
	
	/**
	 * @author Landi
	 * @return
	 * @throws InterruptedException 
	 */
	public StructureModel executeAcc() throws InterruptedException {
		monitorProgress.beginTask("Processing the Architectural Compilance Checking", 100);
		monitorProgress.worked(0);

		structureDrifts = StructureFactory.eINSTANCE.createStructureModel();
		structureDrifts.setName("violations");
		
		Collection<? extends AbstractStructureElement> copyOfAllStructureElements = this.getCopyStructureElementsOfActualArchitecture(this.actualArchitectureMapped);
		
		if(copyOfAllStructureElements == null){
			monitorProgress.setTaskName("Fail to copy elements from the actual architecture. Please, choose other XMI file.");
			return null;
		}
		
		structureDrifts.getStructureElement().addAll(copyOfAllStructureElements);
		
		
		//make the processing initiate here
		for (int i = 0; i < 10; i++) {
			Thread.sleep(500);
			monitorProgress.worked(i*10);
		}

		monitorProgress.done();
		return structureDrifts;
	}

	/**
	 * @author Landi
	 * @param actualArchitecture 
	 * @return
	 */
	private Collection<? extends AbstractStructureElement> getCopyStructureElementsOfActualArchitecture(Segment actualArchitecture) {
		
		ValidateFilter<?, ?> filter = ValidateFilterJavaFactory.eINSTANCE.createValidateFilterNameOfKDMFramework("");
		Map<String, List<StructureModel>> allStructureModelActualArchitecture = KDMModelReaderJavaFactory.eINSTANCE.createKDMStructureModelReaderWithFilter(filter).getAllFromSegment(actualArchitecture);
		
		if(allStructureModelActualArchitecture.keySet().size() == 1){
			for (String key : allStructureModelActualArchitecture.keySet()) {
				if(allStructureModelActualArchitecture.get(key).size() == 1){
					Collection<AbstractStructureElement> elementsToCopy = allStructureModelActualArchitecture.get(key).get(0).getStructureElement();
					return EcoreUtil.copyAll(elementsToCopy);
				}
			}
		}
		
		return null;
		
	}
	
}
