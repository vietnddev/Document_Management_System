package com.flowiee.dms.controller.category;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.entity.category.Category;
import com.flowiee.dms.exception.NotFoundException;
import com.flowiee.dms.service.category.CategoryService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.PagesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@RestController
@RequestMapping("/system/category")
public class CategoryControllerView extends BaseController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryControllerView(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewRootCategory() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.CTG_CATEGORY);
        modelAndView.addObject("category", new Category());
        modelAndView.addObject("listCategory", categoryService.findRootCategory());
        return baseView(modelAndView);
    }

    @GetMapping("/{type}")
    @PreAuthorize("@vldModuleCategory.readCategory(true)")
    public ModelAndView viewSubCategory(@PathVariable("type") String categoryType) {
        if (!CommonUtils.isValidCategory(categoryType)) {
            throw new NotFoundException("Category not found!");
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtils.CTG_CATEGORY_DETAIL);
        modelAndView.addObject("categoryType", categoryType);
        modelAndView.addObject("ctgRootName", AppConstants.CATEGORY.valueOf(CommonUtils.getCategoryType(categoryType)).getLabel());
        modelAndView.addObject("url_template", "");
        modelAndView.addObject("url_import", "");
        modelAndView.addObject("url_export", "");
        return baseView(modelAndView);
    }
}