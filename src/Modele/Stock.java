/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author PC
 */
public class Stock {
    private int idStock;
    private String designation;
    private int quantite_stock;
    private int codeBarres;
    private double prixUnitaire;
    private double tauxTVA;
    private int idCategorie;
    //private double total;
    //private int quantiteAchetee;

    // ✅ Constructeurs
    public Stock() {}
    
   

    public Stock(int idStock, String designation, int quantite_stock, int codeBarres, double prixUnitaire, double tauxTVA, int idCategorie) {
        this.idStock = idStock;
        this.designation = designation;
        this.quantite_stock = quantite_stock;
        this.codeBarres = codeBarres;
        this.prixUnitaire = prixUnitaire;
        this.tauxTVA = tauxTVA;
        this.idCategorie = idCategorie;
    }

    // ✅ Getters et Setters
    public int getIdStock() { return idStock; }
    public void setIdStock(int idStock) { this.idStock = idStock; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public int getQuantite_stock() { return quantite_stock; }
    public void setQuantite(int quantite) { this.quantite_stock = quantite; }

    public int getCodeBarres() { return codeBarres; }
    public void setCodeBarres(int codeBarres) { this.codeBarres = codeBarres; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public double getTauxTVA() { return tauxTVA; }
    public void setTauxTVA(double tauxTVA) { this.tauxTVA = tauxTVA; }

    public int getIdCategorie() { return idCategorie; }
    public void setIdCategorie(int idCategorie) { this.idCategorie = idCategorie; }
    
    private String nomCategorie; // Nouvelle variable pour stocker le NOM de la catégorie

    public String getNomCategorie() { 
        return nomCategorie; 
    }

    public void setNomCategorie(String nomCategorie) { 
        this.nomCategorie = nomCategorie; 
    }
    
   

    // ✅ CRUD 
    
    // 🔹 Ajouter un stock
    public static boolean ajouterStock(String designation, int quantite, int codeBarres, double prix, double tva, int idCategorie) {
        String sql = "INSERT INTO Stock (designation, quantite_stock, prix_unitaire, taux_tva, code_barres, id_categorie) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, designation);
            pstmt.setInt(2, quantite);
            pstmt.setDouble(3, prix);
            pstmt.setDouble(4, tva);
            pstmt.setInt(5, codeBarres);
            pstmt.setInt(6, idCategorie);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Stock ajouté avec succès !");
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'ajout du stock : " + e.getMessage());
            return false;
        }
    }

    // 🔹 Modifier un stock
    public static boolean modifierStock(int id, String designation, int quantite, double prix, double tva, int codeBarres, int idCategorie) {
        String sql = "UPDATE Stock SET designation=?, quantite_stock=?, prix_unitaire=?, taux_tva=?, code_barres=?, id_categorie=? WHERE id_stock=?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, designation);
            pstmt.setInt(2, quantite);
            pstmt.setDouble(3, prix);
            pstmt.setDouble(4, tva);
            pstmt.setInt(5, codeBarres);
            pstmt.setInt(6, idCategorie);
            pstmt.setInt(7, id);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Stock modifié avec succès !");
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la modification du stock : " + e.getMessage());
            return false;
        }
    }

    // 🔹 Supprimer un stock
    public static boolean supprimerStock(int id) {
        String sql = "DELETE FROM Stock WHERE id_stock=?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Réinitialisation de l'auto-incrémentation
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE Stock AUTO_INCREMENT = 1");
                }
                System.out.println("Stock supprimé avec succès !");
                return true;
            } else {
                return false;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔹 Récupérer tous les stocks
    public static List<Stock> getStocks(String recherche) {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT s.id_stock, s.designation, s.quantite_stock, s.prix_unitaire, s.taux_tva, " +
                     "s.code_barres, c.designation AS nom_categorie " +
                     "FROM Stock s " +
                     "LEFT JOIN Categorie c ON s.id_categorie = c.id " +
                     "WHERE LOWER(s.designation) LIKE ? " +
                     "OR LOWER(c.designation) LIKE ? " +
                     "OR s.code_barres LIKE ?";

        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (recherche == null || recherche.trim().isEmpty()) {
                recherche = "";
            }

            // ✅ Ajout des wildcards "%" pour la recherche partielle
            String recherchePattern = "%" + recherche.trim().toLowerCase() + "%";

            //System.out.println("🔍 Recherche SQL avec LIKE : " + recherchePattern); // ✅ Debugging

            pstmt.setString(1, recherchePattern);
            pstmt.setString(2, recherchePattern);
            pstmt.setString(3, recherchePattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Stock stock = new Stock(
                    rs.getInt("id_stock"),
                    rs.getString("designation"),
                    rs.getInt("quantite_stock"),
                    rs.getInt("code_barres"),
                    rs.getDouble("prix_unitaire"),
                    rs.getDouble("taux_tva"),
                    0
                );
                stock.setNomCategorie(rs.getString("nom_categorie"));
                stocks.add(stock);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
        return stocks;
    }

    
   public static String getCategorieName(int idCategorie) {
        String sql = "SELECT designation FROM Categorie WHERE id = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCategorie);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String categorie = rs.getString("designation");
                System.out.println("✔ Catégorie trouvée : " + categorie + " pour ID : " + idCategorie);
                return categorie;
            } else {
                System.out.println("⚠ Catégorie introuvable pour ID : " + idCategorie);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "Inconnu";
    }
}
