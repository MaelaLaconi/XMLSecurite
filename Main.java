import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {






        // chargement de la classe du driver en memoire
        Class.forName("com.mysql.jdbc.Driver");

        // URL de la base de données
        String DBurl = "jdbc:mysql://localhost:3306/restaurant";
        // nom de l'utilisateur de la base
        String username = "root" ;
        // son mot de passe
        String password = "" ;

        // connection a la base
        Connection connection = DriverManager.getConnection(DBurl, username, password) ;



        String selection = "R";
        Scanner input = new Scanner(System.in);

        String fileName = "";

        /***************************************************/
        while(!selection.equals("Q")){
            System.out.println("Veuillez faire votre choix");
            System.out.println("-------------------------\n");
            System.out.println("(R)echercher");
            System.out.println("(I)nsérer");
            System.out.println("(M)ettre à jour");
            System.out.println("(E)ffacer");
            System.out.println("(Q)uitter");

            selection = input.next();
            if(selection.equals("R")){
                Scanner input2 = new Scanner(System.in);
                System.out.println("Veuillez indiquer le fichier xml à charger pour la recherche :");
                fileName = input2.next();
                Rechercher rechercher = new Rechercher(connection) ;
                rechercher.research("src/ressources/"+fileName);
            }

            if(selection.equals("I")){
                Scanner input2 = new Scanner(System.in);
                System.out.println("Veuillez indiquer le fichier xml à charger pour l'insertion :");
                fileName = input2.next();
                Inserer inserer = new Inserer(connection);
                inserer.insert("src/ressources/"+fileName);
            }

            if(selection.equals("M")){
                Scanner input2 = new Scanner(System.in);
                System.out.println("Veuillez indiquer le fichier xml à charger pour la mise à jour :");
                fileName = input2.next();
                Maj maj = new Maj(connection);
                maj.update("src/ressources/"+fileName);
            }

            if(selection.equals("E")){
                Scanner input2 = new Scanner(System.in);
                System.out.println("Veuillez indiquer le fichier xml à charger pour effacer :");
                fileName = input2.next();
                Effacer effacer = new Effacer(connection);
                effacer.delete("src/ressources/"+fileName);
            }
        }

        Signature signature = new Signature();
        //signature.createSignature("rechercherTest.xml");

        Rechercher rechercher = new Rechercher(connection) ;
        //rechercher.research("src/ressources/SignedrechercherTest.xml");

        /*Inserer inserer = new Inserer(connection);
        inserer.insert("src/ressources/SignedinsererTest.xml");*/

        /*Effacer effacer = new Effacer(connection);
        effacer.delete("src/ressources/SignedeffacerTest.xml");*/

        /*Maj maj = new Maj(connection);
        maj.update("src/ressources/SignedmajTest.xml");*/

        // creation d'un statement

        // retourne le nombre de n-uplets traités (pour insert, update, delete, create table, drop table)
        //int rows = statement.executeUpdate(sql) ;
        // pour select utiliser executeQuery



        connection.close();
    }
}
