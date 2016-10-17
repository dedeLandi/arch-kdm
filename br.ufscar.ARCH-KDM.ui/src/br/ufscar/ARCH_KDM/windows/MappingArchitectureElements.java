package br.ufscar.ARCH_KDM.windows;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import br.ufscar.ARCH_KDM.model.MapItem;
import br.ufscar.ARCH_KDM.util.ReadingKDMFile;
import br.ufscar.ARCH_KDM.util.SWTResourceManager;

public class MappingArchitectureElements {

	protected Shell shlMappingArchitectureElements;
	
	private ReadingKDMFile readingKDMFile = new ReadingKDMFile();
	
	private static String pathPlannedArch = "";//"file:C:/TestsPlug-in/archKDM/planned.xmi";
	private static String pathActualArch = "";//"file:C:/TestsPlug-in/archKDM/newKDM.xmi";

	private static String pathOutputArch = "";//"file:C:/TestsPlug-in/archKDM/newKDM2.xmi"

	private static String outputFileName = "initialMap.xmi";
	
	private Segment TOBE;
	
	private Segment ASIS;
	
	private List listPlannedArchitecture;
	
	private Tree treeCodeElements;
	
	private List listOfMaps;
	
//	private ArrayList<Package> allPackagesTOBE;
	
	private ArrayList<Package> allPackagesASIS;

	private ArrayList<MapItem> allItensToMap = new ArrayList<MapItem>();
	
	private EList<AbstractStructureElement> structureElementsTOBE;
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlMappingArchitectureElements.open();
		shlMappingArchitectureElements.layout();
		while (!shlMappingArchitectureElements.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		this.TOBE = this.readingKDMFile.load(pathPlannedArch);
		this.ASIS = this.readingKDMFile.load(pathActualArch);
		
		StructureModel structureModelTOBE = readingKDMFile.getStructureModelPassingSegment(this.TOBE);
		
		//copiar elementos 
		
		//Collection copyAllAbstractStructureElementASIS = EcoreUtil.copyAll(structureModelTOBE.getStructureElement());
		
		//structureElementsTOBE.addAll(copyAllAbstractStructureElementASIS);
		
		structureElementsTOBE = structureModelTOBE.getStructureElement();
		
		//this.allPackagesTOBE = readingKDMFile.getAllPackages(this.TOBE);
		this.allPackagesASIS = readingKDMFile.getAllPackages(this.ASIS);
		
		
		shlMappingArchitectureElements = new Shell();
		shlMappingArchitectureElements.setSize(657, 472);
		shlMappingArchitectureElements.setText("Mapping Architecture Elements");
		
		Label lblArchitecturalElements = new Label(shlMappingArchitectureElements, SWT.NONE);
		lblArchitecturalElements.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblArchitecturalElements.setBounds(10, 10, 275, 21);
		lblArchitecturalElements.setText("Planned Architecture Elements");
		
		listPlannedArchitecture = new List(shlMappingArchitectureElements, SWT.BORDER);
		listPlannedArchitecture.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		fillListArchitecturalElements();		
		
		//list.setItems(new String[] {"layer - model", "layer - controller", "layer - view"});
		listPlannedArchitecture.setBounds(10, 37, 294, 198);
		
		listOfMaps = new List(shlMappingArchitectureElements, SWT.BORDER | SWT.H_SCROLL);
		listOfMaps.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		listOfMaps.setBounds(10, 268, 625, 123);
		
		Label lblArchitecturalElementsMapped = new Label(shlMappingArchitectureElements, SWT.NONE);
		lblArchitecturalElementsMapped.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblArchitecturalElementsMapped.setText("Architectural Elements Mapped\n");
		lblArchitecturalElementsMapped.setBounds(10, 241, 275, 21);
		
		treeCodeElements = new Tree(shlMappingArchitectureElements, SWT.BORDER | SWT.MULTI);
		treeCodeElements.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		treeCodeElements.setBounds(329, 37, 306, 197);
		
		fillCodeElements();
		
	
		
		Button btnMap = new Button(shlMappingArchitectureElements, SWT.NONE);
		btnMap.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				elementsMap();
			}
		});
		btnMap.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		btnMap.setBounds(541, 234, 94, 28);
		btnMap.setText("Map");
		
		Label lblCodeElements = new Label(shlMappingArchitectureElements, SWT.NONE);
		lblCodeElements.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblCodeElements.setBounds(329, 10, 138, 21);
		lblCodeElements.setText("Code Elements");
		
		Button btnDelete = new Button(shlMappingArchitectureElements, SWT.NONE);
		btnDelete.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				deleteSelectedMap();
			}
		});
		btnDelete.setText("Delete");
		btnDelete.setBounds(541, 397, 94, 28);
		
		Button btnGenerate = new Button(shlMappingArchitectureElements, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				saveElementsMapped();
			}
		});
		btnGenerate.setText("Generate");
		btnGenerate.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		btnGenerate.setBounds(10, 397, 94, 28);

	}

	private void fillCodeElements() {
		//MappingArchitectureElements.class.getResourceAsStream("package_obj.gif")
		Image packageImage = new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/package_obj.gif"));
		Image classImage = new Image(Display.getDefault(), getClass().getResourceAsStream("/icons/class_obj.gif"));
		
		String pathToGet = null;
		TreeItem treeItem0 = null;
		TreeItem treeItem11 = null;
		for (Package package1 : allPackagesASIS) {
			pathToGet = "";
			pathToGet =  readingKDMFile.getPathOfPackage(package1, pathToGet);
			treeItem0 = new TreeItem(treeCodeElements, 0);
			treeItem0.setImage(packageImage);
			
			
			String nameCorrected2 = getClassName(package1);
			
			treeItem0.setText("[" + nameCorrected2 + "] " + pathToGet);
			
			EList<AbstractCodeElement> allClassesAndInterfaces = readingKDMFile.getClassesAndInterfacesByPackage(package1);	
			for (AbstractCodeElement classOrInterface : allClassesAndInterfaces) {
				
				nameCorrected2 = getClassName(classOrInterface);				
				treeItem11 = new TreeItem(treeItem0, 0);
				treeItem11.setImage(classImage);
				treeItem11.setText("[" + nameCorrected2 + "] " + classOrInterface.getName() + ".java");				
			}
		}
	}

	private String getClassName (Object obj) {
		String[] nameCorrected = obj.getClass().getName().split("\\.");
		String nameCorrected2 = nameCorrected[nameCorrected.length-1];
		nameCorrected2 = nameCorrected2.substring(0, nameCorrected2.length()-4);
		return nameCorrected2;
	}
	
	private void fillListArchitecturalElements() {
		for (AbstractStructureElement element : structureElementsTOBE) {
			String [] nameCorrected = element.getClass().getName().split("\\.");
			String nameCorrected2 = nameCorrected[nameCorrected.length-1];
			nameCorrected2 = nameCorrected2.substring(0, nameCorrected2.length()-4);
			listPlannedArchitecture.add("[" + nameCorrected2 + "] " + element.getName());
		}
	}
	

	private void elementsMap() {
		MapItem mapItem = new MapItem();
		//getting item selected on the list
		int ind = listPlannedArchitecture.getSelectionIndex();
		//saving architecture element to save
		
		mapItem.setStructureElement(structureElementsTOBE.get(ind));
		
		TreeItem [] selTree = treeCodeElements.getSelection();
		System.out.println("IndicesSelecionados: " + structureElementsTOBE.get(ind).getName());
		//System.out.println("Tree");

		String map = "";
		map+= "[" + getClassName(mapItem.getStructureElement()) + "] " + mapItem.getStructureElement().getName() + " was mapped to ";
		
		for (int i = 0; i < selTree.length; i++) {
			
			System.out.println("entrou");
			
			//caso possua pai
			if (selTree[i].getParentItem() != null) {
				
				String pathToGet = "";
				
				
				for (Package package1 : allPackagesASIS) {
					pathToGet = "";
					pathToGet = readingKDMFile.getPathOfPackage(package1, pathToGet);
					
					
					//retirando o ClassName							
					String auxTreeElement = selTree[i].getParentItem().getText().replace("["+getClassName(package1)+"] ", "");
					
					if (pathToGet.equals(auxTreeElement)) {
						EList<AbstractCodeElement> elements = readingKDMFile.getClassesAndInterfacesByPackage(package1);
						for (AbstractCodeElement abstractCodeElement : elements) {
							String elementToFind = selTree[i].getText().replace(".java", "");
							//retirando o className
							elementToFind = elementToFind.replace("["+getClassName(abstractCodeElement)+"] ", "");
							System.out.println("Element to Find: " + elementToFind);
							if (elementToFind.equals(abstractCodeElement.getName())) {
								mapItem.getCodeElements().add(abstractCodeElement);
								allItensToMap.add(mapItem);
								//adding to list																																																
								
								break;
							}
						}
					}
				}

			} else {
				
				String pathToGet = "";
				
				
				for (Package package1 : allPackagesASIS) {
					pathToGet = "";
					pathToGet = readingKDMFile.getPathOfPackage(package1, pathToGet);
					//retirando o ClassName							
					String auxTreeElement = selTree[i].getText().replace("["+getClassName(package1)+"] ", "");
					if (pathToGet.equals(auxTreeElement)) {
						//System.err.println("encontrou o pacote");
						mapItem.getCodeElements().add(package1);
						allItensToMap.add(mapItem);
					}
				}
				
			}
			
			
			AbstractCodeElement itemToMap = mapItem.getCodeElements().get(mapItem.getCodeElements().size()-1);
			if (i == (selTree.length-1)) {
				//System.out.println("ultimo");
				if (itemToMap instanceof Package)
					map += "[" + getClassName(itemToMap) + "] " + itemToMap.getName() + ".";
				else 
					map += "[" + getClassName(itemToMap) + "] " + itemToMap.getName() + ".java.";
				
				listOfMaps.add(map);
			} else {
				//System.out.println(mapItem.getCodeElements().get(mapItem.getCodeElements().size()-1).getName());
				
				if (itemToMap instanceof Package)
					map += "[" + getClassName(itemToMap) + "] " + itemToMap.getName() + ", ";
				else 
					map += "[" + getClassName(itemToMap) + "] " + itemToMap.getName() + ".java, ";
			}
		}
	}

	private void saveElementsMapped() {
		StructureModel structureModel = StructureFactory.eINSTANCE
				.createStructureModel();// create a StructureModel

		//clean Previous Structure Model
		for (int j = 0; j < ASIS.getModel().size(); j++) {
			if (ASIS.getModel().get(j) instanceof StructureModel) {
				System.out.println("Removeu " + ASIS.getModel().get(j).getName());
				ASIS.getModel().remove(j);
			}
		}				
		
		ASIS.getModel().add(structureModel);// add the StructureModel into the Segment
		
		for(MapItem mapItem : allItensToMap) {
								
			for (AbstractCodeElement codeElement : mapItem.getCodeElements()) {
				
				mapItem.getStructureElement().getImplementation().add(codeElement);
				
			}					
			structureModel.getStructureElement().add(mapItem.getStructureElement());	
			
			for (AbstractStructureElement abstractStructureElement : structureModel.getStructureElement()) {
				
				abstractStructureElement.getAggregated().clear();
				
			}
			
		}
		System.err.println(this.pathOutputArch);
		readingKDMFile.save(ASIS, this.pathOutputArch);
	}

	private void deleteSelectedMap() {
		//Falta fazer
		MessageDialog.openInformation(shlMappingArchitectureElements, "Not implemented", "Under construction!");
	}

	public static void setPathPlannedArchitecture(String pathPlannedArch) {
		MappingArchitectureElements.pathPlannedArch = "file:" + pathPlannedArch;
	}

	public static void setPathActualArchitecture(String pathActualArch) {
		MappingArchitectureElements.pathActualArch = "file:" + pathActualArch;
		
	}

	public static void setPathSelectedFile(String pathFileSelect) {
		String partialPath = "";
		
		for (String folder : pathFileSelect.split("/")) {
			
			if( !(folder.toUpperCase()).contains(".XMI") ){
				partialPath = partialPath + folder + "/";
			}
		}
		
		MappingArchitectureElements.pathOutputArch  = partialPath + MappingArchitectureElements.outputFileName ;
		
	}

}
