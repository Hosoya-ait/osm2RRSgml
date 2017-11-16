import java.util.ArrayList;
import java.util.HashMap;

public class EdgeManager {
    //新しいedgeIDと新しいnodeIDを対応付ける
    private HashMap<String,ArrayList<String>> edge_map_ = new HashMap<String,ArrayList<String>>();
    //新しいedgeID クラス内部ではint，外部ではStringで扱う
    private int edge_id_ = 0;

    public String getEdgeID() {
        return String.valueOf(edge_id_);
    }
    //edge_id_で管理している2点のNodeのArrayを返す
    public ArrayList getEdgeNodeList(String edge_id) {
        return edge_map_.get(edge_id);
    }

    //2点のosmIDを利用したedgeが他にあれば，マイナスにする処理のためにtrue返す
    //これedgeを構成するnodeのArrayを引数にした方が扱いやすいのでは？
    public boolean checkMinusDirectionEdge(String new_node_id_A,String new_node_id_B) {
        //edge_map_のkeyは1から始まり，edge_map_.size()と同じ値までのidを保持していることに注意
        for (int i=1; edge_map_.size()>=i; i++) {
            String edge_id = String.valueOf(i);
            if (edge_map_.get(edge_id).contains(new_node_id_A) && edge_map_.get(edge_id).contains(new_node_id_B)) {
                return true;
            }
        }
        return false;
    }
//既に作ってあるedgeかどうかcheck用関数　
    public String checkExistEdge(String node1,String node2){
        String tmp_node_ID = "0";
        for (int i=1; i<=edge_id_; i++) {
            ArrayList<String> check_edge_list = getEdgeNodeList(String.valueOf(i));
            if (check_edge_list.contains(node1) && check_edge_list.contains(node2)) {
                tmp_node_ID = String.valueOf(i);
            }
        }
        return tmp_node_ID;
    }
    //edgeを構成する2つの新しいnodeIDを渡して, 新しいedgeIDと対応させる
    public void setEdgeMap(ArrayList new_nodes) {
        edge_id_++;
        edge_map_.put(String.valueOf(edge_id_), new_nodes);

    }

}
