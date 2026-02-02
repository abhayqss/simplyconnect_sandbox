package org.openhealthtools.openxds.registry;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import org.apache.log4j.Logger;
import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;

public class CustomPipeParser extends CopyOfPipeParserWithConstructor {
    private XdsRegistryPatientService registryPatientService;
    private static final Logger logger = Logger.getLogger(CustomPipeParser.class);

    public CustomPipeParser() {
        super(new ExchangeCustomModelClassFactory());
        registryPatientService = (XdsRegistryPatientService) XdsFactory.getInstance().getBean("registryPatientService");
        setValidationContext(new CustomValidation());
    }

    @Override
    public String getVersion(String message) throws HL7Exception {
        registryPatientService.saveRawXdsMessage(message);
        String messageVersion = super.getVersion(message);
        //CCN-2847
        if (!"2.3.1".equals(messageVersion)) {
            logger.info("Setting 2.3.1 version. Incoming message version is " + messageVersion);
        }
        return "2.3.1";
    }

    public static class ExchangeCustomModelClassFactory extends DefaultModelClassFactory {

        @Override
        /**
         * Set isExplicit to true so that model factory does not try to find class by name and uses name
         * as actual class name.
         *
         * Version 2.3 of hapi provides CustomModelClassFactory with method which allows to set custom path to
         * mapping property file, which is used to defined class if structure was not explicitly specified in message.
         * 
         */
        public Class getMessageClass(String name, String version, boolean isExplicit) throws HL7Exception {
            return super.getMessageClass(name, version, true);
        }
    }

}
