package Testing;

import Crypto.HKDF;

public class TestHKDF {
    public static void main(String[] args){
        HKDF hkdf = new HKDF();
        byte[] bytes = hkdf.sha256("abc".getBytes());

        System.out.println(Utils.toHexString(bytes));
    }
}

