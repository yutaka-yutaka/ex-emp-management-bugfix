package com.example.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.domain.Administrator;
import com.example.form.InsertAdministratorForm;
import com.example.form.LoginForm;
import com.example.service.AdministratorService;

import jakarta.servlet.http.HttpSession;

/**
 * 管理者情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/")
public class AdministratorController {

	@Autowired
	private AdministratorService administratorService;

	@Autowired
	private HttpSession session;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public InsertAdministratorForm setUpInsertAdministratorForm() {
		return new InsertAdministratorForm();
	}

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public LoginForm setUpLoginForm() {
		return new LoginForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：管理者を登録する
	/////////////////////////////////////////////////////
	/**
	 * 管理者登録画面を出力します.
	 * 
	 * @return 管理者登録画面
	 */
	@GetMapping("/toInsert")
	public String toInsert() {
		return "administrator/insert";
	}

	/**
	 * 管理者情報を登録します.
	 * 
	 * @param form 管理者情報用フォーム
	 * @return ログイン画面へリダイレクト
	 */
	@PostMapping("/insert")
	public String insert(@Validated InsertAdministratorForm form, BindingResult result) {
		// バリデーションエラーがある場合は登録画面に戻す
		if (result.hasErrors()) {
			return "administrator/insert";
		}

		// パスワードと確認用パスワードの一致をチェック
		if (!form.getPassword().equals(form.getConfirmPassword())) {
			result.rejectValue("confirmPassword", null, "パスワードが一致しません");
			return "administrator/insert";
		}

		Administrator administrator = new Administrator();
		// フォームからドメインにプロパティ値をコピー
		BeanUtils.copyProperties(form, administrator);

		// メールアドレスの重複を確認
		if (!administratorService.insert(administrator)) {
			result.rejectValue("mailAddress", null, "既に使われているメールアドレスです");
			return "administrator/insert";
		}
		return "redirect:";
	}

	/////////////////////////////////////////////////////
	// ユースケース：ログインをする
	/////////////////////////////////////////////////////
	/**
	 * ログイン画面を出力します.
	 * 
	 * @return ログイン画面
	 */
	@GetMapping("/")
	public String toLogin() {
		return "administrator/login";
	}

	/**
	 * ログインします.
	 * 
	 * @param form 管理者情報用フォーム
	 * @return ログイン後の従業員一覧画面
	 */
	@PostMapping("/login")
	public String login(@Validated LoginForm form, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "administrator/login";
		}
		Administrator administrator = administratorService.login(form.getMailAddress(), form.getPassword());
		if (administrator == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "メールアドレスまたはパスワードが不正です。");
			return "redirect:/";
		}
		session.setAttribute("administrator", administrator);
		return "redirect:/employee/showList";
	}

	/////////////////////////////////////////////////////
	// ユースケース：ログアウトをする
	/////////////////////////////////////////////////////
	/**
	 * ログアウトをします. (SpringSecurityに任せるためコメントアウトしました)
	 * 
	 * @return ログイン画面
	 */
	@GetMapping(value = "/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/";
	}

}
