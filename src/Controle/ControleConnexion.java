/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;
import Modele.Parametres;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
/**
 *
 * @author PC
 */
public abstract class ControleConnexion {
    static boolean etatConnexion;
    static Connection laConnectionStatique;

    static {
        try {
            // Initialisation de la connexion via Parametres
            laConnectionStatique = Parametres.getConnection();
            etatConnexion = (laConnectionStatique != null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    " je suis ici Impossible de se connecter à la base de données",
                    "ALERTE", JOptionPane.ERROR_MESSAGE);
            etatConnexion = false;
        }
    }

    public static boolean isEtatConnexion() {
        return etatConnexion;
    }

    public static Connection getLaConnectionStatique() {
        return laConnectionStatique;
    }

    public static boolean controle(String username, String password) {
        try {
            Connection conn = Parametres.getConnection();
            String query = "SELECT * FROM Users WHERE username = '" + username + "' AND password = '" + password + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la vérification des identifiants", "ERREUR", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(null, "Vérifier votre saisie.", "ERREUR", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public static void fermetureSession() {
        try {
            if (laConnectionStatique != null && !laConnectionStatique.isClosed()) {
                laConnectionStatique.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Problème rencontré à la fermeture de la connexion",
                    "ERREUR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void transfertDonnees() {
        try {
            System.out.println("Gestion Stock");
            System.out.println("---------------------------------");
            Connection conn = Parametres.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Users");
            
            while (rs.next()) {
                System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString("password"));
                
            }
            stmt.close();
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors du transfert des données", "ERREUR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
