package eu.recred.fidouafsvc.controller;

import com.google.gson.Gson;
import eu.recred.fidouafsvc.dao.TrustedFacetDao;
import eu.recred.fidouafsvc.service.impl.ProcessAuxRequestsService;
import eu.recred.fidouafsvc.service.impl.ProcessResponseService;
import eu.recred.fidouafsvc.storage.AuthenticatorRecord;
import eu.recred.fido.uaf.msg.AuthenticationResponse;
import eu.recred.fido.uaf.msg.RegistrationResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/fidouafsvc-servlet.xml"})
public class AuthenticationIdTest {
    private Gson gson = new Gson();

    @Autowired
    private TrustedFacetDao trustedFacetDao;

    @Autowired
    private ProcessResponseService processResponseService;

    @Autowired
    private ProcessAuxRequestsService processAuxRequestsService;

    private AuthenticatorRecord[] result;

    @Before
    public void setUp() {
        trustedFacetDao.populateTrustedFacets();

        RegistrationResponse[] fromJson = gson.fromJson(getRegResponse(), RegistrationResponse[].class);
        RegistrationResponse response = fromJson[0];
        processResponseService.processRegResponse(response);

        AuthenticationResponse[] responses = gson.fromJson(getAuthResponse(), AuthenticationResponse[].class);
        result = processResponseService.processAuthResponse(responses[0]);
    }

    @Ignore("Not yet!")
    @Test
    public void testId() {
        String response = processAuxRequestsService.getAuthenticated(result[0].authenticationId);
    }

    String getRegResponse() {
        return "[{\"header\":{\"upv\":{\"major\":1,\"minor\":0},\"op\":\"Reg\",\"appID\":\"http://172.16.101.174:8080/fidouafsvc/v1/trustedfacets\",\"serverData\":\"N0FVWU1WSXhJbjhJTUJSN3g4Q0RPTG1UZVk4bm53NU5EbjNjN1lvY0s2QS5NVFEzTXpFMU16QTRPRFEzTncuWVc1aC5Ta1JLYUVwRVJYZEtTRVpaVjFWa2RVeDZXbXhTTWtwUVl6QndNVk51VG10TmJFNUpZekpW\"},\"fcParams\":\"eyJhcHBJRCI6Imh0dHA6Ly8xNzIuMTYuMTAxLjE3NDo4MDgwL2ZpZG91YWZzdmMvdjEvdHJ1c3Rl\n" +
                "ZGZhY2V0cyIsImNoYWxsZW5nZSI6IkpESmhKREV3SkhGWVdVZHVMelpsUjJKUGMwcDFTbk5rTWxO\n" +
                "SWMyVSIsImZhY2V0SUQiOiIifQ==\n" +
                "\",\"assertions\":[{\"assertionScheme\":\"UAFV1TLV\",\"assertion\":\"AT4nAwM-2gALLgkARUJBMCMwMDAxDi4HAAAAAQEAAAEKLiAAiEGqeKAiyNhFI65GCV6kkI1dzt90\n" +
                "g0GhSQppe9MejIsJLkkAWldKaGVTMTBaWE4wTFd0bGVTMUtSRXBvU2tSRmQwcEVSbTFoUXpoMVZV\n" +
                "YzFVbFpETlhwYVJVWndaREJXU1dGRVZsVlphVFE5Cg0uCAAAAAEAAAABAAwuQQAEyk5bm4G81x6u\n" +
                "GaRuL1NJyf4NFJZU1oWmM0u6YcDDBK41kJDVKCowyqf7H8oHIfyvIJptZtBdF9z4qAI3eml5Qgc-\n" +
                "RQIGLkAAeDz7-FBqGjmUZH-No-sJU3NdRsbLsePj6ohFUVwPclqOiq_-KyXQVzhBitasQ_PSLuJB\n" +
                "49PBm88OIIpcG417DgUu_QEwggH5MIIBn6ADAgECAgRVMUzTMAkGByqGSM49BAEwgYQxCzAJBgNV\n" +
                "BAYTAlVTMQswCQYDVQQIDAJDQTERMA8GA1UEBwwIU2FuIEpvc2UxEzARBgNVBAoMCmVCYXksIElu\n" +
                "Yy4xDDAKBgNVBAsMA1ROUzESMBAGA1UEAwwJZUJheSwgSW5jMR4wHAYJKoZIhvcNAQkBFg9ucGVz\n" +
                "aWNAZWJheS5jb20wHhcNMTUwNDE3MTgxMTMxWhcNMTUwNDI3MTgxMTMxWjCBhDELMAkGA1UEBhMC\n" +
                "VVMxCzAJBgNVBAgMAkNBMREwDwYDVQQHDAhTYW4gSm9zZTETMBEGA1UECgwKZUJheSwgSW5jLjEM\n" +
                "MAoGA1UECwwDVE5TMRIwEAYDVQQDDAllQmF5LCBJbmMxHjAcBgkqhkiG9w0BCQEWD25wZXNpY0Bl\n" +
                "YmF5LmNvbTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABDyHDmUdNRe9ndLNj1quBs44EPamfnMA\n" +
                "ziZtKHBDIIvltGyRMvcdU9Y9DGphUz7ekm0kgtio5awgx3lFv-HwP4QwCQYHKoZIzj0EAQNJADBG\n" +
                "AiEAimSin8vd862bv1DaUqsrF5r4py8DWDqqTNqPjZUPaGACIQCu0-lMQAMhbPbKuXlcZ8lVx9wW\n" +
                "a-kwC4wq-4TUnveOng==\n" +
                "\"}]}]";
    }


    String getAuthResponse() {
        return "[{\"header\":{\"upv\":{\"major\":1,\"minor\":0},\"op\":\"Auth\",\"appID\":\"http://172.16.101.174:8080/fidouafsvc/v1/trustedfacets\",\"serverData\":\"aWRrcGFlMGJqMVdGYmR2eVFMWEZFTGxPMThJZVFYR1RHUk5TckdPd1dSYy5NVFEzTXpFME5EQTROek14TkEuU2tSS2FFcEVSWGRLUmxFeldYcEtlbEZUTlc5bFJWa3hWMFJhY0dGV1FuaFNiVnBTVGpBNA\"},\"fcParams\":\"eyJhcHBJRCI6Imh0dHA6Ly8xNzIuMTYuMTAxLjE3NDo4MDgwL2ZpZG91YWZzdmMvdjEvdHJ1c3Rl\n" +
                "ZGZhY2V0cyIsImNoYWxsZW5nZSI6IkpESmhKREV3SkZRM1l6SnpRUzVvZUVZMVdEWnBhVkJ4Um1a\n" +
                "Uk4wOCIsImZhY2V0SUQiOiIifQ==\n" +
                "\",\"assertions\":[{\"assertionScheme\":\"UAFV1TLV\",\"assertion\":\"Aj4fAQQ+1wALLgkARUJBMCMwMDAxDi4FAAAAAQEADy5AAGQ1ZWFlNDYzMTE4MDQ0MDBjZDU5OTZj\n" +
                "NGMwODRhYWRlZTA4MWZkZjcxNGM5M2I5ODNmYmE0Nzk1YmQ4OTRhMjgKLiAAFicGQxTIZ2mzgdgE\n" +
                "xG6lJ3IaQAnmMutp/wcvr/IJp/IQLgAACS5JAFpXSmhlUzEwWlhOMExXdGxlUzFLUkVwb1NrUkZk\n" +
                "MHBFUm0xaFF6aDFWVWMxVWxaRE5YcGFSVVp3WkRCV1NXRkVWbFZaYVRROQoNLgQAAAABAAYuQACX\n" +
                "jfpH1Hed4eBQHV5dpGp7vR2YkSjaV1WdVSVBjfNb4UPBcMgv2ER9+e7B6YDQPXTbFez8ZI5UgvII\n" +
                "AYf8vT4p\n" +
                "\"}]}]";
    }
}