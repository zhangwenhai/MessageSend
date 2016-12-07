package com.theone.sns.ui.login;

import android.os.Bundle;
import android.webkit.WebView;

import com.theone.sns.R;
import com.theone.sns.ui.base.IphoneTitleActivity;

public class ReadPrivacyAcitivity extends IphoneTitleActivity {

	private WebView privacyWebView;

	@Override
	protected void initLogics() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.read_privacy_main);
		setTitle(R.string.read_privacy_title);
		setLeftButton(R.drawable.icon_back, false, false);
		privacyWebView = (WebView) findViewById(R.id.webview);
		privacyWebView.loadUrl("file:///android_asset/terms.html");
	}
}
