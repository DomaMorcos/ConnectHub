package connecthub.UserAccountManagement.Backend;

import connecthub.FriendManagement.Backend.FriendManager;
import connecthub.ProfileManagement.Backend.ProfileDatabase;
import connecthub.ProfileManagement.Backend.UserProfile;
import javax.swing.*;
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
        String generatedId = generateID();
        User newUser = new User(generatedId, email, username, hash, dateOfBirth, "offline");
        userDB.users.add(newUser);
        userDB.saveUsersToJsonFile();
        // Initialize friends for the new user
        FriendManager.getInstance().initializeFriends(generatedId, null);
        // Initialize and save the profile for the new user with default values
        ProfileDatabase profileDB = ProfileDatabase.getInstance();
        UserProfile profile = new UserProfile(
                generatedId,
                new ImageIcon(), // Default empty profile photo
                new ImageIcon(), // Default empty cover photo
                "Default bio",
                new ArrayList<>()
        );
        profileDB.updateProfile(profile);
        return true;
    }
}
