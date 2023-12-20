
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatData implements Serializable {
	private static final long serialVersionUID = 1L;
	public String whoSend;
	public String type;
	public String content;
	public boolean isRead = false;

	public ChatData(String whoSend, String type, String content) {
		this.whoSend = whoSend;
		this.type = type;
		this.content = content;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean read) {
		isRead = read;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		// Custom serialization logic
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// Custom deserialization logic
		in.defaultReadObject();
	}

	public static ChatData parse(String message) {
		String pattern = "(\\w+):\\s(.+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(message);

		if (m.find()) {
			String whoSend = m.group(1);
			String content = m.group(2);
			return new ChatData(whoSend, "text", content);
		} else {
			// Handle invalid message format
			return null;
		}
	}
}