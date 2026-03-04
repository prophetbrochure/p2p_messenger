package Crypto;


import java.security.SecureRandom;


public class Key {
    byte[] key;

    Key(int keyLength) {
        key = new byte[keyLength];
    }

    void generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
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