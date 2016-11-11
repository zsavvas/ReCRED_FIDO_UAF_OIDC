package eu.recred.fidouaf.authenticator.cmds;

import eu.recred.fidouaf.tlv.ByteInputStream;
import eu.recred.fidouaf.tlv.TagsEnum;
import eu.recred.fidouaf.tlv.UnsignedUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by sorin.teican on 10/12/2016.
 */
//TODO: Remove alias from Android Key Store.
public class DeregCmd {

    private int requestLength;
    private int authenticatorIndex;
    private byte[] appID;
    private byte[] keyID;
    private byte[] khAccessToken;

    public byte[] process(ByteInputStream request) throws IOException {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        byte[] value;
        int length;

        parseRequest(request);

        return byteout.toByteArray();
    }

    private void parseRequest(ByteInputStream request) throws IOException {
        int tag;
        int len;
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();

        requestLength = UnsignedUtil.read_UAFV1_UINT16(request);

        while (request.available() > 0) {
            tag = UnsignedUtil.read_UAFV1_UINT16(request);

            if (tag == TagsEnum.TAG_AUTHENTICATOR_INDEX.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request);
                authenticatorIndex = UnsignedUtil.read_UAFV1_UINT16(request);
            } else if (tag == TagsEnum.TAG_APPID.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                appID = byteout.toByteArray();
            } else if (tag == TagsEnum.TAG_KEYID.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                keyID = byteout.toByteArray();
            } else if (tag == TagsEnum.TAG_KEYHANDLE_ACCESS_TOKEN.id) {
                len = UnsignedUtil.read_UAFV1_UINT16(request) / 2;
                for (int i = 0; i < len; i++)
                    byteout.write(UnsignedUtil.encodeInt(UnsignedUtil.read_UAFV1_UINT16(request)));
                khAccessToken = byteout.toByteArray();
            } else { // Unknown tag.
                return;
            }

            byteout.reset();
        }
    }
}
