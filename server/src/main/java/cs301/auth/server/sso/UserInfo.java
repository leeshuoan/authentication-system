package cs301.auth.server.sso;

public class UserInfo {
    private String sub;
    private String email;
    private String given_name;
    private String family_name;
    private String name;
    private String birthdate;
    private String gender;
    private String phone_number;

    public String getSub() {
        return sub;
    }


    public void setSub(String sub) {
        this.sub = sub;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getGiven_name() {
        return given_name;
    }


    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }


    public String getFamily_name() {
        return family_name;
    }


    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getBirthdate() {
        return birthdate;
    }


    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getPhone_number() {
        return phone_number;
    }


    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }


    @Override
    public String toString() {
        return "UserInfo [sub=" + sub + ", email=" + email + ", given_name=" + given_name + ", family_name="
                + family_name + ", name=" + name + ", birthdate=" + birthdate + ", gender=" + gender + ", phone_number="
                + phone_number + "]";
    }
}
