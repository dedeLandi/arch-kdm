/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.ui.wizardsPage;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Landi
 *
 */
public class Page05ViewLog extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public Page05ViewLog() {
		super("page05_1");
		setTitle("Architectural Compilance Checking Wizard");
		setDescription("Fail log of the ACC");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		return null;
	}

	/**
	 * @author Landi
	 * @param log
	 */
	public void setLog(StringBuilder log) {
		// TODO Auto-generated method stub
		
	}

}
