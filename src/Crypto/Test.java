package Crypto;

import java.util.Arrays;

/**
 * <p>Класс для тестирования CTR + MAC </p>
 */
public class Test{
    public static void main(String[] args) {

        // === Исходное сообщение ===
        String text = "abcdefghijklmnopqrstuvwxyz";
        byte[] plaintext = text.getBytes();

        // === Ключи ===
        Key encKey = new Key(16);
        encKey.generateKey();

        Key macKey = new Key(16);
        macKey.generateKey();

        AES128_CTR_MAC crypto = new AES128_CTR_MAC(encKey, macKey);

        System.out.println("Ключ шифрования: " + Utils.toHexString(encKey.key));
        System.out.println("Ключ MAC:        " + Utils.toHexString(macKey.key));

        // === Исходный текст ===
        System.out.println("\nИсходный текст:");
        System.out.println(text);

        // === Шифрование ===
        EncryptedMessage encrypted = crypto.encrypt(plaintext);

        System.out.println("\nNonce:");
        System.out.println(Utils.toHexString(encrypted.nonce));

        System.out.println("\nШифротекст:");
        System.out.println(Utils.toHexString(encrypted.ciphertext));

        System.out.println("\nMAC:");
        System.out.println(Utils.toHexString(encrypted.mac));

        // === Дешифрование ===
        byte[] decrypted = crypto.decrypt(encrypted);

        System.out.println("\nРасшифрованный текст:");
        System.out.println(new String(decrypted));

        System.out.println("\nСовпадает: " + Arrays.equals(plaintext, decrypted));

        // === HEX всего сообщения ===
        byte[] full = encrypted.toBytes();
        System.out.println("\nПолное сообщение (nonce + ct + mac):");
        System.out.println(Utils.toHexString(full));

        // === Тест подмены ===
        System.out.println("\n=== Тест подмены ===");
        encrypted.ciphertext[0] ^= 0x01;

        try {
            crypto.decrypt(encrypted);
            System.out.println("Ошибка: подмена НЕ обнаружена");
        } catch (SecurityException e) {
            System.out.println("Подмена обнаружена: " + e.getMessage());
        }
    }
}