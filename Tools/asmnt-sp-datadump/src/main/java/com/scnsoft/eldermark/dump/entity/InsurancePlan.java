package com.scnsoft.eldermark.dump.entity;


import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Immutable
@Entity
@Table(name = "InsurancePlan")
public class InsurancePlan extends DisplayableNamedEntity{
}
