package connecthub.Groups.Backend;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GroupDatabase {

    private static final String GROUP_FILEPATH = "Groups.JSON";
    public ArrayList<Group> groups;

    private static GroupDatabase instance;

    private GroupDatabase() {
        groups = loadGroupsFromJsonFile();
    }

    public static GroupDatabase getInstance() {
        if (instance == null) {
            instance = new GroupDatabase();
        }
        return instance;
    }

    public void saveGroupsToJsonFile() {
        JSONArray groupsArray = new JSONArray();
        for (Group group : groups) {
            groupsArray.put(group.toJson());
        }

        try (FileWriter file = new FileWriter(GROUP_FILEPATH)) {
            file.write(groupsArray.toString(4));
            System.out.println("Groups saved to JSON file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Group> loadGroupsFromJsonFile() {
        groups = new ArrayList<>();
        File file = new File(GROUP_FILEPATH);
        if (!file.exists()) {
            System.out.println("Groups JSON file not found. Returning empty group list.");
            return groups;
        }

        try {
            String json = new String(Files.readAllBytes(Paths.get(GROUP_FILEPATH)));
            JSONArray groupsArray = new JSONArray(json);
            for (int i = 0; i < groupsArray.length(); i++) {
                groups.add(Group.fromJson(groupsArray.getJSONObject(i)));
            }
            System.out.println("Groups loaded from JSON file.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public ArrayList<Group> getGroups() {
        return new ArrayList<>(groups);
    }

    public ArrayList<GroupPost> getAllPostsForGroup(String groupId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        return group.getGroupPosts();
    }

    public ArrayList<GroupPost> getAllPostsForAllGroupsForUser(String userId) {
        ArrayList<GroupPost> allPosts = new ArrayList<>();
        for (Group group : groups) {
            if (group.getMembersId().contains(userId) || group.getAdminsId().contains(userId)) {
                allPosts.addAll(group.getGroupPosts());
            }
        }
        return allPosts;
    }

    public ArrayList<Group> getGroupsForUser(String userId) {
        ArrayList<Group> userGroups = new ArrayList<>();

        for (Group group : groups) {
            if (group.getMembersId().contains(userId) || group.getAdminsId().contains(userId)) {
                userGroups.add(group);
            }
        }

        return userGroups;
    }

    public ArrayList<Group> getGroupSuggestionsForUser(String userId) {
        ArrayList<Group> groupSuggestions = new ArrayList<>();
        for (Group group : groups) {
            if (!group.getMembersId().contains(userId) && !group.getAdminsId().contains(userId)) {
                groupSuggestions.add(group);
            }
        }

        return groupSuggestions;
    }

    public Group getGroupById(String groupId) {
        for (Group group : groups) {
            if (group.getGroupId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    public void addGroup(Group group) {
        if (getGroupById(group.getGroupId()) == null) {
            groups.add(group);
            saveGroupsToJsonFile();
        } else {
            System.out.println("Group already exists.");
        }
    }

    // Updated methods to reflect changes made in the Group class:

    public void promoteToAdmin(String groupId, String memberId, String adminId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.promoteToAdmin(groupId, memberId, adminId);
    }

    public void leaveGroup(String groupId, String memberId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.leaveGroup(groupId, memberId);
    }

    public void demoteToMember(String groupId, String adminId, String adminToDemote) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.demoteToMember(groupId, adminId, adminToDemote);
    }

    public void removeMember(String groupId, String memberId, String adminId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.removeMember(groupId, memberId, adminId);
    }

    public void addPost(String groupId, GroupPost post) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.addPost(groupId, post);
    }

    public void removePost(String groupId, String postId, String adminId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.removePost(groupId, postId, adminId);
    }

    public void requestToJoinGroup(String groupId, String userId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.requestToJoinGroup(groupId, userId);
    }

    public void approveJoinRequest(String groupId, String userId, String adminId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.approveJoinRequest(groupId, userId, adminId);
    }

    public void rejectJoinRequest(String groupId, String userId, String adminId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        group.rejectJoinRequest(groupId, userId, adminId);
    }
}