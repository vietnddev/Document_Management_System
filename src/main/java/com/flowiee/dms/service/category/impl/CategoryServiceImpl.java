package com.flowiee.dms.service.category.impl;

import com.flowiee.dms.entity.system.SystemLog;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.exception.DataInUseException;
import com.flowiee.dms.entity.category.Category;
import com.flowiee.dms.entity.category.CategoryHistory;
import com.flowiee.dms.repository.category.CategoryHistoryRepository;
import com.flowiee.dms.repository.category.CategoryRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.category.CategoryService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.utils.ChangeLog;
import com.flowiee.dms.utils.constants.ErrorCode;
import com.flowiee.dms.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryServiceImpl extends BaseService implements CategoryService {
    CategoryRepository        categoryRepo;
    DocumentInfoService       documentInfoService;
    CategoryHistoryRepository categoryHistoryRepo;

    @Override
    public Optional<Category> findById(Long entityId) {
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
    public Category update(Category entity, Long entityId) {
        Optional<Category> categoryInput = this.findById(entityId);
        if (categoryInput.isEmpty()) {
            throw new BadRequestException();
        }
        Category categoryBefore = ObjectUtils.clone(categoryInput.get());
        entity.setId(entityId);
        Category categoryAfter = categoryRepo.save(entity);

        ChangeLog changeLog = new ChangeLog(categoryBefore, categoryAfter);
        for (Map.Entry<String, Object[]> entry : changeLog.getLogChanges().entrySet()) {
            String field = entry.getKey();
            String oldValue = ObjectUtils.isNotEmpty(entry.getValue()[0]) ? entry.getValue()[0].toString() : SystemLog.EMPTY;
            String newValue = ObjectUtils.isNotEmpty(entry.getValue()[1]) ? entry.getValue()[1].toString() : SystemLog.EMPTY;

            categoryHistoryRepo.save(CategoryHistory.builder()
                    .title("Cập nhật danh mục " + categoryAfter.getType())
                    .category(categoryAfter)
                    .field(field)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build());
        }

        return categoryAfter;
    }

    @Transactional
    @Override
    public String delete(Long entityId) {
        Optional<Category> category = this.findById(entityId);
        if (entityId == null || entityId <= 0 || category.isEmpty()) {
            throw new BadRequestException("Category not found!");
        }
        if (!documentInfoService.findByDoctype(category.get().getId()).isEmpty()) {
            throw new DataInUseException(ErrorCode.DATA_LOCKED_ERROR.getDescription());
        }
        categoryHistoryRepo.deleteAllByCategory(entityId);
        categoryRepo.deleteById(entityId);
        return MessageCode.DELETE_SUCCESS.getDescription();
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
    public Page<Category> findSubCategory(String categoryType, Long parentId, Long idNotIn, int pageSize, int pageNum) {
        Pageable pageable = Pageable.unpaged();
        if (pageSize >= 0 && pageNum >= 0) {
            pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        }
        return categoryRepo.findSubCategory(categoryType, parentId, idNotIn, pageable);
    }
}