import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.sql.*;
import java.util.Collections;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        String selection = "R";
        Scanner input = new Scanner(System.in);

        /***************************************************/
        /*while(!selection.equals("Q")){
            System.out.println("Veuillez faire votre choix");
            System.out.println("-------------------------\n");
            System.out.println("(R)echercher");
            System.out.println("(I)nsérer");
            System.out.println("(M)ettre à jour");
            System.out.println("(E)ffacer");
            System.out.println("(Q)uitter");

            selection = input.next();

            System.out.println("Votre choix est : " + selection);
        }*/

        // signer rechercherTest.xml avant

        // Creating a Public Key Pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // Instantiating the Document that Contains the Signature
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new FileInputStream("src/ressources/rechercherTest.xml"));

        //Creating a Signing Context
        DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), doc.getDocumentElement());

        // Assembling the XML Signature
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA256, null),
                        Collections.singletonList
                                (fac.newTransform(Transform.ENVELOPED,
                                        (TransformParameterSpec) null)), null, null);

        SignedInfo si = fac.newSignedInfo
                (fac.newCanonicalizationMethod
                                (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                        (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod("http://www.w3.org/2009/xmldsig11#dsa-sha256", null),
                        Collections.singletonList(ref));

        KeyInfoFactory kif = fac.getKeyInfoFactory();

        KeyValue kv = kif.newKeyValue(kp.getPublic());
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

        XMLSignature signature = fac.newXMLSignature(si, ki);

        // Generating the XML Signature
        signature.sign(dsc);

        // Printing or Displaying the Resulting Document

        OutputStream os = new FileOutputStream("src/ressources/rechercherTestSigne.xml");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));



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

        Rechercher rechercher = new Rechercher("src/ressources/rechercherTest.xml", connection) ;
        rechercher.research("src/ressources/rechercherTestSigne.xml");

        Inserer inserer = new Inserer("src/ressources/insererTest.xml", connection);
        inserer.insert("src/ressources/insererTest.xml");
        //String sql = "INSERT INTO `contient` (`numcom`, `numplat`, `quantite`) VALUES ('1', '2', '3');" ;
        String sql = "INSERT INTO contient VALUES('111', '222', '113');" ;
        // creation d'un statement
        Statement statement = connection.createStatement();

        // retourne le nombre de n-uplets traités (pour insert, update, delete, create table, drop table)
        //int rows = statement.executeUpdate(sql) ;
        // pour select utiliser executeQuery


        String delete = "DELETE FROM `contient` WHERE `contient`.`numcom` = 1 AND `contient`.`numplat` = 1;";

        //statement.close() ;
        //connection.close();
    }
}
