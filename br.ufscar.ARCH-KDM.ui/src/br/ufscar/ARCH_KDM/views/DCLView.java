package br.ufscar.ARCH_KDM.views;

import java.util.ArrayList;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import br.ufscar.ARCH_KDM.actions.ActionRecoveryArchitecture;

public class DCLView extends ViewPart {

	public static final String ID = "com.br.ufscar.dc.ui.DCLView"; //$NON-NLS-1$

	private Graph graph;
	
	private int layout = 1;
	
	private ArrayList<GraphNode> allNodes = new ArrayList<GraphNode>();
	
	private EList<AbstractStructureElement> allElements = null;
	
	private ArrayList<AggregatedRelationship> allAggregatedRelationships = new ArrayList<AggregatedRelationship>();
	
	public DCLView() {
	}
	
	

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		// Graph will hold all other objects
		graph = new Graph(container, SWT.NONE);
		graph.setBounds(0, 0, 1050, 435);
		
		Button btnNewButton = new Button(graph, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				System.out.println(graph.getSelection());
				
				createContainer2(graph, (GraphConnection) graph.getSelection().get(0));
				
			}
		});
		btnNewButton.setBounds(10, 10, 106, 28);
		btnNewButton.setText("Show Relations");
		
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

		createActions();
		initializeToolBar();
		initializeMenu();
		
		this.draw(parent);
	}
	
	
	private void draw (Composite parent) {
		
		StructureModel structureModelToDraw  = (StructureModel) ActionRecoveryArchitecture.plannedSegment.getModel().get(3);
		
		this.allElements = structureModelToDraw.getStructureElement();
		
		this.allNodes = new ArrayList<GraphNode>();
		
		for (AbstractStructureElement abstractStructureElement : this.allElements) {
		
			GraphNode node = new GraphNode(graph, SWT.NONE, abstractStructureElement.getName());
			
			this.allNodes.add(node);
			
		}
		
		this.allAggregatedRelationships = new ArrayList<AggregatedRelationship>();
		
		for (AbstractStructureElement abstractStructureElement : this.allElements) {
			
			if (abstractStructureElement.getAggregated().size() > 0) {
				
				this.allAggregatedRelationships.addAll(abstractStructureElement.getAggregated());
				
			}
			
		}
		
		for (AggregatedRelationship aggregatedRelationship : this.allAggregatedRelationships) {
		
			KDMEntity from = aggregatedRelationship.getFrom();
			KDMEntity to = aggregatedRelationship.getTo();
			
			GraphNode nodeFROM = null;
			
			GraphNode nodeTO = null;
			
			for (GraphNode node : this.allNodes) {
				
				if (node.getText().equals(from.getName())) {
					
					nodeFROM = node;
					
				} 
				
				if (node.getText().equals(to.getName())) {
					
					nodeTO = node;
					
				}
				
			}
		
			graph.addListener(SWT.MouseDoubleClick, new Listener() {
				
				@Override
				public void handleEvent(Event event) {
					
					System.out.println("Selecionou sim " + graph.getSelection());
			
					System.out.println("Count number " +event.count);
					
					
//					createContainer2(graph);
					
					GraphNode grapNode = (GraphNode) graph.getSelection().get(0);
					grapNode.dispose();
					
//					System.out.println("O Evento é " + event);
					
					
					
//					try {
//						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.br.ufscar.dc.ui.RelationShipView");
//					} catch (PartInitException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
				}
			});
			
//			graph.addSelectionListener(new SelectionAdapter() {
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					System.out.println(e);
//					System.out.println(e.getSource().getClass());
//					if (e.getSource() instanceof Graph) {
//						
//						Graph teste = (Graph) e.getSource();
//						
//						System.out.println("The selection is " + teste.getSelection());
//						
//						GraphNode pegou = (GraphNode)teste.getSelection().get(0);
//						
//						System.out.println(pegou.getText());
//						
//						System.out.println(teste.getChildren());
//						System.out.println(teste.getChildren().length);
//						
//						System.out.println("SIm é ");
//					}
//					System.out.println("rafa");
//				}
//
//			});
			
			IFigure tooltip1 = new Label("Density");
			
			GraphConnection graphConnection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodeFROM,
					nodeTO);
			graphConnection.setTooltip(tooltip1);
			graphConnection.setText(aggregatedRelationship.getDensity().toString());
			graphConnection.setLineColor(new org.eclipse.swt.graphics.Color(Display.getCurrent(), 15, 39, 40));
			
			
		}
		
		
		
		
		
		
	}

	public void setLayoutManager() {
		switch (layout) {
		case 1:
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
					LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout++;
			break;
		case 2:
			graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
					LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout = 1;
			break;

		}

	}

	
	public void createContainer2 (Graph graph, GraphConnection connection) {
		
		String destination = connection.getDestination().getText();
		String source = connection.getSource().getText();
		
		GraphContainer graphContainer = new GraphContainer(graph, SWT.NONE, "AR for " + source + " to " + destination);
		
		AggregatedRelationship selected = null;
		
		for (AggregatedRelationship aggregatedRelationship : this.allAggregatedRelationships) {
			
			KDMEntity from = aggregatedRelationship.getFrom();
			KDMEntity to = aggregatedRelationship.getTo();
			
			if (from.getName().equals(source) && to.getName().equals(destination)) {
				selected = aggregatedRelationship;
				break;
			}
		
		}
		
		GraphNode node;
		
		EList<KDMRelationship> relationsToAdd = selected.getRelation();
		
		for (KDMRelationship relation : relationsToAdd) {
			
			String [] nameCorrected = relation.getClass().getName().split("\\.");
			String nameCorrected2 = nameCorrected[nameCorrected.length-1];
			nameCorrected2 = nameCorrected2.substring(0, nameCorrected2.length()-4);
			
			node = new GraphNode(graphContainer, ZestStyles.NODES_NO_ANIMATION, nameCorrected2);
			node.setBackgroundColor(chooseColor(nameCorrected2));
		}
		
		graphContainer.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	private Color chooseColor (String relation) {
		
		if (relation.equals("Calls")) {
			return new org.eclipse.swt.graphics.Color(Display.getCurrent(), 0, 128, 0);
		} else if (relation.equals("Imports")) {
			return new org.eclipse.swt.graphics.Color(Display.getCurrent(), 128, 128, 128);
		} else if (relation.equals("Implements")) {
			return new org.eclipse.swt.graphics.Color(Display.getCurrent(), 0, 128, 128);
		} else if (relation.equals("HasType")) {
			return new org.eclipse.swt.graphics.Color(Display.getCurrent(), 192, 192, 192);
		} else if (relation.equals("HasValue")) {
			return new org.eclipse.swt.graphics.Color(Display.getCurrent(), 128, 0, 0);
		} else if (relation.equals("UsesType")) {
			return new org.eclipse.swt.graphics.Color(Display.getCurrent(), 128, 128, 0);
		} else {
			return new org.eclipse.swt.graphics.Color(Display.getCurrent(), 15, 39, 40);
		}
		
	}
	
	
	
	
	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
