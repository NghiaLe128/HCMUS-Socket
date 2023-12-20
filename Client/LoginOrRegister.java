import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginOrRegister extends JFrame implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private DataServer connectedServer;

    public LoginOrRegister(DataServer connectedServer) {
        this.connectedServer = connectedServer;
        setTitle("Đăng nhập/Đăng ký");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeUI();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
    
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(125, 229, 251));
        JLabel titleLabel = new JLabel("Chào mừng bạn đến với hệ thống!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titlePanel.add(titleLabel);
    
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JLabel usernameLabel = new JLabel("Tên người dùng:");
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // Increased font size
        formPanel.add(usernameLabel);
    
        usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(usernameField);
    
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // Increased font size
        formPanel.add(passwordLabel);
    
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(passwordField);
    
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        registerButton = new JButton("Đăng ký");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));
    
        ActionListener actionListener = new LoginOrRegisterListener();
        loginButton.addActionListener(actionListener);
        registerButton.addActionListener(actionListener);
    
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
    
        // Adding components to the main panel
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    
        // Customize background color
        panel.setBackground(new Color(255, 255, 255));
    
        add(panel);
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    private class LoginOrRegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Đăng nhập":
                    performLogin();
                    break;
                case "Đăng ký":
                    performRegistration();
                    break;
            }
        }
    }
    
    private void performLogin() {
        if(usernameField.getText().isEmpty() || new String(passwordField.getPassword()).isEmpty()){
            JOptionPane.showMessageDialog(loginButton, "Tên người dùng hoặc mật khẩu không được để trống");
        }
        else{
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            Main.socketControl = new SocketControl(username, password, connectedServer);
            Main.socketControl.Login();
            dispose();
        }

    }

    private void performRegistration() {
        RegistrationWindow registration = new RegistrationWindow();
        registration.setVisible(true);
    }

    private class RegistrationWindow extends JFrame implements ActionListener {
        private JTextField fullNameField;
        private JTextField emailField;
        private JTextField phoneField;
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JPasswordField confirmPasswordField;

        private JButton registerButton;
        private JButton backButton;

        public RegistrationWindow() {
            setTitle("Đăng ký");
            setSize(300, 400); // Adjusted size for a vertical layout
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            initializeRegistrationUI();

            setLocationRelativeTo(LoginOrRegister.this);
        }

        private void initializeRegistrationUI() {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            // Title Panel for Registration Window
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(125, 229, 251));
            JLabel titleLabel = new JLabel("Đăng ký tài khoản mới");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            titlePanel.add(titleLabel);

            // Form Panel
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridLayout(6, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Adding form fields
            formPanel.add(new JLabel("Họ tên:"));
            fullNameField = new JTextField();
            formPanel.add(fullNameField);

            formPanel.add(new JLabel("Email:"));
            emailField = new JTextField();
            formPanel.add(emailField);

            formPanel.add(new JLabel("Số điện thoại:"));
            phoneField = new JTextField();
            formPanel.add(phoneField);

            formPanel.add(new JLabel("Tên người dùng:"));
            usernameField = new JTextField();
            formPanel.add(usernameField);

            formPanel.add(new JLabel("Mật khẩu:"));
            passwordField = new JPasswordField();
            formPanel.add(passwordField);

            formPanel.add(new JLabel("Xác nhận mật khẩu:"));
            confirmPasswordField = new JPasswordField();
            formPanel.add(confirmPasswordField);

            // Button Panel
            JPanel buttonPanel = new JPanel();
            registerButton = new JButton("Đăng ký");
            backButton = new JButton("Quay lại");

            ActionListener registrationListener = this;
            registerButton.addActionListener(registrationListener);
            backButton.addActionListener(registrationListener);

            buttonPanel.add(registerButton);
            buttonPanel.add(backButton);

            // Adding components to the main panel
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(formPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            // Customize background color
            panel.setBackground(new Color(255, 255, 255));

            add(panel);
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == registerButton) {
                performRegistration();
            } else if (e.getSource() == backButton) {
                this.dispose();
            }
        }

        private void performRegistration() {
            String fullName = fullNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerButton, "Mật khẩu và xác nhận mật khẩu không khớp");
                return;
            }

            if (validateRegistration(fullName, email, phone, username, password)) {
                Main.socketControl = new SocketControl(fullName, email, phone, username, password, connectedServer);
                Main.socketControl.Register();
                JOptionPane.showMessageDialog(registerButton, "Đăng ký thành công");
                dispose();
            } else {
                JOptionPane.showMessageDialog(registerButton, "Đăng ký thất bại. Vui lòng kiểm tra lại thông tin.");
            }
        }

        private boolean validateRegistration(String fullName, String email, String phone, String username, String password) {
            // Kiểm tra các điều kiện hợp lệ, ví dụ: không để trống thông tin, kiểm tra định dạng email, ...
            return !fullName.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !username.isEmpty() && !password.isEmpty();
        }

    }

}
