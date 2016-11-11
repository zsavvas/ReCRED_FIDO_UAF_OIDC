package eu.recred.fidouaf.res.util;

import java.util.ArrayList;
import java.util.List;

public class ProcessAuxRequests {

	private static boolean m_populatedIds = false;
	private static List<String> m_trustedIds = new ArrayList<>();

	public AuthIdResponse getAuthenticated(String authenticationId) {
		if (authenticationId == null || authenticationId.isEmpty()) {
			AuthIdResponse response = new AuthIdResponse();
			response.authenticated = false;
			response.username = null;
			response.timestamp = -1;
			return response;
		}
		String username = StorageImpl.getInstance().getAuthenticated(authenticationId);
		AuthIdResponse response = new AuthIdResponse();
		if (username != null) {
			response.authenticated = true;
			response.username = username;
			response.timestamp = StorageImpl.getInstance().getLastAuth(username).timestamp.getTime();
		} else {
			response.authenticated = false;
			response.username = null;
			response.timestamp = -1;
		}
		return response;
	}

	public class AuthIdResponse {
		public boolean authenticated;
		public String username;
		public long timestamp;
	}

	//-----------------Start of AddFacetId Functionality-------------------------------------------.

	// Initial trustedFactes.
	private static void populateTrustedIds() {
		m_trustedIds.add("https://www.head2toes.org");
		m_trustedIds.add("android:apk-key-hash:Df+2X53Z0UscvUu6obxC3rIfFyk");
		m_trustedIds.add("android:apk-key-hash:bE0f1WtRJrZv/C0y9CM73bAUqiI");
		m_trustedIds.add("android:apk-key-hash:Lir5oIjf552K/XN4bTul0VS3GfM");
		m_trustedIds.add("android:apk-key-hash:RVimey8gA1qIjM9FAc5yCjAbZcQ");
		m_trustedIds.add("android:apk-key-hash:QYqRW1WIuO/S9OFkfcotZ90xRzY");
		m_trustedIds.add("android:apk-key-hash:bdoYthDGgXnJVnnljazcCADQKls");
		m_trustedIds.add("https://openidconnect.ebay.com");

		m_populatedIds = true;
	}

	public static String[] getTrustedIds() {
		if (m_populatedIds == false)
			populateTrustedIds();

	 	String[] ids = m_trustedIds.toArray(new String[m_trustedIds.size()]);
	 	return ids;
	}

	public static void addFacetId(String facetId) {
		m_trustedIds.add(facetId);
	}

	//-----------------End of AddFacetId Functionality-------------------------------------------.

}