package p2pmessenger.crypto;

/**
 * <h2>Режим CTR</h2>
 * <p>Превращает блочный шифр AES-128 в потоковый</p>
 *
 * <pre>
 * Для каждого блока открытого текста:
 *   1. Формируем блок [nonce | counter]
 *   2. Шифруем его AES-128, получаем keystream
 *   3. XOR keystream с блоком открытого текста
 * </pre>
 *
 * <p>Шифрование и дешифрование - одна и та же операция (XOR)</p>
 */
public class CTRMode {

    private final AES128 cipher;

    public CTRMode(AES128 cipher) {
        this.cipher = cipher;
    }

    /**
     * Шифрует/дешифрует данные в режиме CTR
     * Операция симметрична: encrypt(encrypt(data)) = data
     *
     * @param data          входные данные произвольной длины
     * @param nonce         12-байтовый nonce
     * @param counterStart  начальное значение счётчика (обычно 1, 0 зарезервирован для MAC)
     * @return              результат XOR с keystream
     */
    public byte[] process(byte[] data, Nonce nonce, int counterStart) {
        byte[] result = new byte[data.length];
        int fullBlocks = data.length / 16;
        int remainder = data.length % 16;

        for (int i = 0; i < fullBlocks; i++) {
            byte[] keystreamBlock = generateKeystreamBlock(nonce, counterStart + i);
            for (int j = 0; j < 16; j++) {
                result[i * 16 + j] = (byte) (data[i * 16 + j] ^ keystreamBlock[j]);
            }
        }

        // Последний неполный блок
        if (remainder > 0) {
            byte[] keystreamBlock = generateKeystreamBlock(nonce, counterStart + fullBlocks);
            for (int j = 0; j < remainder; j++) {
                result[fullBlocks * 16 + j] = (byte) (data[fullBlocks * 16 + j] ^ keystreamBlock[j]);
            }
        }

        return result;
    }

    /**
     * Генерирует один 16-байтовый блок keystream
     */
    private byte[] generateKeystreamBlock(Nonce nonce, int counter) {
        State counterBlock = nonce.toCounterBlock(counter);
        cipher.encryptState(counterBlock);
        return counterBlock.matrix;
    }
}
