package com.scnsoft.eldermark.entity.document.ccd.codes;

import com.scnsoft.eldermark.entity.ConceptDescriptor;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;

/**
 * Combined value set for Problem status code
 * HITSP Problem Status - 2.16.840.1.113883.3.88.12.80.68
 * CCD Problem Status - 2.16.840.1.113883.1.11.20.13
 */
public enum ProblemStatusCode implements ConceptDescriptor {
	ACTIVE("Active", "55561003"),
	/**
	 * An inactive problem refers to one that is quiescent, and may appear again in future.
	 */
	INACTIVE("Inactive", "73425007"),
	/**
	 * A resolved problem refers to one that used to affect a patient, but does not any more.
	 */
	RESOLVED("Resolved", "413322009"),

	CHRONIC("Chronic", "90734009"),
	INTERMITTENT("Intermittent", "7087005"),
	RECURRENT("Recurrent", "255227004"),
	RULE_OUT("Rule out", "415684004"),
	RULED_OUT("Ruled out", "410516002");

	private final String codeText;
	private final String codeOid;

	ProblemStatusCode(String codeText, String codeOid) {
        this.codeText = codeText;
        this.codeOid = codeOid;
    }

	public String getDisplayName() {
		return codeText;
	}

	public String getCode() {
		return codeOid;
	}

	public String getCodeSystem() {
		return CodeSystem.SNOMED_CT.getOid();
	}

	public String getCodeSystemName() {
		return CodeSystem.SNOMED_CT.getDisplayName();
	}

	public static ProblemStatusCode getByText(String text) {
		if (text == null) {
			return null;
		}

		for (ProblemStatusCode code : ProblemStatusCode.values()) {
			if (text.equalsIgnoreCase(code.getDisplayName())) {
				return code;
			}
		}

		return null;
	}
}
