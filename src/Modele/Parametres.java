/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import Controle.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author PC
 */
public class Parametres {
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
 
            // Récupération des propriétés depuis config.properties(.enc)
            String driver   = Configuration.getProperty("db.driver");
            String url      = Configuration.getProperty("db.url");
            String user     = Configuration.getProperty("db.user");
            String password = Configuration.getProperty("db.password");
 
            // Vérifier la présence des propriétés essentielles
            if (driver == null || url == null || user == null || password == null) {
                throw new IllegalArgumentException(
                    "Les paramètres de connexion (db.driver, db.url, db.user, db.password) "
                    + "sont introuvables ou nuls. Vérifiez votre fichier de configuration."
                );
            }
 
            // Charger le driver JDBC
            Class.forName(driver);
            //Class.forName("org.postgresql.Driver");
 
            // Établir la connexion
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
    
    public static void reloadConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = null; // Réinitialisation
        getConnection(); // Recharge la connexion avec les nouvelles valeurs
    }
}

