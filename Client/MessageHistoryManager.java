import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MessageHistoryManager {
    // Map to store message history for each room
    private static Map<Integer, List<ChatData>> messageHistoryMap = new HashMap<>();

    // Save message for a specific room
    public static void saveMessage(int roomID, ChatData message) {
        List<ChatData> roomMessages = messageHistoryMap.getOrDefault(roomID, new ArrayList<>());
        roomMessages.add(message);
        messageHistoryMap.put(roomID, roomMessages);
    }

    // Load message history for a specific room
    public static List<ChatData> loadMessages(int roomID) {
        return messageHistoryMap.getOrDefault(roomID, new ArrayList<>());
    }

    // Other methods as needed
}
