import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    public int id;
    public String name;
    public String type;
    public List<String> users;
    public List<ChatData> messages;

    public Room(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Room(int id, String name, String type, List<String> users) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.users = users;
        this.messages = new ArrayList<>();
    }

    public void addMessage(ChatData message) {
        messages.add(message);
    }

    public static Room findRoom(List<Room> roomList, int id) {
        for (Room room : roomList)
            if (room.id == id)
                return room;
        return null;
    }

    public static Room findPrivateRoom(List<Room> rooms, String user) {
        for (Room room : rooms) {
            if (room.type.equals("private") && room.users.contains(user)) {
                return room;
            }
        }
        return null;
    }

    public static Room findGroup(List<Room> roomList, String groupName) {
        for (Room room : roomList) {
            if ("group".equals(room.type) && room.name.equals(groupName))
                return room;
        }
        return null;
    }

    public List<String> getMessageContents() {
        List<String> messageContents = new ArrayList<>();
        for (ChatData message : messages) {
            messageContents.add(message.content);
        }
        return messageContents;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<String> getUsers() {
        return users;
    }

    public List<ChatData> getMessages() {
        return messages;
    }

    public List<ChatData> getMessagesByUser(String username) {
    return messages.stream()
            .filter(message -> message.getSender().equals(username))
            .collect(Collectors.toList());
}

}
