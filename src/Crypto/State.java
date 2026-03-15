package Crypto;

/**
 * <p>Класс для работы с шифрованием. Содержит массив byte[16] matrix.</p>
 *
 * @see Key
 */
public class State {
    public byte[] matrix;

    State(){
        this.matrix = new byte[16];
    }

    /**
     * <p>Преобразует 16 байтов из State в строку из 8 char'ов.</p>
     * <p>Не добавляет пустые байты 00, которые получаются при разборе последних символов строки, длина которой не кратна 0.</p>
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 8; i++){
            char c = (char) ((matrix[8 * (i % 2) + (i / 2)] << 8) | (matrix[8 * (i % 2) + 4 + (i / 2)] & 0xFF));

            if(c != 0){
                sb.append(c);
            }
        }

        return sb.toString();
    }

}
