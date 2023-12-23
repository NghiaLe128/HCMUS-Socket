package Server;

import java.io.Serializable;

public class ChatData implements Serializable {
    private static final long serialVersionUID = 1L;

    public String whoSend;
    public String type;
    public String content;

    public ChatData(String whoSend, String type, String content) {
        this.whoSend = whoSend;
        this.type = type;
        this.content = content;
    }


}

