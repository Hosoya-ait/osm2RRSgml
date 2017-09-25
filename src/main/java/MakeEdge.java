import java.util.ArrayList;

public class MakeEdge {

  private static int  tmpEdgeId= 1;
  public MakeEdge(){

    Converter.tmpWayMap.forEach((key,nodes)->{
      //key = String  highway or building
      //nodes = ArrayList
      String tmpNode = new String();
      System.out.println("test1");
      System.out.println(key);
      switch (key) {
        case "highway":
        System.out.println("test2");
        tmpNode = nodes.get(0);
        for (int i=1;i<nodes.size() ; i++) {
            ArrayList tmpList = new ArrayList();
            tmpList.add(tmpNode);
            tmpList.add(nodes.get(i));
            Converter.edgeMap.put(""+tmpEdgeId,tmpList);

            tmpNode = nodes.get(i);
            tmpEdgeId++;
          }
          //RoadMapをかく
          break;

        case "building":
          System.out.println("test2");
          tmpNode = nodes.get(nodes.size()-1);
          for (int i=0;i<nodes.size() ; i++) {
              ArrayList tmpList = new ArrayList();
              tmpList.add(tmpNode);
              tmpList.add(nodes.get(i));
              Converter.edgeMap.put(""+tmpEdgeId,tmpList);
              tmpNode = nodes.get(i);
              tmpEdgeId++;
            }
          //BuildingMapを書く
      }
      tmpEdgeId++;
      System.out.println("test3");
    });
    System.out.println("test4");
    Converter.edgeMap.forEach((id,nodes)->{
      System.out.println("edgeID = "+ id);
      System.out.println("nodeIDMinus = "+ nodes.get(0));
      System.out.println("nodeIDPlus = "+ nodes.get(1));

    });
  }
}
