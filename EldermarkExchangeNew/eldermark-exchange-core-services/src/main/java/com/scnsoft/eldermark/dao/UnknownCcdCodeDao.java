package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.UnknownCcdCode;

@Repository
public interface UnknownCcdCodeDao extends JpaRepository<UnknownCcdCode, Long> {
    
    @Query("SELECT o FROM UnknownCcdCode o WHERE o.code=:code AND o.codeSystem=:codeSystem")
    List<UnknownCcdCode> getCcdCodes(@Param("code") String code, @Param("codeSystem") String codeSystem);
     
}
   
    
    
    

   
        
        

