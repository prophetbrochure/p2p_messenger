package Crypto;

import java.util.List;

/**
 * <p>Класс для тестирования функций криптографии.</p>
 */
public class Test {
    public static void main(String[] args) {

        Message m = new Message("abcdefghijklmnopqrstuvwxyz");
        List<State> spisok = m.getStatesList();
        Key k = new Key(16);
        k.generateKey();


        System.out.println("Исходный текст:");
        for (State s : spisok) {
            System.out.printf(s.toString());
        }

        for (State s : spisok) {
            AES.encryptState(s, k);
        }

        System.out.println("\nШифротекст:");
        for (State s : spisok) {
            System.out.printf(s.toString());
        }

        for (State s : spisok) {
            AES.decryptState(s, k);
        }

        System.out.println("\nРасшифрованный текст:");
        for (State s : spisok) {
            System.out.printf(s.toString());
        }

    }
}

