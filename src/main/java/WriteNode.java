import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import java.util.HashMap;
import java.util.ArrayList;

public class WriteNode {

    private Document document;
    private Element rcrMap;
    private NodeManager nm;

    public WriteNode(Document doc, Element rcr, NodeManager nm){
        this.document = doc;
        this.rcrMap = rcr;
        this.nm = nm;
    }

    public Document WriteToDocumentNode(){
        Element rcrNodeList=this.document.createElement("rcr:nodelist");
        for (int i=1; i<nm.getNodeSize(); i++) {
            if (nm.checkUsedNodeList(String.valueOf(i))) {
                Element rcrNode = this.document.createElement("gml:Node");
                Element rcrPointProperty = this.document.createElement("gml:pointProperty");
                Element rcrPoint = this.document.createElement("gml:Point");
                Element rcrCoordinates = this.document.createElement("gml:coordinates");

                String text = String.format("%3.3f,%3.3f", nm.getX(String.valueOf(i)), nm.getY(String.valueOf(i)));

                rcrCoordinates.appendChild(this.document.createTextNode(text));

                rcrPoint.appendChild(rcrCoordinates);
                rcrPointProperty.appendChild(rcrPoint);
                rcrNode.appendChild(rcrPointProperty);
                rcrNodeList.appendChild(rcrNode);

                Attr idDeclare=this.document.createAttribute("gml:id");
                idDeclare.setValue(String.valueOf(i));
                rcrNode.setAttributeNode(idDeclare);
            }
        }
        this.rcrMap.appendChild(rcrNodeList);

        return document;
    }
}
