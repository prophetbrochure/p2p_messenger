package Crypto;

import java.util.List;

/**
 * <p>Класс для тестирования функций криптографии.</p>
 */
public class Test {
    public static void main(String[] args) {

        Message m = new Message("abcdefghijklmnopqrstuvwxyz");

        List<State> spisok = m.getStatesList();

        for (State s : spisok) {
            System.out.println(Utils.toHexMatrix(s.matrix));
        }

        for (State s : spisok) {
            System.out.println(Utils.toBinMatrix(s.matrix));
        }


    }
}

