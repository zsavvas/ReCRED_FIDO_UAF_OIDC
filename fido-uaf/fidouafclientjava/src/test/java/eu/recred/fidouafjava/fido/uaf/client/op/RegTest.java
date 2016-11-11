package eu.recred.fidouafjava.fido.uaf.client.op;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import eu.recred.fidouafjava.fido.uaf.client.op.Reg;
import eu.recred.fidouafjava.fido.uaf.msg.RegistrationRequest;
import eu.recred.fidouafjava.fido.uaf.msg.RegistrationResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RegTest {

    private Gson gson = new Gson();

    @Test
    public void basic() throws Exception {
        String response = new Reg().register(getRequestMessage());
        RegistrationRequest requestObj = gson.fromJson(new JSONArray(getRequestMessage()).getJSONObject(0).toString(),
                RegistrationRequest.class);
        response = new JSONObject(response).getString("uafProtocolMessage");
        response = response.substring(1, (response.length() - 1));
        RegistrationResponse responseObj = gson.fromJson(response, RegistrationResponse.class);
        assertEquals(requestObj.header.serverData, responseObj.header.serverData);
        assertNotNull(responseObj.assertions[0].assertion);
    }

    String getRequestMessage() {
        return "[{\"header\":{\"upv\":{\"major\":1,\"minor\":0},\"op\":\"Reg\",\"appID\":\"http://172.16.101.174:8080/fidouafsvc/v1/trustedfacets\",\"serverData\":\"eFFmbWhvZlVwRzFjOWlIMXJsXzAxNFRWNC03aG0zNzZJb1FNRlEwTk1COC5NVFEzTXpBNE9UZ3lNalF4T0EuYzI5eWFXNC5Ta1JLYUVwRVJYZEtRemg1VFZWU1RVMUhhR3BYVm1SUVdrZDRVazlWVG14VFNHc3hUMVU0\"},\"challenge\":\"JDJhJDEwJC8yMURMMGhjWVdPZGxROUNlSHk1OU8\",\"username\":\"sorin\",\"policy\":{\"accepted\":[[{\"aaid\":[\"EBA0#0001\"]}],[{\"aaid\":[\"0015#0001\"]}],[{\"aaid\":[\"0012#0002\"]}],[{\"aaid\":[\"0010#0001\"]}],[{\"aaid\":[\"4e4e#0001\"]}],[{\"aaid\":[\"5143#0001\"]}],[{\"aaid\":[\"0011#0701\"]}],[{\"aaid\":[\"0013#0001\"]}],[{\"aaid\":[\"0014#0000\"]}],[{\"aaid\":[\"0014#0001\"]}],[{\"aaid\":[\"53EC#C002\"]}],[{\"aaid\":[\"DAB8#8001\"]}],[{\"aaid\":[\"DAB8#0011\"]}],[{\"aaid\":[\"DAB8#8011\"]}],[{\"aaid\":[\"5143#0111\"]}],[{\"aaid\":[\"5143#0120\"]}],[{\"aaid\":[\"4746#F816\"]}],[{\"aaid\":[\"53EC#3801\"]}]]}}]";
    }
}