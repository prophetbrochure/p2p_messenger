package Crypto;

import java.util.List;

/**
 * <p>Класс для тестирования функций криптографии.</p>
 */
public class Test {
    public static void main(String[] args) {

        Message m = new Message("абвгдежзиклмнопр");

        List<State> spisok = m.getStatesList();

        // Текст -> байты
        for (State s : spisok) {
            System.out.println(Utils.toHexString(s.matrix));
        }

        // Байты -> текст
        for(State s : spisok){
            System.out.println(s.toString());
        }


    }
}

