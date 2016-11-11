package eu.recred.fidouafsvc.model;

/**
 * Created by georgeg on 02/07/16.
 */
public class FidoConfig {

    // TODO: base uri should be in config
    private String appId = "http://172.16.101.174:8080/fidouaf-svc/v1/trustedfacets";
    private String[] aaids = { "EBA0#0001", "0015#0001", "0012#0002", "0010#0001",
            "4e4e#0001", "5143#0001", "0011#0701", "0013#0001",
            "0014#0000", "0014#0001", "53EC#C002", "DAB8#8001",
            "DAB8#0011", "DAB8#8011", "5143#0111", "5143#0120",
            "4746#F816", "53EC#3801" };

    public String getAppId() {
        return appId;
    }

    public String[] getAaids() { return aaids; }

}
