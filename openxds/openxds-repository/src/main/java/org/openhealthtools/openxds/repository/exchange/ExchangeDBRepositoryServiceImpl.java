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

package org.openhealthtools.openxds.repository.exchange;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;
import org.openhealthtools.openxds.repository.ByteArrayDataSource;
import org.openhealthtools.openxds.repository.Utility;
import org.openhealthtools.openxds.repository.XdsRepositoryItemImpl;
import org.openhealthtools.openxds.repository.api.RepositoryException;
import org.openhealthtools.openxds.repository.api.RepositoryRequestContext;
import org.openhealthtools.openxds.repository.api.XdsRepositoryItem;
import org.openhealthtools.openxds.repository.api.XdsRepositoryService;
import org.openhealthtools.openxds.repository.entity.XdsUploadDocumentRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides a xds repository manager service implementation.
 * This class overrides existing OpenXDS Repository implementations, so it redirects all requests to Exchange Web Service through {@link ExchangeHttpSender}.
 *
 * Changes:
 * <ol>
 *     <li>Send data to Exchange Document Web Service when ITI-41 request comes to store new or delete an existing document.</li>
 *     <li>Request data from Exchange Document Web service when ITI-43 request comes to fetch document contents.</li>
 * </ol>
 *
 * @author averazub
 * 
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ExchangeDBRepositoryServiceImpl implements XdsRepositoryService {
	private static final Log log = LogFactory.getLog(ExchangeDBRepositoryServiceImpl.class);


	XdsRegistryPatientService patientService=null;


	ExchangeHttpSender httpSender;

	private String repositoryUniqueId;

	public ExchangeHttpSender getHttpSender() {
		return httpSender;
	}

	public void setHttpSender(ExchangeHttpSender httpSender) {
		this.httpSender = httpSender;
	}

	public void setRepositoryUniqueId(String repositoryUniqueId) {
		this.repositoryUniqueId = repositoryUniqueId;
	}
	
	public String getRepositoryUniqueId() {		
		return repositoryUniqueId;
	}

	/* (non-Javadoc)
         * @see org.openhealthtools.openxds.repository.api.IXdsRepositoryManager#getRepositoryItem()
         */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public XdsRepositoryItem getRepositoryItem(String documentUniqueId,
			RepositoryRequestContext context) throws RepositoryException {
		try {
			if (patientService==null) patientService = XdsFactory.getXdsRegistryPatientService();
			// Strip off the "urn:uuid:"
			String id = Utility.getInstance().stripId(documentUniqueId);
			XdsUploadDocumentRequest docSource = httpSender.sendGetDocumentRequest(id);
			XdsRepositoryItem result = new XdsRepositoryItemImpl();
			result.setMimeType(docSource.getMimeType());
			result.setDocumentUniqueId(docSource.getUniqueId());
			/*PatientIdentifier pid = patientService.getPatientId(docSource.getResidentId(), null);
			result.setPatientId(pid);
			result.setDocumentTitle(docSource.getTitle());
			result.setDocumentUUID(docSource.getUuid());
			*/
			result.setDataHandler(new DataHandler(new ByteArrayDataSource(docSource.getContent(),docSource.getMimeType())));
			return result;
		} catch (Exception e) {
			log.error(e);
			throw new RepositoryException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.repository.api.IXdsRepositoryManager#getRepositoryItems()
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<XdsRepositoryItem> getRepositoryItems(
			List<String> documentUniqueIds, RepositoryRequestContext context)
			throws RepositoryException {
		List<XdsRepositoryItem> repositoryItems = null;
		try {
			if (documentUniqueIds != null) {
				Iterator<String> item = documentUniqueIds.iterator();
				while (item.hasNext()) {
					String repositoryItem = item.next();
					XdsRepositoryItem xdsRepositoryItem = getRepositoryItem(repositoryItem, context);
					if (xdsRepositoryItem != null)
						repositoryItems.add(xdsRepositoryItem);
				}

			}
		} catch (Exception e) {
			log.error(e);
			throw new RepositoryException(e);
		}
		return repositoryItems;
	}
	
	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.repository.api.IXdsRepositoryManager#insert()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(XdsRepositoryItem item, RepositoryRequestContext context)
			throws RepositoryException {

		if (patientService==null) patientService = XdsFactory.getXdsRegistryPatientService();

		Long residentId = null;
		try {
			residentId = patientService.getResidentId(item.getPatientId(), null);
			XdsUploadDocumentRequest request = prepareRequest(item, residentId, item.getDataHandler().getInputStream());
			httpSender.sendPostDocumentRequest(request);
		} catch (Exception e) {
			throw new RepositoryException(e);
		}

	}
	
	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.repository.api.IXdsRepositoryManager#insert()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(List<XdsRepositoryItem> items,
			RepositoryRequestContext context) throws RepositoryException {
		try {
			if (items != null) {
				Iterator<XdsRepositoryItem> item = items.iterator();
				while (item.hasNext()) {
					XdsRepositoryItem repositoryItem = item.next();
					insert(repositoryItem, context);
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RepositoryException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.repository.api.IXdsRepositoryManager#delete()
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void delete(String documentUniqueId, RepositoryRequestContext context)
			throws RepositoryException {
		// Strip off the "urn:uuid:"
		String id = Utility.getInstance().stripId(documentUniqueId);		
		try {
			httpSender.sendDeleteDocumentRequest(id);
		 }
		 catch (Exception e) {
			 log.error(e); 
			throw new RepositoryException(e);
		}
		log.debug("Repository bean deleted successfully");
				
	}
	/* (non-Javadoc)
	 * @see org.openhealthtools.openxds.repository.api.IXdsRepositoryManager#delete()
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void delete(List<String> ids, RepositoryRequestContext context)
			throws RepositoryException {
		try {
			if (ids != null) {
				Iterator<String> item = ids.iterator();
				while (item.hasNext()) {
					String repositoryItem = item.next();
					delete(repositoryItem, context);
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RepositoryException(e);
		}
		
	}

	private XdsUploadDocumentRequest prepareRequest(XdsRepositoryItem item, Long residentId, InputStream is) throws RepositoryException {
		XdsUploadDocumentRequest request = new XdsUploadDocumentRequest();

		// Strip off the "urn:uuid:"
		String documentUniqueId = Utility.getInstance().stripId(item.getDocumentUniqueId());
		String documentUuid = Utility.getInstance().stripId(item.getDocumentUUID());

		request.setResidentId(residentId);
		request.setMimeType(item.getMimeType());
		request.setTitle(item.getDocumentTitle());
		request.setUniqueId(documentUniqueId);
		request.setUuid(documentUuid);
		try {
			byte[] bytes = IOUtils.toByteArray(is);
			request.setContent(bytes);
			return request;
		} catch (Exception e) {
			log.error(e);
			throw new RepositoryException("error while converting datahandler object into byte array");
		}

	}


}
