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

package org.openhealthtools.openxds.entity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "MPI_merged_residents")
public class PersonIdentifierMerged implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name="merged_resident_id")
	private Long mergedResidentId;

	@Column(name="surviving_resident_id")
	private Long survivingResidentId;

	@Column(name="merged")
	private Boolean merged;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMergedResidentId() {
		return mergedResidentId;
	}

	public void setMergedResidentId(Long mergedResidentId) {
		this.mergedResidentId = mergedResidentId;
	}

	public Long getSurvivingResidentId() {
		return survivingResidentId;
	}

	public void setSurvivingResidentId(Long survivingResidentId) {
		this.survivingResidentId = survivingResidentId;
	}

	public Boolean getMerged() {
		return merged;
	}

	public void setMerged(Boolean merged) {
		this.merged = merged;
	}
}