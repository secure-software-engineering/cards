package de.uni_paderborn.swt.cards.result;

public class AnalysisResult {
	private String timestamp;
	private String assumptionId;
	private boolean pass;
	private String[] violations;
	
	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the admissionId
	 */
	public String getAssumptionId() {
		return assumptionId;
	}
	
	/**
	 * @param admissionId the admissionId to set
	 */
	public void setAssumptionId(String admissionId) {
		this.assumptionId = admissionId;
	}
	
	/**
	 * @return the pass
	 */
	public boolean isPass() {
		return pass;
	}
	
	/**
	 * @param pass the pass to set
	 */
	public void setPass(boolean pass) {
		this.pass = pass;
	}
	
	/**
	 * @return the violations
	 */
	public String[] getViolations() {
		return violations;
	}
	
	/**
	 * @param violations the violations to set
	 */
	public void setViolations(String[] violations) {
		this.violations = violations;
	}
}
