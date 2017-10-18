/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.ui.util;

import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import br.ufscar.arch_kdm.ui.dialogs.ConfigureClusteringAlgoTOFROMDialog;
import br.ufscar.arch_kdm.ui.dialogs.ConfigureClusteringAlgoMatrixSimilarityDialog;
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
	
	/**
	 * @author Landi
	 */
	public static String dialogWhatAlgoMatrixSimilarityConfiguration(String textInterface, Shell shell) {
		ConfigureClusteringAlgoMatrixSimilarityDialog.setTextSelection(textInterface);
		ConfigureClusteringAlgoMatrixSimilarityDialog dialog = new ConfigureClusteringAlgoMatrixSimilarityDialog(shell);
		// get the new values from the dialog
		if (dialog.open() == Window.OK) {
			return "-E " + dialog.getEpsilon() +
					" -M " + dialog.getMinCluster();
		}
		return null;
	}
	
	/**
	 * @author Landi
	 */
	public static String dialogWhatAlgoTOFROMConfiguration(String textInterface, Shell shell) {
		ConfigureClusteringAlgoTOFROMDialog.setTextSelection(textInterface);
		ConfigureClusteringAlgoTOFROMDialog dialog = new ConfigureClusteringAlgoTOFROMDialog(shell);
		// get the new values from the dialog
		if (dialog.open() == Window.OK) {
			return "GenerateUML:" + dialog.getUML() + ";"
					+ "GenerateUMLCode:" + dialog.getUMLCode()  + ";"
							+ "GenerateUMLStructure:" + dialog.getUMLStructure()  + ";"
					; 
		}
		return null;
	}

}
