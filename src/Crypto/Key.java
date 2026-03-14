package Crypto;


import java.security.SecureRandom;
import java.util.Arrays;


public class Key {
    byte[] key;

    Key(int keyLength) {
        key = new byte[keyLength];
    }

    void generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
    }

    Key getCopy() {
        Key result = new Key(this.key.length);
        result.key = Arrays.copyOf(this.key, this.key.length);
        return result;
    }





    String toHexString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : key) {
            sb.append(String.format("%02X", b));
            sb.append(' ');
        }
        return sb.toString();
    }

    String toBinString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : key) {
            sb.append(
                    String.format("%8s",
                            Integer.toBinaryString(b & 0xFF)
                    ).replace(' ', '0')
            );
            sb.append(' ');
        }
        return sb.toString();
    }
}