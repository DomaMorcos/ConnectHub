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
        // Fetch the group from the database
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }

        // Check if the adminId has permission to promote
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the group creator or an admin can promote members to admin.");
        }

        // Ensure the memberId is a member of the group
        if (!group.membersId.contains(memberId)) {
            throw new IllegalArgumentException("User with ID '" + memberId + "' is not a member of the group.");
        }

        // Promote the member to admin if they are not already an admin
        if (group.adminsId.contains(memberId)) {
            throw new IllegalStateException("User with ID '" + memberId + "' is already an admin.");
        }

        group.adminsId.add(memberId);
        saveGroupChanges(group);
    }

    public void leaveGroup(String groupId, String memberId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.membersId.contains(memberId)) {
            throw new IllegalArgumentException("User with ID '" + memberId + "' is not a member of the group.");
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
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the group creator or an admin can demote admins.");
        }
        if (adminToDemote.equals(group.creator)) {
            throw new IllegalArgumentException("The creator cannot be demoted.");
        }
        if (!group.adminsId.contains(adminToDemote)) {
            throw new IllegalArgumentException("User with ID '" + adminToDemote + "' is not an admin.");
        }
        group.adminsId.remove(adminToDemote);
        if (!group.membersId.contains(adminToDemote)) {
            group.membersId.add(adminToDemote);
        }
        saveGroupChanges(group);
    }

    public void removeMember(String groupId, String memberId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the group creator or an admin can remove members.");
        }
        if (!group.membersId.contains(memberId)) {
            throw new IllegalArgumentException("User with ID '" + memberId + "' is not a member of the group.");
        }
        if (memberId.equals(group.creator)) {
            throw new IllegalArgumentException("The creator cannot be removed from the group.");
        }
        group.membersId.remove(memberId);
        group.adminsId.remove(memberId);
        saveGroupChanges(group);
    }
    public void addPost(String groupId, GroupPost post) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        group.groupPosts.add(post);
        saveGroupChanges(group);
    }

    public void removePost(String groupId, String postId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can remove posts.");
        }
        group.groupPosts.removeIf(post -> post.getPostId().equals(postId));
        saveGroupChanges(group);
    }

    public void editPost(String groupId, String postId, String authorId, String newContent) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        GroupPost post = group.getGroupPosts().stream()
                .filter(p -> p.getPostId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Post with ID '" + postId + "' not found."));
        if (!post.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("Only the author can edit this post.");
        }
        post.setContent(newContent);
        saveGroupChanges(group);
    }

    public void requestToJoinGroup(String groupId, String userId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.joinRequests.contains(userId)) {
            group.joinRequests.add(userId);
        }
        saveGroupChanges(group);
    }

    public void approveJoinRequest(String groupId, String userId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can approve join requests.");
        }
        if (group.joinRequests.contains(userId)) {
            group.joinRequests.remove(userId);
            if (!group.membersId.contains(userId)) {
                group.membersId.add(userId);
            }
        } else {
            throw new IllegalArgumentException("No join request from user with ID '" + userId + "' found.");
        }
        saveGroupChanges(group);
    }

    public void addMemberToGroup(String groupId, String memberId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.membersId.contains(memberId)) {
            group.membersId.add(memberId);
        }
        saveGroupChanges(group);
    }

    public void rejectJoinRequest(String groupId, String userId, String adminId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID '" + groupId + "' does not exist.");
        }
        if (!group.adminsId.contains(adminId) && !adminId.equals(group.creator)) {
            throw new IllegalArgumentException("Only the creator or admins can reject join requests.");
        }
        if (group.joinRequests.contains(userId)) {
            group.joinRequests.remove(userId);
        } else {
            throw new IllegalArgumentException("No join request from user with ID '" + userId + "' found.");
        }
        saveGroupChanges(group);
    }


    public ArrayList<String> getJoinRequests(String groupId) {
        Group group = GroupDatabase.getInstance().getGroupById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group " + groupId + " does not exist.");
        }
        return new ArrayList<>(group.joinRequests);
    }

    public void saveGroupChanges(Group group) {
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
                try {
                    group.groupPosts.add(GroupPost.fromJson(postsArray.getJSONObject(i)));
                } catch (Exception e) {
                    System.err.println("Skipping malformed post: " + e.getMessage());
                }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setAdminsId(ArrayList<String> adminsId) {
        this.adminsId = adminsId;
    }

    public void setMembersId(ArrayList<String> membersId) {
        this.membersId = membersId;
    }

    public void setGroupPosts(ArrayList<GroupPost> groupPosts) {
        this.groupPosts = groupPosts;
    }

    public ArrayList<String> getJoinRequests() {
        return joinRequests;
    }

    public void setJoinRequests(ArrayList<String> joinRequests) {
        this.joinRequests = joinRequests;
    }
}