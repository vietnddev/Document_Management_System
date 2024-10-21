package com.flowiee.dms.repository.storage;

import com.flowiee.dms.entity.storage.DocHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocHistoryRepository extends JpaRepository<DocHistory, Long> {
    @Query("from DocHistory d where d.document.id=:documentId")
    List<DocHistory> findByDocument(@Param("documentId") Long documentId);

    @Query("from DocHistory d where d.docData.id=:docDataId")
    List<DocHistory> findByDocData(@Param("docDataId") Long docDataId);

    @Modifying
    @Query("delete from DocHistory d where d.document.id=:documentId")
    void deleteAllByDocument(@Param("documentId") Long documentId);
}