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

package org.openhealthtools.openxds.registry.adapter.omar31;

import com.misyshealthcare.connect.net.Identifier;
import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.freebxml.omar.common.CommonRequestContext;
import org.freebxml.omar.common.spi.LifeCycleManager;
import org.freebxml.omar.common.spi.LifeCycleManagerFactory;
import org.freebxml.omar.common.spi.RequestContext;
import org.freebxml.omar.server.security.authentication.AuthenticationServiceImpl;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequest;
import org.oasis.ebxml.registry.bindings.lcm.SubmitObjectsRequestType;
import org.oasis.ebxml.registry.bindings.rim.*;
import org.oasis.ebxml.registry.bindings.rim.impl.*;
import org.oasis.ebxml.registry.bindings.rs.RegistryRequestType;
import org.oasis.ebxml.registry.bindings.rs.RegistryResponse;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthtools.openxds.registry.Document;
import org.openhealthtools.openxds.registry.Util;
import org.openhealthtools.openxds.registry.api.RegistryLifeCycleContext;
import org.openhealthtools.openxds.registry.api.RegistryLifeCycleException;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.openhealthtools.openxds.registry.api.XdsRegistryLifeCycleService;
import org.openhealthtools.openxds.registry.dao.MergePatientDao;
import org.openhealthtools.openxds.registry.dao.XdsRegistryEexDaoImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.registry.JAXRException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Stack;

/**
 * This class adapts to the freebXML Omar 3.1 registry and 
 * defines the operations to manipulate XDS Registry
 * objects.
 * 
 * @author <a href="mailto:wenzhi.li@misys.com">Wenzhi Li</a>
 * @author <a href="mailto:anilkumar.reddy@misys.com">Anil kumar</a>
 *
 */
public class XdsRegistryLifeCycleServiceImpl implements XdsRegistryLifeCycleService {
	private static Log log = LogFactory.getLog(XdsRegistryLifeCycleServiceImpl.class);
	protected static LifeCycleManager lcm = LifeCycleManagerFactory.getInstance().getLifeCycleManager();
	protected static ConversionHelper helper = ConversionHelper.getInstance();
	MergePatientDao mergePatientDao =null;
	XdsRepoDocumentService xdsRepoDocumentService;

	private XdsRegistryEexDaoImpl xdsRegistryEexDao;

	private String exchangeRepositoryUniqueId;

	public OMElement submitObjects(OMElement request, RegistryLifeCycleContext context)  throws RegistryLifeCycleException {
		RequestContext omarContext;
		RegistryResponse omarResponse = null;
		OMElement response;
		
		final String contextId = "org:openhealthexchange:openxds:registry:adapter:omar31:XdsRegistryLifeCycleManager:submitObjects:context";
		try {
			InputStream is = new ByteArrayInputStream(request.toString().getBytes("UTF-8"));
			Object registryRequest = helper.getUnmarsheller().unmarshal(is);
			//Creating context with request.
			omarContext = new CommonRequestContext(contextId,(RegistryRequestType) registryRequest);
			//Adding RegistryOperator role for the user.
			omarContext.setUser(AuthenticationServiceImpl.getInstance().registryOperator);

			storeMetadataInExchange(omarContext);
			// Sending request to OMAR methods.
			omarResponse = lcm.submitObjects(omarContext);
			//Create RegistryResponse as OMElement
			response = helper.omFactory().createOMElement("RegistryResponse", helper.nsRs);
			response.declareNamespace(helper.nsRs);
			response.declareNamespace(helper.nsXsi);
			response.addAttribute("status", omarResponse.getStatus(), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RegistryLifeCycleException(e.getMessage());
		}

		return response;
	}

	private void storeMetadataInExchange(RequestContext omarContext) throws JAXRException, ParseException, RegistryPatientException {
		Stack<SubmitObjectsRequest> stack = omarContext.getRegistryRequestStack();
		if (stack!=null && !stack.empty() ) {
			SubmitObjectsRequest submitObjectsRequest = stack.get(0);
			SubmitObjectsRequestType submitObjectsRequestType = submitObjectsRequest.getValueObject();
			RegistryObjectListImpl registryObjectList = (RegistryObjectListImpl) submitObjectsRequestType.getRegistryObjectList();
			RegistryObjectListType registryObjectListType = registryObjectList.getValueObject();
			List registryObjectListTypeIterable = registryObjectListType.getIdentifiable();
			if (!CollectionUtils.isEmpty(registryObjectListTypeIterable)) {
				for (Object object : registryObjectListTypeIterable) {
					if (object instanceof ExtrinsicObjectImpl) {
						Document document = new Document();
						ExtrinsicObjectImpl extrinsicObject = (ExtrinsicObjectImpl) object;
						document.setMimeType(extrinsicObject.getMimeType());
						ExtrinsicObjectType valueObject = extrinsicObject.getValueObject();
//						InternationalStringType name1 = valueObject.getName();
						String pid = null;
						List<ExternalIdentifierImpl> externalIdentifiers = valueObject.getExternalIdentifier();
						for (ExternalIdentifierImpl externalIdentifier:externalIdentifiers) {
							if(externalIdentifier.getIdentificationScheme().equals("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab")) {
								document.setUniqueId(externalIdentifier.getValue());
								document.setUuid(externalIdentifier.getRegistryObject());
							}
							else if(externalIdentifier.getIdentificationScheme().equals("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427")) {
								pid = externalIdentifier.getValue();
							}
						}

						InternationalStringType name = extrinsicObject.getName();
						List<LocalizedStringImpl> localizedStringList = name.getLocalizedString();
						if (!CollectionUtils.isEmpty(localizedStringList)) {
							LocalizedStringImpl localizedString = localizedStringList.get(0);
							String fileName = localizedString.getValue();
							document.setOriginalFileName(localizedString.getValue());
							document.setDocumentTitle(localizedString.getValue());
//							document.setMimeType(Files.probeContentType(fileName));
							document.setMimeType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(fileName));
						}
						List<Slot> slots = extrinsicObject.getSlot();
						if (!CollectionUtils.isEmpty(slots)) {
							for (Slot slot : slots) {
								if (slot.getName().equals("repositoryUniqueId")) {
									String repositoryUniqueId = getSlotValue(slot);
									if (exchangeRepositoryUniqueId.equals(repositoryUniqueId)) {
										//exchange handles adding metadata about its documents, no need to do it here for the second time
										return;
									} else if (StringUtils.isNotBlank(repositoryUniqueId)) {
										//get Database by repositoryUniqueId
									}
								} else if (slot.getName().equals("creationTime")) {
									String dateStr = getSlotValue(slot);
									if (StringUtils.isNotBlank(dateStr)) {
										//document.setCreationTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr));
										document.setCreationTime(Util.convertHL7Date(dateStr));
									}
								} else if (slot.getName().equals("languageCode")) {

								} else if (slot.getName().equals("sourcePatientId")) {
									if (StringUtils.isBlank(pid)) {
										pid=getSlotValue(slot);
									}

								} else if (slot.getName().equals("hash")) {
									document.setHash(getSlotValue(slot));

								} else if (slot.getName().equals("size")) {
									try {
										document.setSize(Integer.parseInt(getSlotValue(slot)));
									} catch (NumberFormatException e) {
										log.warn("Wrong or blank document size registered: "+ slot.getName(), e);
									}
								}

							}
						}
						//xdsRepoPatientDao.
//						PersonIdentifier personIdentifier = new PersonIdentifier();
//				PersonIdentifier personIdentifier
						String patientId = pid.substring(0,pid.indexOf("^"));
						String aa = pid.substring(pid.indexOf("&")+1,pid.lastIndexOf("&"));

//						personIdentifier.setPatientId(patientId);
//						personIdentifier.setAssigningAuthority(aa);

//						PatientIdentifier patientIdentifier = getPatientIdentifier(pid);
//						PersonIdentifier personIdentifier1= xdsRegistryPatientDao.getPersonById(personIdentifier);
						String patientLegacyId = xdsRegistryEexDao.getLegacyId(patientId,aa);

						String databaseAlternativeId =xdsRegistryEexDao.getDatabaseAlternativeId(aa);

//						Resident resident = personIdentifier1.getResident();
//						PatientIdentifier patientIdentifier = getPatientIdentifier(pid);

						document.setAuthorLegacyId("XdsUser");
						document.setResidentDatabaseAlternativeId("ADT_Repo");
						document.setAuthorDatabaseAlternativeId(databaseAlternativeId!=null?databaseAlternativeId:"ADT_Repo");
						document.setResidentLegacyId(patientLegacyId);
						document.setVisible(true);
						document.setEldermarkShared(true);

//						saveDocument(document);
						xdsRepoDocumentService.saveDocument(document);
					}
				}
			}

		}
	}

//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void saveDocument(Document document) {
//			xdsRepoDocumentDao.save(document);
//	}

	private String getSlotValue(Slot slot) {
		SlotType1 slotType1 = slot.getValueObject();
		if (slotType1!=null) {
			List<ValueImpl> valueList = slotType1.getValueList().getValue();
			if (!CollectionUtils.isEmpty(valueList)) {
				return valueList.get(0).getValue();
			}
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void mergePatients(String survivingPatient, String mergePatient, 
			RegistryLifeCycleContext context) throws RegistryLifeCycleException {
    	 try {
			 mergePatientDao.mergeDocument(survivingPatient, mergePatient);
			} catch (Exception e) {
				throw new RegistryLifeCycleException(e);
			}
	}

	
	public OMElement approveObjects(OMElement request, RegistryLifeCycleContext context) throws RegistryLifeCycleException {
		RequestContext omarContext;
		RegistryResponse omarResponse = null;
		InputStream is;
		OMElement response;
		final String contextId = "org:openhealthexchange:openxds:registry:adapter:omar31:XdsRegistryLifeCycleManager:approveObjects:context";
		try {
			is = new ByteArrayInputStream(request.toString().getBytes("UTF-8"));
			Object registryRequest = helper.getUnmarsheller().unmarshal(is);
			//Creating context with request.
			omarContext = new CommonRequestContext(contextId,(RegistryRequestType) registryRequest);
			//Adding RegistryOperator role for the user.
			omarContext.setUser(AuthenticationServiceImpl.getInstance().registryOperator);
			// Sending request to OMAR methods.
			omarResponse = lcm.approveObjects(omarContext);
			// 
			response = helper.omFactory().createOMElement("RegistryResponse", helper.nsRs);
			response.declareNamespace(helper.nsRs);
			response.declareNamespace(helper.nsXsi);
			response.addAttribute("status", omarResponse.getStatus(), null);
		
		}  catch (Exception e) {
			e.printStackTrace();
			throw new RegistryLifeCycleException(e.getMessage());
		}

		return response;
	}

	public OMElement deprecateObjects(OMElement request, RegistryLifeCycleContext context) throws RegistryLifeCycleException {
		RequestContext omarContext;
		RegistryResponse omarResponse = null;
		InputStream is;
		OMElement response;
		final String contextId = "org:openhealthexchange:openxds:registry:adapter:omar31:XdsRegistryLifeCycleManager:deprecateObjects:context";
		try {
			is = new ByteArrayInputStream(request.toString().getBytes("UTF-8"));
			Object registryRequest = helper.getUnmarsheller().unmarshal(is);
			//Creating context with request.
			omarContext = new CommonRequestContext(contextId,(RegistryRequestType) registryRequest);
			//Adding RegistryOperator role for the user.
			omarContext.setUser(AuthenticationServiceImpl.getInstance().registryOperator);
			// Sending request to OMAR methods.
			omarResponse = lcm.deprecateObjects(omarContext);
			
			response = helper.omFactory().createOMElement("RegistryResponse", helper.nsRs);
			response.declareNamespace(helper.nsRs);
			response.declareNamespace(helper.nsXsi);
			response.addAttribute("status", omarResponse.getStatus(), null);
		
		}  catch (Exception e) {
			e.printStackTrace();
			throw new RegistryLifeCycleException(e.getMessage());
		}

		return response;
	}
	public MergePatientDao getMergePatientDao() {
		return mergePatientDao;
	}
	public void setMergePatientDao(MergePatientDao mergePatientDao) {
		this.mergePatientDao = mergePatientDao;
	}
    private PatientIdentifier getPatientIdentifier(String patientId){
	    	Identifier assigningAuthority = null;
	    	String[] patient = patientId.split("\\^");
	    	String patId = patient[0];
	    	String[] assignAuth = patient[3].split("\\&");
	    	assigningAuthority =  new Identifier(assignAuth[0], assignAuth[1], assignAuth[2]);
	    	PatientIdentifier identifier =new PatientIdentifier();
	    	identifier.setId(patId);
	    	identifier.setAssigningAuthority(assigningAuthority);
	    	return identifier;
	}


	public void setXdsRepoDocumentService(XdsRepoDocumentService xdsRepoDocumentService) {
		this.xdsRepoDocumentService = xdsRepoDocumentService;
	}

	public void setXdsRegistryEexDao(XdsRegistryEexDaoImpl xdsRegistryEexDao) {
		this.xdsRegistryEexDao = xdsRegistryEexDao;
	}

	public String getExchangeRepositoryUniqueId() {
		return exchangeRepositoryUniqueId;
	}

	public void setExchangeRepositoryUniqueId(String exchangeRepositoryUniqueId) {
		this.exchangeRepositoryUniqueId = exchangeRepositoryUniqueId;
	}
}
