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

    ReadOsmFile(Document document){
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
                    setNodeMap("id", elementNodes);
                    break;

                case "way":
                    tmpNodeList = new ArrayList();
                    tmpKey = new String();

                    checkHighway.clearCheckList();

                    Node itemNodes = elementNodes.getFirstChild();
                    while(itemNodes != null) {
                        getWayInfo(elementNodesNodeName, itemNodes);
                        itemNodes = itemNodes.getNextSibling();
                    }
                    //System.out.println("key"+tmpKey);

                    switch (tmpKey) {
                        case "building":
                            if (tmpNodeList.size() > 2) {
                                tmpNodeList = checkDirection(tmpNodeList);
                                OsmToGmlConverter.tmpBuildingList.add(tmpNodeList);
                            }

                            break;
                        case "highway":
                            if (tmpNodeList.size() > 1) {
                                if (checkHighway.checkList()) {
                                    OsmToGmlConverter.tmpHighwayList.add(tmpNodeList);
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

    private static void getWayInfo(String nodeName, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        switch (node.getNodeName()) {
            case "nd":
                Node attributeRef = attributes.getNamedItem("ref");
                tmpNodeList.add(OsmToGmlConverter.linkNodeID.get(attributeRef.getNodeValue()));
                //System.out.println(OsmToGmlConverter.linkNodeID.get(attributeRef.getNodeValue()));
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

                    default:
                        //System.out.println("tag:"+attributeK.getNodeValue());
                        checkHighway.setCheckList(attributeK.getNodeValue());
                        break;
                }
                break;
        }

    }


        private static Boolean checkWayInfo(String key, ArrayList nodes) {
        if (key != null) {
            //System.out.println(key);
            switch (key) {
                case "building":
                    if (nodes.size() > 2) {
                        return true;
                    }

                    break;
                case "highway":
                    if (nodes.size() > 1) {
                        return true;
                    }
                    break;

                default:
                    break;
            }
        }
        return false;
    }

    private static void setNodeMap(String id, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes!=null) {
            HashMap<String,Double> map = new HashMap<String,Double>();

            Node attributeId = attributes.getNamedItem(id);
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


            OsmToGmlConverter.nodeMap.put(attributeId.getNodeValue(),map);
            OsmToGmlConverter.linkNodeID.put(attributeId.getNodeValue(),""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),attributeId.getNodeValue());
        }
    }
    private ArrayList checkDirection(ArrayList tmp_List){


        String tmp_switch_1 = new String();
        String tmp_switch_2 = new String();

        Boolean counter_clock_wise_switch = false;

        int counter_clock_wise_count = 0;
        int clock_wise_count = 0;

        String point_A = new String();
        String point_B = new String();
        String point_C = new String();
        String point_D = new String();

        HashMap<String,Double> map_A = new HashMap<String,Double>();
        HashMap<String,Double> map_B = new HashMap<String,Double>();
        HashMap<String,Double> map_C = new HashMap<String,Double>();
        HashMap<String,Double> map_D = new HashMap<String,Double>();
        for (int i = 0; i<tmp_List.size()-2;i++ ) {
            point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i));
            point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i+1));
            if (i==0) {
                point_C = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(tmp_List.size()-2));
                point_D = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(tmp_List.size()-1));
            }else{
                point_C = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i-1));
                point_D = OsmToGmlConverter.linkInverseNodeID.get(tmp_List.get(i));
            }


            map_A = OsmToGmlConverter.nodeMap.get(point_A);
            map_B = OsmToGmlConverter.nodeMap.get(point_B);
            map_C = OsmToGmlConverter.nodeMap.get(point_C);
            map_D = OsmToGmlConverter.nodeMap.get(point_D);

            if (map_B.get("x")-map_A.get("x") > 0) {
                tmp_switch_1 = "+";
            }else{
                tmp_switch_1 = "-";
            }
            if (map_B.get("y")-map_A.get("y") > 0) {
                tmp_switch_1 = tmp_switch_1+"+";
            }else{
                tmp_switch_1 = tmp_switch_1+"-";
            }

            if (map_D.get("x")-map_C.get("x") > 0) {
                tmp_switch_2 = "+";
            }else{
                tmp_switch_2 = "-";
            }
            if (map_D.get("y")-map_C.get("y") > 0) {
                tmp_switch_2 = tmp_switch_2+"+";
            }else{
                tmp_switch_2 = tmp_switch_2+"-";
            }

            switch(tmp_switch_1){
                case "++":
                    switch(tmp_switch_2){
                        case "+-":
                            counter_clock_wise_count++;
                        break;

                        default:
                            clock_wise_count++;
                        break;
                    }
                break;
                case "+-":
                    switch(tmp_switch_2){
                        case "--":
                            counter_clock_wise_count++;
                            break;
                        default:
                            clock_wise_count++;
                        break;
                    }

                break;
                case "--":
                    switch(tmp_switch_2){
                        case "-+":
                        counter_clock_wise_count++;
                        break;
                    default:
                        clock_wise_count++;
                    break;
                        }
                break;
                case "-+":

                    switch(tmp_switch_2){
                        case "++":
                        counter_clock_wise_count++;
                        break;
                    default:
                        clock_wise_count++;
                    break;
                    }
                break;
                default:
                break;
            }
        }






        if (counter_clock_wise_count>clock_wise_count) {
            ArrayList counter_tmp_List = new ArrayList();
            for (int i=tmp_List.size()-1; i>=0;i-- ) {
                counter_tmp_List.add(tmp_List.get(i));
            }
            System.out.println("test");
            return counter_tmp_List;
        }

        return tmp_List;



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
