import javax.crypto.SecretKey;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println("=== Secure Messaging Prototype ===");

            // Create the three users
            User user1 = new User("User 1");
            User user2 = new User("User 2");
            User user3 = new User("User 3");

            System.out.println("\nUsers created:");
            System.out.println(user1.getName());
            System.out.println(user2.getName());
            System.out.println(user3.getName());

            // User 1 creates a private message for User 2
            String originalMessage =
                    "Hello User 2, this is a private message from User 1.";

            System.out.println("\nSender: " + user1.getName());
            System.out.println("Receiver: " + user2.getName());
            System.out.println("Original message:");
            System.out.println(originalMessage);

            // Generate a new AES key for the message
            SecretKey aesKey =
                    EncryptionService.generateAESKey();

            // Encrypt the message using AES-GCM
            String encryptedMessage =
                    EncryptionService.encrypt(originalMessage, aesKey);

            // Protect the AES key using User 2's RSA public key
            String encryptedAESKey =
                    EncryptionService.encryptAESKey(
                            aesKey,
                            user2.getPublicKey()
                    );

            System.out.println("\n--- TRANSMISSION ---");

            System.out.println("\nEncrypted message:");
            System.out.println(encryptedMessage);

            System.out.println("\nEncrypted AES key:");
            System.out.println(encryptedAESKey);

            // User 2 recovers the AES key using their RSA private key
            SecretKey recoveredAESKey =
                    EncryptionService.decryptAESKey(
                            encryptedAESKey,
                            user2.getPrivateKey()
                    );

            // User 2 decrypts the message
            String decryptedMessage =
                    EncryptionService.decrypt(
                            encryptedMessage,
                            recoveredAESKey
                    );

            System.out.println("\n--- RECEIVER ---");
            System.out.println("Decrypted by: " + user2.getName());
            System.out.println("Decrypted message:");
            System.out.println(decryptedMessage);
// Security test: User 3 tries to decrypt User 2's AES key
System.out.println("\n--- WRONG RECEIVER TEST ---");
System.out.println(user3.getName()
        + " attempts to decrypt the message intended for "
        + user2.getName());

try {

    SecretKey wrongAESKey =
            EncryptionService.decryptAESKey(
                    encryptedAESKey,
                    user3.getPrivateKey()
            );

    String wrongDecryption =
            EncryptionService.decrypt(
                    encryptedMessage,
                    wrongAESKey
            );

    System.out.println("SECURITY FAILURE:");
    System.out.println(user3.getName()
            + " decrypted the message: "
            + wrongDecryption);

} catch (Exception e) {

    System.out.println("ACCESS DENIED");
    System.out.println(user3.getName()
            + " cannot decrypt the message intended for "
            + user2.getName());
}
// Second communication: User 1 sends a private message to User 3
System.out.println("\n======================================");
System.out.println("SECOND PRIVATE COMMUNICATION");
System.out.println("======================================");

String secondMessage =
        "Hello User 3, this is a private message from User 1.";

System.out.println("\nSender: " + user1.getName());
System.out.println("Receiver: " + user3.getName());
System.out.println("Original message:");
System.out.println(secondMessage);

// Generate a new AES key for the second message
SecretKey secondAESKey =
        EncryptionService.generateAESKey();

// Encrypt the second message using AES-GCM
String secondEncryptedMessage =
        EncryptionService.encrypt(
                secondMessage,
                secondAESKey
        );

// Protect the AES key using User 3's RSA public key
String secondEncryptedAESKey =
        EncryptionService.encryptAESKey(
                secondAESKey,
                user3.getPublicKey()
        );

System.out.println("\n--- TRANSMISSION ---");

System.out.println("\nEncrypted message:");
System.out.println(secondEncryptedMessage);

System.out.println("\nEncrypted AES key:");
System.out.println(secondEncryptedAESKey);

// User 3 recovers the AES key using their private key
SecretKey secondRecoveredAESKey =
        EncryptionService.decryptAESKey(
                secondEncryptedAESKey,
                user3.getPrivateKey()
        );

// User 3 decrypts the message
String secondDecryptedMessage =
        EncryptionService.decrypt(
                secondEncryptedMessage,
                secondRecoveredAESKey
        );

System.out.println("\n--- RECEIVER ---");
System.out.println("Decrypted by: " + user3.getName());
System.out.println("Decrypted message:");
System.out.println(secondDecryptedMessage);


// Security test: User 2 tries to decrypt User 3's message
System.out.println("\n--- WRONG RECEIVER TEST ---");
System.out.println(user2.getName()
        + " attempts to decrypt the message intended for "
        + user3.getName());

try {

    SecretKey wrongSecondAESKey =
            EncryptionService.decryptAESKey(
                    secondEncryptedAESKey,
                    user2.getPrivateKey()
            );

    String wrongSecondDecryption =
            EncryptionService.decrypt(
                    secondEncryptedMessage,
                    wrongSecondAESKey
            );

    System.out.println("SECURITY FAILURE:");
    System.out.println(user2.getName()
            + " decrypted the message: "
            + wrongSecondDecryption);

} catch (Exception e) {

    System.out.println("ACCESS DENIED");
    System.out.println(user2.getName()
            + " cannot decrypt the message intended for "
            + user3.getName());
}
// Tampering test: modify the encrypted message
System.out.println("\n======================================");
System.out.println("TAMPERING TEST");
System.out.println("======================================");

System.out.println("Simulating modification of encrypted data...");

try {

    // Change one character in the encrypted message
    char firstCharacter = encryptedMessage.charAt(0);

    char modifiedCharacter =
            firstCharacter == 'A' ? 'B' : 'A';

    String tamperedMessage =
            modifiedCharacter + encryptedMessage.substring(1);

    // Try to decrypt the modified ciphertext
    EncryptionService.decrypt(
            tamperedMessage,
            recoveredAESKey
    );

    System.out.println("SECURITY FAILURE:");
    System.out.println("Modified ciphertext was accepted.");

} catch (Exception e) {

    System.out.println("TAMPERING DETECTED");
    System.out.println(
            "AES-GCM rejected the modified encrypted message."
    );
}
        } catch (Exception e) {
            System.out.println("Encryption error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}