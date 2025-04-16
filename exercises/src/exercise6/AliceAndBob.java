package exercise6;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hedy Huang
 * @version 1.0
 */

public class AliceAndBob {

    // Simulate two parties: Alice and Bob
    static class Person {
        String name;
        SecretKey symmetricKey; // For AES
        KeyPair asymmetricKeyPair; // For RSA
        Map<String, PublicKey> otherPublicKeys = new HashMap<>();

        public Person(String name) {
            this.name = name;
        }

        public void generateSymmetricKey() throws NoSuchAlgorithmException {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // AES-256
            this.symmetricKey = keyGen.generateKey();
        }

        public void generateAsymmetricKeys() throws NoSuchAlgorithmException {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048); // RSA-2048
            this.asymmetricKeyPair = keyPairGen.generateKeyPair();
        }

        public void sharePublicKeyWith(Person other) {
            other.otherPublicKeys.put(this.name, this.asymmetricKeyPair.getPublic());
        }

        // Symmetric encryption
        public byte[] encryptWithSymmetricKey(String message) throws Exception {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12]; // 96-bit IV for GCM
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128-bit auth tag

            cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, spec);
            byte[] encrypted = cipher.doFinal(message.getBytes());

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return combined;
        }

        public String decryptWithSymmetricKey(byte[] encryptedData) throws Exception {
            // Extract IV
            byte[] iv = new byte[12];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);

            // Extract encrypted message
            byte[] encryptedMessage = new byte[encryptedData.length - iv.length];
            System.arraycopy(encryptedData, iv.length, encryptedMessage, 0, encryptedMessage.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, symmetricKey, spec);

            byte[] decrypted = cipher.doFinal(encryptedMessage);
            return new String(decrypted);
        }

        // Asymmetric encryption
        public byte[] encryptWithAsymmetricKey(String message, PublicKey publicKey) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(message.getBytes());
        }

        public String decryptWithAsymmetricKey(byte[] encryptedData) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, asymmetricKeyPair.getPrivate());
            byte[] decrypted = cipher.doFinal(encryptedData);
            return new String(decrypted);
        }

        // Digital signature
        public byte[] signMessage(String message) throws Exception {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(asymmetricKeyPair.getPrivate());
            signature.update(message.getBytes());
            return signature.sign();
        }

        public boolean verifySignature(String message, byte[] signatureBytes, PublicKey publicKey) throws Exception {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());
            return signature.verify(signatureBytes);
        }
    }

    public static void main(String[] args) throws Exception {
        // Create Alice and Bob
        Person alice = new Person("Alice");
        Person bob = new Person("Bob");

        // Generate keys
        alice.generateSymmetricKey();
        bob.generateSymmetricKey();
        alice.generateAsymmetricKeys();
        bob.generateAsymmetricKeys();

        // Exchange public keys
        alice.sharePublicKeyWith(bob);
        bob.sharePublicKeyWith(alice);

        System.out.println("=== Symmetric Encryption (AES-256/GCM) ===");
        // Alice shares her symmetric key with Bob
        bob.symmetricKey = alice.symmetricKey;

        String secretMessage = "Hello Bob, this is Alice.(Symmetric)";
        System.out.println("Original message: " + secretMessage);

        // Alice encrypts the message
        byte[] encryptedMessage = alice.encryptWithSymmetricKey(secretMessage);
        System.out.println("Encrypted message: " + Base64.getEncoder().encodeToString(encryptedMessage));

        // Bob decrypts the message
        String decryptedMessage = bob.decryptWithSymmetricKey(encryptedMessage);
        System.out.println("Decrypted message: " + decryptedMessage);

        System.out.println("\n=== Asymmetric Encryption (RSA-2048) ===");
        String publicKeyMessage = "Hello Bob, this is Alice.(Asymmetric)";
        System.out.println("Original message: " + publicKeyMessage);

        // Alice encrypts with Bob's public key
        PublicKey bobPublicKey = alice.otherPublicKeys.get("Bob");
        byte[] rsaEncrypted = alice.encryptWithAsymmetricKey(publicKeyMessage, bobPublicKey);
        System.out.println("Encrypted message: " + Base64.getEncoder().encodeToString(rsaEncrypted));

        // Bob decrypts with his private key
        String rsaDecrypted = bob.decryptWithAsymmetricKey(rsaEncrypted);
        System.out.println("Decrypted message: " + rsaDecrypted);

        System.out.println("\n=== Digital Signatures (RSA-2048) ===");
        String messageToSign = "Signed by Alice";
        System.out.println("Signed message: " + messageToSign);

        // Alice signs the message
        byte[] signature = alice.signMessage(messageToSign);
        System.out.println("Signature: " + Base64.getEncoder().encodeToString(signature));

        // Bob verifies the signature
        PublicKey alicePublicKey = bob.otherPublicKeys.get("Alice");
        boolean isValid = bob.verifySignature(messageToSign, signature, alicePublicKey);
        System.out.println("Signature valid? " + isValid);
    }
}

