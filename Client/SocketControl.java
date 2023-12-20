import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class SocketControl {

	String userName;
	String password;

	String fullName;
	String email;
	String phone;

	DataServer connectedServer;
	Socket s;
	BufferedReader receiver;
	BufferedWriter sender;

	Thread receiveAndProcessThread;

	public List<String> onlineUsers;
	public List<Room> allRooms;

	List<Integer> savedRoomIDs;
	List<String> userMessages;

	public SocketControl(String name, String password, DataServer connectedServer) {
		onlineUsers = new ArrayList<String>();
		allRooms = new ArrayList<Room>();
		savedRoomIDs = new ArrayList<>();
		userMessages = new ArrayList<>();

		try {
			this.userName = name;
			this.password = password;
			this.connectedServer = connectedServer;
			s = new Socket(connectedServer.ip, connectedServer.port);
			InputStream is = s.getInputStream();
			receiver = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			OutputStream os = s.getOutputStream();
			sender = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

		} catch (IOException e1) {
			Main.connectServer.loginResultAction("closed");
		}
	}
	public SocketControl(String fullName, String email, String phone, String name, String password, DataServer connectedServer) {
		userMessages = new ArrayList<>();


		try {
			this.fullName = fullName;
			this.email = email;
			this.phone = phone;
			this.userName = name;
			this.password = password;
			this.connectedServer = connectedServer;
			s = new Socket(connectedServer.ip, connectedServer.port);
			InputStream is = s.getInputStream();
			receiver = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			OutputStream os = s.getOutputStream();
			sender = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

		} catch (IOException e1) {
			Main.connectServer.loginResultAction("closed");
		}
	}

	public void Login() {
		try {
			sender.write("new login");
            sender.newLine();
            sender.write(userName);
            sender.newLine();
            sender.write(password);
            sender.newLine();
            sender.flush();

			String loginResult = receiver.readLine();
			if (loginResult.equals("login success")) {
				Main.connectServer.loginResultAction("success");

				int serverOnlineAccountCount = Integer.parseInt(receiver.readLine());
				for (int i = 0; i < serverOnlineAccountCount; i++)
					onlineUsers.add(receiver.readLine());

				Main.mainScreen.updateDataServer();
				Main.mainScreen.updateOnlineUserJList();

				receiveAndProcessThread = new Thread(() -> {
					try {
						while (true) {
							String header = receiver.readLine();
							System.out.println("Header " + header);
							if (header == null)
								throw new IOException();

							switch (header) {
							case "new user online": {
								String who = receiver.readLine();
								onlineUsers.add(who);
								Main.mainScreen.updateDataServer();
								Main.mainScreen.updateOnlineUserJList();
								break;
							}
							case "user quit": {
								String username = receiver.readLine();
								String whoQuit = receiver.readLine();
								onlineUsers.remove(whoQuit);
								Main.mainScreen.updateDataServer();
								Main.mainScreen.updateOnlineUserJList();
								for (Room room : allRooms) {
									if (room.users.contains(whoQuit)) {
										Main.mainScreen.addNewMessage(room.id, "notify", whoQuit, "Đã thoát ứng dụng");
										room.users.remove(whoQuit);
									}
								}
								Main.mainScreen.updateRoomUsersJList();

								break;
							}
							case "new room": {
								System.out.println("new");
								int roomID = Integer.parseInt(receiver.readLine());
								String whoCreate = receiver.readLine();
								String name = receiver.readLine();
								String type = receiver.readLine();
								int roomUserCount = Integer.parseInt(receiver.readLine());
								List<String> users = new ArrayList<String>();
								for (int i = 0; i < roomUserCount; i++)
									users.add(receiver.readLine());

								Room newRoom = new Room(roomID, name, type, users);
								Main.socketControl.allRooms.add(newRoom);
								Main.mainScreen.newRoomTab(newRoom);
								Main.mainScreen.addNewMessage(newRoom.id, "notify", whoCreate,
										type.equals("group") ? "Đã tạo group" : "Đã mở chat");
								Main.mainScreen.updateGroupJList();
								break;
							}
							case "join room": {
								System.out.println("join");
								int roomID = Integer.parseInt(receiver.readLine());
								String whoCreate = receiver.readLine();
								String name = receiver.readLine();
								String type = receiver.readLine();
								int roomUserCount = Integer.parseInt(receiver.readLine());
								List<String> users = new ArrayList<String>();
								for (int i = 0; i < roomUserCount; i++)
									users.add(receiver.readLine());

								Room newRoom = new Room(roomID, name, type, users);
								Main.socketControl.allRooms.add(newRoom);
								Main.mainScreen.newRoomTab(newRoom);
								Main.mainScreen.addNewMessage(newRoom.id, "notify", whoCreate,
										type.equals("group") ? "Đã tạo group" : "Đã mở chat");
								Main.mainScreen.updateGroupJList();
								break;
							}

							case "text from user to room": {
								String user = receiver.readLine();
								int roomID = Integer.parseInt(receiver.readLine());
								String content = "";
								char c;
								do {
									c = (char) receiver.read();
									if (c != '\0')
										content += c;
								} while (c != '\0');
								Main.mainScreen.addNewMessage(roomID, "text", user, content);
								break;
							}
                        }
						}
					} catch (IOException e) {
						JOptionPane.showMessageDialog(Main.mainScreen, "Server đã đóng, ứng dụng sẽ thoát", "Thông báo",
								JOptionPane.INFORMATION_MESSAGE);
						try {
							Main.socketControl.s.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						System.exit(0);
					}
				});
				receiveAndProcessThread.start();
			} else if(loginResult.equals("Wrong")){
				Main.connectServer.loginResultAction("wrong");
			}else
				Main.connectServer.loginResultAction("existed");

		} catch (IOException e1) {

		}
	}

	public void Register() {
		try {
			sender.write("new register");
			sender.newLine();
            sender.write(fullName);
			sender.newLine();
            sender.write(email);
			sender.newLine();
            sender.write(phone);
            sender.newLine();
            sender.write(userName);
            sender.newLine();
            sender.write(password);
            sender.newLine();
            sender.flush();

			String registerResult = receiver.readLine();
			if (registerResult.equals("registration success")) {
				Main.connectServer.registerResultAction("success");

			} else if(registerResult.equals("Existed")){
				Main.connectServer.registerResultAction("existed");
			}else
				Main.connectServer.registerResultAction("closed");

		} catch (IOException e1) {

		}
	}
	
	public void sendTextToRoom(int roomID, String content) {
		try {
			sender.write("text to room");
			sender.newLine();
			sender.write("" + roomID);
			sender.newLine();
			sender.write(content);
			sender.write('\0');
			sender.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logout() {
		try {
			// Check if the socket is already closed or null
			if (s != null && !s.isClosed()) {

				sender.write("logout");
				sender.newLine();
				sender.flush();

			}
	
			// Interrupt the receiveAndProcessThread if it's running
			if (receiveAndProcessThread != null && receiveAndProcessThread.isAlive()) {
				receiveAndProcessThread.interrupt();
			}
	
			// Optionally set the socket to null to indicate it's closed
			s = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadMessages(int roomID) {
		try {
			sender.write("load messages");
			sender.newLine();
			sender.write("" + roomID);
			sender.newLine();
			sender.flush();
	
			// Handle the response from the server (list of messages)
			int messageCount = Integer.parseInt(receiver.readLine());
			List<String> messages = new ArrayList<>();
			for (int i = 0; i < messageCount; i++) {
				messages.add(receiver.readLine());
			}

			Room targetRoom = Room.findRoom(allRooms, roomID);

        // Update the message list of the corresponding room
        if (targetRoom != null) {
            targetRoom.messages.clear(); // Clear existing messages
            for (String message : messages) {
                // Assuming you have a ChatData constructor that parses the message string
                ChatData chatData = ChatData.parse(message);
                targetRoom.addMessage(chatData);
            }
        }

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createPrivateRoom(String otherUser) {
		Room existingRoom = Room.findPrivateRoom(allRooms, otherUser);
	
		if (existingRoom != null) {
			// Room already exists, no need to create a new one
			System.out.println("Private room already exists for users " + userName + " and " + otherUser);
		} else {
			try {
				sender.write("request create room");
				sender.newLine();
				sender.write(otherUser); // room name
				sender.newLine();
				sender.write("private"); // room type
				sender.newLine();
				sender.write("2");
				sender.newLine();
				sender.write(userName);
				sender.newLine();
				sender.write(otherUser);
				sender.newLine();
				sender.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		loadMessages(existingRoom.id);
	}

	
	
	public void createGroup(String groupName, List<String> otherUsers) {

		try {
			sender.write("request create room");
			sender.newLine();
			sender.write(groupName);
			sender.newLine();
			sender.write("group"); // room name
			sender.newLine();
			sender.write("" + (otherUsers.size() + 1));
			sender.newLine();
			sender.write(userName);
			sender.newLine();
			for (String user : otherUsers) {
				sender.write(user);
				sender.newLine();
			}
			sender.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static boolean serverOnline(String ip, int port) {
		try {
			Socket s = new Socket();
			s.connect(new InetSocketAddress(ip, port), 300);
			s.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	public static String serverName(String ip, int port) {

		if (!serverOnline(ip, port))
			return "";

		try {
			Socket s = new Socket(ip, port);
			InputStream is = s.getInputStream();
			BufferedReader receiver = new BufferedReader(new InputStreamReader(is));
			OutputStream os = s.getOutputStream();
			BufferedWriter sender = new BufferedWriter(new OutputStreamWriter(os));

			sender.write("get name");
			sender.newLine();
			sender.flush();

			String name = receiver.readLine();

			s.close();
			return name;
		} catch (IOException ex) {
			return "";
		}
	}

	public static int serverConnectedAccountCount(String ip, int port) {
		try {
			Socket s = new Socket(ip, port);
			InputStream is = s.getInputStream();
			BufferedReader receiver = new BufferedReader(new InputStreamReader(is));
			OutputStream os = s.getOutputStream();
			BufferedWriter sender = new BufferedWriter(new OutputStreamWriter(os));

			sender.write("get connected count");
			sender.newLine();
			sender.flush();

			int count = Integer.parseInt(receiver.readLine());

			s.close();
			return count;
		} catch (IOException ex) {
			return 0;
		}
	}

}