package br.ufscar.ARCH_KDM.windows;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import br.ufscar.ARCH_KDM.actions.ArchitectureComplianceChecking;

public class SetPathFilesXMI {

	protected Shell shlMappingArchitectureElementsFileSelection;
	
	private Text tPathPlanned;
	private Text tPathActual;
	
	private String pathFileSelect = "";

	private String calledFrom = "";
	
	public SetPathFilesXMI(String calledFrom) {
		this.calledFrom  = calledFrom;
	}
	
	/**
	 * Open the window.
	 * @param pathFileSelect 
	 */
	public void open(String pathFileSelect) {
		if(!"".equalsIgnoreCase(this.calledFrom)){
			this.pathFileSelect = pathFileSelect;
			Display display = Display.getDefault();
			createContents();
			shlMappingArchitectureElementsFileSelection.open();
			shlMappingArchitectureElementsFileSelection.layout();
			while (!shlMappingArchitectureElementsFileSelection.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}else{
			MessageDialog.openError(shlMappingArchitectureElementsFileSelection, "Error", "Error to open the window. Please check from who called.");
		}
		
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		
		shlMappingArchitectureElementsFileSelection = new Shell();
		shlMappingArchitectureElementsFileSelection.setSize(650, 171);
		if("MapArchitectureElementsToCodeElements".equalsIgnoreCase(this.calledFrom)){
			shlMappingArchitectureElementsFileSelection.setText("Mapping Architecture Elements");
		}else if("ArchitectureComplianceChecking".equalsIgnoreCase(this.calledFrom)){
			shlMappingArchitectureElementsFileSelection.setText("Architectural Compilance Checking");
		}else{
			shlMappingArchitectureElementsFileSelection.setText("Not Informed");
		}
		shlMappingArchitectureElementsFileSelection.setLayout(new GridLayout(3, false));
		
		Label lblPlannedArchitecturexmi = new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		lblPlannedArchitecturexmi.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPlannedArchitecturexmi.setText("Planned Architecture (XMI)");
		
		tPathPlanned = new Text(shlMappingArchitectureElementsFileSelection, SWT.BORDER);
		tPathPlanned.setEditable(false);
		tPathPlanned.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tPathPlanned.setText(this.pathFileSelect.replace("\\", "/").replace("file:/", ""));
		
		Button bSearchPlanned = new Button(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		bSearchPlanned.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				searchPlannedFile();
			}
		});
		bSearchPlanned.setText("Search");
		new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		
		Label lblModiscoActualArchitecture = new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		lblModiscoActualArchitecture.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		if("MapArchitectureElementsToCodeElements".equalsIgnoreCase(this.calledFrom)){
			lblModiscoActualArchitecture.setText("MoDisco Actual Architecture (XMI)");
		}else if("ArchitectureComplianceChecking".equalsIgnoreCase(this.calledFrom)){
			lblModiscoActualArchitecture.setText("Actual Architecture Mapped (XMI)");
		}else{
			lblModiscoActualArchitecture.setText("Not Informed");
		}
		
		tPathActual = new Text(shlMappingArchitectureElementsFileSelection, SWT.BORDER);
		tPathActual.setEditable(false);
		tPathActual.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button bSearchActual = new Button(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		bSearchActual.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				searchActualFile();
			}
		});
		bSearchActual.setText("Search");
		new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		new Label(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		
		Button bContinue = new Button(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		bContinue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if("MapArchitectureElementsToCodeElements".equalsIgnoreCase(calledFrom)){
					continueToMap();
				}else if("ArchitectureComplianceChecking".equalsIgnoreCase(calledFrom)){
					continueToACC();
				}else{
					MessageDialog.openError(shlMappingArchitectureElementsFileSelection, "Error", "Error to open the window. Please check from who called.");
				}
			}
		});
		bContinue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bContinue.setText("Continue");
		
		Button bCancel = new Button(shlMappingArchitectureElementsFileSelection, SWT.NONE);
		bCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				cancelOperation();
			}
		});
		bCancel.setText("Cancel");
		

	}

	private void cancelOperation() {
		boolean cancel = false;
		if("MapArchitectureElementsToCodeElements".equalsIgnoreCase(calledFrom)){
			cancel = MessageDialog.openConfirm(shlMappingArchitectureElementsFileSelection, "Map Architectural Elements", "Are you sure?");
		}else if("ArchitectureComplianceChecking".equalsIgnoreCase(calledFrom)){
			cancel = MessageDialog.openConfirm(shlMappingArchitectureElementsFileSelection, "Architectural Compilance Checking", "Are you sure?");
		}else{
			cancel = MessageDialog.openConfirm(shlMappingArchitectureElementsFileSelection, "Not Informed", "Are you sure?");
		}
		
		if(cancel){
			this.shlMappingArchitectureElementsFileSelection.dispose();
		}
	}

	private void continueToMap() {
//		String kdmFilePath = this.file.getLocationURI().toString();
//		ReadingKDMFile readingKDM = new ReadingKDMFile();				
//		Segment segment = readingKDM.load(kdmFilePath);
//		readingKDM.setSegmentMain(segment);
		if(validateFields()){
			
			MappingArchitectureElements.setPathPlannedArchitecture(tPathPlanned.getText());
			MappingArchitectureElements.setPathActualArchitecture(tPathActual.getText());
			MappingArchitectureElements.setPathSelectedFile(this.pathFileSelect);
			
			this.shlMappingArchitectureElementsFileSelection.dispose();
			
			MappingArchitectureElements mappingArchitectureElements = new MappingArchitectureElements();
			mappingArchitectureElements.open();
			
		}
	}
	
	private void continueToACC() {
//		String kdmFilePath = this.file.getLocationURI().toString();
//		ReadingKDMFile readingKDM = new ReadingKDMFile();				
//		Segment segment = readingKDM.load(kdmFilePath);
//		readingKDM.setSegmentMain(segment);
		if(validateFields()){

			ArchitectureComplianceChecking.setPathPlannedArchitecture(tPathPlanned.getText());
			ArchitectureComplianceChecking.setPathActualMappedArchitecture(tPathActual.getText());
			ArchitectureComplianceChecking.setPathSelectedFile(this.pathFileSelect);
			
			this.shlMappingArchitectureElementsFileSelection.dispose();

			ArchitectureComplianceChecking architectureComplianceChecking = new ArchitectureComplianceChecking();
			architectureComplianceChecking.executeACC();
			
		}
	}

	private boolean validateFields() {
		boolean canContinue = true;
		
		if("".equalsIgnoreCase(tPathPlanned.getText())){
			MessageDialog.openError(shlMappingArchitectureElementsFileSelection, "Map Architectural Elements", "Please, select the planned architecture to continue.");
			canContinue = false;
		}else if("".equalsIgnoreCase(tPathActual.getText())){
			MessageDialog.openError(shlMappingArchitectureElementsFileSelection, "Map Architectural Elements", "Please, select the actual architecture to continue.");
			canContinue = false;
		}
		
		
		return canContinue;
	}

	private void searchPlannedFile() {
		FileDialog fd = new FileDialog(shlMappingArchitectureElementsFileSelection, SWT.OPEN);
		fd.setText("Open");
		fd.setFilterPath("C:/");
		String[] filterExt = new String[]{"*.xmi"};
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if(selected == null){
			tPathPlanned.setText("");
		}else{
			System.out.println("Path of the XMI planned file:" + selected);
			tPathPlanned.setText(selected.replace("\\", "/").replace("file:", ""));
		}
	}
	
	private void searchActualFile() {
		FileDialog fd = new FileDialog(shlMappingArchitectureElementsFileSelection, SWT.OPEN);
		fd.setText("Open");
		fd.setFilterPath("C:/");
		String[] filterExt = new String[]{"*.xmi"};
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		if(selected == null){
			tPathActual.setText("");
		}else{
			System.out.println("Path of the XMI actual file:" + selected);
			tPathActual.setText(selected.replace("\\", "/").replace("file:", ""));
		}
	}
	
}
