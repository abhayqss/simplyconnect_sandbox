package com.scnsoft.eldermark.entity;

import java.util.List;

import javax.persistence.*;

import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "InNetworkInsurance")
public class InNetworkInsurance extends BasicInsuranceEntity {

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inNetworkInsurance")
	@OrderBy("displayName")
	private List<InsurancePlan> insurancePlans;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "in1_id")
	private IN1InsuranceSegment in1InsuranceSegment;

	public List<InsurancePlan> getInsurancePlans() {
		return insurancePlans;
	}

	public void setInsurancePlans(List<InsurancePlan> insurancePlans) {
		this.insurancePlans = insurancePlans;
	}

	public IN1InsuranceSegment getIn1InsuranceSegment() {
		return in1InsuranceSegment;
	}

	public void setIn1InsuranceSegment(IN1InsuranceSegment in1InsuranceSegment) {
		this.in1InsuranceSegment = in1InsuranceSegment;
	}
}
