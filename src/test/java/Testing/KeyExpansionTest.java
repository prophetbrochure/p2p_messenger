package Testing;

import Crypto.AES128;
import Crypto.Key;
import Crypto.Message;
import Crypto.State;
//import Crypto.Testing.Utils;

import java.util.List;

// Требует, чтобы keysList в AES128 был доступен.
public class KeyExpansionTest {
    public static void main(String[] args){
        Key k = new Key(16);

        k.key[0] = (byte) 0x2b; k.key[1] = (byte) 0x28; k.key[2] = (byte) 0xab; k.key[3] = (byte) 0x09;
        k.key[4] = (byte) 0x7e; k.key[5] = (byte) 0xae; k.key[6] = (byte) 0xf7; k.key[7] = (byte) 0xcf;
        k.key[8] = (byte) 0x15; k.key[9] = (byte) 0xd2; k.key[10] = (byte) 0x15; k.key[11] = (byte) 0x4f;
        k.key[12] = (byte) 0x16; k.key[13] = (byte) 0xa6; k.key[14] = (byte) 0x88; k.key[15] = (byte) 0x3c;

        AES128 cipher = new AES128(k);

        for(int i = 0; i < 11; i++){
            System.out.println(Utils.toHexMatrix(cipher.keysList.get(i).key) + "\n");
        }


        State block = new State();
        block.matrix[0] = (byte) 0x32; block.matrix[1] = (byte) 0x88; block.matrix[2] = (byte) 0x31; block.matrix[3] = (byte) 0xe0;
        block.matrix[4] = (byte) 0x43; block.matrix[5] = (byte) 0x5a; block.matrix[6] = (byte) 0x31; block.matrix[7] = (byte) 0x37;
        block.matrix[8] = (byte) 0xf6; block.matrix[9] = (byte) 0x30; block.matrix[10] = (byte) 0x98; block.matrix[11] = (byte) 0x07;
        block.matrix[12] = (byte) 0xa8; block.matrix[13] = (byte) 0x8d; block.matrix[14] = (byte) 0xa2; block.matrix[15] = (byte) 0x34;

        System.out.println("Незашифрованный блок");
        System.out.println(Utils.toHexMatrix(block.matrix));

        cipher.encryptState(block);

        System.out.println("Зашифрованный блок");
        System.out.println(Utils.toHexMatrix(block.matrix));

        Message msg = new Message("Hello 123 Привет 你好世界 こんにちは éëê \uD83D\uDC4B\uD83D\uDC4D\uD83D\uDE00\uD83D\uDD25\uD83D\uDCBB\uD83D\uDE80\uD83C\uDF0D");

        List<State> stateList = msg.getStatesList();

        System.out.println(Message.getMessage(stateList));

    }
}
