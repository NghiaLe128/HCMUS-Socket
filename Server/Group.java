package Server; 

import java.io.*;
import java.util.List;

public class Group implements Serializable {
    private int groupId;
    private String groupName;
    private String groupType;
    private List<String> groupMembers;


    public Group(int groupId, String groupName, String groupType, List<String> groupMembers) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupType = groupType;
        this.groupMembers = groupMembers;
    }

    // Getter methods
    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public List<String> getGroupMembers() {
        return groupMembers;
    }

    // Setter methods
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public void setGroupMembers(List<String> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public void saveGroup(Group group) {
        try {
            File groupsFolder = new File("groups");
            if (!groupsFolder.exists()) {
                groupsFolder.mkdir(); // Tạo thư mục nếu chưa tồn tại
            }
    
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("groups/" + group.getGroupId() + ".dat"))) {
                oos.writeObject(group);
                System.out.println("Group saved successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public Group loadGroup(int groupId) {
        Group group = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("groups/" + groupId + ".dat"))) {
            group = (Group) ois.readObject();
            System.out.println("Group loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return group;
    }
}
