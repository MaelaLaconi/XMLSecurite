import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;

public class Rechercher {
    private String xmlFile ;

    public Rechercher(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    public void research(String fileName) throws Exception {
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
        }
        else{
            System.out.println("the signature doesn't validates according to the core validation rules in the W3C XML Signature Recommendation");
        }


    }
}