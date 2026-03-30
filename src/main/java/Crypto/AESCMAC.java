package Crypto;

/**
 * <h2>AES-CMAC (RFC 4493)</h2>
 * <p>Вычисляет 16-байтовый Message Authentication Code на основе AES-128</p>
 *
 * <pre>
 * Зачем нужен MAC:
 *   - MAC гарантирует: если хоть один бит изменён -> проверка провалится
 *
 * </pre>
 *
 * <p>Схема Encrypt-then-MAC: сначала шифруем, потом считаем MAC от шифротекста
 * При дешифровании сначала проверяем MAC, потом дешифруем</p>
 */
public class AESCMAC {

    private final AES128 cipher;
    private final byte[] k1;
    private final byte[] k2;

    /**
     * @param macCipher экземпляр AES128 с отдельным ключом для MAC (нельзя использовать тот же ключ)
     *
     */
    public AESCMAC(AES128 macCipher) {
        this.cipher = macCipher;

        // Генерация подключей K1, K2 по RFC 4493
        byte[] L = new byte[16]; // нулевой блок
        State lState = new State(L);
        cipher.encryptState(lState);
        L = lState.matrix;

        k1 = doubleInGF(L);
        k2 = doubleInGF(k1);
    }

    /**
     * Вычисляет 16-байтовый MAC для сообщения
     */
    public byte[] compute(byte[] message) {
        int n = (message.length + 15) / 16; // количество блоков (округление вверх)
        boolean lastBlockComplete;

        if (n == 0) {
            n = 1;
            lastBlockComplete = false;
        } else {
            lastBlockComplete = (message.length % 16 == 0);
        }

        // Подготовка последнего блока
        byte[] lastBlock = new byte[16];
        int lastBlockStart = (n - 1) * 16;
        int bytesInLastBlock = message.length - lastBlockStart;

        if (lastBlockComplete) {
            // XOR последнего полного блока с K1
            System.arraycopy(message, lastBlockStart, lastBlock, 0, 16);
            xorInPlace(lastBlock, k1);
        } else {
            //дописываем 0x80 00 00 ... и XOR с K2
            if (bytesInLastBlock > 0) {
                System.arraycopy(message, lastBlockStart, lastBlock, 0, bytesInLastBlock);
            }
            lastBlock[bytesInLastBlock] = (byte) 0x80;
            // остальные байты уже 0x00
            xorInPlace(lastBlock, k2);
        }

        // CBC-MAC
        byte[] x = new byte[16]; // начальный вектор = 0

        for (int i = 0; i < n - 1; i++) {
            // XOR с текущим блоком сообщения
            for (int j = 0; j < 16; j++) {
                x[j] ^= message[i * 16 + j];
            }
            State state = new State(x);
            cipher.encryptState(state);
            x = state.matrix;
        }

        // Последний блок (уже подготовленный)
        for (int j = 0; j < 16; j++) {
            x[j] ^= lastBlock[j];
        }
        State state = new State(x);
        cipher.encryptState(state);

        return state.matrix;
    }

    /**
     * Удвоение в GF(2^128) с неприводимым полиномом x^128 + x^7 + x^2 + x + 1
     * Сдвиг влево на 1 бит, если старший бит был 1, XOR с 0x87
     */
    private static byte[] doubleInGF(byte[] input) {
        byte[] output = new byte[16];
        boolean carry = (input[0] & 0x80) != 0;

        for (int i = 0; i < 15; i++) {
            output[i] = (byte) ((input[i] << 1) | ((input[i + 1] & 0x80) >>> 7));
        }
        output[15] = (byte) (input[15] << 1);

        if (carry) {
            output[15] ^= (byte) 0x87;
        }
        return output;
    }

    private static void xorInPlace(byte[] a, byte[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] ^= b[i];
        }
    }
}
