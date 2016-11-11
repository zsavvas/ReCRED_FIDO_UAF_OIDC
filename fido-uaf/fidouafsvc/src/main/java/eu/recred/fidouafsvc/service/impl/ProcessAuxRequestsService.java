package eu.recred.fidouafsvc.service.impl;

import com.google.gson.JsonObject;
import eu.recred.fidouafsvc.dao.TrustedFacetDao;
import eu.recred.fidouafsvc.model.About;
import eu.recred.fidouafsvc.model.Facets;
import eu.recred.fidouafsvc.model.TrustedFacet;
import eu.recred.fidouafsvc.model.TrustedFacets;
import eu.recred.fidouafsvc.storage.StorageInterface;
import eu.recred.fido.uaf.msg.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.Manifest;

//import org.springframework.context.annotation.DependsOn;

@Service
public class ProcessAuxRequestsService {
	
	public ProcessAuxRequestsService() {
		// TODO Auto-generated constructor stub
		//System.out.println("ProcessAuxRequestsService created!");
	}

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private TrustedFacetDao trustedFacetDao;
	
	@Autowired @Qualifier("storageDao")
    private StorageInterface storageDao;
	
	public String getAuthenticated(String authenticationId) {
		JsonObject response = new JsonObject();
		if (authenticationId == null || authenticationId.trim().isEmpty()) {
			response.addProperty("authenticated", false);
			response.addProperty("username", "");
			return response.toString();
		}
		String username = storageDao.getAuthenticated(authenticationId);
		if (username != null) {
			response.addProperty("authenticated", true);
			response.addProperty("username", username);
		} else {
			response.addProperty("authenticated", false);
			response.addProperty("username", "");
		}
		return response.toString();
	}

	public Facets getFacets() {
		List<TrustedFacet> facets = trustedFacetDao.listAllTrustedFacets();
		String[] trustedIds = new String[facets.size()];

		TrustedFacets trusted = new TrustedFacets();
		trusted.version = new Version(1, 0);
		trusted.ids = trustedIds;
		for (int i = 0; i < facets.size(); i++)
			trustedIds[i] = facets.get(i).getName();
		trusted.ids = trustedIds;
		Facets trustedFacets = new Facets();
		trustedFacets.trustedFacets = new TrustedFacets[1];
		trustedFacets.trustedFacets[0] = trusted;
		return trustedFacets;
	}

	public void populateFacets() {
		trustedFacetDao.populateTrustedFacets();
	}

	public About getAbout() throws IOException {
		Resource resManifest = applicationContext.getResource("/META-INF/MANIFEST.MF");
		InputStream streamManifest = resManifest.getInputStream();
		return new About( new Manifest(streamManifest) );
	}
}
