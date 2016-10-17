package br.ufscar.ARCH_KDM.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionFactory;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.action.UsesType;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeFactory;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.Imports;
import org.eclipse.gmt.modisco.omg.kdm.code.Module;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmFactory;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.ArchitectureView;
import org.eclipse.gmt.modisco.omg.kdm.structure.Component;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.gmt.modisco.omg.kdm.structure.Subsystem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.br.terra.dcl.DCLStandaloneSetup;
import com.br.terra.dcl.dCL.BasicType;
import com.br.terra.dcl.dCL.DCDecl;
import com.br.terra.dcl.dCL.DCLComponent;
import com.br.terra.dcl.dCL.DCLComponentInterface;
import com.br.terra.dcl.dCL.DCLLayer;
import com.br.terra.dcl.dCL.DCLModule;
import com.br.terra.dcl.dCL.DCLStructureElement;
import com.br.terra.dcl.dCL.DCLSubSystem;
import com.br.terra.dcl.dCL.ElementType;
import com.br.terra.dcl.dCL.EntityType;
import com.br.terra.dcl.dCL.Model;
import com.google.inject.Injector;

public class ReadinDSLVIew implements IObjectActionDelegate {

	//Andre - variavel não usada então comentei
	//private Shell shell;

	private IFile file;

	private Segment segment;

	private CodeModel codeModelForRelations = null;

	private ArrayList<DCLLayer> allDclLayers = new ArrayList<DCLLayer>(); 

	ArrayList<AbstractStructureElement> allAbstractStructureElements = new ArrayList<AbstractStructureElement>();

	/**
	 * Constructor for Action1.
	 */
	public ReadinDSLVIew() {
		super();
	}


	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		this.segment = null;

		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		System.out.println(editorPart);



		IEditorSite iEditorSite = editorPart.getEditorSite();

		if (iEditorSite != null) {


			//System.out.println("iEditorSite != null");
			// get selection provider
			ISelectionProvider selectionProvider = iEditorSite.getSelectionProvider();

			if (selectionProvider != null) {

				//System.out.println("selectionProvider != null");

				IFileEditorInput input = (IFileEditorInput) editorPart
						.getEditorInput();
				this.file = input.getFile();


				String dclFileTOBE = this.file.getRawLocationURI().toString();

				System.out.println("Path of the file DCL: " + dclFileTOBE);

				//trava aqui
				//new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../"); pra que serve isso mesmo? hauihaiuhiua
				Injector injector = new DCLStandaloneSetup().createInjectorAndDoEMFRegistration();

				//Andre - pega o editor aberto com o arquivo da DCL - não entendi direito o que isso faz
				XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
				resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
				Resource resource = resourceSet.getResource(URI.createURI(dclFileTOBE), true);

				//Andre - pega os dados do modelo do arquivo da DCL
				Model model = (Model) resource.getContents().get(0); 

				//Andre - pega todos os elementos estruturais escritos no arquivo da DCL
				EList<DCLStructureElement> allStructureElements = model.getStructureElements();
				
				EList<DCDecl> allRestrictions = model.getDCDecl();

				//Andre - cria o segment e o structure
				createArchitecture("PlannedArchitecture");

				//Andre - antes era assim isso podia dar erro se o 0 não fosse o Structure Model
				//Andre - StructureModel structureModel = (StructureModel) this.segment.getModel().get(0);

				//Andre - recupera o structure model do segment criado
				StructureModel structureModel = this.getStructureModelFromSegment("PlannedArchitecture");
				
				//Fernando - Limpando os elementos para nova execução
				this.allAbstractStructureElements.clear();
				this.allDclLayers.clear();

				//Andre - transforma os elementos da estrutura que estão como da DCL em elementos do KDM 
				ArrayList<AbstractStructureElement> setCorrectStructuredElement = this.setCorrectStructuredElement(allStructureElements);

				//Andre - adiciona no structure model os elementos arquiteturais convertidos
				structureModel.getStructureElement().addAll(setCorrectStructuredElement);

				//Andre - variavel não usada, comentei ela
				//EList<DCDecl> DCdecl = model.getDCDecl();		

				//Andre - recupera os elementos estruturais do kdm que foram acrescentados no structure agora pouco
				EList<AbstractStructureElement> auxList = structureModel.getStructureElement();

				//Andre - adiciona eles nessa lista allAbstractStructureElements faz isso para usar no metodo getToORFrom
				for (AbstractStructureElement abstractStructureElement : auxList) {

					this.allAbstractStructureElements.add(abstractStructureElement);

				}

				//Andre - cria as restrições das layers
				createRestrictionToLayers();

				
				this.createRestrictionToBeRepresentedInKDM(this.allAbstractStructureElements, allRestrictions);


				//String path =  "file:/Users/rafaeldurelli/Documents/runtime-EclipseApplication/University/src/com/br/Examples/TOBE_KDM.xmi";
				//String path =  "file:C:/TestsPlug-in/archKDM/TOBE_KDM.xmi";
				String path = dclFileTOBE.replace(".dcl", ".xmi");
				System.out.println("Path to save the planned KDM: " + path);
				//Andre - serializa a instancia do KDM
				this.save(this.segment, path);

			}


		}

	}

	/**
	 * This method search in the segment for the Structure Model that the name is the same of the parameter.
	 * 
	 * @author Landi
	 * 
	 * @param name
	 * @return StructureModel
	 */
	private StructureModel getStructureModelFromSegment(String name) {

		for ( KDMModel model : this.segment.getModel()) {
			if(model instanceof StructureModel){
				if(name.equalsIgnoreCase(model.getName())){
					return (StructureModel) model;
				}
			}
		}

		return null;
	}


	/** 
	 * Esse metodo e responsavel por salvar uma instancia do KDM apos a realizacao de mudancas no mesmo.
	 * @param segment, representa uma instancia do KDM
	 * @param kdmPath representa o caminho do arquivo KDM
	 */
	public void save(Segment segment, String KDMPath) {

		KdmPackage.eINSTANCE.eClass();


		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		Resource resource = resSet.createResource(URI.createURI(KDMPath));

		resource.getContents().add(segment);

		try {

			resource.save(Collections.EMPTY_MAP);

		} catch (IOException e) {

		}

	}

	/**
	 * This method generate a Segment instance to represent the KDM instance.
	 * Then generate a instance of the Structure Model to be the planned architecture.
	 * 
	 * The parameter name is the name that both instances will have.
	 * 
	 * @author Landi
	 * 
	 * @param name
	 */
	private void createArchitecture (String name) {

		this.segment = KdmFactory.eINSTANCE.createSegment();
		this.segment.setName(name);

		StructureModel structureModel = StructureFactory.eINSTANCE.createStructureModel();
		structureModel.setName(name);
		this.segment.getModel().add(structureModel);		

	}

	/**
	 * This method transform the structure elements from DCL to KDM
	 * 
	 * @author Landi
	 * 
	 * @param allStructureElements
	 * @return
	 */
	private ArrayList<AbstractStructureElement> setCorrectStructuredElement (EList<DCLStructureElement> allStructureElements) {


		//System.err.println("entrou - setCorrectStructuredElement");

		for (DCLStructureElement dclStructureElement : allStructureElements) {
			if (dclStructureElement instanceof DCLLayer) {
				Layer layer = StructureFactory.eINSTANCE.createLayer();
				layer.setName(dclStructureElement.getName());
				//Andre - add nessa lista pra usar depois no metodo createRestrictionToLayers()
				this.allDclLayers.add((DCLLayer)dclStructureElement);
				allAbstractStructureElements.add(layer);

				//Fernando - Criando relacionamento hierárquico
				if ((((DCLLayer) dclStructureElement).getLayer()) != null) {
					String name = ((DCLLayer) dclStructureElement).getLayer().getName();
					AbstractStructureElement from = this.getToORFrom(name, allAbstractStructureElements);
					ArrayList<KDMRelationship> relations = createActionsExamples(from.getName(), layer.getName());
					this.createAggregatedRelationship(from, layer, relations);
				}
				else if ((((DCLLayer) dclStructureElement).getSubSystem()) != null) {					
					String name = ((DCLLayer) dclStructureElement).getSubSystem().getName();
					AbstractStructureElement from = this.getToORFrom(name, allAbstractStructureElements);
					ArrayList<KDMRelationship> relations = createActionsExamples(from.getName(), layer.getName());
					this.createAggregatedRelationship(from, layer, relations);
					
				} else if ((((DCLLayer) dclStructureElement).getComponent()) != null) {
					String name = ((DCLLayer) dclStructureElement).getComponent().getName();
					AbstractStructureElement from = this.getToORFrom(name, allAbstractStructureElements);
					ArrayList<KDMRelationship> relations = createActionsExamples(from.getName(), layer.getName());
					this.createAggregatedRelationship(from, layer, relations);
				}

			}else if (dclStructureElement instanceof DCLComponent) {

				Component component = StructureFactory.eINSTANCE.createComponent();
				component.setName(dclStructureElement.getName());
				allAbstractStructureElements.add(component);
				
				
				if ((((DCLComponent) dclStructureElement).getLayer()) != null) {
					String name = ((DCLLayer) dclStructureElement).getLayer().getName();
					AbstractStructureElement from = this.getToORFrom(name, allAbstractStructureElements);
					ArrayList<KDMRelationship> relations = createActionsExamples(from.getName(), component.getName());
					this.createAggregatedRelationship(from, component, relations);
				}
				else if ((((DCLComponent) dclStructureElement).getSubSystem()) != null) {
					String name = ((DCLLayer) dclStructureElement).getSubSystem().getName();
					AbstractStructureElement from = this.getToORFrom(name, allAbstractStructureElements);
					ArrayList<KDMRelationship> relations = createActionsExamples(from.getName(), component.getName());
					this.createAggregatedRelationship(from, component, relations);
				}

			} else if (dclStructureElement instanceof DCLSubSystem) {

				Subsystem subSystem = StructureFactory.eINSTANCE.createSubsystem();
				subSystem.setName(dclStructureElement.getName());
				allAbstractStructureElements.add(subSystem);
				
				if ((((DCLSubSystem) dclStructureElement).getSubSystem()) != null) {
					String name = ((DCLLayer) dclStructureElement).getSubSystem().getName();
					AbstractStructureElement from = this.getToORFrom(name, allAbstractStructureElements);
					ArrayList<KDMRelationship> relations = createActionsExamples(from.getName(), subSystem.getName());
					this.createAggregatedRelationship(from, subSystem, relations);
				}

			} else if (dclStructureElement instanceof DCLModule) {

				//ArchitectureView architectureView = StructureFactory.eINSTANCE.createArchitectureView();
				//architectureView.setName(dclStructureElement.getName());
				//allAbstractStructureElements.add(architectureView);
				
				Component component = StructureFactory.eINSTANCE.createComponent();
				component.setName(dclStructureElement.getName());
				allAbstractStructureElements.add(component);

			} else if (dclStructureElement instanceof DCLComponentInterface) {
				//TODO 
			}			

		}				

		return allAbstractStructureElements;
	}

	/**
	 * This method create the restrictions for all layers in the list this.allDclLayers 
	 * 
	 * @author Landi
	 * 
	 */
	private void createRestrictionToLayers () {

		//System.err.println("entrou - createRestrictionToLayers");

		//Andre - percorre a lista das layers que foram escritas na DCL (this.allDclLayers)
		for (int i = 0; i < this.allDclLayers.size(); i++) {

			//busca as layers 2, 3 e cria os relacionamento....
			//Andre - verifica se a layer tem um nivel maior que 1 pois a layer de nivel um não tem relacionamento com ninguem
			if (this.allDclLayers.get(i).getLevel() > 1) {

				//quando encontrar o desejado, busca o anterior para criar os relacionamentos
				for (DCLLayer dclLayer : this.allDclLayers) {

					//Andre - verifica qual é a layer imediatamente abaixo da layer da vez para criar um relacionamento entre elas
					if (dclLayer.getLevel() == (this.allDclLayers.get(i).getLevel()-1)) {

						//Andre - aqui dentro temos que dclLayer é a layer "filha" e a this.allDclLayers.get(i) é a layer "pai"

						//Andre - Na lista de todos os elementos estruturais e buscado o from da relacao entre duas camadas
						//busca o elemento estrutural descrito na restricao
						AbstractStructureElement from = this.getToORFrom(this.allDclLayers.get(i).getName(), allAbstractStructureElements);
						//Andre - na lista de todos os elementos estruturais e buscado o to da relacao entre duas camadas
						AbstractStructureElement to = this.getToORFrom(dclLayer.getName(), allAbstractStructureElements);

						//Andre - cria uma lista de relacoes entre duas camadas que podem ser realizadas
						ArrayList<KDMRelationship> lisfOfRelationshipsToAdd = createActionsExamples(from.getName(), to.getName());

						createAggregatedRelationship(from, to, lisfOfRelationshipsToAdd);						
					}

				}

			}

		}
		this.segment.getModel().add(this.codeModelForRelations);
	}

	/**
	 * This method creates the six types of relation that can exists between two layers.
	 * Each relation does not have from and to, because it represents the types of relation that can exists between to layers.
	 * 
	 * @author Landi
	 * 
	 * @return
	 */
	private ArrayList<KDMRelationship> createActionsExamples (String from, String to) {
		ArrayList<KDMRelationship> lisfOfRelationshipsToAdd = new ArrayList<KDMRelationship>();

		if(this.codeModelForRelations == null){
			this.codeModelForRelations = CodeFactory.eINSTANCE.createCodeModel(); 

			this.codeModelForRelations.setName("Elements Instances");
		}
		Module moduleForRelations = CodeFactory.eINSTANCE.createModule();

		moduleForRelations.setName("Module Instance From: " + from + " To: " + to);

		this.codeModelForRelations.getCodeElement().add(moduleForRelations);

		//Andre - cria o eContainer das relacoes de action
		ActionElement actionElementForRelations = ActionFactory.eINSTANCE.createActionElement();

		actionElementForRelations.setName("actionElement Instance");

		moduleForRelations.getCodeElement().add(actionElementForRelations);

		//Andre - cria o eContainer das relações de código
		CodeElement codeElementForRelations = CodeFactory.eINSTANCE.createCodeElement();

		codeElementForRelations.setName("codeElement Instance");

		moduleForRelations.getCodeElement().add(codeElementForRelations);

		//Andre - tipos de relacao de action:
		Calls relation = ActionFactory.eINSTANCE.createCalls();
		lisfOfRelationshipsToAdd.add(relation);												
		actionElementForRelations.getActionRelation().add(relation);

		UsesType relation2 = ActionFactory.eINSTANCE.createUsesType();
		lisfOfRelationshipsToAdd.add(relation2);
		actionElementForRelations.getActionRelation().add(relation2);

		Creates relation3 = ActionFactory.eINSTANCE.createCreates();
		lisfOfRelationshipsToAdd.add(relation3);
		actionElementForRelations.getActionRelation().add(relation3);

		//Andre - Tipos de relação de code:
		Extends relation4 = CodeFactory.eINSTANCE.createExtends();
		lisfOfRelationshipsToAdd.add(relation4);
		codeElementForRelations.getCodeRelation().add(relation4);

		Implements relation5 = CodeFactory.eINSTANCE.createImplements();
		lisfOfRelationshipsToAdd.add(relation5);
		codeElementForRelations.getCodeRelation().add(relation5);

		HasValue relation6 = CodeFactory.eINSTANCE.createHasValue();
		lisfOfRelationshipsToAdd.add(relation6);
		codeElementForRelations.getCodeRelation().add(relation6);
		
		Imports relation7 = CodeFactory.eINSTANCE.createImports();
		lisfOfRelationshipsToAdd.add(relation7);
		codeElementForRelations.getCodeRelation().add(relation7);

		return lisfOfRelationshipsToAdd;
	}

	private void createRestrictionToBeRepresentedInKDM (ArrayList<AbstractStructureElement> allAbstractStructureElements, EList<DCDecl> dclRestrictions) {

		String dependence = null;

		ArrayList<KDMRelationship> lisfOfRelationshipsToAdd = new ArrayList<KDMRelationship>();
		


		CodeModel elements = CodeFactory.eINSTANCE.createCodeModel(); 

		elements.setName("Elements Instances");

		this.segment.getModel().add(elements);

		Module module = CodeFactory.eINSTANCE.createModule();

		module.setName("Module Instance");

		elements.getCodeElement().add(module);

		ActionElement actionElement = ActionFactory.eINSTANCE.createActionElement();

		actionElement.setName("actionElement Instance");

		CodeElement codeElement = CodeFactory.eINSTANCE.createCodeElement();

		codeElement.setName("codeElement Instance");

		module.getCodeElement().add(codeElement);

		module.getCodeElement().add(actionElement);

		for (DCDecl restriction : dclRestrictions) {

			//Fernando: T é o elemento arquitetural de chegada (TO)
			String structureElementNameTO = restriction.getT().getName();
			//Fernando: Type é o elemento arquitetural de partida (from)
			String structureElementNameFROM = restriction.getType().getName();

			//busca o elemento estrutural descrito na restricao
			AbstractStructureElement from = this.getToORFrom(structureElementNameFROM, allAbstractStructureElements);
			AbstractStructureElement to = this.getToORFrom(structureElementNameTO, allAbstractStructureElements);

			ElementType elementType = restriction.getElementType();

			if (elementType instanceof BasicType) {
				BasicType basicType = (BasicType) elementType;
				dependence = basicType.getTypeName();

			} else if (elementType instanceof EntityType) {

				EntityType entityType = (EntityType) elementType;
				dependence = entityType.getEntity();
			}

			if (dependence.equals("access")) {			
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);												
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("declare")) {
				UsesType relation = ActionFactory.eINSTANCE.createUsesType();
				lisfOfRelationshipsToAdd.add(relation);
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("create")) {
				Creates relation = ActionFactory.eINSTANCE.createCreates();
				lisfOfRelationshipsToAdd.add(relation);
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("handle")) {
				//Access + Declare
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);							
				actionElement.getActionRelation().add(relation);

				UsesType relation2 = ActionFactory.eINSTANCE.createUsesType();
				lisfOfRelationshipsToAdd.add(relation2);
				actionElement.getActionRelation().add(relation2);
			} else if (dependence.equals("depend")) {
				//TODOS
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);							
				actionElement.getActionRelation().add(relation);

				UsesType relation2 = ActionFactory.eINSTANCE.createUsesType();
				lisfOfRelationshipsToAdd.add(relation2);
				actionElement.getActionRelation().add(relation2);

				Creates relation3 = ActionFactory.eINSTANCE.createCreates();
				lisfOfRelationshipsToAdd.add(relation3);
				actionElement.getActionRelation().add(relation3);

				Extends relation4 = CodeFactory.eINSTANCE.createExtends();
				lisfOfRelationshipsToAdd.add(relation4);
				codeElement.getCodeRelation().add(relation4);

				Implements relation5 = CodeFactory.eINSTANCE.createImplements();
				lisfOfRelationshipsToAdd.add(relation5);
				codeElement.getCodeRelation().add(relation5);

				HasValue relation6 = CodeFactory.eINSTANCE.createHasValue();
				lisfOfRelationshipsToAdd.add(relation6);
				codeElement.getCodeRelation().add(relation6);
				
				Imports relation7 = CodeFactory.eINSTANCE.createImports();
				lisfOfRelationshipsToAdd.add(relation7);
				codeElement.getCodeRelation().add(relation7);
			} else if (dependence.equals("extend")) {
				Extends relation = CodeFactory.eINSTANCE.createExtends();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);
			} else if (dependence.equals("implement")) {
				Implements relation = CodeFactory.eINSTANCE.createImplements();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);
			} else if (dependence.equals("derive")) {
				//Extend + Implement
				Extends relation = CodeFactory.eINSTANCE.createExtends();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);

				Implements relation2 = CodeFactory.eINSTANCE.createImplements();
				lisfOfRelationshipsToAdd.add(relation2);
				codeElement.getCodeRelation().add(relation2);
			} else if (dependence.equals("throw")) {
				Calls relation = ActionFactory.eINSTANCE.createCalls();
				lisfOfRelationshipsToAdd.add(relation);
				actionElement.getActionRelation().add(relation);
			} else if (dependence.equals("annotated")) {
				HasValue relation = CodeFactory.eINSTANCE.createHasValue();
				lisfOfRelationshipsToAdd.add(relation);
				codeElement.getCodeRelation().add(relation);
			}

			if (restriction.getOnly() != null) {

				//TODO

			}else if (restriction.getMust() != null) {

				//TODO
			} else if (restriction.getCannot() != null) {

				//TODO
			} else {

				//System.out.println(abstractRelationship);

				createAggregatedRelationship(from, to, lisfOfRelationshipsToAdd);
			}
		}

	}

	/**
	 * 
	 * This method create and put the aggregatedRelationship in the from layer.
	 * 
	 * @author Landi
	 * 
	 * @param from
	 * @param to
	 * @param relations
	 */
	private void createAggregatedRelationship (AbstractStructureElement from, AbstractStructureElement to, ArrayList<KDMRelationship> relations) {

		//Andre - verifica se ja existe um aggregated no na layer from
		if (from.getAggregated().size() > 0) {
			//System.out.println("MAIOR QUE 1, TODO");

			//Andre - pega os aggragated que ja estão no from
			EList<AggregatedRelationship> aggregatedFROM = from.getAggregated();		

			//Andre - começa um for nesses aggregated
			for (int i = 0; i < aggregatedFROM.size(); i++) {

				//Andre - verifica se o aggregated que ja existe tem o mesmo destino que o que esta pra ser criado 
				if (to.getName().equalsIgnoreCase(aggregatedFROM.get(i).getTo().getName())) {

					//Andre - se tiver o mesmo destino ele adiciona as relacoes novas e atualiza a densidade, depois disso ele pega e sai do for
					//ADICIONAR

					aggregatedFROM.get(i).setDensity(aggregatedFROM.get(i).getDensity()+relations.size());
					aggregatedFROM.get(i).getRelation().addAll(relations);

					break;
				}

				//Andre - se for o ultimo aggregated do for e mesmo assim não encontrou o com o mesmo destino que esta pra ser criado 
				//Andre - entao cria um novo aggregated para ser adicionado
				//se chegar no ultimo e nao encontrar
				if (i == (aggregatedFROM.size()-1)) {

					AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
					newRelationship.setDensity(relations.size());
					newRelationship.setFrom(from);
					newRelationship.setTo(to);
					newRelationship.getRelation().addAll(relations);
					from.getAggregated().add(newRelationship);
					break;

				}


			}

		} else {
			//Andre - se não tiver um agrregated na layer from adiciona um com as relacoes que podem entre duas layers
			AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
			newRelationship.setDensity(relations.size());
			newRelationship.setFrom(from);
			newRelationship.setTo(to);
			newRelationship.getRelation().addAll(relations);
			from.getAggregated().add(newRelationship);
		}
		
		//Fernando - Limpando lista 
		relations.clear();
		from = null;
		to = null;
		

	}

	/**
	 * This method search in the allAbstractStructureElements list for the name of the element in the elementToFind.
	 * Return the element if were found and null if were not.
	 * 
	 * @author Landi
	 * 
	 * @param elementToFind
	 * @param allAbstractStructureElements
	 * @return
	 */
	private AbstractStructureElement getToORFrom (String elementToFind, ArrayList<AbstractStructureElement> allAbstractStructureElements) {

		for (AbstractStructureElement abstractStructureElement : allAbstractStructureElements) {
			if (abstractStructureElement.getName().equals(elementToFind)) {

				return abstractStructureElement;

			}
		}
		return null;

	} 

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {


	}

}
