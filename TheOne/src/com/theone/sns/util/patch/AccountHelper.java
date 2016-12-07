package com.theone.sns.util.patch;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.theone.sns.TheOneApp;

public class AccountHelper {

	@SuppressLint("NewApi")
	public static String getDefaultEmail() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR)
			return null;
		Context ctx = TheOneApp.getContext();
		AccountManager accountManager = AccountManager.get(ctx);
		Account[] accounts = accountManager.getAccounts();
		if (accounts == null || accounts.length < 1)
			return null;
		for (Account account : accounts) {
			String name = account.name;
			if (name.indexOf('@') > 1)
				return name;
		}
		return null;
	}
}
