import java.util.HashMap;
import java.util.ArrayList;
import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

public class ReadOsmFile {

    private static ArrayList tmpNodeList = new ArrayList();
    private static String tmpKey = new String();
    //基準点用変数
    private static double referenceLat = 0.0;
    private static double referenceLon = 0.0;

    private static CheckHighwayTag checkHighway = new CheckHighwayTag();

    private Document  document;
    private NodeManager     nm = new NodeManager();
    private HighwayManager  hm = new HighwayManager();
    private BuildingManager bm = new BuildingManager();

    private CoordinateUtil coordinateUtil;
    ReadOsmFile(Document document,NodeManager nm,HighwayManager hm,BuildingManager bm) {
        this.document = document;
        this.nm = nm;
        this.hm = hm;
        this.bm = bm;
        coordinateUtil = new CoordinateUtil();
    }

    public void readosmFile () {
        Node osmNode = document.getDocumentElement();
        Node elementNodes = osmNode.getFirstChild();

        //Document内のnode way relationを発見したら処理に入る
        while(elementNodes != null) {
            String elementNodesNodeName = elementNodes.getNodeName();

            switch (elementNodesNodeName) {
                case "node":
                    setNodeMap(elementNodes);
                    break;

                case "way":

                    /*
                        osmのwayの構造
                        <way id='32170762' timestamp='2010-03-04T16:20:06Z' uid='146930' user='Tom_G3X' version='4' changeset='4033941'>
                            <nd ref='361018947' />
                            <nd ~>
                            ...
                            <tag k='KSJ2:INT_label' v='公営鉄道' />
                            ...
                            <tag k='railway' v='subway' />
                        </way>

                     */

                    tmpNodeList = new ArrayList();
                    tmpKey = new String();
                    checkHighway.clearCheckList();

                    Node itemNodes = elementNodes.getFirstChild();
                    while(itemNodes != null) {
                        getWayInfo(itemNodes);
                        itemNodes = itemNodes.getNextSibling();
                    }

                    //直前のgetWayInfoにてそれぞれのタグ情報に対応するNodeの集合がtmpNodeListに入っている。
                    //NodeListはOSMの管理しているNode集合であるから，始めのNodeが最後にも重複している。
                    switch (tmpKey) {
                        case "building":
                            if (tmpNodeList.size() > 2) {
                                tmpNodeList = checkDirection(tmpNodeList);
                                bm.setBuildingNodeList(tmpNodeList);
                            }
                            break;

                        case "highway":
                            if (tmpNodeList.size() > 1) {
                                if (checkHighway.checkList()) {
                                    hm.setTmpHighwayList(tmpNodeList);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                break;
            }
            elementNodes = elementNodes.getNextSibling();
        }
    }

    private void setNodeMap(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes!=null) {
            HashMap<String,Double> map = new HashMap<String,Double>();

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

            lat = lat - referenceLat;
            lon = lon - referenceLon;
            // map.put("y",lat);
            // map.put("x",lon);

            Point2D.Double point = coordinateUtil.convertLonLat2XY(lon,lat,0,0);

            map.put("y",point.getY());
            map.put("x",point.getX());

            nm.addGmlNode(attributeId.getNodeValue(),map);
        }
    }

    private void getWayInfo(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        switch (node.getNodeName()) {
            case "nd":
                Node attributeRef = attributes.getNamedItem("ref");
                tmpNodeList.add(nm.getGmlID(attributeRef.getNodeValue()));
                break;

            case "tag":
                Node attributeK = attributes.getNamedItem("k");

                switch (attributeK.getNodeValue()) {

                    case "highway":
                        Node attributeV = attributes.getNamedItem("v");
                        checkHighway.setCheckList(attributeV.getNodeValue());

                    case "building":
                        tmpKey = attributeK.getNodeValue();
                        break;

                    case "area":
                        checkHighway.setCheckList("area");
                        break;


                    default:
                        checkHighway.setCheckList(attributeK.getNodeValue());
                        break;
                }

                break;
        }
    }

    //List内のNodeの順番を時計回りに調整
    //注意：ここでは扱っていないが、道路のNodeは逆時計回りに管理している。
    private ArrayList<String> checkDirection(ArrayList<String> tmp_List){

        double sum_sita = 0.0;
        double sum_degree = 0.0;

        for (int i = 0; i<tmp_List.size()-2;i++ ) {

            String point_A = (String)tmp_List.get(i+0);
            String point_B = (String)tmp_List.get(i+1);
            String point_C = (String)tmp_List.get(i+2);

            double dif_BA_X = nm.getX(point_A)-nm.getX(point_B);
            double dif_BA_Y = nm.getY(point_A)-nm.getY(point_B);
            double dif_BC_X = nm.getX(point_C)-nm.getX(point_B);
            double dif_BC_Y = nm.getY(point_C)-nm.getY(point_B);

            double length_BA = Math.sqrt((dif_BA_X*dif_BA_X)+(dif_BA_Y*dif_BA_Y));
            double length_BC = Math.sqrt((dif_BC_X*dif_BC_X)+(dif_BC_Y*dif_BC_Y));
            //
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
            sum_degree += degree;
        }

        // if(sum_sita>0){
        //     System.out.println("別の回転方向チェック処理");
        //     sum_sita = anotherCheckDirection(tmp_List);
        // }

        //sum_sita = anotherCheckDirection(tmp_List);

        if (sum_sita < 0) {
            //逆向き処理
            System.out.println("逆向き処理");
            ArrayList counter_tmp_List = new ArrayList();
            for (int i=tmp_List.size()-1; i>=0;i-- ) {
                counter_tmp_List.add(tmp_List.get(i));
            }
            return counter_tmp_List;
        }

        return tmp_List;
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
