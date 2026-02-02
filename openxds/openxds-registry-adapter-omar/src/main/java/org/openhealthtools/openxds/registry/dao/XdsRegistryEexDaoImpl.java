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

package org.openhealthtools.openxds.registry.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/* 
* @author <a href="mailto:Rasakannu.Palaniyandi@misys.com">Raja</a>
*/

public class XdsRegistryEexDaoImpl extends HibernateDaoSupport implements XdsRegistryEexDao {
	private static final Log log = LogFactory.getLog(XdsRegistryEexDaoImpl.class);

	public String getLegacyId(String personId, String  assigningAuthority){
		List list;
		String getLegacyId = null;

//		String personId = patientId.getPatientId();
//		String assigningAuthority = patientId.getAssigningAuthority();
		String deletePatient = "N";

//		try {
			list = getSession().createQuery(
					"select p.resident.legacyId from PersonIdentifier p where p.patientId = :personId and p.assigningAuthority like :assigningAuthority and p.deleted = :deletePatient")
					.setParameter("personId", personId)
					.setParameter("assigningAuthority", "%" +assigningAuthority + "%")
					.setParameter("deletePatient", deletePatient)
					.list();
//		}
//		catch (Exception e) {
//			log.error("Failed to retrieve person identifier from registry patient service",e);
//			throw new RegistryPatientException(e);
//		}
	
		if (list.size() > 0)
			getLegacyId = (String)list.get(0);

		return getLegacyId;
	}

	public String getDatabaseAlternativeId(String oid){

		List list;
		String alternativeId = null;

		SQLQuery query = getSession().createSQLQuery("select alternative_id from SourceDatabase where oid = :oid");
		query.setParameter("oid", oid);

		list = query.list();

		if (list.size() > 0)
			alternativeId = (String)list.get(0);

		return alternativeId;
	}

}
