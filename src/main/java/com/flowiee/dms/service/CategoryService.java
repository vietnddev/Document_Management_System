package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService extends BaseService<Category> {
    List<Category> findAll();

    List<Category> findRootCategory();

    List<Category> findSubCategory(String categoryType, Integer parentId, Integer idNotIn);

    Page<Category> findSubCategory(String categoryType, Integer parentId, int pageSize, int pageNum);

    Boolean categoryInUse(Integer categoryId);
}