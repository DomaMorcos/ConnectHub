package connecthub.UserAccountManagement.Backend;


import java.io.Serializable;
public class User implements Serializable {

    private String userId;
    private String email;
    private String username;
    private String password;
    private String dateOfBirth;
    private String status;

    public User(String userId, String email, String username, String password, String dateOfBirth, String status) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

}



