/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
/**
 *
 * @author PC
 */
public class Adresse {
     private int idAdresse;     // PK auto-increment
    private int numRue;
    private String nomRue;
    private String localite;
    private String pays;
    private String codePostal;

    // --- Constructeurs ---
    public Adresse() {
        // constructeur vide si besoin
    }

    // Pour créer une adresse existante en mémoire (par ex. après SELECT)
    public Adresse(int idAdresse, int numRue, String nomRue, String localite, String pays, String codePostal) {
        this.idAdresse = idAdresse;
        this.numRue = numRue;
        this.nomRue = nomRue;
        this.localite = localite;
        this.pays = pays;
        this.codePostal = codePostal;
    }

    // Pour créer une adresse avant insertion (idAdresse = 0 => pas encore en BD)
    public Adresse(int numRue, String nomRue, String localite, String pays, String codePostal) {
        this.numRue = numRue;
        this.nomRue = nomRue;
        this.localite = localite;
        this.pays = pays;
        this.codePostal = codePostal;
    }

    // --- Getters / Setters ---
    public int getIdAdresse() {
        return idAdresse;
    }
    public void setIdAdresse(int idAdresse) {
        this.idAdresse = idAdresse;
    }

    public int getNumRue() {
        return numRue;
    }
    public void setNumRue(int numRue) {
        this.numRue = numRue;
    }

    public String getNomRue() {
        return nomRue;
    }
    public void setNomRue(String nomRue) {
        this.nomRue = nomRue;
    }

    public String getLocalite() {
        return localite;
    }
    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public String getPays() {
        return pays;
    }
    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getCodePostal() {
        return codePostal;
    }
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    // --- Méthodes CRUD en statique ou dans d'autres classes DAO ---

    // CREATE : insère une nouvelle adresse en BD et récupère l'ID auto-généré
    public static int createAdresse(Adresse adr) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Adresse (num_rue, nom_rue, localite, pays, code_postal) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setInt(1, adr.getNumRue());
            stmt.setString(2, adr.getNomRue());
            stmt.setString(3, adr.getLocalite());
            stmt.setString(4, adr.getPays());
            stmt.setString(5, adr.getCodePostal());
            stmt.executeUpdate();

            // Récupérer la clé générée
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    adr.setIdAdresse(generatedId);
                    return generatedId;
                }
            }
        }
        return 0; // échec
    }

    // READ : récupérer une adresse par son id_adresse
    public static Adresse getAdresseById(int idAdresse) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Adresse WHERE id_adresse = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idAdresse);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Adresse(
                        rs.getInt("id_adresse"),
                        rs.getInt("num_rue"),
                        rs.getString("nom_rue"),
                        rs.getString("localite"),
                        rs.getString("pays"),
                        rs.getString("code_postal")
                    );
                }
            }
        }
        return null; // pas trouvé
    }

    // UPDATE : mettre à jour une adresse existante
    public static boolean updateAdresse(Adresse adr) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Adresse "
                   + "SET num_rue = ?, nom_rue = ?, localite = ?, pays = ?, code_postal = ? "
                   + "WHERE id_adresse = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, adr.getNumRue());
            stmt.setString(2, adr.getNomRue());
            stmt.setString(3, adr.getLocalite());
            stmt.setString(4, adr.getPays());
            stmt.setString(5, adr.getCodePostal());
            stmt.setInt(6, adr.getIdAdresse());
            int rows = stmt.executeUpdate();
            return (rows > 0);
        }
    }

    // DELETE : supprimer une adresse
    public static boolean deleteAdresse(int idAdresse) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Adresse WHERE id_adresse = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idAdresse);
            int rows = stmt.executeUpdate();
            return (rows > 0);
        }
    }
}
