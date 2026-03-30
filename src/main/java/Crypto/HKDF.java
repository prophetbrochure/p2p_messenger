package Crypto;

/**
 * <p><h2><strong>HMAC-based Key Derivation Function - <br>
 * Позволяет получать ключи нужной длины из общего секрета.
 * </strong></h2></p>
 *
 * <pre>
 * Используется HMAC + SHA-256
 *
 * Из Изначального секрета любой длины генерируется промежуточный ключ
 * (PRK — pseudorandom key) длиной 256 бит
 * PRK=HMAC(salt,input keying material)
 * Где input keying material получен в результате DH
 * salt - случайная или заранее созданная соль, публичная.
 *
 * Формула Hmac:
 * HMAC(K,m) = H ((K′⊕ opad) ∥ H((K′ ⊕ ipad) ∥ m)
 * ∥ - конкатенация
 * opad, ipad - константы
 * K  - PRK
 * K' - PRK, расширенный до 512 байт
 * H  - хэш-функция (sha-256), возвращает 256 бит
 *
 * Extract:
 * T0 = ∅
 * T1 = HMAC(PRK, T0 ∥ info ∥ 0x01)
 * T2 = HMAC(PRK, T1 ∥ info ∥ 0x02)
 * Где info - информация о ключе (MAC или AES)
 * Затем, из T1, T2, ...
 * Получаем ключ ОKM = T1 ∥ T2 ∥ ...
 * ОКМ обрезаем до нужной длины
 * </pre>
 */
public class HKDF {

    //  чёт ниче не придумал пока

    /**
     * <p><h2><strong>Возвращает 32 байта хэша</strong></h2></p>
     *
     * @param data - массив byte любой длины
     * @return 32 байта хэша
     */
    public byte[] sha256(byte[] data) {
        byte[] paddedArray = getPaddedArray(data, 64);  // padding, подготавливаем массив.

        int h0 = Constants.initialH[0], h1 = Constants.initialH[1], h2 = Constants.initialH[2], h3 = Constants.initialH[3];
        int h4 = Constants.initialH[4], h5 = Constants.initialH[5], h6 = Constants.initialH[6], h7 = Constants.initialH[7];

        for (int blockNumber = 0; blockNumber < paddedArray.length / 64; blockNumber++) { // Для каждого блока
            //  Подготовка слов
            int[] wordsArray = new int[64];
            for (int i = 0; i < 16; i++) {    // Первые 16 слов
                wordsArray[i] = getWord(paddedArray, 64 * blockNumber + i * 4);
            }
            for (int i = 16; i < 64; i++) {    // Дополнительные 48 слов
                int s0 = rotr(wordsArray[i - 15], 7) ^ rotr(wordsArray[i - 15], 18) ^ shr(wordsArray[i - 15], 3);
                int s1 = rotr(wordsArray[i - 2], 17) ^ rotr(wordsArray[i - 2], 19) ^ shr(wordsArray[i - 2], 10);
                wordsArray[i] = wordsArray[i - 16] + s0 + wordsArray[i - 7] + s1;
            }

            //  Инициализация вспомогательных переменных
            int a = h0, b = h1, c = h2, d = h3, e = h4, f = h5, g = h6, h = h7;

            // Основной цикл, 64 раунда
            for (int i = 0; i < 64; i++) {
                int sigma0 = rotr(a, 2) ^ rotr(a, 13) ^ rotr(a, 22);
                int Ma = (a & b) ^ (a & c) ^ (b & c);
                int t2 = sigma0 + Ma;
                int sigma1 = rotr(e, 6) ^ rotr(e, 11) ^ rotr(e, 25);
                int Ch = (e & f) ^ ((~e) & g);  // ~ - побитовое НЕ
                int t1 = h + sigma1 + Ch + Constants.k[i] + wordsArray[i];

                h = g;
                g = f;
                f = e;
                e = d + t1;
                d = c;
                c = b;
                b = a;
                a = t1 + t2;
            }
            h0 += a;
            h1 += b;
            h2 += c;
            h3 += d;
            h4 += e;
            h5 += f;
            h6 += g;
            h7 += h;
        }

        return concat(h0, h1, h2, h3, h4, h5, h6, h7);
    }

    /**
     * <p><h2><strong>Добавляет к массиву padding, делая его кратным длине блока</strong></h2></p>
     *
     * <pre>
     *     После сообщения добавляется байт 0x80, затем нули, последние 8 байт - длина data.
     *     В итоге длина сообщения становится кратной blockSize (64 байта).
     * </pre>
     *
     * @param data      Исходный массив
     * @param blockSize Размер блока, которому будет кратный получившийся массив.
     * @return paddedArray - массив, кратный длине блока, в котором последние 8 бит - длина исходного массива.
     */
    byte[] getPaddedArray(byte[] data, int blockSize) {
        int paddingSize = blockSize - (data.length + 8) % blockSize;    //  +8 для длины data в конце

        byte[] paddedArray = new byte[data.length + paddingSize + 8];
        System.arraycopy(data, 0, paddedArray, 0, data.length);
        paddedArray[data.length] = (byte) 0x80; // После данных записывается байт 0x80

        long dataLength = (long) data.length * 8;   // Длина data в битах
        for (int i = 1; i <= 8; i++) { //  В последние 8 байт записывается длина data
            paddedArray[paddedArray.length - i] = (byte) dataLength;
            dataLength >>>= 8;
        }

        return paddedArray;
    }

    /**
     * <p><h2><strong>Получает слово (int) из массива байтов</strong></h2></p>
     *
     * @param array массив
     * @param index индекс, с которого начинается слово
     */
    int getWord(byte[] array, int index) {
        return ((array[index]     & 0xFF) << 24) |
                ((array[index + 1] & 0xFF) << 16) |
                ((array[index + 2] & 0xFF) << 8)  |
                (array[index + 3]  & 0xFF);
    }

    /**
     * <p><h2><strong>Циклический сдвиг слова вправо</strong></h2></p>
     *
     * @param x     слово
     * @param shift величина сдвига
     */
    int rotr(int x, int shift) {
        return (x >>> shift) | (x << (32 - shift));
    }

    /**
     * <p><h2><strong>Логический сдвиг слова вправо</strong></h2></p>
     *
     * @param x     слово
     * @param shift величина сдвига
     */
    int shr(int x, int shift) {
        return x >>> shift;
    }

    /**
     * <p><h2><strong>Соединяет слова (int) в массив байтов</strong></h2></p>
     */
    byte[] concat(int... numbers) {
        byte[] result = new byte[numbers.length * 4];
        for (int i = 0; i < numbers.length; i++) {
            result[4 * i] = (byte) (numbers[i] >>> 24);
            result[4 * i + 1] = (byte) (numbers[i] >>> 16);
            result[4 * i + 2] = (byte) (numbers[i] >>> 8);
            result[4 * i + 3] = (byte) (numbers[i]);
        }
        return result;
    }
}
