package br.ufscar.ARCH_KDM.wizards;

import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import br.ufscar.ARCH_KDM.wizardsPage.Page01Introduction;
import br.ufscar.ARCH_KDM.wizardsPage.Page03MapArchitecture;
import br.ufscar.ARCH_KDM.wizardsPage.Page02SelectFileWithDrift;
import br.ufscar.kdm_manager.core.exceptions.KDMFileException;
import br.ufscar.kdm_manager.core.loads.factory.KDMFileReaderFactory;

public class ArchKDMWizard extends Wizard {
	
	private Page01Introduction page1 = new Page01Introduction();
	private Page02SelectFileWithDrift page2 = new Page02SelectFileWithDrift();
	private Page03MapArchitecture page3 = new Page03MapArchitecture();

	
	private Segment segmentPlannedArchitecture = null;
	private Segment segmentActualArchitecture = null;
	
	private String pathPlannedArchitecture = null;
	private String pathActualArchitecture = null;
	
	private void cleanObjects() {
		segmentPlannedArchitecture = null;
		segmentActualArchitecture = null;
		
		setPathPlannedArchitecture(null);
		setPathActualArchitecture(null);
	}

	public ArchKDMWizard() {
		setWindowTitle("Architectural Compilance Checking Wizard");
		setNeedsProgressMonitor(true);
		cleanObjects();
	}


	@Override
	public void addPages() {
		addPage(page1);
		addPage(page2);
		addPage(page3);
	}

	@Override
	public boolean canFinish() {
		 if(getContainer().getCurrentPage() == page3){
			 return true;
		 }else{
			 return false;
		 }
	}
	
	@Override
	public boolean performFinish() {
		//usar o mensage dialog
		MessageDialog.open(MessageDialog.INFORMATION, getShell(), "Wizzard finished", "Wizzard finished", MessageDialog.INFORMATION);
		return true;
	}

	public boolean performCancel() {
		boolean ans = MessageDialog.openConfirm(getShell(), "Confirmation", "Are you sure to cancel the wizard?");
		if(ans)
			return true;
		else
			return false;
	}

	/**
	 * @return the setPlannedArchitecture
	 */
	public Segment getSegmentPlannedArchitecture() {
		return segmentPlannedArchitecture;
	}

	/**
	 * @param setPlannedArchitecture the setPlannedArchitecture to set
	 */
	public void setSegmentPlannedArchitecture(Segment setPlannedArchitecture) {
		this.segmentPlannedArchitecture = setPlannedArchitecture;
	}

	/**
	 * @return the setActualArchitecture
	 */
	public Segment getSegmentActualArchitecture() {
		return segmentActualArchitecture;
	}

	/**
	 * @param setActualArchitecture the setActualArchitecture to set
	 */
	public void setSegmentActualArchitecture(Segment setActualArchitecture) {
		this.segmentActualArchitecture = setActualArchitecture;
	}

	public String getPathPlannedArchitecture() {
		return pathPlannedArchitecture;
	}

	public void setPathPlannedArchitecture(String pathPlannedArchitecture) {
		this.pathPlannedArchitecture = pathPlannedArchitecture;
	}

	public String getPathActualArchitecture() {
		return pathActualArchitecture;
	}

	public void setPathActualArchitecture(String pathActualArchitecture) {
		this.pathActualArchitecture = pathActualArchitecture;
	}

	public void readSements() {
		try {
			this.segmentActualArchitecture = KDMFileReaderFactory.eINSTANCE.createKDMFileReaderToSegment().readFromPath(this.pathActualArchitecture);
			this.segmentPlannedArchitecture = KDMFileReaderFactory.eINSTANCE.createKDMFileReaderToSegment().readFromPath(this.pathPlannedArchitecture);
		} catch (KDMFileException e) {
			e.printStackTrace();
		}
	}

}
