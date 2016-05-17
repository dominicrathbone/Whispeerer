package whispeerer.whispeerer;

/**
 * Created by Dominic on 12/05/2016.
 */
public enum ServerInfo {
    BASE_URL("http://192.168.1.101"),
    PORT("8080"),
    USER("/user"),
    USERS("/users/");

    private final String uri;

    ServerInfo(String uri) {
        this.uri  = uri;
    }

    public String getUri() {
        return this.uri;
    }

}
