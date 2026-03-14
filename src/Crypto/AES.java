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

    }

    /**
     * <p><h2><strong>Операция, обратная subBytes.</strong></h2></p>
     */
    private static void reverseSubBytes(State block) {

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

    }

    /**
     * <p><h2><strong>Операция, обратная addRoundKey.</strong></h2></p>
     * <p>Вроде бы XOR обратен сам себе, так что этот метод, возможно, не нужен.</p>
     */
    private static void reverseAddRoundKey(State block, Key roundKey) {

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
     * @param block - блок, который будет зашифрован.
     * @param key   - 128-битный ключ, из которого генерируются раундовые ключи.
     */
    public static void encryptState(State block, Key key) {

    }

    /**
     * <p><h2><strong>Дешифрование 16-байтового блока.</strong></h2></p>
     *
     * @param block - блок, который будет дешифрован.
     * @param key - 128-битный ключ, из которого генерируются раундовые ключи.
     */
    public static void decryptState(State block, Key key) {

    }
}
