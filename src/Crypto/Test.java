package Crypto;

public class Test {
    public static void main(String[] args) {

        for (int i = 0; i < 8; i++) {
            Key key = new Key(8);
            key.generateKey();
            System.out.println(key.toHexString());
            System.out.println(key.toBinString());
        }
    }
}
