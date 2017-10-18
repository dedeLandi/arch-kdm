package br.ufscar.arch_kdm.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConfigureClusteringAlgoMatrixSimilarityDialog extends Dialog{

	private static String textSelection = "Select the configuration of the DBScan Algo:";

	private Text tEpsilon;
	private Text tMinCluster;

	private String minCluster;

	private String epsilon;

	public ConfigureClusteringAlgoMatrixSimilarityDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);

		Label lblSelectOneModel = new Label(container, SWT.NONE);
		lblSelectOneModel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblSelectOneModel.setText(textSelection);
		
		Label lblEpsilon = new Label(container, SWT.NONE);
		lblEpsilon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEpsilon.setText("Epsilon");
		
		tEpsilon = new Text(container, SWT.BORDER);
		tEpsilon.setText("0.45");
		tEpsilon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblMinimumNumbercluster = new Label(container, SWT.NONE);
		lblMinimumNumbercluster.setText("Minimum number(Cluster)");
		
		tMinCluster = new Text(container, SWT.BORDER);
		tMinCluster.setText("1");
		tMinCluster.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		return container;
	}

	// override method to use "Login" as label for the OK button
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnOk = createButton(parent, IDialogConstants.OK_ID, "Select", true);
		btnOk.setText("OK");
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		if(validateConfiguration()){
			this.epsilon = tEpsilon.getText();
			this.minCluster = tMinCluster.getText();
			super.okPressed();
		}
	}

	private boolean validateConfiguration() {
		if("".equalsIgnoreCase(tEpsilon.getText())){
			MultiStatus status = new MultiStatus("br.ufscar.ARCHREF-KDM.ui",
					IStatus.ERROR, "Algorithm unconfigured", new Throwable());
			ErrorDialog.openError(this.getShell(), "Error", "Configure the parameters of the algorithm.", status);
			return false;
		}else if("".equalsIgnoreCase(tMinCluster.getText())){
			MultiStatus status = new MultiStatus("br.ufscar.ARCHREF-KDM.ui",
					IStatus.ERROR, "Algorithm unconfigured", new Throwable());
			ErrorDialog.openError(this.getShell(), "Error", "Configure the parameters of the algorithm.", status);
			return false;
		}else{
			return true;
		}
	}

	public String getEpsilon() {
		return this.epsilon;
	}
	
	public String getMinCluster() {
		return this.minCluster;
	}

	public static void setTextSelection(String textSelection) {
		ConfigureClusteringAlgoMatrixSimilarityDialog.textSelection = textSelection;
	}
}
