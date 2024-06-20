package com.flowiee.dms.exception;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.utils.PagesUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {
    @ExceptionHandler
    public ModelAndView exceptionHandler(AuthenticationException ex) {
        logger.error(ex.getMessage(), ex);
        return new ModelAndView(PagesUtils.SYS_LOGIN);
    }

    @ExceptionHandler
    public ModelAndView exceptionHandler(ResourceNotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }

    @ExceptionHandler
    public ApiResponse<?> exceptionHandler(BadRequestException ex) {
        logger.error(ex.getMessage(), ex);
        return ApiResponse.fail(ex.getMessage(), ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ApiResponse<?> exceptionHandler(DataExistsException ex) {
        logger.error(ex.getMessage(), ex);
        return ApiResponse.fail(ex.getMessage(), ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ModelAndView exceptionHandler(ForbiddenException ex) {
        logger.error(ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }

    @ExceptionHandler
    public ApiResponse<?> exceptionHandler(DataInUseException ex) {
        logger.error(ex.getMessage(), ex);
        return ApiResponse.fail(ex.getMessage(), ex, HttpStatus.LOCKED);
    }

    @ExceptionHandler
    public ApiResponse<?> exceptionHandler(AppException ex) {
        logger.error(ex.getMessage(), ex);
        return ApiResponse.fail(ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler
//    public ModelAndView exceptionHandler(Exception ex) {
//        logger.error("Exception2", ex);
//        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
//        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
//        modelAndView.addObject("error", error);
//        return baseView(modelAndView);
//    }

    @ExceptionHandler
    public ApiResponse<?> exceptionHandler(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        return ApiResponse.fail(ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ApiResponse<?> exceptionHandler(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ApiResponse.fail(ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}