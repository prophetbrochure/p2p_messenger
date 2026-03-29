package Crypto;

/**
 * <p><h2><strong>HMAC-based Key Derivation Function - <br>
 * Позволяет получать ключи нужной длины из общего секрета.
 * </strong></h2></p>
 *
 * <pre>
 * Используется HMAC + SHA-256
 *
 * Из Изначального секрета любой длины генерируется промежуточный ключ
 * (PRK — pseudorandom key) длиной 256 бит
 * PRK=HMAC(salt,input keying material)
 * Где input keying material получен в результате DH
 * salt - случайная или заранее созданная соль, публичная.
 *
 * Формула Hmac:
 * HMAC(K,m) = H ((K′⊕ opad) ∥ H((K′ ⊕ ipad) ∥ m)
 * ∥ - конкатенация
 * opad, ipad - константы
 * K  - PRK
 * K' - PRK, расширенный до 512 байт
 * H  - хэш-функция (sha-256), возвращает 256 бит
 *
 * Extract:
 * T0 = ∅
 * T1 = HMAC(PRK, T0 ∥ info ∥ 0x01)
 * T2 = HMAC(PRK, T1 ∥ info ∥ 0x02)
 * Где info - информация о ключе (MAC или AES)
 * Затем, из T1, T2, ...
 * Получаем ключ ОKM = T1 ∥ T2 ∥ ...
 * ОКМ обрезаем до нужной длины
 * </pre>
 */
public class HKDF {

    //  чёт ниче не придумал пока

}
