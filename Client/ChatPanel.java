import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class ChatPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    public ChatData data;

    public ChatPanel(ChatData data) {
        this.data = data;
        Dimension thisMaxSize = this.getMaximumSize();

        JLabel senderLabel = new JLabel((data.whoSend.equals(Main.socketControl.userName) ? "Bạn" : data.whoSend) + ": ");
        senderLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        JLabel senderOrLabel = new JLabel(" :" + (data.whoSend.equals(Main.socketControl.userName) ? "Bạn" : data.whoSend));
        senderOrLabel.setFont(new Font("Dialog", Font.BOLD, 15));

        JPanel contentPanelLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel contentPanelRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        contentPanelLeft.setBorder(getBorder());
        contentPanelRight.setBorder(getBorder());

        contentPanelLeft.setBackground(Color.WHITE);
        contentPanelRight.setBackground(new Color(173, 216, 230));

        if (data.type.equals("notify")) {
            JTextArea textContent = new JTextArea(data.content);
            textContent.setFont(new Font("Dialog", Font.ITALIC, 15));
            textContent.setForeground(Color.red);
            textContent.setEditable(false);

            textContent.setBorder(null);

            contentPanelLeft.setBackground(Color.white);
            contentPanelLeft.add(textContent);
            contentPanelRight.setBackground(Color.white);
            contentPanelRight.add(textContent);
            this.setMaximumSize(new Dimension(thisMaxSize.width, 30));

        } else if (data.type.equals("text")) {
            JTextArea textContent = new JTextArea(data.content);
            textContent.setFont(new Font("Dialog", Font.PLAIN, 15));
            textContent.setEditable(false);

            textContent.setBorder(null);

            if (data.whoSend.equals(Main.socketControl.userName)) {
                textContent.setBackground(new Color(173, 216, 230));
                contentPanelRight.add(Box.createHorizontalGlue());
                contentPanelRight.add(textContent);
            } else {
                textContent.setBackground(Color.white);
                contentPanelLeft.add(textContent);
                senderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            }

            int lineCount = data.content.split("\r\n|\r|\n").length;
            if (lineCount > 1) {
                this.setMaximumSize(new Dimension(thisMaxSize.width, 19 * lineCount));
            } else {
                this.setMaximumSize(new Dimension(thisMaxSize.width, 30));
            }
        } else if (data.type.equals("file")) {
            JPanel contentPanelLeftFile = createFilePanel(data.content, FlowLayout.LEFT);
            JPanel contentPanelRightFile = createFilePanel(data.content, FlowLayout.RIGHT);

            this.setMaximumSize(new Dimension(thisMaxSize.width, 30));

            if (data.whoSend.equals(Main.socketControl.userName)) {
                //contentPanelRight.add(Box.createHorizontalGlue());
                contentPanelRight.add(contentPanelRightFile);
            } else {
                contentPanelLeft.add(Box.createHorizontalGlue());
                contentPanelLeft.add(contentPanelLeftFile);
                senderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        }

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        senderLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        senderOrLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        contentPanelRight.setAlignmentY(Component.TOP_ALIGNMENT);
        contentPanelLeft.setAlignmentY(Component.TOP_ALIGNMENT);

        if (data.whoSend.equals(Main.socketControl.userName)) {
            this.add(Box.createHorizontalGlue());
            this.add(contentPanelRight);
            this.add(senderOrLabel);
        } else {
            this.add(senderLabel);
            this.add(contentPanelLeft);
            this.add(Box.createHorizontalGlue());
        }

        this.setBorder(null);
        this.setBackground(null);
    }

    private JPanel createFilePanel(String fileName, int alignment) {
        JPanel contentPanelFile = new JPanel(new FlowLayout(alignment));
        contentPanelFile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        contentPanelFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                downloadFileLogic(fileName); // Pass the data.content directly
            }
        });

       JLabel fileIcon = new JLabel();
			try {
				String extension = data.content.split("\\.")[1];
				Random r = new Random();
				File tempFile = new File("temp" + r.nextInt() + r.nextInt() + r.nextInt() + "." + extension);
				tempFile.createNewFile();
				fileIcon.setIcon(FileSystemView.getFileSystemView().getSystemIcon(tempFile));
				tempFile.delete();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

        JLabel fileNameLabel = new JLabel("<HTML><U>" + fileName + "</U></HTML>");
        fileNameLabel.setFont(new Font("Dialog", Font.PLAIN, 15));

        contentPanelFile.add(fileIcon);
        contentPanelFile.add(fileNameLabel);

        return contentPanelFile;
    }

    private void downloadFileLogic(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Chọn đường dẫn download");
        jfc.setFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + " files", extension));
        jfc.setSelectedFile(new File(fileName));
        int result = jfc.showSaveDialog(this);
        jfc.setVisible(true);
    
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.endsWith("." + extension)) {
                filePath += "." + extension;
            }
    
            Room room = Room.findRoom(Main.socketControl.allRooms, Screen.chattingRoom);
            int messageIndex = -1;
            for (int i = 0; i < room.messages.size(); i++) {
                if (room.messages.get(i) == data) {
                    messageIndex = i;
                    break;
                }
            }
            Main.socketControl.downloadFile(room.id, messageIndex, fileName, filePath);
        }
    }
    
    public ChatData getMessage() {
        return this.data;
    }
}
