import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.HashMap;
import java.util.ArrayList;


public class WriteBuilding {

  private Document document;
  private Element rcrMap;
  private Integer i;

  public WriteBuilding(Document doc, Element rcr){
    this.document = doc;
    this.rcrMap = rcr;
  }

  public Document WriteToDocumentBuilding(){

    Element rcrBuildingList=this.document.createElement("rcr:buildinglist");



    Converter.buildingMap.forEach((id,edges)->{

      Element rcrBuilding = this.document.createElement("rcr:building");
      Element gmlFace = this.document.createElement("gml:Face");

      for (Integer n = 0; n < edges.size() ;n++ ) {
        Element gmlDirectedEdge = this.document.createElement("gml:directedEdge");

        gmlFace.appendChild(gmlDirectedEdge);

        Attr orientation = this.document.createAttribute("orientation");
        orientation.setValue("+");
        gmlDirectedEdge.setAttributeNode(orientation);

        Attr href = this.document.createAttribute("xlink:href");
        href.setValue("#"+Converter.linkEdgeID.get(edges.get(n)));
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
      int i = Converter.nodeMap.size()+Converter.edgeMap.size()+Integer.parseInt(id);
      idDeclare.setValue(""+i);
      rcrBuilding.setAttributeNode(idDeclare);


    });

    this.rcrMap.appendChild(rcrBuildingList);

    return document;
  }
}
