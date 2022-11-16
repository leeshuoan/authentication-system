package cs301.auth.server.sso;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPublicKey;


@Service
public class PubKey {

    private static byte[] parsePEMFile(File pemFile) throws IOException {
        if (!pemFile.isFile() || !pemFile.exists()) {
            throw new FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
        }
        String key = new String(Files.readAllBytes(pemFile.toPath()), Charset.defaultCharset());
        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("\\n", "")
                .replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        return encoded;
    }

    public static RSAPublicKey readPublicKeyFromFile(String filepath, String algorithm) throws IOException {
        byte[] bytes = PubKey.parsePEMFile(new File(filepath));
        return PubKey.getPublicKey(bytes, algorithm);
    }

    private static RSAPublicKey getPublicKey(byte[] keyBytes, String algorithm) {
        RSAPublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = (RSAPublicKey) kf.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not reconstruct the public key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            System.out.println("Could not reconstruct the public key");
        }

        return publicKey;
    }
    
}
