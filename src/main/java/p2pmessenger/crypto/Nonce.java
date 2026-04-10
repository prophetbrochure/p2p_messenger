package p2pmessenger.crypto;

import java.security.SecureRandom;

/**
 * 12-байтовый nonce (IV) для CTR-режима.
 * 12 байт nonce + 4 байта счётчик = 16 байт блок для AES.
 */

public class Nonce {
    public static final int NONCE_SIZE = 12;
    public final byte[] value;

    public Nonce(byte[] value) {
        if (value.length != NONCE_SIZE) {
            throw new IllegalArgumentException("Nonce must be " + NONCE_SIZE + " bytes");
        }
        this.value = value.clone();
    }

    /**
     * Генерирует случайный nonce
     */
    public static Nonce random() {
        byte[] bytes = new byte[NONCE_SIZE];
        new SecureRandom().nextBytes(bytes);
        return new Nonce(bytes);
    }

    /**
     * Строим 16-байтовый блок: nonce (12 байт) / counter (4 байта)
     */
    public State toCounterBlock(int counter) {
        byte[] block = new byte[16];
        System.arraycopy(value, 0, block, 0, NONCE_SIZE);
        block[12] = (byte) (counter >>> 24);
        block[13] = (byte) (counter >>> 16);
        block[14] = (byte) (counter >>> 8);
        block[15] = (byte) (counter);
        return new State(block);
    }
}
