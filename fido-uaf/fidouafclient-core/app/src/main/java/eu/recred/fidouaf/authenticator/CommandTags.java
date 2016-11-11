package eu.recred.fidouaf.authenticator;

/**
 * Created by sorin.teican on 10/5/2016.
 */
public enum CommandTags {
    TAG_UAFV1_GETINFO_CMD(0x3401),
    TAG_UAFV1_GETINFO_CMD_RESPONSE(0x3601),
    TAG_UAFV1_REGISTER_CMD(0x3402),
    TAG_UAFV1_REGISTER_CMD_RESPONSE(0x3602),
    TAG_UAFV1_SIGN_CMD(0x3403),
    TAG_UAFV1_SIGN_CMD_RESPONSE(0x3603),
    TAG_UAFV1_DEREGISTER_CMD(0x3404),
    TAG_UAFV1_DEREGISTER_CMD_RESPONSE(0x3606),
    TAG_UAGV1_OPEN_SETTINGS_CMD(0x3406),
    TAG_UAFV1_OPEN_SETTINGS_CMD_RESPONSE(0x3606);

    final public int id;

    CommandTags (int id){
        this.id = id;
    }

    public static CommandTags get(int id){
        for (CommandTags tag : CommandTags.values()) {
            if (tag.id == id){
                return tag;
            }
        }
        return null;
    }
}
