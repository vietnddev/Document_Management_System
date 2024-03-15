package com.flowiee.dms.controller;

import com.flowiee.dms.entity.Category;
import com.flowiee.dms.core.exception.AppException;
import com.flowiee.dms.core.exception.BadRequestException;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.service.CategoryService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${app.api.prefix}/category")
@Tag(name = "Category API", description = "Quản lý danh mục hệ thống")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Find all category")
    @GetMapping("/all")
    public ApiResponse<List<Category>> findAll() {
        try {
            return ApiResponse.ok(categoryService.findRootCategory());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "category"));
        }
    }

    @Operation(summary = "Find by type")
    @GetMapping("/{type}")
    public ApiResponse<List<Category>> findByType(@PathVariable("type") String categoryType,
                                                  @RequestParam(value = "parentId", required = false) Integer parentId,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                  @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                  @RequestParam(value = "idNotIn", required = false) Integer idNotIn) {
        try {
            if (Objects.isNull(pageSize) || Objects.isNull(pageNum)) {
                return ApiResponse.ok(categoryService.findSubCategory(CommonUtils.getCategoryType(categoryType), parentId, idNotIn));
            }
            Page<Category> categories = categoryService.findSubCategory(CommonUtils.getCategoryType(categoryType), parentId, pageSize, pageNum - 1);
            return ApiResponse.ok(categories.getContent(), pageNum, pageSize, categories.getTotalPages(), categories.getTotalElements());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "category"));
        }
    }

    @Operation(summary = "Create category")
    @PostMapping("/create")
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        try {
            category.setType(CommonUtils.getCategoryType(category.getType()));
            return ApiResponse.ok(categoryService.save(category));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new AppException(String.format(MessageUtils.CREATE_ERROR_OCCURRED, "category"));
        }
    }

    @Operation(summary = "Update category")
    @PutMapping("/update/{categoryId}")
    public ApiResponse<Category> updateCategory(@RequestBody Category category, @PathVariable("categoryId") Integer categoryId) {
        try {
            if (categoryService.findById(categoryId) == null) {
                throw new BadRequestException();
            }
            category.setType(CommonUtils.getCategoryType(category.getType()));
            if (category.getCode() == null) {
                category.setCode("");
            }
            if (category.getColor() == null) {
                category.setColor("");
            }
            if (category.getNote() == null) {
                category.setNote("");
            }
            return ApiResponse.ok(categoryService.update(category, categoryId));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new AppException(String.format(MessageUtils.UPDATE_ERROR_OCCURRED, "category"));
        }
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/delete/{categoryId}")
    public ApiResponse<String> deleteCategory(@PathVariable("categoryId") Integer categoryId) {
        try {
            if (categoryService.findById(categoryId) == null) {
                throw new BadRequestException();
            }
            return ApiResponse.ok(categoryService.delete(categoryId));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new AppException(String.format(MessageUtils.DELETE_ERROR_OCCURRED, "category"));
        }
    }
}