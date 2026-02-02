package com.scnsoft.eldermark.services.hl7;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import com.scnsoft.eldermark.entity.hl7.Hl7Message;
import org.springframework.stereotype.Service;

@Service
public class ParsingService {

    private HapiContext context = new DefaultHapiContext();

    public Hl7Message parseMessage(String message) throws Exception {
        /*String messagev25 =  "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
          			+ "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
          			+ "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
          			+ "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
          			+ "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
          			+ "OBX|1|ST|||Test Value";*/
        /*String messagev25 = "MSH|^~\\&||^2.16.840.1.113883.19.3.1^ISO||^2.16.840.1.113883.19.3.2^ISO|20070701132554-0400||ORU^R01^ORU_R01|20070701132554000008|P|2.5.1|||AL|NE|||||USLabReport^^2.16.840.1.113883.19.9.7^ISO\r"
         + "PID|1||36363636^^^&2.16.840.1.113883.19.3.2&ISO^MR||Everywoman^Eve^E^^^^L ||19750410|F||2131-1^Other Race^HL70005|2222 Home Street^^Ann Arbor^MI^48103||^^^^^555^5552003\r"
         + "OBR|1|20070701113255409^EHR^2.16.840.1.113883.19.3.2.3^ISO|2007070111132896^Lab^2.16.840.1.113883.19.3.1.6^ISO|14134-1^Hemoglobin^LN|||20070701123054-0400|||||||20070701130030-0400||000100^Hippocrates^Harold^H^^Dr^MD^^NPPES&2.16.840.1.113883.19.4.6&ISO^^^^NPI||||||20070701132554-0400||CH|F\r"
         + "OBX|1|NM|14134-1^Hemoglobin^LN||12.4|g/dL^^UCUM|12.0-16.0||||F||||||||||||Reliable Labs, Inc|3434 Industrial Loop^^Ann Arbor^MI^48103^^B|9876543^Slide^Stan^S^^^^^NPPES&2.16.840.1.113883.19.4.6&ISO^^^^NPI";
        */
        //hapiContext.setValidationRuleBuilder(new NoValidationBuilder());
        CanonicalModelClassFactory mcf = new CanonicalModelClassFactory("2.5");
        context.setModelClassFactory(mcf);

        PipeParser parser = context.getPipeParser();
        ca.uhn.hl7v2.model.v25.message.ORU_R01 msg = (ca.uhn.hl7v2.model.v25.message.ORU_R01) parser.parse(message);
        Hl7Message hl7Message = new Hl7Message(msg.getMSH(),
                msg.getPATIENT_RESULT().getPATIENT().getPID(),
                msg.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR(),
                msg.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION().getOBX());
        System.out.println(msg.getMSH().getMessageControlID().getValue());
        return hl7Message;
    }

}
