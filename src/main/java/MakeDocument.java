import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

//Documentを作るクラス
public class MakeDocument{

    //読み込み用のDocument作成
    public Document MakeReadDocument(String str) throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(str));

        return document;
    }

    //書き込み用のDocument作成
    public Document MakeWriteDocument()throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        Document document = domImpl.createDocument(Converter.xmlns_rcr_namespace_uri,"rcr:map",null);

        return document;
    }
}
