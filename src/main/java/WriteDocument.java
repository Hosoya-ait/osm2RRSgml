import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class WriteDocument {

    // WriteDocument(Document document) throws Exception {
    //
    // }

    private NodeManager     nm;
    private EdgeManager     em;
    private HighwayManager  hm;
    private BuildingManager bm;
    private RoadManager     rm;

    WriteDocument (NodeManager     nm,
                   EdgeManager     em,
                   HighwayManager  hm,
                   BuildingManager bm,
                   RoadManager     rm) {
        this.nm = nm;
        this.em = em;
        this.hm = hm;
        this.bm = bm;
        this.rm = rm;
    }



    public Document WriteToDocument1(Document document){
        Element rcrMap=document.getDocumentElement();

        Attr xmlnsGmlDeclare=document.createAttribute("xmlns:gml");
        xmlnsGmlDeclare.setValue(OsmToGmlConverter.xmlns_gml_namespace_uri);
        rcrMap.setAttributeNode(xmlnsGmlDeclare);

        Attr xmlnsXlinkDeclare=document.createAttribute("xmlns:xlink");
        xmlnsXlinkDeclare.setValue(OsmToGmlConverter.xmlns_xlink_namespace_uri);
        rcrMap.setAttributeNode(xmlnsXlinkDeclare);

        WriteNode writeNode = new WriteNode(document,rcrMap,nm);
        document = writeNode.WriteToDocumentNode();


        WriteEdge writeEdge = new WriteEdge(document,rcrMap,nm,em);
        document = writeEdge.WriteToDocumentEdge();

        WriteBuilding writeBuilding = new WriteBuilding(document,rcrMap,nm,em,hm,bm,rm);
        document = writeBuilding.WriteToDocumentBuilding();

        /*
        WriteRoad writeRoad = new WriteRoad(document,rcrMap);
        document = writeRoad.WriteToDocumentRoad();
        */

        return document;
    }

    public Document WriteToDocument(Document document){
        Element rcrMap=document.getDocumentElement();

        Attr xmlnsGmlDeclare=document.createAttribute("xmlns:gml");
        xmlnsGmlDeclare.setValue(OsmToGmlConverter.xmlns_gml_namespace_uri);
        rcrMap.setAttributeNode(xmlnsGmlDeclare);

        Attr xmlnsXlinkDeclare=document.createAttribute("xmlns:xlink");
        xmlnsXlinkDeclare.setValue(OsmToGmlConverter.xmlns_xlink_namespace_uri);
        rcrMap.setAttributeNode(xmlnsXlinkDeclare);

        //WriteNode writeNode = new WriteNode(document,rcrMap);
        //document = writeNode.WriteToDocumentNode();

        /*
        WriteEdge writeEdge = new WriteEdge(document,rcrMap);
        document = writeEdge.WriteToDocumentEdge();

        WriteBuilding writeBuilding = new WriteBuilding(document,rcrMap);
        document = writeBuilding.WriteToDocumentBuilding();

        WriteRoad writeRoad = new WriteRoad(document,rcrMap);
        document = writeRoad.WriteToDocumentRoad();
        */

        return document;
    }

}
