package com.flowiee.dms.service.category.impl;

import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.exception.DataInUseException;
import com.flowiee.dms.entity.category.Category;
import com.flowiee.dms.entity.category.CategoryHistory;
import com.flowiee.dms.repository.category.CategoryHistoryRepository;
import com.flowiee.dms.repository.category.CategoryRepository;
import com.flowiee.dms.service.category.CategoryService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepo;
    private final CategoryHistoryRepository categoryHistoryRepo;
    private final DocumentInfoService       documentInfoService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepo, DocumentInfoService documentInfoService, CategoryHistoryRepository categoryHistoryRepo) {
        this.categoryRepo = categoryRepo;
        this.documentInfoService = documentInfoService;
        this.categoryHistoryRepo = categoryHistoryRepo;
    }

    @Override
    public Optional<Category> findById(Integer entityId) {
        return categoryRepo.findById(entityId);
    }

    @Override
    public Category save(Category entity) {
        if (entity == null) {
            throw new BadRequestException();
        }
        return categoryRepo.save(entity);
    }

    @Transactional
    @Override
    public Category update(Category entity, Integer entityId) {
        Optional<Category> categoryBefore = this.findById(entityId);
        if (categoryBefore.isEmpty()) {
            throw new BadRequestException();
        }
        categoryBefore.get().compareTo(entity).forEach((key, value) -> {
            CategoryHistory categoryHistory = new CategoryHistory();
            categoryHistory.setTitle("Cập nhật danh mục " + categoryBefore.get().getType());
            categoryHistory.setCategory(new Category(categoryBefore.get().getId(), null));
            categoryHistory.setField(key);
            categoryHistory.setOldValue(value.substring(0, value.indexOf("#")));
            categoryHistory.setNewValue(value.substring(value.indexOf("#") + 1));
            categoryHistoryRepo.save(categoryHistory);
        });
        entity.setId(entityId);
        return categoryRepo.save(entity);
    }

    @Transactional
    @Override
    public String delete(Integer entityId) {
        Optional<Category> category = this.findById(entityId);
        if (entityId == null || entityId <= 0 || category.isEmpty()) {
            throw new BadRequestException("Category not found!");
        }
        if (!documentInfoService.findByDoctype(category.get().getId()).isEmpty()) {
            throw new DataInUseException(MessageUtils.ERROR_DATA_LOCKED);
        }
        categoryHistoryRepo.deleteAllByCategory(entityId);
        categoryRepo.deleteById(entityId);
        return MessageUtils.DELETE_SUCCESS;
    }

    @Override
    public List<Category> findRootCategory() {
        List<Category> roots = categoryRepo.findRootCategory();
        List<Object[]> recordsOfEachType = categoryRepo.totalRecordsOfEachType();
        for (Category c : roots) {
            for (Object[] o : recordsOfEachType) {
                if (c.getType().equals(o[0])) {
                    c.setTotalSubRecords(Integer.parseInt(String.valueOf(o[1])));
                    break;
                }
            }
        }
        return roots;
    }

    @Override
    public Page<Category> findSubCategory(String categoryType, Integer parentId, Integer idNotIn, int pageSize, int pageNum) {
        Pageable pageable = Pageable.unpaged();
        if (pageSize >= 0 && pageNum >= 0) {
            pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        }
        return categoryRepo.findSubCategory(categoryType, parentId, idNotIn, pageable);
    }
}