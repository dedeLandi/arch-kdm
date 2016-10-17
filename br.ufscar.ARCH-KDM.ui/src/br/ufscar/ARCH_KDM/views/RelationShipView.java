package br.ufscar.ARCH_KDM.views;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

public class RelationShipView extends ViewPart {

	public static final String ID = "com.br.ufscar.dc.ui.RelationShipView"; //$NON-NLS-1$

	public Graph graph;
	
	public RelationShipView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);

		// Graph will hold all other objects
		this.graph = new Graph(container, SWT.NONE);
		this.graph.setBounds(0, 0, 1050, 435);
		RelationShipView.createActionAfterClicks(graph);
		
		createActions();
		initializeToolBar();
		initializeMenu();
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
	
	public static void createActionAfterClicks (Graph g) {
		
		
		createContainer2(g);

		CompositeLayoutAlgorithm compositeLayoutAlgorithm = new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new LayoutAlgorithm[] { new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
		//g.setLayoutAlgorithm(new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		g.setLayoutAlgorithm(compositeLayoutAlgorithm, true);

	
	}
	
	public static void createContainer2 (Graph graph) {
		
		GraphContainer graphContainer = new GraphContainer(graph, SWT.NONE, "AggregatedRealationShip");
		
		GraphNode a1 = new GraphNode(graphContainer, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "Extends");
		GraphNode a2 = new GraphNode(graphContainer, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "Calls");
		GraphNode a3 = new GraphNode(graphContainer, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "Implements");
		GraphNode a4 = new GraphNode(graphContainer, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "Imports");
	
		
		
		graphContainer.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}
	
	

	public static void createContainer(Graph g) {
		GraphContainer a = new GraphContainer(g, SWT.NONE, "SomeClass.java");
		int r = (int) ((Math.random() * 3) + 1);
		r = 2;
		populateContainer(a, g, r, true);
		for (int i = 0; i < 4; i++) {
			GraphContainer b = new GraphContainer(g, SWT.NONE, "SomeNestedClass.java");
			r = (int) ((Math.random() * 3) + 1);
			r = 2;
			populateContainer(b, g, r, false);
			new GraphConnection(g, SWT.NONE, a, b);
			for (int j = 0; j < 4; j++) {
				GraphContainer c = new GraphContainer(g, SWT.NONE, "DefaultAction.java");
				r = (int) ((Math.random() * 3) + 1);
				r = 2;
				populateContainer(c, g, r, true);
				new GraphConnection(g, SWT.NONE, b, c);
			}
		}
	}

	public static void populateContainer(GraphContainer c, Graph g, int number, boolean radial) {
		GraphNode a = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "SomeClass.java");
		for (int i = 0; i < 4; i++) {
			GraphNode b = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "SomeNestedClass.java");
			new GraphConnection(g, SWT.NONE, a, b);
			for (int j = 0; j < 4; j++) {
				GraphNode d = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "DefaultAction.java");
				new GraphConnection(g, SWT.NONE, b, d);
				if (number > 2) {
					for (int k = 0; k < 4; k++) {
						GraphNode e = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "LastAction(Hero).java");
						new GraphConnection(g, SWT.NONE, d, e);
						if (number > 3) {
							for (int l = 0; l < 4; l++) {
								GraphNode f = new GraphNode(c, ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT, "LastAction(Hero).java");
								new GraphConnection(g, SWT.NONE, e, f);
							}
						}
					}
				}
			}
		}
		if (number == 1) {
			c.setScale(0.75);
		} else if (number == 2) {
			c.setScale(0.50);
		} else {
			c.setScale(0.25);
		}
		if (radial) {
			c.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		} else {
			c.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		}
	}
}
