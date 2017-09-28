import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.HashMap;
import java.util.ArrayList;


public class WriteRoad {

  private Document document;
  private Element rcrMap;
  private Integer i;

  public WriteRoad(Document doc, Element rcr){
    this.document = doc;
    this.rcrMap = rcr;
  }

  public Document WriteToDocumentRoad(){

    Element rcrRoadList=this.document.createElement("rcr:roadlist");

    Converter.roadMap.forEach((id,edges)->{

      Element rcrRoad = this.document.createElement("rcr:road");
      Element gmlFace = this.document.createElement("gml:Face");

      for (Integer n = 0; n < edges.size() ;n++ ) {
        Element gmlDirectedEdge = this.document.createElement("gml:directedEdge");

        gmlFace.appendChild(gmlDirectedEdge);

        //plusかMinusかの処理
        Attr orientation = this.document.createAttribute("orientation");
        orientation.setValue("+");
        gmlDirectedEdge.setAttributeNode(orientation);

        Attr href = this.document.createAttribute("xlink:href");
        href.setValue("#"+Converter.linkEdgeID.get(edges.get(n)));
        gmlDirectedEdge.setAttributeNode(href);

        //neighbour情報
      }

      rcrRoad.appendChild(gmlFace);
      rcrRoadList.appendChild(rcrRoad);

      //idの付与
      Attr idDeclare=this.document.createAttribute("gml:id");
      int i = Converter.nodeMap.size()+Converter.edgeMap.size()+Converter.buildingMap.size()+Integer.parseInt(id);
      idDeclare.setValue(""+i);
      rcrRoad.setAttributeNode(idDeclare);


    });

    this.rcrMap.appendChild(rcrRoadList);

    return document;
  }
}
