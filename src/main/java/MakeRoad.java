import java.util.ArrayList;
import java.util.HashMap;

public class MakeRoad{
  private static Double refDistance = 1.5;
  private static int tmpNodeId = 1;
  private static ArrayList checkedNodeList = new ArrayList();
  public MakeRoad(){

    Converter.tmpHighwayList.forEach(nodes -> {
      //nodes = ArrayList
      String tmpNode = new String();

      tmpNode = nodes.get(0);
      for (int i=1;i < nodes.size() ; i++) {

          ArrayList arrayListA = new ArrayList();
          ArrayList arrayListB = new ArrayList();

          String pointA = Converter.linkInverseNodeID.get(tmpNode);
          System.out.println(pointA);
          String pointB = Converter.linkInverseNodeID.get(nodes.get(i));
          System.out.println(pointB);

          if (checkDistance(pointA,pointB)) {
            arrayListA = AddNode(pointA,pointB);
            arrayListB = AddNode(pointB,pointA);
            Converter.tmpRoadList.add(arrayListA);
            Converter.tmpRoadList.add(arrayListB);
            AddSquareTmpRoad(arrayListA,arrayListB);
          }else{
            AddNodeShort(pointA,pointB);
          }
        }
    });

    Converter.tmpHighwayList.forEach(nodes->{
      for (int i = 0; i< nodes.size();i++ ) {
        if (this.checkedNodeList.contains(nodes.get(i))) {

        }else{
          ConnectRoad(nodes.get(i));
          this.checkedNodeList.add(nodes.get(i));
        }
      }

    });

  }

  private ArrayList AddNode(String nodeNumA,String nodeNumB){

    ArrayList array = new ArrayList();
    array.add(Converter.linkNodeID.get(nodeNumA));


    HashMap<String,Double> map1 = new HashMap<String,Double>();
    HashMap<String,Double> map2 = new HashMap<String,Double>();

    HashMap tmpMap = Converter.nodeMap.get(nodeNumA);

    double pointAx = (double)tmpMap.get("x");
    double pointAy = (double)tmpMap.get("y");

    tmpMap = Converter.nodeMap.get(nodeNumB);

    double pointBx = (double)tmpMap.get("x");
    double pointBy = (double)tmpMap.get("y");

    double difPointX = pointBx - pointAx;
    double difPointY = pointBy - pointAy;

    double radian = Math.atan2(difPointY,difPointX);
    double degree = (radian*180)/Math.PI;

    double tmpX = Math.cos(radian)*refDistance*Math.sqrt(3);
    double tmpY = Math.sin(radian)*refDistance*Math.sqrt(3);

    System.out.println("X="+tmpX);
    System.out.println("Y="+tmpY);

    Double radian1 = ((degree-90)*Math.PI)/180.0;
    Double radian2 = ((degree+90)*Math.PI)/180.0;

    Double point1x = Math.cos(radian1)*refDistance+tmpX+pointAx;
    Double point1y = Math.sin(radian1)*refDistance+tmpY+pointAy;

    Double point2x = Math.cos(radian2)*refDistance+tmpX+pointAx;
    Double point2y = Math.sin(radian2)*refDistance+tmpY+pointAy;

    map1.put("y",point1y);
    map1.put("x",point1x);

    map2.put("y",point2y);
    map2.put("x",point2x);

    while(Converter.nodeMap.containsKey(""+this.tmpNodeId)){
      this.tmpNodeId++;
    }

    Converter.nodeMap.put(""+this.tmpNodeId,map1);
    Converter.linkNodeID.put(""+this.tmpNodeId,""+Converter.nodeMap.size());
    Converter.linkInverseNodeID.put(""+Converter.nodeMap.size(),""+this.tmpNodeId);

    array.add(""+Converter.nodeMap.size());

    this.tmpNodeId++;

    while(Converter.nodeMap.containsKey(""+this.tmpNodeId)){
      this.tmpNodeId++;
    }

    Converter.nodeMap.put(""+this.tmpNodeId,map2);
    Converter.linkNodeID.put(""+this.tmpNodeId,""+Converter.nodeMap.size());
    Converter.linkInverseNodeID.put(""+Converter.nodeMap.size(),""+this.tmpNodeId);

    array.add(""+Converter.nodeMap.size());

    this.tmpNodeId++;

    array.add(Converter.linkNodeID.get(nodeNumA));

    return array;

  }


  private Boolean checkDistance(String nodeNumA,String nodeNumB){
    HashMap tmpMap = Converter.nodeMap.get(nodeNumA);

    double pointAx = (double)tmpMap.get("x");
    double pointAy = (double)tmpMap.get("y");

    tmpMap = Converter.nodeMap.get(nodeNumB);

    double pointBx = (double)tmpMap.get("x");
    double pointBy = (double)tmpMap.get("y");

    double distance = Math.sqrt(Math.pow((pointBx - pointAx),2) + Math.pow((pointBy - pointAy),2));

    if( distance > refDistance*2 ){
      return true;
    }else{
      return false;
    }

  }

  private void AddSquareTmpRoad(ArrayList arrayA,ArrayList arrayB){
    ArrayList array = new ArrayList();
    array.add(arrayA.get(1));
    array.add(arrayA.get(2));
    array.add(arrayB.get(1));
    array.add(arrayB.get(2));
    array.add(arrayA.get(1));
    Converter.tmpRoadList.add(array);
  }

  private void AddNodeShort(String nodeNumA,String nodeNumB){
    //highwayのノード二つが十分に離れていない場合
    ArrayList arrayA = new ArrayList();
    arrayA.add(Converter.linkNodeID.get(nodeNumA));

    ArrayList arrayB = new ArrayList();
    arrayB.add(Converter.linkNodeID.get(nodeNumB));

    String tmpPointNode;

    HashMap<String,Double> map1 = new HashMap<String,Double>();
    HashMap<String,Double> map2 = new HashMap<String,Double>();

    HashMap tmpMap = Converter.nodeMap.get(nodeNumA);

    double pointAx = (double)tmpMap.get("x");
    double pointAy = (double)tmpMap.get("y");

    tmpMap = Converter.nodeMap.get(nodeNumB);

    double pointBx = (double)tmpMap.get("x");
    double pointBy = (double)tmpMap.get("y");

    double difPointX = pointBx - pointAx;
    double difPointY = pointBy - pointAy;

    double radian = Math.atan2(difPointY,difPointX);
    double degree = (radian*180)/Math.PI;

    double halfDistance = Math.sqrt(Math.pow((pointBx - pointAx),2) + Math.pow((pointBy - pointAy),2))/2;

    double tmpX = Math.cos(radian)*halfDistance*Math.sqrt(3);
    double tmpY = Math.sin(radian)*halfDistance*Math.sqrt(3);

    System.out.println("X="+tmpX);
    System.out.println("Y="+tmpY);

    Double radian1 = ((degree-90)*Math.PI)/180.0;
    Double radian2 = ((degree+90)*Math.PI)/180.0;

    Double point1x = Math.cos(radian1)*refDistance+tmpX;
    Double point1y = Math.sin(radian1)*refDistance+tmpY;

    Double point2x = Math.cos(radian2)*refDistance+tmpX;
    Double point2y = Math.sin(radian2)*refDistance+tmpY;

    map1.put("y",point1y);
    map1.put("x",point1x);

    map2.put("y",point2y);
    map2.put("x",point2x);

    while(Converter.nodeMap.containsKey(""+this.tmpNodeId)){
      this.tmpNodeId++;
    }

    Converter.nodeMap.put(""+this.tmpNodeId,map1);
    Converter.linkNodeID.put(""+this.tmpNodeId,""+Converter.nodeMap.size());
    Converter.linkInverseNodeID.put(""+Converter.nodeMap.size(),""+this.tmpNodeId);

    arrayA.add(""+Converter.nodeMap.size());
    tmpPointNode = ""+Converter.nodeMap.size();


    this.tmpNodeId++;

    while(Converter.nodeMap.containsKey(""+this.tmpNodeId)){
      this.tmpNodeId++;
    }

    Converter.nodeMap.put(""+this.tmpNodeId,map2);
    Converter.linkNodeID.put(""+this.tmpNodeId,""+Converter.nodeMap.size());
    Converter.linkInverseNodeID.put(""+Converter.nodeMap.size(),""+this.tmpNodeId);

    arrayA.add(""+Converter.nodeMap.size());
    arrayB.add(""+Converter.nodeMap.size());

    this.tmpNodeId++;


    arrayB.add(tmpPointNode);

    arrayA.add(Converter.linkNodeID.get(nodeNumA));
    arrayB.add(Converter.linkNodeID.get(nodeNumB));

    Converter.tmpRoadList.add(arrayA);
    Converter.tmpRoadList.add(arrayB);

  }
  private void ConnectRoad(String node){

  }
}
