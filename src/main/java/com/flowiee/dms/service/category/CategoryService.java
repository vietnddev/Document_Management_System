package com.flowiee.dms.service.category;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.category.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService extends BaseCurdService<Category> {
    List<Category> findRootCategory();

    Page<Category> findSubCategory(String categoryType, Long parentId, Long idNotIn, int pageSize, int pageNum);
}