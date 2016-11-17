package br.ufscar.arch_kdm.ui.dialogs;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SelectionModelDialog extends Dialog{

	private static String textSelection = "Select one model to realize the operation:";

	private KDMModel selectedModel;

	private Tree treeModelOptions;

	private Object models;

	public SelectionModelDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);

		Label lblSelectOneModel = new Label(container, SWT.NONE);
		lblSelectOneModel.setText(textSelection);

		treeModelOptions = new Tree(container, SWT.BORDER);
		treeModelOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		fillTree();
		
		return container;
	}

	@SuppressWarnings("unchecked")
	private void fillTree() {
		if(models instanceof Map<?,?>){

			Map<String, List<KDMModel>> allModels = (Map<String, List<KDMModel>>) models;
			fillTree(allModels);

		}else if(models instanceof List<?>){

			List<KDMModel> allModels = (List<KDMModel>) models;
			fillTree(allModels);
		}
	}

	// override method to use "Login" as label for the OK button
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Select", true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		if(validateSelection()){
			TreeItem[] selection = treeModelOptions.getSelection();
			for (TreeItem treeItem : selection) {
				this.selectedModel = (KDMModel) treeItem.getData();
			}
			super.okPressed();
		}
	}

	private boolean validateSelection() {
		if(treeModelOptions.getSelection().length <= 0){
			MultiStatus status = new MultiStatus("br.ufscar.ARCHREF-KDM.ui",
					IStatus.ERROR, "Model unselected", new Throwable());
			ErrorDialog.openError(this.getShell(), "Error", "Select one model to continue.", status);
			return false;
		}else{
			return true;
		}
	}

	public <T> void fillTreeOptions(T models) {
		this.models = models;
	}

	private void fillTree(Map<String, List<KDMModel>> allModels) {
		for (String nameModel : allModels.keySet()) {
			int modelNumber = 1;
			TreeItem treeItem = null;
			for (KDMModel model : allModels.get(nameModel)) {
				treeItem = new TreeItem(treeModelOptions, 0);
				String name = model.getName() == null ? "anonymous" : model.getName();
				treeItem.setText(nameModel + " [" + name + "] " + modelNumber);
				treeItem.setData(model);
				modelNumber++;
			}
		}
	}

	private void fillTree(List<KDMModel> allModels) {
		int modelNumber = 1;
		TreeItem treeItem = null;
		for (KDMModel kdmModel : allModels) {
			treeItem = new TreeItem(treeModelOptions, 0);
			String name = kdmModel.getName() == null ? "anonymous" : kdmModel.getName();
			treeItem.setText(name + " [" + modelNumber + "]");
			treeItem.setData(kdmModel);
			modelNumber++;
		}
	}

	public KDMModel getSelectedModel() {
		return this.selectedModel;
	}

	public static void setTextSelection(String textSelection) {
		SelectionModelDialog.textSelection = textSelection;
	}
}
