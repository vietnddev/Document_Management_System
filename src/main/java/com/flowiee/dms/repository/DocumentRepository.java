package com.flowiee.dms.repository;

import com.flowiee.dms.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    @Query("from Document d where d.parentId=:parentId")
    Page<Document> findAll(@Param("parentId") Integer parentId, Pageable pageable);

    @Query("from Document d where d.parentId =:parentId order by d.isFolder desc")
    List<Document> findListDocumentByParentId(@Param("parentId") Integer parentId);

    @Query("from Document d where d.parentId =:parentId and d.isFolder = 'Y' order by d.isFolder desc")
    List<Document> findListFolderByParentId(@Param("parentId") Integer parentId);

    @Query("from Document d where d.parentId =:parentId and d.isFolder = 'N' order by d.isFolder desc")
    List<Document> findListFileByParentId(@Param("parentId") Integer parentId);

    @Query("from Document d where d.isFolder=:isThuMuc")
    List<Document> findAllFolder(@Param("isThuMuc") String isThuMuc);

    @Query("from Document d where d.docType.id=:docTypeId")
    List<Document> findDocumentByDocTypeId(@Param("docTypeId") Integer docTypeId);

    @Query(value = "select f.id as field_Id_0, " +
                   "       f.name as field_name_1, " +
                   "       d.id as data_id_2, " +
                   "       d.value as data_value_3, " +
                   "       f.type as field_type_4, " +
                   "       f.required as field_required_5 " +
                   "from stg_doc_field f " +
                   "left join stg_doc_data d on d.doc_field_id = f.id and d.document_id = :documentId " +
                   "left join stg_document dc on dc.doc_type_id = f.doc_type_id and dc.id = :documentId " +
                   "where f.doc_type_id = dc.doc_type_id " +
                   "order by f.sort",
           nativeQuery = true)
    List<Object[]> findMetadata(@Param("documentId") Integer documentId);

    @Modifying
    @Query("update Document d set d.parentId=:parentId where d.id=:docId")
    void updateParentId(Integer parentId, Integer docId);
}