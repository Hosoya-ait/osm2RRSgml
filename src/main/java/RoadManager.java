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

    //road_node_id_に対応したnodeのArrayを返す
    public ArrayList getRoadNodeList(String road_node_id){
        return tmp_road_list_.get(road_node_id);
    }
    //road_edge_id_に対応したedgeのArrayを返す
    public ArrayList getRoadEdgeList(String road_edge_id){
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
    public ArrayList getMinusDirectionEdgeMap(String road_edge_id){
        return minus_direction_edge_map_.get(road_edge_id);
    }
    //引数のnodeを含んだroadID集合のArrayを返す
    public ArrayList getRoadIDContainNode(String node_id){
        for(int i=0; road_node_id_>i; i++){
            if(tmp_road_list_.get(i).contains(node_id)){
                return tmp_road_list_.get(i);
            }
        }
        return null;
    }
    //引数のedgeを含んだroad_edge_id_の集合のArrayを返す
    public ArrayList getRoadIDContainEdge(String edge_id){
//        System.out.println("edge_id = " + edge_id);
//        System.out.println("road_edge_id_ = " + road_edge_id_);
        for(int i=1; i<=road_edge_id_; i++){
//            System.out.println("road_map_.get(String.valueOf(i)) = " + road_map_.get(String.valueOf(i)));
//            System.out.println("true" + road_map_.get(i));
            if(road_map_.get(String.valueOf(i)).contains(edge_id)){
//                System.out.println("true");
                return road_map_.get(String.valueOf(i));
            }
        }
        return null;
    }

    //
    //道路と建物の接続時に作成する２つのnodeを追加するメソッドを考える
    //


    public void setTmpRoadList(ArrayList nodes){
        road_node_id_++;
        tmp_road_list_.put(String.valueOf(road_node_id_),nodes);
    }
    //内部でマイナスをつけるべきedgeをminus_direction_edge_map_へセットする
    //minus_direction_edge_map_は(roadID,minusにすべきedge集合)
    public void setRoadMap(ArrayList<String> edges){
        road_edge_id_++;
        road_map_.put(String.valueOf(road_edge_id_),edges);

        ArrayList<String> tmp = new ArrayList<>();

//        System.out.println("edges = "+edges);

        for (int i=0; i<edges.size(); i++) {
            if (getRoadIDContainEdge(edges.get(i)) != null) {
//                System.out.println("アアアアアアアアアアアアアアアあ");
                tmp.add(edges.get(i));
            }
        }
//        System.out.println("tmp = " + tmp);
//        System.out.println("road_edge_id_  = " + road_edge_id_);
        minus_direction_edge_map_.put(String.valueOf(road_edge_id_), tmp);

        //建物を反時計回りに作るのであれば，建物で使っているedgeをminusにする処理も必要

        //setMinusDirectionEdgeMapの処理かく
        //別のメソッドとして書いた方が計算量削減になるはず
    }
    public void setMinusDirectionEdgeMap (ArrayList minusEdges) {

    }
}
