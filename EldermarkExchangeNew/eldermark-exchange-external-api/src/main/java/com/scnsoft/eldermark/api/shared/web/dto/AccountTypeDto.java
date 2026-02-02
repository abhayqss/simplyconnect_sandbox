package com.scnsoft.eldermark.api.shared.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.api.shared.entity.AccountType;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-06T18:36:31.035+03:00")
public class AccountTypeDto {

    @JsonProperty("type")
    private AccountType.Type type = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("current")
    private Boolean current = null;


    @ApiModelProperty(example = "CONSUMER")
    public AccountType.Type getType() {
        return type;
    }

    public void setType(AccountType.Type type) {
        this.type = type;
    }

    @ApiModelProperty(example = "Consumer")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty
    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public static final class Builder {
        private AccountType.Type type = null;
        private String name = null;
        private Boolean current = null;

        private Builder() {
        }

        public static Builder anAccountTypeDto() {
            return new Builder();
        }

        public Builder withType(AccountType.Type type) {
            this.type = type;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCurrent(Boolean current) {
            this.current = current;
            return this;
        }

        public AccountTypeDto build() {
            AccountTypeDto accountTypeDto = new AccountTypeDto();
            accountTypeDto.setType(type);
            accountTypeDto.setName(name);
            if (name == null && type != null) {
                // default name
                accountTypeDto.setName(StringUtils.capitalize(StringUtils.lowerCase(type.name())));
            }
            accountTypeDto.setCurrent(current);
            return accountTypeDto;
        }
    }
}

