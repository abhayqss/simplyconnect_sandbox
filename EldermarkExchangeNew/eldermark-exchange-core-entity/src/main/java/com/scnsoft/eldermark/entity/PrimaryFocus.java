package com.scnsoft.eldermark.entity;

import javax.persistence.Entity;

import org.hibernate.annotations.Immutable;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedKeyEntity;

@Immutable
@Entity
@Deprecated
public class PrimaryFocus extends DisplayableNamedKeyEntity{
}
