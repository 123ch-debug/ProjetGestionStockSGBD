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
public class Client {
   private int idClient;
    private String nom;
    private String prenom;
    private String email;
    private boolean carteFidelite;

    // Au lieu de stocker directement numRue/nomRue/etc.,
    // on stocke un champ "idAdresse" ou un objet Adresse
    private int idAdresse; 
    private Adresse adresse; // Optionnel si vous voulez avoir l'objet complet

    // --- Constructeurs ---
    public Client() {
    }

    // Constructeur pour la lecture en BD
    public Client(int idClient, String nom, String prenom, String email, boolean carteFidelite, int idAdresse) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.carteFidelite = carteFidelite;
        this.idAdresse = idAdresse;
    }

    // --- Getters / Setters ---
    public int getIdClient() {
        return idClient;
    }
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCarteFidelite() {
        return carteFidelite;
    }
    public void setCarteFidelite(boolean carteFidelite) {
        this.carteFidelite = carteFidelite;
    }

    public int getIdAdresse() {
        return idAdresse;
    }
    public void setIdAdresse(int idAdresse) {
        this.idAdresse = idAdresse;
    }

    // Optionnel : si vous voulez charger l'objet Adresse complet
    public Adresse getAdresse() {
        return adresse;
    }
    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }
    
    @Override
    public String toString() {
        return nom + " " + prenom; // Affiche "Nom Prénom" dans la JComboBox
    }

    // --- Méthodes CRUD ---

    // CREATE
    public static boolean createClient(Client c) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Client (nom, prenom, email, carte_fidelite, id_adresse) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, c.getNom());
            stmt.setString(2, c.getPrenom());
            stmt.setString(3, c.getEmail());
            stmt.setBoolean(4, c.isCarteFidelite());
            stmt.setInt(5, c.getIdAdresse());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // Récupérer l'id généré
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdClient(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // READ by ID
    public static Client getClientById(int idClient) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Client WHERE id_client = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idClient);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client cli = new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getBoolean("carte_fidelite"),
                        rs.getInt("id_adresse")
                    );
                    return cli;
                }
            }
        }
        return null;
    }

    // READ all
    public static List<Client> getAllClients() throws SQLException, ClassNotFoundException {
        List<Client> liste = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {
                Client c = new Client(
                    rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getBoolean("carte_fidelite"),
                    rs.getInt("id_adresse")
                );
                liste.add(c);
            }
        }
        return liste;
    }

    // UPDATE
    public static boolean updateClient(Client c) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Client "
                   + "SET nom = ?, prenom = ?, email = ?, carte_fidelite = ?, id_adresse = ? "
                   + "WHERE id_client = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, c.getNom());
            stmt.setString(2, c.getPrenom());
            stmt.setString(3, c.getEmail());
            stmt.setBoolean(4, c.isCarteFidelite());
            stmt.setInt(5, c.getIdAdresse());
            stmt.setInt(6, c.getIdClient());

            int rows = stmt.executeUpdate();
            return (rows > 0);
        }
    }

    // DELETE
    public static boolean deleteClient(int idClient) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Client WHERE id_client = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idClient);
            int rows = stmt.executeUpdate();
            return (rows > 0);
        }
    }
    
    public static boolean deleteClientAndAddress(int idClient) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmtDelClient = null;
        PreparedStatement stmtDelAdresse = null;
        PreparedStatement stmtCheckAdresse = null;
        ResultSet rsCheck = null;

        try {
            // 1️⃣ Connexion et transaction
            conn = Parametres.getConnection();
            conn.setAutoCommit(false); 

            // 2️⃣ Récupérer l'id_adresse du client
            int idAdresse = -1;
            String sqlGetAddr = "SELECT id_adresse FROM client WHERE id_client = ?";
            try (PreparedStatement stmtGet = conn.prepareStatement(sqlGetAddr)) {
                stmtGet.setInt(1, idClient);
                try (ResultSet rsGet = stmtGet.executeQuery()) {
                    if (!rsGet.next()) {
                        return false; // Client inexistant
                    }
                    idAdresse = rsGet.getInt("id_adresse");
                }
            }

            // 3️⃣ Supprimer le client
            String sqlDelClient = "DELETE FROM client WHERE id_client = ?";
            stmtDelClient = conn.prepareStatement(sqlDelClient);
            stmtDelClient.setInt(1, idClient);
            int rowsClient = stmtDelClient.executeUpdate();

            // 4️⃣ Vérifier si l'adresse est encore utilisée
            String sqlCheck = "SELECT COUNT(*) FROM client WHERE id_adresse = ?";
            stmtCheckAdresse = conn.prepareStatement(sqlCheck);
            stmtCheckAdresse.setInt(1, idAdresse);
            rsCheck = stmtCheckAdresse.executeQuery();
            rsCheck.next();
            int countClients = rsCheck.getInt(1);

            // 5️⃣ Si plus aucun client ne l’utilise, supprimer l’adresse
            if (countClients == 0) {
                String sqlDelAdr = "DELETE FROM adresse WHERE id_adresse = ?";
                stmtDelAdresse = conn.prepareStatement(sqlDelAdr);
                stmtDelAdresse.setInt(1, idAdresse);
                stmtDelAdresse.executeUpdate();
            }

            // 6️⃣ Tout est bon, commit
            conn.commit();
            return true;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler si erreur
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
            throw ex; 
        } finally {
            // -- Fermer ResultSet
            if (rsCheck != null) try { rsCheck.close(); } catch (SQLException e) { e.printStackTrace(); }

            // -- Fermer PreparedStatement
            if (stmtCheckAdresse != null) try { stmtCheckAdresse.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmtDelAdresse != null) try { stmtDelAdresse.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmtDelClient != null) try { stmtDelClient.close(); } catch (SQLException e) { e.printStackTrace(); }

            // -- Fermer la connexion
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    public static List<Client> rechercherClientsParNomOuId(String recherche) throws SQLException, ClassNotFoundException {
        Connection conn = Parametres.getConnection();
        String sql = "SELECT * FROM client WHERE id_client = ? OR nom LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);

        try {
            ps.setInt(1, Integer.parseInt(recherche)); // Si l'utilisateur tape un ID
        } catch (NumberFormatException e) {
            ps.setInt(1, -1); // Valeur invalide pour éviter une erreur SQL
        }

        ps.setString(2, "%" + recherche + "%"); // Recherche partielle par nom

        ResultSet rs = ps.executeQuery();
        List<Client> listeClients = new ArrayList<>();

        while (rs.next()) {
            listeClients.add(new Client(
                rs.getInt("id_client"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getBoolean("carte_fidelite"),
                    rs.getInt("id_adresse")
            ));
        }

        return listeClients;
    }
}
