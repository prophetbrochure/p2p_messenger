package Crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * <p><h2><strong>Протокол обмен ключами Диффи-Хеллмана</strong></h2></p>
 * <p>privateKey - свой приватный ключ, publicKeyB - Публичный ключ собеседника.</p>
 */
public class DH {
    private final BigInteger module;

    private final BigInteger privateKey;
    private final BigInteger publicKey;

    private BigInteger sharedSecret;

    public DH(BigInteger generator, BigInteger module) {
        this.privateKey = new BigInteger(2048, new SecureRandom());

        this.module = module;
        this.publicKey = generator.modPow(this.privateKey, this.module);
    }

    /**
     * <p><h2><strong>Возвращает публичный ключ</strong></h2></p>
     */
    public BigInteger getPublicKey() {
        return publicKey;
    }

    /**
     * <p><h2><strong>Генерирует общий ключ</strong></h2></p>
     *
     * @param publicKeyB - публичный ключ собеседника.
     */
    public void generateSharedSecret(BigInteger publicKeyB) {
        sharedSecret = publicKeyB.modPow(privateKey, module);
    }

    /**
     * <p><h2><strong>Возвращает общий секрет.</strong></h2></p>
     */
    public byte[] getSharedSecret() {
        return sharedSecret.toByteArray();
    }
}
