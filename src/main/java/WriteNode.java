import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.HashMap;

public class WriteNode {

  private Document document;
  private Element rcrMap;

  public WriteNode(Document doc, Element rcr){
    this.document = doc;
    this.rcrMap = rcr;
  }

  public Document WriteToDocumentNode(){
    Element rcrNodeList=this.document.createElement("rcr:nodelist");

  
    Converter.nodeMap.forEach((id,ll)->{

      Element rcrNode = this.document.createElement("gml:Node");
      Element rcrPointProperty = this.document.createElement("gml:pointProperty");
      Element rcrPoint = this.document.createElement("gml:Point");
      Element rcrCoordinates = this.document.createElement("gml:coordinates");

      rcrCoordinates.appendChild(this.document.createTextNode(ll.get("lat")+","+ll.get("lon")));



      rcrPoint.appendChild(rcrCoordinates);
      rcrPointProperty.appendChild(rcrPoint);
      rcrNode.appendChild(rcrPointProperty);
      rcrNodeList.appendChild(rcrNode);

      Attr idDeclare=this.document.createAttribute("gml:id");
      idDeclare.setValue(""+id);
      rcrNode.setAttributeNode(idDeclare);



    });

    this.rcrMap.appendChild(rcrNodeList);

    return document;
  }
}
