package Crypto;

/**
 * <h2>AES-128 в режиме CTR с аутентификацией Encrypt-then-MAC</h2>
 *
 * <pre>
 *
 *
 * Шифрование:
 *    1. Генерируем случайный nonce (12 байт)
 *    2. CTR-шифруем plaintext → ciphertext
 *    3. CMAC(nonce || ciphertext) → mac (16 байт)
 *    4. Возвращаем {nonce, ciphertext, mac}
 *
 *  Дешифрование:
 *    1. CMAC(nonce || ciphertext) -> expected_mac
 *    2. Сравниваем mac с expected_mac (constant-time!)
 *    3. Если совпали -> CTR-дешифруем
 *    4. Если нет -> выбрасываем исключение
 *
 *
 * Два разных ключа:
 *   - encKey: для CTR-шифрования
 *   - macKey: для вычисления CMAC
 *
 * </pre>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * Key encKey = new Key(new byte[]{...}); // 16 байт
 * Key macKey = new Key(new byte[]{...}); // 16 байт, ДРУГОЙ ключ
 *
 * AES128_CTR_MAC crypto = new AES128_CTR_MAC(encKey, macKey);
 *
 * // Шифрование
 * byte[] plaintext = "abcdefg".getBytes();
 * EncryptedMessage encrypted = crypto.encrypt(plaintext);
 *
 * // Передача/хранение
 * byte[] wire = encrypted.toBytes();
 *
 * // Дешифрование
 * EncryptedMessage received = EncryptedMessage.fromBytes(wire);
 * byte[] decrypted = crypto.decrypt(received);
 * }</pre>
 */
public class AES128_CTR_MAC {

    private final CTRMode ctr;
    private final AESCMAC cmac;

    /**
     * @param encryptionKey ключ для шифрования (CTR mode)
     * @param macKey        ключ для аутентификации (CMAC) — должен отличаться от encryptionKey
     */
    public AES128_CTR_MAC(Key encryptionKey, Key macKey) {
        this.ctr = new CTRMode(new AES128(encryptionKey));
        this.cmac = new AESCMAC(new AES128(macKey));
    }

    /**
     * Шифрует данные произвольной длины
     * Каждый вызов генерирует новый случайный nonce - один и тот же plaintext
     * даёт разный ciphertext при каждом вызове
     *
     * @param plaintext открытый текст (любая длина, включая 0)
     * @return контейнер {nonce, ciphertext, mac}
     */
    public EncryptedMessage encrypt(byte[] plaintext) {
        Nonce nonce = Nonce.random();
        return encrypt(plaintext, nonce);
    }

    /**
     * Шифрует с заданным nonce (для тестирования).
     *   {@link #encrypt(byte[])} — он сам генерирует nonce.
     */
    public EncryptedMessage encrypt(byte[] plaintext, Nonce nonce) {
        // 1. CTR-шифрование (счётчик начинается с 1)
        byte[] ciphertext = ctr.process(plaintext, nonce, 1);

        // 2. MAC от (nonce || ciphertext) — Encrypt-then-MAC
        byte[] macInput = concatenate(nonce.value, ciphertext);
        byte[] mac = cmac.compute(macInput);

        return new EncryptedMessage(nonce.value, ciphertext, mac);
    }

    /**
     * Дешифрует и проверяет целостность.
     *
     * @param message зашифрованное сообщение
     * @return расшифрованный plaintext
     * @throws SecurityException если MAC не совпадает (данные повреждены или подменены)
     */
    public byte[] decrypt(EncryptedMessage message) throws SecurityException {
        // 1. Проверяем MAC ПЕРЕД дешифрованием
        byte[] macInput = concatenate(message.nonce, message.ciphertext);
        byte[] expectedMac = cmac.compute(macInput);

        if (!constantTimeEquals(expectedMac, message.mac)) {
            throw new SecurityException(
                    "MAC verification failed! Data has been tampered with.");
        }

        // 2. Только если MAC верный — дешифруем
        Nonce nonce = new Nonce(message.nonce);
        return ctr.process(message.ciphertext, nonce, 1);
    }

    /**
     * Сравнение в постоянном времени — защита от timing attack.
     * Обычное сравнение (Arrays.equals) выходит рано при первом несовпадении,
     * что позволяет атакующему побайтово подбирать MAC.
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= (a[i] ^ b[i]) & 0xFF; // накапливаем различия, но НЕ выходим рано
        }
        return result == 0;
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}