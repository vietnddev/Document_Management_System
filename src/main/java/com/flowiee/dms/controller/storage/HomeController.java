package com.flowiee.dms.controller.storage;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.model.DashboardModel;
import com.flowiee.dms.service.storage.DashboardService;
import com.flowiee.dms.utils.PagesUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HomeController extends BaseController {
    DashboardService dashboardService;

    @GetMapping
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView dashboard() {
        DashboardModel dashboardModel = dashboardService.loadDashboard();
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DASHBOARD);
        modelAndView.addObject("dashboardModel", dashboardModel);
        return baseView(modelAndView);
    }
}