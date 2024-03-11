package com.flowiee.dms.repository;

import com.flowiee.dms.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface FileStorageRepository extends JpaRepository<FileStorage, Integer> {
    @Query("from FileStorage f where f.document.id=:documentId order by f.createdAt desc")
    List<FileStorage> findFileOfDocument(@Param("documentId") Integer documentId);

    @Query("from FileStorage f where f.document.id=:documentId and f.isActive=:isActive")
    FileStorage findFileIsActiveOfDocument(@Param("documentId") Integer documentId, @Param("isActive") boolean isActive);
}