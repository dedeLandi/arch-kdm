/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.visualization.model;

import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;

/**
 * @author Landi
 *
 */
public class Violations {

	private String action = "";
	private String possibleLoC = "";
	
	private Drift driftOwner;
	
	private KDMRelationship violation;

	public Violations(String action, Drift driftOwner, KDMRelationship violation) {
		this.action = action;
		this.driftOwner = driftOwner;
		this.violation = violation;
	}
	
	/**
	 * @return the violation
	 */
	public KDMRelationship getViolation() {
		return violation;
	}

	/**
	 * @return the driftOwner
	 */
	public Drift getDriftOwner() {
		return driftOwner;
	}
	
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @return the possibleLoC
	 */
	public String getPossibleLoC() {
		return possibleLoC;
	}
	
	/**
	 * @param possibleLoC the possibleLoC to set
	 */
	public void setPossibleLoC(String possibleLoC) {
		this.possibleLoC = possibleLoC;
	}
	
	/**
	 * @return the packagePath
	 */
	public String getPackagePath() {
		return driftOwner.getPackagePath();
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return driftOwner.getClassName();
	}
	
	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return driftOwner.getMethodName();
	}
	
}
