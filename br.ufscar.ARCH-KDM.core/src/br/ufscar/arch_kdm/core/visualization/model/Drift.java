/**
 * @author Landi
 * 
 */
package br.ufscar.arch_kdm.core.visualization.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Landi
 *
 */
public class Drift {

	private String name = "";
	private String packagePath = "";
	private String className = "";
	private String methodName = "";
	private String possibleLoC;

	private List<Violations> violations;

	public Drift(String name) {
		this.violations = new ArrayList<>();
		this.name = name;
	}
	
	public void addViolation(Violations violation){
		violations.add(violation);
	}
	
	/**
	 * @return the violations
	 */
	public List<Violations> getViolations() {
		return violations;
	}

	/**
	 * @param violations the violations to set
	 */
	public void setViolations(List<Violations> violations) {
		this.violations = violations;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the packagePath
	 */
	public String getPackagePath() {
		return packagePath;
	}

	/**
	 * @param packagePath the packagePath to set
	 */
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
	 * @author Landi
	 * @return the actions
	 */
	public String getAction() {
		String actions = "";
		for (Violations violation : violations) {
			actions = actions.concat("[" + violation.getAction() + "] ");
		}
		return actions;
	}
	
}
