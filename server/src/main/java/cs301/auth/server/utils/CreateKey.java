package cs301.auth.server.utils;
import java.io.DataOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import java.security.PrivateKey;
import java.security.PublicKey;

public class CreateKey {
    public static void main(String[] args) {
        genRSAKeyPairAndSaveToFile();
    }
    public static void genRSAKeyPairAndSaveToFile() {
        genRSAKeyPairAndSaveToFile(2048, "src/main/resources/static/");
    }/*from  ww w  .j  a v  a  2 s  . co  m*/

    public static void genRSAKeyPairAndSaveToFile(String dir) {
        genRSAKeyPairAndSaveToFile(2048, dir);
    }

    public static void genRSAKeyPairAndSaveToFile(int keyLength, String dir) {
        KeyPair keyPair = genRSAKeyPair(keyLength);

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(dir
                    + "rsaPublicKey2"));
            dos.write(publicKey.getEncoded());
            dos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        try {
            dos = new DataOutputStream(new FileOutputStream(dir
                    + "rsaPrivateKey2"));
            dos.write(privateKey.getEncoded());
            dos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (dos != null)
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static KeyPair genRSAKeyPair() {
        return genRSAKeyPair(2048);
    }

    public static KeyPair genRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator
                    .getInstance("RSA");
            keyPairGenerator.initialize(keyLength);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}