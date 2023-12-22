import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
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
	public List<String> usersWithAccounts;

	public List<Room> allRooms;
	public String downloadToPath;

	List<Integer> savedRoomIDs;
	List<String> userMessages;

	public SocketControl(String name, String password, DataServer connectedServer) {
		onlineUsers = new ArrayList<String>();
		usersWithAccounts = new ArrayList<String>();
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

	public SocketControl(String fullName, String email, String phone, String name, String password,
			DataServer connectedServer) {
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
				Main.mainScreen.updateRegisteredUserList();
				getGroups();
				// Main.mainScreen.updateGroupJList();

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
									String whoQuit = receiver.readLine();
									onlineUsers.remove(whoQuit);
									Main.mainScreen.updateDataServer();
									Main.mainScreen.updateOnlineUserJList();
									for (Room room : allRooms) {
										if (room.users.contains(whoQuit)) {
											Main.mainScreen.addNewMessage(room.id, "notify", whoQuit,
													"Đã thoát ứng dụng");
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
								case "file from user to room": {
									String user = receiver.readLine();
									int roomID = Integer.parseInt(receiver.readLine());
									String fileName = receiver.readLine();
									System.out.println(
											"Recevie file " + fileName + " from " + user + " to room " + roomID);
									Main.mainScreen.addNewMessage(roomID, "file", user, fileName);
									break;
								}
								case "response download file": {
									int fileSize = Integer.parseInt(receiver.readLine());
									File file = new File(downloadToPath);
									byte[] buffer = new byte[1024];
									InputStream in = s.getInputStream();
									OutputStream out = new FileOutputStream(file);

									int count;
									int receivedFileSize = 0;
									while ((count = in.read(buffer)) > 0) {
										out.write(buffer, 0, count);
										receivedFileSize += count;
										if (receivedFileSize >= fileSize)
											break;
									}

									out.close();
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
			} else if (loginResult.equals("Wrong")) {
				Main.connectServer.loginResultAction("wrong");
			} else
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

			} else if (registerResult.equals("Existed")) {
				Main.connectServer.registerResultAction("existed");
			} else
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
		if (existingRoom != null) {
			loadMessages(existingRoom.id);
		}

	}

	public void sendFileToRoom(int roomID, String fileName, String filePath) {
		try {
			System.out.println("Send file " + fileName + " to room " + roomID);

			File file = new File(filePath);
			Room room = Room.findRoom(allRooms, roomID);

			sender.write("file to room");
			sender.newLine();
			sender.write("" + roomID);
			sender.newLine();
			sender.write("" + room.messages.size());
			sender.newLine();
			sender.write(fileName);
			sender.newLine();
			sender.write("" + file.length());
			sender.newLine();
			sender.flush();

			byte[] buffer = new byte[1024];
			InputStream in = new FileInputStream(file);
			OutputStream out = s.getOutputStream();

			int count;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}

			in.close();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void downloadFile(int roomID, int fileMessageIndex, String fileName, String downloadToPath) {

		this.downloadToPath = downloadToPath;
		try {
			sender.write("request download file");
			sender.newLine();
			sender.write("" + roomID);
			sender.newLine();
			sender.write("" + fileMessageIndex);
			sender.newLine();
			sender.write(fileName);
			sender.newLine();
			sender.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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

	public List<String> getUsersWithAccounts() {
		try {
			sender.write("get users with accounts");
			sender.newLine();
			sender.flush();

			int userCount = Integer.parseInt(receiver.readLine());
			for (int i = 0; i < userCount; i++) {
				usersWithAccounts.add(receiver.readLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return usersWithAccounts;
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

	public void getGroups() {

		try {
			sender.write("get groups");
			sender.newLine();
			sender.flush();

			// Read the count of groups from the server
			int groupCount = Integer.parseInt(receiver.readLine());

			// Read information for each group and add it to the list
			for (int i = 0; i < groupCount; i++) {
				int roomID = Integer.parseInt(receiver.readLine());
				String name = receiver.readLine();
				String type = receiver.readLine();
				int userCount = Integer.parseInt(receiver.readLine());

				List<String> users = new ArrayList<>();
				for (int j = 0; j < userCount; j++) {
					users.add(receiver.readLine());
				}

				Room group = new Room(roomID, name, type, users);
				Main.socketControl.allRooms.add(group);

				Main.mainScreen.newRoomTab(group);
				Main.mainScreen.addNewMessage(group.id, "notify", group.name,
						type.equals("group") ? "Đã tạo group" : "Đã mở chat");
				Main.mainScreen.updateGroupJList();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<ChatData> getUserMessages(int roomID) {
		Room targetRoom = Room.findRoom(allRooms, roomID);
		if (targetRoom != null) {
			return targetRoom.getMessagesByUser(userName);
		}
		return Collections.emptyList();
	}
}