import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import java.util.ArrayList;

public class WriteBuilding {

    private Document document;
    private Element rcrMap;

    private static String tmpEdgeID = new String();
    private static String tmpNodeID = new String();

    private NodeManager     nm;
    private EdgeManager     em;
    private BuildingManager bm;
    private RoadManager     rm;

    public WriteBuilding(Document doc,
                         Element rcr,
                         NodeManager     nm,
                         EdgeManager     em,
                         BuildingManager bm,
                         RoadManager     rm){
        this.document = doc;
        this.rcrMap = rcr;
        this.nm = nm;
        this.em = em;
        this.bm = bm;
        this.rm = rm;
    }

    public Document WriteToDocumentBuilding(){

        Element rcrBuildingList=this.document.createElement("rcr:buildinglist");

        for (int i=1; i<=Integer.parseInt(bm.getBuildingEdgeID()); i++) {
            Element rcrBuilding = this.document.createElement("rcr:building");
            Element gmlFace = this.document.createElement("gml:Face");

            for (int j=0; j<bm.getBuildingEdgeList(String.valueOf(i)).size(); j++) {
                Element gmlDirectedEdge = this.document.createElement("gml:directedEdge");
                gmlFace.appendChild(gmlDirectedEdge);

                Attr orientation = this.document.createAttribute("orientation");

                ArrayList<String> array = bm.getBuildingEdgeList(String.valueOf(i));

                //逆方向にする辺は-にする処理
                if(bm.containReverseEdge(String.valueOf(i),array.get(j))){
                    orientation.setValue("-");
                }else{
                    orientation.setValue("+");
                }
                //orientation.setValue("-");

                gmlDirectedEdge.setAttributeNode(orientation);

                this.tmpNodeID = String.valueOf(i);
                this.tmpEdgeID = (String)bm.getBuildingEdgeList(String.valueOf(i)).get(j);

                for (int k=1; k<=Integer.parseInt(rm.getRoadEdgeID()); k++) {
                    if (String.valueOf(k) != this.tmpNodeID) {
                        if (rm.getRoadEdgeList(String.valueOf(k)).contains(this.tmpEdgeID)) {
                            Attr neighbour = this.document.createAttribute("rcr:neighbour");
                            int neighbourRoadID = nm.getNodeSize() + Integer.parseInt(em.getEdgeID()) + Integer.parseInt(bm.getBuildingEdgeID()) + k;
                            neighbour.setValue(""+ neighbourRoadID);
                            gmlDirectedEdge.setAttributeNode(neighbour);
                        }
                    }
                }
                Attr href = this.document.createAttribute("xlink:href");
                int hrefValue = nm.getNodeSize() + Integer.parseInt((String)bm.getBuildingEdgeList(String.valueOf(i)).get(j));
                href.setValue("#" + hrefValue);
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
            idDeclare.setValue(""+id);
            rcrBuilding.setAttributeNode(idDeclare);
        }
        this.rcrMap.appendChild(rcrBuildingList);

        return document;
    }
}
