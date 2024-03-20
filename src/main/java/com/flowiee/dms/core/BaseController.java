package com.flowiee.dms.core;

import com.flowiee.dms.core.vld.ValidateModuleCategory;
import com.flowiee.dms.core.vld.ValidateModuleStorage;
import com.flowiee.dms.core.vld.ValidateModuleSystem;
import com.flowiee.dms.service.AccountService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.EndPointUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Component
public class BaseController {
	protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired protected ValidateModuleSystem vldModuleSystem;
	@Autowired protected ValidateModuleCategory vldModuleCategory;
	@Autowired protected ValidateModuleStorage vldModuleStorage;
	@Autowired protected BaseAuthorize baseAuthorize;
	@Autowired protected AccountService accountService;

	protected ModelAndView baseView(ModelAndView modelAndView) {
		modelAndView.addObject("USERNAME_LOGIN", CommonUtils.getUserPrincipal().getUsername());
		setURLHeader(modelAndView);
		setURLSidebar(modelAndView);
		return modelAndView;
	}

	private void setURLHeader(ModelAndView modelAndView) {
		modelAndView.addObject("URL_PROFILE", EndPointUtil.SYS_PROFILE);
		modelAndView.addObject("URL_LOGOUT", EndPointUtil.SYS_LOGOUT);
	}
	
	private void setURLSidebar(ModelAndView modelAndView) {
		modelAndView.addObject("URL_CATEGORY", EndPointUtil.CATEGORY);
		modelAndView.addObject("URL_STORAGE_DASHBOARD", EndPointUtil.STORAGE);
		modelAndView.addObject("URL_STORAGE_DOCUMENT", EndPointUtil.STORAGE_DOCUMENT);
		modelAndView.addObject("URL_SYSTEM_CONFIG", EndPointUtil.SYS_CONFIG);
		modelAndView.addObject("URL_SYSTEM_LOG", EndPointUtil.SYS_LOG);
		modelAndView.addObject("URL_SYSTEM_ACCOUNT", EndPointUtil.SYS_ACCOUNT);
	}
}