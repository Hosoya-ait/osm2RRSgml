import java.util.ArrayList;

public class MakeEdge {

  private static int  tmpEdgeId= 1;
  private static int tmpBuildingId = 1;
    private static int tmpRoadId = 1;

  private static String tmpNodeID = new String();
  public MakeEdge(){

    Converter.tmpBuildingList.forEach(nodes -> {
      //nodes = ArrayList
      String tmpNode = new String();
      ArrayList<String> tmpEdgeList = new ArrayList<String>();
      tmpNode = nodes.get(0);
      for (int i=1;i<nodes.size() ; i++) {
        ArrayList tmpNodeList = new ArrayList();

        tmpNodeList.add(tmpNode);
        tmpNodeList.add(nodes.get(i));
        Converter.edgeMap.put(""+tmpEdgeId,tmpNodeList);
        tmpEdgeList.add(""+tmpEdgeId);
        tmpNode = nodes.get(i);
        tmpEdgeId++;
      }
      //BuildingMapを書く
      Converter.buildingMap.put(""+tmpBuildingId,tmpEdgeList);

      tmpBuildingId++;
    });

    Converter.tmpRoadList.forEach(nodes -> {
      //nodes = ArrayList
      String tmpNode = new String();
      ArrayList<String> tmpEdgeList = new ArrayList<String>();
      tmpNode = nodes.get(0);
      for (int i=1;i<nodes.size() ; i++) {
        ArrayList tmpNodeList = new ArrayList();
        String checkEdgeId = new String();

        tmpNodeList.add(tmpNode);
        tmpNodeList.add(nodes.get(i));
        //すでにあるEdgeか判定
        checkEdgeId = checkEdge(tmpNode,nodes.get(i));

        if (checkEdgeId == "0") {
          Converter.edgeMap.put(""+tmpEdgeId,tmpNodeList);
          tmpEdgeList.add(""+tmpEdgeId);
          tmpEdgeId++;
        }else{
          tmpEdgeList.add(""+checkEdgeId);
        }
        tmpNode = nodes.get(i);
      }
      Converter.roadMap.put(""+tmpRoadId,tmpEdgeList);

      tmpRoadId++;
      //RoadMapを書く
    });

  }
  private String checkEdge(String node1,String node2){
    this.tmpNodeID = "0";
    Converter.edgeMap.forEach((id,edgeNodes)->{

      if (edgeNodes.get(0) == node1) {
        if (edgeNodes.get(1) == node2) {
           this.tmpNodeID = id;
        }
      }else if(edgeNodes.get(1) == node1){
        if (edgeNodes.get(0) == node2) {
          this.tmpNodeID =  id;
        }
      }
    });
    return this.tmpNodeID;
  }
}
