package br.ufscar.arch_kdm.ui.wizardsPage;

import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
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

import br.ufscar.arch_kdm.core.mapping.MapArchitecture;
import br.ufscar.arch_kdm.core.util.GenericClean;
import br.ufscar.arch_kdm.core.util.GenericMethods;
import br.ufscar.arch_kdm.ui.util.IconsType;
import br.ufscar.arch_kdm.ui.util.InterfaceGenericMethods;
import br.ufscar.arch_kdm.ui.wizards.ArchKDMWizard;

public class Page03MapArchitecture extends WizardPage {

	private Tree treeArchitecturalElements = null;
	private Tree treeCodeElements = null;
	private Tree treeElementsMapped = null;

	private StructureModel completeMap = null;

	private Button bRemoveMap;
	private Button bMap;
	private boolean alreadyMap = false;
	private Button bSaveCompleteMap;

	/**
	 * Create the wizard.
	 */
	public Page03MapArchitecture() {
		super("page03");
		setTitle("Mapping Architectural Elements in Code Elements");
		setDescription("Mapping the code elements in the elements of hte Planned Architecture (PA)");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(2, true));

		Label lArchitecturalElements = new Label(container, SWT.NONE);
		lArchitecturalElements.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lArchitecturalElements.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lArchitecturalElements.setText("Architectural Elements of the PA");

		Label lCodeElements = new Label(container, SWT.NONE);
		lCodeElements.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lCodeElements.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lCodeElements.setText("Code Elements");

		treeArchitecturalElements = new Tree(container, SWT.BORDER);
		GridData gd_treeArchitecturalElements = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeArchitecturalElements.heightHint = 100;
		gd_treeArchitecturalElements.minimumHeight = 100;
		treeArchitecturalElements.setLayoutData(gd_treeArchitecturalElements);

		treeCodeElements = new Tree(container, SWT.BORDER);
		GridData gd_treeCodeElements = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeCodeElements.heightHint = 100;
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
		lElementsMapped.setText("Mapped Elements");

		treeElementsMapped = new Tree(container, SWT.BORDER);
		GridData gd_treeElementsMapped = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_treeElementsMapped.heightHint = 100;
		gd_treeElementsMapped.minimumHeight = 90;
		treeElementsMapped.setLayoutData(gd_treeElementsMapped);

		bSaveCompleteMap = new Button(container, SWT.NONE);
		GridData gd_bSaveCompleteMap = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd_bSaveCompleteMap.widthHint = 150;
		bSaveCompleteMap.setLayoutData(gd_bSaveCompleteMap);
		bSaveCompleteMap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(validateAllElementsMap()){
					executeCompleteMap();
					save();
					disableButtons();
				}
			}
		});
		bSaveCompleteMap.setText("Save Complete Map");

	}

	protected void executeCompleteMap() {
		executeInitialMap();
		((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture().getModel().add(completeMap);
		completeMap = new MapArchitecture(completeMap).mapCompleteArchitecture();
	}

	private void executeInitialMap() {
		completeMap.setName("CompleteMap");
		completeMap = GenericClean.cleanAggregateds(completeMap);

		TreeItem[] items = treeElementsMapped.getItems();
		for (TreeItem treeItem : items) {
			Object data[] = (Object[]) treeItem.getData();

			new MapArchitecture().mapInitialArchitecture((AbstractStructureElement)data[0], (KDMEntity) data[1]);

		}

	}

	private void disableButtons() {
		bMap.setEnabled(false);
		bRemoveMap.setEnabled(false);
		bSaveCompleteMap.setEnabled(false);
	}
	private void enableButtons() {
		bMap.setEnabled(true);
		bRemoveMap.setEnabled(true);
		bSaveCompleteMap.setEnabled(true);
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
		String KDMPath = ((ArchKDMWizard)this.getWizard()).getPathActualArchitecture().replace(".xmi", "-mapped.xmi");


		GenericMethods.serializeSegment(KDMPath, ((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture());

		((ArchKDMWizard)this.getWizard()).setPathActualArchitectureCompleteMap(KDMPath);

		((ArchKDMWizard)this.getWizard()).readCompleteMap();

		this.setAlreadyMap(true);

		getWizard().getContainer().updateButtons();

	}

	protected void removeMappedElement() {
		cleanCompleteMap();
		if(validateSelectionElementMapped()){
			TreeItem[] selection = treeElementsMapped.getSelection();
			for (TreeItem treeItem : selection) {
				treeItem.dispose();
			}
		}
	}

	private void cleanCompleteMap() {
		this.setAlreadyMap(false);
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
		cleanCompleteMap();
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
		return isAlreadyMap();
	}

	@Override
	public IWizardPage getNextPage() {
		((Page04ArchitecturalCompilanceChecking) getWizard().getPage("page04")).setPreviousPage("page03");
		return getWizard().getPage("page04");
	}

	/**
	 * @author Landi
	 */
	public void fillPlannedArchitecture() {
		String textInterface = "Select the planned architectural model:";
		treeArchitecturalElements.removeAll();
		completeMap = null;
		enableButtons();
		Map<String, java.util.List<StructureModel>> allStructure = GenericMethods.getAllStructure(((ArchKDMWizard)this.getWizard()).getSegmentPlannedArchitecture());
		StructureModel plannedArchitecture = null;
		if(allStructure.keySet().size() == 1){
			for (String key : allStructure.keySet()) {
				if(allStructure.get(key).size() == 1){
					plannedArchitecture = allStructure.get(key).get(0); 
				}else{
					plannedArchitecture = (StructureModel) InterfaceGenericMethods.dialogWhatModelUse(allStructure.get(key), textInterface, null);
				}
			}
		}else{
			plannedArchitecture = (StructureModel) InterfaceGenericMethods.dialogWhatModelUse(allStructure, textInterface, null);
		}
		completeMap = EcoreUtil.copy(plannedArchitecture);

		TreeItem treeItemParent = null;
		for (AbstractStructureElement abstractStructureElement : completeMap.getStructureElement()) {

			treeItemParent = new TreeItem(treeArchitecturalElements, 0);
			treeItemParent.setImage(IconsType.STRUCTURAL_ELEMENT.getImage());
			treeItemParent.setText("[" + abstractStructureElement.eClass().getName() + "] " + abstractStructureElement.getName());
			treeItemParent.setData(abstractStructureElement);

			this.fillPlannedArchitecture(treeItemParent, abstractStructureElement);

		}


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
		Map<String, java.util.List<CodeModel>> allCode = GenericMethods.getAllCode(((ArchKDMWizard)this.getWizard()).getSegmentActualArchitecture());
		((ArchKDMWizard)this.getWizard()).setCodeActualArchitecture(null);
		if(allCode.keySet().size() == 1){
			for (String key : allCode.keySet()) {
				if(allCode.get(key).size() == 1){
					((ArchKDMWizard)this.getWizard()).setCodeActualArchitecture(allCode.get(key).get(0)); 
				}else{
					((ArchKDMWizard)this.getWizard()).setCodeActualArchitecture((CodeModel) InterfaceGenericMethods.dialogWhatModelUse(allCode.get(key), textInterface, null));
				}
			}
		}else{
			((ArchKDMWizard)this.getWizard()).setCodeActualArchitecture((CodeModel) InterfaceGenericMethods.dialogWhatModelUse(allCode, textInterface, null));
		}

		for (AbstractCodeElement abstractCodeElement : ((ArchKDMWizard)this.getWizard()).getCodeActualArchitecture().getCodeElement()) {

			TreeItem treeItemParent = new TreeItem(treeCodeElements, 0);
			treeItemParent.setImage(IconsType.getImageByElement(abstractCodeElement));
			treeItemParent.setText("[" + abstractCodeElement.eClass().getName() + "] " + abstractCodeElement.getName());
			treeItemParent.setData(abstractCodeElement);

			if(abstractCodeElement instanceof Package){
				this.fillActualArchitecture(treeItemParent, (Package)abstractCodeElement);
			}

		}
	}
	
	private TreeItem addChildNode(TreeItem treeItemParent, AbstractCodeElement childElement) {
		TreeItem treeItemChild = new TreeItem(treeItemParent, 0);
		treeItemChild.setImage(IconsType.getImageByElement(childElement));
		treeItemChild.setText("[" + childElement.eClass().getName() + "] " + childElement.getName());
		treeItemChild.setData(childElement);
		
		return treeItemChild;
	}

	private void fillActualArchitecture(TreeItem treeItemParent, CodeItem parentElement) {		
		if (parentElement instanceof ClassUnit)
			for (AbstractCodeElement childElement : ((ClassUnit)parentElement).getCodeElement())
				if (childElement instanceof MethodUnit)
					addChildNode(treeItemParent, childElement);
		
		if (parentElement instanceof Package)
			for (AbstractCodeElement childElement : ((Package)parentElement).getCodeElement()) 
				if (childElement instanceof Package || childElement instanceof ClassUnit || childElement instanceof InterfaceUnit)
					this.fillActualArchitecture(addChildNode(treeItemParent, childElement), (CodeItem) childElement);					
	}

	/**
	 * @return the alreadyMap
	 */
	public boolean isAlreadyMap() {
		return alreadyMap;
	}

	/**
	 * @param alreadyMap the alreadyMap to set
	 */
	public void setAlreadyMap(boolean alreadyMap) {
		this.alreadyMap = alreadyMap;
	}
}
