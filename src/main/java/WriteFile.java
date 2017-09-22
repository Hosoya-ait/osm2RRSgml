import org.w3c.dom.Document;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

import java.io.File;
import java.io.FileOutputStream;

public class WriteFile {
  WriteFile(Document document) throws Exception{
    TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();

        transformer.setOutputProperty("indent", "yes"); //改行指定
        transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");//インデントの桁指定
        transformer.setOutputProperty("method", "xml");


        DOMSource source = new DOMSource(document);
        File newXML = new File(Converter.fileName);
        FileOutputStream os = new FileOutputStream(newXML);
        StreamResult result = new StreamResult(os);

        transformer.transform(source, result);
  }
}
