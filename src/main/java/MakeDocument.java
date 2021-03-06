import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

//Documentを作るクラス
public class MakeDocument{

    //以下の処理は
    /*
        「テックコア」
        http://www.techscore.com/tech/Java/JavaSE/index/
        にあるDOMのチャプターを勉強することでわかる

        xml形式のファイルの読み込み・作成のテンプレな方法の感じである
     */

    //読み込み用のDocument作成
    public Document ReadDocument(String str) throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(str));

        return document;
    }

    //書き込み用のDocument作成
    public Document WriteDocument()throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        DOMImplementation domImpl = documentBuilder.getDOMImplementation();
        Document document = domImpl.createDocument(OsmToGmlConverter.xmlns_rcr_namespace_uri,"rcr:map",null);

        return document;
    }
}
