import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class Connect extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JTable serverTable;
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

        JScrollPane serverScrollPane = new JScrollPane(serverTable);
        serverScrollPane.setBorder(BorderFactory.createTitledBorder("Server List"));

        JButton joinButton = new JButton("Join Server");
        joinButton.addActionListener(this);
        joinButton.setActionCommand("join");

        JButton addButton = new JButton("Add Server");
        addButton.addActionListener(this);
        addButton.setActionCommand("add");

        JButton deleteButton = new JButton("Delete Server");
        deleteButton.addActionListener(this);
        deleteButton.setActionCommand("delete");

        JButton editButton = new JButton("Edit Server");
        editButton.addActionListener(this);
        editButton.setActionCommand("edit");

        JPanel buttonPanelRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanelRow1.add(joinButton);
        buttonPanelRow1.add(refreshButton);

        JPanel buttonPanelRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanelRow2.add(addButton);
        buttonPanelRow2.add(editButton);
        buttonPanelRow2.add(deleteButton);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(buttonPanelRow1);
        buttonPanel.add(buttonPanelRow2);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        mainContent.add(serverScrollPane, BorderLayout.CENTER);
        mainContent.add(bottomPanel, BorderLayout.SOUTH);

        this.setTitle("Chat Application");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

   

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            // Inside the actionPerformed method in the Connect class
            case "join": {
                // join
                break;
            }

			case "add": {
				//add
                break;
            }

            case "refresh": {
                break;
            }
        }
    }

}
