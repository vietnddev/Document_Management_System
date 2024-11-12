package com.flowiee.dms.repository.storage;

import com.flowiee.dms.entity.storage.DocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {
    @Query("from DocumentHistory d where d.entityId = :documentId and d.version = :versionId")
    DocumentHistory findByVersion(@Param("documentId") Long documentId, @Param("version") Long versionId);
}