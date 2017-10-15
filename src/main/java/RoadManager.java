import java.util.ArrayList;
import java.util.HashMap;

public class RoadManager {
    //roadを構成するnode集を管理
    private static HashMap<String,ArrayList<String>> tmp_road_list_ = new HashMap<String,ArrayList<String>>();
    //roadを構成するedge集を管理
    private static HashMap<String,ArrayList<String>> road_map_ = new HashMap<String,ArrayList<String>>();
    //EdgeManagerのcheckで確認し，マイナスにすべきedgeの管理
    private static HashMap<String,ArrayList<String>> minus_direction_edge_map_ = new HashMap<String,ArrayList<String>>();
    //roadを構成するnodeのIDを新しく一時的に保持する
    private static int road_node_id_ = 1;
    //roadを構成するedgeのIDを新しく一時的に保持する
    private static int road_edge_id_ = 1;

    //road_node_id_に対応したnodeのArrayを返す
    public static ArrayList getRoadNodeList(String road_node_id){
        return tmp_road_list_.get(road_node_id);
    }
    //road_edge_id_に対応したedgeのArrayを返す
    public static ArrayList getRoadEdgeList(String road_edge_id){
        return road_map_.get(road_edge_id);
    }
    //road_node_id_の値を返す
    public static String getRoadNodeID(){
        return String.valueOf(road_node_id_);
    }
    //road_edge_id_の値を返す
    public static String getRoadEdgeID(){
        return String.valueOf(road_edge_id_);
    }
    //マイナスにすべきedge_id_のnodeのArrayを返す
    public static ArrayList getMinusDirectionEdgeMap(String road_edge_id){
        return minus_direction_edge_map_.get(road_edge_id);
    }
    //引数のnodeを含んだroad_node_id_のnodeのArrayを返す
    public static ArrayList getRoadIDContainNode(String node_id){
        for(int i=0; road_node_id_>i; i++){
            if(tmp_road_list_.get(i).contains(node_id)){
                return tmp_road_list_.get(i);
            }
        }
        return null;
    }
    //引数のedgeを含んだroad_edge_id_のedgeのArrayを返す
    public static ArrayList getRoadIDContainEdge(String edge_id){
        for(int i=0; road_edge_id_>i; i++){
            if(road_map_.get(i).contains(edge_id)){
                return road_map_.get(i);
            }
        }
        return null;
    }

    //
    //道路と建物の接続時に作成する２つのnodeを追加するメソッドを考える
    //


    public static void setTmpRoadList(ArrayList nodes){
        tmp_road_list_.put(String.valueOf(road_node_id_++),nodes);
    }
    //内部でマイナスをつけるべきedgeをminus_direction_edge_map_へセットする
    public static void setRoadMap(ArrayList edges){
        road_map_.put(String.valueOf(road_edge_id_++),edges);

        //setMinusDirectionEdgeMapの処理かく
        //別のメソッドとして書いた方が計算量削減になるはず
    }
}
