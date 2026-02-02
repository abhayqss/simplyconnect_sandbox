package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;

/**
 * @author phomal
 * Created on 5/6/2017
 */
@Entity
@Table(name = "UserAccountType")
public class UserAccountType extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_type_id")
    private AccountType accountType;

    @Column(name = "is_current")
    private Boolean current;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }
}
