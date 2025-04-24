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
public class Commande {
    private int idCommande;
    private int idClient;
    private int idEmploye;
    private Timestamp dateCommande;
    private String statut;
    private double montantTotal; 
    private String Nom_Client;

    // Constructeurs
    public Commande() {}

    public Commande(int idCommande, int idClient, int idEmploye, Timestamp dateCommande, String statut, double montantTotal) {
        this.idCommande = idCommande;
        this.idClient = idClient;
        this.idEmploye = idEmploye;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.montantTotal = montantTotal;
    }

    // Getters et Setters
    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }

    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }

    public int getIdEmploye() { return idEmploye; }
    public void setIdEmploye(int idEmploye) { this.idEmploye = idEmploye; }
    
    public String getNomClient() {
        return Nom_Client;
    }

    public void setNomClient(String Nom_Client) {
        this.Nom_Client = Nom_Client;
    }

    public Timestamp getDateCommande() { return dateCommande; }
    public void setDateCommande(Timestamp dateCommande) { this.dateCommande = dateCommande; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    @Override
    public String toString() {
        return "Commande #" + idCommande + " - Client: " + idClient + " - Statut: " + statut;
    }

    // --- 🚀 CRUD (Create, Read, Update, Delete) ---

    // CREATE : Ajouter une nouvelle commande
    public static boolean createCommande(Commande c) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Commande (id_client, id_employe, statut) VALUES (?, ?, ?)";
        
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, c.getIdClient());
            stmt.setInt(2, c.getIdEmploye());
            stmt.setString(3, "En cours"); // Par défaut, une commande est en cours
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdCommande(rs.getInt(1)); // Récupère l'ID auto-généré
                    }
                }
                return true;
            }
            return false;
        }
    }

    // READ : Récupérer une commande par son ID
    public static Commande getCommandeById(int idCommande) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Commande WHERE id_commande = ?";
        
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Commande(
                    rs.getInt("id_commande"),
                    rs.getInt("id_client"),
                    rs.getInt("id_employe"),
                    rs.getTimestamp("date_commande"),
                    rs.getString("statut"),
                    getMontantTotalCommande(idCommande) // Récupère le total
                );
            }
        }
        return null;
    }
    
    public static List<Commande> getToutesLesCommandes() throws SQLException, ClassNotFoundException {
        List<Commande> commandes = new ArrayList<>();
         // Modifiez la requête pour joindre la table Client et récupérer le nom du client
        String sql = "SELECT c.*, cl.nom AS nom_client FROM Commande c JOIN Client cl ON c.id_client = cl.id_client";

        // 1) Lire toutes les lignes sans le total
        List<Integer> idsCommande = new ArrayList<>();

        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Commande cmd = new Commande(
                    rs.getInt("id_commande"),
                    rs.getInt("id_client"),
                    //rs.getString("nom_client"),
                    rs.getInt("id_employe"),
                    rs.getTimestamp("date_commande"),
                    rs.getString("statut"),
                    0.0 // On mettra le vrai total juste après
                );
                // Ajouter le nom du client à l'objet Commande
                cmd.setNomClient(rs.getString("nom_client"));
                commandes.add(cmd);
                idsCommande.add(cmd.getIdCommande());
            }
        }

        // 2) Calculer les totaux dans un deuxième temps
        for (Commande c : commandes) {
            double total = getMontantTotalCommande(c.getIdCommande());
            c.setMontantTotal(total);
        }

        return commandes;
    }


    // UPDATE : Modifier le statut d'une commande
    public static boolean updateStatutCommande(Connection conn, int idCommande, String nouveauStatut) throws SQLException {
        String sql = "UPDATE Commande SET statut = ? WHERE id_commande = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nouveauStatut);
            stmt.setInt(2, idCommande);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }


    // DELETE : Supprimer une commande
    /*public static boolean deleteCommande(int idCommande) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Commande WHERE id_commande = ?";
        
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCommande);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }*/
    
    public static boolean deleteCommande(int idCommande) throws SQLException, ClassNotFoundException {
        // 1) Supprimer d’abord Commande_Stock (si tu as une FK sans cascade)
        try (Connection conn = Parametres.getConnection()) {
            // Supprimer les lignes de Commande_Stock
            String sqlDelDetails = "DELETE FROM Commande_Stock WHERE id_commande = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDelDetails)) {
                ps.setInt(1, idCommande);
                ps.executeUpdate();
            }

            // Supprimer la commande
            String sqlDelCommande = "DELETE FROM Commande WHERE id_commande = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDelCommande)) {
                ps.setInt(1, idCommande);
                int rows = ps.executeUpdate();
                return rows > 0;
            }
        }
    }


    

    // Calculer le montant total d'une commande
    public static double getMontantTotalCommande(int idCommande) throws SQLException, ClassNotFoundException {
        String sql = "SELECT SUM(cs.quantite_achetee * cs.prix_unitaire) AS total " +
                     "FROM Commande_Stock cs WHERE cs.id_commande = ?";
        
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }
    
    public static List<Commande> rechercherCommandesParIdOuNom(String recherche) throws SQLException, ClassNotFoundException {
        List<Commande> results = new ArrayList<>();
        String sql = "SELECT c.id_commande, c.id_client, c.id_employe, cl.nom AS nom_client, c.date_commande, c.statut, " +
                     "  (SELECT SUM(cs.quantite_achetee * cs.prix_unitaire) " +
                     "   FROM Commande_Stock cs WHERE cs.id_commande = c.id_commande) AS total " +
                     "FROM Commande c " +
                     "JOIN Client cl ON c.id_client = cl.id_client " +
                     "WHERE c.id_commande = ? OR cl.nom LIKE ?";

        try (Connection conn = Parametres.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Tenter d'interpréter "recherche" comme un ID
            int idComm;
            try {
                idComm = Integer.parseInt(recherche);
            } catch (NumberFormatException e) {
                idComm = -1; // si ce n'est pas un nombre
            }

            ps.setInt(1, idComm);
            ps.setString(2, "%" + recherche + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idC = rs.getInt("id_commande");
                    int idClient = rs.getInt("id_client");
                    int idEmploye = rs.getInt("id_employe");
                    String nomClient = rs.getString("nom_client");
                    Timestamp dateC = rs.getTimestamp("date_commande");
                    String statut = rs.getString("statut");
                    double total = rs.getDouble("total");

                    // Création de l'objet Commande avec le constructeur existant
                    Commande cmd = new Commande(idC, idClient, idEmploye, dateC, statut, total);
                    // Affecter le nom du client via son setter
                    cmd.setNomClient(nomClient);
                    results.add(cmd);
                }
            }
        }
        return results;
    }
    
    
}
