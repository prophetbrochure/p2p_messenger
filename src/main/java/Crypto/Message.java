package Crypto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Message {
    String messageText;
    byte[] paddedBytes;

    public Message(String string) {
        this.messageText = string;
        byte[] unpaddedBytes = string.getBytes(StandardCharsets.UTF_8);

        int paddingSize = 16 - unpaddedBytes.length % 16;
        this.paddedBytes = new byte[unpaddedBytes.length + paddingSize];
        System.arraycopy(unpaddedBytes, 0, this.paddedBytes, 0, unpaddedBytes.length);
        for (int i = unpaddedBytes.length; i < paddedBytes.length; i++) {   // Используется padding PKCS#7
            this.paddedBytes[i] = (byte) paddingSize;
        }
    }

    /**
     * <p><h2><strong>Разбивает сообщение на список объектов класса State, чтобы работать с набором матриц 4x4</strong></h2></p>
     * <p>Используется padding PKCS#7</p>
     *
     * @see State
     */
    public List<State> getStatesList() {
        List<State> result = new ArrayList<>();
        int pointer = 0;


        while (pointer < paddedBytes.length) {
            State currentState = new State();
            result.add(currentState);

            for (int i = 0; i < 16 && pointer < paddedBytes.length; i++, pointer++) {
                currentState.matrix[(i % 4) * 4 + (i / 4)] = paddedBytes[pointer];
            }
        }
        return result;
    }

    /**
     * <p><h2><strong>Преобразует List<State> в String, с учётом padding'а</strong></h2></p>
     */
    public static String getMessage(List<State> statesList) {
        byte[] bytes = new byte[16 * statesList.size() - statesList.getLast().matrix[15]];

        for (int stateNumber = 0; stateNumber < statesList.size(); stateNumber++) {
            for (int i = 0; i < 16 && (stateNumber * 16 + i < bytes.length); i++) {
                bytes[stateNumber * 16 + i] = statesList.get(stateNumber).matrix[(i % 4) * 4 + (i / 4)];
            }
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
