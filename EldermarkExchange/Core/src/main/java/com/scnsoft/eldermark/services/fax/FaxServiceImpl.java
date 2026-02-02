package com.scnsoft.eldermark.services.fax;

import com.scnsoft.eldermark.shared.carecoordination.service.FaxDto;
import net.interfax.outbound.Sendfax;
import net.interfax.outbound.SendfaxResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * Created by pzhurba on 09-Dec-15.
 */
@Service
public class FaxServiceImpl implements FaxService {
    private static final Logger logger = LoggerFactory.getLogger(FaxServiceImpl.class);

    @Value("${interfax.username}")
    private String interfaxUsername;

    @Value("${interfax.password}")
    private String interfaxPassword;

    final static String FILE_TYPE = "PDF";


    @Override
    public <T> Future<Boolean> sendFax(final FaxDto faxDto, T dto, FaxContentGenerator<T> faxContentGenerator) {
        if (StringUtils.isBlank(interfaxUsername) || StringUtils.isBlank(interfaxPassword)) {
            logger.warn("Can't send fax, because sender not configured");
            return new AsyncResult<Boolean>(false);
        } else if (StringUtils.isBlank(faxDto.getFax())) {
            logger.warn("Can't send fax, because destination number not defined");
            return new AsyncResult<Boolean>(false);
        }
        try {
            net.interfax.outbound.InterFaxSoapStub theBinding = (net.interfax.outbound.InterFaxSoapStub) new net.interfax.outbound.InterFaxLocator().getInterFaxSoap();
            theBinding.setTimeout(60000);
            logger.info("Sending Fax using sendCharFax()");

            //preparePdfTemplate(faxDto, eventDetails);

            //no need to generate fax content before validation therefore passing necessary faxContentGenerator as argument
            Sendfax theParams = new Sendfax(interfaxUsername,
                    interfaxPassword,
                    faxDto.getFax(),
                    faxContentGenerator.generateFaxContent(faxDto, dto),
                    FILE_TYPE);

            final SendfaxResponse theResponse = theBinding.sendfax(theParams);
            logger.info("sendCharFax() call returned code: {} ", theResponse.getSendfaxResult());
            return new AsyncResult<Boolean>(true);
        } catch (Exception e) {
            logger.error("Error sending Fax ", e);
            return new AsyncResult<Boolean>(false);
        }

    }
}
