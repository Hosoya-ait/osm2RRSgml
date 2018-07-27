import java.util.ArrayList;
import java.util.HashMap;

public class RoadManager {
    //roadを構成するnode集を管理
    private HashMap<String,ArrayList<String>> tmp_road_list_ = new HashMap<String,ArrayList<String>>();
    //roadを構成するedge集を管理
    private HashMap<String,ArrayList<String>> road_map_ = new HashMap<String,ArrayList<String>>();
    //EdgeManagerのcheckで確認し，マイナスにすべきedgeの管理
    private HashMap<String,ArrayList<String>> minus_direction_edge_map_ = new HashMap<String,ArrayList<String>>();
    //roadを構成するnodeのIDを新しく一時的に保持する
    private int road_node_id_ = 0;
    //edgeで構成されているroadのIDを新しく一時的に保持する
    private int road_edge_id_ = 0;
//    //minusのedgesを管理する用のid
//    private int minus_edges_id_ = 0;
    private HashMap<String,ArrayList<String>> road_connected_road_list_ = new HashMap<String,ArrayList<String>>();
    private HashMap<String,ArrayList<String>> road_connected_building_list_ = new HashMap<String,ArrayList<String>>();



    private ArrayList<String> remove_road_ = new ArrayList<String>();

    //road_node_id_に対応したnodeのArrayを返す
    public ArrayList<String> getRoadNodeList(String road_node_id){
        return tmp_road_list_.get(road_node_id);
    }
    //road_edge_id_に対応したedgeのArrayを返す
    public ArrayList<String> getRoadEdgeList(String road_edge_id){
        return road_map_.get(road_edge_id);
    }
    //road_node_id_の値を返す
    public String getRoadNodeID(){
        return String.valueOf(road_node_id_);
    }
    //road_edge_id_の値を返す
    public String getRoadEdgeID(){
        return String.valueOf(road_edge_id_);
    }
    //マイナスにすべきedge_id_のnodeのArrayを返す
    public ArrayList<String> getMinusDirectionEdgeMap(String road_edge_id){
        return minus_direction_edge_map_.get(road_edge_id);
    }
    //引数のnodeを含んだroadID集合のArrayを返す
    public ArrayList<String> getRoadIDContainNode(String node_id){
        for(int i=0; road_node_id_>i; i++){
            if(tmp_road_list_.get(i).contains(node_id)){
                return tmp_road_list_.get(i);
            }
        }
        return null;
    }
    //引数のedgeを含んだroad_edge_id_の集合のArrayを返す
    //機能を変更
    //edge_id = 拡張道路を構成するedge集合の1辺
    //調査対象である拡張道路の1辺が含まれている,拡張道路を構成するedge集合を返す関数
    public ArrayList<String> getRoadIDContainEdge(String road_id,String edge_id){
        //調査対象であるedge作成された拡張道路以外のedge作成された拡張道路を回す
        for(int i=1; i<=road_edge_id_-1; i++){
            //拡張道路を構成するedge集合を取得
            ArrayList<String> check_list = road_map_.get(String.valueOf(i));
            //拡張道路を構成するedge集合の中に調査対象である拡張道路の1辺が含まれている場合
            if(check_list.contains(edge_id)){
                //調査対象である拡張道路の1辺が含まれている,拡張道路を構成するedge集合を返す
                return road_map_.get(String.valueOf(i));
            }
        }
        return null;
    }

    public Boolean containMinusDirectionEdge(String road_id,String edge_id ){
        if (minus_direction_edge_map_.containsKey(road_id)) {
            ArrayList<String> tmp_check_list = minus_direction_edge_map_.get(road_id);
            if (tmp_check_list.contains(edge_id)) {
                return true;
            }
        }
        return false;
    }

    public Boolean containRemoveRoadList(String road_id){
        if (remove_road_.contains(road_id)) {
          //System.out.println("除外");
          return true;
        }
        return false;
    }
    //
    //道路と建物の接続時に作成する２つのnodeを追加するメソッドを考える
    //
    public void setTmpRoadList(ArrayList<String> nodes){
        road_node_id_++;
        tmp_road_list_.put(String.valueOf(road_node_id_),nodes);
    }
    //内部でマイナスをつけるべきedgeをminus_direction_edge_map_へセットする
    //minus_direction_edge_map_は(roadID,minusにすべきedge集合)
    //edges = 拡張道路を構成するedge集合
    public void setRoadMap(ArrayList<String> edges){
        road_edge_id_++;
        road_map_.put(String.valueOf(road_edge_id_),edges);
        //
        setMinusDirectionEdgeMap (edges);
    }

    //既に存在しているedgeを使用したとき書き込み時にminusにするようにする処理
    //check_Edges = 拡張道路を構成するedge集合
    public void setMinusDirectionEdgeMap (ArrayList<String> check_Edges) {
        ArrayList<String> tmp_list = new ArrayList<String>();
        //拡張道路を構成するedge集合を回す
        for (int i=0; i<check_Edges.size(); i++) {
            //拡張道路を構成する辺を含む拡張道路が他にある場合
            if (getRoadIDContainEdge( String.valueOf(road_edge_id_),check_Edges.get(i)) != null ) {
                //他の拡張道路に含まれていることがわかっている1辺を保持する
                tmp_list.add(check_Edges.get(i));
            }
        }
        //対象の拡張道路のidと逆方向にするべき辺のリストの対応を登録
        minus_direction_edge_map_.put(String.valueOf(road_edge_id_), tmp_list);
        //建物を反時計回りに作るのであれば，建物で使っているedgeをminusにする処理も必要
    }


    public void insertRoadInNode(String road_ID,int road_edge_Index,String node_ID){
        ((ArrayList)tmp_road_list_.get(road_ID)).add(road_edge_Index,node_ID);
        return;
    }

    public void setRemoveRoadList(String road_id){
      remove_road_.add(road_id);
    }



    public void setRoadConnectedObject(String connected_road,String road_id,String building_id){
      if (checkRoadConnected(connected_road)) {
        ((ArrayList)road_connected_road_list_.get(connected_road)).add(road_id);
        ((ArrayList)road_connected_building_list_.get(connected_road)).add(building_id);
      }else{
        ArrayList<String> tmp_array_road = new ArrayList<String>();
        ArrayList<String> tmp_array_building = new ArrayList<String>();
        tmp_array_road.add(road_id);
        tmp_array_building.add(building_id);

        road_connected_road_list_.put(connected_road,tmp_array_road);
        road_connected_building_list_.put(connected_road,tmp_array_building);
      }
    }

    public Boolean checkRoadConnected(String road_id){
      return road_connected_road_list_.containsKey(road_id);
    }

    public ArrayList<String> getRoadConnectedRoad(String road_id){
      if (checkRoadConnected(road_id)) {
        return road_connected_road_list_.get(road_id);
      }else{
        ArrayList<String> empty_arr = new ArrayList<String>();
        return empty_arr;
      }

    }
    public ArrayList<String>  getRoadConnectedBuilding(String road_id){
      if (checkRoadConnected(road_id)) {
        return road_connected_building_list_.get(road_id);
      }else{
        ArrayList<String> empty_arr = new ArrayList<String>();
        return empty_arr;
      }

    }
}
