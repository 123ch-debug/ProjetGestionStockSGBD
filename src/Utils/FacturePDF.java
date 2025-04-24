/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;
import Modele.Parametres;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class FacturePDF {
    /**
     * Génère une facture PDF avec les informations du ticket et le contenu du panier.
     * 
     * @param idTicket     l'ID du ticket de caisse (facture)
     * @param idClient     l'ID du client
     * @param idEmploye    l'ID de l'employé qui a traité le paiement
     * @param montantTotal le montant total de la commande
     * @param modelPanier  le modèle de la JTable contenant les articles de la commande
     */
    public static void genererFacturePDF(int idTicket, int idClient, int idEmploye, double montantTotal, DefaultTableModel modelPanier) {
        try {
            Connection conn = Parametres.getConnection();
            Document document = new Document();
            try {
                String fileName = "Facture_Ticket_" + idTicket + ".pdf";
                PdfWriter.getInstance(document, new FileOutputStream(fileName));
                document.open();
                
                // 1) Ajouter le logo (si vous avez un fichier logo.png, par exemple)
                // Astuce : placez-le dans un dossier "resources" ou à la racine du projet
                String logoPath = "C:\\Users\\PC\\OneDrive\\Documents\\NetbeansSGBD projets\\GROUPE DE PROJETS SGBD\\ProjetGestionStockSGBD\\src\\Images\\connection\\logo.JPG";
                try {
                    Image logo = Image.getInstance(logoPath);
                    logo.scaleToFit(80, 80);
                    document.add(logo);
                } catch (Exception e) {
                    System.err.println("Logo introuvable, on continue sans logo : " + e.getMessage());
                }
                
                // 2) Entête du magasin
                Paragraph enteteMagasin = new Paragraph(
                    "BRICO-BELGIQUE\n" +
                    "12, Rue du Bricolage, 1000 Bruxelles (Belgique)\n" +
                    "Tél : +32 2 123 45 67\n" +
                    "Email : contact@brico-belgique.be\n\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK)
                );
                document.add(enteteMagasin);
                
                // Titre de la facture
                Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("Facture", fontTitle);
                title.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" "));
                
                // Récupération du nom de l'employé
                PreparedStatement pstmtEmploye = conn.prepareStatement("SELECT nom FROM Employe WHERE id_employe = ?");
                pstmtEmploye.setInt(1, idEmploye);
                ResultSet rsEmploye = pstmtEmploye.executeQuery();
                String nomEmploye = "Inconnu";
                if (rsEmploye.next()) {
                    nomEmploye = rsEmploye.getString("nom");
                }
                document.add(new Paragraph("Vous êtes servi par : " + nomEmploye, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY)));
                
                
                // Informations du ticket
                document.add(new Paragraph("Ticket de Caisse n° : " + idTicket));
                document.add(new Paragraph("Client ID : " + idClient));
                document.add(new Paragraph("Employé ID : " + idEmploye));
                document.add(new Paragraph("Montant total : " + montantTotal + " €"));
                document.add(new Paragraph(" "));
                
                // Création d'un tableau pour afficher les articles
                PdfPTable table = new PdfPTable(6); // 5 colonnes
                table.setWidthPercentage(100);
                // Largeur relative des colonnes
                table.setWidths(new float[]{10f, 40f, 10f, 15f, 15f, 15f});
                
                /*table.addCell("ID Stock");
                table.addCell("Désignation");
                table.addCell("Quantité");
                table.addCell("Prix Unitaire");
                table.addCell("Total");*/
                
                // Entête du tableau
                table.addCell("ID Stock");
                table.addCell("Désignation");
                table.addCell("Qté");
                table.addCell("Prix U. (HT)");
                table.addCell("TVA (%)");
                table.addCell("Sous-Total (HT)");

                // Variables pour cumul si tu veux un récap final
                double sousTotalHTGlobal = 0;
                double tvaGlobal = 0;
                // Parcourir le modèle du panier et ajouter chaque ligne dans le tableau PDF
                /*for (int i = 0; i < modelPanier.getRowCount(); i++) {
                    table.addCell(modelPanier.getValueAt(i, 0).toString());
                    table.addCell(modelPanier.getValueAt(i, 1).toString());
                    table.addCell(modelPanier.getValueAt(i, 2).toString());
                    table.addCell(modelPanier.getValueAt(i, 3).toString());
                    table.addCell(modelPanier.getValueAt(i, 4).toString());
                }
                document.add(table);*/
                
                // 6) Parcourir le panier
                //   On suppose que la jTable_Panier stocke :
                //   col0 = idStock
                //   col1 = designation
                //   col2 = quantite
                //   col3 = prixUnitaire (HT)
                //   col4 = total (HT)
                //   Si vous avez le Taux TVA dans col5, récupérez-le.
                //   Sinon, mettez un taux par défaut ou récupérez-le depuis la BD.
                for (int i = 0; i < modelPanier.getRowCount(); i++) {
                    int idStock = (int) modelPanier.getValueAt(i, 0);
                    String designation = modelPanier.getValueAt(i, 1).toString();
                    int qte = (int) modelPanier.getValueAt(i, 2);
                    double prixU = (double) modelPanier.getValueAt(i, 3);
                    // double tva = 21.0; // Par exemple, si tu n'as pas la TVA dans la jTable
                    // Ou si tu as la TVA en col5 : double tva = (double) modelPanier.getValueAt(i, 5);
                    double tva = 21.0; // Ex. Taux standard BE

                    // Sous-total HT = qte * prixU
                    double sousTotalHT = qte * prixU;
                    sousTotalHTGlobal += sousTotalHT;
                    // TVA pour cette ligne
                    double tvaLigne = sousTotalHT * (tva / 100.0);
                    tvaGlobal += tvaLigne;

                    table.addCell(String.valueOf(idStock));
                    table.addCell(designation);
                    table.addCell(String.valueOf(qte));
                    table.addCell(String.format("%.2f", prixU));
                    table.addCell(String.format("%.2f", tva));
                    table.addCell(String.format("%.2f", sousTotalHT));
                }

                document.add(table);
                document.add(new Paragraph("\n"));

                // 7) Récap (ex. Sous-total HT, TVA, Total TTC)
                double totalTTC = sousTotalHTGlobal + tvaGlobal;
                Paragraph recap = new Paragraph(
                    "Sous-total HT : " + String.format("%.2f", sousTotalHTGlobal) + " €\n" +
                    "TVA : " + String.format("%.2f", tvaGlobal) + " €\n" +
                    "Total TTC : " + String.format("%.2f", totalTTC) + " €\n\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)
                );
                recap.setAlignment(Paragraph.ALIGN_RIGHT);
                document.add(recap);

                // 8) Bas de page
                Paragraph footer = new Paragraph(
                    "Merci pour votre achat chez BRICO-BELGIQUE !\n" +
                    "TVA : BE123456789\n" +
                    "Site Web : https://www.bricobel.be\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY)
                );
                document.add(footer);
                
                
                document.close();
                JOptionPane.showMessageDialog(null, "Facture générée en PDF : " + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur lors de la génération de la facture PDF.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacturePDF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FacturePDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
