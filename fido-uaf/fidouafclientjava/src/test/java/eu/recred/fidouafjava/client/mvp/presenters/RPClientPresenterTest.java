package eu.recred.fidouafjava.client.mvp.presenters;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@Ignore
public class RPClientPresenterTest {

  private RPClientPresenter rpClientPresenter = new RPClientPresenter();

  boolean registered = rpClientPresenter.register("username");

//  @BeforeClass
//  public void setUp() {
//    registered = rpClientPresenter.register("username");
//  }

  @Test
  public void testRegistered() {
    assertTrue(registered);
  }

  @Test
  public void testAuthentication() {
    if (!registered)
      registered = rpClientPresenter.register("username");
    assertTrue(registered);

    boolean success = rpClientPresenter.authenticate("username");
    assertTrue(success);
  }

  @Test
  public void testTransaction() {
    if (!registered)
      registered = rpClientPresenter.register("sorin");
    assertTrue(registered);

    boolean success = rpClientPresenter.transaction("username");
    assertTrue(success);
  }

  @Ignore
  @Test
  public void testDereg() {
    assertTrue(registered);

    boolean success = rpClientPresenter.dereg("username");
    assertTrue(success);
  }

}
