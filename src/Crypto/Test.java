package Crypto;

/**
 * <p>Класс для тестирования функций криптографии.</p>
 */
public class Test {
    public static void main(String[] args) {

        for (int i = 0; i < 8; i++) {
            Key key = new Key(8);
            key.generateKey();
            Key copy = key.getCopy();

            System.out.println(Utils.toBinString(key.key));
            System.out.println(Utils.toBinString(copy.key));

            System.out.println(Utils.toHexString(key.key));
            System.out.println(Utils.toHexString(copy.key) + "\n\n");
        }

        Message m = new Message("Часть1. Часть2. Часть3.");

        State s1 = m.getNextState();
        State s2 = m.getNextState();
        State s3 = m.getNextState();

        System.out.println(Utils.toHexString(s1.matrix) + Utils.toHexString(s2.matrix) + Utils.toHexString(s3.matrix));
        System.out.println(s1.toString() + s2 + s3);    // Лол, как это работает???


    }
}
