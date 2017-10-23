import java.util.ArrayList;
import java.util.HashMap;

public class EdgeManager {
    //edge_id_は　クラス内部ではint，外部ではStringで扱う
    private static HashMap<String,ArrayList<String>> edge_map_ = new HashMap<String,ArrayList<String>>();
    private static int edge_id_ = 1;

    public static String getEdgeID() {
        return String.valueOf(edge_id_);
    }
    //edge_id_で管理している2点のNodeのArrayを返す
    public static ArrayList getEdgeNodeList(String edge_id) {
        return edge_map_.get(edge_id);
    }
    //引数の2点を利用したedgeが他にあれば，マイナスにする処理のためにfalse返せばいいかな？　確認求む
    public static boolean checkMinusDirectionEdge(String nodeA,String nodeB) {
        for(int i = 1; edge_map_.size()>i; i++){
            if(edge_map_.get(i).contains(nodeA)
                    && edge_map_.get(i).contains(nodeB)){
                return true;
            }
        }
        return false;
    }

    public static void setEdgeMap(ArrayList nodes) {
        edge_map_.put(String.valueOf(edge_id_++), nodes);
    }

}
