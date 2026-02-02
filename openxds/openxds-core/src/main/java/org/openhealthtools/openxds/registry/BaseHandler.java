/**
 *  Copyright (c) 2009-2010 Misys Open Source Solutions (MOSS) and others
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Contributors:
 *    Misys Open Source Solutions - initial API and implementation
 *    -
 */

package org.openhealthtools.openxds.registry;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthexchange.openpixpdq.ihe.configuration.Configuration;
import org.openhealthexchange.openpixpdq.ihe.configuration.IheConfigurationException;

import ca.uhn.hl7v2.app.ApplicationException;

import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.Identifier;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Header;

/**
 * The base class of all handlers. It provides some common 
 * methods available to all handlers.
 * 
 * @author <a href="mailto:wenzhi.li@misys.com">Wenzhi Li</a>
 */
public class BaseHandler {

	private static Log log = LogFactory.getLog(BaseHandler.class);

	/** The connection description of the actor for this handler */
	protected IConnectionDescription connection;

	private Properties properties = null;
	
	/**
	 * Default constructor
	 */
	protected BaseHandler() {
	}
	/**
	 * Constructor
	 * 
	 * @param connection the connection description of the actor 
	 */
	protected BaseHandler(IConnectionDescription connection) {
		this.connection = connection;
	}
	
	/**
	 * Gets the application name of this PIX/PDQ server. If the server receives messages from
	 * a source application, then the application name is the same as ReceivingApplication, configurable
	 * through the XML actor configuration files.
	 * 
	 * @return the application name of this PIX/PDQ server
	 * @throws ApplicationException
	 */
	protected Identifier getServerApplication() throws ApplicationException {
		Identifier ret = null;
		try {
			ret = Configuration.getIdentifier(connection,
					"ReceivingApplication", true);
		} catch (IheConfigurationException e) {
			throw new ApplicationException(
					"Missing receivingApplication for connection "
							+ connection.getDescription(), e);
		}
		return ret;		
	}
    
	/**
	 * Gets the facility name of this PIX/PDQ server. If the server receives messages from
	 * a source application, then the facility name is the same as ReceivingFacility, configurable
	 * through the XML actor configuration files.
	 * 
	 * @return the facility name of this PIX/PDQ server
	 * @throws ApplicationException
	 */
	protected Identifier getServerFacility() throws ApplicationException {
		Identifier ret = null;
		try {
			ret = Configuration.getIdentifier(connection,
					"ReceivingFacility", true);
		} catch (IheConfigurationException e) {
			throw new ApplicationException(
					"Missing ReceivingFacility for connection "
							+ connection.getDescription(), e);
		}
		return ret;				
	}
	
	private static String ip = null;
	static {
		try {
			InetAddress addr = InetAddress.getLocalHost();
		    ip = addr.getHostAddress();
		}catch(Exception e) {
			//just ignore it.
		}
	}
	
	private static int staticCounter=0;
	private static final int nBits=4;
	
	/**
	 * Generates a new unique message id.
	 * 
	 * @return a message id
	 */
	public static synchronized String getMessageControlId() {
		String prefix = "OpenPIXPDQ"; 
		if (ip != null)	prefix += ip;
		long temp = (System.currentTimeMillis() << nBits) | (staticCounter++ & 2^nBits-1);
		String id = prefix + "." + temp;
		return id;
	}

	public void logAccess(HL7Header hl7Header, PatientIdentifier patientId) {
		File f = new File(getProperties().getProperty("access.log.file"));
		FileWriter fw = null;
		try {
			if (!f.exists()) f.createNewFile();
			fw = new FileWriter(f, true);
			fw.write("---");
			fw.write( "\nITI-8 " + hl7Header.getMessageCode() + " request came");
			fw.write("\nPID: " + patientId.getId() + ":" + patientId.getAssigningAuthority().getNamespaceId() + ":" + patientId.getAssigningAuthority().getUniversalId());
			fw.write("\nDate: " + hl7Header.getMessagedate());
			fw.write("\n---\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Properties getProperties() {
		if (properties==null) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("openxds.properties");
			if (is == null) {
				log.fatal("Cannot load openxds.properties");
				return null;
			}
			properties = new java.util.Properties();
			try {
				properties.load(is);
			} catch (Exception e) {
				log.fatal(e.getMessage());
			}
		}
		return properties;
	}

	public IConnectionDescription getConnection() {
		return connection;
	}

	public void setConnection(final IConnectionDescription connection) {
		this.connection = connection;
	}
}
