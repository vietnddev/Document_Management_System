package com.flowiee.dms.repository;

import com.flowiee.dms.entity.DocShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocShareRepository extends JpaRepository<DocShare, Integer> {
    @Query("from DocShare d where d.document.id=:documentId and d.account.id=:accountId")
    List<DocShare> findByDocAndAccount(@Param("documentId") Integer documentId, @Param("accountId") Integer accountId);

    @Query("from DocShare d where d.document.id=:documentId")
    List<DocShare> findByDocument(@Param("documentId") Integer documentId);

    @Modifying
    @Query("delete DocShare d where d.account.id=:accountId")
    void deleteAllByAccount(@Param("accountId") Integer accountId);

    @Modifying
    @Query("delete DocShare d where d.document.id=:documentId")
    void deleteAllByDocument(@Param("documentId") Integer documentId);
}