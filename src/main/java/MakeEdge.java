import java.util.ArrayList;

public class MakeEdge {

  private static int  tmpEdgeId= 1;
  private static int tmpBuildingId = 1;
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

      tmpNode = nodes.get(0);
      for (int i=1;i<nodes.size() ; i++) {
          ArrayList tmpList = new ArrayList();
          tmpList.add(tmpNode);
          tmpList.add(nodes.get(i));
          Converter.edgeMap.put(""+tmpEdgeId,tmpList);
          tmpNode = nodes.get(i);
          tmpEdgeId++;
        }
      //RoadMapを書く
    });

  }
}
