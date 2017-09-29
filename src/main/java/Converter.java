import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

// import org.apache.commons.collections4.BidiMap;
// import org.apache.commons.collections4.bidimap.DualHashBidiMap;



public class Converter {

  public static HashMap<String,HashMap<String,Double>> nodeMap = new HashMap<String,HashMap<String,Double>>();
  public static HashMap<String,String> linkNodeID = new HashMap<String,String>();
  public static HashMap<String,String> linkInverseNodeID = new HashMap<String,String>();

  public static HashMap<String,String> linkEdgeID = new HashMap<String,String>();

  public static ArrayList<ArrayList<String>> tmpBuildingList = new ArrayList<ArrayList<String>>();
  public static ArrayList<ArrayList<String>> tmpHighwayList = new ArrayList<ArrayList<String>>();
  public static ArrayList<ArrayList<String>> tmpRoadList = new ArrayList<ArrayList<String>>();

  //String ID  ArrayList Node
  public static HashMap<String,ArrayList<String>> edgeMap = new HashMap<String,ArrayList<String>>();

  public static ArrayList<String> usedNodeList = new ArrayList<String>();
  public static HashMap<String,ArrayList<String>> buildingMap = new HashMap<String,ArrayList<String>>();
  public static HashMap<String,ArrayList<String>> roadMap = new HashMap<String,ArrayList<String>>();

  public static ArrayList<ArrayList<String>>  addedConnectRoadList = new ArrayList<ArrayList<String>>() ;
  public static HashMap<String,ArrayList<String>> minusDirectionEdgeMap = new HashMap<String,ArrayList<String>>();

//作成するファイル
  public static String fileName = "./XML/oosu.gml";
//読み込むファイル
  public static String fileLocation = "./OSM/oosu.osm";

//付与する名前空間
  public static String xmlns_rcr_namespace_uri="urn:roborescue:map:gml";
  public static String xmlns_gml_namespace_uri="http://www.opengis.net/gml";
  public static String xmlns_xlink_namespace_uri="http://www.w3.org/1999/xlink";


    public static void main(String args[]) throws Exception{

      //読み込み用Documentの作成
        MakeDocument makeDocument = new MakeDocument();
      //作成したDocumentにファイルの情報を読み込み
        Document readDocument = makeDocument.MakeReadDocument(fileLocation);
      //Documentから各種情報の取り出し
        ReadFile readFile = new ReadFile(readDocument);

        MakeRoad makeRoad = new MakeRoad();

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
