package eu.recred.fidouafsvc.controller;

import eu.recred.fidouafsvc.model.About;
import eu.recred.fidouafsvc.service.impl.ProcessAuxRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

//import org.springframework.web.bind.annotation.PathVariable;

// TODO: Implement this controller as a Service
@RestController
@RequestMapping("/v1/about")
public class AboutController {

    @Autowired
    ProcessAuxRequestsService processAuxRequestsService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody About getAbout() throws IOException {
        return processAuxRequestsService.getAbout();
	}
}
