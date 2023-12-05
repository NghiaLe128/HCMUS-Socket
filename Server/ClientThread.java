package Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {

	Client thisClient;

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

					String clientUsername = thisClient.receiver.readLine();
					String clientPassword = thisClient.receiver.readLine();

					boolean userNameExisted = false;
					for (Client connectedClient : Main.socketControl.connectedClient) {
						if (connectedClient.userName.equals(clientUsername)) {
							userNameExisted = true;
							// Check if the password matches
							if (connectedClient.password.equals(clientPassword)) {
								// Perform login actions here
							} else {
								thisClient.sender.write("login failed");
								thisClient.sender.newLine();
								thisClient.sender.flush();
							}
							break;
						}
					}

					if (!userNameExisted) {
						thisClient.userName = clientUsername;
						thisClient.password = clientPassword;

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
					} else {
						thisClient.sender.write("login failed");
						thisClient.sender.newLine();
						thisClient.sender.flush();
					}
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

					thisClient.socket.close();

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Main.socketControl.connectedClient.remove(thisClient);
				Main.mainScreen.updateClientTable();
			}
		}
	}
}
