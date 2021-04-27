import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public class Effacer {
    private Connection connection;
    public Effacer( Connection connection) {
        this.connection = connection ;
    }

    public void delete(String fileName) throws Exception {

        Signature signature = new Signature();
        boolean coreValidity = signature.validateSignature(fileName);

        if (coreValidity) {
            System.out.println("The signature validates successfully according to the core validation rules in the W3C XML Signature Recommendation");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc1 = dBuilder.parse(new File(fileName));
            //doc1.getDocumentElement().normalize();

            // doit contenir la balise select elemt node
            Node select = doc1.getDocumentElement();

            // si la balise racine est bien une balise insert alors on peut effectuer l'insertion
            if (select.getNodeName().equals("DELETE")) {
                // on recupere les noeuds en dessous de la balise insert
                NodeList nList = select.getChildNodes();
                int i;

                StringBuilder sql = new StringBuilder();

                // parcours de tout les fils de la racine
                for (i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        String baliseName = nNode.getNodeName();
                        switch (baliseName) {
                            case "TABLE":
                                sql.append("DELETE FROM " + nNode.getTextContent() + " WHERE ");
                                break;

                            case "CONDITION":
                                sql.append(nNode.getTextContent() + ";");
                                // on execute la requete
                                Statement statement = connection.createStatement();
                                int rows = statement.executeUpdate(sql.toString());

                                break;
                            default:
                        }

                    }
                }
            } else {
                throw new Exception("Le document xml ne possède pas de balise <DELETE> pour effacer");
            }
        }else{
            System.out.println("The signature doesn't validates according to the core validation rules in the W3C XML Signature Recommendation");
        }
    }
}
