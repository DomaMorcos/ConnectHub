package connecthub.Groups.Backend;

import connecthub.UserAccountManagement.Backend.User;
import connecthub.UserAccountManagement.Backend.UserDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupDatabase implements GroupPersistence {

    private static final String GROUP_FILEPATH = "Groups.JSON";
    public ArrayList<Group> groups = new ArrayList<>();

    private static GroupDatabase groupDatabase = null;

    private GroupDatabase() {
    }

    public static GroupDatabase getInstance() {
        if (groupDatabase == null) {
            groupDatabase = new GroupDatabase();
            groupDatabase.loadGroupsFromJsonFile();
        }
        return groupDatabase;
    }

    public void saveGroupsToJsonFile() {
        GroupDatabase groupDatabase = GroupDatabase.getInstance();
        JSONArray jsonArray = new JSONArray();

        for (Group group : groupDatabase.groups) {
            jsonArray.put(group.toJson());
        }

        try (FileWriter file = new FileWriter(GROUP_FILEPATH)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
        }
    }

    public ArrayList<Group> loadGroupsFromJsonFile() {
        GroupDatabase db = GroupDatabase.getInstance();
        db.groups.clear();

        Path pathFile = Paths.get(GROUP_FILEPATH);

        try {
            if (!Files.exists(pathFile)) {
                return db.groups;
            }

            String json = Files.readString(pathFile);
            if (json.trim().isEmpty()) {
                return db.groups;
            }

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                db.groups.add(Group.fromJson(jsonObject));
            }
        } catch (Exception e) {

        }

        return db.groups;
    }

    public ArrayList<Group> getAllGroups() {
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
            if (group.isCreator(userId)||group.isAdmin(userId)||group.isMember(userId)) {
                allPosts.addAll(group.getGroupPosts());
            }
        }
        return allPosts;
    }

    public ArrayList<Group> getGroupsForUser(String userId) {
        ArrayList<Group> userGroups = new ArrayList<>();

        for (Group group : groups) {
            if (group.isCreator(userId)||group.isAdmin(userId)||group.isMember(userId)) {
                userGroups.add(group);
            }
        }

        return userGroups;
    }

    public ArrayList<Group> getGroupSuggestionsForUser(String userId) {
        ArrayList<Group> groupSuggestions = new ArrayList<>();
        for (Group group : groups) {
            if (!group.getMembersId().contains(userId) && !group.getAdminsId().contains(userId) && !group.getCreator().equals(userId)) {
                groupSuggestions.add(group);
            }
        }

        groupSuggestions.removeIf(group -> group.getJoinRequests().contains(userId)); // Remove leftGroups from Suggestions
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
    public Group getGroupByName(String groupName) {
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }
    public List<Group> getGroupsByName(String name) {
        GroupDatabase groupDatabase = GroupDatabase.getInstance();
        List<Group> matchedGroups = new ArrayList<>();
        String lowerCaseName = name.toLowerCase(); // Normalize the search term to lowercase
        for (Group group : groupDatabase.getAllGroups()) {
            if (group.getName().toLowerCase().contains(lowerCaseName)) { // Normalize usernames and check
                matchedGroups.add(group);
            }
        }
        return matchedGroups;
    }

    public void addGroup(Group group) {
        boolean groupExists = groups.stream().anyMatch(g -> g.getGroupId().equals(group.getGroupId()));
        if (!groupExists) {
            groups.add(group);
            saveGroupsToJsonFile(); // Save only when a new group is added
        }
    }

    public void removeGroup(String groupId) {
        groups.removeIf(group -> group.getGroupId().equals(groupId));
        saveGroupsToJsonFile();
    }

    public ArrayList<GroupPost> getAllCommentsForGroupPost(GroupPost post) {
        //load
        Group group = new Group();
        if (group.getGroupPosts() != null) {
            for (GroupPost groupPost : group.getGroupPosts()) {
                //if post is found
                if (Objects.equals(groupPost.getPostId(), post.getPostId())) {
                    return groupPost.getGroupPostComments();
                }
            }
        }
        //if not found
        return new ArrayList<>();
    }
}