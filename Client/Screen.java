import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Screen extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    public static int chattingRoom = -1;

    GBCBuild gbc;
    JPanel mainContent;

    private JList<String> connectedServerInfoJList;

    private JList<String> registeredUserJList;
    private JList<String> onlineUserJList;

    private JList<String> groupJList;

    private JTabbedPane roomTabbedPane;
    private List<RoomMessagesPanel> roomMessagesPanels;
    private JList<String> roomUsersJList;
    private JPanel enterMessagePanel;
    private JTextArea messageArea;

    private JButton logoutButton;

    public Screen() {
        initComponents();
        setupUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });

        // Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            handleWindowClosing();
        }));
    }

    private void initComponents() {
        gbc = new GBCBuild(1, 3).setInsets(5, 5, 5, 5);
        mainContent = new JPanel(new GridBagLayout());
        connectedServerInfoJList = createConnectedServerInfoList();

        registeredUserJList = new JList<>();
        registeredUserJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleRegisteredUserListDoubleClick(e);
            }
        });

        onlineUserJList = new JList<>();
        onlineUserJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleOnlineUserListDoubleClick(e);
            }
        });

        groupJList = new JList<>();
        groupJList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleGroupListDoubleClick(e);
            }
        });

        enterMessagePanel = createEnterMessagePanel();
        roomTabbedPane = createRoomTabbedPane();
        roomMessagesPanels = new ArrayList<>();
        roomUsersJList = new JList<>();

        logoutButton = new JButton("Đăng Xuất");
        logoutButton.setBackground(new Color(255, 69, 0)); // Red-orange
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setActionCommand("logout");
        logoutButton.addActionListener(this);
    }

    private JList<String> createConnectedServerInfoList() {
        String[] serverInfo = {
                "IP: " + Main.socketControl.connectedServer.ip,
                "Port: " + Main.socketControl.connectedServer.port,
                "Số user online: " + Main.socketControl.connectedServer.connectAccountCount
        };

        JList<String> list = new JList<>(serverInfo);
        list.setBorder(BorderFactory.createTitledBorder(
                SocketControl.serverName(Main.socketControl.connectedServer.ip,
                        Main.socketControl.connectedServer.port)));

        return list;
    }

    private void handleRegisteredUserListDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            String clickedUser = registeredUserJList.getSelectedValue();
            System.out.println("Double click " + clickedUser);

            Room foundRoom = Room.findPrivateRoom(Main.socketControl.allRooms, clickedUser);
            if (foundRoom == null) {
                System.out.println("null");
                Main.socketControl.createPrivateRoom(clickedUser);

            } else {
                System.out.println("ko null");
                // Load messages for the existing private room
                Main.socketControl.loadMessages(foundRoom.id);

                int roomTabIndex = findRoomTabIndex(foundRoom);
                if (roomTabIndex == -1) {
                    newRoomTab(foundRoom);
                    roomTabbedPane.setSelectedIndex(roomTabbedPane.getTabCount() - 1);
                } else {
                    roomTabbedPane.setSelectedIndex(roomTabIndex);
                }
            }
        }
    }

    private void handleOnlineUserListDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            String clickedUser = onlineUserJList.getSelectedValue();
            System.out.println("Double click " + clickedUser);

            Room foundRoom = Room.findPrivateRoom(Main.socketControl.allRooms, clickedUser);
            if (foundRoom == null) {
                System.out.println("null");
                Main.socketControl.createPrivateRoom(clickedUser);

            } else {
                System.out.println("ko null");
                // Load messages for the existing private room
                Main.socketControl.loadMessages(foundRoom.id);

                int roomTabIndex = findRoomTabIndex(foundRoom);
                if (roomTabIndex == -1) {
                    newRoomTab(foundRoom);
                    roomTabbedPane.setSelectedIndex(roomTabbedPane.getTabCount() - 1);
                } else {
                    roomTabbedPane.setSelectedIndex(roomTabIndex);
                }
            }
        }
    }

    private void handleGroupListDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            String clickedGroup = groupJList.getSelectedValue();
            System.out.println("Double click " + clickedGroup);
            Room foundRoom = Room.findGroup(Main.socketControl.allRooms, clickedGroup);

            if (foundRoom == null) {
                System.out.println("null");
                Main.socketControl.createPrivateRoom(clickedGroup);

            } else {
                System.out.println("ko null");
                // Load messages for the existing private room
                Main.socketControl.loadMessages(foundRoom.id);

                int roomTabIndex = findRoomTabIndex(foundRoom);
                if (roomTabIndex == -1) {
                    newRoomTab(foundRoom);
                    roomTabbedPane.setSelectedIndex(roomTabbedPane.getTabCount() - 1);
                } else {
                    roomTabbedPane.setSelectedIndex(roomTabIndex);
                }
            }
        }
    }

    private int findRoomTabIndex(Room foundRoom) {
        for (int i = 0; i < roomTabbedPane.getTabCount(); i++) {
            JScrollPane currentScrollPane = (JScrollPane) roomTabbedPane.getComponentAt(i);
            RoomMessagesPanel currentRoomMessagePanel = (RoomMessagesPanel) currentScrollPane.getViewport().getView();
            if (currentRoomMessagePanel.room.id == foundRoom.id) {
                return i;
            }
        }
        return -1;
    }

    private JPanel createEnterMessagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240)); // Light gray background

        JButton sendButton = new JButton("Gửi");
        sendButton.setBackground(new Color(50, 205, 50)); // Lime green
        sendButton.setForeground(Color.white);
        sendButton.setActionCommand("send");
        sendButton.addActionListener(this);

        JButton emojiButton = new JButton(new String(Character.toChars(0x1F601)));
        emojiButton.setBackground(new Color(70, 130, 180)); // Steel blue
        emojiButton.setForeground(Color.white);
        emojiButton.setActionCommand("emoji");
        emojiButton.addActionListener(this);

        JButton fileButton = new JButton(Main.getScaledImage("/image/fileIcon.png", 16, 16));
        fileButton.setBackground(new Color(255, 69, 0)); // Red-orange
        fileButton.setForeground(Color.white);
        fileButton.setActionCommand("file");
        fileButton.addActionListener(this);

        messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createLineBorder(new Color(169, 169, 169), 1, true)); // Dark gray
                                                                                                        // border

        InputMap input = messageArea.getInputMap();
        input.put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        input.put(KeyStroke.getKeyStroke("ENTER"), "text-submit");

        messageArea.getActionMap().put("text-submit", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });

        panel.add(messageScrollPane, gbc.setGrid(1, 1).setWeight(1, 1).setFill(GridBagConstraints.BOTH));
        panel.add(sendButton,
                gbc.setGrid(2, 1).setWeight(0, 0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.NORTH));
        panel.add(emojiButton, gbc.setGrid(3, 1));
        panel.add(fileButton, gbc.setGrid(4, 1));

        return panel;
    }

    private JTabbedPane createRoomTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JScrollPane selectedTab = (JScrollPane) roomTabbedPane.getSelectedComponent();
                if (selectedTab != null) {
                    RoomMessagesPanel selectedMessagePanel = (RoomMessagesPanel) selectedTab.getViewport().getView();
                    chattingRoom = selectedMessagePanel.room.id;
                    updateRoomUsersJList();
                }
            }
        });
        return tabbedPane;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new GridBagLayout());
        chatPanel.setBackground(new Color(255, 255, 255)); // White background

        chatPanel.add(roomTabbedPane, gbc.setGrid(1, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
        chatPanel.add(enterMessagePanel, gbc.setGrid(1, 2).setWeight(1, 0));

        return chatPanel;
    }

    private void setupUI() {
        // Create JScrollPane for registered users
        JScrollPane registeredUserScrollPane = new JScrollPane(registeredUserJList);
        registeredUserScrollPane.setBorder(BorderFactory.createTitledBorder("List of users available for chat"));
    
        JScrollPane groupListScrollPane = new JScrollPane(groupJList);
        groupListScrollPane.setBorder(BorderFactory.createTitledBorder("List of groups"));
    
        JButton createGroupButton = new JButton("Create Group");
        createGroupButton.setBackground(new Color(0, 191, 255));
        createGroupButton.setForeground(Color.white);
        createGroupButton.setActionCommand("group");
        createGroupButton.addActionListener(this);
    
        JPanel groupPanel = new JPanel(new GridBagLayout());
        groupPanel.add(groupListScrollPane, gbc.setGrid(1, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
        groupPanel.add(createGroupButton, gbc.setGrid(1, 2).setWeight(1, 0));
    
        JSplitPane chatSubjectSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, registeredUserScrollPane, groupPanel);
        chatSubjectSplitPane.setDividerLocation(230);
    
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.add(connectedServerInfoJList, gbc.setGrid(1, 1).setWeight(1, 0).setFill(GridBagConstraints.BOTH));
        leftPanel.add(chatSubjectSplitPane, gbc.setGrid(1, 2).setWeight(1, 1));
    
        JPanel chatPanel = createChatPanel();
    
        roomUsersJList.setBorder(BorderFactory.createTitledBorder("Users in the current room"));
    
        // Create JScrollPane for online users
        JScrollPane onlineUserScrollPane = new JScrollPane(onlineUserJList);
        onlineUserScrollPane.setBorder(BorderFactory.createTitledBorder("List of users currently online"));
    
        // Create a panel for room users
        JPanel roomUsersPanel = new JPanel(new GridBagLayout());
        roomUsersPanel.add(roomUsersJList, gbc.setGrid(1, 1).setWeight(1, 1));
    
        // Create a panel for online users
        JPanel onlineUserPanel = new JPanel(new GridBagLayout());
        onlineUserPanel.add(onlineUserScrollPane, gbc.setGrid(1, 1).setWeight(1, 1));
    
        // Create a panel for user lists (room users and online users)
        JPanel userListPanel = new JPanel(new GridBagLayout());
        userListPanel.add(roomUsersPanel, gbc.setGrid(1, 1).setWeight(1, 1));
        userListPanel.add(onlineUserPanel, gbc.setGrid(1, 2).setWeight(1, 1));
    
        JSplitPane roomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatPanel, userListPanel);
        roomSplitPane.setDividerLocation(420);
    
        JSplitPane mainSplitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, roomSplitPane);
        mainContent.add(mainSplitpane, gbc.setGrid(1, 1).setWeight(1, 1));
    
        mainContent.add(logoutButton,
                gbc.setGrid(1, 2).setWeight(0, 0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.SOUTH));
    
        SwingUtilities.invokeLater(() -> mainSplitpane.setDividerLocation(180));
    
        configureFrame(mainContent);
    }
    
    private void configureFrame(JPanel mainContent) {

        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Font customFont = new Font("Arial", Font.PLAIN, 14);
        UIManager.put("Button.font", customFont);
        UIManager.put("Label.font", customFont);

        this.setPreferredSize(new Dimension(900, 600));
        this.setTitle("Ứng dụng chat đăng nhập với tên " + Main.socketControl.userName);
        this.setContentPane(mainContent);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void updateDataServer() {
        Main.socketControl.connectedServer.connectAccountCount = Main.socketControl.onlineUsers.size();

        connectedServerInfoJList.setListData(new String[] { "IP: " + Main.socketControl.connectedServer.ip,
                "Port: " + Main.socketControl.connectedServer.port,
                "Số user online: " + Main.socketControl.connectedServer.connectAccountCount });
    }

    public void newRoomTab(Room room) {
        RoomMessagesPanel roomMessagesPanel = new RoomMessagesPanel(room);
        roomMessagesPanels.add(roomMessagesPanel);

        // Load messages for the new room
        System.out.println(room.id);
        Main.socketControl.loadMessages(room.id);

        System.out.println("add");
        roomMessagesPanel.addMessages(room.messages);

        JScrollPane messagesScrollPane = new JScrollPane(roomMessagesPanel);
        messagesScrollPane.setMinimumSize(new Dimension(50, 100));
        messagesScrollPane.getViewport().setBackground(Color.white);

        roomTabbedPane.addTab(room.name, messagesScrollPane);
        roomTabbedPane.setTabComponentAt(roomTabbedPane.getTabCount() - 1,
                new TabComponent(room.name, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        roomMessagesPanels.remove(roomMessagesPanel);
                        roomTabbedPane.remove(messagesScrollPane);
                    }
                }));
    }

    public void updateOnlineUserJList() {
        onlineUserJList.setListData(Main.socketControl.onlineUsers.toArray(new String[0]));
    }

    public void updateRegisteredUserList() {
        try {
            // Assuming you have a method in SocketControl to get the list of registered users
            List<String> registeredUsers = Main.socketControl.getUsersWithAccounts();
            registeredUserJList.setListData(registeredUsers.toArray(new String[0]));
        } catch (Exception e) {
            // Handle the exception appropriately (e.g., show an error message)
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching registered users", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateRoomUsersJList() {
        System.out.println("updateRoomUsersJList");
        Room theChattingRoom = Room.findRoom(Main.socketControl.allRooms, chattingRoom);
        if (theChattingRoom != null)
            roomUsersJList.setListData(theChattingRoom.users.toArray(new String[0]));
    }

    public void updateGroupJList() {
        //Main.socketControl.getGroups();
        
        List<String> groupList = new ArrayList<>();
        for (Room room : Main.socketControl.allRooms) {
            if ("group".equals(room.type))
                groupList.add(room.name);
        }
        groupJList.setListData(groupList.toArray(new String[0]));
    }

    public void addNewMessage(int roomID, String type, String whoSend, String content) {
        ChatData messageData = new ChatData(whoSend, type, content);
        Room receiveMessageRoom = Room.findRoom(Main.socketControl.allRooms, roomID);
        receiveMessageRoom.messages.add(messageData);

        addNewMessageGUI(roomID, messageData);
    }

    private void addNewMessageGUI(int roomID, ChatData messageData) {
        ChatPanel newMessagePanel = new ChatPanel(messageData);
        newMessagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoomMessagesPanel receiveMessageRoomMessagesPanel = RoomMessagesPanel.findRoomMessagesPanel(roomMessagesPanels,
                roomID);
        receiveMessageRoomMessagesPanel.add(Box.createHorizontalGlue());
        receiveMessageRoomMessagesPanel.add(newMessagePanel);
        receiveMessageRoomMessagesPanel.validate();
        receiveMessageRoomMessagesPanel.repaint();
        roomTabbedPane.validate();
        roomTabbedPane.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "send": {
                String content = messageArea.getText();
                if (content.isEmpty())
                    break;
                if (chattingRoom != -1)
                    Main.socketControl.sendTextToRoom(chattingRoom, content);
                messageArea.setText("");
                break;
            }
            case "logout": {
                handleLogout();
                break;
            }
        
            case "group": {
                JDialog chooseUserDialog = new JDialog();
                JPanel chooseUserContent = new JPanel(new GridBagLayout());
                GBCBuild gbc = new GBCBuild(1, 1);
    
                JList<String> onlineUserJList = new JList<String>(Main.socketControl.usersWithAccounts.toArray(new String[0]));
                JScrollPane onlineUserScrollPanel = new JScrollPane(onlineUserJList);
                onlineUserScrollPanel.setBorder(BorderFactory.createTitledBorder("Chọn user để thêm vào nhóm"));
    
                JLabel groupNameLabel = new JLabel("Tên group: ");
                JTextField groupNameField = new JTextField();
                JButton createButton = new JButton("Tạo group");
                createButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String groupName = groupNameField.getText();
                        if (groupName.isEmpty()) {
                            JOptionPane.showMessageDialog(chooseUserDialog, "Tên group không được trống", "Lỗi tạo group",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        List<String> chosenUsers = onlineUserJList.getSelectedValuesList();
                        if (chosenUsers.size() < 2) {
                            JOptionPane.showMessageDialog(chooseUserDialog,
                                    "Group phải có từ 3 người trở lên (chọn 2 người trở lên)", "Lỗi tạo group",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        Main.socketControl.createGroup(groupName, chosenUsers);
                        chooseUserDialog.setVisible(false);
                        chooseUserDialog.dispose();
                    }
                });
    
                chooseUserContent.add(onlineUserScrollPanel,
                        gbc.setSpan(2, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 0));
                chooseUserContent.add(groupNameLabel, gbc.setGrid(1, 2).setSpan(1, 1).setWeight(0, 0));
                chooseUserContent.add(groupNameField, gbc.setGrid(2, 2).setWeight(1, 0));
                chooseUserContent.add(createButton,
                        gbc.setGrid(1, 3).setSpan(2, 1).setWeight(0, 0).setFill(GridBagConstraints.NONE));
    
                chooseUserDialog.setMinimumSize(new Dimension(300, 150));
                chooseUserDialog.setContentPane(chooseUserContent);
                chooseUserDialog.setTitle("Tạo group mới");
                chooseUserDialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
                chooseUserDialog.pack();
                chooseUserDialog.getRootPane().setDefaultButton(createButton);
                chooseUserDialog.setLocationRelativeTo(null);
                chooseUserDialog.setVisible(true);
                break;
            }
    
        
            case "file": {
                if (chattingRoom == -1)
                    break;
                JFileChooser jfc = new JFileChooser();
                jfc.setDialogTitle("Chọn file để gửi");
                int result = jfc.showDialog(null, "Chọn file");
                jfc.setVisible(true);
    
                if (result == JFileChooser.APPROVE_OPTION) {
                    String fileName = jfc.getSelectedFile().getName();
                    String filePath = jfc.getSelectedFile().getAbsolutePath();
    
                    Main.socketControl.sendFileToRoom(chattingRoom, fileName, filePath);
                }
            }
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Assuming you have a method in SocketControl to handle logout
                Main.socketControl.logout();

                for (RoomMessagesPanel roomMessagesPanel : roomMessagesPanels) {
                    roomMessagesPanel.clearMessages();
                }

                dispose();
                Connect connectServer = new Connect();
                connectServer.clearUserData();
                connectServer.setVisible(true);

            } catch (Exception e) {
                // Handle any IOException that may occur during logout
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi đăng xuất", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            return;
        }
    }

    private void handleWindowClosing() {

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát chương trình?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Assuming you have a method in SocketControl to handle logout
                Main.socketControl.logout();

                for (RoomMessagesPanel roomMessagesPanel : roomMessagesPanels) {
                    roomMessagesPanel.clearMessages();
                }

                dispose();

            } catch (Exception e) {
                // Handle any IOException that may occur during logout
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi đăng xuất", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            return;
        }
        // Optionally close the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dispose();
    }

    public static class RoomMessagesPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        public Room room;

        public RoomMessagesPanel(Room room) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBackground(Color.white);
            this.room = room;

        }


        public static RoomMessagesPanel findRoomMessagesPanel(List<RoomMessagesPanel> roomMessagesPanelList, int id) {
            for (RoomMessagesPanel roomMessagesPanel : roomMessagesPanelList) {
                if (roomMessagesPanel.room.id == id)
                    return roomMessagesPanel;
            }
            return null;
        }

        public void clearMessages() {
            removeAll();
            validate();
            repaint();
            room.messages.clear();
        }

        public void addNewMessageGUI(int roomID, ChatData messageData) {
            ChatPanel newMessagePanel = new ChatPanel(messageData);
            newMessagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // add(Box.createHorizontalGlue());
            add(newMessagePanel);

            validate();
            repaint();
        }

        public void addMessages(List<ChatData> messages) {
            for (ChatData message : messages) {
                addNewMessageGUI(room.id, message);
            }
        }
    }

    public static class TabComponent extends JPanel {
        private static final long serialVersionUID = 1L;

        public TabComponent(String tabTitle, ActionListener closeButtonListener) {
            JLabel titleLabel = new JLabel(tabTitle);
            JButton closeButton = new JButton(UIManager.getIcon("InternalFrame.closeIcon"));
            closeButton.addActionListener(closeButtonListener);
            closeButton.setPreferredSize(new Dimension(16, 16));

            this.setLayout(new FlowLayout());
            this.add(titleLabel);
            this.add(closeButton);
            this.setOpaque(false);
        }
    }
}
