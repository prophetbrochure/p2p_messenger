package Testing;

import Crypto.DH;
import Crypto.Constants;
import Crypto.HKDF;
import Crypto.Key;

import java.math.BigInteger;

// Требует, чтобы sha256 в AES128 был доступен.
public class TestHKDF {
    public static void main(String[] args) {
        DH dh1 = new DH(Constants.generator, Constants.module);
        DH dh2 = new DH(Constants.generator, Constants.module);

        BigInteger publicKey1 = dh1.getPublicKey();
        BigInteger publicKey2 = dh2.getPublicKey();

        dh1.generateSharedSecret(publicKey2);
        dh2.generateSharedSecret(publicKey1);

        HKDF hkdf1 = new HKDF(Constants.defaultSalt, dh1.getSharedSecret());
        HKDF hkdf2 = new HKDF(Constants.defaultSalt, dh2.getSharedSecret());

        Key aes1 = hkdf1.getKey("AES", 16);
        Key aes2 = hkdf2.getKey("AES", 16);

        Key mac1 = hkdf1.getKey("mac", 12);
        Key mac2 = hkdf2.getKey("mac", 12);

        System.out.println("aes1: " + Utils.toHexString(aes1.key));
        System.out.println("aes2: " + Utils.toHexString(aes2.key));

        System.out.println("mac1: " + Utils.toHexString(mac1.key));
        System.out.println("mac2: " + Utils.toHexString(mac2.key));


    }
}

