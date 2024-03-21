package com.flowiee.dms.repository.category;

import com.flowiee.dms.entity.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("from Category c where c.code = 'ROOT' order by c.sort")
    List<Category> findRootCategory();

    @Query("from Category c " +
           "where 1=1 " +
           "and (:type is null or c.type=:type) " +
           "and c.code <> 'ROOT' " +
           "and (:parentId is null or c.parentId=:parentId) " +
           "and (:idNotIn is null or c.id <> :idNotIn) " +
           "order by c.sort")
    Page<Category> findSubCategory(@Param("type") String type, @Param("parentId") Integer parentId, @Param("idNotIn") Integer idNotIn, Pageable pageable);

    @Query("from Category c where c.type in (:type) and (c.code is null or c.code <> 'ROOT') order by c.sort")
    List<Category> findSubCategory(@Param("type") List<String> type);

    @Query("from Category c " +
           "where 1=1 " +
           "and c.type=:type " +
           "and (c.code is null or c.code <> 'ROOT') " +
           "and (c.isDefault is null or trim(c.isDefault) = '' or c.isDefault = 'N') " +
           "order by c.sort")
    List<Category> findSubCategoryUnDefault(@Param("type") String type);

    @Query("from Category c where c.type=:type and (c.code is null or c.code <> 'ROOT') and c.isDefault = 'Y'")
    Category findSubCategoryDefault(@Param("type") String type);

    @Query("select c.type, nvl((select count(*) from Category where code <> 'ROOT' and type = c.type), 0) as total_records " +
           "from Category c " +
           "where c.code = 'ROOT'")
    List<Object[]> totalRecordsOfEachType();
}