/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

/**
 *
 * @author PC
 */


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

public class ChiffreDechiffre {
private static final String SECRET_KEY = "1234567890123456"; // 16 chars = 128 bits

    // Chiffre une chaîne de caractères et renvoie la chaîne Base64
    public static String chiffrerString(String texteClair) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        Key secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] texteClairBytes = texteClair.getBytes("UTF-8");
        byte[] texteChiffre = cipher.doFinal(texteClairBytes);

        // Encode en Base64 pour stockage en BD
        return Base64.getEncoder().encodeToString(texteChiffre);
    }

    // Déchiffre une chaîne Base64 et renvoie le texte en clair
    public static String dechiffrerString(String texteChiffreBase64) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        Key secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] cipherBytes = Base64.getDecoder().decode(texteChiffreBase64);
        byte[] texteDechiffre = cipher.doFinal(cipherBytes);

        return new String(texteDechiffre, "UTF-8");
    }
}
