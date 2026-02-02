package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.template;

/**
 * Created by averazub on 5/24/2016.
 */
public class Xml4DOperationsFactory {

    public Xml4DOperations create(String host, Integer port, String userName, String password, boolean isSsl) {
        Xml4DConnectionFactory connectionFactory = new Xml4DConnectionFactory(host, port, isSsl, userName, password);
        return new Xml4DOperations(connectionFactory);
    }

}
