/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.ui.util;

import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import br.ufscar.arch_kdm.ui.dialogs.SelectionModelDialog;

/**
 * @author Landi
 *
 */
public class InterfaceGenericMethods {

	/**
	 * 
	 * @author Landi
	 * @param models
	 * @param textInterface
	 * @param shell 
	 * @return
	 */
	public static <T> KDMModel dialogWhatModelUse(T models, String textInterface, Shell shell) {
		SelectionModelDialog.setTextSelection(textInterface);
		SelectionModelDialog dialog = new SelectionModelDialog(shell);
		dialog.fillTreeOptions(models);
		// get the new values from the dialog
		if (dialog.open() == Window.OK) {
			return dialog.getSelectedModel();
		}
		return null;
	}

}
