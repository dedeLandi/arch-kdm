/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.ui.wizardsPage;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import br.ufscar.arch_kdm.core.visualization.VisualizeDrifts;
import br.ufscar.arch_kdm.core.visualization.model.Drift;
import br.ufscar.arch_kdm.core.visualization.model.Violations;
import br.ufscar.arch_kdm.ui.util.IconsType;
import br.ufscar.arch_kdm.ui.util.InterfaceGenericMethods;
import br.ufscar.arch_kdm.ui.visualization.groupingTypes.GroupingAlgorithmTypes;
import br.ufscar.arch_kdm.ui.wizards.ArchKDMWizard;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

/**
 * @author Landi
 *
 */
public class Page05ViewDrifts extends WizardPage {
	private Text tfClass;
	private Text tfMethod;
	private Text tfActions;
	private Text tfPackage;
	private Text tfLoC;
	private Tree treeDriftsFounded;

	private Combo cbAlgoType;
	private Object config;


	/**
	 * Create the wizard.
	 */
	public Page05ViewDrifts() {
		super("page05");
		setTitle("Architectural Compilance Checking Wizard");
		setDescription("Viewing of the violations founded");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label lblDrifts = new Label(composite, SWT.NONE);
		lblDrifts.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblDrifts.setText("Drifts");

		treeDriftsFounded = new Tree(composite, SWT.BORDER);
		treeDriftsFounded.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeDriftsFounded.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite_1 = new Composite(scrolledComposite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));

		cbAlgoType = new Combo(composite_1, SWT.READ_ONLY);
		cbAlgoType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fillCbAlgoType();

		Button bConfigureMLAlgo = new Button(composite_1, SWT.NONE);
		bConfigureMLAlgo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				configureAlgoritm();
			}
		});
		bConfigureMLAlgo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		bConfigureMLAlgo.setText("Configure ML Algo");

		Button bEvaluateDrifts = new Button(composite_1, SWT.NONE);
		bEvaluateDrifts.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				executeMLAlgo();
			}
		});
		bEvaluateDrifts.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		bEvaluateDrifts.setText("Evaluate Drifts");

		Label lblDetails = new Label(composite_1, SWT.NONE);
		lblDetails.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		lblDetails.setText("Details");

		Label lblPackage = new Label(composite_1, SWT.NONE);
		lblPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPackage.setText("Package:");

		tfPackage = new Text(composite_1, SWT.BORDER);
		tfPackage.setEditable(false);
		tfPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblClass = new Label(composite_1, SWT.NONE);
		lblClass.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClass.setText("Class:");

		tfClass = new Text(composite_1, SWT.BORDER);
		tfClass.setEditable(false);
		tfClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblMethod = new Label(composite_1, SWT.NONE);
		lblMethod.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMethod.setText("Method:");

		tfMethod = new Text(composite_1, SWT.BORDER);
		tfMethod.setEditable(false);
		tfMethod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblActions = new Label(composite_1, SWT.NONE);
		lblActions.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblActions.setText("Actions:");

		tfActions = new Text(composite_1, SWT.BORDER);
		tfActions.setEditable(false);
		tfActions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblPossibleLineOf = new Label(composite_1, SWT.NONE);
		lblPossibleLineOf.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPossibleLineOf.setText("Possible LoC:");

		tfLoC = new Text(composite_1, SWT.BORDER);
		tfLoC.setEditable(false);
		tfLoC.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scrolledComposite.setContent(composite_1);
		scrolledComposite.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * @author André
	 */
	private void fillCbAlgoType() {
		for (GroupingAlgorithmTypes algo : GroupingAlgorithmTypes.values()) {
			cbAlgoType.add(algo.getDescription());
		}
		
		cbAlgoType.select(0);
	}

	/**
	 * @author Landi
	 */
	protected void fillData() {
		Object data[] = new Object[1];

		TreeItem[] selection2 = treeDriftsFounded.getSelection();
		for (TreeItem treeItem : selection2) {
			data[0] = treeItem.getData();
		}

		if(data[0] instanceof Drift){
			fillData((Drift) data[0]);
		}else if(data[0] instanceof Violations){
			fillData((Violations) data[0]);
		}

	}

	/**
	 * @author Landi
	 * @param violations
	 */
	private void fillData(Violations violations) {
		tfPackage.setText(violations.getPackagePath());
		tfClass.setText(violations.getClassName());
		tfMethod.setText(violations.getMethodName());
		tfActions.setText(violations.getAction());
		tfLoC.setText(violations.getPossibleLoC());
	}

	/**
	 * @author Landi
	 * @param drift
	 */
	private void fillData(Drift drift) {
		tfPackage.setText(drift.getPackagePath());
		tfClass.setText(drift.getClassName());
		tfMethod.setText(drift.getMethodName());
		tfActions.setText(drift.getAction());
		tfLoC.setText(drift.getPossibleLoC());
	}

	/**
	 * @author Landi
	 */
	protected void configureAlgoritm() {
		config = GroupingAlgorithmTypes.getAlgo(cbAlgoType.getText()).configAlgo();
	}

	/**
	 * @author Landi
	 */
	protected void executeMLAlgo() {
		if(config == null){
			config = GroupingAlgorithmTypes.getAlgo(cbAlgoType.getText()).configAlgoDefault();
		}
		List<Drift> drifts = GroupingAlgorithmTypes.getAlgo(cbAlgoType.getText()).execAlgo(this.getWizard(), config);
		fillDrifts(drifts);
		config = null;
	}

	/**
	 * @author Landi
	 * @param drifts
	 */
	public void fillDrifts(List<Drift> drifts) {
		treeDriftsFounded.removeAll();
		TreeItem treeItemParent = null;
		for (Drift drift : drifts) {

			treeItemParent = new TreeItem(treeDriftsFounded, 0);
			treeItemParent.setImage(IconsType.ERROR.getImage());
			treeItemParent.setText("[" + drift.getName() + "] ");
			treeItemParent.setData(drift);

			fillViolations(treeItemParent, drift);

		}
	}

	/**
	 * @author Landi
	 * @param treeItemParent
	 * @param drift
	 */
	private void fillViolations(TreeItem treeItemParent, Drift drift) {
		TreeItem treeItemChild = null;
		for (Violations violation : drift.getViolations()) {

			treeItemChild = new TreeItem(treeItemParent, 0);
			treeItemChild.setImage(IconsType.RECOVERY.getImage());
			treeItemChild.setText("[" + violation.getViolation().eClass().getName() + "] " + "anonymous");
			treeItemChild.setData(violation);

		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		return null;
	}

}
