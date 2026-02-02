package com.scnsoft.eldermark.entity.careteam;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Immutable
@Entity
@Table(name = "CareTeamRole")
public class CareTeamRole implements Serializable, IdAware {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "position")
	private Integer position;

	@Column(name = "display_name")
	private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name="code", nullable = false)
    private CareTeamRoleCode code;

	@Column(name = "is_manually_assignable")
    private boolean manuallyAssignable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

    public CareTeamRoleCode getCode() {
        return code;
    }

    public void setCode(CareTeamRoleCode code) {
        this.code = code;
    }

	public boolean isManuallyAssignable() {
		return manuallyAssignable;
	}

	public void setManuallyAssignable(boolean manuallyAssignable) {
		this.manuallyAssignable = manuallyAssignable;
	}
}
