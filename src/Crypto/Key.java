package Crypto;

import java.security.SecureRandom;
import java.util.Arrays;

public class Key {
    public byte[] key;

    /**
     * @param keyLength длина ключа в байтах
     */
    public Key(int keyLength) {
        key = new byte[keyLength];
    }

    /**
     * Конструктор из готового массива байт
     */
    public Key(byte[] data) {
        if (data.length != 16) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        this.key = data.clone();
    }

    /**
     * <p>Заполняет массив байтов key случайными значениями.</p>
     */
    public void generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
    }

    Key getCopy() {
        Key result = new Key(this.key.length);
        result.key = Arrays.copyOf(this.key, this.key.length);
        return result;
    }
}
