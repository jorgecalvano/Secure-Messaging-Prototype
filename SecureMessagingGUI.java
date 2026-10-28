import javax.swing.*;
import java.awt.*;
import javax.crypto.SecretKey;

public class SecureMessagingGUI extends JFrame {

    private JComboBox<String> senderBox;
    private JComboBox<String> receiverBox;
    private JComboBox<String> decryptAsBox;

    private JTextArea messageArea;
    private JTextArea encryptedArea;
    private JTextArea decryptedArea;

    private JLabel statusLabel;

    private User user1;
    private User user2;
    private User user3;

    private String currentEncryptedAESKey;
    private String currentEncryptedMessage;
    private User currentReceiver;

    public SecureMessagingGUI() {

        // Create users and their RSA key pairs
        try {
            user1 = new User("User 1");
            user2 = new User("User 2");
            user3 = new User("User 3");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error creating users: " + e.getMessage()
            );
        }

        setTitle("Secure Messaging Prototype");
        setSize(750, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // ---------------- HEADER ----------------

        JLabel titleLabel = new JLabel(
            "SECURE MESSAGING PROTOTYPE",
            SwingConstants.CENTER
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 22)
        );

        JLabel subtitleLabel = new JLabel(
            "The Future of Messaging",
            SwingConstants.CENTER
        );

        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(
            new BoxLayout(headerPanel, BoxLayout.Y_AXIS)
        );

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ---------------- MAIN PANEL ----------------

        JPanel centrePanel = new JPanel();

        centrePanel.setLayout(
            new BoxLayout(centrePanel, BoxLayout.Y_AXIS)
        );

        String[] users = {
            "User 1",
            "User 2",
            "User 3"
        };

        senderBox = new JComboBox<>(users);
        receiverBox = new JComboBox<>(users);
        decryptAsBox = new JComboBox<>(users);

        senderBox.setSelectedItem("User 1");
        receiverBox.setSelectedItem("User 2");
        decryptAsBox.setSelectedItem("User 2");

        // Sender and Receiver
        JPanel userPanel =
            new JPanel(new GridLayout(2, 2, 10, 10));

        userPanel.add(new JLabel("Sender:"));
        userPanel.add(senderBox);

        userPanel.add(new JLabel("Receiver:"));
        userPanel.add(receiverBox);

        centrePanel.add(userPanel);
        centrePanel.add(Box.createVerticalStrut(15));

        // ---------------- MESSAGE ----------------

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centrePanel.add(messageLabel);

        messageArea = new JTextArea(4, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        centrePanel.add(
            new JScrollPane(messageArea)
        );

        centrePanel.add(
            Box.createVerticalStrut(10)
        );

        // ---------------- ENCRYPT BUTTON ----------------

        JButton sendButton =
            new JButton("ENCRYPT & SEND");

        sendButton.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        sendButton.addActionListener(e -> {

            try {

                String message =
                    messageArea.getText().trim();

                String senderName =
                    (String) senderBox.getSelectedItem();

                String receiverName =
                    (String) receiverBox.getSelectedItem();

                if (message.isEmpty()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Please enter a message."
                    );

                    return;
                }

                if (senderName.equals(receiverName)) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Sender and receiver must be different users."
                    );

                    return;
                }

                currentReceiver =
                    getUserByName(receiverName);

                // Generate a new AES key for this message
                SecretKey aesKey =
                    EncryptionService.generateAESKey();

                // AES-GCM encrypts the actual message
                currentEncryptedMessage =
                    EncryptionService.encrypt(
                        message,
                        aesKey
                    );

                // RSA protects the AES key
                // using the receiver's public key
                currentEncryptedAESKey =
                    EncryptionService.encryptAESKey(
                        aesKey,
                        currentReceiver.getPublicKey()
                    );

                encryptedArea.setText(
                    currentEncryptedMessage
                );

                decryptedArea.setText("");

                // Automatically select the intended receiver
                // as the default user attempting decryption
                decryptAsBox.setSelectedItem(
                    receiverName
                );

                statusLabel.setText(
                    "Status: Message encrypted for "
                    + receiverName
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Encryption error: "
                    + ex.getMessage()
                );
            }
        });

        centrePanel.add(sendButton);

        centrePanel.add(
            Box.createVerticalStrut(15)
        );

        // ---------------- ENCRYPTED MESSAGE ----------------

        JLabel encryptedLabel =
            new JLabel("Encrypted Message:");

        encryptedLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        centrePanel.add(encryptedLabel);

        encryptedArea = new JTextArea(5, 40);
        encryptedArea.setLineWrap(true);
        encryptedArea.setWrapStyleWord(true);
        encryptedArea.setEditable(false);

        centrePanel.add(
            new JScrollPane(encryptedArea)
        );

        centrePanel.add(
            Box.createVerticalStrut(15)
        );

        // ---------------- DECRYPT AS ----------------

        JPanel decryptUserPanel =
            new JPanel(new GridLayout(1, 2, 10, 10));

        decryptUserPanel.add(
            new JLabel("Decrypt as:")
        );

        decryptUserPanel.add(
            decryptAsBox
        );

        centrePanel.add(decryptUserPanel);

        centrePanel.add(
            Box.createVerticalStrut(10)
        );

        // ---------------- DECRYPT BUTTON ----------------

        JButton decryptButton =
            new JButton("DECRYPT MESSAGE");

        decryptButton.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        decryptButton.addActionListener(e -> {

            if (currentEncryptedMessage == null ||
                currentEncryptedAESKey == null ||
                currentReceiver == null) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please encrypt and send a message first."
                );

                return;
            }

            String selectedUserName =
                (String) decryptAsBox.getSelectedItem();

            User selectedUser =
                getUserByName(selectedUserName);

            try {

                /*
                 * The selected user tries to recover
                 * the AES key using their RSA private key.
                 */
                SecretKey recoveredAESKey =
                    EncryptionService.decryptAESKey(
                        currentEncryptedAESKey,
                        selectedUser.getPrivateKey()
                    );

                /*
                 * If the AES key is recovered successfully,
                 * AES-GCM decrypts the message.
                 */
                String decryptedMessage =
                    EncryptionService.decrypt(
                        currentEncryptedMessage,
                        recoveredAESKey
                    );

                decryptedArea.setText(
                    decryptedMessage
                );

                statusLabel.setText(
                    "Status: Message successfully decrypted by "
                    + selectedUserName
                );

            } catch (Exception ex) {

                decryptedArea.setText("");

                JOptionPane.showMessageDialog(
                    this,
                    "ACCESS DENIED\n"
                    + selectedUserName
                    + " cannot decrypt this message.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
                );

                statusLabel.setText(
                    "Status: ACCESS DENIED - "
                    + selectedUserName
                    + " is not the intended receiver"
                );
            }
        });

        centrePanel.add(decryptButton);

        centrePanel.add(
            Box.createVerticalStrut(15)
        );

        // ---------------- DECRYPTED MESSAGE ----------------

        JLabel decryptedLabel =
            new JLabel("Decrypted Message:");

        decryptedLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        centrePanel.add(decryptedLabel);

        decryptedArea = new JTextArea(3, 40);
        decryptedArea.setLineWrap(true);
        decryptedArea.setWrapStyleWord(true);
        decryptedArea.setEditable(false);

        centrePanel.add(
            new JScrollPane(decryptedArea)
        );

        centrePanel.add(
            Box.createVerticalStrut(15)
        );

        // ---------------- STATUS ----------------

        statusLabel =
            new JLabel("Status: Ready");

        statusLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        centrePanel.add(statusLabel);

        mainPanel.add(
            centrePanel,
            BorderLayout.CENTER
        );

        add(mainPanel);

        setVisible(true);
    }

    // Returns the correct User object
    private User getUserByName(String name) {

        if (name.equals("User 1")) {
            return user1;

        } else if (name.equals("User 2")) {
            return user2;

        } else if (name.equals("User 3")) {
            return user3;
        }

        return null;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
            () -> new SecureMessagingGUI()
        );
    }
}