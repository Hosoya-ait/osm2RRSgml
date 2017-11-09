import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class OsmToGmlConverter {
    //nodeのOSMIDとx,yのmapを対応したmap x,yのmapはxとその値,yとその値の対応が入っている
    public static HashMap<String,HashMap<String,Double>> nodeMap = new HashMap<String,HashMap<String,Double>>();
    //nodeのOSMID NEWID(1~) の対応map
    public static HashMap<String,String> linkNodeID = new HashMap<String,String>();
    //nodeのNEWID OSMID の対応map
    public static HashMap<String,String> linkInverseNodeID = new HashMap<String,String>();

    // EdgeのNEWIDとGML書き込み時に使ったIDの対応 map
    public static HashMap<String,String> linkEdgeID = new HashMap<String,String>();

    //building highway Roadを構成するnodeのNEWIDリスト
    public static ArrayList<ArrayList<String>> tmpBuildingList = new ArrayList<ArrayList<String>>();
    public static ArrayList<ArrayList<String>> tmpHighwayList = new ArrayList<ArrayList<String>>();
    public static ArrayList<ArrayList<String>> tmpRoadList = new ArrayList<ArrayList<String>>();

    //edgeのNEWID(1~) とedgeを構成するnodeのNEWIDの対応map
    public static HashMap<String,ArrayList<String>> edgeMap = new HashMap<String,ArrayList<String>>();

    //edgeで使用したnodeを記憶する用のList
    public static ArrayList<String> usedNodeList = new ArrayList<String>();

    //road,building NEWID(~1)と使用しているEdgeのNEWIDのListの対応map
    public static HashMap<String,ArrayList<String>> buildingMap = new HashMap<String,ArrayList<String>>();
    public static HashMap<String,ArrayList<String>> roadMap = new HashMap<String,ArrayList<String>>();

    //road同士をつなげるときに作ったRoadのリスト（いらなさそう）
    public static ArrayList<ArrayList<String>>  addedConnectRoadList = new ArrayList<ArrayList<String>>() ;
    //face作成時にroad内でDirectionをマイナスにする必要があるedgeの対応map
    public static HashMap<String,ArrayList<String>> minusDirectionEdgeMap = new HashMap<String,ArrayList<String>>();

    //作成するファイル
    public static String fileName = "./GMLs/test.gml";
    //読み込むファイル
    public static String fileLocation = "./OSMs/test.osm";

    //付与する名前空間
    public static String xmlns_rcr_namespace_uri="urn:roborescue:map:gml";
    public static String xmlns_gml_namespace_uri="http://www.opengis.net/gml";
    public static String xmlns_xlink_namespace_uri="http://www.w3.org/1999/xlink";


    public static void main(String args[]) throws Exception{
        //データ管理用のManagerクラスのインスタンス生成
        NodeManager     nm = new NodeManager();
        EdgeManager     em = new EdgeManager();
        HighwayManager  hm = new HighwayManager();
        BuildingManager bm = new BuildingManager();
        RoadManager     rm = new RoadManager();

        //計算時間計測開始
        long start = System.currentTimeMillis();

        //読み込み用Documentの作成
        MakeDocument makeDocument = new MakeDocument();
        //作成したDocumentにファイルの情報を読み込み
        Document readDocument = makeDocument.MakeReadDocument(fileLocation);
        //Documentから各種情報の取り出し
        //ReadOsmFile readOsmFile = new ReadOsmFile(readDocument);

        ReadOsmFile readOsmFile1 = new ReadOsmFile(readDocument,nm,hm,bm);
        readOsmFile1.readosmFile();

        ExpansionHighway expansionHighway = new ExpansionHighway(nm,hm,rm);
        expansionHighway.ExpantionHighway();

        MakeEdge makeEdge = new MakeEdge(nm,em,bm,rm);
        makeEdge.MakeEdge();

        /* 森島が行数減らしてからリファクタリングする
        ConnectBuildingToRoad connectBuildingToRoad = new ConnectBuildingToRoad();

        connectBuildingToRoad.connect();
        */

        //書き込み用Documentの作成
        Document writeDoc = makeDocument.MakeWriteDocument();
        //書き込み用クラスの作成
        WriteDocument writeDocument = new WriteDocument(nm,em,bm,rm);
        writeDoc = writeDocument.WriteToDocument1(writeDoc);


        //Fileに書き込み
        WriteGmlFile writeGmlFile = new WriteGmlFile(writeDoc);


        //計算時間計測終了
        long end = System.currentTimeMillis();
        System.out.println("RunTime : " + (end - start)  + "ms");
        System.out.println("owata");
    }

}
