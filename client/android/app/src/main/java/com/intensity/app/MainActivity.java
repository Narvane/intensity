package com.intensity.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.activity.EdgeToEdge;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Required by @capacitor-community/safe-area: draw under system bars and
		// let CSS env(safe-area-inset-*) (or the plugin padding fallback) own insets.
		EdgeToEdge.enable(this);
		disableWebViewZoom();
	}

	@Override
	public void onStart() {
		super.onStart();
		disableWebViewZoom();
	}

	private void disableWebViewZoom() {
		if (getBridge() == null) {
			return;
		}

		WebView webView = getBridge().getWebView();
		if (webView == null) {
			return;
		}

		WebSettings settings = webView.getSettings();
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		settings.setDisplayZoomControls(false);
	}
}
