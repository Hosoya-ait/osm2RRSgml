import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class WriteEdge {

    private Document document;
    private Element rcrMap;
    private NodeManager     nm;
    private EdgeManager     em;
//    private HighwayManager  hm;
//    private BuildingManager bm;
//    private RoadManager     rm;

    public WriteEdge(Document doc,
                     Element rcr,
                     NodeManager     nm,
                     EdgeManager     em){
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
            int j = nm.getNodeSize() + i;
            idDeclare.setValue(""+j);
            rcrEdge.setAttributeNode(idDeclare);

            //orientationの付与
            Attr orientationMinus = this.document.createAttribute("orientation");
            orientationMinus.setValue("-");
            rcrDirectednodeMinus.setAttributeNode(orientationMinus);

            Attr orientationPlus = this.document.createAttribute("orientation");
            orientationPlus.setValue("+");
            rcrDirectednodePlus.setAttributeNode(orientationPlus);

//            System.out.println("辺" + em.getEdgeNodeList(String.valueOf(i)));

            //hrefの付与
            Attr hrefMinus = this.document.createAttribute("xlink:href");
            hrefMinus.setValue("#" + em.getEdgeNodeList(String.valueOf(i)).get(0));
            rcrDirectednodeMinus.setAttributeNode(hrefMinus);

            Attr hrefPlus = this.document.createAttribute("xlink:href");
            hrefMinus.setValue("#" + em.getEdgeNodeList(String.valueOf(i)).get(1));
            rcrDirectednodePlus.setAttributeNode(hrefPlus);

//            System.out.println("点1 = "  + em.getEdgeNodeList(String.valueOf(i)).get(1));
//            System.out.println("点0 = "  + em.getEdgeNodeList(String.valueOf(i)).get(0));
//            OsmToGmlConverter.linkEdgeID.put(""+id,""+i); //iをjにする
        }

        this.rcrMap.appendChild(rcrEdgeList);

        return document;
    }

//        OsmToGmlConverter.edgeMap.forEach((id, nodes)->{

//            Element rcrEdge = this.document.createElement("gml:Edge");
//            Element rcrDirectednodePlus = this.document.createElement("gml:directedNode");
//            Element rcrDirectednodeMinus = this.document.createElement("gml:directedNode");

//            rcrEdge.appendChild(rcrDirectednodeMinus);
//            rcrEdge.appendChild(rcrDirectednodePlus);
//            rcrEdgeList.appendChild(rcrEdge);

//            //idの付与
//            Attr idDeclare=this.document.createAttribute("gml:id");
//            int i = OsmToGmlConverter.nodeMap.size()+Integer.parseInt(id);
//            idDeclare.setValue(""+i);
//            rcrEdge.setAttributeNode(idDeclare);


//            //orientationの付与
//            Attr orientationMinus = this.document.createAttribute("orientation");
//            orientationMinus.setValue("-");
//            rcrDirectednodeMinus.setAttributeNode(orientationMinus);
//
//            Attr orientationPlus = this.document.createAttribute("orientation");
//            orientationPlus.setValue("+");
//            rcrDirectednodePlus.setAttributeNode(orientationPlus);

//            //hrefの付与
//            Attr hrefMinus = this.document.createAttribute("xlink:href");
//            hrefMinus.setValue("#"+nodes.get(0));
//            rcrDirectednodeMinus.setAttributeNode(hrefMinus);
//
//            Attr hrefPlus = this.document.createAttribute("xlink:href");
//            hrefPlus.setValue("#"+nodes.get(1));
//            rcrDirectednodePlus.setAttributeNode(hrefPlus);
//
//
//
//            OsmToGmlConverter.linkEdgeID.put(""+id,""+i); //iをjにする
//        });

}
