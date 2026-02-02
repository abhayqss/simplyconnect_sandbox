package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.template;

import com.scnsoft.eldermark.framework.Pair;
import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.CommunicationRequest;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema.CommunicationResponse;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by averazub on 5/24/2016.
 */
public class Xml4DParser {

    private static final String SQL_STRING_ENCODING = "B64";
    private static final Integer REQUEST_VERSION = 1;
    private static final Integer REQUEST_SQL_VERSION = 5;
    private static final Integer ROLLBACK_IF_PROBLEMS = 0;

    // TODO use other Base64 implementation.
    // Read "Why Developers Should Not Write Programs That Call 'sun' Packages" for explanation http://www.oracle.com/technetwork/java/faq-sun-packages-142232.html
    private static BASE64Encoder encoder;
    private static BASE64Decoder decoder;
    private static Long requestId;

    static Marshaller jaxbRequestMarshaller;
    static Unmarshaller jaxbResponseUnmarshaller;


    static {
        encoder = new BASE64Encoder();
        decoder = new BASE64Decoder();
        requestId = 1l;
        try {
            JAXBContext jaxbRequestContext = JAXBContext.newInstance(CommunicationRequest.class);
            JAXBContext jaxbResponseContext = JAXBContext.newInstance(CommunicationResponse.class);
            jaxbRequestMarshaller = jaxbRequestContext.createMarshaller();
            jaxbResponseUnmarshaller = jaxbResponseContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }



    public static Pair<Long, String> serializeRequest(String sql, ResultListParameters resultListParameters, boolean isSingleResult,
                                               CommunicationRequest.Authentication authentication, Long sqlRequestId) {
        CommunicationRequest request = new CommunicationRequest();
        request.setRequestid(requestId++);
        request.setVersion(REQUEST_VERSION);
        request.setAuthentication(authentication);

        CommunicationRequest.Sqlrequest sqlrequest = new CommunicationRequest.Sqlrequest();
        sqlrequest.setVersion(REQUEST_SQL_VERSION);
        sqlrequest.setRollbackifproblems(ROLLBACK_IF_PROBLEMS);
        sqlrequest.setRequestid(sqlRequestId++);

        CommunicationRequest.Sqlrequest.Sql sqlTag = new CommunicationRequest.Sqlrequest.Sql();
        sqlTag.setEncoding(SQL_STRING_ENCODING);
        sqlTag.setValue(encoder.encode(sql.getBytes()));
        sqlrequest.setSql(sqlTag);

        CommunicationRequest.Sqlrequest.Returnvarlist returnvarlist = new CommunicationRequest.Sqlrequest.Returnvarlist();
        List<CommunicationRequest.Sqlrequest.Returnvarlist.Var> varList = returnvarlist.getVar();

        for (Integer counter=0; counter < resultListParameters.getParameterCount(); counter++) {
            ResultListParameters.Parameter parameter = resultListParameters.getParameterMap().get(counter);
            CommunicationRequest.Sqlrequest.Returnvarlist.Var resultVar =new CommunicationRequest.Sqlrequest.Returnvarlist.Var();
            resultVar.setName(parameter.getColumnName());
            resultVar.setNum(counter+1);
            ColumnType4D columnType4D = parameter.getType();
            if (isSingleResult) {
                resultVar.setType(columnType4D.getSingleTypeSource());
            } else {
                resultVar.setType(columnType4D.getMultipleTypeSource());
            }

            varList.add(resultVar);
        }
        sqlrequest.setReturnvarlist(returnvarlist);

        request.setSqlrequest(sqlrequest);

        try {
            StringWriter sw = new StringWriter();
            jaxbRequestMarshaller.marshal(request, sw);
            return new Pair<Long, String>(requestId, sw.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static CommunicationResponse parseResponse(String responseXmlStr) {
        try {
            StringReader reader = new StringReader(responseXmlStr);
            return (CommunicationResponse)jaxbResponseUnmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

}
