package Crypto;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * <p>Криптографический ключ. Содержит массив байтов произвольной длины.</p>
 */
public class Key {
    byte[] key;

    Key(int keyLength) {
        key = new byte[keyLength];
    }

    /**
     * <p>Заполняет массив байтов key внутри класса Key случайными значениями.</p>
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