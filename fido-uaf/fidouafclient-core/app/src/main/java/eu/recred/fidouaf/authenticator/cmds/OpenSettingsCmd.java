package eu.recred.fidouaf.authenticator.cmds;

import eu.recred.fidouaf.tlv.ByteInputStream;
import eu.recred.fidouaf.tlv.TagsEnum;
import eu.recred.fidouaf.tlv.UnsignedUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by sorin.teican on 10/12/2016.
 */
//TODO: invoke KeyGuard to change authentication mode.
public class OpenSettingsCmd {

    int requestLength;
    int authenticatorIndex;

    public byte[] process(ByteInputStream request) {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        //parseRequest(request);

        return byteout.toByteArray();
    }

    private void parseRequest(ByteInputStream request) throws IOException {
        int tag;
        int len;
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();

        requestLength = UnsignedUtil.read_UAFV1_UINT16(request);

        tag = UnsignedUtil.read_UAFV1_UINT16(request);
        if (tag == TagsEnum.TAG_AUTHENTICATOR_INDEX.id) {
            len = UnsignedUtil.read_UAFV1_UINT16(request);
            authenticatorIndex = UnsignedUtil.read_UAFV1_UINT16(request);
        } else return;
    }

    private byte[] deregResponse() throws IOException{
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_UAFV1_OPEN_SETTINGS_CMD_RESPONSE.id));
        value = buildResponse();
        length = value.length;
        byteout.write(UnsignedUtil.encodeInt(length));
        byteout.write(value);

        return byteout.toByteArray();
    }

    private byte[] buildResponse() throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        byteout.write(UnsignedUtil.encodeInt(TagsEnum.TAG_STATUS_CODE.id));
        byteout.write(UnsignedUtil.encodeInt(2));
        byteout.write(UnsignedUtil.encodeInt(TagsEnum.UAF_CMD_STATUS_OK.id));

        return byteout.toByteArray();
    }
}
