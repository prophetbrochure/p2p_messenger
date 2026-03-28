package Crypto;

/**
 * <h2>Контейнер зашифрованного сообщения</h2>
 * <pre>
 * Формат: /nonce 12B/ciphertext NB/mac 16B/
 * </pre>
 */
public class EncryptedMessage {
    public final byte[] nonce;      // 12 байт
    public final byte[] ciphertext; // Х длина
    public final byte[] mac;        // 16 байт

    public EncryptedMessage(byte[] nonce, byte[] ciphertext, byte[] mac) {
        this.nonce = nonce;
        this.ciphertext = ciphertext;
        this.mac = mac;
    }

    /**
     * Сериализация в один массив байт
     * Формат: /nonce 12байт/ciphertext Xбайт/mac 16Байт/
     */
    public byte[] toBytes() {
        byte[] result = new byte[12 + ciphertext.length + 16];
        System.arraycopy(nonce, 0, result, 0, 12);
        System.arraycopy(ciphertext, 0, result, 12, ciphertext.length);
        System.arraycopy(mac, 0, result, 12 + ciphertext.length, 16);
        return result;
    }

    /**
     * Десериализация из массива байт
     */
    public static EncryptedMessage fromBytes(byte[] data) {
        if (data.length < 28) { // 12 + 0 + 16 минимум
            throw new IllegalArgumentException("Data too short");
        }
        byte[] nonce = new byte[12];
        byte[] ciphertext = new byte[data.length - 28];
        byte[] mac = new byte[16];

        System.arraycopy(data, 0, nonce, 0, 12);
        System.arraycopy(data, 12, ciphertext, 0, ciphertext.length);
        System.arraycopy(data, data.length - 16, mac, 0, 16);

        return new EncryptedMessage(nonce, ciphertext, mac);
    }
}
