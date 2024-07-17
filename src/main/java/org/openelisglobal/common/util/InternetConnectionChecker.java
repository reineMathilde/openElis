package org.openelisglobal.common.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetConnectionChecker {
	public static boolean isInternetAvailable() {
		try {
			URL url = new URL("http://www.google.com");
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			urlConnect.setConnectTimeout(1000);
			urlConnect.connect();
			return (urlConnect.getResponseCode() == 200);
		} catch (IOException e) {
			return false;
		}
	}
}
