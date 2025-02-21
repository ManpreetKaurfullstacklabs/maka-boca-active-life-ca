package io.reactivestax.activelife.constants;

public class SecurityConstants {
    public static final String SECRET = "TestSecrEtKeyF0rJwtHash1ng";
    public static final long EXPIRATION_TIME = 900_000; // 15 mins
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String LOGIN_URL = "/api/login";
}
