/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vue;

import Modele.Stock;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author PC
 */
public class GestionDesStocks extends javax.swing.JFrame {

    /**
     * Creates new form GestionDesStocks
     */
    public GestionDesStocks() {
        initComponents();
        resetFields();  // ✅ Réinitialise les champs au démarrage
        chargerStocks();
    }
    
    // 📌 Méthode pour charger les stocks dans la JTable
    private void chargerStocks() {
        DefaultTableModel model = (DefaultTableModel) jTable_Stock.getModel();
        model.setRowCount(0); // ✅ Efface toutes les lignes avant de recharger

        List<Stock> stocks = Stock.getStocks(""); // ✅ Charge tous les stocks

        for (Stock stock : stocks) {
            model.addRow(new Object[]{
                stock.getIdStock(),
                stock.getDesignation(),
                stock.getQuantite_stock(),
                stock.getPrixUnitaire(),
                stock.getTauxTVA(),
                stock.getCodeBarres(),
                stock.getNomCategorie()
            });
        }

        resetFields();  // ✅ Vider les champs après une recherche vide
    }
    
    // 📌 Méthode pour ajouter un stock
    private void ajouterStock() {
        try {
            String designation = jTextF_Designation.getText();
            int quantite_stock = Integer.parseInt(jTextF_QuantitreStock.getText());
            double prixUnitaire = Double.parseDouble(jTextF_Prixunitaire.getText());
            double tauxTva = Double.parseDouble(jTextF_Tauxtva.getText());
            int codeBarres = Integer.parseInt(jTextF_Codebarres.getText());
            int idCategorie = getCategorieId(jComboBox_categorie.getSelectedItem().toString());

            // Vérifier si un stock avec le même code-barres ou la même désignation existe
            List<Stock> stocksExistants = Stock.getStocks("");
            for (Stock stock : stocksExistants) {
                if (stock.getDesignation().equalsIgnoreCase(designation) || stock.getCodeBarres() == codeBarres) {
                    JOptionPane.showMessageDialog(this, "❌ Ce stock existe déjà !", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            boolean success = Stock.ajouterStock(designation, quantite_stock, codeBarres, prixUnitaire, tauxTva, idCategorie);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Stock ajouté avec succès !");
                chargerStocks();
                resetFields();  // ✅ Réinitialiser les champs après ajout
            } else {
                JOptionPane.showMessageDialog(this, "❌ Erreur lors de l'ajout.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❗ Veuillez remplir correctement tous les champs.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 📌 Méthode pour modifier un stock
    private void modifierStock() {
        int selectedRow = jTable_Stock.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❗ Sélectionnez un stock à modifier.", "Alerte", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idStock = (int) jTable_Stock.getValueAt(selectedRow, 0);
            String designation = jTextF_Designation.getText();
            int quantite = Integer.parseInt(jTextF_QuantitreStock.getText());
            double prixUnitaire = Double.parseDouble(jTextF_Prixunitaire.getText());
            double tauxTva = Double.parseDouble(jTextF_Tauxtva.getText());
            int codeBarres = Integer.parseInt(jTextF_Codebarres.getText());
            int idCategorie = getCategorieId(jComboBox_categorie.getSelectedItem().toString());

            boolean success = Stock.modifierStock(idStock, designation, quantite, prixUnitaire, tauxTva, codeBarres, idCategorie);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Stock modifié avec succès !");
                chargerStocks();
                resetFields();  // ✅ Réinitialise les champs au démarrage
            } else {
                JOptionPane.showMessageDialog(this, "❌ Erreur lors de la modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❗ Veuillez remplir correctement tous les champs.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 📌 Méthode pour supprimer un stock
    private void supprimerStock() {
        int selectedRow = jTable_Stock.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❗ Sélectionnez un stock à supprimer.", "Alerte", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce stock ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idStock = (int) jTable_Stock.getValueAt(selectedRow, 0);
                boolean success = Stock.supprimerStock(idStock);
                if (success) {
                    JOptionPane.showMessageDialog(this, "✅ Stock supprimé !");
                    chargerStocks();
                    resetFields();  // ✅ Réinitialise les champs au démarrage
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "❗ Erreur lors de la suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 📌 Méthode pour réinitialiser les champs après une action
    private void resetFields() {
        jTextF_Designation.setText("");
        jTextF_QuantitreStock.setText("");
        jTextF_Prixunitaire.setText("");
        jTextF_Tauxtva.setText("");
        jTextF_Codebarres.setText("");
        jComboBox_categorie.setSelectedIndex(0);
    }

    // 📌 Méthode pour récupérer l'ID d'une catégorie depuis son nom
    private int getCategorieId(String categorie) {
        switch (categorie) {
            case "Peinture & decoration": return 1;
            case "Outils & equipements": return 2;
            case "Quincaillerie": return 3;
            case "Plomberie & sanitaires": return 4;
            case "Electricite & eclairage": return 5;
            case "Jardinage & exterieur": return 6;
            case "Bois & materiaux": return 7;
            case "Chauffage & climatisation": return 8;
            default: return 1; // Par défaut
        }
    }
    
    private void rechercherStock(String recherche) {
        try {
            List<Stock> stocks = Stock.getStocks(recherche); // 🔍 Recherche des stocks

            if (stocks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Aucun stock trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                resetFields(); // ✅ Vider les champs si aucun stock n'est trouvé
                return;
            }

            DefaultTableModel model = (DefaultTableModel) jTable_Stock.getModel();
            model.setRowCount(0); // ✅ Effacer les anciennes lignes avant d'ajouter les nouvelles

            // ✅ Afficher tous les stocks trouvés dans la JTable
            for (Stock stock : stocks) {
                model.addRow(new Object[]{
                    stock.getIdStock(),
                    stock.getDesignation(),
                    stock.getQuantite_stock(),
                    stock.getPrixUnitaire(),
                    stock.getTauxTVA(),
                    stock.getCodeBarres(),
                    stock.getNomCategorie()
                });
            }

            // ✅ Si un seul stock est trouvé, remplir les champs automatiquement
            if (stocks.size() == 1) {
                afficherInformationsStock(stocks.get(0));
            } else {
                resetFields(); // ✅ Réinitialiser les champs si plusieurs résultats
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la recherche.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void afficherInformationsStock(Stock stock) {
        jTextF_Designation.setText(stock.getDesignation());
        jTextF_QuantitreStock.setText(String.valueOf(stock.getQuantite_stock()));
        jTextF_Prixunitaire.setText(String.valueOf(stock.getPrixUnitaire()));
        jTextF_Tauxtva.setText(String.valueOf(stock.getTauxTVA()));
        jTextF_Codebarres.setText(String.valueOf(stock.getCodeBarres()));
        jComboBox_categorie.setSelectedItem(stock.getNomCategorie());
    }
    

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextF_Recherche = new javax.swing.JTextField();
        jLab_Titre = new javax.swing.JLabel();
        jLab_Designation = new javax.swing.JLabel();
        jLab_Codebarres = new javax.swing.JLabel();
        jLab_Tauxtva = new javax.swing.JLabel();
        jTextF_Tauxtva = new javax.swing.JTextField();
        jTextF_Codebarres = new javax.swing.JTextField();
        jTextF_Designation = new javax.swing.JTextField();
        jLab_QuantiteStock = new javax.swing.JLabel();
        jLab_PrixUnitaire = new javax.swing.JLabel();
        jTextF_Prixunitaire = new javax.swing.JTextField();
        jTextF_QuantitreStock = new javax.swing.JTextField();
        jComboBox_categorie = new javax.swing.JComboBox<>();
        jLab_Categorie = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Stock = new javax.swing.JTable();
        jBtn_AjouterStock = new javax.swing.JButton();
        jBtn_ModifierStock = new javax.swing.JButton();
        jBtn_SupprimerStock = new javax.swing.JButton();
        jBtn_Quitter = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jBtn_RechercherStock = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextF_Recherche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextF_RechercheActionPerformed(evt);
            }
        });
        getContentPane().add(jTextF_Recherche, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, 190, -1));

        jLab_Titre.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLab_Titre.setText("Gestion Des Stocks");
        getContentPane().add(jLab_Titre, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, -1, -1));

        jLab_Designation.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Designation.setText("Designation");
        getContentPane().add(jLab_Designation, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        jLab_Codebarres.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Codebarres.setText("Code_barres");
        getContentPane().add(jLab_Codebarres, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, -1, -1));

        jLab_Tauxtva.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Tauxtva.setText("Taux_tva");
        getContentPane().add(jLab_Tauxtva, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, -1, -1));
        getContentPane().add(jTextF_Tauxtva, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 230, 140, -1));

        jTextF_Codebarres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextF_CodebarresActionPerformed(evt);
            }
        });
        getContentPane().add(jTextF_Codebarres, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 190, 140, -1));
        getContentPane().add(jTextF_Designation, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 150, 140, -1));

        jLab_QuantiteStock.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_QuantiteStock.setText("Quantite_stock");
        getContentPane().add(jLab_QuantiteStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 150, -1, -1));

        jLab_PrixUnitaire.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_PrixUnitaire.setText("Prix_unitaire");
        getContentPane().add(jLab_PrixUnitaire, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 190, -1, -1));
        getContentPane().add(jTextF_Prixunitaire, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 190, 100, -1));
        getContentPane().add(jTextF_QuantitreStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 150, 100, -1));

        jComboBox_categorie.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Peinture & decoration", "Outils & equipements", "Quincaillerie", "Plomberie & sanitaires", "Electricite & eclairage", "Jardinage & exterieur", "Bois & materiaux", "Chauffage & climatisation" }));
        getContentPane().add(jComboBox_categorie, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 230, -1, -1));

        jLab_Categorie.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Categorie.setText("Categorie");
        getContentPane().add(jLab_Categorie, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 230, -1, -1));

        jTable_Stock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID_Stock", "Designation", "Quantite_stock", "Prix_Unitaire", "Taux_tva", "Code_barres", "Categorie"
            }
        ));
        jTable_Stock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_StockMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Stock);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 280, 650, 160));

        jBtn_AjouterStock.setBackground(new java.awt.Color(70, 161, 234));
        jBtn_AjouterStock.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_AjouterStock.setText("Ajouter_Stock");
        jBtn_AjouterStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_AjouterStockActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_AjouterStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 470, -1, -1));

        jBtn_ModifierStock.setBackground(new java.awt.Color(70, 161, 234));
        jBtn_ModifierStock.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_ModifierStock.setText("Modifier_Stock");
        jBtn_ModifierStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ModifierStockActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_ModifierStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 470, -1, -1));

        jBtn_SupprimerStock.setBackground(new java.awt.Color(70, 161, 234));
        jBtn_SupprimerStock.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_SupprimerStock.setText("Supprimer_Stock");
        jBtn_SupprimerStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_SupprimerStockActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_SupprimerStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 470, -1, -1));

        jBtn_Quitter.setBackground(new java.awt.Color(70, 161, 234));
        jBtn_Quitter.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_Quitter.setText("Quitter");
        jBtn_Quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_QuitterActionPerformed(evt);
            }
        });
        getContentPane().add(jBtn_Quitter, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 470, -1, -1));

        jPanel1.setBackground(new java.awt.Color(108, 188, 250));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jBtn_RechercherStock.setBackground(new java.awt.Color(70, 161, 234));
        jBtn_RechercherStock.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_RechercherStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/imgRech.JPG"))); // NOI18N
        jBtn_RechercherStock.setText("Rechercher stock");
        jBtn_RechercherStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_RechercherStockActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_RechercherStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 830, 60));

        jPanel2.setBackground(new java.awt.Color(108, 188, 250));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 830, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 450, 830, 60));

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 830, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 830, 390));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextF_RechercheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextF_RechercheActionPerformed
        String texteRecherche = jTextF_Recherche.getText().trim();
        rechercherStock(texteRecherche);

        if (texteRecherche.isEmpty()) {
            chargerStocks(); // ✅ Recharge tous les stocks si la recherche est vide
            resetFields(); // ✅ Réinitialiser les champs
        }
    }//GEN-LAST:event_jTextF_RechercheActionPerformed

    private void jTextF_CodebarresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextF_CodebarresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextF_CodebarresActionPerformed

    private void jTable_StockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_StockMouseClicked
        int selectedRow = jTable_Stock.getSelectedRow(); // Récupérer la ligne sélectionnée
        if (selectedRow != -1) {
            jTextF_Designation.setText(jTable_Stock.getValueAt(selectedRow, 1).toString());
            jTextF_QuantitreStock.setText(jTable_Stock.getValueAt(selectedRow, 2).toString());
            jTextF_Prixunitaire.setText(jTable_Stock.getValueAt(selectedRow, 3).toString());
            jTextF_Tauxtva.setText(jTable_Stock.getValueAt(selectedRow, 4).toString());
            jTextF_Codebarres.setText(jTable_Stock.getValueAt(selectedRow, 5).toString());
            jComboBox_categorie.setSelectedItem(jTable_Stock.getValueAt(selectedRow, 6).toString());
        }
    }//GEN-LAST:event_jTable_StockMouseClicked

    private void jBtn_AjouterStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_AjouterStockActionPerformed
        ajouterStock();
    }//GEN-LAST:event_jBtn_AjouterStockActionPerformed

    private void jBtn_ModifierStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ModifierStockActionPerformed
        modifierStock();
    }//GEN-LAST:event_jBtn_ModifierStockActionPerformed

    private void jBtn_SupprimerStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_SupprimerStockActionPerformed
        supprimerStock();
    }//GEN-LAST:event_jBtn_SupprimerStockActionPerformed

    private void jBtn_QuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_QuitterActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBtn_QuitterActionPerformed

    private void jBtn_RechercherStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_RechercherStockActionPerformed
        String texteRecherche = jTextF_Recherche.getText().trim();

        //System.out.println("🔎 Recherche lancée : " + texteRecherche); // ✅ Debugging

        if (texteRecherche.isEmpty()) {
            chargerStocks(); // ✅ Recharge tous les stocks si la recherche est vide
            resetFields(); // ✅ Réinitialiser les champs
        } else {
            rechercherStock(texteRecherche); // ✅ Recherche un stock
        }
    }//GEN-LAST:event_jBtn_RechercherStockActionPerformed

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
            java.util.logging.Logger.getLogger(GestionDesStocks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionDesStocks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionDesStocks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionDesStocks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionDesStocks().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_AjouterStock;
    private javax.swing.JButton jBtn_ModifierStock;
    private javax.swing.JButton jBtn_Quitter;
    private javax.swing.JButton jBtn_RechercherStock;
    private javax.swing.JButton jBtn_SupprimerStock;
    private javax.swing.JComboBox<String> jComboBox_categorie;
    private javax.swing.JLabel jLab_Categorie;
    private javax.swing.JLabel jLab_Codebarres;
    private javax.swing.JLabel jLab_Designation;
    private javax.swing.JLabel jLab_PrixUnitaire;
    private javax.swing.JLabel jLab_QuantiteStock;
    private javax.swing.JLabel jLab_Tauxtva;
    private javax.swing.JLabel jLab_Titre;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Stock;
    private javax.swing.JTextField jTextF_Codebarres;
    private javax.swing.JTextField jTextF_Designation;
    private javax.swing.JTextField jTextF_Prixunitaire;
    private javax.swing.JTextField jTextF_QuantitreStock;
    private javax.swing.JTextField jTextF_Recherche;
    private javax.swing.JTextField jTextF_Tauxtva;
    // End of variables declaration//GEN-END:variables
}
