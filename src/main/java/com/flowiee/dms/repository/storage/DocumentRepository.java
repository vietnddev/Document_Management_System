package com.flowiee.dms.repository.storage;

import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.storage.view.DocumentTreeView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    @Query("select distinct d from Document d " +
           "left join DocShare ds on ds.document.id = d.id " +
           "where 1=1 " +
           "and (:txtSearch is null or d.name like %:txtSearch%) " +
           "and (:parentId is null or d.parentId=:parentId) " +
           "and (:isAdmin is true or d.createdBy=:currentAccountId or (ds.account.id=:accountId and ds.role = 'R')) " +
           "and (:docTypeId is null or d.docType.id=:docTypeId) " +
           "and (:isFolder is null or d.isFolder=:isFolder) " +
           "and (:listId is null or d.id in :listId)")
    Page<Document> findAll(@Param("txtSearch") String txtSearch,
                           @Param("parentId") Integer parentId,
                           @Param("currentAccountId") Integer currentAccountId,
                           @Param("isAdmin") boolean isAdmin,
                           @Param("accountId") Integer accountId,
                           @Param("docTypeId") Integer docTypeId,
                           @Param("isFolder") String isFolder,
                           @Param("listId") List<Integer> listId,
                           Pageable pageable);

    @Query(value = "select f.id as field_Id_0, " +
                   "       f.name as field_name_1, " +
                   "       d.id as data_id_2, " +
                   "       d.value as data_value_3, " +
                   "       f.type as field_type_4, " +
                   "       f.required as field_required_5 " +
                   "from doc_field f " +
                   "left join doc_data d on d.doc_field_id = f.id and d.document_id = :documentId " +
                   "left join document dc on dc.doc_type_id = f.doc_type_id and dc.id = :documentId " +
                   "where f.doc_type_id = dc.doc_type_id " +
                   "order by f.sort",
           nativeQuery = true)
    List<Object[]> findMetadata(@Param("documentId") Integer documentId);

    @Modifying
    @Query("update Document d set d.parentId=:parentId where d.id=:docId")
    void updateParentId(@Param("parentId") Integer parentId, @Param("docId") Integer docId);

    @Query("from Document d where d.id in (select ds.document.id from DocShare ds where ds.account.id=:accountId) order by d.isFolder, d.createdAt")
    List<Document> findWasSharedDoc(@Param("accountId") Integer accountId);

    @Query("select " +
           "count(case when d.isFolder = 'Y' then 1 end) as total_folder, " +
           "count(case when d.isFolder = 'N' then 1 end) as total_file, " +
           "concat(to_char(sum(f.fileSize) / 1024 / 1024, 'FM9999999990.99'), ' MB') AS total_size " +
           "from Document d " +
           "left join FileStorage f on f.document.id = d.id")
    List<Object[]> summaryStorage();

    @Query("select d from DocumentTreeView d " +
           "where 1=1 " +
           "and (:parentId is null or d.parentId = :parentId) " +
           "and (:isOnlyFolder is null or d.isFolder = :isOnlyFolder) " +
           "order by d.path")
    List<DocumentTreeView> findGeneralFolderTree(@Param("parentId") Integer parentId, @Param("isOnlyFolder") String isOnlyFolder);

    @Query("select case when count(d) > 0 then true else false end " +
           "from Document d " +
           "where 1=1 " +
           "and d.parentId = :docId " +
           "and (:isFolder is null or d.isFolder = :isFolder)")
    boolean existsSubDocument(@Param("docId") Integer docId, @Param("isFolder") String isFolder);
}