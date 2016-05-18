package whispeerer.whispeerer;

/**
 * Created by Dominic on 12/05/2016.
 */
public enum ServerInfo {
    BASE_URL("https://www.transfer4.me"),
    PORT("8081"),
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
