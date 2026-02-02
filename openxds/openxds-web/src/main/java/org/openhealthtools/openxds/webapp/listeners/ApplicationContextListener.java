package org.openhealthtools.openxds.webapp.listeners;

import gov.nist.registry.common2.registry.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.openxds.configuration.XdsConfigurationLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.LinkedList;
import java.util.List;

public class ApplicationContextListener implements ServletContextListener {
    protected transient final Log log = LogFactory.getLog(getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            String configFileProp = Properties.loader().getString("config.file.path");
            ServletContext context = servletContextEvent.getServletContext();;
//            String configFile = "c:\\dev\\exchange-master\\openxds\\openxds-web\\target\\openxds-web\\WEB-INF\\classes\\conf\\actors\\IheActors.xml" ;
            String configFile = context.getRealPath(configFileProp);

            XdsConfigurationLoader.getInstance().resetConfiguration(null, null);
            XdsConfigurationLoader.getInstance().loadConfiguration(configFile, false);

            List<Object> lString = new LinkedList<Object>();
            lString.add("xdsreg");
            lString.add("xdsreg_secure");
            lString.add("xdsrep");
            lString.add("xdsrep_secure");

            XdsConfigurationLoader.getInstance().resetConfiguration(lString, null);
            log.info("XDS services started");

        } catch (Exception e) {
            log.error("error while starting XDS services", e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

}
