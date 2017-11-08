import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class WriteBuilding {

    private Document document;
    private Element rcrMap;
    private Integer i;

    private static String tmpEdgeID = new String();
    private static String tmpNodeID = new String();

    private NodeManager     nm;
    private EdgeManager     em;
    private HighwayManager  hm;
    private BuildingManager bm;
    private RoadManager     rm;

    public WriteBuilding(Document doc,
                         Element rcr,
                         NodeManager     nm,
                         EdgeManager     em,
                         HighwayManager  hm,
                         BuildingManager bm,
                         RoadManager     rm){
        this.document = doc;
        this.rcrMap = rcr;
        this.nm = nm;
        this.em = em;
        this.hm = hm;
        this.bm = bm;
        this.rm = rm;
    }

    public Document WriteToDocumentBuilding(){

        Element rcrBuildingList=this.document.createElement("rcr:buildinglist");

        for (int i=1; i<=Integer.parseInt(bm.getBuildingEdgeID()); i++) {
            Element rcrBuilding = this.document.createElement("rcr:building");
            Element gmlFace = this.document.createElement("gml:Face");

//            System.out.println("buildingSize = " + bm.getBuildingEdgeList(String.valueOf(i)).size());
//            System.out.println("buildingList = " + bm.getBuildingEdgeList(String.valueOf(i)));

            for (int j=0; j<bm.getBuildingEdgeList(String.valueOf(i)).size(); j++) {

//                System.out.println("buildinglist内" + bm.getBuildingEdgeList(String.valueOf(i)).get(j));

                Element gmlDirectedEdge = this.document.createElement("gml:directedEdge");
                gmlFace.appendChild(gmlDirectedEdge);

                Attr orientation = this.document.createAttribute("orientation");
                orientation.setValue("-");

                gmlDirectedEdge.setAttributeNode(orientation);

                this.tmpNodeID = String.valueOf(i);
                this.tmpEdgeID = (String)bm.getBuildingEdgeList(String.valueOf(i)).get(j);

//                System.out.println("roadID = " + rm.getRoadEdgeID());

                for (int k=1; k<=Integer.parseInt(rm.getRoadEdgeID()); k++) {
//                    System.out.println("k = " + k);


                    //ここまで動作確認済み



                    if (String.valueOf(k) != this.tmpNodeID) {
                        if (rm.getRoadEdgeList(String.valueOf(k)).contains(this.tmpEdgeID)) {
                            Attr neighbour = this.document.createAttribute("rcr:neighbour");

//                            int neighbourRoadID = OsmToGmlConverter.nodeMap.size()+ OsmToGmlConverter.edgeMap.size()+ OsmToGmlConverter.buildingMap.size()+Integer.parseInt(neID);
                            int neighbourRoadID = nm.getNodeSize() + Integer.parseInt(em.getEdgeID()) + Integer.parseInt(bm.getBuildingEdgeID()) + k;
                            neighbour.setValue(""+ neighbourRoadID);
                            gmlDirectedEdge.setAttributeNode(neighbour);
                        }
                    }
                }
                Attr href = this.document.createAttribute("xlink:href");
//                href.setValue("#"+ OsmToGmlConverter.linkEdgeID.get(edges.get(n)));
                href.setValue("#" + bm.getBuildingEdgeList(String.valueOf(i)).get(j));
                gmlDirectedEdge.setAttributeNode(href);

                //neighbour情報

            }

            rcrBuilding.appendChild(gmlFace);
            rcrBuildingList.appendChild(rcrBuilding);

            //各種Face情報の付与
            Attr floorDeclare=this.document.createAttribute("rcr:floor");
            floorDeclare.setValue("1");
            gmlFace.setAttributeNode(floorDeclare);

            Attr buildingCodeDeclare=this.document.createAttribute("rcr:buildingcode");
            buildingCodeDeclare.setValue("0");
            gmlFace.setAttributeNode(buildingCodeDeclare);

            Attr importanceDeclare=this.document.createAttribute("rcr:importance");
            importanceDeclare.setValue("1");
            gmlFace.setAttributeNode(importanceDeclare);

            //idの付与
            Attr idDeclare=this.document.createAttribute("gml:id");
            int id = nm.getNodeSize() + Integer.parseInt(em.getEdgeID()) + i;
//            int i = OsmToGmlConverter.nodeMap.size()+ OsmToGmlConverter.edgeMap.size()+Integer.parseInt(id);
            idDeclare.setValue(""+id);
            rcrBuilding.setAttributeNode(idDeclare);
        }
        this.rcrMap.appendChild(rcrBuildingList);

        return document;
    }
}
