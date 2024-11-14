package com.flowiee.dms.controller.system;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.utils.EndPointUtil;
import com.flowiee.dms.utils.PagesUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class ProfileControllerView extends BaseController {
	@GetMapping(EndPointUtil.SYS_PROFILE)
	public ModelAndView showInformation(@ModelAttribute("message") String message) {
		ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_PROFILE);
		modelAndView.addObject("message", message);
		modelAndView.addObject("profile", accountService.findCurrentAccount());
		return baseView(modelAndView);
	}

	@PostMapping(EndPointUtil.SYS_PROFILE_UPDATE)
	public ModelAndView updateProfile(@ModelAttribute("account") Account pAccount) {
		Account account = accountService.findCurrentAccount();
		account.setPhoneNumber(pAccount.getPhoneNumber());
		account.setEmail(pAccount.getEmail());
		account.setDiaChi(pAccount.getDiaChi());
		accountService.update(account, account.getId());
		return new ModelAndView("redirect:/profile");
	}

	@PostMapping(EndPointUtil.SYS_PROFILE_CHANGEPASSWORD)
	public ModelAndView changePassword(HttpServletRequest request,
									   @ModelAttribute("account") Account accountEntity,
									   RedirectAttributes redirectAttributes) {
		String password_old = request.getParameter("password_old");
		String password_new = request.getParameter("password_new");
		String password_renew = request.getParameter("password_renew");

		Account profile = accountService.findCurrentAccount();

		BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
		if (bCrypt.matches(password_old, accountService.findByUsername(profile.getUsername()).getPassword())) {
			if (password_new.equals(password_renew)) {
				profile.setPassword(bCrypt.encode(password_new));
				accountService.save(accountEntity);

				redirectAttributes.addAttribute("message", "Cập nhật thành công!");
				RedirectView redirectView = new RedirectView();
				redirectView.setUrl("/profile");
				return new ModelAndView(redirectView);
			}
			redirectAttributes.addAttribute("message", "Mật khẩu nhập lại chưa khớp!");
		}
		redirectAttributes.addAttribute("message", "Sai mật khẩu hiện tại!");

		return new ModelAndView("redirect:/profile");
	}
}