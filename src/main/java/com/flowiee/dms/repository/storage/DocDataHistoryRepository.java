package com.flowiee.dms.repository.storage;

import com.flowiee.dms.entity.storage.DocDataHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocDataHistoryRepository extends JpaRepository<DocDataHistory, Long> {
    @Query("from DocDataHistory d where d.documentId = :documentId and d.version = :version")
    List<DocDataHistory> findByVersion(@Param("documentId") long documentId, @Param("version") Long versionId);
}