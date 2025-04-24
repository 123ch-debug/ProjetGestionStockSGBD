/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vue;


import Modele.Adresse;
import Modele.Client;
import Modele.Commande;
import Modele.Employe;
import Modele.Parametres;
import Modele.Stock;
import java.sql.SQLException;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;


/**
 *
 * @author PC
 */
public class GestionVente extends javax.swing.JFrame {
    private int idClientSelectionne = -1; // stocké quand on a choisi un client
    private int idCommandeEnCours = -1;
    

    /**
     * Creates new form GestionVente
     */
    public GestionVente() {
        //this.GestionVente = GestionVente;
        initComponents();
        DefaultTableModel model = (DefaultTableModel) jTable_Panier.getModel();
        model.setRowCount(0); // Vider toutes les lignes
        chargerStockDisponible();
        
    }
   
    
    private void chargerStockDisponible() {
        DefaultTableModel model = (DefaultTableModel) jTable_Stock.getModel();
        model.setRowCount(0); // Réinitialiser la table
        List<Stock> stocks = Stock.getStocks(""); // Charger tous les stocks

        for (Stock stock : stocks) {
            model.addRow(new Object[]{
                stock.getIdStock(),
                stock.getDesignation(),
                stock.getQuantite_stock(),
                stock.getPrixUnitaire(),
                stock.getCodeBarres(),
                stock.getNomCategorie()
            });
        }
    }
    
    public void setIdClientSelectionne(int idClient) {
        this.idClientSelectionne = idClient;
    }

    private double calculerTotalCommande() {
        double total = 0;
        DefaultTableModel model = (DefaultTableModel) jTable_Panier.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            Object objTotal = model.getValueAt(i, 4); // Colonne "Total"

            if (objTotal == null) {
                System.out.println("❌ Erreur : Valeur null détectée dans le panier à la ligne " + i);
                continue; // Passe à la ligne suivante pour éviter l'erreur
            }

            try {
                double valeur = Double.parseDouble(objTotal.toString()); 
                System.out.println("✅ Ligne " + i + " - Valeur : " + valeur);
                total += valeur;
            } catch (NumberFormatException e) {
                System.out.println("❌ Erreur de conversion à la ligne " + i + " : " + objTotal);
            }
        }

        System.out.println("🔹 Montant total final : " + total);
        return total;
    }

    
    private void afficherDebugPanier() {
        DefaultTableModel model = (DefaultTableModel) jTable_Panier.getModel();
        System.out.println("DEBUG - Contenu du panier :");

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                System.out.print(model.getValueAt(i, j) + "\t");
            }
            System.out.println();
        }
    }
    
    
    private int getIdPaiement(Connection conn, String modePaiement) throws SQLException, ClassNotFoundException {
        // Vérifier si le mode de paiement existe déjà
        String sqlSelect = "SELECT id_paiement FROM Type_Paiement WHERE mode_paiement = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setString(1, modePaiement);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_paiement");
                }
            }
        }
        // S'il n'existe pas, l'insérer
        String sqlInsert = "INSERT INTO Type_Paiement (mode_paiement) VALUES (?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, modePaiement);
            insertStmt.executeUpdate();
            try (ResultSet rsGenerated = insertStmt.getGeneratedKeys()) {
                if (rsGenerated.next()) {
                    return rsGenerated.getInt(1);
                }
            }
        }
        return -1; // En cas d'erreur
    }

    
    private void ajouterArticleDansCommande(int idCommande, int idStock, int quantite, double prixUnitaire) {
        String sql = "INSERT INTO Commande_Stock (id_commande, id_stock, quantite_achetee, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCommande);
            pstmt.setInt(2, idStock);
            pstmt.setInt(3, quantite);
            pstmt.setDouble(4, prixUnitaire);
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur insertion article dans la commande");
        }
    }
    
    public void afficherInfosClient(Client c) {
        try {
            // Affiche les champs de base
            jTextF_Nom.setText(c.getNom());
            jTextF_Prenom.setText(c.getPrenom());
            jTextF_Email.setText(c.getEmail());
            jTextF_CarteFidelite.setText(c.isCarteFidelite() ? "Oui" : "Non");

            // Récupérer l’adresse complète
            Adresse adr = Adresse.getAdresseById(c.getIdAdresse());
            if (adr != null) {
                jTextF_Adresse.setText(
                      adr.getNumRue() + " " + adr.getNomRue() + ", "
                    + adr.getCodePostal() + " " + adr.getLocalite() + " (" + adr.getPays() + ")"
                );
            } else {
                jTextF_Adresse.setText("Adresse introuvable");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'affichage du client");
        }
    }
    
    public void chargerCommandeExistante(int idCommande, int idClient) {
        this.idCommandeEnCours = idCommande;
        this.idClientSelectionne = idClient;

        // Charger les infos du client
        try {
            Client c = Client.getClientById(idClient);
            if (c != null) {
                afficherInfosClient(c); // Remplit les champs Nom, Prenom, etc.
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        // Charger les articles depuis Commande_Stock
        chargerArticlesDansPanier(idCommande);
    }
    
    private void chargerArticlesDansPanier(int idCommande) {
        DefaultTableModel model = (DefaultTableModel) jTable_Panier.getModel();
        model.setRowCount(0); // Vider avant de remplir

        String sql = "SELECT cs.id_stock, s.designation, cs.quantite_achetee, cs.prix_unitaire "
                   + "FROM Commande_Stock cs "
                   + "JOIN Stock s ON cs.id_stock = s.id_stock "
                   + "WHERE cs.id_commande = ?";

        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCommande);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idStock = rs.getInt("id_stock");
                    String designation = rs.getString("designation");
                    int qte = rs.getInt("quantite_achetee");
                    double prixU = rs.getDouble("prix_unitaire");
                    double total = qte * prixU;

                    model.addRow(new Object[]{
                        idStock, designation, qte, prixU, total
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des articles");
        }
    }
    
    private void majQuantiteEnBase(int idCommande, int idStock, int nouvelleQte) {
        String sql = "UPDATE Commande_Stock SET quantite_achetee=? WHERE id_commande=? AND id_stock=?";

        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nouvelleQte);
            pstmt.setInt(2, idCommande);
            pstmt.setInt(3, idStock);
            pstmt.executeUpdate();

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la mise à jour de la quantité.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void supprimerArticleDansCommande(int idCommande, int idStock) {
        String sql = "DELETE FROM Commande_Stock WHERE id_commande = ? AND id_stock = ?";
        try (Connection conn = Parametres.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCommande);
            pstmt.setInt(2, idStock);
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'article dans la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
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

        jLab_Titre = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_Panier = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_Stock = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jBtn_ajouterAuPanier = new javax.swing.JButton();
        jBtn_supprimer = new javax.swing.JButton();
        jBtn_EffectuerPaiement = new javax.swing.JButton();
        jBtn_Quitter = new javax.swing.JButton();
        jBtn_AffcherCommande = new javax.swing.JButton();
        jBtn_CréerCommande = new javax.swing.JButton();
        jBtn_SelectionnerClient = new javax.swing.JButton();
        jBtn_Modifier = new javax.swing.JButton();
        jLab_Nom = new javax.swing.JLabel();
        jLab_Prenom = new javax.swing.JLabel();
        jLab_Email = new javax.swing.JLabel();
        jLab_CarteFidelite = new javax.swing.JLabel();
        jLab_Adresse = new javax.swing.JLabel();
        jTextF_Nom = new javax.swing.JTextField();
        jTextF_Prenom = new javax.swing.JTextField();
        jTextF_Email = new javax.swing.JTextField();
        jTextF_CarteFidelite = new javax.swing.JTextField();
        jTextF_Adresse = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLab_Titre.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLab_Titre.setText("Fenetre Vente\n");
        getContentPane().add(jLab_Titre, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, -1, -1));

        jTable_Panier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID_Stock", "Designation", "Quantite_achete", "Prix_unitaire", "Total"
            }
        ));
        jScrollPane1.setViewportView(jTable_Panier);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 380, 590, 130));

        jTable_Stock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID_Stock", "Designation", "Quantite_Stock", "Prix_unitaire", "Taux_tva", "Codes_barres", "Categorie"
            }
        ));
        jScrollPane2.setViewportView(jTable_Stock);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 200, 590, 160));

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jBtn_ajouterAuPanier.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_ajouterAuPanier.setText("Ajouter au Panier");
        jBtn_ajouterAuPanier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ajouterAuPanierActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_ajouterAuPanier, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 160, -1));

        jBtn_supprimer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_supprimer.setText("Supprimer du panier");
        jBtn_supprimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_supprimerActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_supprimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 160, -1));

        jBtn_EffectuerPaiement.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_EffectuerPaiement.setText("Effectuer Paiement");
        jBtn_EffectuerPaiement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_EffectuerPaiementActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_EffectuerPaiement, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 160, 30));

        jBtn_Quitter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_Quitter.setText("Quitter");
        jBtn_Quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_QuitterActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_Quitter, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 460, -1, -1));

        jBtn_AffcherCommande.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_AffcherCommande.setText("Afficher Commande");
        jBtn_AffcherCommande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_AffcherCommandeActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_AffcherCommande, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 170, -1));

        jBtn_CréerCommande.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_CréerCommande.setText("Créer Commande");
        jBtn_CréerCommande.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_CréerCommandeActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_CréerCommande, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 160, -1));

        jBtn_SelectionnerClient.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_SelectionnerClient.setText("Selectionner Client");
        jBtn_SelectionnerClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_SelectionnerClientActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_SelectionnerClient, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 160, -1));

        jBtn_Modifier.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_Modifier.setText("Modifier Panier");
        jBtn_Modifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ModifierActionPerformed(evt);
            }
        });
        jPanel3.add(jBtn_Modifier, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 160, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 520));

        jLab_Nom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Nom.setText("Nom");
        getContentPane().add(jLab_Nom, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, -1, -1));

        jLab_Prenom.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Prenom.setText("Prenom");
        getContentPane().add(jLab_Prenom, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 120, -1, -1));

        jLab_Email.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Email.setText("Email");
        getContentPane().add(jLab_Email, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 160, -1, -1));

        jLab_CarteFidelite.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_CarteFidelite.setText("Carte Fidélité");
        getContentPane().add(jLab_CarteFidelite, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 100, -1, -1));

        jLab_Adresse.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLab_Adresse.setText("Adresse");
        getContentPane().add(jLab_Adresse, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 160, -1, -1));
        getContentPane().add(jTextF_Nom, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 80, 230, -1));
        getContentPane().add(jTextF_Prenom, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 120, 230, -1));
        getContentPane().add(jTextF_Email, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 160, 230, -1));
        getContentPane().add(jTextF_CarteFidelite, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 100, 230, -1));
        getContentPane().add(jTextF_Adresse, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 160, 230, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtn_ajouterAuPanierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ajouterAuPanierActionPerformed
        if (idCommandeEnCours < 0) {
        JOptionPane.showMessageDialog(this, "Créez d’abord une commande.");
        return;
        }
        // 1) Récupérer la ligne sélectionnée dans jTable_Stock
        int row = jTable_Stock.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un article dans la liste du haut.");
            return;
        }

        // 2) Extraire infos (idStock, prixUnitaire, etc.)
        int idStock = (int) jTable_Stock.getValueAt(row, 0);
        String designation = jTable_Stock.getValueAt(row, 1).toString();
        double prix = Double.parseDouble(jTable_Stock.getValueAt(row, 3).toString());

        // 3) Demander la quantité
        String qteStr = JOptionPane.showInputDialog(this, "Quantité : ", "1");
        if (qteStr == null) return; 
        int qte = Integer.parseInt(qteStr);
        
        //  Vérifier si cet article est déjà dans le panier
        DefaultTableModel modelPanier = (DefaultTableModel) jTable_Panier.getModel();
        for (int i = 0; i < modelPanier.getRowCount(); i++) {
            int existingStock = (int) modelPanier.getValueAt(i, 0); // colonne ID_Stock
            if (existingStock == idStock) {
                JOptionPane.showMessageDialog(this,
                    "Cet article existe déjà dans le panier. " 
                  + "Modifiez plutôt la quantité si nécessaire.");
                return; // on s'arrête, pas d'ajout ni d'insertion en BD
            }
        }

        // 4) Appeler la méthode qui insère dans Commande_Stock
        ajouterArticleDansCommande(idCommandeEnCours, idStock, qte, prix);

        // 5) Mettre à jour la JTable du bas (le panier)
        //DefaultTableModel modelPanier = (DefaultTableModel) jTable_Panier.getModel();
        double totalLigne = qte * prix;
        modelPanier.addRow(new Object[]{
            idStock, designation, qte, prix, totalLigne
        });
         
    }//GEN-LAST:event_jBtn_ajouterAuPanierActionPerformed

    private void jBtn_supprimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_supprimerActionPerformed
        int selectedRow = jTable_Panier.getSelectedRow();
        if (selectedRow != -1) {
            // Récupérer l'idStock de la ligne sélectionnée
            int idStock = (int) jTable_Panier.getValueAt(selectedRow, 0);

            // Supprimer l'article dans la base pour la commande en cours
            supprimerArticleDansCommande(idCommandeEnCours, idStock);

            // Supprimer la ligne du modèle de la JTable
            DefaultTableModel model = (DefaultTableModel) jTable_Panier.getModel();
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Sélectionnez un article à supprimer du panier.");
        }
    }//GEN-LAST:event_jBtn_supprimerActionPerformed

    private void jBtn_QuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_QuitterActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBtn_QuitterActionPerformed

    private void jBtn_EffectuerPaiementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_EffectuerPaiementActionPerformed
        // Vérifier que le panier n'est pas vide
        if (jTable_Panier.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Le panier est vide. Ajoutez des articles avant de valider le paiement.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /*// Calculer le montant total de la commande
        double montantTotal = calculerTotalCommande();
        System.out.println("💰 Montant total calculé : " + montantTotal);

        // Afficher une boîte de dialogue indiquant le montant à payer
        JOptionPane.showMessageDialog(this, "Vous avez " + montantTotal + " € à payer.", "Montant Total", JOptionPane.INFORMATION_MESSAGE);*/
        
        // 2. Calculer le montant total TTC en parcourant le panier
        //    On suppose que jTable_Panier a les colonnes suivantes :
        //    col0 : idStock, col1 : désignation, col2 : quantité, col3 : prix unitaire (HT), col4 : total HT
        double sousTotalHTGlobal = 0;
        double tvaGlobal = 0;
        double tauxTVA = 21.0;  // taux TVA fixe (en %). Adaptez si nécessaire.
        DefaultTableModel modelPanier = (DefaultTableModel) jTable_Panier.getModel();
        for (int i = 0; i < modelPanier.getRowCount(); i++) {
            double totalHT = Double.parseDouble(modelPanier.getValueAt(i, 4).toString());
            sousTotalHTGlobal += totalHT;
            tvaGlobal += totalHT * (tauxTVA / 100.0);
        }
        double montantTotalTTC = sousTotalHTGlobal + tvaGlobal;
        System.out.println("Montant total TTC : " + montantTotalTTC);

        // 3. Afficher une boîte de dialogue indiquant le montant TTC à payer
        JOptionPane.showMessageDialog(this, "Vous avez " + montantTotalTTC + " € à payer.", "Montant Total", JOptionPane.INFORMATION_MESSAGE);

        
        // Demander le mode de paiement
        String[] modes = {"Cash", "Carte", "Cash et Carte"};
        int choix = JOptionPane.showOptionDialog(this, 
                "Choisissez le mode de paiement :", 
                "Paiement", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                modes, 
                modes[0]);
        if (choix == -1) return; // L'utilisateur a annulé

        double montantCash = 0, montantCarte = 0;
        switch (choix) {
            case 0: // Paiement uniquement par Cash
                montantCash = montantTotalTTC;
                montantCarte = 0;
                break;
            case 1: // Paiement uniquement par Carte
                montantCarte = montantTotalTTC;
                montantCash = 0;
                break;
            case 2: // Paiement mixte : Cash et Carte
                // Demander le montant à payer en Cash
                String cashStr = JOptionPane.showInputDialog(this, "Entrez le montant à payer en Cash (en €) :", "0");
                if (cashStr == null || cashStr.trim().isEmpty()) {
                    cashStr = "0";
                }
                try {
                    montantCash = Double.parseDouble(cashStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Montant invalide pour le paiement en Cash.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                montantCarte = montantTotalTTC - montantCash;
                if (montantCarte < 0) {
                    JOptionPane.showMessageDialog(this, "Le montant en Cash dépasse le total de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
        }

        // Vérifier que la somme des paiements est au moins égale au montant total
        double sommePaiements = montantCash + montantCarte;
        if (sommePaiements < montantTotalTTC) {
            JOptionPane.showMessageDialog(this, 
                "Le montant payé (" + sommePaiements + " €) est inférieur au total de la commande (" + montantTotalTTC + " €).", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (sommePaiements > montantTotalTTC) {
            JOptionPane.showMessageDialog(this, 
                "Attention : Le montant payé dépasse le total de la commande.", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            // Vous pouvez choisir de continuer ou d'ajuster les montants ici
        }

        // --- Procéder au paiement dans une transaction ---
        try (Connection conn = Parametres.getConnection()) {
            conn.setAutoCommit(false); // Démarrer la transaction
            
             // Vérification : afficher si la connexion est ouverte
             System.out.println("conn.isClosed() = " + conn.isClosed());

            // 1) Mettre à jour le statut de la commande en "clôturé"
            boolean majStatut = Commande.updateStatutCommande(conn, idCommandeEnCours, "clôturé");
            if (!majStatut) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut de la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2) Créer le ticket de caisse (facture)
            String sqlTicket = "INSERT INTO Ticket_Caisse (statut, id_employe, id_client, id_commande) VALUES (?, ?, ?, ?)";
            int idTicket = -1;
            try (PreparedStatement pstmtTicket = conn.prepareStatement(sqlTicket, Statement.RETURN_GENERATED_KEYS)) {
                pstmtTicket.setString(1, "Payé");
                int idEmploye = FenetreConnexion.employeActif.getIdEmploye();
                pstmtTicket.setInt(2, idEmploye);
                pstmtTicket.setInt(3, idClientSelectionne);
                pstmtTicket.setInt(4, idCommandeEnCours);
                pstmtTicket.executeUpdate();
                try (ResultSet rs = pstmtTicket.getGeneratedKeys()) {
                    if (rs.next()) {
                        idTicket = rs.getInt(1);
                    }
                }
            }

            // 3) Insérer les paiements dans la table est_lie
            String sqlPaiement = "INSERT INTO est_lie (id_ticket_caisse, id_paiement, montant) VALUES (?, ?, ?)";
            try (PreparedStatement pstmtPaiement = conn.prepareStatement(sqlPaiement)) {
                // Paiement Cash
                int idPaiementCash = getIdPaiement(conn, "Cash"); // Passage de la connexion
                pstmtPaiement.setInt(1, idTicket);
                pstmtPaiement.setInt(2, idPaiementCash);
                pstmtPaiement.setDouble(3, montantCash);
                pstmtPaiement.executeUpdate();

                // Paiement Carte
                int idPaiementCarte = getIdPaiement(conn, "Carte");
                pstmtPaiement.setInt(1, idTicket);
                pstmtPaiement.setInt(2, idPaiementCarte);
                pstmtPaiement.setDouble(3, montantCarte);
                pstmtPaiement.executeUpdate();
            }

            // 4) Mettre à jour le stock pour chaque article du panier
            DefaultTableModel model = (DefaultTableModel) jTable_Panier.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                int idStock = (int) model.getValueAt(i, 0);
                int quantiteAchetee = (int) model.getValueAt(i, 2);
                String sqlUpdateStock = "UPDATE Stock SET quantite_stock = quantite_stock - ? WHERE id_stock = ?";
                try (PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {
                    pstmtStock.setInt(1, quantiteAchetee);
                    pstmtStock.setInt(2, idStock);
                    pstmtStock.executeUpdate();
                }
            }

            conn.commit(); // Valider la transaction

            // 5) Générer la facture PDF via la classe FacturePDF
            //FacturePDF.genererFacturePDF(idTicket, idClientSelectionne, FenetreConnexion.employeActif.getIdEmploye(), montantTotal, modelPanier);
            Utils.FacturePDF.genererFacturePDF(idTicket, idClientSelectionne, FenetreConnexion.employeActif.getIdEmploye(), montantTotalTTC, model);

            JOptionPane.showMessageDialog(this, "Paiement effectué, commande clôturée et facture générée !");

            // 6) Vider le panier
            model.setRowCount(0);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du paiement.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        
         
    }//GEN-LAST:event_jBtn_EffectuerPaiementActionPerformed

    private void jBtn_AffcherCommandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_AffcherCommandeActionPerformed
        // TODO add your handling code here:
        new GestionDesCommandes().setVisible(true);
    }//GEN-LAST:event_jBtn_AffcherCommandeActionPerformed

    private void jBtn_SelectionnerClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_SelectionnerClientActionPerformed
         // On passe `this` pour que la fenêtre clients sache qui est la parent
        GestionClients fenetreClients = new GestionClients(this);
        fenetreClients.setVisible(true);

        // -> Il n’y a pas de code juste après, 
        //    car ce JFrame n’est pas modal : le programme continue. 
        //    La sélection sera faite directement depuis la fenêtre clients.
    }//GEN-LAST:event_jBtn_SelectionnerClientActionPerformed

    private void jBtn_CréerCommandeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_CréerCommandeActionPerformed
        // 1) Vérifier qu’un client est sélectionné
        if (idClientSelectionne < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez d’abord un client.");
            return;
        }

        // 2) Récupérer l’employé connecté
        Employe empConnecte = FenetreConnexion.employeActif;
        if (empConnecte == null) {
            JOptionPane.showMessageDialog(this, "Aucun employé connecté !");
            return;
        }

        // 3) Créer la commande
        Commande cmd = new Commande();
        cmd.setIdClient(idClientSelectionne);
        cmd.setIdEmploye(empConnecte.getIdEmploye());
        try {
            boolean ok = Commande.createCommande(cmd);
            if (ok) {
                idCommandeEnCours = cmd.getIdCommande(); // l’ID généré
                JOptionPane.showMessageDialog(this, 
                    "Commande créée. ID=" + idCommandeEnCours 
                    + "\nStatut = En cours.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur création commande");
        }
    }//GEN-LAST:event_jBtn_CréerCommandeActionPerformed

    private void jBtn_ModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ModifierActionPerformed
        // 1) Vérifier qu'une commande est en cours (ou que la commande est “En cours”)
        //    Si vous tenez absolument à vérifier le statut, vous pouvez le faire via Commande.getCommandeById(...)
        if (idCommandeEnCours < 0) {
            JOptionPane.showMessageDialog(this, 
                "Créez ou chargez d’abord une commande en cours.");
            return;
        }

        // 2) Sélectionner une ligne dans jTable_Panier
        int rowArticle = jTable_Panier.getSelectedRow();
        if (rowArticle == -1) {
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez un article à modifier dans le panier.");
            return;
        }

        // 3) Récupérer l’idStock et l’ancienne quantité
        int idStock = (int) jTable_Panier.getValueAt(rowArticle, 0);
        int ancienneQte = (int) jTable_Panier.getValueAt(rowArticle, 2);

        // 4) Demander la nouvelle quantité à l’utilisateur
        String quantiteStr = JOptionPane.showInputDialog(
            this, 
            "Nouvelle Quantité (ancienne valeur : " + ancienneQte + ")", 
            String.valueOf(ancienneQte) // valeur par défaut
        );
        if (quantiteStr == null) return; // l’utilisateur a annulé
        int nouvelleQte = Integer.parseInt(quantiteStr);

        // 5) Mettre à jour en base
        majQuantiteEnBase(idCommandeEnCours, idStock, nouvelleQte);

        // 6) Recharger la table du panier pour refléter la nouvelle quantité
        //    (ou vous pouvez juste mettre à jour la cellule localement, 
        //     mais recharger est plus sûr s’il y a un recalcul de prix, etc.)
        chargerArticlesDansPanier(idCommandeEnCours);

        JOptionPane.showMessageDialog(this, "Quantité modifiée avec succès.");

    }//GEN-LAST:event_jBtn_ModifierActionPerformed

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
            java.util.logging.Logger.getLogger(GestionVente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionVente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionVente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionVente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionVente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_AffcherCommande;
    private javax.swing.JButton jBtn_CréerCommande;
    private javax.swing.JButton jBtn_EffectuerPaiement;
    private javax.swing.JButton jBtn_Modifier;
    private javax.swing.JButton jBtn_Quitter;
    private javax.swing.JButton jBtn_SelectionnerClient;
    private javax.swing.JButton jBtn_ajouterAuPanier;
    private javax.swing.JButton jBtn_supprimer;
    private javax.swing.JLabel jLab_Adresse;
    private javax.swing.JLabel jLab_CarteFidelite;
    private javax.swing.JLabel jLab_Email;
    private javax.swing.JLabel jLab_Nom;
    private javax.swing.JLabel jLab_Prenom;
    private javax.swing.JLabel jLab_Titre;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable_Panier;
    private javax.swing.JTable jTable_Stock;
    private javax.swing.JTextField jTextF_Adresse;
    private javax.swing.JTextField jTextF_CarteFidelite;
    private javax.swing.JTextField jTextF_Email;
    private javax.swing.JTextField jTextF_Nom;
    private javax.swing.JTextField jTextF_Prenom;
    // End of variables declaration//GEN-END:variables
}
