import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;


public class OsmToGmlConverter {

    //付与する名前空間
    public static String xmlns_rcr_namespace_uri="urn:roborescue:map:gml";
    public static String xmlns_gml_namespace_uri="http://www.opengis.net/gml";
    public static String xmlns_xlink_namespace_uri="http://www.w3.org/1999/xlink";


    public static void main(String args[]) throws Exception{
        mainRun(args[0]);
    }

    private static void mainRun(String readFileName){

        //作成するファイル
        String fileName = "./GMLs/"+readFileName+".gml";

        //読み込むファイルの場所
        String fileLocation = "./OSMs/"+readFileName+".osm";

        //データ管理用のManagerクラスのインスタンス生成
        NodeManager     nm = new NodeManager();
        EdgeManager     em = new EdgeManager();
        HighwayManager  hm = new HighwayManager();
        BuildingManager bm = new BuildingManager();
        RoadManager     rm = new RoadManager();

        //計算時間計測開始
        long start = System.currentTimeMillis();

        MakeDocument makeDocument =null;
        try{
            //読み込み用Documentの作成
            makeDocument = new MakeDocument();
        }catch(Exception e){
            System.out.println("ファイル作成時にエラー");
        }

        //作成したDocumentにファイルの情報を読み込み
        Document readDocument = null;

        try{
            readDocument = makeDocument.MakeReadDocument(fileLocation);
        }catch (Exception e) {
            System.out.println("読み込みファイル作成時にエラー");
        }

        //Documentから各種情報の取り出し
        //ReadOsmFile readOsmFile = new ReadOsmFile(readDocument);

        ReadOsmFile readOsmFile1 = new ReadOsmFile(readDocument,nm,hm,bm);
        readOsmFile1.readosmFile();

        // ExpansionHighway expansionHighway = new ExpansionHighway(nm,hm,rm);
        // expansionHighway.ExpantionHighway();
        ExpansionHighwaySimplePoint expansionHighway = new ExpansionHighwaySimplePoint(nm,hm,rm);
        expansionHighway.ExpantionHighway();

        AreaManager am = new AreaManager(nm,bm,rm);

        // 森島が行数減らしてからリファクタリングする
        //ConnectBuildingToRoad connectBuildingToRoad = new ConnectBuildingToRoad(nm,bm,rm,am);
        ConnectBuildingToRoadTest connectBuildingToRoad = new ConnectBuildingToRoadTest(nm,bm,rm,am);

        connectBuildingToRoad.connect();
        // nm = connectBuildingToRoad.getNodeManeger();
        // bm = connectBuildingToRoad.getBuildingManeger();
        // rm = connectBuildingToRoad.getRoadManeger();

        MakeEdge makeEdge = new MakeEdge(nm,em,bm,rm);
        makeEdge.makeNodeToEdge();

        Document writeDoc = null;
        try{
            //書き込み用Documentの作成
            writeDoc = makeDocument.MakeWriteDocument();
        }catch(Exception e){

            System.out.println("書き込みファイル作成時のエラー");
        }



        //書き込み用クラスの作成
        WriteDocument writeDocument = new WriteDocument(nm,em,bm,rm);
        writeDoc = writeDocument.WriteToDocument1(writeDoc);

        WriteGmlFile writeGmlFile = null;
        try{
            //Fileに書き込み
            writeGmlFile = new WriteGmlFile(writeDoc,fileName);
        }catch (Exception e) {
            System.out.println("gml作成時にエラー");
        }



        //計算時間計測終了
        long end = System.currentTimeMillis();
        System.out.println("RunTime : " + (end - start)  + "ms");
        System.out.println("owata");


        //GCさせるよう

        nm = null;
        em = null;
        hm = null;
        bm = null;
        rm = null;
        makeDocument =null;
        readDocument = null;
        readOsmFile1 = null;
        expansionHighway =null;
        am = null;
        connectBuildingToRoad = null;
        makeEdge = null;
        writeDoc = null;
        writeDocument =null;
        writeGmlFile = null;

    }

}
