/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import Modele.ChiffreDechiffre;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

/**
 *
 * @author PC
 */
public class Configuration {
  
    private static final String CONFIG_FILE_ENCRYPTED = "src/Ressources/config.enc";
    private static Properties props = new Properties(); // Stocke les propriétés en mémoire

    /**
     * Charge les paramètres de connexion à partir de `config.enc`.
     */
    public static void loadProperties() throws Exception {
        File fichierChiffre = new File(CONFIG_FILE_ENCRYPTED);

        if (!fichierChiffre.exists()) {
            throw new FileNotFoundException("❌ ERREUR : Fichier `config.enc` introuvable !");
        }

        // Lire et décrypter `config.enc`
        String encryptedContent = new String(Files.readAllBytes(fichierChiffre.toPath()), StandardCharsets.UTF_8);
        String decryptedContent = ChiffreDechiffre.dechiffrerString(encryptedContent);

        // Charger les propriétés depuis le contenu déchiffré
        try (InputStream input = new ByteArrayInputStream(decryptedContent.getBytes(StandardCharsets.UTF_8))) {
            props.load(input);
        }

        System.out.println("🔓 Paramètres chargés avec succès depuis `config.enc` !");
    }

    /**
     * Enregistre les paramètres chiffrés dans `config.enc` (sans laisser de fichier en clair).
     */
    public static void saveAndEncryptProperties() throws Exception {
        // Convertir les propriétés en texte brut
        StringWriter writer = new StringWriter();
        props.store(writer, "Configuration sécurisée");
        String plainContent = writer.toString();

        // Chiffrer le contenu et l'enregistrer dans `config.enc`
        String encryptedContent = ChiffreDechiffre.chiffrerString(plainContent);
        try (FileWriter fw = new FileWriter(CONFIG_FILE_ENCRYPTED)) {
            fw.write(encryptedContent);
        }

        System.out.println("✅ `config.enc` a été généré avec succès !");
    }

    /**
     * Définit une propriété (clé-valeur) en mémoire.
     */
    public static void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    /**
     * Récupère une propriété depuis les paramètres chargés.
     */
    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     * Vérifie le contenu chiffré et déchiffré de `config.enc` (à des fins de test).
     */
    public static void debugEncryptedFile() throws Exception {
        File fichierChiffre = new File(CONFIG_FILE_ENCRYPTED);
        if (!fichierChiffre.exists()) {
            System.err.println("❌ ERREUR : `config.enc` n'existe pas !");
            return;
        }

        String encryptedContent = new String(Files.readAllBytes(fichierChiffre.toPath()), StandardCharsets.UTF_8);
        System.out.println("🔐 Contenu chiffré de `config.enc` :\n" + encryptedContent);

        String decryptedContent = ChiffreDechiffre.dechiffrerString(encryptedContent);
        System.out.println("🔓 Contenu déchiffré :\n" + decryptedContent);
    }

    /*public static void main(String[] args) {
        try {
            // 1️⃣ Définir les paramètres de connexion (remplace par tes vraies valeurs)
            setProperty("db.driver", "org.postgresql.Driver");
            setProperty("db.url", "jdbc:postgresql://localhost:5432/gestion_stock");
            setProperty("db.user", "postgres");
            setProperty("db.password", "admin123");  // ⚠️ Remplace par ton vrai mot de passe PostgreSQL

            // 2️⃣ Chiffrer et sauvegarder
            saveAndEncryptProperties();

            // 3️⃣ Tester la lecture des paramètres chiffrés
            loadProperties();
            debugEncryptedFile();

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }*/
}
