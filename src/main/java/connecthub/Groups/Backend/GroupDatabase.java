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
    private ArrayList<Group> groups;

    public GroupDatabase() {
        groups = loadGroupsFromJsonFile();
    }

    public ArrayList<Group> getGroups() {
        return new ArrayList<>(groups);
    }

    public ArrayList<String> getJoinRequestsForGroup(String groupId) {
        Group group = getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found.");
        }
        return group.getJoinRequests();
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
        groups = new ArrayList<>(); // Reset list to avoid duplicates
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

    private Group getGroupById(String groupId) {
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
}