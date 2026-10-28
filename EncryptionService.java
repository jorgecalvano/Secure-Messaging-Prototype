import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionService {

    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // Generate a random AES key
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // Encrypt a message using AES-GCM
    public static String encrypt(String message, SecretKey key) throws Exception {

        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec parameterSpec =
                new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] encryptedMessage =
                cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedMessageWithIv =
                new byte[iv.length + encryptedMessage.length];

        System.arraycopy(
                iv, 0,
                encryptedMessageWithIv, 0,
                iv.length
        );

        System.arraycopy(
                encryptedMessage, 0,
                encryptedMessageWithIv, iv.length,
                encryptedMessage.length
        );

        return Base64.getEncoder()
                .encodeToString(encryptedMessageWithIv);
    }

    // Decrypt an AES-GCM encrypted message
    public static String decrypt(String encryptedMessage, SecretKey key)
            throws Exception {

        byte[] decodedMessage =
                Base64.getDecoder().decode(encryptedMessage);

        byte[] iv = new byte[GCM_IV_LENGTH];

        System.arraycopy(
                decodedMessage, 0,
                iv, 0,
                GCM_IV_LENGTH
        );

        byte[] ciphertext =
                new byte[decodedMessage.length - GCM_IV_LENGTH];

        System.arraycopy(
                decodedMessage, GCM_IV_LENGTH,
                ciphertext, 0,
                ciphertext.length
        );

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec parameterSpec =
                new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] decryptedMessage =
                cipher.doFinal(ciphertext);

        return new String(
                decryptedMessage,
                StandardCharsets.UTF_8
        );
    }
    // Encrypt the AES key using the receiver's RSA public key
public static String encryptAESKey(
        SecretKey aesKey,
        java.security.PublicKey publicKey) throws Exception {

    Cipher rsaCipher =
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

    rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

    byte[] encryptedKey =
            rsaCipher.doFinal(aesKey.getEncoded());

    return Base64.getEncoder().encodeToString(encryptedKey);
}


// Decrypt the AES key using the receiver's RSA private key
public static SecretKey decryptAESKey(
        String encryptedAESKey,
        java.security.PrivateKey privateKey) throws Exception {

    Cipher rsaCipher =
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

    rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

    byte[] encryptedKeyBytes =
            Base64.getDecoder().decode(encryptedAESKey);

    byte[] decryptedKey =
            rsaCipher.doFinal(encryptedKeyBytes);

    return new javax.crypto.spec.SecretKeySpec(
            decryptedKey,
            "AES"
    );
}
} 