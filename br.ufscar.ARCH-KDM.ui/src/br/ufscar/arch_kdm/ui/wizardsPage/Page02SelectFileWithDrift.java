package br.ufscar.arch_kdm.ui.wizardsPage;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.arch_kdm.ui.wizards.ArchKDMWizard;

public class Page02SelectFileWithDrift extends WizardPage {
	private Text tPathFilePlanned;
	private Text tPathFileActual;
	private Combo cbTypeActualArchitecture;
	private static final String ALREADY_MAPPED = "Architectural Elements Already Mapped";
	private static final String ORIGINAL_MAP = "Original (From Discover)";
	private Button ckHasType;

	/**
	 * Create the wizard.
	 */
	public Page02SelectFileWithDrift() {
		super("page02");
		setTitle("Architectural Compilance Checking Wizard");
		setDescription("Select the planned architecture file and the file containing the actual architecture.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		Label lblSelectionOfThe = new Label(container, SWT.NONE);
		lblSelectionOfThe.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		lblSelectionOfThe.setText("Selection of the files containing the planned architecture and the actual one. ");

		Label lFilePlannedArchitecture = new Label(container, SWT.NONE);
		lFilePlannedArchitecture.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lFilePlannedArchitecture.setText("File containing the \r\nplanned architecture");

		tPathFilePlanned = new Text(container, SWT.BORDER);
//		tPathFilePlanned.setText("/Users/Bruno/Desenvolvimento/EclipseWorkspace/ModiscoTargetApp/rules.xmi");
//		tPathFilePlanned.setText("C:\\JavaLab\\workspace\\arch-kdm\\br.ufscar.ARCH-KDM.ui\\src\\br\\ufscar\\arch_kdm\\ui\\tests\\archPlan.xmi");
		tPathFilePlanned.setEditable(false);
		GridData gd_tPathFilePlanned = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_tPathFilePlanned.widthHint = 100;
		tPathFilePlanned.setLayoutData(gd_tPathFilePlanned);

		Button bSearchPlanned = new Button(container, SWT.NONE);
		bSearchPlanned.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				chooseFilePlanned();
			}
		});
		bSearchPlanned.setText("Search");
		
		Label lblTypeOfActual = new Label(container, SWT.NONE);
		lblTypeOfActual.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTypeOfActual.setText("Type of Actual Architecture");
		
		cbTypeActualArchitecture = new Combo(container, SWT.READ_ONLY);
		cbTypeActualArchitecture.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//		cbTypeActualArchitecture.select(1);
		cbTypeActualArchitecture.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				getWizard().getContainer().updateButtons();
			}

		});

		Label lFileActualArchitecture = new Label(container, SWT.NONE);
		lFileActualArchitecture.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lFileActualArchitecture.setText("File containing the \r\nactual architecture");

		tPathFileActual = new Text(container, SWT.BORDER);
		//tPathFileActual.setText("/Users/Bruno/Desenvolvimento/EclipseWorkspace/ModiscoTargetApp/ModiscoTargetApp_kdm.xmi");
		//tPathFileActual.setText("C:\\JavaLab\\workspace\\arch-kdm\\br.ufscar.ARCH-KDM.ui\\src\\br\\ufscar\\arch_kdm\\ui\\tests\\SystemExampleMVC-SimplesComDesvios_kdm.xmi");
//		tPathFileActual.setText("C:\\JavaLab\\workspace\\arch-kdm\\br.ufscar.ARCH-KDM.ui\\src\\br\\ufscar\\arch_kdm\\ui\\tests\\SystemExampleMVC-SimplesComDesvios_kdm.xmi");
		tPathFileActual.setEditable(false);
		GridData gd_tPathFileActual = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_tPathFileActual.widthHint = 100;
		tPathFileActual.setLayoutData(gd_tPathFileActual);

		Button bSearchActual = new Button(container, SWT.NONE);
		bSearchActual.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				chooseFileActual();
			}
		});
		bSearchActual.setText("Search");
		
		ckHasType = new Button(container, SWT.CHECK);
		//ckHasType.setSelection(true);
		ckHasType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
		ckHasType.setText("Complete Actual Architecture With HasType");

		fillCbType();
	}

	protected void chooseFilePlanned() {

		setErrorMessage(null); // clear error message. 

		FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		fd.setText("Open");
		//fd.setFilterPath("C:/");
		String[] filterExt = new String[]{"*.xmi"};
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if(selected == null){
			tPathFilePlanned.setText("");
		}else{
			System.out.println("Path of the XMI planned file:" + selected);
			tPathFilePlanned.setText(selected);
		}
		getWizard().getContainer().updateButtons();
	}

	protected void chooseFileActual() {

		setErrorMessage(null); // clear error message. 

		FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		fd.setText("Open");
		//fd.setFilterPath("C:/");
		String[] filterExt = new String[]{"*.xmi"};
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if(selected == null){
			tPathFileActual.setText("");
		}else{
			System.out.println("Path of the XMI actual file:" + selected);
			tPathFileActual.setText(selected);
		}
		getWizard().getContainer().updateButtons();
	}

	private void fillCbType() {
		cbTypeActualArchitecture.removeAll();
		cbTypeActualArchitecture.add(ALREADY_MAPPED);
		cbTypeActualArchitecture.add(ORIGINAL_MAP);
	}

	private boolean validateCbType() {
		if(cbTypeActualArchitecture.getSelectionIndex() == -1) {  
			setErrorMessage("Select one type of actual architecture to continue.");
			return false;
		}else{
			setErrorMessage(null); // clear error message. 
			return true;
		}
	}

	private boolean validateTPathFilePlanned() {
		if("".equalsIgnoreCase(tPathFilePlanned.getText())) { 
			setErrorMessage("Select one file to continue. (Planned)");
			return false;
		}else{
			setErrorMessage(null); // clear error message. 
			return true;
		}
	}
	private boolean validateTPathFileActual() {
		if("".equalsIgnoreCase(tPathFileActual.getText())) { 
			setErrorMessage("Select one file to continue. (Actual)");
			return false;
		}else{
			setErrorMessage(null); // clear error message. 
			return true;
		}
	}
	
	

	@Override
	public void performHelp(){
		//TODO Desenvolver o help dessa pagina
	    Shell shell = new Shell(getShell());
	    shell.setText("My Custom Help !!");
	    shell.setLayout(new GridLayout());
	    shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Browser browser = new Browser(shell, SWT.NONE);
	    browser.setUrl("http://advanse.dc.ufscar.br/index.php/tools");
	    browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    shell.open();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return validateTPathFilePlanned() && validateTPathFileActual() && validateCbType();
	}

	@Override
	public IWizardPage getNextPage() {
		executeWizardPageFinalAction();
		if(isMapped()){
			((Page04ArchitecturalCompilanceChecking) getWizard().getPage("page04")).setPreviousPage("page02");
			return getWizard().getPage("page04");
		}else{
			((Page03MapArchitecture) getWizard().getPage("page03")).fillPlannedArchitecture();
			((Page03MapArchitecture) getWizard().getPage("page03")).fillActualArchitecture();
			return getWizard().getPage("page03");
		}
	}

	private void executeWizardPageFinalAction() {
		ArchKDMWizard archKDMWizard = (ArchKDMWizard) this.getWizard();
		
		if(ckHasType.getSelection()){
			String savedPath = GenericMethods.updateInstanceToHasType(this.tPathFileActual.getText());
			this.tPathFileActual.setText(savedPath);
		}
		
		archKDMWizard.setPathActualArchitecture(this.tPathFileActual.getText());
		archKDMWizard.setPathPlannedArchitecture(this.tPathFilePlanned.getText());
		
		archKDMWizard.readSements();
	}

	/**
	 * @author Landi
	 * @return
	 */
	private boolean isMapped() {
		return cbTypeActualArchitecture.getText().equalsIgnoreCase(ALREADY_MAPPED);
	}

}
