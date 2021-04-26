import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Rechercher {
    private String xmlFile ;

    public Rechercher(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    public void research(String fileName) throws Exception {
        // liste qui contiendra la valeur de toutes les balises champ
        List<String> listChamp = new ArrayList<>();

        // liste qui contiendra la valeur de toutes les balises table
        List<String> listTable = new ArrayList<>();

        // contient notre condition (pas besoin de list car une seule balise condition par xml
        String cond ="";


        NodeList sousNoeud ;

        // vérifiaction de la signature

        // Instantiating the Document that Contains the Signature
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new FileInputStream(fileName));

        // Specifying the Signature Element to be Validated
        NodeList nl = doc.getElementsByTagNameNS
                (XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }

        // créer la classe keyValueSelector
        // Creating a Validation Context
        DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));

        // Unmarshaling the XML Signature

        XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");

        XMLSignature signature = factory.unmarshalXMLSignature(valContext);

        // Validating the XML Signature

        boolean coreValidity = signature.validate(valContext);

        if (coreValidity){
            System.out.println("the signature validates successfully according to the core validation rules in the W3C XML Signature Recommendation");


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc1 = dBuilder.parse(new File(fileName));
            //doc1.getDocumentElement().normalize();

            // doit contenir la balise select elemt node ou element ?
            Node select = doc1.getDocumentElement();

            // si la balise racine est bien une balise select alors on peut effectuer la recherche
            if(select.getNodeName().equals("SELECT")){
                // on recupere les noeuds en dessous de la balise select
                NodeList nList = select.getChildNodes();
                int i, j ;

                // parcours de tout les fils de la racine
                for(i = 0 ; i < nList.getLength() ; i ++){
                    Node nNode = nList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        String baliseName = nNode.getNodeName() ;
                        switch (baliseName){
                            case "CHAMPS" :
                                // on recupere les fils de la balise <CHAMPS>
                                sousNoeud = nNode.getChildNodes();

                                for(j = 0 ; j < sousNoeud.getLength() ; j ++){
                                    // recuperation de la balise <CHAMP>
                                    Node nNode2 = sousNoeud.item(j);
                                    if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                                        // on ajoute ce que contient la balise champ dans notre liste
                                        listChamp.add(nNode2.getTextContent());
                                    }
                                }
                                break;

                            case "TABLES":
                                    sousNoeud = nNode.getChildNodes();

                                    for(j = 0 ; j < sousNoeud.getLength() ; j ++){
                                        // recuperation de la balise <TABLE>
                                        Node nNode2 = sousNoeud.item(j);
                                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                                            // on ajoute ce que contient la balise champ dans notre liste
                                            listTable.add(nNode2.getTextContent());
                                        }
                                    }
                                break;

                            case "CONDITION":
                                 cond = nNode.getTextContent() ;
                                break;
                            default:
                        }

                    }
                }
                System.out.println("Contenu des listes : \n Champs : "+ listChamp.size());
                for(String c : listChamp){
                    System.out.println(c);
                }

                System.out.println("\n\n Table :"+ listTable.size());
                for(String t : listTable){
                    System.out.println(t);
                }

                System.out.println("Et la condition : "+cond);
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT ");
                String prefix = "";

                // on parcours les balises champ
                for(String c : listChamp){
                    sql.append(prefix);
                    prefix = ", ";
                    sql.append(c);
                }

                sql.append(" FROM ");
                prefix = "";

                // on parcours les balises table
                for(String t : listTable){
                    sql.append(prefix);
                    prefix = ", ";
                    sql.append(t);
                }

                sql.append(" WHERE ");

                if(!cond.equals("")){
                    sql.append(cond+" ;") ;
                }
                System.out.println("String sql a la fin = "+ sql.toString());

            }
            else{
                throw new Exception("Le document xml ne possède pas de balise <SELECT> pour la recherche");
            }










        }
        else{
            System.out.println("the signature doesn't validates according to the core validation rules in the W3C XML Signature Recommendation");
        }


    }
}