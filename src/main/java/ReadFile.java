import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

public class ReadFile {
    ReadFile(Document document){
      Node osmNode = document.getDocumentElement();//[4]
      Node elementNodes = osmNode.getFirstChild();//[5]

      while(elementNodes != null) {//[6]
          String elementNodesNodeName = elementNodes.getNodeName();//[7]
          if (elementNodesNodeName == "way") {
            break;
          }
          switch (elementNodesNodeName) {//[8]
              case "node"://[9]
                  printNode(elementNodesNodeName, elementNodes);//[10]
                  printAttribute("id", elementNodes);

                  break;//[12]
              case "way"://[13]
                  Node itemNodes = elementNodes.getFirstChild();//[14]
                  while(itemNodes != null) {//[15]
                      printNode(elementNodesNodeName, itemNodes);//[16
                      itemNodes = itemNodes.getNextSibling();//[17]
                  }
                  break;//[18]
          }
          elementNodes = elementNodes.getNextSibling();//[19]
      }
    }

    private static void printNode(String nodeName, Node node) {//[30]
        Node textNode = node.getFirstChild();//[31]
        if (checkTextNode(textNode)) {//[32]
            System.out.println("[33] ノード = " + nodeName);
            System.out.println("[34] テキストノード = " + textNode.getNodeValue());
        }
    }
    private static void printAttribute(String id, Node node) {//[40]
        NamedNodeMap attributes = node.getAttributes();//[41]
        if (attributes!=null) {//[42]
            HashMap<String,Double> map = new HashMap<String,Double>();

            Node attributeId = attributes.getNamedItem(id);//[43]
            Node attributeLat = attributes.getNamedItem("lat");//[43]
            Node attributeLon = attributes.getNamedItem("lon");//[43]

            map.put("lat",Double.parseDouble(attributeLat.getNodeValue()));
            map.put("lon",Double.parseDouble(attributeLon.getNodeValue()));

            Converter.nodeMap.put(Long.parseLong(attributeId.getNodeValue()),map);
        }
    }
    private static boolean checkTextNode(Node node) {//[50]
        if (node == null)   return false;//[51]
        if (node.getNodeType() == Node.TEXT_NODE)//[52]
            return true;//[53]
        return false;//[54]
    }
}
