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
        AES128 cipher = new AES128(k);

        System.out.println("Ключ: " + Utils.toHexString(k.key));

        System.out.println("Исходный текст:");
        for (State s : spisok) {
            System.out.printf(s.toString());
        }

        for (State s : spisok) {
            cipher.encryptState(s);
        }

        System.out.println("\nШифротекст:");
        for (State s : spisok) {
            System.out.printf(s.toString());
        }

        for (State s : spisok) {
            cipher.decryptState(s);
        }

        System.out.println("\nРасшифрованный текст:");
        for (State s : spisok) {
            System.out.printf(s.toString());
        }

        cipher.encryptState(spisok.get(0));

//        System.out.println("\n" + Utils.toHexMatrix(spisok.getFirst().matrix));

//        AES.mixColumns(spisok.getFirst());
//
//        System.out.println(Utils.toHexMatrix(spisok.getFirst().matrix));
//
//        AES.reverseMixColumns(spisok.getFirst());
//
//        System.out.println(Utils.toHexMatrix(spisok.getFirst().matrix));


    }
}
