/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.ui.wizardsPage;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import br.ufscar.arch_kdm.core.architecturalCompilanceChecking.ArchitecturalCompilanceChecking;
import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.arch_kdm.ui.util.InterfaceGenericMethods;
import br.ufscar.arch_kdm.ui.wizards.ArchKDMWizard;

/**
 * @author Landi
 *
 */
public class Page04ArchitecturalCompilanceChecking extends WizardPage {

	private boolean canFlip = false;
	private boolean accComplete = false;
	protected Segment actualArchitecture;
	protected Segment plannedArchitecture;
	private ArchKDMWizard archKDMWizard;
	private String previousPage = "";
	private Button btnInitiateProcessing;
	protected StringBuilder accLog;
	private String nameActualStructure;
	private String namePlannedStructure;
	private boolean canExecuteACC = true;

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

		btnInitiateProcessing = new Button(container, SWT.NONE);
		btnInitiateProcessing.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnInitiateProcessing.setText("Initiate Processing");
		btnInitiateProcessing.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(isCanExecuteACC()){
					setErrorMessage(null);
					initiateAcc();
				}else{
					setErrorMessage("ACC already executed. Please, go to the next page.");
				}
			}
		});

		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		label_1.setText("Please, wait while is performed the process of conformance check.");
		label_1.setAlignment(SWT.CENTER);
	}

	private void initiateAcc() {
		archKDMWizard = (ArchKDMWizard)this.getWizard();

		try {
			// puts the data into a database ...
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {

					monitor.beginTask("Architectural Compilance Checking", 100);
					monitor.worked(0);

					if("page02".equalsIgnoreCase(previousPage)){
						accFromPage02(monitor);
					}else if("page03".equalsIgnoreCase(previousPage)){
						accFromPage03(monitor);
					}else{
						failInWizard();
					}
					monitor.done();
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
	protected void save() {
		String KDMPath = "file:///" + ((ArchKDMWizard)this.getWizard()).getPathActualArchitecture().replace(".xmi", "-violations.xmi");
		GenericMethods.serializeSegment(KDMPath, ((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture());
	}

	@Override
	public IWizardPage getNextPage() {
		if(isACCComplete()){
			return getWizard().getPage("page05");
		}else{
			((Page05ViewLog) getWizard().getPage("page05_1")).setLog(accLog);
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

	public boolean isACCComplete() {
		return accComplete;
	}

	public void setACCComplete(boolean analisisComplete) {
		this.accComplete = analisisComplete;
	}

	/**
	 * @author Landi
	 * @param string
	 */
	public void setPreviousPage(String previousPage) {
		this.previousPage  = previousPage;
		enableButtons();
	}

	/**
	 * @author Landi
	 * @param monitor
	 */
	private void accFromPage02(IProgressMonitor monitor) {
		nameActualStructure = "";
		namePlannedStructure = "";

		Display.getDefault().syncExec(new Runnable() {
			public void run() {

				String textInterface = "Select the planned architectural model:";
				Map<String, java.util.List<StructureModel>> allStructure = GenericMethods.getAllStructure(((ArchKDMWizard)getWizard()).getSegmentPlannedArchitecture());
				StructureModel plannedArchitecture = null;
				if(allStructure.keySet().size() == 1){
					for (String key : allStructure.keySet()) {
						if(allStructure.get(key).size() == 1){
							plannedArchitecture = allStructure.get(key).get(0); 
						}else{
							plannedArchitecture = (StructureModel) InterfaceGenericMethods.dialogWhatModelUse(allStructure.get(key), textInterface, getShell());
						}
					}
				}else{
					plannedArchitecture = (StructureModel) InterfaceGenericMethods.dialogWhatModelUse(allStructure, textInterface, getShell());
				}



				String textInterface2 = "Select the actual architectural model (Structure Model):";
				Map<String, java.util.List<StructureModel>> allStructure1 = GenericMethods.getAllStructure(((ArchKDMWizard)getWizard()).getSegmentActualArchitectureCompleteMap());
				StructureModel actualArchitecture = null;
				if(allStructure1.keySet().size() == 1){
					for (String key : allStructure1.keySet()) {
						if(allStructure1.get(key).size() == 1){
							actualArchitecture = allStructure1.get(key).get(0); 
						}else{
							actualArchitecture = (StructureModel) InterfaceGenericMethods.dialogWhatModelUse(allStructure1.get(key), textInterface2, getShell());
						}
					}
				}else{
					actualArchitecture = (StructureModel) InterfaceGenericMethods.dialogWhatModelUse(allStructure1, textInterface2, getShell());
				}

				String textInterface3 = "Select the actual architecture model (Code Model):";
				Map<String, java.util.List<CodeModel>> allCode = GenericMethods.getAllCode(((ArchKDMWizard)getWizard()).getSegmentActualArchitectureCompleteMap());
				((ArchKDMWizard)getWizard()).setCodeActualArchitecture(null);
				if(allCode.keySet().size() == 1){
					for (String key : allCode.keySet()) {
						if(allCode.get(key).size() == 1){
							((ArchKDMWizard)getWizard()).setCodeActualArchitecture(allCode.get(key).get(0)); 
						}else{
							((ArchKDMWizard)getWizard()).setCodeActualArchitecture((CodeModel) InterfaceGenericMethods.dialogWhatModelUse(allCode.get(key), textInterface3, getShell()));
						}
					}
				}else{
					((ArchKDMWizard)getWizard()).setCodeActualArchitecture((CodeModel) InterfaceGenericMethods.dialogWhatModelUse(allCode, textInterface3, getShell()));
				}

				if(plannedArchitecture != null && actualArchitecture != null){
					namePlannedStructure = plannedArchitecture.getName();
					nameActualStructure = actualArchitecture.getName();
				}else{
					setErrorMessage("Select the planned and the actual architecture before continue.");
				}

			}
		});

		if(!"".equalsIgnoreCase(namePlannedStructure) && !"".equalsIgnoreCase(nameActualStructure)){
			if(((ArchKDMWizard)getWizard()).getCodeActualArchitecture() != null){
				executeACC(monitor, namePlannedStructure, nameActualStructure);
			}else{
				Display.getDefault().syncExec(new Runnable() {
					public void run() {	
						setErrorMessage("Select the actual code model before continue.");
					}
				});
			}
		}
	}

	/**
	 * 
	 * @author Landi
	 * @param monitor
	 */
	private void accFromPage03(IProgressMonitor monitor) {
		String namePlannedStructure = "PlannedArchitecture";
		String nameActualStructure = "CompleteMap";
		executeACC(monitor, namePlannedStructure, nameActualStructure);
	}

	/**
	 * 
	 * @author Landi
	 * @param monitor
	 * @param namePlannedStructure
	 * @param nameActualStructure
	 */
	private void executeACC(IProgressMonitor monitor, String namePlannedStructure, String nameActualStructure) {
		ArchitecturalCompilanceChecking acc = 
				new ArchitecturalCompilanceChecking(
						archKDMWizard.getSegmentPlannedArchitecture(), 
						archKDMWizard.getSegmentActualArchitectureCompleteMap(), 
						archKDMWizard.getCodeActualArchitecture(),
						monitor,
						namePlannedStructure,
						nameActualStructure);

		StructureModel violations = acc.executeAcc();

		if(violations != null){
			archKDMWizard.setStructureDrifts(violations);
			save();

			setCanFlip(true);
			setACCComplete(true);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					setErrorMessage(null);
					disableButtons();
				}
			});

		}else{
			ArchitecturalCompilanceChecking.appendMessageToLog("Fail to execute de compilance checking.");
			setACCComplete(false);
			accLog = acc.getMessageLog();
		}
	}

	/**
	 * @author Landi
	 */
	protected void disableButtons() {
		setCanExecuteAcc(false);
	}
	/**
	 * @author Landi
	 * @param b
	 */
	private void setCanExecuteAcc(boolean canExecuteACC) {
		this.canExecuteACC = canExecuteACC;
	}
	
	/**
	 * @return the canExecuteACC
	 */
	public boolean isCanExecuteACC() {
		return canExecuteACC;
	}

	/**
	 * @author Landi
	 */
	protected void enableButtons() {
		setCanExecuteAcc(true);
	}

	/**
	 * @author Landi
	 */
	private void failInWizard() {
		accLog.append("\n");
		accLog.append("Falha ao executar o plug-in");
		accLog.append("\n");
		setACCComplete(false);
	}

}
