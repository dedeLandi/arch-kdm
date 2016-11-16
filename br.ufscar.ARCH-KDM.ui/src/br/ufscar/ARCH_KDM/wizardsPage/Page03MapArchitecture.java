package br.ufscar.ARCH_KDM.wizardsPage;

import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import br.ufscar.ARCH_KDM.dialogs.SelectionModelDialog;
import br.ufscar.ARCH_KDM.mapping.MapArchitecture;
import br.ufscar.ARCH_KDM.util.IconsType;
import br.ufscar.ARCH_KDM.wizards.ArchKDMWizard;
import br.ufscar.kdm_manager.core.readers.modelReader.factory.KDMModelReaderJavaFactory;
import br.ufscar.kdm_manager.core.serializes.factory.KDMFileSerializeFactory;

public class Page03MapArchitecture extends WizardPage {

	private Tree treeArchitecturalElements = null;
	private Tree treeCodeElements = null;
	private Tree treeElementsMapped = null;

	private StructureModel plannedArchitecture = null;
	private StructureModel completeMap = null;

	private CodeModel actualArchitecture = null;
	private Button bRemoveMap;
	private Button bMap;

	/**
	 * Create the wizard.
	 */
	public Page03MapArchitecture() {
		super("page03");
		setTitle("Architectural Refactoring Wizard");
		setDescription("Map the actual architecture with the planned architecture.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, true));

		Label lArchitecturalElements = new Label(container, SWT.NONE);
		lArchitecturalElements.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lArchitecturalElements.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lArchitecturalElements.setText("Architectural Elements");

		Label lCodeElements = new Label(container, SWT.NONE);
		lCodeElements.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lCodeElements.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lCodeElements.setText("Code Elements");

		treeArchitecturalElements = new Tree(container, SWT.BORDER);
		GridData gd_treeArchitecturalElements = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeArchitecturalElements.minimumHeight = 100;
		treeArchitecturalElements.setLayoutData(gd_treeArchitecturalElements);

		treeCodeElements = new Tree(container, SWT.BORDER);
		GridData gd_treeCodeElements = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeCodeElements.minimumHeight = 100;
		treeCodeElements.setLayoutData(gd_treeCodeElements);

		bMap = new Button(container, SWT.NONE);
		bMap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mapElements();
			}
		});
		GridData gd_bMap = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_bMap.widthHint = 150;
		bMap.setLayoutData(gd_bMap);
		bMap.setText("Map Element");

		bRemoveMap = new Button(container, SWT.NONE);
		GridData gd_bRemoveMap = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_bRemoveMap.widthHint = 150;
		bRemoveMap.setLayoutData(gd_bRemoveMap);
		bRemoveMap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				removeMappedElement();
			}
		});
		bRemoveMap.setText("Remove Mapped Element");

		Label lElementsMapped = new Label(container, SWT.NONE);
		lElementsMapped.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lElementsMapped.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		lElementsMapped.setText("Elements Alread Mapped");

		treeElementsMapped = new Tree(container, SWT.BORDER);
		GridData gd_treeElementsMapped = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_treeElementsMapped.minimumHeight = 90;
		treeElementsMapped.setLayoutData(gd_treeElementsMapped);

		Button bSaveCompleteMap = new Button(container, SWT.NONE);
		GridData gd_bSaveCompleteMap = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd_bSaveCompleteMap.widthHint = 150;
		bSaveCompleteMap.setLayoutData(gd_bSaveCompleteMap);
		bSaveCompleteMap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(validateAllElementsMap()){
					executeCompleteMap();
					save();
				}
			}
		});
		bSaveCompleteMap.setText("Save Complete Map");

	}

	protected void executeCompleteMap() {
		if(completeMap == null){
			executeInitialMap();
			((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture().getModel().add(completeMap);
			completeMap = new MapArchitecture(completeMap).mapCompleteArchitecture();
//			completeMap = new MapArchitecture(completeMap, ((ArchitecturalRefactoringWizard)this.getWizard()).getSegmentActualArchitecture()).mapCompleteArchitecture();
			disableButtons();
		}
	}
	
	private void executeInitialMap() {
		completeMap = this.plannedArchitecture;
		completeMap.setName("CompleteMap");
		completeMap = new MapArchitecture(completeMap).cleanAggregateds();

		TreeItem[] items = treeElementsMapped.getItems();
		for (TreeItem treeItem : items) {
			Object data[] = (Object[]) treeItem.getData();

			new MapArchitecture().mapInitialArchitecture((AbstractStructureElement)data[0], (KDMEntity) data[1]);

		}

	}

	private void disableButtons() {
		bMap.setEnabled(false);
		bRemoveMap.setEnabled(false);
	}
	private void enableButtons() {
		bMap.setEnabled(true);
		bRemoveMap.setEnabled(true);
	}

	private boolean validateAllElementsMap() {
		boolean allArchitecturalElementsMap = false;

		TreeItem[] itemsArchitectural = treeArchitecturalElements.getItems();

		for (TreeItem architecturalItem : itemsArchitectural) {
			allArchitecturalElementsMap = validateAllElementsMap(architecturalItem);
			if(!allArchitecturalElementsMap){
				break;
			}
		}

		if(!allArchitecturalElementsMap){
			setErrorMessage("Please, map all architectural elements.");
			MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			messageBox.setMessage("Do you really want to continue with missing mappings?");
			messageBox.setText("Architectural elements mapping");
			int response = messageBox.open();
			if (response == SWT.YES){
				allArchitecturalElementsMap = true;
			}
		}

		return allArchitecturalElementsMap;
	}

	private boolean validateAllElementsMap(TreeItem parentItem) {
		boolean found = false;
		TreeItem[] itemsMapped = treeElementsMapped.getItems();
		for (TreeItem mappedItem : itemsMapped) {
			if(mappedItem.getText().contains(parentItem.getText())){
				found = true;
			}
		}
		if(!found){
			return found;
		}
		for (TreeItem childItem : parentItem.getItems()) {
			found = validateAllElementsMap(childItem);
			if(!found){
				return found;
			}
		}
		return found;
	}

	protected void save() {

		String KDMPath = "file:///" + ((ArchKDMWizard)this.getWizard()).getPathActualArchitecture().replace(".xmi", "-mapped.xmi");

		KDMFileSerializeFactory.eINSTANCE.createKDMFileSerializeFromSegment().serializeFromObject(KDMPath, ((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture());
	}

	protected void removeMappedElement() {
		this.completeMap = null;
		if(validateSelectionElementMapped()){
			TreeItem[] selection = treeElementsMapped.getSelection();
			for (TreeItem treeItem : selection) {
				treeItem.dispose();
			}
		}
	}

	private boolean validateSelectionElementMapped() {
		if(treeElementsMapped.getSelection().length <= 0){
			setErrorMessage("Please, select one mapping to remove.");
			return false;
		}else{
			setErrorMessage(null); // clear error message.
			return true;
		}
	}

	protected void mapElements() {
		this.completeMap = null;
		if(validateSelectionArchCode()){
			String text = "";
			Object data[] = new Object[2];

			TreeItem[] selection2 = treeArchitecturalElements.getSelection();
			for (TreeItem treeItem : selection2) {
				text = text.concat(treeItem.getText());
				data[0] = treeItem.getData();
			}
			text = text.concat(" was mapped to ");
			TreeItem[] selection = treeCodeElements.getSelection();
			for (TreeItem treeItem : selection) {
				text = text.concat(treeItem.getText());
				data[1] = treeItem.getData();
			}

			if(elementCanBeMap(text)){
				TreeItem treeItemParent = new TreeItem(treeElementsMapped, 0);
				treeItemParent.setText(text);
				treeItemParent.setData(data);
			}
		}
	}

	private boolean validateSelectionArchCode() {
		if(treeArchitecturalElements.getSelection().length <= 0
				|| treeCodeElements.getSelection().length <= 0){
			setErrorMessage("Please, select one architectural element and one code element.");
			return false;
		}else{
			setErrorMessage(null); // clear error message.
			return true;
		}
	}

	private boolean elementCanBeMap(String text) {

		for (TreeItem treeItem : treeElementsMapped.getItems()) {
			if(text.equalsIgnoreCase(treeItem.getText())){
				setErrorMessage("These elements alread been mapped.");
				return false;
			}else{
				setErrorMessage(null); // clear error message.
			}
		}
		return true;
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
		return validateCanFlip();
	}

	/**
	 * @author Landi
	 * @return
	 */
	private boolean validateCanFlip() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IWizardPage getNextPage() {
//		((Page03SelectDrift) getWizard().getPage("page03")).fillTDrifts();
		return getWizard().getPage("page03");
	}

	/**
	 * @author Landi
	 */
	public void fillPlannedArchitecture() {
		String textInterface = "Select the planned architectural model:";
		treeArchitecturalElements.removeAll();
		enableButtons();
		Segment segmentPlannedArchitecture = ((ArchKDMWizard)this.getWizard()).getSegmentPlannedArchitecture();
		Map<String, java.util.List<StructureModel>> allStructure = KDMModelReaderJavaFactory.eINSTANCE.createKDMStructureModelReader().getAllFromSegment(segmentPlannedArchitecture);
		plannedArchitecture = null;
		if(allStructure.keySet().size() == 1){
			for (String key : allStructure.keySet()) {
				if(allStructure.get(key).size() == 1){
					plannedArchitecture = allStructure.get(key).get(0); 
				}else{
					plannedArchitecture = (StructureModel) dialogWhatModelUse(allStructure.get(key), textInterface);
				}
			}
		}else{
			plannedArchitecture = (StructureModel) dialogWhatModelUse(allStructure, textInterface);
		}

		TreeItem treeItemParent = null;
		for (AbstractStructureElement abstractStructureElement : plannedArchitecture.getStructureElement()) {

			treeItemParent = new TreeItem(treeArchitecturalElements, 0);
			treeItemParent.setImage(IconsType.STRUCTURAL_ELEMENT.getImage());
			treeItemParent.setText("[" + abstractStructureElement.eClass().getName() + "] " + abstractStructureElement.getName());
			treeItemParent.setData(abstractStructureElement);

			this.fillPlannedArchitecture(treeItemParent, abstractStructureElement);

		}


	}

	private <T> KDMModel dialogWhatModelUse(T models, String textInterface) {
		SelectionModelDialog.setTextSelection(textInterface);
		SelectionModelDialog dialog = new SelectionModelDialog(null);
		dialog.fillTreeOptions(models);
		// get the new values from the dialog
		if (dialog.open() == Window.OK) {
			return dialog.getSelectedModel();
		}
		return null;
	}

	private void fillPlannedArchitecture(TreeItem treeItemParent, AbstractStructureElement parentElement) {
		TreeItem treeItemChild = null;
		for (AbstractStructureElement childElement : parentElement.getStructureElement()) {

			treeItemChild = new TreeItem(treeItemParent, 0);
			treeItemChild.setImage(IconsType.STRUCTURAL_ELEMENT.getImage());
			treeItemChild.setText("[" + childElement.eClass().getName() + "] " + childElement.getName());
			treeItemChild.setData(childElement);

			this.fillPlannedArchitecture(treeItemChild, childElement);
		}

	}

	/**
	 * @author Landi
	 */
	public void fillActualArchitecture() {
		String textInterface = "Select the actual architecture model:";
		treeCodeElements.removeAll();
		enableButtons();
		Segment segmentPlannedArchitecture = ((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture();
		Map<String, java.util.List<CodeModel>> allCode = KDMModelReaderJavaFactory.eINSTANCE.createKDMCodeModelReader().getAllFromSegment(segmentPlannedArchitecture);
		actualArchitecture = null;
		if(allCode.keySet().size() == 1){
			for (String key : allCode.keySet()) {
				if(allCode.get(key).size() == 1){
					actualArchitecture = allCode.get(key).get(0); 
				}else{
					actualArchitecture = (CodeModel) dialogWhatModelUse(allCode.get(key), textInterface);
				}
			}
		}else{
			actualArchitecture = (CodeModel) dialogWhatModelUse(allCode, textInterface);
		}

		for (AbstractCodeElement abstractCodeElement : actualArchitecture.getCodeElement()) {

			TreeItem treeItemParent = new TreeItem(treeCodeElements, 0);
			treeItemParent.setImage(IconsType.getImageByElement(abstractCodeElement));
			treeItemParent.setText("[" + abstractCodeElement.eClass().getName() + "] " + abstractCodeElement.getName());
			treeItemParent.setData(abstractCodeElement);

			if(abstractCodeElement instanceof Package){
				this.fillActualArchitecture(treeItemParent, (Package)abstractCodeElement);
			}

		}
	}

	private void fillActualArchitecture(TreeItem treeItemParent, Package parentElement) {

		for (AbstractCodeElement childElement : parentElement.getCodeElement()) {
			TreeItem treeItemChild = new TreeItem(treeItemParent, 0);
			treeItemChild.setImage(IconsType.getImageByElement(childElement));
			treeItemChild.setText("[" + childElement.eClass().getName() + "] " + childElement.getName());
			treeItemChild.setData(childElement);

			if(childElement instanceof Package){
				this.fillActualArchitecture(treeItemChild, (Package) childElement);
			}

		}	
	}
}
