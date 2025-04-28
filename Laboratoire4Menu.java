package ca.ets.tch055_H23.laboratoire4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


/**
 * Classe principale du laboratoire 4
 * Contient un ensemble de méthodes statique pour 
 * la manipulation de la BD Produit 
 * 
 * @equipe : XX
 * 
 * @author
 * @author
 * @author
 * @author
 *
 */
public class Laboratoire4Menu {
	
	public static Statement statmnt = null;
	
	/* Référence vers l'objer de connection à la BD*/ 
	public static Connection connexion = null;
	
	/* Chargement du pilote Oracle */ 
	static {
	   try {
		   Class.forName("oracle.jdbc.driver.OracleDriver");
	   } catch (ClassNotFoundException e) {
		
		   e.printStackTrace();
	   }
	}
	
	/**
	 * Question : Ouverture de la connection
	 * 
	 * @param login
	 * @param password
	 * @param uri
	 * @return
	 */
    public static Connection connexionBDD(String login, String password, String uri) {
    	
    	Connection une_connexion = null ;

    	try{
			une_connexion = DriverManager.getConnection(uri,login,password);
		} catch (SQLException e){
			e.printStackTrace();
		}

    	return une_connexion  ; 
    }
    
    /**
     *  Option 1 - lister les produits 
     */
    public static void listerProduits() {
    	String[] colones = new String[]{"Référence", "NOM", "MARQUE", "Prix Unitaire", "Quantité", "Seuil", "Statut", "Code Fournisseur"};

		String separation = "";
		for(String c: colones){
			separation += "-----------------";
		}


		try{
			Statement requete = connexion.createStatement();

			ResultSet result = requete.executeQuery("SELECT * FROM produit ORDER BY ref_produit");

			System.out.println(separation);
			System.out.println(String.format("%-17s%-17s%-17s%-17s%-17s%-17s%-17s%-17s", colones));
			System.out.println(separation);

			while(result.next()){
				System.out.println(String.format("%-17s%-17s%-17s%-17.2f%-17d%-17d%-17s%-17d",
						result.getString("ref_produit"),
						result.getString("nom_produit"),
						result.getString("marque"),
						result.getDouble("prix_unitaire"),
						result.getInt("quantite_stock"),
						result.getInt("quantite_seuil"),
						result.getString("statut_produit"),
						result.getInt("code_fournisseur_prioritaire")
					));

			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		promptEnterKey();
    }
    
    /**
     *  Option 2 - Ajouter un produit
     *   
     */
    public static void ajouterProduit() { 
    	String ref;
		String nom;
		String marque;
		double prix;
		int qte;
		int seuil;
		int fournisseur;
		String categorie;

		Scanner scan = new Scanner(System.in);

		System.out.println("Référence: ");
		ref = scan.nextLine();
		System.out.println("nom: ");
		nom = scan.nextLine();
		System.out.println("marque: ");
		marque = scan.nextLine();
		System.out.println("prix unitaire: ");
		prix = scan.nextDouble();
		System.out.println("quantité en stock: ");
		qte = scan.nextInt();
		System.out.println("seuil de quantitée: ");
		seuil = scan.nextInt();
		System.out.println("id de fournisseur: ");
		fournisseur = scan.nextInt();
		System.out.println("Catégorie: ");
		categorie = scan.nextLine();
		categorie = scan.nextLine();

		try{
			PreparedStatement requete = connexion.prepareStatement("INSERT INTO produit " +
					"(ref_produit, nom_produit, marque, prix_unitaire, quantite_stock, quantite_seuil, code_fournisseur_prioritaire, nom_categorie) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

			requete.setString(1, ref);
			requete.setString(2, nom);
			requete.setString(3, marque);
			requete.setDouble(4, prix);
			requete.setInt(5, qte);
			requete.setInt(6, seuil);
			requete.setInt(7, fournisseur);
			requete.setString(8, categorie);

			if(requete.executeUpdate() > 0){
				System.out.println("Produit ajouté!");
			} else {
				System.out.println("Erreur d'insertion de données!");
			}


		} catch (SQLException e){
			System.out.println("Saisie erroné!");

		}

		promptEnterKey();
    }

    public static void promptEnterKey(){
		System.out.println("Appuyer sur \"ENTER\" pour continuer...");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}
 
    /**
     * Option 3 : Affiche la Commande et ses items 
     *  
     * @param numCommande : numéro de la commande à afficher 
     * 
     */
    public static void afficherCommande(int numCommande) { 
    	String nom;
		String prenom;
		String telephone;
		String date;
		String statut;
		double prix_total = 0;
		double sous_tot;
		int qte_cmd;
		double prix;

		String[] colones = new String[]{"Ref Produit", "Nom", "Marque", "Prix", "Q.Commandée", "Q.Stock", "T.Partiel"};
		String separation = "";
		for(String c: colones){
			separation += "-------------";
		}




		try{
			Statement requete = connexion.createStatement();

			ResultSet result = requete.executeQuery("SELECT " +
					"C.nom, C.prenom, C.telephone, O.date_commande, O.statut " +
					"FROM client C JOIN commande O ON O.no_client = C.no_client " +
					"WHERE O.no_commande = " + numCommande);
			result.next();

			nom = result.getString("nom");
			prenom = result.getString("prenom");
			telephone = result.getString("telephone");
			date = result.getString("date_commande");
			statut = result.getString("statut");


			result = requete.executeQuery("SELECT P.ref_produit, P.nom_produit, P.marque, P.prix_unitaire, C.quantite_cmd, P.quantite_stock " +
					"FROM produit P" +
					" JOIN commande_produit C ON C.no_produit = P.ref_produit" +
					" WHERE C.no_commande = " + numCommande);

			System.out.println("Client: " + prenom + " " + nom);
			System.out.println("Téléphone: " + telephone);
			System.out.println("No Commande: " + numCommande);
			System.out.println("Date: " + date.split(" ")[0]);
			System.out.println("Statut: " + statut);


			System.out.println(separation);
			System.out.println(String.format("%-13s%-13s%-13s%-13s%-13s%-13s%-13s", colones));
			System.out.println(separation);

			while (result.next()){
				prix = result.getDouble("prix_unitaire");
				qte_cmd = result.getInt("quantite_cmd");
				sous_tot = prix * qte_cmd;
				prix_total += sous_tot;

				System.out.println(String.format("%-13s%-13s%-13s%-13.2f%-13d%-13d%-13.2f",
						result.getString("ref_produit"),
						result.getString("nom_produit"),
						result.getString("marque"),
						result.getDouble("prix_unitaire"),
						result.getInt("quantite_cmd"),
						result.getInt("quantite_stock"),
						sous_tot
						));

			}
			System.out.println(separation);
			System.out.println("Montant Total: " + prix_total);

		} catch (SQLException e){
			e.printStackTrace();
		}	
    }   

    /**
     * Option 4 : Calcule le total des paiements effectués pour une facture
     *   
     * @param numFacture : numéro de la facture
     * @param affichage  : si false, la méthode ne fait aucun affichage ni arrêt
     * 
     */
    public static float calculerPaiements(int numFacture , boolean affichage) {
    	float resultat = -1 ;     	

    	// Ligne suivante à supprimer après implémentation
    	System.out.println("Option 4 : calculerPaiements() n'est pas implémentée");
    	
    	return resultat ; 
    }

    /** 
     * Option 5 -  Enregistrer un paiement 
     * Ajoute un paiement pour une facture 
     *  
     * @param numFacture : numéro de la facture pour laquelle est fait le paiement
     * 
     */
    public static void enregistrerPaiement(int numFacture) { 
    	// Ligne suivante à supprimer après implémentation
    	System.out.println("Option 5 : enregistrerPaiement() n'est pas implémentée");
    }

    /**
     * 
     *  
     */
    
    /**
     * Option 6 : enregistre une liste d'évalutions dans la BD. Les données d'une évaluation sont des objets 
     * 			   SatisfactionData. 
     * 
     * @param listEvaluation : tableau d'objet StatisfactionData, contient les données des évaluations 
     * 						   du client à insérer dans la BD
     */
    public static void enregistreEvaluation(SatisfactionData[] listEvaluation) {
    	// Ligne suivante à supprimer après implémentation
    	System.out.println("Option 6 : enregistreEvaluation() n'est pas implémentée");
    }

    /**
     * Question 9 - fermeture de la connexion   
     * @return
     */
    public static boolean fermetureConnexion() {
    	boolean resultat = false ;
    	// Ligne suivante à supprimer après implémentation
		try {
			connexion.close();
			resultat =true;
		} catch (SQLException e){
			e.printStackTrace();
		}
    	return resultat ; 
    }

    // ==============================================================================
    // NE PAS MODIFIER LE CODE QUI VA SUIVRE 
    // ==============================================================================    
    /**
     * Crée et retourne un tableau qui contient 5 évaluations de produits 
     * Chaque évaluation est stockée dans un objet de la classe SatisfactionData
     * 
     * @return un tableau d'objets SatisfactionData
     */
	public static SatisfactionData[] listSatisfactionData() {
			
		SatisfactionData[] list = new SatisfactionData[5]; 
		
		list[0] = new SatisfactionData(105 , "PC2000" , 4 , "PC très performant" ) ;
		list[1] = new SatisfactionData(105 , "LT2011" , 3 , "Produit satisfaisant, un peu bruyant" ) ;
		list[2] = new SatisfactionData(103 , "PC2000" , 5 , "Excellent ordinateur" ) ;
		list[3] = new SatisfactionData(101 , "DD2003" , 2 , "Performance moyenne du disque" ) ;
		list[4] = new SatisfactionData(104 , "SF3001" , 4 , "Je suis très satisfait de ma nouvelle version de l'OS" ) ;
		
		return list ;
	}
    /* ------------------------------------------------------------------------- */      
    /**
     * Affiche un menu pour le choix des opérations 
     * 
     */
    public static void afficheMenu(){
        System.out.println("0. Quitter le programme");
        System.out.println("1. Lister les produits");
        System.out.println("2. Ajouter un produit");
        System.out.println("3. Afficher une commande");
        System.out.println("4. Afficher le montant payé d'une facture");
        System.out.println("5. Enregistrer un paiement");
        System.out.println("6. Enregistrer les évaluations des clients");   
        System.out.println();
        System.out.println("Votre choix...");
    }
    
    
	/**
	 * La méthode main pour le lancement du programme 
	 * Il faut mettre les informations d'accès à la BDD  
	 * 
	 * @param args
	 */
	public static void main(String args[]){
		
		// Mettre les informations de votre compte sur SGBD Oracle 
		String username = "equipeXXX" ; 
		String motDePasse = "XXXXXXXX" ;
		
		String uri = "jdbc:oracle:thin:@tch054ora12c.logti.etsmtl.ca:1521:TCH054" ;   
		
		// Appel de le méthode pour établir la connexion avec le SGBD 
		connexion = connexionBDD(username , motDePasse , uri ) ;
		
		if (connexion != null) {
			
			System.out.println("Connection reussie...");
			
			// Affichage du menu pour le choix des opérations 
			afficheMenu(); 
             
			Scanner sc = new Scanner(System.in);
            String choix = sc.nextLine();
            
            while(!choix.equals("0")){
           	
                if(choix.equals("1")){
 
                    listerProduits() ; 
                    
                 }else if(choix.equals("2")){
 
                	 ajouterProduit() ; 
                                     
                 }else if(choix.equals("3")){
 
                    System.out.print("Veuillez saisir le numéro de la commande: ");
                    sc = new Scanner(System.in);
                    int numCommande = Integer.parseInt(sc.nextLine().trim()) ;              
                   
                    afficherCommande(numCommande) ; 
                    
                 }else if(choix.equals("4")){
                	
                	sc = new Scanner(System.in);
                	System.out.print("Veuillez saisir le numéro de la facture : ");
                	int numFacture = Integer.parseInt(sc.nextLine().trim()) ;                                  
                	calculerPaiements(numFacture , true) ; 
                                                                           
                 }else if(choix.equals("5")){
                	
                    
                	System.out.print("Veuillez saisir le numéro de la facture : ");
                	int numFacture = Integer.parseInt(sc.nextLine().trim()) ;      
                	sc = new Scanner(System.in);
                	enregistrerPaiement(numFacture) ; 

                 }else if(choix.equals("6")){                    
                	 enregistreEvaluation(listSatisfactionData());
                	 
                 }

                afficheMenu();
                sc = new Scanner(System.in);
                choix = sc.nextLine();
            	
            } // while 

            // FIn de la boucle While - Fermeture de la connexion 
            if(fermetureConnexion()){
                System.out.println("Deconnection reussie...");
                
            }else{
                System.out.println("Échec ou Erreur lors de le déconnexion...");
            }
            
		 } else {  // if (connexion != null) {
			 
			 System.out.println("Echec de la Connection. Au revoir ! ");
			 
		 } // if (connexion != null) {	        
	} // main() 
}


// =============================================================================================
/**
 * Contient les données d'une évaluation d'un produit  */
class SatisfactionData
{
	 int no_client ;
	String ref_produit ;
	int note ; 
	String commentaire ; 
	
	/**
	 * Constructeur
	 * 
	 * @param no_client
	 * @param ref_produit
	 * @param note
	 * @param commentaire
	 */
	public SatisfactionData(int no_client, String ref_produit, int note, String commentaire) {
		super();
		this.no_client = no_client;
		this.ref_produit = ref_produit;
		this.note = note;
		this.commentaire = commentaire;
	}	
}