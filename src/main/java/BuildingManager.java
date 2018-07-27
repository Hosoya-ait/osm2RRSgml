import java.util.ArrayList;
import java.util.HashMap;

public class BuildingManager {
    //building_node_id_,building_edge_id_はこのクラス内部ではint,外部ではStringで扱う

    //buildig_id_とnode_id_の対応
    private HashMap<String,ArrayList<String>> building_node_list_ = new HashMap<String,ArrayList<String>>();
    //building_id_とedge_id_の対応
    private HashMap<String,ArrayList<String>> building_edge_list_ = new HashMap<String,ArrayList<String>>();
    //building_node_list_のkeyを1~管理する変数
    private int building_node_id_ = 0;
    //building_edge_list_のkeyを1~管理する変数
    private int building_edge_id_ = 0;

    private HashMap<String,ArrayList<String>> building_connected_road_list_ = new HashMap<String,ArrayList<String>>();
    private HashMap<String,ArrayList<String>> building_connected_building_list_ = new HashMap<String,ArrayList<String>>();

    private ArrayList<String> remove_building_ = new ArrayList<String>();

    //edgeが作られた建物と逆方向にするedge集合の対応をまとめる変数
    private HashMap<String,ArrayList<String>> reverseEdgeMap = new HashMap<String,ArrayList<String>>();



    public ArrayList<String> getBuildingNodeList(String building_node_id){
        return building_node_list_.get(building_node_id);
    }
    public ArrayList<String> getBuildingEdgeList(String building_edge_id){
        return building_edge_list_.get(building_edge_id);
    }
    public String getBuildingNodeID(){
        return String.valueOf(building_node_id_);
    }
    public String getBuildingEdgeID(){
        return String.valueOf(building_edge_id_);
    }
    //引数のnodeを含んだbuildingの集合をArrayで返す
    public ArrayList getBuildingContainNode(String node_id){
        for(int i=1; building_node_id_>=i; i++) {
            if (building_node_list_.get(String.valueOf(i)).contains(node_id)) {
                return building_node_list_.get(String.valueOf(i));
            }
        }
        return null;
    }
    //引数のedgeを含んだbuildingの集合をArrayで返す
    public ArrayList getBuildingContainEdge(String edge_id){
        for(int i=1; building_edge_id_>=i; i++){
            if(building_edge_list_.get(String.valueOf(i)).contains(edge_id)){
                return building_edge_list_.get(String.valueOf(i));
            }
        }
        return null;
    }

    public Boolean containRemoveBuildingList(String building_id){
        if (remove_building_.contains(building_id)) {
          System.out.println("除外");
          return true;
        }
        return false;
    }

    //道路と建物の接続時に作成する2つのnodeを追加するメソッドを考える

    public void setBuildingNodeList(ArrayList<String> nodes){
        building_node_id_++;
        building_node_list_.put(String.valueOf(building_node_id_),nodes);
    }
    public void setBuildingEdgeList(ArrayList<String> edges){
        building_edge_id_++;
        building_edge_list_.put(String.valueOf(building_edge_id_),edges);
        //edgeを作成した建物の各辺に対して、すでにedge作成された建物のどれかの辺と同じ場合、
        //その辺(edgesの1辺)を逆方向に登録する関数
        setReverseEdge(String.valueOf(building_edge_id_),edges);
    }
    public void insertBuildingInNode(String building_ID,int building_edge_Index,String node_ID){
        ((ArrayList)building_node_list_.get(building_ID)).add(building_edge_Index,node_ID);
        return;
    }

    public void setRemoveBuildingList(String building_id){
      remove_building_.add(building_id);
    }


    public void setBuildingConnectedObject(String connected_building,String road_id,String building_id){
      if (checkBuildingConnected(connected_building)) {
        ((ArrayList)building_connected_road_list_.get(connected_building)).add(road_id);
        ((ArrayList)building_connected_building_list_.get(connected_building)).add(building_id);
      }else{
        ArrayList<String> tmp_array_road = new ArrayList<String>();
        ArrayList<String> tmp_array_building = new ArrayList<String>();
        tmp_array_road.add(road_id);
        tmp_array_building.add(building_id);

        building_connected_road_list_.put(connected_building,tmp_array_road);
        building_connected_building_list_.put(connected_building,tmp_array_building);
      }
    }

    //調べる建物のedgeのどれかが他のすでにedgeが作られている建物の1辺と同じ場合は、
    //調べているedgeを逆方向にするリストへ追加する関数
    //引数はedgeの作られた建物のid（1~）,その建物を構成するedge集合
    private void setReverseEdge(String buildingID,ArrayList<String> edges){
        ArrayList<String> reverseEdgeList = new ArrayList<String>();
        //ここはedgeが作られた建物数を取得
        int buildingEdgeNum = Integer.parseInt(getBuildingEdgeID());
        //edgeが作られた建物分回す
        for(int i=1;i<buildingEdgeNum;i++){
            //edgeが作られた建物のedge集合を取得　これ関数名がややこしい
            ArrayList<String> checkList = getBuildingEdgeList(String.valueOf(i));
            //引数で得られている、建物のedge集合を回す
            for(int k = 0;k<edges.size();k++){
                //引数で得られている建物の1edgeとすでにある建物のedgeとが同じである場合
                if(checkList.contains(edges.get(k))){
                    //逆にするedgeとしてリストに一時的に保持
                    reverseEdgeList.add(edges.get(k));
                }
            }
        }
        //調べている建物idと逆方向にするedgeリストの対応を登録
        reverseEdgeMap.put(buildingID,reverseEdgeList);

    }

    public Boolean containReverseEdge(String buildingID,String edgeID){
        ArrayList checkList = reverseEdgeMap.get(buildingID);
        if(checkList.contains(edgeID)) return true;
        return false;
    }

    public Boolean checkBuildingConnected(String building_id){
      return building_connected_road_list_.containsKey(building_id);
    }

    public ArrayList<String> getBuildingConnectedRoad(String building_id){
      if (checkBuildingConnected(building_id)) {
        return building_connected_road_list_.get(building_id);
      }else{
        ArrayList<String> empty_arr = new ArrayList<String>();
        return empty_arr;
      }

    }
    public ArrayList<String>  getBuildingConnectedBuilding(String building_id){
      if (checkBuildingConnected(building_id)) {
        return building_connected_building_list_.get(building_id);
      }else{
        ArrayList<String> empty_arr = new ArrayList<String>();
        return empty_arr;
      }

    }

}
