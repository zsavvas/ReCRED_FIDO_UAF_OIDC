package eu.recred.fidouafsvc.model;

import eu.recred.fido.uaf.msg.Version;

/**
 * Created by georgeg on 02/07/16.
 */
public class TrustedFacets {

    public Version version;

    public String[] ids;

    // TODO:
    public TrustedFacets() {
        version = null;
        ids = null;
    }
}
