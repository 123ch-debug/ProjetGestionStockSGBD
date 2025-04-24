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
public class Categorie {
    private int id;
    private String designation;

    public Categorie(int id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    public int getId() { return id; }
    public String getDesignation() { return designation; }

    // ✅ Récupérer toutes les catégories
    public static List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        try (Connection conn = Parametres.getConnection()) {
            String sql = "SELECT * FROM Categorie";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categories.add(new Categorie(rs.getInt("id"), rs.getString("designation")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
