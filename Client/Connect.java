import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class Connect extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private DataServer connectedServer;
    private JTable serverTable;
    private List<DataServer> serverList;
    private JLabel titleLabel;

    public Connect() {
        JPanel mainContent = new JPanel(new BorderLayout());
        this.setContentPane(mainContent);

        titleLabel = new JLabel("Client Control");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.BLUE);

        mainContent.add(titleLabel, BorderLayout.NORTH);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0, 191, 255));
        refreshButton.setActionCommand("refresh");
        refreshButton.addActionListener(this);

        serverTable = new JTable();
        serverTable.setRowHeight(30);
        serverTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3) {
                    c.setForeground(value.toString().equals("Hoạt động") ? Color.green : Color.red);
                    c.setFont(new Font("SansSerif", Font.BOLD, 14));
                } else
                    c.setForeground(Color.black);

                return c;
            }
        });

        serverList = FileServer.getServerList();
        updateServerTable();

        JScrollPane serverScrollPane = new JScrollPane(serverTable);
        serverScrollPane.setBorder(BorderFactory.createTitledBorder("Server List"));

        JButton joinButton = new JButton("Join Server");
        joinButton.setBackground(new Color(0, 191, 255));
        joinButton.addActionListener(this);
        joinButton.setActionCommand("join");

        JButton addButton = new JButton("Add Server");
        addButton.setBackground(new Color(0, 191, 255));
        addButton.addActionListener(this);
        addButton.setActionCommand("add");

        JButton deleteButton = new JButton("Delete Server");
        deleteButton.setBackground(new Color(0, 191, 255));
        deleteButton.addActionListener(this);
        deleteButton.setActionCommand("delete");

        JButton editButton = new JButton("Edit Server");
        editButton.setBackground(new Color(0, 191, 255));
        editButton.addActionListener(this);
        editButton.setActionCommand("edit");

        JPanel buttonPanelRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanelRow1.add(joinButton);
        buttonPanelRow1.add(refreshButton);

        JPanel buttonPanelRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanelRow2.add(addButton);
        buttonPanelRow2.add(editButton);
        buttonPanelRow2.add(deleteButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(buttonPanelRow1);
        buttonPanel.add(buttonPanelRow2);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        mainContent.add(serverScrollPane, BorderLayout.CENTER);
        mainContent.add(bottomPanel, BorderLayout.SOUTH);

        this.setTitle("Chat Application");
        this.setPreferredSize(new Dimension(900, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    public void clearUserData() {
        connectedServer = null; 
        this.setVisible(false);
    }


    JTextField nameText;

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            // Inside the actionPerformed method in the Connect class
            case "join": {
                if (serverTable.getSelectedRow() == -1)
                    break;
                String serverState = serverTable.getValueAt(serverTable.getSelectedRow(), 3).toString();
                if (serverState.equals("Không hoạt động")) {
                    JOptionPane.showMessageDialog(this, "Server không hoạt động", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                }

                // Get server details
                String selectedIP = serverTable.getValueAt(serverTable.getSelectedRow(), 1).toString();
                int selectedPort = Integer.parseInt(serverTable.getValueAt(serverTable.getSelectedRow(), 2).toString());
                DataServer selectedServer = serverList.stream()
                        .filter(x -> x.ip.equals(selectedIP) && x.port == selectedPort).findAny().orElse(null);

                // Open the login/register form
                new LoginOrRegister(selectedServer);
                break;
            }

			case "add": {
				JDialog addServerDialog = new JDialog();
			
				JLabel ipLabel = new JLabel("IP");
				JLabel portLabel = new JLabel("Port");
				JTextField ipText = new JTextField();
				JTextField portText = new JTextField();
				JButton addServerButton = new JButton("Thêm");
				addServerButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							int port = Integer.parseInt(portText.getText());
							String ip = ipText.getText();
							String serverName = SocketControl.serverName(ip, port);
			
							if (serverList == null)
								serverList = new ArrayList<DataServer>();
							serverList.add(new DataServer(serverName, ip, port, false, 0));
			
							FileServer.setServerList(serverList);
							updateServerTable();
			
							addServerDialog.setVisible(false);
							addServerDialog.dispose();
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(addServerDialog, "Port phải là 1 số nguyên dương", "Thông báo",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				});
			
				GridBagConstraints gbc = new GridBagConstraints();
			
				JPanel addServerContent = new JPanel(new GridBagLayout());
				
				gbc.gridx = 1;
				gbc.gridy = 2;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 0;
				gbc.weighty = 0;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new Insets(5, 5, 5, 5);
				addServerContent.add(ipLabel, gbc);
			
				gbc.gridx = 2;
				gbc.gridy = 2;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 1;
				gbc.weighty = 0;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new Insets(5, 5, 5, 5);
				addServerContent.add(ipText, gbc);
			
				gbc.gridx = 1;
				gbc.gridy = 3;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 0;
				gbc.weighty = 0;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new Insets(5, 5, 5, 5);
				addServerContent.add(portLabel, gbc);
			
				gbc.gridx = 2;
				gbc.gridy = 3;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 1;
				gbc.weighty = 0;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new Insets(5, 5, 5, 5);
				addServerContent.add(portText, gbc);
			
				gbc.gridx = 1;
				gbc.gridy = 4;
				gbc.gridwidth = 2;
				gbc.gridheight = 1;
				gbc.weightx = 0;
				gbc.weighty = 0;
				gbc.fill = GridBagConstraints.NONE;
				addServerContent.add(addServerButton, gbc);
			
				addServerDialog.setContentPane(addServerContent);
				addServerDialog.setTitle("Nhập port của server");
				addServerDialog.getRootPane().setDefaultButton(addServerButton);
				addServerDialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
				addServerDialog.pack();
				addServerDialog.setLocationRelativeTo(null);
				addServerDialog.setVisible(true);
			
				break;
			}
			
            case "delete": {
                if (serverTable.getSelectedRow() == -1)
                    break;

                String selectedIP = serverTable.getValueAt(serverTable.getSelectedRow(), 1).toString();
                int selectedPort = Integer.parseInt(serverTable.getValueAt(serverTable.getSelectedRow(), 2).toString());
                for (int i = 0; i < serverList.size(); i++) {
                    if (serverList.get(i).ip.equals(selectedIP) && serverList.get(i).port == selectedPort) {
                        serverList.remove(i);
                        break;
                    }
                }
                FileServer.setServerList(serverList);
                updateServerTable();
                break;
            }
            case "edit": {
                if (serverTable.getSelectedRow() == -1)
                    break;

                String selectedIP = serverTable.getValueAt(serverTable.getSelectedRow(), 1).toString();
                int selectedPort = Integer.parseInt(serverTable.getValueAt(serverTable.getSelectedRow(), 2).toString());
                DataServer editingServer = serverList.stream()
                        .filter(x -> x.ip.equals(selectedIP) && x.port == selectedPort).findAny().orElse(null);

                JDialog editDialog = new JDialog();

                JLabel ipLabel = new JLabel("IP");
                JTextField ipText = new JTextField(editingServer.ip);
                JLabel portLabel = new JLabel("Port");
                JTextField portText = new JTextField("" + editingServer.port);
                portText.setPreferredSize(new Dimension(150, 20));
                JButton editButton = new JButton("Sửa");
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            int newPort = Integer.parseInt(portText.getText());
                            String newIP = ipText.getText();

                            editingServer.port = newPort;
                            editingServer.ip = newIP;

                            FileServer.setServerList(serverList);

                            updateServerTable();

                            editDialog.setVisible(false);
                            editDialog.dispose();

                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(editDialog, "Port phải là 1 số nguyên dương", "Thông báo",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });

                JPanel editContent = new JPanel(new GridBagLayout());
                GBCBuild gbc = new GBCBuild(1, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 0).setInsets(5);

                editContent.add(portLabel, gbc.setGrid(1, 2));
                editContent.add(portText, gbc.setGrid(2, 2));
                editContent.add(ipLabel, gbc.setGrid(1, 3));
                editContent.add(ipText, gbc.setGrid(2, 3));
                editContent.add(editButton, new GBCBuild(1, 4).setSpan(2, 1));

                editDialog.setTitle("Chỉnh sửa thông tin server");
                editDialog.setContentPane(editContent);
                editDialog.getRootPane().setDefaultButton(editButton);
                editDialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
                editDialog.setLocationRelativeTo(null);
                editDialog.pack();
                editDialog.setVisible(true);
                break;
            }

            case "refresh": {
                updateServerTable();
                break;
            }
        }
    }

    public void loginResultAction(String result) {
        if (result.equals("success")) {
            clearUserData();

            String selectedIP = serverTable.getValueAt(serverTable.getSelectedRow(), 1).toString();
            int selectedPort = Integer.parseInt(serverTable.getValueAt(serverTable.getSelectedRow(), 2).toString());
            connectedServer = serverList.stream().filter(x -> x.ip.equals(selectedIP) && x.port == selectedPort)
                    .findAny().orElse(null);

            this.setVisible(false);
            this.dispose();
            Main.mainScreen = new Screen();

        } else if (result.equals("existed"))
            JOptionPane.showMessageDialog(this, "Username đã tồn tại", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        else if(result.equals("wrong"))
            JOptionPane.showMessageDialog(this, "Username hoặc Password không đúng", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        else if (result.equals("closed"))
            JOptionPane.showMessageDialog(this, "Server đã đóng", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

    }

    public void registerResultAction(String result) {
        if (result.equals("success")) {

            String selectedIP = serverTable.getValueAt(serverTable.getSelectedRow(), 1).toString();
            int selectedPort = Integer.parseInt(serverTable.getValueAt(serverTable.getSelectedRow(), 2).toString());
            connectedServer = serverList.stream().filter(x -> x.ip.equals(selectedIP) && x.port == selectedPort)
                    .findAny().orElse(null);

            this.setVisible(false);
            this.dispose();

        } else if (result.equals("existed"))
            JOptionPane.showMessageDialog(this, "Username đã tồn tại", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        else if (result.equals("closed"))
            JOptionPane.showMessageDialog(this, "Server đã đóng", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

    }

    public void showConnectScreen() {
        this.setVisible(true);
        clearUserData(); // Phương thức bạn đã tạo để xóa dữ liệu khi đăng xuất
    }

    public void updateServerTable() {
        if (serverList == null)
            return;
        for (DataServer dataServer : serverList) {
            dataServer.isOpen = SocketControl.serverOnline(dataServer.ip, dataServer.port);
            if (dataServer.isOpen) {
                dataServer.Name = SocketControl.serverName(dataServer.ip, dataServer.port);
                dataServer.connectAccountCount = SocketControl.serverConnectedAccountCount(dataServer.ip,
                        dataServer.port);
            }
        }

        serverTable.setModel(new DefaultTableModel(FileServer.getServerObjectMatrix(serverList), new String[] {
                "Tên server", "IP server", "Port server", "Trạng thái", "Số user online" }) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int arg0, int arg1) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

        });

        serverTable.setGridColor(Color.BLACK);

        // Set grid lines visibility
        serverTable.setShowGrid(true);
        serverTable.setShowHorizontalLines(true);
        serverTable.setShowVerticalLines(true);

        JTableHeader header = serverTable.getTableHeader();
        header.setForeground(new Color(30, 144, 255));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
    }
}
