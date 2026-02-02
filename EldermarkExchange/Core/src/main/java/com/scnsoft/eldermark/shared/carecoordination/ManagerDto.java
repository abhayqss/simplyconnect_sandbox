package com.scnsoft.eldermark.shared.carecoordination;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent details of responsible manager (Event Manager).
 * Created by pzhurba on 05-Oct-15.
 */
@ApiModel(description = "This DTO is intended to represent details of responsible manager (Event Manager).")
public class ManagerDto extends NameDto {
    private String email;
    private String phone;

    @Email
    @Size(max = 255)
    @ApiModelProperty(value = "Email address. Nullable.", example = "donald@disney.com")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // TODO validate phone ?
    @Size(max = 20)
    @ApiModelProperty(value = "Phone number. Nullable.", example = "6458765432")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
