import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.HashMap;



public class WriteNode {

  private Document document;
  private Element rcrMap;
  private Integer i;

  public WriteNode(Document doc, Element rcr){
    this.document = doc;
    this.rcrMap = rcr;
  }

  public Document WriteToDocumentNode(){
    Element rcrNodeList=this.document.createElement("rcr:nodelist");

    for (int i = 1; i<=Converter.linkNodeID.size();i++ ) {
      Element rcrNode = this.document.createElement("gml:Node");
      Element rcrPointProperty = this.document.createElement("gml:pointProperty");
      Element rcrPoint = this.document.createElement("gml:Point");
      Element rcrCoordinates = this.document.createElement("gml:coordinates");

      // String text = String.format("%3.3f,%3.3f",
      //               ((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("lon"),
      //               ((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("lat")
      //               );

      //rcrCoordinates.appendChild(this.document.createTextNode(text));

      String text = String.format("%3.3f,%3.3f",
                    ((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("lon"),
                    ((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("lat")
                    );

      rcrCoordinates.appendChild(this.document.createTextNode(((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("lon")+","+
      ((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("lat")));


      rcrPoint.appendChild(rcrCoordinates);
      rcrPointProperty.appendChild(rcrPoint);
      rcrNode.appendChild(rcrPointProperty);
      rcrNodeList.appendChild(rcrNode);

      Attr idDeclare=this.document.createAttribute("gml:id");
      idDeclare.setValue(""+i);
      rcrNode.setAttributeNode(idDeclare);
    }

    // Converter.nodeMap.forEach((id,ll)->{
    //   Element rcrNode = this.document.createElement("gml:Node");
    //   Element rcrPointProperty = this.document.createElement("gml:pointProperty");
    //   Element rcrPoint = this.document.createElement("gml:Point");
    //   Element rcrCoordinates = this.document.createElement("gml:coordinates");
    //
    //   String text = String.format("%3.3f,%3.3f",ll.get("lon"),ll.get("lat"));
    //   rcrCoordinates.appendChild(this.document.createTextNode(text));
    //
    //   rcrPoint.appendChild(rcrCoordinates);
    //   rcrPointProperty.appendChild(rcrPoint);
    //   rcrNode.appendChild(rcrPointProperty);
    //   rcrNodeList.appendChild(rcrNode);
    //
    //   Attr idDeclare=this.document.createAttribute("gml:id");
    //   idDeclare.setValue(Converter.linkNodeID.get(id));
    //   rcrNode.setAttributeNode(idDeclare);
    //
    // });

    this.rcrMap.appendChild(rcrNodeList);

    return document;
  }
}
