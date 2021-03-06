import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

public class WriteDocument {
    private NodeManager     nm;
    private EdgeManager     em;
    private BuildingManager bm;
    private RoadManager     rm;

    WriteDocument (NodeManager     nm,
                   EdgeManager     em,
                   BuildingManager bm,
                   RoadManager     rm) {
        this.nm = nm;
        this.em = em;
        this.bm = bm;
        this.rm = rm;
    }



    //                                        writeDoc
    public Document WriteToDocument1(Document document){
        //エレメントrcrMapを作成
        Element rcrMap=document.getDocumentElement();

        //以下2段落はgmlファイルのお約束を入力している
        Attr xmlnsGmlDeclare=document.createAttribute("xmlns:gml");
        xmlnsGmlDeclare.setValue(OsmToGmlConverter.xmlns_gml_namespace_uri);
        rcrMap.setAttributeNode(xmlnsGmlDeclare);

        Attr xmlnsXlinkDeclare=document.createAttribute("xmlns:xlink");
        xmlnsXlinkDeclare.setValue(OsmToGmlConverter.xmlns_xlink_namespace_uri);
        rcrMap.setAttributeNode(xmlnsXlinkDeclare);

        //nmで管理されているnodeをgmlへ書き込み
        WriteNode writeNode = new WriteNode(document,rcrMap,nm);
        document = writeNode.WriteToDocumentNode();

        //emで管理されているedgeをgmlへ書き込み
        WriteEdge writeEdge = new WriteEdge(document,rcrMap,nm,em);
        document = writeEdge.WriteToDocumentEdge();


        WriteBuilding writeBuilding = new WriteBuilding(document,rcrMap,nm,em,bm,rm);
        document = writeBuilding.WriteToDocumentBuilding();

        WriteRoad writeRoad = new WriteRoad(document,rcrMap,nm,em,bm,rm);
        document = writeRoad.WriteToDocumentRoad();

        return document;
    }
}
