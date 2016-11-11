package eu.recred.fidouafjava.client.util;

import org.junit.Before;
import org.junit.Test;
import eu.recred.fidouafjava.client.util.Preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PreferencesTest {

  private Preferences preferences = Preferences.getInstance();

  @Before
  public void setUp() {

  }

  @Test
  public void loadEndpointsConfigTest() throws Exception {
    // load endpoints config.
    preferences.loadEndpointsConfig();

    // now assert endpoints.
    assertTrue(preferences.getServer().length() > 0);
    assertTrue(preferences.getRegRequestEndpoint().length() > 0);
    assertTrue(preferences.getAuthRequestEndpoint().length() > 0);
    assertTrue(preferences.getRegResponseEndpoint().length() > 0);
    assertTrue(preferences.getDeregEndpoint().length() > 0);
    assertTrue(preferences.getAuthResponseEndpoint().length() > 0);
  }

  @Test
  public void getSetParamTest() {
    // set the param.
    preferences.setSettingsParam("prop", "value");

    // assert the param.
    String param = preferences.getSettingsParam("prop");
    assertEquals("value", param);
  }

  @Test
  public void registrationFileTest() throws Exception {
    // set up registrationData.
    preferences.setSettingsParam("keyId", "keyId");
    preferences.setSettingsParam("keyID", "keyID");
    preferences.setSettingsParam("pub", "pub");
    preferences.setSettingsParam("priv", "priv");
    preferences.setSettingsParam("AAID", "AAID");

    // write the file.
    preferences.writeRegistrationFile("username");

    // now assert values.
    preferences.loadRegistrationFile("username");
    assertEquals(preferences.getSettingsParam("keyId"), "keyId");
    assertEquals(preferences.getSettingsParam("keyID"), "keyID");
    assertEquals(preferences.getSettingsParam("pub"), "pub");
    assertEquals(preferences.getSettingsParam("priv"), "priv");
    assertEquals(preferences.getSettingsParam("AAID"), "AAID");
  }

}
