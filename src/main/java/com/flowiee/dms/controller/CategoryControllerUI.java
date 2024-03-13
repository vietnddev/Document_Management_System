package com.flowiee.dms.controller;

import com.flowiee.dms.core.BaseController;
import com.flowiee.dms.entity.Category;
import com.flowiee.dms.core.exception.NotFoundException;
import com.flowiee.dms.service.CategoryService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.PagesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@RestController
@RequestMapping("/system/category")
public class CategoryControllerUI extends BaseController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryControllerUI(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ModelAndView viewRootCategory() {
        vldModuleCategory.readCategory(true);
        ModelAndView modelAndView = new ModelAndView(PagesUtils.CTG_CATEGORY);
        modelAndView.addObject("category", new Category());
        modelAndView.addObject("listCategory", categoryService.findRootCategory());
        return baseView(modelAndView);
    }

    @GetMapping("/{type}")
    public ModelAndView viewSubCategory(@PathVariable("type") String categoryType) {
        vldModuleCategory.readCategory(true);
        if (CommonUtils.getCategoryType(categoryType) == null) {
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