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
public class Employe {
     private int idEmploye;
    private String prenom;
    private String nom;
    private String titre;
    private double salaire;
    private double pctCommission;

    private int idAdresse; // FK vers la table Adresse
    private int idUser;    // FK vers la table Users

    // Optionnel : un objet Adresse
    private Adresse adresse;

    // --- Constructeurs ---
    public Employe() {
    }

    public Employe(int idEmploye, String prenom, String nom, String titre, double salaire, double pctCommission,
                   int idAdresse, int idUser) {
        this.idEmploye = idEmploye;
        this.prenom = prenom;
        this.nom = nom;
        this.titre = titre;
        this.salaire = salaire;
        this.pctCommission = pctCommission;
        this.idAdresse = idAdresse;
        this.idUser = idUser;
    }

    // --- Getters / Setters ---
    public int getIdEmploye() {
        return idEmploye;
    }
    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public double getSalaire() {
        return salaire;
    }
    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public double getPctCommission() {
        return pctCommission;
    }
    public void setPctCommission(double pctCommission) {
        this.pctCommission = pctCommission;
    }

    public int getIdAdresse() {
        return idAdresse;
    }
    public void setIdAdresse(int idAdresse) {
        this.idAdresse = idAdresse;
    }

    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

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
    public static boolean createEmploye(Employe e) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Employe (prenom, nom, titre, salaire, pct_commission, id_adresse, id_user) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, e.getPrenom());
            stmt.setString(2, e.getNom());
            stmt.setString(3, e.getTitre());
            stmt.setDouble(4, e.getSalaire());
            stmt.setDouble(5, e.getPctCommission());
            stmt.setInt(6, e.getIdAdresse());
            stmt.setInt(7, e.getIdUser());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        e.setIdEmploye(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // READ by ID
    public static Employe getEmployeById(int id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Employe WHERE id_employe = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Employe e = new Employe(
                        rs.getInt("id_employe"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getString("titre"),
                        rs.getDouble("salaire"),
                        rs.getDouble("pct_commission"),
                        rs.getInt("id_adresse"),
                        rs.getInt("id_user")
                    );
                    return e;
                }
            }
        }
        return null;
    }

    // READ all
    public static List<Employe> getAllEmployes() throws SQLException, ClassNotFoundException {
        List<Employe> liste = new ArrayList<>();
        String sql = "SELECT id_employe, nom, prenom, titre, salaire, pct_commission, id_adresse, id_user FROM employe";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next()) {
                Employe e = new Employe(
                    rs.getInt("id_employe"),
                    rs.getString("prenom"),
                    rs.getString("nom"),
                    rs.getString("titre"),
                    rs.getDouble("salaire"),
                    rs.getDouble("pct_commission"),
                    rs.getInt("id_adresse"),
                    rs.getInt("id_user")
                );
                liste.add(e);
            }
        }
        return liste;
    }

    // UPDATE
    public static boolean updateEmploye(Employe e) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Employe "
                   + "SET prenom = ?, nom = ?, titre = ?, salaire = ?, pct_commission = ?, id_adresse = ?, id_user = ? "
                   + "WHERE id_employe = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, e.getPrenom());
            stmt.setString(2, e.getNom());
            stmt.setString(3, e.getTitre());
            stmt.setDouble(4, e.getSalaire());
            stmt.setDouble(5, e.getPctCommission());
            stmt.setInt(6, e.getIdAdresse());
            stmt.setInt(7, e.getIdUser());
            stmt.setInt(8, e.getIdEmploye());

            int rows = stmt.executeUpdate();
            return (rows > 0);
        }
    }

    // DELETE
    public static boolean deleteEmploye(int idEmploye) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Employe WHERE id_employe = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idEmploye);
            int rows = stmt.executeUpdate();
            return (rows > 0);
        }
    }
    
    public static boolean supprimerEmployeComplet(int idEmploye, int idUser) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement stmtDelEmploye = null;
        PreparedStatement stmtDelUser = null;
        PreparedStatement stmtDelAdresse = null;
        PreparedStatement stmtCheckAdresse = null;
        ResultSet rsCheck = null;

        try {
            // 1) Ouvrir la connexion et démarrer la transaction
            conn = Parametres.getConnection();
            conn.setAutoCommit(false); 

            // 2) Récupérer l'id_adresse de l'employé (ou vous pouvez charger l'objet Employe via getEmployeById)
            int idAdresse = -1;
            String sqlGetAddr = "SELECT id_adresse FROM employe WHERE id_employe = ?";
            try (PreparedStatement stmtGet = conn.prepareStatement(sqlGetAddr)) {
                stmtGet.setInt(1, idEmploye);
                try (ResultSet rsGet = stmtGet.executeQuery()) {
                    if (!rsGet.next()) {
                        return false; // employé inexistant
                    }
                    idAdresse = rsGet.getInt("id_adresse");
                }
            }

            // 3) Supprimer l'employé
            String sqlDelEmp = "DELETE FROM employe WHERE id_employe = ?";
            stmtDelEmploye = conn.prepareStatement(sqlDelEmp);
            stmtDelEmploye.setInt(1, idEmploye);
            int rowsEmp = stmtDelEmploye.executeUpdate();

            // 4) Supprimer l'utilisateur
            String sqlDelUser = "DELETE FROM users WHERE id_user = ?";
            stmtDelUser = conn.prepareStatement(sqlDelUser);
            stmtDelUser.setInt(1, idUser);
            int rowsUser = stmtDelUser.executeUpdate();

            // 5) Vérifier si d’autres employés utilisent cette adresse
            String sqlCheck = "SELECT COUNT(*) FROM employe WHERE id_adresse = ?";
            stmtCheckAdresse = conn.prepareStatement(sqlCheck);
            stmtCheckAdresse.setInt(1, idAdresse);
            rsCheck = stmtCheckAdresse.executeQuery();
            rsCheck.next();
            int countEmp = rsCheck.getInt(1);

            // 6) Si plus aucun employé ne l'utilise, on peut supprimer l’adresse
            if (countEmp == 0) {
                String sqlDelAdr = "DELETE FROM adresse WHERE id_adresse = ?";
                stmtDelAdresse = conn.prepareStatement(sqlDelAdr);
                stmtDelAdresse.setInt(1, idAdresse);
                stmtDelAdresse.executeUpdate();
            }

            // 7) Tout est bon => commit
            conn.commit();

            // on peut tester "rowsEmp > 0 && rowsUser > 0" si nécessaire
            return true;

        } catch (SQLException ex) {
            // En cas d’erreur => rollback
            if (conn != null) {
                try {
                    conn.rollback();
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
            if (stmtDelUser != null) try { stmtDelUser.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmtDelEmploye != null) try { stmtDelEmploye.close(); } catch (SQLException e) { e.printStackTrace(); }

            // -- Fermer la connexion
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    public static Employe getEmployeByUserId(int idUser) throws SQLException, ClassNotFoundException {
        Connection conn = Parametres.getConnection();
        String sql = "SELECT * FROM employe WHERE id_user = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idUser);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Employe emp = new Employe();
            emp.setIdEmploye(rs.getInt("id_employe"));
            emp.setNom(rs.getString("nom"));
            emp.setPrenom(rs.getString("prenom"));
            emp.setTitre(rs.getString("titre"));
            emp.setSalaire(rs.getDouble("salaire"));
            emp.setPctCommission(rs.getDouble("pct_commission"));
            emp.setIdAdresse(rs.getInt("id_adresse"));
            emp.setIdUser(rs.getInt("id_user"));
            return emp;
        }
        return null;
    }
    
    public static List<Employe> rechercherEmployeParNomOuId(String recherche) throws SQLException, ClassNotFoundException {
        List<Employe> employesTrouves = new ArrayList<>();
        String sql = "SELECT * FROM employe WHERE id_employe = ? OR nom LIKE ?";

        try (Connection conn = Parametres.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Vérifier si l'entrée est un ID ou un nom
            try {
                int idRecherche = Integer.parseInt(recherche);
                ps.setInt(1, idRecherche);
            } catch (NumberFormatException e) {
                ps.setInt(1, -1); // Si ce n'est pas un ID, on met une valeur invalide
            }

            ps.setString(2, "%" + recherche + "%"); // Recherche partielle par nom

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employe emp = new Employe(
                        rs.getInt("id_employe"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("titre"),
                        rs.getDouble("salaire"),
                        rs.getDouble("pct_commission"),
                        rs.getInt("id_adresse"),
                        rs.getInt("id_user")
                    );
                    employesTrouves.add(emp);
                }
            }
        }
        return employesTrouves;
    }
    
    
}
