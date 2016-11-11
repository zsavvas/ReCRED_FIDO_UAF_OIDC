package eu.recred.fidouafjava.fido.uaf.client.op;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import eu.recred.fidouafjava.client.mvp.presenters.RPClientPresenter;
import eu.recred.fidouafjava.fido.uaf.client.op.Auth;
import eu.recred.fidouafjava.fido.uaf.msg.AuthenticationRequest;
import eu.recred.fidouafjava.fido.uaf.msg.AuthenticationResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by sorin.teican on 9/6/2016.
 */
@Ignore
public class AuthTest {

    private Gson gson = new Gson();
    private RPClientPresenter rpClientPresenter;

    @Before
    public void setUp() {
        rpClientPresenter = new RPClientPresenter();
        rpClientPresenter.register("ana");
        rpClientPresenter.loadRegistrationFile("ana");
    }

    @Test
    public void basic() {
        String response = new Auth().auth(getRequest());
        AuthenticationRequest requestObj = gson.fromJson(new JSONArray(getRequest()).getJSONObject(0).toString(),
                AuthenticationRequest.class);
        response = new JSONObject(response).getString("uafProtocolMessage");
        response = response.substring(1, (response.length() - 1));
        AuthenticationResponse responseObj = gson.fromJson(response, AuthenticationResponse.class);
        assertEquals(requestObj.header.serverData, responseObj.header.serverData);
        assertNotNull(responseObj.assertions[0].assertion);
    }

    String getRequest() {
        return "[{\"header\":{\"upv\":{\"major\":1,\"minor\":0},\"op\":\"Auth\",\"appID\":\"http://172.16.101.174:8080/fidouafsvc/v1/trustedfacets\",\"serverData\":\"aWRrcGFlMGJqMVdGYmR2eVFMWEZFTGxPMThJZVFYR1RHUk5TckdPd1dSYy5NVFEzTXpFME5EQTROek14TkEuU2tSS2FFcEVSWGRLUmxFeldYcEtlbEZUTlc5bFJWa3hWMFJhY0dGV1FuaFNiVnBTVGpBNA\"},\"challenge\":\"JDJhJDEwJFQ3YzJzQS5oeEY1WDZpaVBxRmZRN08\",\"policy\":{\"accepted\":[[{\"aaid\":[\"EBA0#0001\"]}],[{\"aaid\":[\"0015#0001\"]}],[{\"aaid\":[\"0012#0002\"]}],[{\"aaid\":[\"0010#0001\"]}],[{\"aaid\":[\"4e4e#0001\"]}],[{\"aaid\":[\"5143#0001\"]}],[{\"aaid\":[\"0011#0701\"]}],[{\"aaid\":[\"0013#0001\"]}],[{\"aaid\":[\"0014#0000\"]}],[{\"aaid\":[\"0014#0001\"]}],[{\"aaid\":[\"53EC#C002\"]}],[{\"aaid\":[\"DAB8#8001\"]}],[{\"aaid\":[\"DAB8#0011\"]}],[{\"aaid\":[\"DAB8#8011\"]}],[{\"aaid\":[\"5143#0111\"]}],[{\"aaid\":[\"5143#0120\"]}],[{\"aaid\":[\"4746#F816\"]}],[{\"aaid\":[\"53EC#3801\"]}]]}}]";
    }
}
