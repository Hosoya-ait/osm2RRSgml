import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.ArrayList;

public class WriteRoad  {

    private Document document;
    private Element rcrMap;

    private static String tmpEdgeID = new String();
    private static String tmpNodeID = new String();

    private NodeManager     nm;
    private EdgeManager     em;
    private BuildingManager bm;
    private RoadManager     rm;

    public WriteRoad(Document doc,
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

    public Document WriteToDocumentRoad(){

        Element rcrRoadList=this.document.createElement("rcr:roadlist");

        for (int i=1; i<=Integer.parseInt(rm.getRoadEdgeID()); i++) {
            //if (rm.containRemoveRoadList(String.valueOf(i))==false) {
              Element rcrRoad = this.document.createElement("rcr:road");
              Element gmlFace = this.document.createElement("gml:Face");

              ArrayList<String> write_road_edge_list = rm.getRoadEdgeList(String.valueOf(i));
              for (int n=0; n<write_road_edge_list.size(); n++) {

                  Element gmlDirectedEdge = this.document.createElement("gml:directedEdge");

                  gmlFace.appendChild(gmlDirectedEdge);

                  Attr orientation = this.document.createAttribute("orientation");

                  //System.out.println("road_ID:"+String.valueOf(i));
                  //System.out.println("Edge_ID:"+write_road_edge_list.get(n));
                  if (rm.containMinusDirectionEdge(String.valueOf(i),write_road_edge_list.get(n))) {
                      orientation.setValue("-");
                      //System.out.println("-");
                  } else {
                      //System.out.println("+");
                      orientation.setValue("+");
                  }

                  gmlDirectedEdge.setAttributeNode(orientation);

                  this.tmpNodeID = String.valueOf(i);
                  this.tmpEdgeID = write_road_edge_list.get(n);
                  //neighbour情報
                  for (int neID=1; neID<=Integer.parseInt(bm.getBuildingEdgeID()); neID++) {
                      if (bm.getBuildingEdgeList(String.valueOf(neID)).contains(this.tmpEdgeID)) {
                          Attr neighbour = this.document.createAttribute("rcr:neighbour");

                          int neighbourBuildingID = nm.getNodeSize() + Integer.parseInt(em.getEdgeID()) + neID;

                          neighbour.setValue(""+neighbourBuildingID);
                          gmlDirectedEdge.setAttributeNode(neighbour);
                      }
                  }
                  for (int neID=1; neID<=Integer.parseInt(rm.getRoadEdgeID()); neID++) {

                      //String の比較を!=でやってたので！equalsに変えた
                      if (! String.valueOf(neID).equals(this.tmpNodeID)) {

                          if (rm.getRoadEdgeList(String.valueOf(neID)).contains(this.tmpEdgeID)) {
                              Attr neighbour = this.document.createAttribute("rcr:neighbour");

                              int neighbourRoadID = nm.getNodeSize() + Integer.parseInt(em.getEdgeID()) + Integer.parseInt(bm.getBuildingEdgeID()) + neID;
                              neighbour.setValue(""+neighbourRoadID);
                              gmlDirectedEdge.setAttributeNode(neighbour);
                          }
                      }
                  }
                  Attr href = this.document.createAttribute("xlink:href");
                  int hrefValue = nm.getNodeSize() + Integer.parseInt(write_road_edge_list.get(n));
                  href.setValue("#" + hrefValue);
                  gmlDirectedEdge.setAttributeNode(href);
              }
              rcrRoad.appendChild(gmlFace);
              rcrRoadList.appendChild(rcrRoad);

              //idの付与
              Attr idDeclare=this.document.createAttribute("gml:id");
              int newRoadId = nm.getNodeSize() + Integer.parseInt(em.getEdgeID()) + Integer.parseInt(bm.getBuildingEdgeID()) + i;
              idDeclare.setValue(""+newRoadId);
              rcrRoad.setAttributeNode(idDeclare);
            //}

        }
        this.rcrMap.appendChild(rcrRoadList);

        return document;
    }
}
