package com.axel_stein.auth;

import java.io.IOException;
import java.util.List;

import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class NTLMAuthenticator implements Authenticator {
    private final String domain;
    private final String username;
    private final String password;
    private final String ntlmMsg1;
    private final String workstation = "android-device";

    public NTLMAuthenticator(String username, String password, String domain) {
        this.domain = domain;
        this.username = username;
        this.password = password;
        String localNtlmMsg1 = null;
        try {
            localNtlmMsg1 = generateType1Msg(domain, workstation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ntlmMsg1 = localNtlmMsg1;
    }

    @Override
    public Request authenticate(Route route, Response response) {
        final List<String> WWWAuthenticate = response.headers().values("WWW-Authenticate");
        if (WWWAuthenticate.contains("NTLM")) {
            return response.request().newBuilder().header("Authorization", "NTLM " + ntlmMsg1).build();
        }
        String ntlmMsg3 = null;
        try {
            ntlmMsg3 = generateType3Msg(username, password, domain, workstation, WWWAuthenticate.get(0).substring(5));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.request().newBuilder().header("Authorization", "NTLM " + ntlmMsg3).build();
    }

    private String generateType1Msg(String domain, String workstation) {
        Type1Message msg = new Type1Message(Type1Message.getDefaultFlags(), domain, workstation);
        return Base64.encode(msg.toByteArray());
    }

    private String generateType3Msg(String username, String password, String domain, String workstation, String challenge) {
        try {
            Type2Message t2m = new Type2Message(Base64.decode(challenge));
            Type3Message t3m = new Type3Message(t2m, password, domain, username, workstation, 0);
            return Base64.encode(t3m.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
