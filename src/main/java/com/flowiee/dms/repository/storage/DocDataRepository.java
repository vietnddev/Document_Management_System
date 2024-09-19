package com.flowiee.dms.repository.storage;

import com.flowiee.dms.entity.storage.DocData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocDataRepository extends JpaRepository<DocData, Integer> {
    @Query("from DocData d where d.docField.id=:docFieldId")
    List<DocData> findByDocField(@Param("docFieldId") Integer docFieldId);

    @Query("from DocData d where d.document.id=:documentId")
    List<DocData> findByDocumentId(@Param("documentId") Integer documentId);

    @Query("from DocData d where d.docField.id=:docFieldId and d.document.id=:documentId")
    DocData findByFieldIdAndDocId(@Param("docFieldId") Integer docFieldId, @Param("documentId") Integer documentId);

    @Modifying
    @Query("update DocData d set d.value=:value where d.id=:docDataId")
    void updateMetaData(@Param("value") String value, @Param("docDataId") Integer docDataId);

    @Modifying
    @Query("delete from DocData d where d.document.id=:documentId")
    void deleteAllByDocument(@Param("documentId") Integer documentId);
}