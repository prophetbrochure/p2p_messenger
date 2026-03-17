package Crypto;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * <h2><strong><p>Криптографический ключ. Содержит массив байтов произвольной длины.</p></strong></h2>
 */
public class Key {
    byte[] key;

    /**
     * @param keyLength длина ключа в байтах
     */
    Key(int keyLength) {
        key = new byte[keyLength];
    }

    /**
     * <p>Заполняет массив байтов key случайными значениями.</p>
     */
    void generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
    }

    Key getCopy() {
        Key result = new Key(this.key.length);
        result.key = Arrays.copyOf(this.key, this.key.length);
        return result;
    }

}