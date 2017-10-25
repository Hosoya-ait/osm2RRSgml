import java.util.ArrayList;
import java.util.HashMap;

public class EdgeManager {
    //新しいedgeIDと新しいnodeIDを対応付ける
    private static HashMap<String,ArrayList<String>> edge_map_ = new HashMap<String,ArrayList<String>>();
    //新しいedgeID クラス内部ではint，外部ではStringで扱う
    private static int edge_id_ = 0;

    public static String getEdgeID() { return String.valueOf(edge_id_); }
    //edge_id_で管理している2点のNodeのArrayを返す
    public static ArrayList getEdgeNodeList(String edge_id) { return edge_map_.get(edge_id); }

    //2点のosmIDを利用したedgeが他にあれば，マイナスにする処理のためにtrue返す
    //これedgeを構成するnodeのArrayを引数にした方が扱いやすいのでは？
    public static boolean checkMinusDirectionEdge(String nodeA,String nodeB) {
        //edge_map_のkeyは1から始まり，edge_map_.size()と同じ値までのidを保持していることに注意
        for (int i=1; edge_map_.size()>=i; i++) {
            String edge_id = String.valueOf(i);
            if (edge_map_.get(edge_id).contains(nodeA) && edge_map_.get(edge_id).contains(nodeB)) {
                return true;
            }
        }
        return false;
    }

    //edgeを構成する2つの新しいnodeIDを渡して, 新しいedgeIDと対応させる
    public static void setEdgeMap(ArrayList new_nodes) {
        edge_id_++;
        edge_map_.put(String.valueOf(edge_id_), new_nodes);
    }
}
