import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class WriteEdge {

    private Document document;
    private Element rcrMap;

    public WriteEdge(Document doc, Element rcr){
        this.document = doc;
        this.rcrMap = rcr;
    }

    public Document WriteToDocumentEdge(){
        Element rcrEdgeList=this.document.createElement("rcr:edgelist");
        OsmToGmlConverter.edgeMap.forEach((id, nodes)->{

            Element rcrEdge = this.document.createElement("gml:Edge");
            Element rcrDirectednodePlus = this.document.createElement("gml:directedNode");
            Element rcrDirectednodeMinus = this.document.createElement("gml:directedNode");




            rcrEdge.appendChild(rcrDirectednodeMinus);
            rcrEdge.appendChild(rcrDirectednodePlus);
            rcrEdgeList.appendChild(rcrEdge);

            //idの付与
            Attr idDeclare=this.document.createAttribute("gml:id");
            int i = OsmToGmlConverter.nodeMap.size()+Integer.parseInt(id);
            idDeclare.setValue(""+i);
            rcrEdge.setAttributeNode(idDeclare);


            //orientationの付与
            Attr orientationMinus = this.document.createAttribute("orientation");
            orientationMinus.setValue("-");
            rcrDirectednodeMinus.setAttributeNode(orientationMinus);

            Attr orientationPlus = this.document.createAttribute("orientation");
            orientationPlus.setValue("+");
            rcrDirectednodePlus.setAttributeNode(orientationPlus);

            //hrefの付与
            Attr hrefMinus = this.document.createAttribute("xlink:href");
            hrefMinus.setValue("#"+nodes.get(0));
            rcrDirectednodeMinus.setAttributeNode(hrefMinus);

            Attr hrefPlus = this.document.createAttribute("xlink:href");
            hrefPlus.setValue("#"+nodes.get(1));
            rcrDirectednodePlus.setAttributeNode(hrefPlus);



            OsmToGmlConverter.linkEdgeID.put(""+id,""+i);
        });

        this.rcrMap.appendChild(rcrEdgeList);

        return document;
    }
}
