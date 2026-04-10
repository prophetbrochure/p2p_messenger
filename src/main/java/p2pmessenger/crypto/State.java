package p2pmessenger.crypto;

/**
 * <p>Класс для работы с шифрованием. Содержит массив byte[16] matrix.</p>
 * <p>
 *     Байты записываются в следующем порядке:<br>
 *     1A     3A    5A     7A  <br>
 *     1B     3B    5B     7B  <br>
 *     2A     4A    6A     8A  <br>
 *     2B     4B    6B     8B
 * </p>
 *
 * @see Key
 */
public class State {
    public byte[] matrix;

    public State() {
        this.matrix = new byte[16];
    }

    public State(byte[] data) {
        if (data.length != 16) {
            throw new IllegalArgumentException("State must be 16 bytes");
        }
        this.matrix = data.clone();
    }
    /**
     * <p>Преобразует 16 байтов из State в строку из 8 char'ов</p>
     * <p>Не добавляет пустые байты 00, которые получаются при разборе последних символов строки, длина которой не кратна 0.</p>
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            char c = (char) ((matrix[8 * (i % 2) + (i / 2)] << 8) | (matrix[8 * (i % 2) + 4 + (i / 2)] & 0xFF));

            if (c != 0) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
