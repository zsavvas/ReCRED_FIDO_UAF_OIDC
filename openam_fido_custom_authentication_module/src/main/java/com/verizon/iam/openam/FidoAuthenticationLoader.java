package com.verizon.iam.openam;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.identity.shared.debug.Debug;

public class FidoAuthenticationLoader {

    // Name for the debug-log
    private final static String DEBUG_NAME = "RecredFidoAuth";
    private final static Debug debug = Debug.getInstance(DEBUG_NAME);
	
	public FidoAuthenticationResponse getAuthDtls(String fidoendpointisauthed, String AuthID) 
			throws ClientProtocolException, IOException, KeyManagementException, 
			NoSuchAlgorithmException, KeyStoreException {
		ResponseHandler<FidoAuthenticationResponse> rh = new ResponseHandler<FidoAuthenticationResponse>() {

		    @Override
		    public FidoAuthenticationResponse handleResponse(
		            final HttpResponse response) throws IOException {
		        StatusLine statusLine = response.getStatusLine();
		        HttpEntity entity = response.getEntity();
		        if (statusLine.getStatusCode() >= 300) {
		            throw new HttpResponseException(
		                    statusLine.getStatusCode(),
		                    statusLine.getReasonPhrase());
		        }
		        
		        debug.message("FidoAuthenticationLoader::FidoAuthenticationResponse got value back from the server");
		        
		        if (entity == null) {
		            throw new ClientProtocolException("Response contains no content");
		        }
		        JsonFactory jsonf = new JsonFactory();
		        InputStream instream = entity.getContent();
		        // try - finally is not strictly necessary here 
		        // but is a good practice
		        try {
		        	ObjectMapper mapper = new ObjectMapper();
		     
		            // Use the parser to deserialize the object from the content stream
		            return mapper.readValue(instream, FidoAuthenticationResponse.class);
		        }  finally {
		            instream.close();
		        }
		    }
		};
		HttpClient client = createHttpClient_AcceptsUntrustedCerts();
		return client.execute(new HttpGet(fidoendpointisauthed + AuthID), rh);
	}
	public HttpClient createHttpClient_AcceptsUntrustedCerts() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
	    HttpClientBuilder b = HttpClientBuilder.create();
	 
	    // setup a Trust Strategy that allows all certificates.
	    //
	    SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
	        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	            return true;
	        }
	    }).build();
	    b.setSslcontext( sslContext);
	 
	    // don't check Hostnames, either.
	    //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
	    HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
	 
	    // here's the special part:
	    //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
	    //      -- and create a Registry, to register it.
	    //
	    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
	    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", sslSocketFactory)
	            .build();
	 
	    // now, we create connection-manager using our Registry.
	    //      -- allows multi-threaded use
	    PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
	    b.setConnectionManager( connMgr);
	 
	    // finally, build the HttpClient;
	    //      -- done!
	    HttpClient client = b.build();
	    return client;
	}
}


