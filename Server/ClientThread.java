package Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {

	Client thisClient;
    DatabaseHandler databaseHandler = new DatabaseHandler();

	public ClientThread(Socket clientSocket) {
		try {
			thisClient = new Client();
			thisClient.socket = clientSocket;
			OutputStream os = clientSocket.getOutputStream();
			thisClient.sender = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
			InputStream is = clientSocket.getInputStream();
			thisClient.receiver = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			thisClient.port = clientSocket.getPort();
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				String header = thisClient.receiver.readLine();
				if (header == null)
					throw new IOException();

				System.out.println("Header: " + header);
				switch (header) {

				case "new login": {

					handleNewLogin();
					break;
				}

				case "new register": {
					handleNewRegister();
					break;
				}

				case "logout": {
					// Xử lý logic khi client logout
					String username = thisClient.userName;
				
					// Loại bỏ client khỏi danh sách các client đã kết nối
					Main.socketControl.connectedClient.remove(thisClient);
				
					// Cập nhật bảng hiển thị client trên giao diện chính
					Main.mainScreen.updateClientTable();
					
					// Thông báo cho các client khác biết rằng người dùng đã thoát
					for (Client client : Main.socketControl.connectedClient) {
						if (!client.userName.equals(username)) {
							client.sender.write("user quit");
							client.sender.newLine();
							client.sender.write(username);
							client.sender.newLine();
							client.sender.flush();
						}
					}
				
					// Đóng socket liên quan đến client đã logout
					thisClient.userName= null;
					break;
				}
				
				case "get name": {
					thisClient.sender.write(Main.socketControl.serverName);
					thisClient.sender.newLine();
					thisClient.sender.flush();
					break;
				}

				case "get connected count": {
					thisClient.sender.write("" + Main.socketControl.connectedClient.size());
					thisClient.sender.newLine();
					thisClient.sender.flush();
					break;
				}
				case "request create room": {
					handleCreateRequestRoom();
					break;
				}
				
				case "load messages": {
					handleLoadMessages();
					break;
				}
				case "text to room": {
					int roomID = Integer.parseInt(thisClient.receiver.readLine());
					String receiver;
					String content = "";
					char c;
					do {
						c = (char) thisClient.receiver.read();
						if (c != '\0')
							content += c;
					} while (c != '\0');

					Room room = Room.findRoom(Main.socketControl.allRooms, roomID);
					for (String user : room.users) {
						System.out.println("Send text from " + thisClient.userName + " to " + user);
						receiver = user;
						Client currentClient = Client.findClient(Main.socketControl.connectedClient, user);
						if (currentClient != null) {
							currentClient.sender.write("text from user to room");
							currentClient.sender.newLine();
							currentClient.sender.write(thisClient.userName);
							currentClient.sender.newLine();
							currentClient.sender.write("" + roomID);
							currentClient.sender.newLine();
							currentClient.sender.write(content);
							currentClient.sender.write('\0');
							currentClient.sender.flush();
						}
					}

					saveMessage(roomID,thisClient.userName,content);
				
					break;
				}
                case "get users with accounts": {
					handleGetUsersWithAccounts();
					break;
				}
			

				}
			}

		} catch (IOException e) {
			if (!Main.socketControl.s.isClosed() && thisClient.userName != null) {

				try {
					for (Client client : Main.socketControl.connectedClient) {
						if (!client.userName.equals(thisClient.userName)) {
							client.sender.write("user quit");
							client.sender.newLine();
							client.sender.write(thisClient.userName);
							client.sender.newLine();
							client.sender.flush();
						}
					}
					for (Room room : Main.socketControl.allRooms)
						room.users.remove(thisClient.userName);

					thisClient.socket.close();

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Main.socketControl.connectedClient.remove(thisClient);
				Main.mainScreen.updateClientTable();
			}
		}
	}
	
	
	private void handleNewLogin() throws IOException {

		
		String clientUsername = thisClient.receiver.readLine();
		String clientPassword = thisClient.receiver.readLine();
	
		boolean userNameExisted = false;
		for (Client connectedClient : Main.socketControl.connectedClient) {
			if (connectedClient.userName.equals(clientUsername)) {
				userNameExisted = true;
				break;
			}
		}

		// Validate login on the server side
		if (!userNameExisted) {
			if (databaseHandler.validateLogin(clientUsername, clientPassword)) {
				thisClient.userName = clientUsername;
				Main.socketControl.connectedClient.add(thisClient);
				Main.mainScreen.updateClientTable();
		
				thisClient.sender.write("login success");
				thisClient.sender.newLine();
				thisClient.sender.flush();
		
				thisClient.sender.write("" + (Main.socketControl.connectedClient.size() - 1));
				thisClient.sender.newLine();
				thisClient.sender.flush();
				for (Client client : Main.socketControl.connectedClient) {
					if (client.userName.equals(thisClient.userName))
						continue;
					thisClient.sender.write(client.userName);
					thisClient.sender.newLine();
					thisClient.sender.flush();
				}
		
				for (Client client : Main.socketControl.connectedClient) {
					if (client.userName.equals(thisClient.userName))
						continue;
					client.sender.write("new user online");
					client.sender.newLine();
					client.sender.write(thisClient.userName);
					client.sender.newLine();
					client.sender.flush();
				}


			}else{
				thisClient.sender.write("Wrong");
				thisClient.sender.newLine();
				thisClient.sender.flush();
			}

			
		}else {
			thisClient.sender.write("login failed");
			thisClient.sender.newLine();
			thisClient.sender.flush();
		}
		System.out.println("After handling new login");
	}
	
	private void handleNewRegister() throws IOException {
		// Receive registration data from the client
		String fullName = thisClient.receiver.readLine();
		String email = thisClient.receiver.readLine();
		String phone = thisClient.receiver.readLine();
		String username = thisClient.receiver.readLine();
		String password = thisClient.receiver.readLine();
	
		// Check if the username already exists in the database
		if (databaseHandler.isUserExists(username)) {
			// User already exists, send registration failed response
			thisClient.sender.write("Existed");
			thisClient.sender.newLine();
			thisClient.sender.flush();
		} else {
			// Perform registration on the server side
			if (databaseHandler.saveRegistrationToDatabase(fullName, email, phone, username, password)) {
				// Registration successful
				thisClient.sender.write("registration success");
				thisClient.sender.newLine();
				thisClient.sender.flush();
			} else {
				// Registration failed
				thisClient.sender.write("registration failed");
				thisClient.sender.newLine();
				thisClient.sender.flush();
			}
		}
	}	

	private void handleCreateRequestRoom() throws IOException {
		String roomName = thisClient.receiver.readLine();
		String roomType = thisClient.receiver.readLine();
		int userCount = Integer.parseInt(thisClient.receiver.readLine());
		List<String> users = new ArrayList<>();
	
		// Read and add users to the list
		for (int i = 0; i < userCount; i++)
			users.add(thisClient.receiver.readLine());
	
		// Check if a room already exists for the given users
		String creator = users.get(0);

		Room existingRoom = Room.findExistingRoom(Main.socketControl.allRooms, users);
		
		if (existingRoom != null) {
			System.out.print("join");
			handleJoinRoom(existingRoom);
		} else {
			System.out.print("new");
			// Room doesn't exist, create a new room
			Room newRoom = new Room(roomName, roomType, users, creator);
			Main.socketControl.allRooms.add(newRoom);
			

			for (int i = 0; i < userCount; i++) {
				// Check if the user is online before notifying
				Client currentClient = Client.findClient(Main.socketControl.connectedClient, users.get(i));
				if (currentClient != null) {
					BufferedWriter currentClientSender = currentClient.sender;
					currentClientSender.write("new room");
					currentClientSender.newLine();
					currentClientSender.write("" + newRoom.id);
					currentClientSender.newLine();
					currentClientSender.write(thisClient.userName);
					currentClientSender.newLine();
					if (roomType.equals("private")) {
						// private chat thì tên room của mỗi người sẽ là tên của người kia
						currentClientSender.write(users.get(1 - i)); // user 0 thì gửi 1, user 1 thì gửi 0
						currentClientSender.newLine();
					} else {
						currentClientSender.write(roomName);
						currentClientSender.newLine();
					}
					currentClientSender.write(roomType);
					currentClientSender.newLine();
					currentClientSender.write("" + users.size());
					currentClientSender.newLine();
					for (String userr : users) {
						currentClientSender.write(userr);
						currentClientSender.newLine();
					}
					currentClientSender.flush();
				}
			}
		}
	}
	
	private void handleGetUsersWithAccounts() throws IOException {

		List<String> usersWithAccounts = databaseHandler.getAllUsernames(); // Adjust this based on your database logic
		// Send the count of users with accounts to the client
		thisClient.sender.write("" + usersWithAccounts.size());
		thisClient.sender.newLine();
	
		// Send each username to the client
		for (String username : usersWithAccounts) {
			thisClient.sender.write(username);
			thisClient.sender.newLine();
		}
	
		thisClient.sender.flush();
	}
	

	
	private void handleJoinRoom(Room existingRoom) throws IOException {
		if (thisClient.userName != null) {

			List<String> messages = loadMessages(existingRoom.id);
			thisClient.sender.write("" + messages.size());
			thisClient.sender.newLine();
			for (String message : messages) {
				thisClient.sender.write(message);
				thisClient.sender.newLine();
			}
			thisClient.sender.flush();
	
			// Notify existing users about the new user
			for (String user : existingRoom.users) {
				BufferedWriter currentClientSender = Client.findClient(Main.socketControl.connectedClient, user).sender;
				currentClientSender.write("join room");
				currentClientSender.newLine();
				currentClientSender.write("" + existingRoom.id);
				currentClientSender.newLine();
				currentClientSender.write(thisClient.userName);
				currentClientSender.newLine();

				
				if (existingRoom.type.equals("private")) {
					// private chat thì tên room của mỗi người sẽ là tên của người kia
					String otherUser = existingRoom.users.get(1 - existingRoom.users.indexOf(user));
					currentClientSender.write(otherUser);
					currentClientSender.newLine();
				} else {
					currentClientSender.write(existingRoom.name);
					currentClientSender.newLine();
				}

				currentClientSender.write(existingRoom.type);
				currentClientSender.newLine();
				currentClientSender.write("" + existingRoom.users.size());
				currentClientSender.newLine();
				for (String roomUser : existingRoom.users) {
					currentClientSender.write(roomUser);
					currentClientSender.newLine();
				}
	
				// Send existing messages to existing users
				List<String> existingUserMessages = loadMessages(existingRoom.id);
				currentClientSender.write("" + existingUserMessages.size());
				currentClientSender.newLine();
				for (String message : existingUserMessages) {
					currentClientSender.write(message);
					currentClientSender.newLine();
				}
	
				currentClientSender.flush();
			}
	
			thisClient.sender.flush();
		} else {
			// User is no longer logged in, handle accordingly (e.g., notify the client)
			thisClient.sender.write("user not logged in");
			thisClient.sender.newLine();
			thisClient.sender.flush();
		}
	}
	
   
	private void handleLoadMessages() throws IOException {
        int roomID = Integer.parseInt(thisClient.receiver.readLine());

        // Load messages for the specified room
        List<String> messages = loadMessages(roomID);

        // Send the messages count and the messages to the client
        thisClient.sender.write("" + messages.size());
        thisClient.sender.newLine();
        for (String message : messages) {
            thisClient.sender.write(message);
            thisClient.sender.newLine();
        }
        thisClient.sender.flush();
    }

	private void saveMessage(int roomID, String sender, String content) {
		String fileName = "messages_" + roomID + ".txt";
		try (FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
			 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
			 BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
			bufferedWriter.write(sender + ": " + content);
			bufferedWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> loadMessages(int roomID) {
		List<String> messages = new ArrayList<>();
		String fileName = "messages_" + roomID + ".txt";
		try (FileInputStream fileInputStream = new FileInputStream(fileName);
			 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				messages.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messages;
	}
	
}

