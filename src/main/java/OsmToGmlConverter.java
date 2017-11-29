import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class OsmToGmlConverter {

    //作成するファイル
    public static String fileName = "./GMLs/ootakuSmall1.gml";
    //読み込むファイル
    public static String fileLocation = "./OSMs/ootakuSmall1.osm";

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



        // 森島が行数減らしてからリファクタリングする
        ConnectBuildingToRoad connectBuildingToRoad = new ConnectBuildingToRoad(nm,bm,rm);
        connectBuildingToRoad.connect();
        // nm = connectBuildingToRoad.getNodeManeger();
        // bm = connectBuildingToRoad.getBuildingManeger();
        // rm = connectBuildingToRoad.getRoadManeger();

        MakeEdge makeEdge = new MakeEdge(nm,em,bm,rm);
        makeEdge.makeNodeToEdge();

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
