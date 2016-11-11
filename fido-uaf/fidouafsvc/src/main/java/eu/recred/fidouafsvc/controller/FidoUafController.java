package eu.recred.fidouafsvc.controller;

import eu.recred.fidouafsvc.model.Facets;
import eu.recred.fidouafsvc.service.impl.ProcessAuxRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

/**
 * Created by georgeg on 01.07.2016.
 */
@RestController
@RequestMapping("/v1")
public class FidoUafController {
    static Logger logger = Logger.getLogger(FidoUafController.class.getName());
    
    @Autowired
    private ProcessAuxRequestsService processAuxRequests;

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public String getStats() {
        return "";
    }

    @RequestMapping(value = "/trustedfacets", method = RequestMethod.GET)
    public Facets getTrustedFacets() {
       return processAuxRequests.getFacets();
    }

    @RequestMapping(value = "/populatetrustedfacets", method = RequestMethod.GET)
    public void populateTrustedFacets() {
        processAuxRequests.populateFacets();
    }
    
    
    @RequestMapping(value = "/isauth/{auth}", method = RequestMethod.GET)
    public String getAuthenticated(@PathVariable(value = "auth")String authenticationId) {
    	return processAuxRequests.getAuthenticated(authenticationId);
    }
}
