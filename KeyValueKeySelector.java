import javax.xml.crypto.*;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.List;

/**
 * The KeyValueKeySelector implementation tries to find an appropriate validation key using the data contained in KeyValue elements
 * of the KeyInfo element of an XMLSignature.
 */
public class KeyValueKeySelector extends KeySelector {

    public KeySelectorResult select(KeyInfo keyInfo,
                                    KeySelector.Purpose purpose,
                                    AlgorithmMethod method,
                                    XMLCryptoContext context)
            throws KeySelectorException {

        if (keyInfo == null) {
            throw new KeySelectorException("Null KeyInfo object!");
        }
        SignatureMethod sm = (SignatureMethod) method;
        List list = keyInfo.getContent();

        for (int i = 0; i < list.size(); i++) {
            XMLStructure xmlStructure = (XMLStructure) list.get(i);
            if (xmlStructure instanceof KeyValue) {
                PublicKey pk = null;
                try {
                    pk = ((KeyValue)xmlStructure).getPublicKey();
                } catch (KeyException ke) {
                    throw new KeySelectorException(ke);
                }
                // make sure algorithm is compatible with method
                if (algEquals(sm.getAlgorithm(),
                        pk.getAlgorithm())) {
                    return new SimpleKeySelectorResult(pk);
                }
            }
        }
        throw new KeySelectorException("No KeyValue element found!");
    }

    static boolean algEquals(String algURI, String algName) {
        if (algName.equalsIgnoreCase("DSA") &&
                algURI.equalsIgnoreCase("http://www.w3.org/2009/xmldsig11#dsa-sha256")) {
            return true;
        } else if (algName.equalsIgnoreCase("RSA") &&
                algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
            return true;
        } else {
            return false;
        }
    }


}