package com.flowiee.dms.service.category;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.category.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService extends BaseService<Category> {
    List<Category> findRootCategory();

    Page<Category> findSubCategory(String categoryType, Integer parentId, Integer idNotIn, int pageSize, int pageNum);
}