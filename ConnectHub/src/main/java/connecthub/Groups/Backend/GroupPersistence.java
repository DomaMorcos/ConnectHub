package connecthub.Groups.Backend;

import java.util.ArrayList;

public interface GroupPersistence {
    void saveGroupsToJsonFile();
    ArrayList<Group> loadGroupsFromJsonFile();
}