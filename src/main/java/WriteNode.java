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
            //if (Converter.usedNodeList.contains(i)) {
            Element rcrNode = this.document.createElement("gml:Node");
            Element rcrPointProperty = this.document.createElement("gml:pointProperty");
            Element rcrPoint = this.document.createElement("gml:Point");
            Element rcrCoordinates = this.document.createElement("gml:coordinates");


            String text = String.format("%3.3f,%3.3f",
                    ((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("x"),
                    ((HashMap)Converter.nodeMap.get(Converter.linkInverseNodeID.get(""+i))).get("y")
            );


            rcrCoordinates.appendChild(this.document.createTextNode(text));

            rcrPoint.appendChild(rcrCoordinates);
            rcrPointProperty.appendChild(rcrPoint);
            rcrNode.appendChild(rcrPointProperty);
            rcrNodeList.appendChild(rcrNode);

            Attr idDeclare=this.document.createAttribute("gml:id");
            idDeclare.setValue(""+i);
            rcrNode.setAttributeNode(idDeclare);

        }

        this.rcrMap.appendChild(rcrNodeList);

        return document;
    }
}
