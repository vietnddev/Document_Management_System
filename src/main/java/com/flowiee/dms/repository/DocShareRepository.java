package com.flowiee.dms.repository;

import com.flowiee.dms.entity.DocShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocShareRepository extends JpaRepository<DocShare, Integer> {
    @Query("from DocShare d where d.document=:documentId and d.account=:accountId")
    DocShare findByDocAndAccount(@Param("documentId") Integer documentId, @Param("accountId") Integer accountId);
}