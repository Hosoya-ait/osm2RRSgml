import java.util.HashMap;
import java.util.ArrayList;
import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

public class ReadOsmFile {

    //変数スコープの便宜上、wayに関するnode集合とkeyを保持する変数をここに記述。
    private static ArrayList nodeListOfWay = new ArrayList();
    private static String keyOfWay = new String();

    //基準点用変数
    private static double referenceLat = 0.0;
    private static double referenceLon = 0.0;

    //highwayのtag（道路のタイプ）を保持する。道路のタイプでフィルターをかける処理に渡すためにある
    private static CheckHighwayTag checkHighwayTag = new CheckHighwayTag();

    private CoordinateUtil coordinateUtil;
    private Document        document;
    private NodeManager     nm;
    private HighwayManager  hm;
    private BuildingManager bm;

    ReadOsmFile(Document document,NodeManager nm,HighwayManager hm,BuildingManager bm) {
        this.document  = document;
        this.nm        = nm;
        this.hm        = hm;
        this.bm        = bm;
        coordinateUtil = new CoordinateUtil();
    }

    public void readOsmFile () {

        /*
            [例：osmファイルの中身]
            <osm version="0.6" generator="JOSM">
                <bounds minlat="35.2144727" minlon="137.0305824" maxlat="35.2164361" maxlon="137.0349169" origin="CGImap 0.6.1 (21335 thorn-04.openstreetmap.org)"/>
                <node id="623987334" action="delete" timestamp="2015-11-10T15:18:18Z" uid="1240849" user="ediyes" version="5" changeset="35217661" lat="35.2137834" lon="137.0307367">
                    <tag k="crossing" v="traffic_signals"/>
                    <tag k="highway" v="traffic_signals"/>
                    <tag k="source" v="bing"/>
                </node>
                <node id="1143948939" action="delete" timestamp="2011-02-09T15:41:39Z" uid="146930" user="Tom_G3X" version="1" changeset="7237164" lat="35.2155864" lon="137.0283453"/>
                <node id="1143949016" action="delete" timestamp="2015-04-27T05:50:34Z" uid="2526485" user="tetsu1973" version="2" changeset="30526238" lat="35.2154334" lon="137.0277374"/>
                ・・・
                <way id="98883056" action="modify" timestamp="2015-04-30T08:05:51Z" uid="2526485" user="tetsu1973" version="3" changeset="30651088">
                    <nd ref="1895998337"/>
                    <nd ref="1895998315"/>
                    <nd ref="1895998312"/>
                    <nd ref="3488290379"/>
                    <nd ref="1895998256"/>
                    <nd ref="1895998234"/>
                    <nd ref="1895998211"/>
                    <tag k="highway" v="tertiary"/>
                    <tag k="source" v="bing"/>
                </way>
                <way id="344437076" timestamp="2015-05-12T06:48:35Z" uid="2526485" user="tetsu1973" version="1" changeset="31033522">
                    <nd ref="3511817074"/>
                    <nd ref="3511817075"/>
                    <nd ref="3511817094"/>
                    <nd ref="3511817092"/>
                    <nd ref="3511817074"/>
                    <tag k="building" v="retail"/>
                    <tag k="source" v="bing"/>
                </way>
                <way > ・・・
            </osm>
         */

        //rootのnode名がosmだから変数名もosm
        Node osm = document.getDocumentElement();
        //<bounds>と<node>と<way>を扱う変数だからboundsNodeWay
        Node boundsNodeWay = osm.getFirstChild();

        //要素名でnodeかwayを発見したら処理
        while(boundsNodeWay != null) {
            String nodeName = boundsNodeWay.getNodeName();

            switch (nodeName) {

                /*
                    ここにboundsの中にあるminlatとかの値を取っとくことでAreaManagerでの範囲ぎめの全探索を省略できる
                    case "bounds":
                 */


                case "node":
                    //nodeの緯度経度を平面座標(x,y)へ変換し、nmへ登録する処理
                    setNodeCoordinate(boundsNodeWay);
                    break;

                case "way":
                    nodeListOfWay = new ArrayList();
                    keyOfWay = new String();
                    checkHighwayTag.clearCheckList();

                    //<nd>と<tag>を扱う変数だからndTag
                    Node ndTag = boundsNodeWay.getFirstChild();
                    //wayを構成しているnode集合をnodeListOfWayへ、
                    while(ndTag != null) {
                        setWayInfo(ndTag);
                        ndTag = ndTag.getNextSibling();
                    }

                    //直前のgetWayInfoにてそれぞれのタグ情報に対応するNodeの集合がnodeListOfWayに入っている。
                    //NodeListはOSMの管理しているNode集合であるから，始めのNodeが最後にも重複している。
                    switch (keyOfWay) {
                        case "building":
                            if (nodeListOfWay.size() > 2) {
                                //建物の方向を反時計回りにする処理
                                nodeListOfWay = unifyPositiveDirection(nodeListOfWay);
                                bm.setBuildingNodeList(nodeListOfWay);
                            }
                            break;

                        case "highway":
                            if (nodeListOfWay.size() > 1) {
                                if (checkHighwayTag.check()) {
                                    hm.setTmpHighwayList(nodeListOfWay);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                    break;
            }
            boundsNodeWay = boundsNodeWay.getNextSibling();
        }
    }

    //nodeの緯度経度を平面座標(x,y)へ変換し、nmへ登録
    private void setNodeCoordinate(Node node) {
        NamedNodeMap attributes = node.getAttributes();

        if (attributes!=null) {
            HashMap<String,Double> nodeCoordinate = new HashMap<String,Double>();

            Node attributeId = attributes.getNamedItem("id");
            Node attributeLat = attributes.getNamedItem("lat");
            Node attributeLon = attributes.getNamedItem("lon");

            Double lat = Double.parseDouble(attributeLat.getNodeValue());
            Double lon = Double.parseDouble(attributeLon.getNodeValue());


            // lat =lat/0.000008999229891;
            // lon =lon/0.00001;

            if (referenceLon == 0.0) {
                referenceLon = lon;
            }
            if(referenceLat == 0.0){
                referenceLat = lat;
            }

            //最初のnodeを基準点(0,0)として扱う? 2018/07/27
            lat = lat - referenceLat;
            lon = lon - referenceLon;
            // map.put("y",lat);
            // map.put("x",lon);

            //平面直角座標系と緯度経度を相互変換
            Point2D.Double point = coordinateUtil.convertLonLat2XY(lon,lat,0,0);

            nodeCoordinate.put("y",point.getY());
            nodeCoordinate.put("x",point.getX());

            String osmNodeId = attributeId.getNodeValue();
            nm.addGmlNode(osmNodeId,nodeCoordinate);
        }
    }

    //<way>の持つnode集合の保持とtag情報の保持をする関数
    //また、highwayのタイプをチェックする処理のためにタイプの保持もおこなう
    private void setWayInfo(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        switch (node.getNodeName()) {
            case "nd":
                Node attributeRef = attributes.getNamedItem("ref");
                nodeListOfWay.add(nm.getGmlID(attributeRef.getNodeValue()));
                break;

            case "tag":
                Node attributeK = attributes.getNamedItem("k");

                switch (attributeK.getNodeValue()) {

                    case "highway":
                        Node attributeV = attributes.getNamedItem("v");
                        checkHighwayTag.setCheckList(attributeV.getNodeValue());
                        //breakがないため、以下のbuildingのkeyOfWayの代入式も実行される点に注意

                    case "building":
                        keyOfWay = attributeK.getNodeValue();
                        break;

                    case "area":
                        checkHighwayTag.setCheckList("area");
                        break;


                    default:
                        checkHighwayTag.setCheckList(attributeK.getNodeValue());
                        break;
                }

                break;
        }
    }

    //建物のnode集合の順番を反時計回りに統一する関数
    /*
        トレースしてみるとよさそうなものだが、数学的な手法もあるためそちらを利用したほうが確実
        いまのままだと若干もれがあっても文句が言えない方法になっている 2018/07/30

     */
    private ArrayList<String> unifyPositiveDirection(ArrayList<String> nodeListOfWay){

        double sum_sita = 0.0;

        for (int i = 0; i<nodeListOfWay.size()-2;i++ ) {

            String point_A = (String)nodeListOfWay.get(i+0);
            String point_B = (String)nodeListOfWay.get(i+1);
            String point_C = (String)nodeListOfWay.get(i+2);

            double dif_BA_X = nm.getX(point_A)-nm.getX(point_B);
            double dif_BA_Y = nm.getY(point_A)-nm.getY(point_B);
            double dif_BC_X = nm.getX(point_C)-nm.getX(point_B);
            double dif_BC_Y = nm.getY(point_C)-nm.getY(point_B);

            double degree_BA = Math.atan2(dif_BA_Y,dif_BA_X)* 180.0 / Math.PI;
            double degree_BC = Math.atan2(dif_BC_Y,dif_BC_X)* 180.0 / Math.PI;

            double degree = degree_BC-degree_BA;

            if (degree < -180) {
                degree+=360;
            }else if (degree > 180) {
                degree-=360;
            }

            double sita = degree*Math.PI/180.0;

            sum_sita += sita;
        }

        // if(sum_sita>0){
        //     System.out.println("別の回転方向チェック処理");
        //     sum_sita = anotherCheckDirection(nodeListOfWay);
        // }

        //sum_sita = anotherCheckDirection(nodeListOfWay);

        if (sum_sita < 0) {
            //逆向き処理
            System.out.println("逆向き処理");
            ArrayList reverseNodeListOfWay = new ArrayList();
            for (int i=nodeListOfWay.size()-1; i>=0;i-- ) {
                reverseNodeListOfWay.add(nodeListOfWay.get(i));
            }
            return reverseNodeListOfWay;
        }

        return nodeListOfWay;
    }



    private double anotherCheckDirection(ArrayList<String> tmp_List){


        ArrayList<Double> degreeList = new ArrayList<Double>();

        double x_sum = 0.0;
        double y_sum = 0.0;
        for(int i=0;i<tmp_List.size()-1;i++){
            x_sum += nm.getX(tmp_List.get(i));
            y_sum += nm.getY(tmp_List.get(i));
        }
        double x_ave = x_sum/((double)tmp_List.size());
        double y_ave = y_sum/((double)tmp_List.size());

        Point2D.Double centerPoint = new Point2D.Double(x_ave,y_ave);

        double sita_sum = 0.0;
        for(int i=0;i<tmp_List.size()-1;i++){
            Point2D.Double tmpPoint = new Point2D.Double(nm.getX(tmp_List.get(i)),nm.getY(tmp_List.get(i)));
            double x_def = tmpPoint.getX()-centerPoint.getX();
            double y_def = tmpPoint.getY()-centerPoint.getY();
            Point2D.Double defPoint = new Point2D.Double(x_def,y_def);
            double sita = Math.atan2(defPoint.getY(),defPoint.getX());
            double degree = sita* 180.0 / Math.PI;
            if(degree < 0.0) degree += 360.0;
            degreeList.add(degree);
        }
        int plus_counter = 0;
        int minus_counter = 0;

        double beforeDegree = degreeList.get(degreeList.size()-1);
        double afterDegree;
        for(int i=0;i<degreeList.size();i++){
            afterDegree = degreeList.get(i);
            if (afterDegree<beforeDegree) {
                minus_counter++;
            }else{
                plus_counter++;
            }
            beforeDegree = afterDegree;
        }


        if(plus_counter<minus_counter){
            double sita = 1.0;
            return sita;
        }else{
            double sita = -1.0;
            return sita;
        }
    }
}
