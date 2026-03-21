package Crypto;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Класс для шифрования {@link State блока из 16 байт}.</p>
 * <p>Реализованы методы:
 * <ol>
 *     <li>{@link #subBytes} - подстановка </li>
 *     <li>{@link #shiftRows} - сдвиг</li>
 *     <li>{@link #mixColumns} - умножение матриц</li>
 *     <li>{@link #addRoundKey} - XOR с раундовым ключом</li>
 * </ol>
 * </p>
 * <p>
 *     Генерирует набор из 11 раундовых ключей при создании экземпляра.
 * </p>
 */
public class AES128 {
    /**
     * <p><h2><strong>Список из 11 раундовых {@link Key ключей}.</strong></h2></p>
     * <p>Генерируется из начального ключа с помощью {@link AES128#keyExpansion}</p>
     */
    List<Key> keysList;

    /**
     *
     * @param initialKey - ключ, на основе которого {@link AES128#keyExpansion генерируется} список из 11 раундовых {@link Key ключей}
     */
    public AES128(Key initialKey) {
        this.keysList = keyExpansion(initialKey);
    }


    /**
     * <p>Замена байтов на другие согласно {@link Constants#SBOX таблице коммутации}</p>
     */
    private static void subBytes(State block) {
        for (int i = 0; i < 16; i++) {
            block.matrix[i] = Constants.SBOX[block.matrix[i] & 0xFF];
        }
    }

    /**
     * <p><h2><strong>Операция, обратная {@link #subBytes}.</h2></p>
     * <p>Использует {@link Constants#reverseSBOX обратную таблицу коммутации}</strong></p>
     */
    private static void reverseSubBytes(State block) {
        for (int i = 0; i < 16; i++) {
            block.matrix[i] = Constants.reverseSBOX[block.matrix[i] & 0xFF];
        }
    }


    /**
     * <p><h2><strong>Сдвиг строк.</strong></h2></p>
     * <p>Первая строка не сдвигается.<br>
     * Вторая строка сдвигается на 1 влево.<br>
     * Третья строка сдвигается на 2 влево.<br>
     * Четвёртая строка сдвигается на 3 влево.<br>
     * </p>
     * <p>
     * 1A   3A   5A   7A --> 1A   3A   5A   7A<br>
     * 1B   3B   5B   7B --> 3B   5B   7B   1B<br>
     * 2A   4A   6A   8A --> 6A   8A   2A   4A<br>
     * 2B   4B   6B   8B --> 8B   2B   4B   6B
     * </p>
     */
    private static void shiftRows(State block) {
        byte x = block.matrix[4], y = block.matrix[15];
        block.matrix[4] = block.matrix[5];      // 2 строка
        block.matrix[5] = block.matrix[6];
        block.matrix[6] = block.matrix[7];
        block.matrix[7] = x;

        block.matrix[15] = block.matrix[14];    // 3 строка
        block.matrix[14] = block.matrix[13];
        block.matrix[13] = block.matrix[12];
        block.matrix[12] = y;

        x = block.matrix[8];                    // 4 строка
        y = block.matrix[9];
        block.matrix[8] = block.matrix[10];
        block.matrix[9] = block.matrix[11];
        block.matrix[10] = x;
        block.matrix[11] = y;
    }

    /**
     * <p><h2><strong>Операция, обратная {@link #shiftRows}.</strong></h2></p>
     */
    private static void reverseShiftRows(State block) {
        byte x = block.matrix[7], y = block.matrix[12];

        block.matrix[7] = block.matrix[6];      // 2 строка
        block.matrix[6] = block.matrix[5];
        block.matrix[5] = block.matrix[4];
        block.matrix[4] = x;

        block.matrix[12] = block.matrix[13];    // 3 строка
        block.matrix[13] = block.matrix[14];
        block.matrix[14] = block.matrix[15];
        block.matrix[15] = y;

        x = block.matrix[8];                    // 4 строка
        y = block.matrix[9];
        block.matrix[8] = block.matrix[10];
        block.matrix[9] = block.matrix[11];
        block.matrix[10] = x;
        block.matrix[11] = y;
    }


    /**
     * <p><h2><strong>Умножение на матрицу.</strong></h2></p>
     * <p>Каждый столбец умножается на фиксированную матрицу 4×4 в поле GF(2⁸)</p>
     * <p>E = {@link Constants#mixColumnsMatrix C} * U<br>
     * E - зашифрованный столбец<br>
     * {@link Constants#mixColumnsMatrix C - Фиксированная матрица}<br>
     * U - незашифрованный столбец.
     * </p>
     * <p>Вместо сложения используется XOR<br>
     * Вместо умножения - {@link AES128#product умножение в поле Галуа GF(2⁸)} (Что бы это ни значило)
     * </p>
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    private static void mixColumns(State block) {
        byte[] temp = new byte[16];
        for (int column = 0; column < 4; column++) {    // Для каждого столбца
            for (int row = 0; row < 4; row++) {         // Для каждой строки
                temp[row * 4 + column] = (byte) (
                        product(Constants.mixColumnsMatrix[4 * row + 0], block.matrix[0 + column]) ^
                                product(Constants.mixColumnsMatrix[4 * row + 1], block.matrix[4 + column]) ^
                                product(Constants.mixColumnsMatrix[4 * row + 2], block.matrix[8 + column]) ^
                                product(Constants.mixColumnsMatrix[4 * row + 3], block.matrix[12 + column]));
            }
        }
        System.arraycopy(temp, 0, block.matrix, 0, 16);
    }

    /**
     * <p><h2><strong>Операция, обратная {@link #mixColumns}.</strong></h2></p>
     * <p>U = {@link Constants#reverseMixColumnsMatrix C^(−1)} * E<br>
     * U - незашифрованный столбец.<br>
     * E - зашифрованный столбец<br>
     * {@link Constants#reverseMixColumnsMatrix C^(-1) - Фиксированная матрица}
     * </p>
     * <p>
     * Вместо сложения используется XOR<br>
     * Вместо умножения - умножение в поле Галуа GF(2⁸) (Что бы это ни значило)
     * </p>
     *
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    private static void reverseMixColumns(State block) {
        byte[] temp = new byte[16];
        for (int column = 0; column < 4; column++) {    // Для каждого столбца
            for (int row = 0; row < 4; row++) {         // Для каждой строки
                temp[row * 4 + column] = (byte) (
                        product(Constants.reverseMixColumnsMatrix[4 * row + 0], block.matrix[0 + column]) ^
                                product(Constants.reverseMixColumnsMatrix[4 * row + 1], block.matrix[4 + column]) ^
                                product(Constants.reverseMixColumnsMatrix[4 * row + 2], block.matrix[8 + column]) ^
                                product(Constants.reverseMixColumnsMatrix[4 * row + 3], block.matrix[12 + column]));
            }
        }
        System.arraycopy(temp, 0, block.matrix, 0, 16);
    }

    /**
     * <h2><strong>Умножение в поле Галуа GF(2⁸)</strong></h2>
     * <p>Используется в {@link AES128#mixColumns} и в {@link AES128#reverseMixColumns}</p>
     * <p>Использует алгоритм "Русского крестьянина" (Буквально, это не рофл.)</p>
     */
    private static byte product(byte a, byte b) {
        byte res = 0;
        while (b != 0) {
            if ((b & 0x01) == 0x01) { // Если младший бит = 1
                res = (byte) (res ^ a);
            }

            boolean mostSignificantBit = (a & 0x80) == 0x80;    // Старший бит до сдвига
            a = (byte) (a << 1);
            if (mostSignificantBit) {
                a = (byte) (a ^ 0x1b);
            }
            b = (byte) ((b & 0xFF) >> 1);   //  (b & 0xFF) из-за того, что при логических операциях byte автоматически кастуется в int,
            //  при этом сохраняя знак -.
        }
        return res;
    }


    /**
     * <p><h2><strong>Блок XORится с раундовым ключом.</strong></h2></p>
     * <p>Операция обратна сама себе.</p>
     */
    private static void addRoundKey(State block, Key roundKey) {
        for (int i = 0; i < 16; i++) {
            block.matrix[i] ^= roundKey.key[i];   // ^ - XOR
        }
    }


    /**
     * <p><h2><strong>Шифрование {@link State 16-байтового блока} в 10 раундов
     * с использованием набора из 11 128-битных раундовых {@link Key ключей}</strong></h2></p>
     *
     * <p>
     * Шаги шифрования на каждом раунде: <ol>
     * <li> {@link #subBytes}</li>
     * <li> {@link #shiftRows}</li>
     * <li> {@link #mixColumns} (кроме последнего раунда)</li>
     * <li> {@link #addRoundKey} </li>
     * </ol></p>
     *
     * @param block - блок, который будет зашифрован.
     */
    public void encryptState(State block) {
        addRoundKey(block, keysList.get(0));

        for (int roundNumber = 1; roundNumber <= 9; roundNumber++) {
            subBytes(block);
            shiftRows(block);
            mixColumns(block);
            addRoundKey(block, keysList.get(roundNumber));
        }

        subBytes(block);
        shiftRows(block);
        addRoundKey(block, keysList.get(10));
    }

    /**
     * <p><h2><strong>Дешифрование 16-байтового блока.</strong></h2></p>
     * <p>Обратна к {@link #encryptState}</p>
     *
     * @param block - блок, который будет дешифрован.
     */
    public void decryptState(State block) {
        addRoundKey(block, keysList.get(10));
        reverseShiftRows(block);
        reverseSubBytes(block);

        for (int i = 9; i >= 1; i--) {
            addRoundKey(block, keysList.get(i));
            reverseMixColumns(block);
            reverseShiftRows(block);
            reverseSubBytes(block);
        }

        addRoundKey(block, keysList.get(0));
    }

    /**
     * <p><h2><strong>Генерирует список из 11 {@link Key ключей} для каждого из раундов шифрования</strong></h2></p>
     */
    /* Тут костыль на костыле. Я сначала реализовал keyExpansion, но он работал по строкам, а не по столбцам, как должен был.
     * Прикрутил в начало транспонирование ключа, таким образом строки стали столбцами и наоборот.
     */
    private static List<Key> keyExpansion(Key initialKey) {
        List<Key> keys = new ArrayList<>(11);
        keys.add(0, new Key(16));

        for (int row = 0; row < 4; row++) {     // Транспонирование.
            for (int column = 0; column < 4; column++) {
                keys.get(0).key[row * 4 + column] = initialKey.key[column * 4 + row];
            }
        }

        for (int keyNumber = 1; keyNumber < 11; keyNumber++) {
            keys.add(keyNumber, new Key(16));
            for (int wordNumber = 0; wordNumber < 4; wordNumber++) {
                byte[] temp = new byte[4];
                if (wordNumber == 0) {
                    System.arraycopy(keys.get(keyNumber - 1).key, 12, temp, 0, 4);  // Последнее слово предыдущего раунда.

                    byte left = temp[0];    // Сдвиг на 1 влево (RotWord)
                    temp[0] = temp[1];
                    temp[1] = temp[2];
                    temp[2] = temp[3];
                    temp[3] = left;

                    for (int i = 0; i < 4; i++) {   // SubBytes + XOR с константой раунда
                        temp[i] = Constants.SBOX[temp[i] & 0xFF];
                    }
                    temp[0] ^= Constants.RCON[keyNumber - 1];

                } else {
                    System.arraycopy(keys.get(keyNumber).key, 4 * (wordNumber - 1), temp, 0, 4);
                }

                for (int byteNumber = 0; byteNumber < 4; byteNumber++) {
                    keys.get(keyNumber).key[4 * wordNumber + byteNumber] =
                            (byte) (keys.get(keyNumber - 1).key[4 * wordNumber + byteNumber] ^ temp[byteNumber]);
                }
            }

        }
        return keys;
    }
}
