package Crypto;

/**
 * <p>Класс для шифрования {@link State блока из 16 байт}.</p>
 * <p>Реализованы методы:
 * <ol>
 *     <li>{@link #subBytes} - подстановка </li>
 *     <li>{@link #shiftRows} - сдвиг</li>
 *     <li>{@link #mixColumns} - умножение матриц</li>
 *     <li>{@link #addRoundKey} - XOR с ключом</li>
 * </ol>
 * </p>
 */
public class AES {
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
     *     1A   3A   5A   7A --> 1A   3A   5A   7A<br>
     *     1B   3B   5B   7B --> 3B   5B   7B   1B<br>
     *     2A   4A   6A   8A --> 6A   8A   2A   4A<br>
     *     2B   4B   6B   8B --> 8B   2B   4B   6B
     * </p>
     */
    public static void shiftRows(State block) {
        byte x = block.matrix[4], y = block.matrix[15];
        block.matrix[4] = block.matrix[5];      // 1 строка
        block.matrix[5] = block.matrix[6];
        block.matrix[6] = block.matrix[7];
        block.matrix[7] = x;

        block.matrix[15] = block.matrix[14];    // 3 строка
        block.matrix[14] = block.matrix[13];
        block.matrix[13] = block.matrix[12];
        block.matrix[12] = y;

        x = block.matrix[8];                    // 2 строка
        y = block.matrix[9];
        block.matrix[8] = block.matrix[10];
        block.matrix[9] = block.matrix[11];
        block.matrix[10] = x;
        block.matrix[11] = y;
    }

    /**
     * <p><h2><strong>Операция, обратная {@link #shiftRows}.</strong></h2></p>
     */
    public static void reverseShiftRows(State block) {
        byte x = block.matrix[7], y = block.matrix[12];

        block.matrix[7] = block.matrix[6];      // 1 строка
        block.matrix[6] = block.matrix[5];
        block.matrix[5] = block.matrix[4];
        block.matrix[4] = x;

        block.matrix[12] = block.matrix[13];    // 3 строка
        block.matrix[13] = block.matrix[14];
        block.matrix[14] = block.matrix[15];
        block.matrix[15] = y;

        x = block.matrix[8];                    // 2 строка
        y = block.matrix[9];
        block.matrix[8] = block.matrix[10];
        block.matrix[9] = block.matrix[11];
        block.matrix[10] = x;
        block.matrix[11] = y;
    }


    /**
     * <p><h2><strong>Умножение на матрицу.</strong></h2></p>
     * <p>Каждый столбец умножается на фиксированную матрицу 4×4 в поле GF(2⁸)</p>
     */
    private static void mixColumns(State block) {

    }

    /**
     * <p><h2><strong>Операция, обратная {@link #mixColumns}.</strong></h2></p>
     */
    private static void reverseMixColumns(State block) {

    }


    /**
     * <p><h2><strong>Блок XORится с раундовым ключом.</strong></h2></p>
     */
    private static void addRoundKey(State block, Key roundKey) {
        for (int i = 0; i < 16; i++) {
            block.matrix[i] ^= roundKey.key[i];   // ^ - XOR
        }
    }


    /**
     * <p><h2><strong>Шифрование {@link State 16-байтового блока} в 10 раундов с использованием 128-битного {@link Key ключа}</strong></h2></p>
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
     * @param key   - 128-битный ключ, из которого генерируются раундовые ключи.
     */
    public static void encryptState(State block, Key key) {
        addRoundKey(block, key);

        for (int i = 0; i < 9; i++) {
            subBytes(block);
            shiftRows(block);
            mixColumns(block);
            addRoundKey(block, key);
        }

        subBytes(block);
        shiftRows(block);
        addRoundKey(block, key);
    }

    /**
     * <p><h2><strong>Дешифрование 16-байтового блока.</strong></h2></p>
     * <p>Обратна к {@link #encryptState}</p>
     *
     * @param block - блок, который будет дешифрован.
     * @param key   - 128-битный ключ, из которого генерируются раундовые ключи.
     */
    public static void decryptState(State block, Key key) {
        addRoundKey(block, key);
        reverseShiftRows(block);
        reverseSubBytes(block);

        for (int i = 0; i < 9; i++) {
            addRoundKey(block, key);
            reverseMixColumns(block);
            reverseShiftRows(block);
            reverseSubBytes(block);
        }

        addRoundKey(block, key);
    }
}

