/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.ui.wizardsPage;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import br.ufscar.arch_kdm.core.architecturalCompilanceChecking.ArchitecturalCompilanceChecking;
import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.arch_kdm.ui.wizards.ArchKDMWizard;

/**
 * @author Landi
 *
 */
public class Page04ArchitecturalCompilanceChecking extends WizardPage {

	private boolean canFlip = false;
	private boolean analisisComplete = false;
	protected Segment actualArchitecture;
	protected Segment plannedArchitecture;
	private ArchKDMWizard archKDMWizard;
	
	/**
	 * Create the wizard.
	 */
	public Page04ArchitecturalCompilanceChecking() {
		super("page04");
		setTitle("Architectural Compilance Checking Wizard");
		setDescription("");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		label.setText("For process this algorithm, possibly take a long time.\r\nIf you want to continue, click in the button below.");
		label.setAlignment(SWT.CENTER);
		
		Button btnInitiateProcessing = new Button(container, SWT.NONE);
		btnInitiateProcessing.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnInitiateProcessing.setText("Initiate Processing");
		btnInitiateProcessing.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				initiateRecommendation();
			}
		});
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		label_1.setText("Please, wait while is performed the process of conformance check.");
		label_1.setAlignment(SWT.CENTER);
	}

	private void initiateRecommendation() {
		archKDMWizard = (ArchKDMWizard)this.getWizard();
		
		try {
			// puts the data into a database ...
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {

					ArchitecturalCompilanceChecking acc = 
							new ArchitecturalCompilanceChecking(archKDMWizard.getSegmentPlannedArchitecture(), archKDMWizard.getSegmentActualArchitectureCompleteMap(), monitor);
					
					StructureModel violations = acc.executeAcc();
					
					if(violations != null){
						setErrorMessage(null);
						archKDMWizard.setStructureDrifts(violations);
						save();
						
						setCanFlip(true);
						setAnalisisComplete(true);
					}else{
						failToGenerateViolations();
					}
					
				}
			});
			
			getWizard().getContainer().updateButtons();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * @author Landi
	 */
	protected void failToGenerateViolations() {
		setErrorMessage("Please, fail to execute de compilance checking.");
		
	}

	/**
	 * @author Landi
	 */
	protected void save() {
		String KDMPath = "file:///" + ((ArchKDMWizard)this.getWizard()).getPathActualArchitecture().replace(".xmi", "-violations.xmi");
		GenericMethods.serializeSegment(KDMPath, ((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture());
	}

	@Override
	public IWizardPage getNextPage() {
		if(isAnalisisComplete()){
			return getWizard().getPage("page05");
		}else{
			return getWizard().getPage("page05_1");
		}
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return isCanFlip();
	}

	public boolean isCanFlip() {
		return canFlip;
	}

	public void setCanFlip(boolean canFlip) {
		this.canFlip = canFlip;
	}

	public boolean isAnalisisComplete() {
		return analisisComplete;
	}

	public void setAnalisisComplete(boolean analisisComplete) {
		this.analisisComplete = analisisComplete;
	}
	
}
