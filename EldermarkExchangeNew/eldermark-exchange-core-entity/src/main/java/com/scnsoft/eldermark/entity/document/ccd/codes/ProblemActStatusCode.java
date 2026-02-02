package com.scnsoft.eldermark.entity.document.ccd.codes;

import com.scnsoft.eldermark.entity.ConceptDescriptor;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;

public enum ProblemActStatusCode implements ConceptDescriptor {

   /**
    * A concern that is still being tracked.
    */
   ACTIVE("Active", "active"),
   /**
    * A concern that is active, but which may be set aside.
    * For example, this value might be used to suspend concern about a patient problem after some period of remission,
    * but before assumption that the concern has been resolved.
    */
   SUSPENDED("Suspended", "suspended"),
   /**
    * A concern that is no longer actively being tracked, but for reasons other than because the problem was resolved.
    * This value might be used to mark a concern as being aborted after a patient leaves care against medical advice.
    */
   ABORTED("Aborted", "aborted"),
   /**
    * The problem, allergy or medical state has been resolved and the concern no longer needs to be tracked except for historical purposes.
    */
   COMPLETED("Completed", "completed");

   private final String codeText;
   private final String codeOid;

   ProblemActStatusCode(String codeText, String codeOid) {
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
       return CodeSystem.HL7_ACT_STATUS.getOid();
   }

   public String getCodeSystemName() {
       return CodeSystem.HL7_ACT_STATUS.getDisplayName();
   }

   public static ProblemActStatusCode getByCode(String code) {
       if (code == null) {
           return null;
       }

       for (ProblemActStatusCode statusCode : ProblemActStatusCode.values()) {
           if (code.equalsIgnoreCase(statusCode.getCode())) {
               return statusCode;
           }
       }

       return null;
   }
}
