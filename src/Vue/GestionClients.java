/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vue;

import Modele.Adresse;
import Modele.Client;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author PC
 */
public class GestionClients extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    //private GestionVente gestionVente; // Référence à la fenêtre GestionVente
    private Client selectedClient;  // Client choisi
    private GestionVente parentVente;
    
    /**
     * Creates new form GestionClients
     */
    public GestionClients() {
       initComponents();
       
       tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Prénom", "Email","ID_Adresse", "Carte Fidélité"}, 0);
       jTableClients.setModel(tableModel);

        // Charger les clients en base
        chargerClients();
    }
    

    public GestionClients(GestionVente parent) {
        super(); // Comme c'est un JFrame
        initComponents();

        this.parentVente = parent; 
        // Initialiser la table, chargerClients() etc.

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Prénom", "Email","ID_Adresse", "Carte Fidélité"}, 
            0
        );
        jTableClients.setModel(tableModel);
        chargerClients();
    }

    
    public Client getSelectedClient() {
        return selectedClient;
    }
    
    public void chargerClients() {
        try {
            List<Client> listeClients = Client.getAllClients();
            tableModel.setRowCount(0); // On vide la table avant de la remplir

            for (Client c : listeClients) {
                tableModel.addRow(new Object[]{
                    c.getIdClient(),
                    c.getNom(),
                    c.getPrenom(),
                    c.getEmail(),
                    c.getIdAdresse(), // On affiche l'ID de l'adresse
                    c.isCarteFidelite() ? "Oui" : "Non"
                });
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "❌ Erreur lors du chargement des clients: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void rechercherClient(String recherche) {
        try {
            List<Client> clients = Client.rechercherClientsParNomOuId(recherche);

            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Aucun client trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Si un seul client trouvé, affiche directement ses infos
            if (clients.size() == 1) {
                afficherInformationsClient(clients.get(0));
                return;
            }

            // Si plusieurs clients trouvés, affiche une liste pour choisir
            Client clientSelectionne = demanderSelectionClient(clients);

            if (clientSelectionne != null) {
                afficherInformationsClient(clientSelectionne);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Client demanderSelectionClient(List<Client> clients) {
        // Création d'une liste déroulante avec les clients trouvés
        JComboBox<Client> comboBox = new JComboBox<>();

        for (Client c : clients) {
            comboBox.addItem(c); // Ajoute chaque client dans la liste déroulante
        }

        int result = JOptionPane.showConfirmDialog(
            this, 
            comboBox, 
            "Sélectionnez un client", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (Client) comboBox.getSelectedItem();
        }

        return null; // Si l'utilisateur annule
    }
    
    private void afficherInformationsClient(Client client) {
        try {
            Adresse adresse = Adresse.getAdresseById(client.getIdAdresse()); // Récupérer l'adresse

            String message = "📌 **Informations du Client** :\n"
                + "🆔 ID : " + client.getIdClient() + "\n"
                + "👤 Nom : " + client.getNom() + "\n"
                + "👤 **Prénom** : " + client.getPrenom() + "\n"   
                + "📧 Email : " + client.getEmail() + "\n"
                + "🏠 Adresse : " + (adresse != null ? adresse.getNomRue() + ", " + adresse.getLocalite() : "Non renseignée") + "\n"
                + "📜 **Carte fidélité** : " + (client.isCarteFidelite() ? "Oui" : "Non") + "\n";

            JOptionPane.showMessageDialog(this, message, "Client trouvé", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'affichage des informations.", "Erreur", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jBn_Ajout = new javax.swing.JButton();
        jBtn_Modif = new javax.swing.JButton();
        jBtn_supp = new javax.swing.JButton();
        jBtn_Quitter = new javax.swing.JButton();
        jBtn_SelectionnerClient = new javax.swing.JButton();
        jLab_Minifond = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableClients = new javax.swing.JTable();
        jTextF_Rechercher = new javax.swing.JTextField();
        jBtn_RechercherClient = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLab_titre = new javax.swing.JLabel();
        jLab_imgCLt = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jBn_Ajout.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBn_Ajout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/ajouter.JPG"))); // NOI18N
        jBn_Ajout.setText("Ajouter_Clt");
        jBn_Ajout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBn_AjoutActionPerformed(evt);
            }
        });
        jPanel1.add(jBn_Ajout, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 160, -1));

        jBtn_Modif.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_Modif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/modif.JPG"))); // NOI18N
        jBtn_Modif.setText("Modifier_Clt");
        jBtn_Modif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_ModifActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_Modif, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 160, -1));

        jBtn_supp.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_supp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/supp.JPG"))); // NOI18N
        jBtn_supp.setText("Supprimer_Clt");
        jBtn_supp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_suppActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_supp, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 160, -1));

        jBtn_Quitter.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_Quitter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/quitter.JPG"))); // NOI18N
        jBtn_Quitter.setText("Quitter");
        jBtn_Quitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_QuitterActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_Quitter, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 450, -1, -1));

        jBtn_SelectionnerClient.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jBtn_SelectionnerClient.setText("Selectionner Client");
        jBtn_SelectionnerClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_SelectionnerClientActionPerformed(evt);
            }
        });
        jPanel1.add(jBtn_SelectionnerClient, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 370, -1, -1));

        jLab_Minifond.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/Client2.jpg"))); // NOI18N
        jPanel1.add(jLab_Minifond, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 530));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 520));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTableClients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nom", "Prenom", "Email", "ID_Adresse", "Carte_Fidelité"
            }
        ));
        jTableClients.setToolTipText("");
        jTableClients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableClientsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableClients);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 203, 646, 226));

        jTextF_Rechercher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextF_RechercherActionPerformed(evt);
            }
        });
        jPanel2.add(jTextF_Rechercher, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 20, 190, -1));

        jBtn_RechercherClient.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jBtn_RechercherClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/imgRech.JPG"))); // NOI18N
        jBtn_RechercherClient.setText("Rechercher Client");
        jBtn_RechercherClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtn_RechercherClientActionPerformed(evt);
            }
        });
        jPanel2.add(jBtn_RechercherClient, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));
        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 60));

        jLab_titre.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLab_titre.setText("Gestion des clients");
        jPanel2.add(jLab_titre, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, -1, -1));

        jLab_imgCLt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/connection/client.JPG"))); // NOI18N
        jPanel2.add(jLab_imgCLt, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 78, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, 710, 520));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBn_AjoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBn_AjoutActionPerformed
        // TODO add your handling code here:
        FenetreAjoutClient dialog = new FenetreAjoutClient(this, true);
        dialog.setVisible(true);
        chargerClients();
       
    }//GEN-LAST:event_jBn_AjoutActionPerformed

    private void jBtn_ModifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_ModifActionPerformed
         int selectedRow = jTableClients.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this, 
                "❗ Veuillez sélectionner un client à modifier.", 
                "Alerte", 
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Récupérer l'ID client depuis la jTable
        int idClient = (int) jTableClients.getValueAt(selectedRow, 0);

        try {
            // Charger le client en base
            Client c = Client.getClientById(idClient);
            if (c == null) {
                JOptionPane.showMessageDialog(
                    this, 
                    "❌ Client introuvable en base.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Récupérer l'adresse associée
            // (si vous gérez un objet Adresse dans la fenêtre)
            // Exemple:
            // Adresse adr = Adresse.getAdresseById(c.getIdAdresse());

            // Ouvrir la fenêtre d'édition (mode "modif")
            // On peut utiliser un constructeur dédié dans FenetreAjoutClient
            Adresse adr = Adresse.getAdresseById(c.getIdAdresse());
            FenetreAjoutClient dialog = new FenetreAjoutClient(this, true, c , adr );
            dialog.setVisible(true);

            // Rafraîchir
            chargerClients();

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "❌ Erreur lors de la modification: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_jBtn_ModifActionPerformed

    private void jBtn_suppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_suppActionPerformed
        int selectedRow = jTableClients.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this, 
                "❌ Sélectionnez un client à supprimer.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int idClient = (int) jTableClients.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "⚠ Voulez-vous vraiment supprimer ce client ?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = Client.deleteClientAndAddress(idClient); 
                // ou vous faites un deleteClientAndAddress(...) si vous l'avez codé
                if (success) {
                    JOptionPane.showMessageDialog(
                        this, 
                        "✅ Client supprimé avec succès."
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this, 
                        "❌ Erreur lors de la suppression.", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                chargerClients(); 
            } catch (SQLException ex) {
                Logger.getLogger(GestionClients.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GestionClients.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jBtn_suppActionPerformed

    private void jBtn_QuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_QuitterActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir quitter ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Ferme la fenêtre si l'utilisateur confirme
        }
    }//GEN-LAST:event_jBtn_QuitterActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        chargerClients();
    }//GEN-LAST:event_formWindowOpened

    private void jTableClientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableClientsMouseClicked
        
        int selectedRow = jTableClients.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Veuillez sélectionner un client.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Récupérer l'ID client depuis la JTable (colonne 0)
        int idClient = (int) jTableClients.getValueAt(selectedRow, 0);

        try {
            // Charger les informations du client depuis la base de données
            Client client = Client.getClientById(idClient);
            if (client == null) {
                JOptionPane.showMessageDialog(this, "❌ Client introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

        // Charger l'adresse du client
        Adresse adresse = Adresse.getAdresseById(client.getIdAdresse());

            // Construire le message d'affichage
           String info =
                     "👤 **Nom** : " + client.getNom() + "\n"
                    + "👤 **Prénom** : " + client.getPrenom() + "\n"
                    + "🎖 **Email** : " + client.getEmail() + "\n"
                    + "📜 **Carte fidélité** : " + (client.isCarteFidelite() ? "Oui" : "Non") + "\n"
                    + "\n📍 **Adresse**\n"
                    + "🏠 **Numéro de rue** : " + adresse.getNumRue() + "\n"
                    + "🏠 **Nom de rue** : " + adresse.getNomRue() + "\n"
                    + "🌍 **Localité** : " + adresse.getLocalite() + "\n"
                    + "🇫🇷 **Pays** : " + adresse.getPays() + "\n"
                    + "📮 **Code Postal** : " + adresse.getCodePostal();
           
            // Afficher dans une boîte de dialogue
            JOptionPane.showMessageDialog(this, info, "Information Client", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des informations.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jTableClientsMouseClicked

    private void jTextF_RechercherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextF_RechercherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextF_RechercherActionPerformed

    private void jBtn_RechercherClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_RechercherClientActionPerformed
        rechercherClient(jTextF_Rechercher.getText().trim());
    }//GEN-LAST:event_jBtn_RechercherClientActionPerformed

    private void jBtn_SelectionnerClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtn_SelectionnerClientActionPerformed
        int selectedRow = jTableClients.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Veuillez sélectionner un client dans le tableau.",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int idClient = (int) jTableClients.getValueAt(selectedRow, 0);

        try {
            Client c = Client.getClientById(idClient);
            if (c == null) {
                JOptionPane.showMessageDialog(this,
                    "Impossible de charger le client (ID " + idClient + ").",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Envoyer ce client dans la fenêtre parent
            if (parentVente != null) {
                // 1) On dit à la fenêtre parent quel client est choisi
                parentVente.setIdClientSelectionne(c.getIdClient());
                // 2) On affiche les infos du client directement
                parentVente.afficherInfosClient(c);
            }

            // Fermer la fenêtre clients
            this.dispose();

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération du client.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
        
    }//GEN-LAST:event_jBtn_SelectionnerClientActionPerformed

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
            java.util.logging.Logger.getLogger(GestionClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionClients().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBn_Ajout;
    private javax.swing.JButton jBtn_Modif;
    private javax.swing.JButton jBtn_Quitter;
    private javax.swing.JButton jBtn_RechercherClient;
    private javax.swing.JButton jBtn_SelectionnerClient;
    private javax.swing.JButton jBtn_supp;
    private javax.swing.JLabel jLab_Minifond;
    private javax.swing.JLabel jLab_imgCLt;
    private javax.swing.JLabel jLab_titre;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableClients;
    private javax.swing.JTextField jTextF_Rechercher;
    // End of variables declaration//GEN-END:variables
}
