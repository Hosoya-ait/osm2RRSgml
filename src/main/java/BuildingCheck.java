//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;
import java.awt.geom.Point2D;

public class BuildingCheck{
//  private Document document;
//  private Element rcrMap;

  private static String tmpEdgeID = new String();
  private static String tmpNodeID = new String();

  private NodeManager     nm;
  private EdgeManager     em;
  private BuildingManager bm;
  private RoadManager     rm;

  public BuildingCheck(
                       NodeManager     nm,
                       EdgeManager     em,
                       BuildingManager bm,
                       RoadManager     rm){
    //  this.document = doc;
    //  this.rcrMap = rcr;
      this.nm = nm;
      this.em = em;
      this.bm = bm;
      this.rm = rm;
      System.out.println("buildmingcheck");
      CheckBuilding();
    }


    public void CheckBuilding(){
      //lは座標を出すための一時的な値とする
      int l = 0;
      //System.out.println("string" + bm.getBuildingNodeID());
      String tmp =  bm.getBuildingNodeID();
      //System.out.println("int" + Integer.parseInt(tmp));
      for (int k=1; k<= Integer.parseInt(tmp); k++) {
        //building listから初期の値を取得する
        System.out.println("現在の建物= " + k);
        for (int j=0; j<bm.getBuildingEdgeList(String.valueOf(k)).size(); j++) {
          int hrefValue = nm.getNodeSize() + Integer.parseInt((String)bm.getBuildingEdgeList(String.valueOf(k)).get(j));
          System.out.println("値1 " + hrefValue);

          //上で取得された値を利用し（hrefValue）取得した値を使用してgml:idから座標を導くidを取得する
          String set_edge_ID = ""+(hrefValue);
          System.out.println("値2= " +  set_edge_ID);

          l = l+1;
          ArrayList<String> write_gml_edge = em.getEdgeNodeList(String.valueOf(l));
          System.out.println("値3= " + write_gml_edge.get(0));
          String num = write_gml_edge.get(0);

          double nm_x = nm.getX(String.valueOf(num));
          double nm_y = nm.getY(String.valueOf(num));


          System.out.println("x座標" +  String.format("%3.3f",nm_x) + " y座標" + String.format("%3.3f",nm_y));
        }//l46
      }
    }



}//l6
