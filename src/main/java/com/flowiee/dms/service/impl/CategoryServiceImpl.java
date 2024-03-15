package com.flowiee.dms.service.impl;

import com.flowiee.dms.core.exception.BadRequestException;
import com.flowiee.dms.core.exception.DataInUseException;
import com.flowiee.dms.entity.*;
import com.flowiee.dms.repository.*;
import com.flowiee.dms.service.*;
import com.flowiee.dms.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
	private static final String MODULE = AppConstants.SYSTEM_MODULE.CATEGORY.name();
	
    private final CategoryRepository categoryRepo;
    private final CategoryHistoryRepository categoryHistoryRepo;
    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    private final FileStorageRepository fileStorageRepo;
    private final AccountService accountService;


    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepo, DocumentService documentService, FileStorageService fileStorageService,
                               FileStorageRepository fileStorageRepo, AccountService accountService, CategoryHistoryRepository categoryHistoryRepo) {
        this.categoryRepo = categoryRepo;
        this.documentService = documentService;
        this.fileStorageService = fileStorageService;
        this.fileStorageRepo = fileStorageRepo;
        this.accountService = accountService;
        this.categoryHistoryRepo = categoryHistoryRepo;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    @Override
    public Category findById(Integer entityId) {
        return categoryRepo.findById(entityId).orElse(null);
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
        Category categoryBefore = this.findById(entityId);
        if (categoryBefore == null) {
            throw new BadRequestException();
        }
        categoryBefore.compareTo(entity).forEach((key, value) -> {
            CategoryHistory categoryHistory = new CategoryHistory();
            categoryHistory.setTitle("Cập nhật danh mục " + categoryBefore.getType());
            categoryHistory.setCategory(new Category(categoryBefore.getId(), null));
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
        if (entityId == null || entityId <= 0 || this.findById(entityId) == null) {
            throw new BadRequestException();
        }
        if (categoryInUse(entityId)) {
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
    public List<Category> findSubCategory(String categoryType, Integer parentId, Integer idNotIn) {
        return categoryRepo.findSubCategory(categoryType, parentId, idNotIn, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Category> findSubCategory(String categoryType, Integer parentId, int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        return categoryRepo.findSubCategory(categoryType, parentId, null, pageable);
    }

    @Override
    public Boolean categoryInUse(Integer categoryId) {
        Category category = this.findById(categoryId);
        switch (category.getType()) {
            case "DOCUMENT_TYPE":
                if (!documentService.findByDoctype(categoryId).isEmpty()) {
                    return true;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + category.getType());
        }
        return false;
    }
}