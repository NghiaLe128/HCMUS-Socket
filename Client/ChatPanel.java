import java.awt.*;

import javax.swing.*;


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

}
