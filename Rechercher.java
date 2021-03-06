import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Rechercher {
    private Connection connection ;

    public Rechercher(Connection con) {
        this.connection = con ;
    }

    public void research(String fileName) throws Exception {

        // liste qui contiendra la valeur de toutes les balises champ
        List<String> listChamp = new ArrayList<>();

        // liste qui contiendra la valeur de toutes les balises table
        List<String> listTable = new ArrayList<>();

        // contient notre condition (pas besoin de list car une seule balise condition par xml select)
        String cond ="";


        NodeList sousNoeud ;

        Signature signature = new Signature();

        // true si la signature est validé
        boolean coreValidity = signature.validateSignature(fileName);

        if (coreValidity){
            System.out.println("The signature validates successfully according to the core validation rules in the W3C XML Signature Recommendation");


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc1 = dBuilder.parse(new File(fileName));

            // doit contenir la balise select
            Node select = doc1.getDocumentElement();

            // si la balise racine est bien une balise select alors on peut effectuer la recherche
            if(select.getNodeName().equals("SELECT")) {
                // on recupere les noeuds en dessous de la balise select
                NodeList nList = select.getChildNodes();
                int i, j;

                // parcours de tout les fils de la racine
                for (i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        String baliseName = nNode.getNodeName();
                        switch (baliseName) {
                            // si on a une balise <CHAMPS>
                            case "CHAMPS":
                                // on recupere les fils de la balise <CHAMPS>
                                sousNoeud = nNode.getChildNodes();

                                // parcours des balises <CHAMP>
                                for (j = 0; j < sousNoeud.getLength(); j++) {
                                    // recuperation de la balise <CHAMP>
                                    Node nNode2 = sousNoeud.item(j);
                                    if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                                        // on ajoute ce que contient la balise champ dans notre liste
                                        listChamp.add(nNode2.getTextContent());
                                    }
                                }
                                break;

                            // si on a une balise <TABLES>
                            case "TABLES":
                                sousNoeud = nNode.getChildNodes();
                                // on parcours les balises <TABLE>
                                for (j = 0; j < sousNoeud.getLength(); j++) {
                                    // recuperation de la balise <TABLE>
                                    Node nNode2 = sousNoeud.item(j);
                                    if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                                        // on ajoute ce que contient la balise tables dans notre liste
                                        listTable.add(nNode2.getTextContent());
                                    }
                                }
                                break;

                            // si on a une balise <CONDITION>
                            case "CONDITION":
                                // on recupere le String de la condition
                                cond = nNode.getTextContent();
                                break;
                            default:
                        }

                    }
                }

                // contient notre requete
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT ");
                String prefix = "";

                // on parcours les balises champ qu'on ajoute dans notre requete
                // les champs sont espacés par une virgule
                for (String c : listChamp) {
                    sql.append(prefix);
                    prefix = ", ";
                    sql.append(c);
                }

                sql.append(" FROM ");
                prefix = "";

                // on parcours les balises table que l'on ajoute dans notre requete
                // les tables sont espacées par une virgule
                for (String t : listTable) {
                    sql.append(prefix);
                    prefix = ", ";
                    sql.append(t);
                }

                sql.append(" WHERE ");
                //on ajoute la condition si elle n'est pas null
                if (!cond.equals("")) {
                    sql.append(cond + " ;");
                }

                // on execute la requete
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql.toString());


                // on utilise ResultSetMetaData pour recup le type de données que l'on vient de récupérer
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                // contient le xml qui va etre généré
                StringBuilder sb = new StringBuilder();
                sb.append("<RESULTAT>\n    <TUPLES>\n");
                int nbColonnes = resultSetMetaData.getColumnCount();

                // parcours du resultSet
                while (resultSet.next()) {
                    sb.append("        <TUPLE>\n");
                    for (i = 1; i <= nbColonnes; i++) {
                        // value de la balise
                        String col = resultSet.getString(i);
                        // nom de la balisse
                        String nomColonne = resultSetMetaData.getColumnName(i);
                        sb.append("            <"+ nomColonne+">"+col+"</"+nomColonne+">\n");
                    }
                    sb.append("        </TUPLE>\n");
                }
                sb.append("    </TUPLES>\n</RESULTAT>");

                // contient le resultat
                File file = new File("src/ressources/RechercheRes.xml");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(sb.toString());
                fileWriter.close();

                // le xml signé sera dans src/ressources/RechercheRes.xml
                signature.createSignature("RechercheRes.xml");

                System.out.println("Le resultat de la recherche se trouve dans src/ressources/RechercheRes.xml\n\n");
            }
            else{
                throw new Exception("Le document xml ne possède pas de balise <SELECT> pour la recherche");
            }

        }
        else{
            System.out.println("The signature doesn't validates according to the core validation rules in the W3C XML Signature Recommendation");
        }
    }
}