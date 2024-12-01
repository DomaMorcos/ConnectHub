package connecthub.UserAccountManagement.Backend;


import java.util.UUID;

import static connecthub.UserAccountManagement.Backend.EmailValidation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;

public class CreateUser {
    public boolean signup(String email, String username, String password, String dateOfBirth) {
        if (!isEmailValid(email) || users.containsKey(email)){
            return false;
        }
        if(username.isEmpty()||password.isEmpty()||dateOfBirth.isEmpty()){
            return false;
        }
        String hash = hashPassword(password);
        User newUser = new User(UUID.randomUUID().toString(),email,username,hash,dateOfBirth,"offline");
        users.put(email, newUser);
        saveUsers();
        return true;
    }

}
