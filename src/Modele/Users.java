/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;
import java.sql.Statement;
//import com.mysql.cj.xdevapi.Statement;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author PC
 */
public class Users {

    // Champs de la classe
    private int idUser;
    private String username;
    private String motDePasse;
    private String statut;
    private int idPrivilege;

    // 🔹 Constructeurs 🔹
    public Users() {
    }

    public Users(int idUser, String username, String motDePasse,String statut, int idPrivilege) {
        this.idUser = idUser;
        this.username = username;
        this.motDePasse = motDePasse;
        this.statut = statut;
        this.idPrivilege = idPrivilege;
    }

    // 🔹 Getters & Setters 🔹
    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    /**
     * Définit le mot de passe en le chiffrant via AES (ex. Base64).
     * @param motDePasse Mot de passe en clair à chiffrer
     */
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = BCrypt.hashpw(motDePasse, BCrypt.gensalt(10));
    }

    public int getIdPrivilege() {
        return idPrivilege;
    }
    public void setIdPrivilege(int idPrivilege) {
        this.idPrivilege = idPrivilege;
    }
    
    public static int getPrivilegeId(String roleName) {
        switch (roleName.toLowerCase()) {
            case "admin":
                return 1;
            case "gerant":
                return 2;
            case "utilisateur":
                return 3;
            default:
                throw new IllegalArgumentException("Rôle inconnu : " + roleName);
        }
    }

    /**
     * Vérifie si le mot de passe en clair passé en paramètre
     * correspond au mot de passe chiffré stocké (déchiffrage AES).
     * @param plainPassword Mot de passe en clair à vérifier
     * @return true si correspond, false sinon
     */
    public boolean verifyPassword(String plainPassword) {
        try {
            // Vérifie si le mot de passe en base est en format BCrypt
            if (motDePasse.startsWith("$2a$")) {
                return BCrypt.checkpw(plainPassword, motDePasse);
            } else {
                System.out.println("⚠ Mot de passe non haché avec BCrypt : " + motDePasse);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // -------------------------------------------------
    // 🔹 CRUD Methods (utilitaires) 🔹
    // -------------------------------------------------

    // ✅ CREATE : Ajouter un nouvel utilisateur
   public static int ajouterUtilisateur(String username, String password,String statut, int idPrivilege) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int generatedUserId = -1;

        try {
            conn = Parametres.getConnection(); 
            // Vérifier si le mot de passe est déjà un hash BCrypt
            if (!password.startsWith("$2a$")) {
                password = BCrypt.hashpw(password, BCrypt.gensalt(10));
            }

            String sql = "INSERT INTO users (username, password, statut, id_privilege) VALUES (?, ?, ?,?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, password);  // attention au hachage si vous ne l’avez pas déjà fait
            ps.setString(3, statut); // Par défaut, le user est "actif"
            ps.setInt(4, idPrivilege);

            int rowsAffected = ps.executeUpdate();

            // Récupérer la clé générée
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedUserId = rs.getInt(1);
            }
            System.out.println("✅ Utilisateur ajouté avec succès, id_user = " + generatedUserId);

        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }

        return generatedUserId;
    }
    // ✅ READ : Récupérer un utilisateur par son ID
    public static Users getUserById(int id) {
        try {
            Connection conn = Parametres.getConnection();
            String sql = "SELECT * FROM Users WHERE id_user = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Users(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("statut"),    
                    rs.getInt("id_privilege")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ READ : Récupérer un utilisateur par son username
    public static Users getUserByUsername(String username) throws ClassNotFoundException {
        try {
            Connection conn = Parametres.getConnection();
            String sql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Users(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("statut"),   
                    rs.getInt("id_privilege")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ READ : Liste tous les utilisateurs
    public static List<Users> getAllUsers() {
        List<Users> users = new ArrayList<>();
        try {
            Connection conn = Parametres.getConnection();
            String sql = "SELECT * FROM Users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(new Users(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("statut"),   
                    rs.getInt("id_privilege")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // ✅ UPDATE : Modifier un utilisateur
    public static boolean updateUser(int id, String username, String password, String statut ,int idPrivilege) {
        try {
            Connection conn = Parametres.getConnection();
            // Vérifier si le mot de passe est déjà un hash BCrypt
            if (!password.startsWith("$2a$")) {
                password = BCrypt.hashpw(password, BCrypt.gensalt(10));
            }

            String sql = "UPDATE Users SET username = ?, password = ?,statut = ?,id_privilege = ? WHERE id_user = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, statut);//🔹 Ajout du statut
            ps.setInt(4, idPrivilege);
            ps.setInt(5, id); // 🔹 Cinquième paramètre
            ps.executeUpdate();

            System.out.println("✅ Utilisateur mis à jour !");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ DELETE : Supprimer un utilisateur
    public static boolean deleteUser(int id) {
        try {
            Connection conn = Parametres.getConnection();
            String sql = "DELETE FROM Users WHERE id_user = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            System.out.println("✅ Utilisateur supprimé !");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔹 Méthode pour chiffrer les anciens mots de passe en base
    public static void updatePasswordsToBCrypt() throws SQLException, ClassNotFoundException {
        try (Connection conn = Parametres.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id_user, password FROM Users");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("id_user");
                String currentPassword = rs.getString("password");

                // Vérifier si le mot de passe est déjà en format BCrypt
                if (currentPassword.startsWith("$2a$")) {
                    continue; // Déjà sécurisé, on ignore
                }

                // Hachage du mot de passe et mise à jour
                String hashedPassword = BCrypt.hashpw(currentPassword, BCrypt.gensalt(10));

                try (PreparedStatement updatePs = conn.prepareStatement(
                        "UPDATE Users SET password = ? WHERE id_user = ?")) {
                    updatePs.setString(1, hashedPassword);
                    updatePs.setInt(2, userId);
                    updatePs.executeUpdate();
                    System.out.println("Mot de passe mis à jour pour user ID: " + userId);
                }
            }
            System.out.println("✅ Tous les mots de passe sont maintenant sécurisés avec BCrypt !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean updatePassword(int userId, String newPassword) throws SQLException, ClassNotFoundException {
        try (Connection conn = Parametres.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Users SET password = ? WHERE id_user = ?")) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 🔹 Méthode pour générer un mot de passe temporaire sécurisé
    public static String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        StringBuilder tempPassword = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        for (int i = 0; i < 8; i++) { // Longueur du mot de passe temporaire
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        return tempPassword.toString();
    }
    
    public static boolean setUserStatus(int idUser, boolean isActive) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE users SET statut = ? WHERE id_user = ?";

        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isActive ? "actif" : "inactif"); // "actif" ou "inactif"
            stmt.setInt(2, idUser);

            return stmt.executeUpdate() > 0; // Renvoie true si une ligne a été mise à jour
        }
    }
    
    public static Users authenticate(String username, String password) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    String statut = rs.getString("statut");

                    // Vérifier si l'utilisateur est actif
                    if (!"actif".equals(statut)) {
                        JOptionPane.showMessageDialog(null, "⛔ Votre compte est inactif. Contactez un administrateur.");
                        return null;
                    }

                    // Vérifier le mot de passe avec BCrypt
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return new Users(
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            hashedPassword,
                            rs.getString("statut"),    
                            rs.getInt("id_privilege")
                        );
                    }
                }
            }
        }
        return null; // Échec de l'authentification
    }
}
