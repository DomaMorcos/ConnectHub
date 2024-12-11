package connecthub.UserAccountManagement.Backend;


import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;

import java.util.ArrayList;
import static connecthub.UserAccountManagement.Backend.HashPassword.hashPassword;
import static connecthub.UserAccountManagement.Backend.Validation.isEmailValid;
import static connecthub.UserAccountManagement.Backend.Validation.isUsernameValid;

public class CreateUser {
    UserDatabase userDB = UserDatabase.getInstance();

    public synchronized String generateID() {
        if (userDB.users == null) {
            userDB.users = new ArrayList<>();
        }
        return Integer.toString(userDB.users.size() + 1);
    }

    public boolean signup(String email, String username, String password, String dateOfBirth) {
        if (!isEmailValid(email) || !isUsernameValid(username) || userDB.contains(email)) {
            return false;
        }
        String hash = hashPassword(password);
        String generatedID = generateID();
        User newUser = new User.Builder().userId(generatedID).email(email).username(username).password(hash).dateOfBirth(dateOfBirth).status("offline").build();
        UserProfile userProfile = new UserProfile(generatedID, "/Images/DefaultProfilePhoto.jpg","/Images/DefaultCoverPhoto.png","Click on Edit to edit Bio",new ArrayList<>(), new ArrayList<>());
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance();
        profileDatabase.updateProfile(userProfile);

        userDB.users.add(newUser);
        userDB.saveUsersToJsonFile();

        return true;
    }
}