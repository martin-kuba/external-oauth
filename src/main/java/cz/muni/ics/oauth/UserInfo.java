package cz.muni.ics.oauth;

/**
 * Info about user.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
public class UserInfo {

    private final String provider;
    private final String id;
    private final String email;
    private final String givenName;
    private final String surname;
    private final String fullname;
    private final String pictureURL;

    public UserInfo(String provider, String id, String email, String givenName, String surname, String fullname, String pictureURL) {
        this.provider = provider;
        this.id = id;
        this.email = email;
        this.givenName = givenName;
        this.surname = surname;
        this.fullname = fullname;
        this.pictureURL = pictureURL;
    }

    public String getProvider() {
        return provider;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "provider='" + provider + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surename='" + surname + '\'' +
                ", fullname='" + fullname + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                '}';
    }
}
