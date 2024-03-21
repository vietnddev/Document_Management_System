package com.flowiee.dms.controller.system;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.exception.ErrorResponse;
import com.flowiee.dms.utils.MessageUtils;
import com.flowiee.dms.utils.PagesUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorControllerView extends BaseController {
    @GetMapping("/403")
    public ModelAndView forbiddenException() {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), MessageUtils.ERROR_FORBIDDEN);
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }

    @GetMapping("/404")
    public ModelAndView notfoundException() {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), MessageUtils.ERROR_NOTFOUND);
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }
}