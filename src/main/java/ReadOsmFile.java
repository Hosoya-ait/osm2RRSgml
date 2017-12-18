import java.util.HashMap;
import java.util.ArrayList;

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


    ReadOsmFile(Document        document,
                NodeManager     nm,
                HighwayManager  hm,
                BuildingManager bm) {
        this.document = document;
        this.nm = nm;
        this.hm = hm;
        this.bm = bm;

    }

    public void readosmFile () {
        Node osmNode = document.getDocumentElement();
        Node elementNodes = osmNode.getFirstChild();


        //Document内のnode way relationを発見したら処理に入る
        while(elementNodes != null) {
            String elementNodesNodeName = elementNodes.getNodeName();
            //relation の場所までいったら終了させる
            // if (elementNodesNodeName == "relation") {
            //     break;
            // }

            switch (elementNodesNodeName) {
                case "node":
                    setNodeMap(elementNodes,this.nm);
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
                        getWayInfo(itemNodes,this.nm);
                        itemNodes = itemNodes.getNextSibling();
                    }

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

    private static void setNodeMap(Node node,NodeManager nm) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes!=null) {
            HashMap<String,Double> map = new HashMap<String,Double>();

            Node attributeId = attributes.getNamedItem("id");
            Node attributeLat = attributes.getNamedItem("lat");
            Node attributeLon = attributes.getNamedItem("lon");

            Double lat = Double.parseDouble(attributeLat.getNodeValue());
            Double lon = Double.parseDouble(attributeLon.getNodeValue());

            lat =lat/0.000009;
            lon =lon/0.000009;

            if (referenceLon == 0.0) {
                referenceLon = lon;
            }
            if(referenceLat == 0.0){
                referenceLat = lat;
            }

            lat = lat - referenceLat;
            lon = lon - referenceLon;

            map.put("y",lat);
            map.put("x",lon);


            nm.addGmlNode(attributeId.getNodeValue(),map);
            //System.out.print(attributeId.getNodeValue());
            //System.out.println(" ," + nm.getGmlID(attributeId.getNodeValue()));
        }
    }

    private static void getWayInfo(Node node,NodeManager nm) {
        NamedNodeMap attributes = node.getAttributes();
        switch (node.getNodeName()) {
            case "nd":
                Node attributeRef = attributes.getNamedItem("ref");
                //tmpNodeList.add(OsmToGmlConverter.linkNodeID.get(attributeRef.getNodeValue()));
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
                        //System.out.println(tmpKey);
                        break;

                    case "area":
                        checkHighway.setCheckList("area");
                        break;


                    default:
                        //System.out.println("tag:"+attributeK.getNodeValue());
                        checkHighway.setCheckList(attributeK.getNodeValue());
                        break;
                }
                break;

        }
    }


    private ArrayList checkDirection(ArrayList tmp_List){
//        System.out.println();

        String point_A = new String();
        String point_B = new String();
        String point_C = new String();

        HashMap<String,Double> map_A = new HashMap<String,Double>();
        HashMap<String,Double> map_B = new HashMap<String,Double>();
        HashMap<String,Double> map_C = new HashMap<String,Double>();

        double sum_sita = 0.0;
        double sum_degree = 0.0;

        for (int i = 0; i<tmp_List.size()-2;i++ ) {

            point_A = (String)tmp_List.get(i+0);
            point_B = (String)tmp_List.get(i+1);
            point_C = (String)tmp_List.get(i+2);

//            point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i+0));
//            point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i+1));
//            point_C = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i+2));

//            map_A = OsmToGmlConverter.nodeMap.get(point_A);
//            map_B = OsmToGmlConverter.nodeMap.get(point_B);
//            map_C = OsmToGmlConverter.nodeMap.get(point_C);

//            double dif_BA_X = map_A.get("x")-map_B.get("x");
//            double dif_BA_Y = map_A.get("y")-map_B.get("y");
//            double dif_BC_X = map_C.get("x")-map_B.get("x");
//            double dif_BC_Y = map_C.get("y")-map_B.get("y");

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
            // System.out.println(i+" sita:"+sita);
            // System.out.println(i+" degree:"+degree);

            sum_sita += sita;
            sum_degree += degree;
        }

        // System.out.println("sumSita:"+sum_sita);
        // System.out.println("sumDegree:"+sum_degree);

        if (sum_sita < 0) {
            //System.out.println("逆向き処理");
            ArrayList counter_tmp_List = new ArrayList();
            for (int i=tmp_List.size()-1; i>=0;i-- ) {
                counter_tmp_List.add(tmp_List.get(i));
            }
            return counter_tmp_List;
        }

        return tmp_List;

        // String tmp_switch_1 = new String();
        // String tmp_switch_2 = new String();
        //
        // Boolean counter_clock_wise_switch = false;
        //
        // int counter_clock_wise_count = 0;
        // int clock_wise_count = 0;
        //
        // String point_A = new String();
        // String point_B = new String();
        // String point_C = new String();
        // String point_D = new String();
        //
        // HashMap<String,Double> map_A = new HashMap<String,Double>();
        // HashMap<String,Double> map_B = new HashMap<String,Double>();
        // HashMap<String,Double> map_C = new HashMap<String,Double>();
        // HashMap<String,Double> map_D = new HashMap<String,Double>();
        // for (int i = 0; i<tmp_List.size()-2;i++ ) {
        //     point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i));
        //     point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i+1));
        //     if (i==0) {
        //         point_C = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(tmp_List.size()-2));
        //         point_D = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(tmp_List.size()-1));
        //     }else{
        //         point_C = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i-1));
        //         point_D = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i));
        //     }
        //
        //     map_A = OsmToGmlConverter.nodeMap.get(point_A);
        //     map_B = OsmToGmlConverter.nodeMap.get(point_B);
        //     map_C = OsmToGmlConverter.nodeMap.get(point_C);
        //     map_D = OsmToGmlConverter.nodeMap.get(point_D);
        //
        //     if (map_B.get("x")-map_A.get("x") > 0) {
        //         tmp_switch_1 = "+";
        //     }else{
        //         tmp_switch_1 = "-";
        //     }
        //     if (map_B.get("y")-map_A.get("y") > 0) {
        //         tmp_switch_1 = tmp_switch_1+"+";
        //     }else{
        //         tmp_switch_1 = tmp_switch_1+"-";
        //     }
        //
        //     if (map_D.get("x")-map_C.get("x") > 0) {
        //         tmp_switch_2 = "+";
        //     }else{
        //         tmp_switch_2 = "-";
        //     }
        //     if (map_D.get("y")-map_C.get("y") > 0) {
        //         tmp_switch_2 = tmp_switch_2+"+";
        //     }else{
        //         tmp_switch_2 = tmp_switch_2+"-";
        //     }
        //
        //     switch(tmp_switch_1){
        //         case "++":
        //             switch(tmp_switch_2){
        //                 case "+-":
        //                     counter_clock_wise_count++;
        //                 break;
        //
        //                 default:
        //                     clock_wise_count++;
        //                 break;
        //             }
        //         break;
        //         case "+-":
        //             switch(tmp_switch_2){
        //                 case "--":
        //                     counter_clock_wise_count++;
        //                     break;
        //                 default:
        //                     clock_wise_count++;
        //                 break;
        //             }
        //
        //         break;
        //         case "--":
        //             switch(tmp_switch_2){
        //                 case "-+":
        //                 counter_clock_wise_count++;
        //                 break;
        //             default:
        //                 clock_wise_count++;
        //             break;
        //                 }
        //         break;
        //         case "-+":
        //
        //             switch(tmp_switch_2){
        //                 case "++":
        //                 counter_clock_wise_count++;
        //                 break;
        //             default:
        //                 clock_wise_count++;
        //             break;
        //             }
        //         break;
        //         default:
        //         break;
        //     }
        // }
        //
        // if ( counter_clock_wise_count > clock_wise_count ) {
        //     ArrayList counter_tmp_List = new ArrayList();
        //     for (int i=tmp_List.size()-1; i>=0;i-- ) {
        //         counter_tmp_List.add(tmp_List.get(i));
        //     }
        //     return counter_tmp_List;
        // }
        //
        // return tmp_List;
        
        // String tmp_switch_1 = new String();
        // String tmp_switch_2 = new String();
        //
        // Boolean counter_clock_wise_switch = false;
        //
        // String point_A = new String();
        // String point_B = new String();
        // String point_C = new String();
        // String point_D = new String();
        //
        // HashMap<String,Double> map_A = new HashMap<String,Double>();
        // HashMap<String,Double> map_B = new HashMap<String,Double>();
        // HashMap<String,Double> map_C = new HashMap<String,Double>();
        // HashMap<String,Double> map_D = new HashMap<String,Double>();
        //
        // point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(0));
        // point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(1));
        // point_C = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(tmp_List.size()-2));
        // point_D = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(tmp_List.size()-1));
        //
        // map_A = OsmToGmlConverter.nodeMap.get(point_A);
        // map_B = OsmToGmlConverter.nodeMap.get(point_B);
        // map_C = OsmToGmlConverter.nodeMap.get(point_C);
        // map_D = OsmToGmlConverter.nodeMap.get(point_D);
        //
        // if (map_B.get("x")-map_A.get("x") > 0) {
        //     tmp_switch_1 = "+";
        // }else{
        //     tmp_switch_1 = "-";
        // }
        // if (map_B.get("y")-map_A.get("y") > 0) {
        //     tmp_switch_1 = tmp_switch_1+"+";
        // }else{
        //     tmp_switch_1 = tmp_switch_1+"-";
        // }
        //
        // if (map_D.get("x")-map_C.get("x") > 0) {
        //     tmp_switch_2 = "+";
        // }else{
        //     tmp_switch_2 = "-";
        // }
        // if (map_D.get("y")-map_C.get("y") > 0) {
        //     tmp_switch_2 = tmp_switch_2+"+";
        // }else{
        //     tmp_switch_2 = tmp_switch_2+"-";
        // }
        //
        // switch(tmp_switch_1){
        //     case "++":
        //         switch(tmp_switch_2){
        //             case "+-":
        //                 counter_clock_wise_switch = true;
        //             break;
        //             default:
        //             break;
        //         }
        //     break;
        //     case "+-":
        //         switch(tmp_switch_2){
        //             case "--":
        //                 counter_clock_wise_switch = true;
        //                 break;
        //                 default:
        //                 break;
        //             }
        //
        //     break;
        //     case "--":
        //         switch(tmp_switch_2){
        //             case "-+":
        //                 counter_clock_wise_switch = true;
        //                 break;
        //                 default:
        //                 break;
        //             }
        //     break;
        //     case "-+":
        //
        //         switch(tmp_switch_2){
        //             case "++":
        //                 counter_clock_wise_switch = true;
        //             break;
        //             default:
        //             break;
        //         }
        //     break;
        //     default:
        //     break;
        // }
        //
        //
        // if (counter_clock_wise_switch) {
        //     ArrayList counter_tmp_List = new ArrayList();
        //     for (int i=tmp_List.size()-1; i>=0;i-- ) {
        //         counter_tmp_List.add(tmp_List.get(i));
        //     }
        //     System.out.println("test");
        //     return counter_tmp_List;
        // }
        //
        // return tmp_List;
    }
}
