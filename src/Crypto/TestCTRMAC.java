package Crypto;

import java.util.Arrays;

public class TestCTRMAC {
    public static void main(String[] args) {
        // Два разных ключа
        Key encKey = new Key(new byte[]{
                0x2b, 0x7e, 0x15, 0x16, 0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
                (byte) 0xab, (byte) 0xf7, 0x15, (byte) 0x88, 0x09, (byte) 0xcf, 0x4f, 0x3c
        });
        Key macKey = new Key(new byte[]{
                0x3c, 0x4f, (byte) 0xcf, 0x09, (byte) 0x88, 0x15, (byte) 0xf7, (byte) 0xab,
                (byte) 0xa6, (byte) 0xd2, (byte) 0xae, 0x28, 0x16, 0x15, 0x7e, 0x2b
        });

        AES128_CTR_MAC crypto = new AES128_CTR_MAC(encKey, macKey);

        // Тест 1: Базовое шифрование/дешифрование
        byte[] plaintext = "Hello, AES-128 CTR with MAC!".getBytes();
        EncryptedMessage encrypted = crypto.encrypt(plaintext);
        byte[] decrypted = crypto.decrypt(encrypted);

        System.out.println("=== Тест 1: Базовый ===");
        System.out.println("Plaintext:  " + new String(plaintext));
        System.out.println("Decrypted:  " + new String(decrypted));
        System.out.println("Match:      " + Arrays.equals(plaintext, decrypted));

        // Тест 2: Сериализация/десериализация
        byte[] wire = encrypted.toBytes();
        EncryptedMessage restored = EncryptedMessage.fromBytes(wire);
        byte[] decrypted2 = crypto.decrypt(restored);

        System.out.println("\n=== Тест 2: Сериализация ===");
        System.out.println("Wire size:  " + wire.length + " bytes (12 nonce + "
                + encrypted.ciphertext.length + " ct + 16 mac)");
        System.out.println("Match:      " + Arrays.equals(plaintext, decrypted2));

        // Тест 3: Обнаружение подмены
        System.out.println("\n=== Тест 3: Tamper detection ===");
        encrypted.ciphertext[0] ^= 0x01; // Меняем 1 бит
        try {
            crypto.decrypt(encrypted);
            System.out.println("FAIL: подмена не обнаружена!");
        } catch (SecurityException e) {
            System.out.println("OK: " + e.getMessage());
        }

        //Тест 4: Разные nonce, разный шифротекст
        EncryptedMessage enc1 = crypto.encrypt(plaintext);
        EncryptedMessage enc2 = crypto.encrypt(plaintext);

        System.out.println("\n=== Тест 4: Разные nonce ===");
        System.out.println("Same ciphertext: " + Arrays.equals(enc1.ciphertext, enc2.ciphertext));
        System.out.println("(должно быть false)");

        //  Тест 5: Пустое сообщение
        byte[] empty = new byte[0];
        EncryptedMessage encEmpty = crypto.encrypt(empty);
        byte[] decEmpty = crypto.decrypt(encEmpty);

        System.out.println("\n=== Тест 5: Пустое сообщение ===");
        System.out.println("Match: " + Arrays.equals(empty, decEmpty));
    }
}
