package Crypto;

/**
 * <p>Класс, содержащий функции для визуального представления массивов/матриц.
 * Необязателен для работы финальной версии программы, но полезен для отладки.</p>
 */
public class Utils {
    /**
     * <p>Представляет массив байтов в виде строки, содержащей шестнадцатеричное представление этих байтов с пробелами между ними.</p>
     * @param bytes массив байтов, который будет представлен.
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * <p>Представляет массив байтов в виде строки, содержащей двоичное представление этих байтов с пробелами между ними.</p>
     * @param bytes массив байтов, который будет представлен.
     */
    public static String toBinString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(
                    String.format("%8s",
                            Integer.toBinaryString(b & 0xFF)
                    ).replace(' ', '0')
            );
            sb.append(' ');
        }
        return sb.toString();
    }
}
