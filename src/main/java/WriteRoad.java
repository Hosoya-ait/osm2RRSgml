import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.ArrayList;

public class WriteRoad  {

    private Document document;
    private Element rcrMap;
    private Integer i;

    private static String tmpEdgeID = new String();
    private static String tmpNodeID = new String();

    public WriteRoad(Document doc, Element rcr){
        this.document = doc;
        this.rcrMap = rcr;
    }

    public Document WriteToDocumentRoad(){

        Element rcrRoadList=this.document.createElement("rcr:roadlist");

        OsmToGmlConverter.roadMap.forEach((id, edges)->{

            Element rcrRoad = this.document.createElement("rcr:road");
            Element gmlFace = this.document.createElement("gml:Face");

            for (Integer n = 0; n < edges.size() ;n++ ) {
                Element gmlDirectedEdge = this.document.createElement("gml:directedEdge");

                gmlFace.appendChild(gmlDirectedEdge);

                Attr orientation = this.document.createAttribute("orientation");

                if (OsmToGmlConverter.minusDirectionEdgeMap.containsKey(id)) {
                    if (((ArrayList) OsmToGmlConverter.minusDirectionEdgeMap.get(id)).contains(edges.get(n))) {
                        orientation.setValue("-");
                    }else{
                        orientation.setValue("+");
                    }
                }else{
                    orientation.setValue("+");
                }

                gmlDirectedEdge.setAttributeNode(orientation);

                this.tmpNodeID = id;
                this.tmpEdgeID = edges.get(n);
                //neighbour情報
                OsmToGmlConverter.buildingMap.forEach((neID, neEdges)->{
                    if(neEdges.contains(this.tmpEdgeID)){
                        Attr neighbour = this.document.createAttribute("rcr:neighbour");

                        int neighbourBuildingID = OsmToGmlConverter.nodeMap.size()+ OsmToGmlConverter.edgeMap.size()+Integer.parseInt(neID);

                        neighbour.setValue(""+neighbourBuildingID);
                        gmlDirectedEdge.setAttributeNode(neighbour);
                    }
                });


                OsmToGmlConverter.roadMap.forEach((neID, neEdges)->{
                    if (neID != this.tmpNodeID) {
                        if(neEdges.contains(this.tmpEdgeID)){
                            Attr neighbour = this.document.createAttribute("rcr:neighbour");

                            int neighbourRoadID = OsmToGmlConverter.nodeMap.size()+ OsmToGmlConverter.edgeMap.size()+ OsmToGmlConverter.buildingMap.size()+Integer.parseInt(neID);
                            neighbour.setValue(""+neighbourRoadID);
                            gmlDirectedEdge.setAttributeNode(neighbour);
                        }
                    }
                });

                Attr href = this.document.createAttribute("xlink:href");
                href.setValue("#"+ OsmToGmlConverter.linkEdgeID.get(edges.get(n)));
                gmlDirectedEdge.setAttributeNode(href);
            }

            rcrRoad.appendChild(gmlFace);
            rcrRoadList.appendChild(rcrRoad);

            //idの付与
            Attr idDeclare=this.document.createAttribute("gml:id");
            int i = OsmToGmlConverter.nodeMap.size()+ OsmToGmlConverter.edgeMap.size()+ OsmToGmlConverter.buildingMap.size()+Integer.parseInt(id);
            idDeclare.setValue(""+i);
            rcrRoad.setAttributeNode(idDeclare);

        });

        this.rcrMap.appendChild(rcrRoadList);

        return document;
    }
}
