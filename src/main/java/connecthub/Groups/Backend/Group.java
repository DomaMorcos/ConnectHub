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

    public void promoteToAdmin(String memberId, String adminId) {
        if (!adminsId.contains(adminId) && !adminId.equals(creator)) {
            throw new IllegalArgumentException("Only the creator or admins can promote members.");
        }
        if (!membersId.contains(memberId)) {
            throw new IllegalArgumentException("User is not a member of the group.");
        }
        if (!adminsId.contains(memberId)) {
            adminsId.add(memberId);  // Add member to admins only if not already in admins list
        }
        saveGroupChanges();
    }

    public void demoteToMember(String adminId, String adminToDemote) {
        if (!adminsId.contains(adminId) && !adminId.equals(creator)) {
            throw new IllegalArgumentException("Only the creator or admins can demote admins.");
        }
        if (adminId.equals(adminToDemote)) {
            throw new IllegalArgumentException("Creator cannot be demoted.");
        }
        adminsId.remove(adminToDemote);
        if (!membersId.contains(adminToDemote)) {
            membersId.add(adminToDemote);  // Add demoted admin back to members if not already there
        }
        saveGroupChanges();
    }

    public void leaveGroup(String memberId) {
        if (!membersId.contains(memberId)) {
            throw new IllegalArgumentException("User is not a member of the group.");
        }
        membersId.remove(memberId);
        adminsId.remove(memberId);
        saveGroupChanges();
    }

    public void removeMember(String memberId, String adminId) {
        if (!adminsId.contains(adminId) && !adminId.equals(creator)) {
            throw new IllegalArgumentException("Only the creator or admins can remove members.");
        }
        if (!membersId.contains(memberId)) {
            throw new IllegalArgumentException("User is not a member of the group.");
        }
        membersId.remove(memberId);
        adminsId.remove(memberId);
        saveGroupChanges();
    }

    public void addPost(GroupPost post) {
        groupPosts.add(post);
        saveGroupChanges();
    }

    public void removePost(String postId, String adminId) {
        if (!adminsId.contains(adminId) && !adminId.equals(creator)) {
            throw new IllegalArgumentException("Only admins or creator can remove posts.");
        }
        groupPosts.removeIf(post -> post.getPostId().equals(postId));
        saveGroupChanges();
    }

    public void requestToJoinGroup(String userId) {
        if (!joinRequests.contains(userId)) {
            joinRequests.add(userId);  // Add user to join requests list only if not already there
        }
        saveGroupChanges();
    }

    public void approveJoinRequest(String userId, String adminId) {
        if (!adminsId.contains(adminId) && !adminId.equals(creator)) {
            throw new IllegalArgumentException("Only the creator or admins can approve requests.");
        }
        if (joinRequests.contains(userId)) {
            joinRequests.remove(userId);
            if (!membersId.contains(userId)) {
                membersId.add(userId);  // Add user to members only if not already there
            }
        } else {
            throw new IllegalArgumentException("No such join request found.");
        }
        saveGroupChanges();
    }

    public void rejectJoinRequest(String userId, String adminId) {
        if (!adminsId.contains(adminId) && !adminId.equals(creator)) {
            throw new IllegalArgumentException("Only the creator or admins can reject requests.");
        }
        if (joinRequests.contains(userId)) {
            joinRequests.remove(userId);
        } else {
            throw new IllegalArgumentException("No such join request found.");
        }
        saveGroupChanges();
    }

    public ArrayList<String> getJoinRequests() {
        return new ArrayList<>(joinRequests);
    }

    private void saveGroupChanges() {
        GroupManager groupManager = new GroupManager();
        groupManager.saveGroupsToJsonFile();
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("description", description);
        jsonObject.put("photo", photo);
        jsonObject.put("groupId", groupId);
        jsonObject.put("creator", creator);
        jsonObject.put("adminsId", new JSONArray(adminsId));
        jsonObject.put("membersId", new JSONArray(membersId));
        JSONArray postsArray = new JSONArray();
        for (GroupPost post : groupPosts) {
            postsArray.put(post.toJson());
        }
        jsonObject.put("groupPosts", postsArray);
        jsonObject.put("joinRequests", new JSONArray(joinRequests));
        return jsonObject;
    }

    public static Group fromJson(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        String description = jsonObject.getString("description");
        String photo = jsonObject.getString("photo");
        String creator = jsonObject.getString("creator");
        Group group = new Group(name, description, photo, creator);
        group.groupId = jsonObject.getString("groupId");
        JSONArray adminsArray = jsonObject.getJSONArray("adminsId");
        for (int i = 0; i < adminsArray.length(); i++) {
            group.adminsId.add(adminsArray.getString(i));
        }
        JSONArray membersArray = jsonObject.getJSONArray("membersId");
        for (int i = 0; i < membersArray.length(); i++) {
            group.membersId.add(membersArray.getString(i));
        }
        JSONArray postsArray = jsonObject.getJSONArray("groupPosts");
        for (int i = 0; i < postsArray.length(); i++) {
            group.groupPosts.add(GroupPost.fromJson(postsArray.getJSONObject(i)));
        }
        JSONArray joinRequestsArray = jsonObject.getJSONArray("joinRequests");
        for (int i = 0; i < joinRequestsArray.length(); i++) {
            group.joinRequests.add(joinRequestsArray.getString(i));
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
        return new ArrayList<>(adminsId);
    }

    public ArrayList<String> getMembersId() {
        return new ArrayList<>(membersId);
    }

    public ArrayList<GroupPost> getGroupPosts() {
        return new ArrayList<>(groupPosts);
    }
}