import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class Converter {

  public static HashMap<String,HashMap<String,Double>> nodeMap = new HashMap<String,HashMap<String,Double>>();
  public static HashMap<String,String> linkNodeID = new HashMap<String,String>();
  public static HashMap<String,ArrayList<String>> tmpWayMap = new HashMap<String,ArrayList<String>>();
  //String ID  ArrayList Node
  public static HashMap<String,ArrayList<String>> edgeMap = new HashMap<String,ArrayList<String>>();

//作成するファイル
  public static String fileName = "./XML/newXML.gml";
//読み込むファイル
  public static String fileLocation = "./OSM/hagi.osm";

//付与する名前空間
  public static String xmlns_rcr_namespace_uri="urn:roborescue:map:gml";
  public static String xmlns_gml_namespace_uri="http://www.opengis.net/gml";
  public static String xmlns_xlink_namespace_uri="http://jakarta.apache.org/struts/tags-html-1.0";


    public static void main(String args[]) throws Exception{

      //読み込み用Documentの作成
        MakeDocument makeDocument = new MakeDocument();
      //作成したDocumentにファイルの情報を読み込み
        Document readDocument = makeDocument.MakeReadDocument(fileLocation);
      //Documentから各種情報の取り出し
        ReadFile readFile = new ReadFile(readDocument);

        MakeEdge makeEdge = new MakeEdge();

      //書き込み用Documentの作成
        Document writeDoc = makeDocument.MakeWriteDocument();
      //書き込み用クラスの作成
        WriteDocument writeDocument = new WriteDocument();
        writeDoc = writeDocument.WriteToDocument(writeDoc);

      //Fileに書き込み
        WriteFile writeFile = new WriteFile(writeDoc);


    }

}
