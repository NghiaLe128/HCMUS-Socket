package Server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Screen extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    JLabel titleLabel;
    JLabel ipLabel;
    JLabel portLabel;
    JTextField portText;
    JLabel serverNameLabel;
    JTextField serverNameText;

    static JTable clientTable;
    JButton openCloseButton;
    boolean isSocketOpened = false;
    JLabel serverStatusLabel;

    JLabel serverInfoLabel;

    public Screen() {
        JPanel mainContent = new JPanel(new BorderLayout());

        // Panel chứa tiêu đề và thông tin IP
        JPanel titlePanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Server Control");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.BLUE); // Màu chữ trắng
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        ipLabel = new JLabel("IP: "+ SocketControl.getThisIP());
        titlePanel.add(ipLabel, BorderLayout.CENTER);

        mainContent.add(titlePanel, BorderLayout.NORTH);

        // Panel chứa port, tên server và danh sách client
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        portLabel = new JLabel("Port: ");
        serverNameLabel = new JLabel("Tên server: ");

        portText = new JTextField(10);
        serverNameText = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(portLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(portText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(serverNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(serverNameText, gbc);

        mainContent.add(inputPanel, BorderLayout.CENTER);

        // Panel chứa trạng thái server và thông tin server
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        serverStatusLabel = new JLabel();
        serverInfoLabel = new JLabel();

        statusPanel.add(serverStatusLabel);
        statusPanel.add(serverInfoLabel);

        mainContent.add(statusPanel, BorderLayout.SOUTH);

        // Panel chứa bảng client
        JPanel tablePanel = new JPanel(new BorderLayout());
        clientTable = new JTable(new Object[][]{}, new String[]{"Tên client", "Port client"});
        JScrollPane clientScrollPane = new JScrollPane(clientTable);
        clientScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách client đang kết nối"));

        tablePanel.add(clientScrollPane, BorderLayout.CENTER);

        mainContent.add(tablePanel, BorderLayout.EAST);

        // Panel chứa nút mở/đóng server
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        openCloseButton = new JButton("Mở server");
        openCloseButton.addActionListener(this);
        buttonPanel.add(openCloseButton, BorderLayout.WEST);

        JPanel serverInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serverStatusLabel = new JLabel();
        serverInfoLabel = new JLabel();

        serverInfoPanel.add(serverStatusLabel);
        serverInfoPanel.add(serverInfoLabel);

        buttonPanel.add(serverInfoPanel, BorderLayout.CENTER);

        mainContent.add(buttonPanel, BorderLayout.SOUTH);

        // Thay đổi màu sắc và font chữ
        ipLabel.setForeground(new Color(30, 144, 255));
        portLabel.setForeground(new Color(30, 144, 255));
        serverNameLabel.setForeground(new Color(30, 144, 255));
        openCloseButton.setBackground(new Color(30, 144, 255));
        openCloseButton.setForeground(Color.white);

        // Font chữ
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        ipLabel.setFont(labelFont);
        portLabel.setFont(labelFont);
        serverNameLabel.setFont(labelFont);

        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);
        portText.setFont(textFieldFont);
        serverNameText.setFont(textFieldFont);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        openCloseButton.setFont(buttonFont);

        Font statusFont = new Font("Arial", Font.ITALIC, 14);
        serverStatusLabel.setFont(statusFont);
        serverInfoLabel.setFont(statusFont);


        this.setTitle("Server Chat");
        this.setContentPane(mainContent);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        Main.socketControl = new SocketControl();


        this.setTitle("Server Chat");
        this.setContentPane(mainContent);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        Main.socketControl = new SocketControl();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isSocketOpened) {
            try {
                if (serverNameText.getText().isEmpty())
                    JOptionPane.showMessageDialog(this, "Tên server không được trống", "Lỗi",
                            JOptionPane.WARNING_MESSAGE);
                else if (portText.getText().isEmpty())
                    JOptionPane.showMessageDialog(this, "Port không được trống", "Lỗi", JOptionPane.WARNING_MESSAGE);
                else {

                    Main.socketControl.serverName = serverNameText.getText();
                    Main.socketControl.serverPort = Integer.parseInt(portText.getText());

                    Main.socketControl.OpenSocket(Main.socketControl.serverPort);
                    isSocketOpened = true;
                    openCloseButton.setText("Đóng server");
                    serverStatusLabel.setText("Đang mở server");

                    // Hiển thị thông tin server đang mở
                    serverInfoLabel.setText("Địa chỉ IP: " + SocketControl.getThisIP() + ", Port: " + Main.socketControl.serverPort + " , Server: " + Main.socketControl.serverName);

                    // Vô hiệu hóa trường nhập liệu port và tên server
                    portText.setEditable(false);
                    serverNameText.setEditable(false);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Port phải là một số nguyên dương", "Lỗi",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {
            Main.socketControl.CloseSocket();
            isSocketOpened = false;
            openCloseButton.setText("Mở server");
            serverStatusLabel.setText("Đã đóng server");

            // Xóa thông tin server khi đóng server
            serverInfoLabel.setText("");

            // Kích hoạt lại trường nhập liệu port và tên server
            portText.setEditable(true);
            serverNameText.setEditable(true);
        }
    }
    public void updateClientTable() {
        Object[][] tableContent = new Object[Main.socketControl.connectedClient.size()][2];
        for (int i = 0; i < Main.socketControl.connectedClient.size(); i++) {
            tableContent[i][0] = Main.socketControl.connectedClient.get(i).userName;
            tableContent[i][1] = Main.socketControl.connectedClient.get(i).port;
        }

        clientTable.setModel(new DefaultTableModel(tableContent, new String[]{"Tên client", "Port client"}));
    }
}