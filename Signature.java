import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;
import java.util.Collections;

public class Signature {
    public Signature() {
    }

    public void createSignature(String fileName) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyException, MarshalException, XMLSignatureException, IOException, TransformerException, ParserConfigurationException, SAXException {

        // Creating a Public Key Pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // Instantiating the Document that Contains the Signature
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new FileInputStream("src/ressources/"+fileName));

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

        OutputStream os = new FileOutputStream("src/ressources/Signed"+fileName);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));

    }

    public boolean validateSignature(String fileName) throws Exception {
        // vérifiaction de la signature

        // Instantiating the Document that Contains the Signature
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder builder = dbf.newDocumentBuilder();
        // contient le xml qui doit etre signé
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
        return signature.validate(valContext);
    }
}
