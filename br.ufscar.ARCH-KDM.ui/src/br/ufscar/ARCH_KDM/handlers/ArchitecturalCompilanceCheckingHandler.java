package br.ufscar.ARCH_KDM.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import br.ufscar.ARCH_KDM.wizards.ArchKDMWizard;

public class ArchitecturalCompilanceCheckingHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public ArchitecturalCompilanceCheckingHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ArchKDMWizard wizard = new ArchKDMWizard();
		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        WizardDialog dialog = new WizardDialog(activeShell, wizard);
        dialog.setBlockOnOpen(true);
        int returnCode = dialog.open();
        if(returnCode == Dialog.OK)
          System.out.println("Wizard OK");
        else
          System.out.println("Wizard Cancelled");
		return null;
	}
}
