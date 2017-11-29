import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import java.util.ArrayList;

public class WriteEdge {

    private Document document;
    private Element rcrMap;
    private NodeManager nm;
    private EdgeManager em;

    public WriteEdge(Document doc,
                     Element rcr,
                     NodeManager nm,
                     EdgeManager em){
        this.document = doc;
        this.rcrMap = rcr;
        this.nm = nm;
        this.em = em;
    }

    public Document WriteToDocumentEdge(){
        Element rcrEdgeList=this.document.createElement("rcr:edgelist");

        for (int i=1; i<=Integer.parseInt(em.getEdgeID()); i++) {
            Element rcrEdge = this.document.createElement("gml:Edge");
            Element rcrDirectednodePlus = this.document.createElement("gml:directedNode");
            Element rcrDirectednodeMinus = this.document.createElement("gml:directedNode");

            rcrEdge.appendChild(rcrDirectednodeMinus);
            rcrEdge.appendChild(rcrDirectednodePlus);
            rcrEdgeList.appendChild(rcrEdge);

            //idの付与
            Attr idDeclare=this.document.createAttribute("gml:id");
            //int i = OsmToGmlConverter.nodeMap.size()+Integer.parseInt(id);
            String set_edge_ID = ""+(nm.getNodeSize() + i);
            idDeclare.setValue(set_edge_ID);
            rcrEdge.setAttributeNode(idDeclare);

            //orientationの付与
            Attr orientationMinus = this.document.createAttribute("orientation");
            orientationMinus.setValue("-");
            rcrDirectednodeMinus.setAttributeNode(orientationMinus);

            Attr orientationPlus = this.document.createAttribute("orientation");
            orientationPlus.setValue("+");
            rcrDirectednodePlus.setAttributeNode(orientationPlus);

            ArrayList<String> write_gml_edge = em.getEdgeNodeList(String.valueOf(i));

            //hrefの付与
            Attr hrefMinus = this.document.createAttribute("xlink:href");
            hrefMinus.setValue("#" + write_gml_edge.get(0));
            rcrDirectednodeMinus.setAttributeNode(hrefMinus);

            Attr hrefPlus = this.document.createAttribute("xlink:href");
            hrefPlus.setValue("#" + write_gml_edge.get(1));
            rcrDirectednodePlus.setAttributeNode(hrefPlus);

        }

        this.rcrMap.appendChild(rcrEdgeList);

        return document;
    }
}
