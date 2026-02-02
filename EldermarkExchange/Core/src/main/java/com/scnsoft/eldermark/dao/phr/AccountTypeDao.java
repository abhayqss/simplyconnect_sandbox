package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 * Created on 5/11/2017.
 */
@Repository
public interface AccountTypeDao extends JpaRepository<AccountType, Long> {

    AccountType findByType(AccountType.Type type);

}
