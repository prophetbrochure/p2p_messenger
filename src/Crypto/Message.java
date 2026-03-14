package Crypto;

public class Message {
    public char[] value;
    private int pointer;

    Message(String value) {
        this.value = value.toCharArray();

        this.pointer = 0;
    }

    /**
     * <p>Используется для преобразования сообщения в набор объектов класса State,
     * чтобы работать со строкой как с массивом байтов для шифрования.</p>
     * <p>Повторное использование функции представляет следующие 8 символов в виде State.</p>
     *
     * <p>Если строка закончилась, возвращает null.</p>
     *
     * @see State
     */
    State getNextState() {
        if (pointer >= value.length) {
            return null;
        }
        State result = new State();
        for (int i = 0; pointer < value.length && i < 8; i++, pointer++) {  // 1 char = 2 байта
            char c = value[pointer];
            result.matrix[2 * i] = (byte) (c >> 8);
            result.matrix[2 * i + 1] = (byte) c;
        }
        return result;
    }
}
