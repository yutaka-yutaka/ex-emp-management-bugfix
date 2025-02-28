package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Administrator;
import com.example.repository.AdministratorRepository;

/**
 * 管理者情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class AdministratorService {

	@Autowired
	private AdministratorRepository administratorRepository;

	/**
	 * 管理者情報を登録します.
	 * 
	 * @param administrator 管理者情報
	 */
	public boolean insert(Administrator administrator) {
		// メールアドレスの重複を確認
		if (administratorRepository.findByMailAddress(administrator.getMailAddress()) != null) {
			return false;
		}
		administratorRepository.insert(administrator);
		return true;
	}

	/**
	 * 管理者情報を取得します.
	 * 
	 * @param administrator 管理者情報
	 * @return
	 */
	public Administrator load(Integer id) {
		return administratorRepository.load(id);
	}

	/**
	 * ログインをします.
	 * 
	 * @param mailAddress メールアドレス
	 * @param password    パスワード
	 * @return 管理者情報 存在しない場合はnullが返ります
	 */
	public Administrator login(String mailAddress, String password) {
		Administrator administrator = administratorRepository.findByMailAddressAndPassward(mailAddress, password);
		return administrator;
	}
}
