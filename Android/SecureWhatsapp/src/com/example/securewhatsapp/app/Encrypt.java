package com.example.securewhatsapp.app;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt{

    public static KeyPair GeneratePubPrivateKey(int keyBits) throws NoSuchAlgorithmException{
        // generate an RSA key
        System.out.println( "\nStart generating RSA key" );
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keyBits);
        KeyPair key = keyGen.generateKeyPair();
        System.out.println( "Finish generating RSA key" );
        System.out.println( "----------------------------------" );
        return key;
    }

    public static byte [][] EncryptMessageGroup(byte[] toBeEncrypted, PublicKey [] ReceiverPublicKey, PrivateKey senderPrivateKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
        byte[] plainText = toBeEncrypted;

        //calculating message digest
        MessageDigest sha2 = MessageDigest.getInstance("SHA-512");
        byte[] digest = sha2.digest(plainText);

        // Signing message digest
        Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher1.init(Cipher.ENCRYPT_MODE, senderPrivateKey);
        byte[] signedDigest = cipher1.doFinal(digest);


        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey symKey = generator.generateKey();
        Cipher cipherSym = Cipher.getInstance("AES");
        cipherSym.init(Cipher.ENCRYPT_MODE, symKey);
        byte[] cipherTextTemp = cipherSym.doFinal(plainText);
        String cipherTextTempString = new String(cipherTextTemp);
        System.out.println(cipherTextTempString);
        byte[] symKeyBytes = symKey.getEncoded();
        System.out.println("Original key string: "+new String(symKeyBytes));
        //
        // get an RSA cipher object
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // encrypt the SecretKey using the receiver public key
        System.out.println( "\nStart encryption" );
        byte [][] result = null;
        for(int i=0;i<ReceiverPublicKey.length;i++){
            cipher.init(Cipher.ENCRYPT_MODE, ReceiverPublicKey[i]);
            byte[] cipherSymKeyBytes = cipher.doFinal(symKeyBytes);
            String cipherSymKeyString = new String(cipherSymKeyBytes);
            System.out.println( "Finish encryption: " );
            System.out.println( "Cipher Sym Key encrypted with public key "+ cipherSymKeyString);
            result[i] = cipherSymKeyBytes;
        }
        result[ReceiverPublicKey.length] = cipherTextTemp;
        result[ReceiverPublicKey.length + 1] = signedDigest;
        return result;
    }


    public static byte [][] EncryptMessage(byte[] toBeEncrypted, PublicKey ReceiverPublicKey, PrivateKey senderPrivateKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
        byte[] plainText = toBeEncrypted;

        //calculating message digest
        MessageDigest sha1 = MessageDigest.getInstance("SHA-512");
        byte[] digest = sha1.digest(plainText);

        // Signing message digest
        Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher1.init(Cipher.ENCRYPT_MODE, senderPrivateKey);
        byte[] signedDigest = cipher1.doFinal(digest);


        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey symKey = generator.generateKey();
        Cipher cipherSym = Cipher.getInstance("AES");
        cipherSym.init(Cipher.ENCRYPT_MODE, symKey);
        byte[] cipherTextTemp = cipherSym.doFinal(plainText);
        String cipherTextTempString = new String(cipherTextTemp);
        System.out.println(cipherTextTempString);
        byte[] symKeyBytes = symKey.getEncoded();
        System.out.println("Original key string: "+new String(symKeyBytes));
        //
        // get an RSA cipher object
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // encrypt the SecretKey using the receiver public key
        System.out.println( "\nStart encryption" );
        cipher.init(Cipher.ENCRYPT_MODE, ReceiverPublicKey);
        byte[] cipherSymKeyBytes = cipher.doFinal(symKeyBytes);
        String cipherSymKeyString = new String(cipherSymKeyBytes);
        System.out.println( "Finish encryption: " );
        System.out.println( "Cipher Sym Key encrypted with public key "+ cipherSymKeyString);
        byte [][] result = {cipherSymKeyBytes,cipherTextTemp,signedDigest};
        return result;
    }

    public static String DecryptMessage(byte [] toBeDecrypted, PrivateKey key, byte[] encryptedSecretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        System.out.println( "\nStart decryption" );
        byte[] encryptedSecretKeyBytes = encryptedSecretKey;
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] originalSymKeyBytes = cipher.doFinal(encryptedSecretKeyBytes);
        String originalSymKeyString = new String(originalSymKeyBytes);
        System.out.println( "Finished decrypting the Secret key " );
        System.out.println( "Original sym key decrypted successfully: "+ originalSymKeyString);
        SecretKey OriginalAfterEncDec = new SecretKeySpec(originalSymKeyBytes, 0, originalSymKeyBytes.length, "AES");

        Cipher cipherSym2 = Cipher.getInstance("AES");
        cipherSym2.init(Cipher.DECRYPT_MODE, OriginalAfterEncDec);
        byte[] plainTextFinal = cipherSym2.doFinal(toBeDecrypted);
        String originalMessage = new String(plainTextFinal);
        System.out.println("The message was: "+ originalMessage);
        return originalMessage;
    }


    public static String DecryptMessageGroup(byte [] toBeDecrypted, PrivateKey key, byte[][] encryptedSecretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        System.out.println( "\nStart decryption" );
        String originalMessage = "";
        for(int i=0;i<encryptedSecretKey.length;i++){
            byte[] encryptedSecretKeyBytes = encryptedSecretKey[i];
            try{
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] originalSymKeyBytes = cipher.doFinal(encryptedSecretKeyBytes);
                String originalSymKeyString = new String(originalSymKeyBytes);
                System.out.println( "Finished decrypting the Secret key " );
                System.out.println( "Original sym key decrypted successfully: "+ originalSymKeyString);
                SecretKey OriginalAfterEncDec = new SecretKeySpec(originalSymKeyBytes, 0, originalSymKeyBytes.length, "AES");

                Cipher cipherSym2 = Cipher.getInstance("AES");
                cipherSym2.init(Cipher.DECRYPT_MODE, OriginalAfterEncDec);
                byte[] plainTextFinal = cipherSym2.doFinal(toBeDecrypted);
                originalMessage = new String(plainTextFinal);
                System.out.println("The message was: "+ originalMessage);
            }
            catch(Exception e){
                if(e instanceof BadPaddingException){
                    System.out.println("Invalid key");
                }
            }
        }
        return originalMessage;
    }


    public static byte[]EncryptWithoutSecretKey(byte[]toBeEncrypted,PublicKey ReceiverPublicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        byte[] plainText = toBeEncrypted;
        //
        // get an RSA cipher object
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // encrypt the SecretKey using the receiver public key
        System.out.println( "\nStart encryption" );
        cipher.init(Cipher.ENCRYPT_MODE, ReceiverPublicKey);
        byte[] cipherText = cipher.doFinal(plainText);
        String cipherTextString = new String(cipherText);
        System.out.println( "Finish encryption: " );
        System.out.println( "Cipher text encrypted with public key "+ cipherTextString);
        byte [] result = cipherText;
        return result;
    }


    public static byte[]DecryptWithoutSecretKey(byte[]toBeDecrypted,PrivateKey SenderPrivateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        //
        // get an RSA cipher object
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // encrypt the SecretKey using the receiver public key
        System.out.println( "\nStart decryption" );
        cipher.init(Cipher.DECRYPT_MODE, SenderPrivateKey);
        byte[] plainTextBytes = cipher.doFinal(toBeDecrypted);
        String plainTextString = new String(plainTextBytes);
        System.out.println( "Finish decryption: " );
        System.out.println( "Plain text decrypted with private key "+ plainTextString);
        byte [] result = plainTextBytes;
        return result;
    }

    public static boolean CheckMsgDigest(byte [] messageDigest, PublicKey key, String receivedText) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        boolean result = false;
        System.out.println( "\n Verifying message" );
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] originalMessageDigest = cipher.doFinal(messageDigest);
        System.out.println( "Decrypted the message digest " );
        System.out.println("Checking if the digest is equal to the plain text digest");
        //calculating message digest
        MessageDigest sha1 = MessageDigest.getInstance("SHA-512");
        byte[] digest = sha1.digest(receivedText.getBytes("UTF-8"));
        if(new String(originalMessageDigest).equals(new String(digest))){
            result = true;
        }
        return result;
    }

    public static void main(String[]args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException{
        KeyPair key = GeneratePubPrivateKey(1024);
        PublicKey publicKey = key.getPublic();
        PrivateKey privateKey = key.getPrivate();
        byte[] input = "My name is Tarek, Karim Tarek".getBytes();
        byte [][] cipherTextAndEncryptedKey = EncryptMessage(input,publicKey,privateKey);
        byte[] cipherKey = cipherTextAndEncryptedKey[0];
        byte[] cipherText = cipherTextAndEncryptedKey[1];
        byte[] encryptedMessageDigest = cipherTextAndEncryptedKey[2];
        String plainText = DecryptMessage(cipherText,privateKey,cipherKey);
        boolean verified = CheckMsgDigest(encryptedMessageDigest, publicKey, plainText);
        System.out.println(plainText);
        System.out.println(verified);
//		System.out.println("PLAIN TEXT: 01283200379");
//		byte [] plainTexTest = "01283200379".getBytes();
//		byte [] cipherTextTest = EncryptWithoutSecretKey(plainTexTest, publicKey);
//		System.out.println(new String(cipherTextTest));
//		byte [] plainTextTest = DecryptWithoutSecretKey(cipherTextTest, privateKey);
//		System.out.println(new String(plainTextTest));

    }












}