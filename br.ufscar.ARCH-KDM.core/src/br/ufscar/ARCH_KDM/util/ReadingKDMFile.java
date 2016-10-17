package br.ufscar.ARCH_KDM.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.UsesType;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.CallableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeFactory;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.HasType;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterTo;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Signature;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateType;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmFactory;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;


/** 
 * @author Fernando Chagas e Rafael Durelli
 * @since 21/08/14 
 */

public class ReadingKDMFile {

	private Segment segmentMain = null;
	
	private ArrayList<ClassUnit> allClassUnits = new ArrayList<ClassUnit>();
	
	private ArrayList<MethodUnit> allMethodUnits = new ArrayList<MethodUnit>();
	
	private ArrayList<InterfaceUnit> allInterfaceUnit = new ArrayList<InterfaceUnit>();
	
	private ArrayList<StorableUnit> allStorableUnits = new ArrayList<StorableUnit>();
	
	private ArrayList<Package> allPackages = new ArrayList<Package>();
	
	private ArrayList<BlockUnit> allBlockUnits = new ArrayList<BlockUnit>();
	
	private ArrayList<Calls> allCalls = new ArrayList<Calls>();
	
	private ArrayList<Layer> allLayers = new ArrayList<Layer>();
	
	private ArrayList<KDMRelationship> allRelationships = new ArrayList<KDMRelationship>();
	
	private ArrayList<HasType> allHasType = new ArrayList<HasType>();
	
	private ArrayList<AbstractActionRelationship> allAbstractActionRelationships = new ArrayList<AbstractActionRelationship>();
	
	private ArrayList<HasValue> allHasValues = new ArrayList<HasValue>();
	
	private Segment targetArchitecture = null;
	
	/** 
	 * Retorna um segmento passando como parametro o caminho completo de um arquivo KDM.
	 * 
	 * @param  KDMModelFullPath  representa o caminho da arquivo KDM
	 * @return      O Segment, que e o elemento principal para manipular uma instancia do KDM.
	 * @see         org.eclipse.gmt.modisco.omg.kdm.kdm.Segment
	 */
	public Segment load(String KDMModelFullPath) {
		
		System.err.println(KDMModelFullPath);

		KdmPackage.eINSTANCE.eClass();//get the KDMPackage instance

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("fer", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();
		// Get the resource
		Resource resource = resSet.getResource(URI.createURI(KDMModelFullPath),
				true);

		return (Segment) resource.getContents().get(0); //pega o primeiro elemento, que e o Segment
	}
	
	
	
	public void createBasicTargetArchitecture (Segment asIsArch) {
		
		//this.targetArchitecture = createSegment();
		this.targetArchitecture = asIsArch; //copia a arq atual completa p/ target			
		
		StructureModel structureModel = getStructureModelPassingSegment(this.targetArchitecture);
		
		StructureModel structureModelViolations = createStructureModel(this.targetArchitecture);		
		
		//copiando elementos estruturais p/ violations
		EList<AbstractStructureElement> allAbstractElements  = structureModel.getStructureElement();
		Collection copyAllAbstractStructureElementASIS = EcoreUtil.copyAll(allAbstractElements);
		structureModelViolations.getStructureElement().addAll(copyAllAbstractStructureElementASIS);
		
		//limpando aggregateds
		for (AbstractStructureElement abstractStructureElement : structureModelViolations.getStructureElement()) {		
			abstractStructureElement.getAggregated().clear();			
		}			
	
//		StructureModel structureModel = (StructureModel) this.targetArchitecture.getModel().get(0);	
//		Collections.copy(structureModel.getStructureElement(), abstractStructureElementASIS);
//		Collection copyAllAbstractStructureElementASIS = EcoreUtil.copyAll(abstractStructureElementASIS);	
		//structureModel.getStructureElement().addAll(copyAllAbstractStructureElementASIS);
		
		//recupera structure model
		//StructureModel structureModel = getStructureModelPassingSegment(this.targetArchitecture.getSegment().get(0));
				
		//EList<AbstractStructureElement> allAbstractElements  = structureModel.getStructureElement();
		
		//for (AbstractStructureElement abstractStructureElement : allAbstractElements) {
		
			//abstractStructureElement.getAggregated().clear();
			
		//}
		
		//this.targetArchitecture.getModel().add(asIsCodeModel); 
		
	}	
	
	public void compareRelations (String ArchitecturePathASIS, String ArchitecturePathTOBE) {
		
		
		System.out.println("Chamou");
		
		Segment architectureASIS = this.load(ArchitecturePathASIS);
		Segment architectureTOBE = this.load(ArchitecturePathTOBE);
		
		System.out.println(architectureASIS);
		System.out.println(architectureTOBE);
		
		List<AggregatedRelationship> allAggregatedRelationShipASIS = new ArrayList<AggregatedRelationship>();
		
		List<AggregatedRelationship> allAggregatedRelationShipTOBE = new ArrayList<AggregatedRelationship>();
		
		StructureModel structureModelASIS = this.getStructureModelPassingSegment(architectureASIS);			
		
		StructureModel structureModelTOBE = this.getStructureModelPassingSegment(architectureTOBE);
		
		this.createBasicTargetArchitecture(architectureASIS);
		
		StructureModel structureModelViolations = this.getStructureModelPassingSegment(this.targetArchitecture);
		
		
		//move to a method
		
		//CodeModel codeModelAsIS = getFirstCodeModelBySegment(architectureASIS);
		
		
		//EList<AbstractStructureElement> abstractStructureElementASIS = (EList<AbstractStructureElement>) structureModelASIS.getStructureElement();
		//alteracao necessária para nao precisar acessar um novo arquivo
		EList<AbstractStructureElement> abstractStructureElementASIS = (EList<AbstractStructureElement>) structureModelViolations.getStructureElement();
		
		EList<AbstractStructureElement> abstractStructureElementTOBE = structureModelTOBE.getStructureElement();
		
		this.addAllAggregatedRelationShip(abstractStructureElementASIS, allAggregatedRelationShipASIS);
		this.addAllAggregatedRelationShip(abstractStructureElementTOBE, allAggregatedRelationShipTOBE);
		
		this.getCorrespondentAggregatedRelationship(allAggregatedRelationShipASIS, allAggregatedRelationShipTOBE);			
		
	}
	
	public void getCorrespondentAggregatedRelationship (List<AggregatedRelationship> allAggregatedRelationShipASIS, List<AggregatedRelationship> allAggregatedRelationShipTOBE) {
		
		KDMEntity fromASIS = null;
		KDMEntity toASIS = null;
		
		for (AggregatedRelationship aggregatedRelationshipASIS : allAggregatedRelationShipASIS) {
			
			fromASIS = aggregatedRelationshipASIS.getFrom();
			toASIS = aggregatedRelationshipASIS.getTo();
			
			if (fromASIS == null || toASIS == null) {
				System.out.println("AggregatedR com from ou to null, desconsiderar e examinar");
			}
			else {
				
				for(int i = 0; i < allAggregatedRelationShipTOBE.size(); i++)	{						
					
					if (fromASIS.getName().equals(allAggregatedRelationShipTOBE.get(i).getFrom().getName()) && toASIS.getName().equals(allAggregatedRelationShipTOBE.get(i).getTo().getName())) {
						
						compare(aggregatedRelationshipASIS, allAggregatedRelationShipTOBE.get(i));
						break;
						
					}
					else if(i == (allAggregatedRelationShipTOBE.size()-1)) {
						searchAndAddStructureElement(aggregatedRelationshipASIS, aggregatedRelationshipASIS);
					}
					
				}
			}
			
		}
		
	}
	
	/*
	 * Este metodo recebe dois AggregatedRelationships equivalentes e compara-os.
	 */
	
	public void compare (AggregatedRelationship aggregatedRelationshipASIS, AggregatedRelationship aggregatedRelationshipTOBE) {
		
		List<KDMRelationship> relationsASIS = aggregatedRelationshipASIS.getRelation();
		List<KDMRelationship> relationsTOBE = aggregatedRelationshipTOBE.getRelation();
		
		String nameRelationTOBE = null;
		String nameRelationASIS = null;
		
		Boolean checked = null;
		
		for (KDMRelationship relationASIS : relationsASIS) {
			
			checked = false;
			
			nameRelationASIS = relationASIS.getClass().getName();			
			
			
			for (int i = 0; i < relationsTOBE.size(); i++) {
				
				nameRelationTOBE = relationsTOBE.get(i).getClass().getName();
				
				//verifica se existe algum relation do tipo solicitado
				if (nameRelationASIS.equals(nameRelationTOBE)) {
					System.out.println("Encontrou " + nameRelationTOBE);
					checked = true;
					break;
					
				}
				
				if (i == (relationsTOBE.size()-1) && checked == false) {
					//cria a nova instância
					System.err.println("Não encontrado");
					
					this.searchStructureElement(aggregatedRelationshipASIS, relationASIS);
					
				}
				
				
			}
						
		}
		
		
		
	}
	
	private void searchStructureElement (AggregatedRelationship aggregatedRelationship,  KDMRelationship relationToAdd) {
		
		AbstractStructureElement fromASIS = (AbstractStructureElement) aggregatedRelationship.getFrom();
		AbstractStructureElement toASIS = (AbstractStructureElement) aggregatedRelationship.getTo();
		
		AbstractStructureElement targetElementFROM = null;
		AbstractStructureElement targetElementTO = null;
		
		//TODO
		EList<AbstractStructureElement> allElementsFromTarget = getAllStructureEViolations(this.targetArchitecture);
		
		for (AbstractStructureElement targetElement : allElementsFromTarget) {
			
			if (fromASIS.getName().equals(targetElement.getName())) {
				targetElementFROM = targetElement;				
			}
			
			if (toASIS.getName().equals(targetElement.getName())) {
				targetElementTO = targetElement;
			}
			
		}
		
		if (targetElementFROM.getAggregated().size() > 0) {
			//TODO
			System.out.println("MAIOR QUE 1, TODO");
			
			EList<AggregatedRelationship> aggregatedFROM = targetElementFROM.getAggregated();		
			
			
			for (int i = 0; i < aggregatedFROM.size(); i++) {
				
				if (toASIS.getName().equals(aggregatedFROM.get(i).getTo().getName())) {
					
					//ADICIONAR
					
					aggregatedFROM.get(i).setDensity(aggregatedFROM.get(i).getDensity()+1);
					aggregatedFROM.get(i).getRelation().add(relationToAdd);
					
					break;
				}
				
				//se chegar no último e não encontrar
				if (i == (aggregatedFROM.size()-1)) {
					
					AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
					newRelationship.setDensity(1);
					newRelationship.setFrom(targetElementFROM);
					newRelationship.setTo(targetElementTO);
					newRelationship.getRelation().add(relationToAdd);
					targetElementFROM.getAggregated().add(newRelationship);
					
				}
				
				
			}
			
			
			
		} else {
			AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
			newRelationship.setDensity(1);
			newRelationship.setFrom(targetElementFROM);
			newRelationship.setTo(targetElementTO);
			newRelationship.getRelation().add(relationToAdd);
			targetElementFROM.getAggregated().add(newRelationship);
		}
		
		
		
	}
	
	private void searchAndAddStructureElement (AggregatedRelationship aggregatedRelationship, AggregatedRelationship relationshipToAdd) {
		
		AbstractStructureElement fromASIS = (AbstractStructureElement) aggregatedRelationship.getFrom();
		AbstractStructureElement toASIS = (AbstractStructureElement) aggregatedRelationship.getTo();
		
		AbstractStructureElement targetElementFROM = null;
		AbstractStructureElement targetElementTO = null;
		
		//TODO
		EList<AbstractStructureElement> allElementsFromTarget = getAllStructureEViolations(this.targetArchitecture);
		
		for (AbstractStructureElement targetElement : allElementsFromTarget) {
			
			if (fromASIS.getName().equals(targetElement.getName())) {
				targetElementFROM = targetElement;				
			}
			
			if (toASIS.getName().equals(targetElement.getName())) {
				targetElementTO = targetElement;
			}
			
		}
		
		AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
		newRelationship.setDensity(relationshipToAdd.getDensity());
		newRelationship.setFrom(targetElementFROM);
		newRelationship.setTo(targetElementTO);
		
		//Exemplo 2, copiando
		//Collection copyAllAbstractStructureElementASIS = EcoreUtil.copyAll(relationshipToAdd.getRelation());
		//newRelationship.getRelation().addAll(copyAllAbstractStructureElementASIS);
		
		for (KDMRelationship relationship : relationshipToAdd.getRelation()) {
			newRelationship.getRelation().add(relationship);
		}
				
		targetElementFROM.getAggregated().add(newRelationship);					
		
	}
	
	private EList<AbstractStructureElement> getAllStructureElements (Segment segment) {
		
		EList<KDMModel> models = segment.getModel();
		
		StructureModel structureModel = null;
		
		for (KDMModel kdmModel : models) {
			
			if (kdmModel instanceof StructureModel) {
				 
				structureModel = (StructureModel) kdmModel;
			}
			
		}
		
		return structureModel.getStructureElement();
		
		
	}
	
	private EList<AbstractStructureElement> getAllStructureEViolations (Segment segment) {
		
		EList<KDMModel> models = segment.getModel();
		
		StructureModel structureModel = null;
		
		for (KDMModel kdmModel : models) {
			
			if (kdmModel instanceof StructureModel && kdmModel.getName() != null) {
				 
				if (kdmModel.getName().equals("violations")) {
					structureModel = (StructureModel) kdmModel;
					break;
				}
			}
			
		}
		
		return structureModel.getStructureElement();
		
		
	}
	
	
	
	
	
	public void addAllAggregatedRelationShip (EList<AbstractStructureElement> abstractStructureElement, List<AggregatedRelationship> listToAdd ) {
		
		for (AbstractStructureElement abstractStructur : abstractStructureElement) {
			
			listToAdd.addAll(this.getAllAggregatedRelationShip(abstractStructur));
			
		}
		
	}
	
	public EList<AggregatedRelationship> getAllAggregatedRelationShip (AbstractStructureElement element) {
		
		
		return element.getAggregated();
		
	}
	
	public StructureModel getStructureModelPassingSegment (Segment segment) {
		
		StructureModel structureModel = null;
		
		
		EList<KDMModel> kdmModelASIS = segment.getModel();
		
		for (KDMModel kdmModel : kdmModelASIS) {
			if (kdmModel instanceof StructureModel) {
				
				structureModel = (StructureModel)kdmModel;
				break;
				
			}
		}

		return structureModel;
		
	}
	
	public ArrayList<StorableUnit> fetchAllStorableUnitFromClassUnit (ClassUnit classUnitToGetTheStorableUnits) {
		
		ArrayList<StorableUnit> allStorableUnits = new ArrayList<StorableUnit>();
		
		EList<CodeItem> allElements = classUnitToGetTheStorableUnits.getCodeElement();
		
		for (CodeItem codeItem : allElements) {
			
			if (codeItem instanceof StorableUnit) {
				
				StorableUnit storableUnitToFetch = (StorableUnit) codeItem;
				allStorableUnits.add(storableUnitToFetch);
			}
			
		}
		
		return allStorableUnits;
		
	}
	
	public ArrayList<HasType> fetchAllHasTypeFromStorableUnits (StorableUnit storableUnitToGetTheHasType) {
		
		ArrayList<HasType> auxAllHasType = new ArrayList<HasType>();
						
		EList<AbstractCodeRelationship> allRelations = storableUnitToGetTheHasType.getCodeRelation();
		
		for (AbstractCodeRelationship abstractCodeRelationship : allRelations) {
			
			if (abstractCodeRelationship instanceof HasType) {
								
				if (verifyIfRelationContaisLayer(abstractCodeRelationship, this.allLayers)) {				
					auxAllHasType.add((HasType)abstractCodeRelationship);
				}
				
			}
			
		}
		
		return auxAllHasType;
		
	}
	
	public ArrayList<HasType> fetchAllHasTypeFromParameterUnits (ParameterUnit parameterUnitToGetTheHasType) {
		
		
		System.out.println("Entrou");
		ArrayList<HasType> auxAllHasType = new ArrayList<HasType>();
						
		EList<AbstractCodeRelationship> allRelations = parameterUnitToGetTheHasType.getCodeRelation();
		
		for (AbstractCodeRelationship abstractCodeRelationship : allRelations) {
			
			if (abstractCodeRelationship instanceof HasType) {
								
				if (verifyIfRelationContaisLayer(abstractCodeRelationship, this.allLayers)) {				
					auxAllHasType.add((HasType)abstractCodeRelationship);
				}
				
			}
			
		}
		
		return auxAllHasType;
		
	}
	
	/** 
	 * Esse metodo e responsavel por adicionar um HasType como CodeRelation para cada StorableUnit
	 * 
	 * @param  allStorableUnitOfAClass representa todas as StorableUnits de uma Classe
	 */
	
	public void addHasTypeToStorableUnit (ArrayList<StorableUnit> allStorableUnitOfAClass) {
		
		for (StorableUnit storableUnit : allStorableUnitOfAClass) {
			
			HasType hasType = CodeFactory.eINSTANCE.createHasType();
			
			CodeItem auxFrom;
			
			if (storableUnit.eContainer() instanceof ActionElement) {
				// caso de um StorableUnit dentro de ActionElement, sobe na arvore ate alcancar um methodUnit
				auxFrom = (MethodUnit)storableUnit.eContainer().eContainer().eContainer();
			} else {
				// caso de um StorableUnit dentro de um ClassUnit
				auxFrom = (ClassUnit)storableUnit.eContainer();
			}
				
			hasType.setFrom(auxFrom);
			
			Datatype dataType = storableUnit.getType();
			
			System.out.println("Todos os Types "+ dataType);
			
			//Caso de 1:*
			if (dataType instanceof TemplateType) {
				
				//desce na arvore do TemplateUnit
				TemplateType auxTemplateUnit = (TemplateType) dataType;
				EList<AbstractCodeRelationship> auxAbstractCodeRelationship = auxTemplateUnit.getCodeRelation();
				ParameterTo auxParameterTo = (ParameterTo) auxAbstractCodeRelationship.get(0);

				dataType = (Datatype) auxParameterTo.getTo();							
								
			} 
			
			hasType.setTo(dataType);
			
			storableUnit.getCodeRelation().add(hasType);					

		}			
		
	}
	
	/** 
	 * Esse metodo e responsavel por adicionar um HasType como CodeRelation para cada ParameterUnit
	 * 
	 * @param  allParameterUnits representa todas os parametros (ParameterUnit) de uma assinatura de metodo (Signature)
	 */
	
	public void addHasTypeToSignature (ArrayList<ParameterUnit> allParameterUnits) {
		
		for (ParameterUnit auxParameterUnit : allParameterUnits) {
			
			HasType hasType = CodeFactory.eINSTANCE.createHasType();
			
			hasType.setFrom((Signature)auxParameterUnit.eContainer());					
			
			Datatype dataType = auxParameterUnit.getType();
			
			hasType.setTo(dataType);
			
			auxParameterUnit.getCodeRelation().add(hasType);
			
		}
				
	}
	
	
	public void teste () {
		
		
		HasType hasType = CodeFactory.eINSTANCE.createHasType();
		
		StorableUnit attribute = CodeFactory.eINSTANCE.createStorableUnit();
		
		ClassUnit classUnit = CodeFactory.eINSTANCE.createClassUnit();
		
		hasType.setFrom(attribute);
		hasType.setTo(classUnit);
		
		AggregatedRelationship aggregated = CoreFactory.eINSTANCE.createAggregatedRelationship();
		
		
		aggregated.getRelation().add(hasType);
		
		
		
	}
	
	private static Segment createSegment() {
		KdmFactory kdmFactory = KdmPackage.eINSTANCE.getKdmFactory();

		Segment createSegment = kdmFactory.createSegment();
		createSegment.setName("TargetArchitecture");

		return createSegment;
	}
	
	private static StructureModel createStructureModel (Segment segment) {
		
		StructureModel structureModel = StructureFactory.eINSTANCE
				.createStructureModel();// create a StructureModel
		structureModel.setName("violations");
		segment.getModel().add(structureModel);
		
		
		return structureModel;
	}
	

	/** 
	 * Esse metodo e responsavel por instancia o StructureModel para atribuir os elementos arquiteturais.
	 * 
	 * @param  allPackages  representa todos os pacotes que a instancia do KDM contem.
	 * @param segment, representa uma instancia do KDM
	 * @param kdmPath representa o caminho do arquivo KDM
	 */
	public void mappingPackageToLayer(ArrayList<Package> allPackages,
			Segment segment, String kdmPath) {

		StructureModel structureModel = StructureFactory.eINSTANCE
				.createStructureModel();// create a StructureModel

		segment.getModel().add(structureModel);// add the StructureModel into the Segment

		Layer layer = null;

		for (Package package1 : allPackages) {						

			layer = StructureFactory.eINSTANCE.createLayer();

			layer.setName(package1.getName());
			layer.getImplementation().add(package1);
			structureModel.getStructureElement().add(layer);
			this.allLayers.add(layer);
		}

//		save(segment, kdmPath);

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
		m.put("website", new XMIResourceFactoryImpl());

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
	 * Esse metodo e responsavel por obter todos os pacotes da instancia do KDM
	 * @param segment, representa uma instancia do KDM
	 * @return ArrayList<Package> todos os pacotes do sistema.
	 */
	public ArrayList<Package> getAllPackages(Segment segment) {

		ArrayList<Package> allPackages = new ArrayList<Package>();

		CodeModel codeModel = (CodeModel) segment.getModel().get(0);

		EList<AbstractCodeElement> elements = codeModel.getCodeElement();

		for (int i = 0; i < elements.size() - 1; i++) {

			System.out.println("aqui " + elements.get(i));

			if (elements.get(i) instanceof Package) {

				Package packageKDM = (Package) elements.get(i);

				allPackages = (ArrayList<Package>) this.getAllPackages(
						packageKDM, allPackages);

			}

		}

		return allPackages;
	}

	/** 
	 * Esse metodo e responsavel por obter todos os pacotes da instancia do KDM
	 * @param packageToGet, o pacote para obter
	 * @param packages a lista com todos os pacotes.
	 * @return ArrayList<Package> todos os pacotes do sistema.
	 */
	private List<Package> getAllPackages(Package packageToGet,
			List<Package> packages) {

		EList<AbstractCodeElement> elements = packageToGet.getCodeElement();

		for (AbstractCodeElement abstractCodeElement : elements) {
			if (abstractCodeElement instanceof Package)
				packages = getAllPackages((Package) abstractCodeElement,
						packages);
			else {
				packages.add(packageToGet);
				return packages;
			}

		}
		return packages;
	}
	
	/** 
	 * Esse metodo e responsavel por obter todas as classes e interfaces a partir de um pacote
	 * @param auxPackage, o pacote que possui as claasses e interfaces
	 * @return EList<KDMEntity> todas as Classes e Interfaces de um pacote
	 */
	
	public EList<AbstractCodeElement> getClassesAndInterfacesByPackage (Package auxPackage) {
		EList<AbstractCodeElement> allClassesAndInterfaces = auxPackage.getCodeElement();
		return allClassesAndInterfaces;
	}

	
	/** 
	 * Esse metodo e responsavel por obter todas as classes da instancia do KDM
	 * @param segment, o segment que representa a instancia do modelo
	 * @return ArrayList<ClassUnit> todas as Classes do sistema.
	 */
	public ArrayList<ClassUnit> getAllClasses(Segment segment) {

		ArrayList<ClassUnit> allClasses = new ArrayList<ClassUnit>();

		CodeModel codeModel = (CodeModel) segment.getModel().get(0);

		EList<AbstractCodeElement> elements = codeModel.getCodeElement();

		for (int i = 0; i < elements.size() - 1; i++) {

			System.out.println(elements.get(i));

			if (elements.get(i) instanceof Package) {

				Package packageKDM = (Package) elements.get(i);

				this.getClasses(packageKDM.getCodeElement(), allClasses);

			}

		}

		return allClasses;

	}
	
	/** 
	 * Esse metodo e responsavel por obter todas as Interfaces da instancia do KDM
	 * @param segment, o segment que representa a instancia do modelo
	 * @return ArrayList<ClassUnit> todas as Classes do sistema.
	 */
	public ArrayList<InterfaceUnit> getAllInterfaces(Segment segment) {

		ArrayList<InterfaceUnit> allInterface = new ArrayList<InterfaceUnit>();

		CodeModel codeModel = (CodeModel) segment.getModel().get(0);

		EList<AbstractCodeElement> elements = codeModel.getCodeElement();

		for (int i = 0; i < elements.size() - 1; i++) {

			System.out.println(elements.get(i));

			if (elements.get(i) instanceof Package) {

				Package packageKDM = (Package) elements.get(i);

				this.getInterfaces(packageKDM.getCodeElement(), allInterface);

			}

		}

		return allInterface;

	}
	

	/** 
	 * Esse metodo e responsavel por obter todas as classes da instancia do KDM
	 * @param elements, representa todos os elementos
	 */
	private void getClasses(EList<AbstractCodeElement> elements,
			ArrayList<ClassUnit> allClasses) {

		for (AbstractCodeElement abstractCodeElement : elements) {

			if (abstractCodeElement instanceof ClassUnit) {

				allClasses.add((ClassUnit) abstractCodeElement);

			} else if (abstractCodeElement instanceof Package) {

				Package packageToPass = (Package) abstractCodeElement;

				getClasses(packageToPass.getCodeElement(), allClasses);

			}

		}

	}
	
	/** 
	 * Esse metodo e responsavel por obter todas as classes da instancia do KDM
	 * @param elements, representa todos os elementos
	 */
	private void getInterfaces(EList<AbstractCodeElement> elements,
			ArrayList<InterfaceUnit> allClasses) {

		for (AbstractCodeElement abstractCodeElement : elements) {

			if (abstractCodeElement instanceof InterfaceUnit) {

				allClasses.add((InterfaceUnit) abstractCodeElement);

			} else if (abstractCodeElement instanceof Package) {

				Package packageToPass = (Package) abstractCodeElement;

				getInterfaces(packageToPass.getCodeElement(), allClasses);

			}

		}

	}
	
	
	/** 
	 * Esse metodo e responsavel por obter todos os imports, implements e extends contidos em uma ClassUnit
	 * @param classUnit, que representa uma instancia de uma classe do KDM
	 */
	public ArrayList<KDMRelationship> addImportsImplementsAndExtends(ClassUnit classUnit, ArrayList<Layer> allLayers) {

		EList<AbstractCodeRelationship> allRelationshipsOfTheClass = classUnit.getCodeRelation();

		ArrayList<KDMRelationship> allRelationships = new ArrayList<KDMRelationship>();

		for (AbstractCodeRelationship relationship : allRelationshipsOfTheClass) {

			if (verifyIfRelationContaisLayer( relationship, allLayers)) {
				allRelationships.add(relationship);
			}
			

		}


		return allRelationships;

	}
	
	
	
	/** 
	 * Esse metodo e responsavel por obter todos os metodos dado uma ClassUnit
	 * @param classUnit, que representa uma instancia de uma classe do KDM
	 * @return ArrayList<MethodUnit> todas as Classes do sistema.
	 */
	public ArrayList<MethodUnit> getMethods(ClassUnit classUnit) {

		EList<CodeItem> allElementsOfTheClass = classUnit.getCodeElement();

		ArrayList<MethodUnit> methodUnit = new ArrayList<MethodUnit>();

		for (CodeItem codeItem : allElementsOfTheClass) {

			if (codeItem instanceof MethodUnit) {

				MethodUnit methodUnitToPutIntoTheList = (MethodUnit) codeItem;

				methodUnit.add(methodUnitToPutIntoTheList);

			}

		}

		return methodUnit;

	}
	

	//Falta concluir o Fix neste método
	//Anteriormente ele estava pegando o To somente de interface unit
	//Agora será adaptado para pegar de método também
	
	public HasValue getRelationShipBetweenAnnotation(HasValue hasValue) {
		
		AbstractCodeElement toToVerify = null;
		ArrayList<AbstractCodeRelationship> relations = new ArrayList<AbstractCodeRelationship>();
		Package packageToVerify = null;

		if (hasValue.getTo() instanceof InterfaceUnit) {
			toToVerify = (InterfaceUnit) hasValue.getTo();
		}
		else if (hasValue.getTo() instanceof MethodUnit) {
			toToVerify = (MethodUnit) hasValue.getTo(); 
			return hasValue; 
		}		

		
				
				
		
		if (((Package) toToVerify.eContainer()).getName().equals("lang")) {

			ClassUnit classUnit = (ClassUnit) hasValue.getFrom().eContainer();
			
			EList<AbstractCodeRelationship> allRelation = classUnit.getCodeRelation();
			
			
			
			for (AbstractCodeRelationship abstractCodeRelationship : allRelation) {
				
				if (abstractCodeRelationship instanceof Extends || abstractCodeRelationship instanceof Implements) {
					
					relations.add(abstractCodeRelationship);
					
				}
				
			}
			
			
			for (int i = 0; i < relations.size(); i++) {
		
				KDMEntity to = relations.get(i).getTo();
				
				if (to instanceof ClassUnit) {
					
					ClassUnit classUnitTO = (ClassUnit) to;
					
					List<MethodUnit> allMethods = this.getMethods(classUnitTO);
					
					for (MethodUnit methodUnit : allMethods) {
						
						if (methodUnit.getName().equals(hasValue.getFrom().getName())) {
							
							hasValue.setTo(methodUnit);
							return hasValue;
							
						}
						
					}
					
				} else if (to instanceof InterfaceUnit) {
					
					InterfaceUnit interfaceUnit = (InterfaceUnit) to;
					
					List<MethodUnit> allMethods = this.getMethods(interfaceUnit);
					
					for (MethodUnit methodUnit : allMethods) {
						
						if (methodUnit.getName().equals(hasValue.getFrom().getName())) {
							
							
							hasValue.setTo(methodUnit);
							return hasValue;
							
						}
						
					}
					
					
				}
				
				if (i == (relations.size()-1)) {
					
					System.out.println("Chegou no ultimo");
					
					return null;
					
				}
				
			}
			
			
		}
		return hasValue;

	}
	
	public ClassUnit getClassUnit (Segment segment, String name) {
		
		ArrayList<ClassUnit> allClasses = this.getAllClasses(segment);
		
		ClassUnit classToReturn = null;
		
		for (ClassUnit classUnit : allClasses) {
			if (classUnit.getName().equals(name)) {
				
				classToReturn = classUnit;
				break;
				
			}
		}
		
		return classToReturn;
		
	}
	
	public InterfaceUnit getInterfaceUnit (Segment segment, String name) {
		
		ArrayList<InterfaceUnit> allInterface = this.getAllInterfaces(segment);
		
		InterfaceUnit interfaceToReturn = null;
		
		for (InterfaceUnit interfaceUnit : allInterface) {
			if (interfaceUnit.getName().equals(name)) {
				
				interfaceToReturn = interfaceUnit;
				break;
				
			}
		}
		
		
		return interfaceToReturn;
		
	}
	
	
	
	/** 
	 * Esse metodo e responsavel por obter uma annotation em forma de HasValue.
	 * Caso retorne null, significa que o metodo nao possui annotation.
	 * @param auxMethodUnit, que representa uma instancia de um metodo do KDM
	 * @return HasValue equivalente a uma annotation
	 */
	
	public HasValue fetchAnnotation (CodeItem codeItem) {
		
		EList<AbstractCodeRelationship> abstractCodeRelationships = codeItem.getCodeRelation();
		
		if (abstractCodeRelationships.size() > 0){
		
			if (abstractCodeRelationships.get(0) instanceof HasValue) {
				
				HasValue hasValue = (HasValue) abstractCodeRelationships.get(0);
				
				if (hasValue.getAnnotation().size() > 0) {
					return hasValue;
				}
				
			}
		}
		return null;
	}
	
	/** 
	 * Esse metodo e responsavel por obter todos os parameterUnit dado uma MethodUnit
	 * @param methodUnit, que representa uma instancia de um metodo do KDM
	 * @return ArrayList<ParameterUnit> todos os parametros de um Metodo.
	 */
	public ArrayList<ParameterUnit> fetchAllParameterUnits (MethodUnit methodUnit) {	
		
		ArrayList<ParameterUnit> parameterUnits = new ArrayList<ParameterUnit>();
		
		//Pega primeiro a assinatura do metodo, que esta sempre na posicao 0
		Signature auxSignature = (Signature) methodUnit.getCodeElement().get(0);
		
		
		//Pega a lista de parametros
		EList<ParameterUnit> auxListParameterUnit = auxSignature.getParameterUnit();
		
		
		for (ParameterUnit parameterUnit : auxListParameterUnit) {			
			parameterUnits.add(parameterUnit);			
		}
				
		return parameterUnits;

	}
	

	
	/** 
	 * Esse metodo e responsavel por obter um BlockUnit dado um MethodUnit
	 * @param methodUnit representa uma instancia de um Metodo do KDM
	 * @return BlockUnit retorna o blockUnit
	 */
	public BlockUnit getBlockUnit(MethodUnit methodUnit) {

		EList<AbstractCodeElement> allElementsOfTheMethod = methodUnit
				.getCodeElement();

		if ((allElementsOfTheMethod.size() == 2)
				&& (allElementsOfTheMethod.get(1) instanceof BlockUnit))
			return (BlockUnit) allElementsOfTheMethod.get(1);
		else
			return null;

	}
	
	/** 
	 * Esse metodo e responsavel por obter todos os AbstractActionRelationship (ate o momento Calls e UsesType) dado um BlockUnit
	 * @param blockUnit representa uma instancia de um BlockUnit do KDM
	 * @return List<AbstractActionRelationship>
	 */
	public List<AbstractActionRelationship> getRelations(BlockUnit blockUnit) {

		ArrayList<AbstractActionRelationship> relations = new ArrayList<AbstractActionRelationship>();

		EList<AbstractCodeElement> allElementsOfTheMethod = blockUnit
				.getCodeElement();

		for (AbstractCodeElement codeItem : allElementsOfTheMethod) {

			if (codeItem instanceof ActionElement) {

				relations = getActionsRelationships((ActionElement) codeItem,
						relations);
			}

		}

		return relations;

	}
	
	/** 
	 * Esse metodo e responsavel por obter todos os ActionElement do tipo "variable declaration" dado um BlockUnit
	 * @param blockUnit representa uma instancia de um BlockUnit do KDM
	 * @return List<StorableUnit> 
	 */
	public List<StorableUnit> fetchStorableUnitsFromBlockUnit(BlockUnit blockUnit) {

		ArrayList<StorableUnit> storableUnits = new ArrayList<StorableUnit>();

		EList<AbstractCodeElement> allElementsOfTheBlockUnit = blockUnit.getCodeElement();

		for (AbstractCodeElement auxBlockUnit : allElementsOfTheBlockUnit) {

			if (auxBlockUnit instanceof ActionElement && auxBlockUnit.getName().equals("variable declaration")) {
				ActionElement auxActionElement = (ActionElement) auxBlockUnit;
				
				StorableUnit auxStorableUnit = (StorableUnit) auxActionElement.getCodeElement().get(0);
				
				System.err.println(auxStorableUnit.getName());
				
				//O StorableUnit se encontra no primeiro codeElement do ActionElement "variable declaration"
				storableUnits.add(auxStorableUnit );
			}

		}

		return storableUnits;

	}

	
	/** 
	 * Esse metodo e responsavel por obter todos os AbstractActionRelationship (ate o momento Calls e UsesType) dado um BlockUnit
	 * @param actionElement representa uma instancia do ActionElement
	 * @param relations representa as relacoes
	 * @return List<AbstractActionRelationship>
	 */
	private ArrayList<AbstractActionRelationship> getActionsRelationships(
			ActionElement actionElement, ArrayList<AbstractActionRelationship> relations) {

		EList<AbstractCodeElement> allElements = actionElement.getCodeElement();

		for (AbstractCodeElement codeItem : allElements) {

			if (codeItem instanceof ActionElement) {

				relations = getActionsRelationships((ActionElement) codeItem,
						relations);

				if (((ActionElement) codeItem).getActionRelation() != null) {

					ActionElement element = ((ActionElement) codeItem);

					EList<AbstractActionRelationship> allRelationhips = element
							.getActionRelation();

					for (AbstractActionRelationship abstractActionRelationship : allRelationhips) {

						if (abstractActionRelationship instanceof Calls || abstractActionRelationship instanceof UsesType || (abstractActionRelationship instanceof HasValue && abstractActionRelationship.getAnnotation().size() > 0)) {
							
							ArrayList<Layer> allLayers = getAllLayers(this.segmentMain);
							
							if (verifyIfRelationContaisLayer(abstractActionRelationship, allLayers)) {
								relations.add(abstractActionRelationship);
							}
							
						}						

					}

				}
			}
		}

		return relations;

	}
	
	/** 
	 * Esse metodo e responsavel por verificar se uma instancia da metaclasse KDMRelationship contem uma relacao com um determinado Layer.
	 * @param relationToVerify representa uma instancia da metaclasse KDMRelationship
	 * @param allLayers representa uma instancia de uma ArrayLista que contem todos os layer do sistema
	 * @return boolean
	 */
	private boolean verifyIfRelationContaisLayer (KDMRelationship relationToVerify, ArrayList<Layer> allLayers) {
		
		Package[] packageToAndFrom = getOriginAndDestiny(relationToVerify.getTo(),relationToVerify.getFrom());
		
		if (packageToAndFrom[0] == null || packageToAndFrom[1] == null) {
			return false;
		}
		
		boolean to = false, from = false;
		
		
		for (Layer layer1 : allLayers) {
						
			if (mappingLayerToPackage(layer1, packageToAndFrom[0]))
				to = true;
			
			if (mappingLayerToPackage(layer1, packageToAndFrom[1]))
				from = true;									 					
			
		}
		
		if (to && from)
			return true;
		return false;
	}


	
	/** 
	 * Esse metodo e responsavel por obter todos os Layers que ja foram mapeados no sistema
	 * @param segment representa um segment
	 * @return ArrayList<Layer> 
	 */
	public ArrayList<Layer> getAllLayers(Segment segment) {

		ArrayList<Layer> allLayers = new ArrayList<Layer>();
		StructureModel structureModel = null;
		EList<KDMModel> models = segment.getModel();

		for (KDMModel kdmModel : models) {

			if (kdmModel instanceof StructureModel) {

				structureModel = (StructureModel) kdmModel;
				EList<AbstractStructureElement> allStructureElement = structureModel
						.getStructureElement();

				for (AbstractStructureElement abstractStructureElement : allStructureElement) {
					if (abstractStructureElement instanceof Layer) {

						allLayers.add((Layer) abstractStructureElement);

					}
				}

			}
		}
		return allLayers;

	}
	
	/** 
	 * Esse metodo e responsavel por criar relacionamentos entre elementos arquiteturais. 
	 * @param layers representa o conjunto de elementos arquiteturais.
	 * @param allRelations representa todas as acoes a serem mapeadas na arquitetura. 
	 */
	public void createAggreatedRelationShips(ArrayList<Layer> layers,
			ArrayList<? extends KDMRelationship> allRelations) {

		for (KDMRelationship relation : allRelations) {

			Package[] packageToAndFrom = null;
			
			EObject to = relation.getTo();
			EObject from = relation.getFrom();
			
			/*
			
			if (relation instanceof Calls) {
				to = ((Calls) relation).getTo();
				from = ((Calls) relation).getFrom();
			
			} else if (relation instanceof Extends) {
				to = ((Extends) relation).getTo();
				from = ((Extends) relation).getFrom();
				
			} else if (relation instanceof Implements) {
				
				to = ((Implements) relation).getTo();
				from = ((Implements) relation).getFrom();
			
				
			} else if (relation instanceof Imports) {
				
				to = ((Imports) relation).getTo();
				from = ((Imports) relation).getFrom();
			}
			*/
			
			packageToAndFrom = getOriginAndDestiny(to, from);
			
			//verifica se os pacotes de origem e destino sao iguais, caso sejam, nao cria novo relacionamento
			if (!packageToAndFrom[0].getName().equals(packageToAndFrom[1].getName())) {
				
				for (Layer layers1 : layers) {
					
					//Itera nas layers ate encontrar a layer que corresponde a origem(from) da chamada(call) 
					if (mappingLayerToPackage(layers1, packageToAndFrom[1])) {
	
						//recupera todos os relacionamentos da camada (layer)
						EList<AggregatedRelationship> aggregatedRelationship = layers1.getAggregated();
	
						//verifica se existe algum relacionamento na camada (layer) atual
						if (aggregatedRelationship.size() > 0) {
	
							//caso exista, o algoritmo percorre a lista com o intuito de encontrar algum relacionamento que possua o destino (to) desejado
							for (int i = 0; i < aggregatedRelationship.size(); i++) {
	
								//verifica se o campo TO da CALL existe em algum relacionamento, na pratica ele verifica se o pacote TO
								//da chamada ja esta cadastrado em algum dos relacionamentos
								if (mappingLayerToPackage((Layer) aggregatedRelationship.get(i).getTo(), packageToAndFrom[0])) {
									aggregatedRelationship.get(i).setDensity(aggregatedRelationship.get(i).getDensity() + 1);
									aggregatedRelationship.get(i).getRelation().add(relation);
									break;
	
								} else if ((aggregatedRelationship.size()-1) == i) {
									AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
									newRelationship.setDensity(1);
									newRelationship.setFrom(layers1);
									newRelationship.setTo(verifyLayerOwnerOfPackage(packageToAndFrom[0], layers));
									newRelationship.getRelation().add(relation);
									layers1.getAggregated().add(newRelationship);
									break;
								}
															
							}
	
							
	
						} else { //se nao existir, cria um novo relacionamento
							AggregatedRelationship newRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
							newRelationship.setDensity(1);
							newRelationship.setFrom(layers1);
							newRelationship.setTo(verifyLayerOwnerOfPackage(packageToAndFrom[0], layers));
							newRelationship.getRelation().add(relation);
							layers1.getAggregated().add(newRelationship);
						}
						
						break;
	
					}
					
					//save(segmentMain, "C:/Users/Fernando/Documents/runtime-EclipseApplication/TesteModisco/Examples/MVCBasic_kdm.xmi");
	
				}
			}

		}

	}

	
	/** 
	 * Esse metodo e responsavel por verificar em qual Layer encontra se um determinado Package 
	 * @param packageToGet representa o Package para verificar
	 * @param layers representa todos os elementos arquiteturais do KDM.
	 * @return Layer representa o Layer. 
	 */
	public Layer verifyLayerOwnerOfPackage(Package packageToGet, ArrayList<Layer> layers) {

		String pathToVerifyPackage = getPathOfPackage(packageToGet, "");

		for (Layer layer : layers) {

			EList<KDMEntity> allImplementation = layer.getImplementation();

			for (KDMEntity kdmEntity : allImplementation) {

				String pathToVerifyLayer = "";

				if (kdmEntity instanceof Package) {

					pathToVerifyLayer = getPathOfPackage((Package) kdmEntity,
							pathToVerifyLayer);

				}

				if (pathToVerifyLayer.equals(pathToVerifyPackage)) {
					return layer;
				}

			}

		}

		return null;

	}

	
	/** 
	 * Esse metodo e responsavel por verificar se uma Package e representado por uma Layer 
	 * @param packageToVerify representa o Package para verificar
	 * @param layer representa todos os elementos arquiteturais do KDM.
	 * @return Boolean 
	 */
	public Boolean mappingLayerToPackage(Layer layer, Package packageToVerify) {

		String pathToVerifyPackage = getPathOfPackage(packageToVerify, "");

		EList<KDMEntity> allImplementation = layer.getImplementation();

		for (KDMEntity kdmEntity : allImplementation) {

			String pathToVerifyLayer = "";

			if (kdmEntity instanceof Package) {

				pathToVerifyLayer = getPathOfPackage((Package) kdmEntity,
						pathToVerifyLayer);

			}

			if (pathToVerifyLayer.equals(pathToVerifyPackage)) {

				return true;

			} else
				return false;

		}

		return null;
	}

	
	/** 
	 * Esse metodo e responsavel por obter o caminho completo dado uma instancia de Package 
	 * @param packageToGetThePath representa uma instancia do Package para obter o caminho
	 * @param pathToGet representa uma String no qual sera add o caminho.
	 * @return String
	 */
	public String getPathOfPackage(EObject packageToGetThePath, String pathToGet) {

		if (packageToGetThePath instanceof Package) {

			Package packageToGet = (Package) packageToGetThePath;
			
			pathToGet = getPathOfPackage(
					(EObject) packageToGetThePath.eContainer(), pathToGet);

			if (packageToGetThePath.eContainer() instanceof CodeModel) {
				pathToGet += packageToGet.getName();
			} else {

				pathToGet += "." + packageToGet.getName() ;

			}
			
		} else
			return pathToGet;

		return pathToGet;

	}
	
	/** 
	 * Esse metodo e responsavel por recuperar a origem e o destino de uma determinada acao. Alem disso, a representacao
	 * e dada em Package. Exemplo: pacote de origen da acao, pacote de destino da acao. 
	 * @param callToMap representa uma instancia do Calls
	 * @return Package[], o primeiro elemento e o TO (destino), e o segundo e o FROM (origem)
	 */
	public Package[] getOriginAndDestiny(EObject to, EObject from) {

		Package[] packageToAndFrom = new Package[2];
		Package auxTo = null;
		Package auxFrom = null;
		
		/*Identificamos um erro que quando temos Arraylist (listas), o modisco retorna um TemplateType que o topo de sua arvore
		 * eh um CodeModel ao inves de um Pacote. Porem, eh possivel buscar o codeRelation do TemplateType e assim encontrar o pacote
		 * que instacia esta classe.
		 */
		
		if (to instanceof TemplateType) {
			to = getToOfTemplateType((TemplateType)to);
		}
		if (from instanceof TemplateType) {
			from = getToOfTemplateType((TemplateType)from);
		}
			

		auxTo = getToOrFrom(to, auxTo);
		auxFrom = getToOrFrom(from, auxFrom);
		packageToAndFrom[0] = auxTo;
		packageToAndFrom[1] = auxFrom;

		return packageToAndFrom;


	}
	
	private EObject getToOfTemplateType (TemplateType element) {
		
		return element.getCodeRelation().get(0).getTo();	
	}

	/** 
	 * Esse metodo e responsavel por recuperar a origem e o destino de uma determinada acao. Alem disso, a representacao
	 * e dada em Package. Exemplo: pacote de origen da acao, pacote de destino da acao. 
	 * @param element representa um ControlElement
	 * @param toOrFrom representa o Package
	 * @return Package
	 */
	private Package getToOrFrom(EObject element, Package toOrFrom) {

		if (element instanceof Package) {

			return (Package) element;
			
		} else if (element instanceof Segment) {
			
			return null;
		}
			else {
			toOrFrom = getToOrFrom(element.eContainer(), toOrFrom);

		}

		return toOrFrom;

	}

	
	/** 
	 * Esse metodo e responsavel por recuperar a origem e o destino de uma determinada acao. Alem disso, a representacao
	 * e dada em Package. Exemplo: pacote de origen da acao, pacote de destino da acao. 
	 * @param element representa um ControlElement
	 * @param toOrFrom representa o Package
	 * @return Package
	 */
	public List<ActionRelationship> getRelationships(ActionElement actionElement) {

		EList<AbstractActionRelationship> allElementsOfTheMethod = actionElement
				.getActionRelation();

		List<ActionRelationship> actionRelationships = new ArrayList<ActionRelationship>();

		for (AbstractActionRelationship codeItem : allElementsOfTheMethod) {

			if (codeItem instanceof ActionRelationship) {

				ActionRelationship actionRelationshipToPutIntoTheList = (ActionRelationship) codeItem;

				actionRelationships.add(actionRelationshipToPutIntoTheList);

			}

		}

		return actionRelationships;

	}

	/** 
	 * Esse metodo e responsavel por recuperar todos os metodos que uma InterfaceUnit contem. 
	 * @param interfaceUnit representa uma InterfaceUnit
	 * @return List<MethodUnit>
	 */
	public List<MethodUnit> getMethods(InterfaceUnit interfaceUnit) {

		EList<CodeItem> allElementsOfTheClass = interfaceUnit.getCodeElement();

		List<MethodUnit> methodUnit = new ArrayList<MethodUnit>();

		for (CodeItem codeItem : allElementsOfTheClass) {

			if (codeItem instanceof MethodUnit) {

				MethodUnit methodUnitToPutIntoTheList = (MethodUnit) codeItem;

				methodUnit.add(methodUnitToPutIntoTheList);

			}

		}

		return methodUnit;

	}

	
	/** 
	 * Esse metodo e responsavel por recuperar todos os CallableUnits que uma ClassUnit contem. 
	 * @param classUnit representa uma ClassUnit
	 * @return List<CallableUnit>
	 */
	public List<CallableUnit> getCallableUnits(ClassUnit classUnit) {

		EList<CodeItem> allElementsOfTheClass = classUnit.getCodeElement();

		List<CallableUnit> callableUnits = new ArrayList<CallableUnit>();

		for (CodeItem codeItem : allElementsOfTheClass) {

			if (codeItem instanceof CallableUnit) {

				CallableUnit callableUnitToPutIntoTheList = (CallableUnit) codeItem;

				callableUnits.add(callableUnitToPutIntoTheList);

			}

		}

		return callableUnits;
	}

	public Segment getSegmentMain() {
		return segmentMain;
	}

	public void setSegmentMain(Segment segmentMain) {
		this.segmentMain = segmentMain;
	}

	public ArrayList<ClassUnit> getAllClassUnits() {
		return allClassUnits;
	}

	public void setAllClassUnits(ArrayList<ClassUnit> allClassUnits) {
		this.allClassUnits = allClassUnits;
	}

	public ArrayList<MethodUnit> getAllMethodUnits() {
		return allMethodUnits;
	}

	public void setAllMethodUnits(ArrayList<MethodUnit> allMethodUnits) {
		this.allMethodUnits = allMethodUnits;
	}

	public ArrayList<InterfaceUnit> getAllInterfaceUnit() {
		return allInterfaceUnit;
	}

	public void setAllInterfaceUnit(ArrayList<InterfaceUnit> allInterfaceUnit) {
		this.allInterfaceUnit = allInterfaceUnit;
	}

	public ArrayList<StorableUnit> getAllStorableUnits() {
		return allStorableUnits;
	}

	public void setAllStorableUnits(ArrayList<StorableUnit> allStorableUnits) {
		this.allStorableUnits = allStorableUnits;
	}

	public ArrayList<Package> getAllPackages() {
		return allPackages;
	}

	public void setAllPackages(ArrayList<Package> allPackages) {
		this.allPackages = allPackages;
	}

	public ArrayList<BlockUnit> getAllBlockUnits() {
		return allBlockUnits;
	}

	public void setAllBlockUnits(ArrayList<BlockUnit> allBlockUnits) {
		this.allBlockUnits = allBlockUnits;
	}

	public ArrayList<Calls> getAllCalls() {
		return allCalls;
	}

	public void setAllCalls(ArrayList<Calls> allCalls) {
		this.allCalls = allCalls;
	}

	public ArrayList<Layer> getAllLayers() {
		return allLayers;
	}

	public void setAllLayers(ArrayList<Layer> allLayers) {
		this.allLayers = allLayers;
	}

	public ArrayList<KDMRelationship> getAllRelationships() {
		return allRelationships;
	}

	public void setAllRelationships(ArrayList<KDMRelationship> allRelationships) {
		this.allRelationships = allRelationships;
	}

	public ArrayList<HasType> getAllHasType() {
		return allHasType;
	}
	
	public void setAllHasType(ArrayList<HasType> allHasType) {
		this.allHasType = allHasType;
	}

	public ArrayList<AbstractActionRelationship> getAllAbstractActionRelationships() {
		return allAbstractActionRelationships;
	}
	
	public void setAllAbstractActionRelationships(
			ArrayList<AbstractActionRelationship> allAbstractActionRelationships) {
		this.allAbstractActionRelationships = allAbstractActionRelationships;
	}

	public ArrayList<HasValue> getAllHasValues() {
		return allHasValues;
	}

	public void setAllHasValues(ArrayList<HasValue> allHasValues) {
		this.allHasValues = allHasValues;
	}
	
	public Segment getTargetArchitecture() {
		return targetArchitecture;
	}
	
	public ArrayList<CodeItem> getAllAffectedElements (ArrayList<CodeItem> affectedElements, CodeItem target) {
		
		CodeItem itemToVerify = (CodeItem) target.eContainer();
		
		affectedElements.add(itemToVerify);
		
		//verifica se alcançou um pacote
		if (itemToVerify instanceof Package) {
			return affectedElements;
		}
		else {
			affectedElements = getAllAffectedElements(affectedElements, itemToVerify);
		}
		return affectedElements;
	}
	
	public AbstractStructureElement getAffectedStructureElement (StructureModel structureModel, Package packageTofind) {
		AbstractStructureElement structureElementFound = null;
		
		EList<AbstractStructureElement> structureElements = structureModel.getStructureElement();
		
		for (AbstractStructureElement abstractStructureElement : structureElements) {
			
			EList<KDMEntity> implementation = abstractStructureElement.getImplementation();
			
			for (KDMEntity kdmEntity : implementation) {
				
				if (kdmEntity instanceof Package) {
					Package package1 = (Package) kdmEntity;
					if (package1.getName().equals(packageTofind.getName())) {
						structureElementFound = abstractStructureElement;
						return abstractStructureElement;
					}
				}
				
			}
			
		}
		
		return structureElementFound;
	}
	
	public ArrayList<KDMRelationship> getAffectedsRelationships (CodeItem codeItem, AbstractStructureElement structureElement) {
		ArrayList<KDMRelationship> affectedRelationships = new ArrayList<KDMRelationship>();
		
		EList<AggregatedRelationship> aggregatedRelationships = structureElement.getAggregated();
		
		for (AggregatedRelationship aggregatedRelationship : aggregatedRelationships) {
			
			EList<KDMRelationship> kdmRelationships = aggregatedRelationship.getRelation();
			
			for (KDMRelationship kdmRelationship : kdmRelationships) {				
				boolean result = false;
				//TODO
				//função que suba na árvore até encontrar o codeItem
				if (codeItem instanceof MethodUnit) {
					result = verifyRelationshipMethod(codeItem, kdmRelationship, result);
				} else if (codeItem instanceof ClassUnit) {
					result = verifyRelationshipClass(codeItem, kdmRelationship, result);
				} else if (codeItem instanceof Package) {
					result = verifyRelationshipPackage(codeItem, kdmRelationship, result);
				}
				if (result) {
					affectedRelationships.add(kdmRelationship);
				}				
			}
			
		}
		
		return affectedRelationships;
	}
	
	//verifica se um relationship pertence a um determinado codeitem
	//não garente que o método seja o mesmo, depois devemos subir a árvore para verificar se o elemento encontrado é realmente o mesmo
	public boolean verifyRelationshipClass (KDMEntity item, EObject elementToVerify, boolean result) {		
		
		if (elementToVerify.eContainer() instanceof ClassUnit) {
			ClassUnit classUnit = (ClassUnit) elementToVerify.eContainer();
			
			if(classUnit.getName().equals(item.getName())) {
				result = true;
				return result;
			}
			else {
				result = false;
				return result;
			}
		}
		else {
			result = verifyRelationshipClass(item, elementToVerify.eContainer(), result);
		}
		
		return result;
		
	}
	
	public boolean verifyRelationshipPackage (KDMEntity item, EObject elementToVerify, boolean result) {		
		
		if (elementToVerify.eContainer() instanceof Package) {
			Package packageFound = (Package) elementToVerify.eContainer();
			
			if(packageFound.getName().equals(item.getName())) {
				result = true;
				return result;
			}
			else {
				result = false;
				return result;
			}
		}
		else {
			result = verifyRelationshipPackage(item, elementToVerify.eContainer(), result);
		}
		
		return result;
		
	}
	
	public boolean verifyRelationshipMethod (KDMEntity item, EObject elementToVerify, boolean result) {		
		
		if (elementToVerify.eContainer() instanceof MethodUnit) {
			MethodUnit methodUnit = (MethodUnit) elementToVerify.eContainer();
			
			if(methodUnit.getName().equals(item.getName())) {
				result = true;
				return result;
			}
			else {
				result = false;
				return result;
			}
		}
		else {
			result = verifyRelationshipMethod(item, elementToVerify.eContainer(), result);
		}
		
		return result;	
	}
	
	
	
}
