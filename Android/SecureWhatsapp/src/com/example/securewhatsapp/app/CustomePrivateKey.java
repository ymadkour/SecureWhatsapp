package com.example.securewhatsapp.app;

import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * Created by youssef on 5/16/14.
 */
public class CustomePrivateKey {

    public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = Base64.decode(key64, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }





    public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = Base64.encodeToString(packed,Base64.DEFAULT);

        Arrays.fill(packed, (byte) 0);
        return key64;
    }
/*


    public static void main(String[] args) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("DSA");
        KeyPair pair = gen.generateKeyPair();

        String pubKey = savePublicKey(pair.getPublic());
        PublicKey pubSaved = loadPublicKey(pubKey);
        System.out.println(pair.getPublic()+"\n"+pubSaved);

        String privKey = savePrivateKey(pair.getPrivate());
        PrivateKey privSaved = loadPrivateKey(privKey);
        System.out.println(pair.getPrivate()+"\n"+privSaved);
    }*/

}
