package Crypto;

/**
 * <p>Класс для шифрования блока из 16 байт.</p>
 * <p>Реализованы методы:
 * <ol>
 *     <li>subBytes - подстановка</li>
 *     <li>shiftRows - сдвиг</li>
 *     <li>mixColumns - умножение матриц</li>
 *     <li>addRoundKey - XOR с ключом</li>
 * </ol>
 * </p>
 */
public class AES {
    /**
     * <p>Замена байтов на другие согласно таблице коммутации.</p>
     */
    private static void subBytes(State block) {
        for (int i = 0; i < 16; i++) {
            block.matrix[i] = Constants.SBOX[block.matrix[i] & 0xFF];
        }
    }

    /**
     * <p><h2><strong>Операция, обратная subBytes.</strong></h2></p>
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
     */
    private static void shiftRows(State block) {

    }

    /**
     * <p><h2><strong>Операция, обратная shiftRows.</strong></h2></p>
     */
    private static void reverseShiftRows(State block) {

    }


    /**
     * <p><h2><strong>Умножение на матрицу.</strong></h2></p>
     * <p>Каждый столбец умножается на фиксированную матрицу 4×4 в поле GF(2⁸)</p>
     */
    private static void mixColumns(State block) {

    }

    /**
     * <p><h2><strong>Операция, обратная mixColumns.</strong></h2></p>
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
     * <p><h2><strong>Шифрование 16-байтового блока в 10 раундов с использованием 128-битного ключа</strong></h2></p>
     *
     * <p>
     * Шаги шифрования на каждом раунде: <ol>
     * <li> SubBytes</li>
     * <li> ShiftRows</li>
     * <li> MixColumns (кроме последнего раунда)</li>
     * <li> AddRoundKey </li>
     * </ol></p>
     *
     * @param block - блок, который будет зашифрован.
     * @param key   - 128-битный ключ, из которого генерируются раундовые ключи.
     */
    public static void encryptState(State block, Key key) {
        addRoundKey(block, key);

        for(int i = 0; i < 9; i++){
            subBytes(block);
            addRoundKey(block, key);
        }

        subBytes(block);
        addRoundKey(block, key);
    }

    /**
     * <p><h2><strong>Дешифрование 16-байтового блока.</strong></h2></p>
     *
     * @param block - блок, который будет дешифрован.
     * @param key   - 128-битный ключ, из которого генерируются раундовые ключи.
     */
    public static void decryptState(State block, Key key) {
        addRoundKey(block, key);
        reverseSubBytes(block);

        for(int i = 0; i < 9; i++){
            addRoundKey(block, key);
            reverseSubBytes(block);
        }

        addRoundKey(block, key);
    }
}

