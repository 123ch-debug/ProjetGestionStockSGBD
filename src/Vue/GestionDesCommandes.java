/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vue;

import Modele.Commande;
//import Modele.Employe;
import Modele.Parametres;
//import Modele.Stock;
import java.sql.*;
import java.sql.SQLException;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;

/**
 *
 * @author PC
 */
public class GestionDesCommandes extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    private int commandeSelectionnee = -1; // ID de la commande actuellement sélectionnée
    

    /**
     * Creates new form GestionDesCommandes
     */
    public GestionDesCommandes() {
        initComponents();
        
        
        // Configuration de la JTable "Commandes"
       tableModel = new DefaultTableModel(new Object[]{"ID", "ID_Client","Nom_Client", "Date", "Statut", "Total"}, 0);
       jTable_Commande.setModel(tableModel);
       
       // Configuration de la JTable "Articles"
       /*tableModel = new DefaultTableModel(new Object[]{"ID_Stock", "Designation", "Quantite", "Prix_Unitaire", "Total"}, 
            0);
       jTable_ArticleCommande.setModel(tableModel);*/
       
        
        // Configuration de la JTable "Articles"
        /*modelArticles = new DefaultTableModel(
            new Object[]{"ID_Stock", "Designation", "Quantite", "Prix_Unitaire", "Total"}, 
            0
        ) {
            // (Optionnel) Pour autoriser la modification de la colonne Quantite, par ex. :
            @Override
            public boolean isCellEditable(int row, int column) {
                // Autoriser l'édition uniquement sur la colonne 2 (Quantite) si vous le souhaitez
                return (column == 2);
            }
        };
        jTable_ArticleCommande.setModel(modelArticles);*/
        
        // Charger toutes les commandes au lancement
        chargerToutesLesCommandes();
        
    }
    
    private void chargerToutesLesCommandes() {
        DefaultTableModel model = (DefaultTableModel) jTable_Commande.getModel();
        model.setRowCount(0);
        try {
            tableModel.setRowCount(0); // On vide la table avant de la remplir 
            List<Commande> liste = Commande.getToutesLesCommandes();
            for (Commande c : liste) {
                model.addRow(new Object[]{
                    c.getIdCommande(),
                    c.getIdClient(),        // ou le nom du client si vous préférez
                    c.getNomClient(),   // Affiche le nom du client 
                    c.getDateCommande(),
                    c.getStatut(),
                    c.getMontantTotal()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur chargement commandes", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // -----------------------------------------------
    // Méthode pour charger les articles d'une commande
    private void chargerArticlesCommande(int idCommande) {
        try {
            tableModel.setRowCount(0); // On vide la table avant de la remplir 
            
            // Récupérer la liste des articles
            // Vous pouvez faire une méthode Commande.getArticlesDeCommande(idCommande)
            // ou un "Commande_Stock.getByCommande(idCommande)" … 
            // Adaptez en fonction de votre code :
            Connection conn = Parametres.getConnection();
            String sql = "SELECT cs.id_stock, s.designation, cs.quantite_achetee, cs.prix_unitaire " +
                         "FROM Commande_Stock cs " +
                         "JOIN Stock s ON cs.id_stock = s.id_stock " +
                         "WHERE cs.id_commande = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int idStock = rs.getInt("id_stock");
                String designation = rs.getString("designation");
                int quantite = rs.getInt("quantite_achetee");
                double prixU = rs.getDouble("prix_unitaire");
                double total = quantite * prixU;
                
                tableModel.addRow(new Object[]{
                    idStock,
                    designation,
                    quantite,
                    prixU,
                    total
                });
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur chargement articles", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // -----------------------------------------------
    // Méthode pour mettre à jour la base quand on modifie la quantité 
    // (Si vous autorisez l’édition directe dans la JTable)
    private void majQuantiteEnBase(int idCommande, int idStock, int nouvelleQte) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Commande_Stock SET quantite_achetee=? WHERE id_commande=? AND id_stock=?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nouvelleQte);
            pstmt.setInt(2, idCommande);
            pstmt.setInt(3, idStock);
            pstmt.executeUpdate();
        }
    }
    
    private void chargerCommandesRecherche(String recherche) {
        DefaultTableModel model = (DefaultTableModel) jTable_Commande.getModel();
        model.setRowCount(0);
        try {
            List<Commande> liste = Commande.rechercherCommandesParIdOuNom(recherche);
            for (Commande c : liste) {
                model.addRow(new Object[]{
                    c.getIdCommande(),
                    c.getIdClient(),        // ou le nom du client si vous préférez
                    c.getNomClient(),
                    c.getDateCommande(),
                    c.getStatut(),
                    c.getMontantTotal()
                });
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de recherche");
        }
    }

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jBtn_Quitter = new javax.swing.JButton();
        jBtn_AllerAVente = new javax.swing.JButton();
        jBtn_SupprimerCommande = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Commande = new javax.swing.JTable();
        jTextF_Recherche = new javax.swing.JTextField();
        jBtn_Recherchercommande = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Fenetre Commande");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, -1, -1));

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jBtn_Quitter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_Quitter.setText("Quitter");
        jBtn_Quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_QuitterActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_Quitter, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 130, -1));

        jBtn_AllerAVente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_AllerAVente.setText("Aller à la vente");
        jBtn_AllerAVente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_AllerAVenteActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_AllerAVente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, -1, -1));

        jBtn_SupprimerCommande.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_SupprimerCommande.setText("Supprimer");
        jBtn_SupprimerCommande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_SupprimerCommandeActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_SupprimerCommande, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 130, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 160, 500));

        jTable_Commande.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "ID_Client", "Nom_Client", "Date", "Statut", "Total"
            }
        ));
        jTable_Commande.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_CommandeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Commande);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 210, 640, 170));

        jTextF_Recherche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextF_RechercheActionPerformed(evt);
            }
        });
        getContentPane().add(jTextF_Recherche, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 190, -1));

        jBtn_Recherchercommande.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_Recherchercommande.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/imgRech.JPG"))); // NOI18N
        jBtn_Recherchercommande.setText("Rechercher ");
        jBtn_Recherchercommande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_RecherchercommandeActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_Recherchercommande, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextF_RechercheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextF_RechercheActionPerformed
        
    }//GEN-LAST:event_jTextF_RechercheActionPerformed

    private void jBtn_RecherchercommandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_RecherchercommandeActionPerformed
       String recherche = jTextF_Recherche.getText().trim();
        if (recherche.isEmpty()) {
            // Si champ vide, recharger toutes
            chargerToutesLesCommandes();
        } else {
            chargerCommandesRecherche(recherche);
        }
    }//GEN-LAST:event_jBtn_RecherchercommandeActionPerformed

    private void jBtn_QuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_QuitterActionPerformed
        // TODO add your handling code here:
        this.dispose(); // Ferme la fenêtre
    }//GEN-LAST:event_jBtn_QuitterActionPerformed

    private void jTable_CommandeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_CommandeMouseClicked
        
    }//GEN-LAST:event_jTable_CommandeMouseClicked

    private void jBtn_AllerAVenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_AllerAVenteActionPerformed
         // 1) Vérifier qu’une ligne est sélectionnée dans la jTable_Commande
        int row = jTable_Commande.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande.");
            return;
        }
        
        // Récupérer le statut de la commande (ici supposé dans la colonne 4)
        String statut = jTable_Commande.getValueAt(row, 4).toString();
        if (!"En cours".equalsIgnoreCase(statut)) {
            JOptionPane.showMessageDialog(this, "La commande est clôturée.\nVous ne pouvez pas accéder à la fenêtre Vente pour une commande non en cours.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2) Récupérer l’ID de la commande
        int idCommande = (int) jTable_Commande.getValueAt(row, 0);

        // 3) Récupérer aussi l’ID du client si tu l’as dans la colonne 1 
        int idClient = (int) jTable_Commande.getValueAt(row, 1);

        // 4) Ouvrir la fenêtre Vente
        GestionVente fenVente = new GestionVente();

        // 5) Appeler la méthode “chargerCommandeExistante(...)”
        fenVente.chargerCommandeExistante(idCommande, idClient);

        // 6) setVisible(true)
        fenVente.setVisible(true);

        // 7) Optionnel : fermer la fenêtre commandes
        this.dispose();
    }//GEN-LAST:event_jBtn_AllerAVenteActionPerformed

    private void jBtn_SupprimerCommandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_SupprimerCommandeActionPerformed
        int row = jTable_Commande.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande à supprimer.");
            return;
        }

        // Récupérer l'ID de la commande (colonne 0)
        int idCommande = (int) jTable_Commande.getValueAt(row, 0);

        try {
            // Vérifier le statut de la commande
            Commande cmd = Commande.getCommandeById(idCommande);
            if (cmd == null) {
                JOptionPane.showMessageDialog(this, "Commande introuvable.");
                return;
            }
            if (!"En cours".equals(cmd.getStatut())) {
                JOptionPane.showMessageDialog(this, "Impossible de supprimer : la commande n'est pas en cours.");
                return;
            }

            // Demander confirmation
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous vraiment supprimer la commande n°" + idCommande + " ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                // Supprimer en base
                boolean success = Commande.deleteCommande(idCommande);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Commande supprimée avec succès.");
                    chargerToutesLesCommandes(); // recharger la liste
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la commande.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }//GEN-LAST:event_jBtn_SupprimerCommandeActionPerformed
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GestionDesCommandes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionDesCommandes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionDesCommandes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionDesCommandes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionDesCommandes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_AllerAVente;
    private javax.swing.JButton jBtn_Quitter;
    private javax.swing.JButton jBtn_Recherchercommande;
    private javax.swing.JButton jBtn_SupprimerCommande;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Commande;
    private javax.swing.JTextField jTextF_Recherche;
    // End of variables declaration//GEN-END:variables
}
