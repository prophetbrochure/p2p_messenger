package Crypto;

import java.util.LinkedList;
import java.util.List;

public class Message {
    public char[] value;

    Message(String value) {
        this.value = value.toCharArray();
    }

    /**
     * <p>Разбивает сообщение на список объектов класса State,
     * чтобы работать с набором матриц 4x4</p>
     *
     * @see State
     */
    List<State> getStatesList() {
        List<State> result = new LinkedList<>();
        int pointer = 0;

        while (pointer < value.length) {
            State currentState = new State();

            for (int i = 0; i < 8 && pointer < value.length; i++, pointer++) {
                char c = value[pointer];

                currentState.matrix[8 * (i % 2) + (i / 2)] = (byte) (c >> 8);
                currentState.matrix[8 * (i % 2) + 4 + (i / 2)] = (byte) c;
            }
            result.add(currentState);
        }

        return result;
    }

}
