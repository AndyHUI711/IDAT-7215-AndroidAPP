package com.example.imagerecognitiondemo.model;

/**
 * 获取鉴权认证Token响应实体
 */
public class GetToken {

    /**
     * refresh_token : 25.b0730ebe1c67e0dea5db9c0e092a8d17.315360000.1982833827.282335-28229257
     * expires_in:2592000
     * session_key:9mzdA5/F2dgaSqTypEZtf//OyCizfbDZb54j3nkSZbJty3sg9zG2MCdEh9ASthJO8VnpTt6hPF5rrDrEj4zdpXxEbCTNoQ==
     * access_toke:24.857af6886af76bb47cd43127d4937844.2592000.1670065827.282335-28229257
     * session_secret:e2c6ac97bfa1bdd8bc5fc933c7633247
     */
    private String refresh_token;
    private long expires_in;
    private String scope;
    private String session_key;
    private String access_token;
    private String session_secret;

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getSession_secret() {
        return session_secret;
    }

    public void setSession_secret(String session_secret) {
        this.session_secret = session_secret;
    }
}


