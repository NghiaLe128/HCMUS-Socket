package Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    public static int currentRoomID = 1;

    public int id;
    public String name;
    public String type;  // Add a type field to distinguish between room types
    public List<String> users;
    public List<ChatData> messages;
    public String creator;

    public Room(String name, String type, List<String> users, String creator) {
        this.id = currentRoomID++;
        this.name = name;
        this.type = type;
        this.users = users;
        this.creator = creator;
        this.messages = new ArrayList<>(); 
    }

    public static Room findRoom(List<Room> roomList, int id) {
        for (Room room : roomList)
            if (room.id == id)
                return room;
        return null;
    }

    public static Room findExistingRoom(List<Room> roomList, List<String> users) {
        for (Room room : roomList) {
            if ("private".equals(room.type) && room.users.containsAll(users)) {
                return room;
            }
        }
        return null;
    }

    public boolean hasSameUsers(List<String> usersToCheck) {
        return this.users.containsAll(usersToCheck) && usersToCheck.containsAll(this.users);
    }

    public static Room findPrivateRoom(List<Room> roomList, List<String> users) {
        for (Room room : roomList) {
            if ("private".equals(room.type) && room.users.containsAll(users) ) {
                return room;
            }
        }
        return null;
    }

    public static Room findRoom(List<Room> roomList, String name, String type, List<String> users) {
        for (Room room : roomList) {
            if (room.name.equals(name) && room.type.equals(type) && room.users.containsAll(users)) {
                return room;
            }
        }
        return null;
    }

    public String getOtherUser(String currentUser) {
        for (String user : users) {
            if (!user.equals(currentUser)) {
                return user;
            }
        }
        return null; 
    }

    public void addMessage(ChatData message) {
        messages.add(message);
    }

    public List<ChatData> getMessages() {
        return messages;
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

    public List<String> getMembers() {
        return users;
    }


}

	
	
	// public void saveMessagesToFile() {
	// 	String fileName = "messages_" + id + ".txt";
	// 	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
	// 		oos.writeObject(messages);
	// 	} catch (IOException e) {
	// 		e.printStackTrace();
	// 	}
	// }

	//  public void loadMessagesFromFile() {
    //     String fileName = "messages_" + id + ".txt";
    //     try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
    //         messages = (List<ChatData>) ois.readObject();
    //     } catch (IOException | ClassNotFoundException e) {
    //         e.printStackTrace();
    //     }
    // }
