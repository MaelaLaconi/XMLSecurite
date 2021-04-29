import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public class Maj {
    private Connection connection;

    public Maj(Connection connection) {
        this.connection = connection ;
    }

    public void update(String fileName) throws Exception {

        Signature signature = new Signature();

        // true si la signature est valide
        boolean coreValidity = signature.validateSignature(fileName);

        if (coreValidity) {
            System.out.println("The signature validates successfully according to the core validation rules in the W3C XML Signature Recommendation");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc1 = dBuilder.parse(new File(fileName));

            // doit contenir la balise update
            Node select = doc1.getDocumentElement();

            // si la balise racine est bien une balise update alors on peut effectuer la mise à jour
            if (select.getNodeName().equals("UPDATE")) {
                // on recupere les noeuds en dessous de la balise update
                NodeList nList = select.getChildNodes();
                int i;

                StringBuilder sql = new StringBuilder();

                // parcours de tout les fils de la racine
                for (i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        String baliseName = nNode.getNodeName();
                        switch (baliseName) {
                            // si on a une balise <TABLE>
                            case "TABLE":
                                // on recupere la table sur laquelle effectuer la requete
                                sql.append("UPDATE " + nNode.getTextContent() + " SET ");
                                break;

                            // si on a une balise <CHAMP>
                            case "CHAMP":
                                // on recupere le champs que l'on veut mettre à jour
                                sql.append(nNode.getTextContent() + " = ");
                                break;

                            // si on a une balise <VALUE>
                            case "VALUE":
                                // on recupere la nouvelle valeur pour mettre a jour
                                sql.append("'"+nNode.getTextContent()+"'");
                                break;

                            // si on a une balise <CONDITION>
                            case "CONDITION":
                                // on recupere la condition a appliquer pour la requete
                                sql.append(" WHERE " + nNode.getTextContent() + ";");
                                // on execute la requete
                                Statement statement = connection.createStatement();
                                int rows = statement.executeUpdate(sql.toString());
                                break;
                            default:
                        }

                    }
                }
            } else {
                throw new Exception("Le document xml ne possède pas de balise <UPDATE> pour la maj");
            }
        }else{
            System.out.println("The signature doesn't validates according to the core validation rules in the W3C XML Signature Recommendation");
        }
    }
}
