import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;




public class WriteDocument {

  // WriteDocument(Document document) throws Exception {
  //
  // }

  public Document WriteToDocument(Document document){
    Element rcrMap=document.getDocumentElement();

    Attr xmlnsGmlDeclare=document.createAttribute("xmlns:gml");
    xmlnsGmlDeclare.setValue(Converter.xmlns_gml_namespace_uri);
    rcrMap.setAttributeNode(xmlnsGmlDeclare);

    Attr xmlnsXlinkDeclare=document.createAttribute("xmlns:xlink");
    xmlnsXlinkDeclare.setValue(Converter.xmlns_xlink_namespace_uri);
    rcrMap.setAttributeNode(xmlnsXlinkDeclare);

    WriteNode writeNode = new WriteNode(document,rcrMap);
    document = writeNode.WriteToDocumentNode();

    WriteEdge writeEdge = new WriteEdge(document,rcrMap);
    document = writeEdge.WriteToDocumentEdge();

    WriteBuilding writeBuilding = new WriteBuilding(document,rcrMap);
    document = writeBuilding.WriteToDocumentBuilding();

    WriteRoad writeRoad = new WriteRoad(document,rcrMap);
    document = writeRoad.WriteToDocumentRoad();


    return document;
  }

}
