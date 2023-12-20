package Server;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;
import java.util.ArrayList;

public class Client {
    public String userName;
    public String password;
    public int port;
    public Socket socket;
    public BufferedReader receiver;
    public BufferedWriter sender;
    public List<Integer> createdRoomIds; 
    

    public Client(String userName, String password, int port, Socket socket, BufferedReader receiver, BufferedWriter sender) {
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.socket = socket;
        this.receiver = receiver;
        this.sender = sender;
        this.createdRoomIds = new ArrayList<>();
    }

    public Client() {
        this.createdRoomIds = new ArrayList<>();
    }

	public static Client findClient(List<Client> clientList, String userName) {
		for (Client client : clientList) {
			if (client.userName.equals(userName))
				return client;
		}
		return null;
	}

}