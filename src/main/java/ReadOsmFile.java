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
    private EdgeManager     em = new EdgeManager();
    private HighwayManager  hm = new HighwayManager();
    private BuildingManager bm = new BuildingManager();
    private RoadManager     rm = new RoadManager();

    ReadOsmFile(Document        document,
                NodeManager     nm,
                EdgeManager     em,
                HighwayManager  hm,
                BuildingManager bm,
                RoadManager     rm) {
        this.document = document;
        this.nm = nm;
        this.em = em;
        this.hm = hm;
        this.bm = bm;
        this.rm = rm;

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
                /*
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
                */
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
            System.out.print(attributeId.getNodeValue());
            System.out.println(" ," + nm.getGmlID(attributeId.getNodeValue()));

            //OsmToGmlConverter.nodeMap.put(attributeId.getNodeValue(),map);
            //OsmToGmlConverter.linkNodeID.put(attributeId.getNodeValue(),""+ OsmToGmlConverter.nodeMap.size());
            //OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),attributeId.getNodeValue());
        }
    }


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

    public NodeManager getNM() {
        return nm;
    }
}
