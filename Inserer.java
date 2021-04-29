import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public class Inserer {
    private Connection connection ;
    public Inserer(Connection connection) {
        this.connection = connection;
    }

    public void insert(String fileName) throws Exception {
        NodeList sousNoeud ;

        Signature signature = new Signature();
        // true si la signature est valide
        boolean coreValidity = signature.validateSignature(fileName);

        if (coreValidity) {
            System.out.println("The signature validates successfully according to the core validation rules in the W3C XML Signature Recommendation");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc1 = dBuilder.parse(new File(fileName));

            // doit contenir la balise insert
            Node select = doc1.getDocumentElement();

            // si la balise racine est bien une balise insert alors on peut effectuer l'insertion
            if(select.getNodeName().equals("INSERT")) {
                // on recupere les noeuds en dessous de la balise insert
                NodeList nList = select.getChildNodes();
                int i, j;

                // contient le insert into nomTable values(
                StringBuilder part1 = new StringBuilder();

                // contient les differentes values
                StringBuilder part2 ;
                StringBuilder sql = new StringBuilder();

                // parcours de tout les fils de la racine
                for (i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        String baliseName = nNode.getNodeName();
                        switch (baliseName) {
                            // si on a une balise <TABLE>
                            case "TABLE":
                                // on recupere le nom de la table
                                part1.append("INSERT INTO "+ nNode.getTextContent()+" VALUES(");
                                break;

                            // si on a une balise <VALUES>
                            case "VALUES":
                                // on recupere les fils de la balise <VALUES>
                                sousNoeud = nNode.getChildNodes();
                                String virgule = "";
                                part2 = new StringBuilder();

                                // parcours de toutes les <VALUE>
                                for (j = 0; j < sousNoeud.getLength(); j++) {
                                    // recuperation de la balise <VALUE>
                                    Node nNode2 = sousNoeud.item(j);
                                    if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                                        // on ajoute la valeur a notre requete (le tout espacé par une virgule)
                                        part2.append(virgule);
                                        virgule = ", ";
                                        part2.append("'"+nNode2.getTextContent()+"'");
                                    }
                                }
                                // on ferme la parenthese de VALUES()
                                part2.append(");");
                                // on concatene les deux sous-chaine de charactere
                                sql.append(part1.toString() + part2.toString());

                                // on execute la requete
                                Statement statement = connection.createStatement();
                                int rows = statement.executeUpdate(sql.toString());

                                // on remets a 0 le string builder pour les cas ou il y a plusieurs insert dans ls xml
                                sql = new StringBuilder();
                                break;
                            default:
                        }

                    }
                }
            }
            else{
                throw new Exception("Le document xml ne possède pas de balise <INSERT> pour l'insertion");
            }

        }
        else{
            System.out.println("The signature doesn't validates according to the core validation rules in the W3C XML Signature Recommendation");
        }
    }
}
