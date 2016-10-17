package br.ufscar.ARCH_KDM.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import br.ufscar.ARCH_KDM.util.ReadingKDMFile;
import br.ufscar.ARCH_KDM.windows.SetPathFilesXMI;


public class MapArchitectureElementsToCodeElements implements IObjectActionDelegate {

	private Shell shell;

	private IFile file;
	
	public static Segment plannedSegment;
	public static String kdmProjectPath = "";
	
	/**
	 * Constructor for ActionRecoveryArchitecture.
	 */
	public MapArchitectureElementsToCodeElements() {
		super();
	}
	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		String kdmFilePath = this.file.getLocationURI().toString();
		ReadingKDMFile readingKDM = new ReadingKDMFile();				
		Segment segment = readingKDM.load(kdmFilePath);
		readingKDM.setSegmentMain(segment);
		
		SetPathFilesXMI settingFileArchives = new SetPathFilesXMI("MapArchitectureElementsToCodeElements");
		settingFileArchives.open(this.file.getLocationURI().toString());
		
//		MappingArchitectureElements mappingArchitectureElements = new MappingArchitectureElements();
//		mappingArchitectureElements.open();
				
//		MessageDialog.openInformation(
//			shell,
//			"ProjetoMestradoFernandoChagas2",
//			"New Action was executed.");
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

		if (selection instanceof IStructuredSelection) {
			action.setEnabled(updateSelection((IStructuredSelection) selection));
		} else {
			action.setEnabled(false);
		}

	}

	public boolean updateSelection(IStructuredSelection selection) {
		for (Iterator<?> objects = selection.iterator(); objects.hasNext();) {
			Object object = AdapterFactoryEditingDomain.unwrap(objects.next());
			if (object instanceof IFile) {
				this.file = (IFile) object;
				return true;
			}
		}
		return false;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();

	}

}
