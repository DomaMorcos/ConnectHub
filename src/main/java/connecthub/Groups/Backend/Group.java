package connecthub.Groups.Backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Group {
    private String name;
    private String description;
    private String photo;
    private String groupId;
    private String creator;
    private ArrayList<String> adminsId;
    private ArrayList<String> membersId;
    private ArrayList<GroupPost> groupPosts;
    private ArrayList<String> joinRequests;

    public Group(String name, String description, String photo, String creator) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.creator = creator;
        this.groupId = name + "_" + creator;
        this.adminsId = new ArrayList<>();
        this.membersId = new ArrayList<>();
        this.groupPosts = new ArrayList<>();
        this.joinRequests = new ArrayList<>();
        this.adminsId.add(creator);
        this.membersId.add(creator);
    }

    public void promoteToAdmin(String groupId, String memberId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can promote members.");
        }
        if (!group.membersId.contains(memberId)) {
            throw new IllegalArgumentException("User is not a member of the group.");
        }
        if (!group.adminsId.contains(memberId)) {
            group.adminsId.add(memberId);
        }
        saveGroupChanges(group);
    }

    public void leaveGroup(String groupId, String memberId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.membersId.contains(memberId)) {
            throw new IllegalArgumentException("User is not a member of the group.");
        }
        if (memberId.equals(group.creator)) {
            throw new IllegalArgumentException("The creator cannot leave the group.");
        }
        group.membersId.remove(memberId);
        group.adminsId.remove(memberId);
        saveGroupChanges(group);
    }

    public void demoteToMember(String groupId, String adminId, String adminToDemote) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can demote admins.");
        }
        if (adminToDemote.equals(group.creator)) {
            throw new IllegalArgumentException("The creator cannot be demoted.");
        }
        group.adminsId.remove(adminToDemote);
        if (!group.membersId.contains(adminToDemote)) {
            group.membersId.add(adminToDemote);
        }
        saveGroupChanges(group);
    }

    public void removeMember(String groupId, String memberId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can remove members.");
        }
        if (!group.membersId.contains(memberId)) {
            throw new IllegalArgumentException("User is not a member of the group.");
        }
        group.membersId.remove(memberId);
        group.adminsId.remove(memberId);
        saveGroupChanges(group);
    }

    public void addPost(String groupId, GroupPost post) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        group.groupPosts.add(post);
        saveGroupChanges(group);
    }

    public void removePost(String groupId, String postId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only admins or the creator can remove posts.");
        }
        group.groupPosts.removeIf(post -> post.getPostId().equals(postId));
        saveGroupChanges(group);
    }

    public void requestToJoinGroup(String groupId, String userId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.joinRequests.contains(userId)) {
            group.joinRequests.add(userId);
        }
        saveGroupChanges(group);
    }

    public void approveJoinRequest(String groupId, String userId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can approve requests.");
        }
        if (group.joinRequests.contains(userId)) {
            group.joinRequests.remove(userId);
            if (!group.membersId.contains(userId)) {
                group.membersId.add(userId);
            }
        } else {
            throw new IllegalArgumentException("No such join request found.");
        }
        saveGroupChanges(group);
    }

    public void rejectJoinRequest(String groupId, String userId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can reject requests.");
        }
        if (group.joinRequests.contains(userId)) {
            group.joinRequests.remove(userId);
        } else {
            throw new IllegalArgumentException("No such join request found.");
        }
        saveGroupChanges(group);
    }

    public void removeGroup(String groupId) {
        GroupDatabase groupDatabase = GroupDatabase.getInstance();
        groupDatabase.groups.removeIf(group -> group.getGroupId().equals(groupId));
        groupDatabase.saveGroupsToJsonFile();
    }

    public ArrayList<String> getJoinRequests(String groupId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        return new ArrayList<>(group.joinRequests);
    }

    private void saveGroupChanges(Group group) {
        GroupDatabase groupDatabase = GroupDatabase.getInstance();
        groupDatabase.saveGroupsToJsonFile();
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name != null ? name : "");
        jsonObject.put("description", description != null ? description : "");
        jsonObject.put("photo", photo != null ? photo : "");
        jsonObject.put("groupId", groupId != null ? groupId : "");
        jsonObject.put("creator", creator != null ? creator : "");
        jsonObject.put("adminsId", new JSONArray(adminsId != null ? adminsId : new ArrayList<>()));
        jsonObject.put("membersId", new JSONArray(membersId != null ? membersId : new ArrayList<>()));

        JSONArray postsArray = new JSONArray();
        if (groupPosts != null) {
            for (GroupPost post : groupPosts) {
                postsArray.put(post.toJson());
            }
        }
        jsonObject.put("groupPosts", postsArray);

        jsonObject.put("joinRequests", new JSONArray(joinRequests != null ? joinRequests : new ArrayList<>()));

        return jsonObject;
    }

    public static Group fromJson(JSONObject jsonObject) {
        String name = jsonObject.optString("name", "");
        String description = jsonObject.optString("description", "");
        String photo = jsonObject.optString("photo", "");
        String creator = jsonObject.optString("creator", "");

        Group group = new Group(name, description, photo, creator);
        group.groupId = jsonObject.optString("groupId", "");

        JSONArray adminsArray = jsonObject.optJSONArray("adminsId");
        if (adminsArray != null) {
            for (int i = 0; i < adminsArray.length(); i++) {
                group.adminsId.add(adminsArray.optString(i, ""));
            }
        }

        JSONArray membersArray = jsonObject.optJSONArray("membersId");
        if (membersArray != null) {
            for (int i = 0; i < membersArray.length(); i++) {
                group.membersId.add(membersArray.optString(i, ""));
            }
        }

        JSONArray postsArray = jsonObject.optJSONArray("groupPosts");
        if (postsArray != null) {
            for (int i = 0; i < postsArray.length(); i++) {
                group.groupPosts.add(GroupPost.fromJson(postsArray.getJSONObject(i)));
            }
        }

        JSONArray joinRequestsArray = jsonObject.optJSONArray("joinRequests");
        if (joinRequestsArray != null) {
            for (int i = 0; i < joinRequestsArray.length(); i++) {
                group.joinRequests.add(joinRequestsArray.optString(i, ""));
            }
        }

        return group;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getCreator() {
        return creator;
    }

    public ArrayList<String> getAdminsId() {
        return new ArrayList<>(adminsId != null ? adminsId : new ArrayList<>());
    }

    public ArrayList<String> getMembersId() {
        return new ArrayList<>(membersId != null ? membersId : new ArrayList<>());
    }

    public ArrayList<GroupPost> getGroupPosts() {
        return new ArrayList<>(groupPosts != null ? groupPosts : new ArrayList<>());
    }

    public boolean isAdmin(String userID){
        return adminsId.contains(userID);
    }
    public boolean isCreator(String userId){
        return userId.equals(creator);
    }
}