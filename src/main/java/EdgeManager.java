import java.util.ArrayList;
import java.util.HashMap;

public class EdgeManager {
    //新しいedgeIDと新しいnodeIDを対応付ける
    private static HashMap<String,ArrayList<String>> edge_map_ = new HashMap<String,ArrayList<String>>();
    //新しいedgeID クラス内部ではint，外部ではStringで扱う
    private static int edge_id_ = 1;

    public static String getEdgeID() {
        return String.valueOf(edge_id_);
    }
    //edge_id_で管理している2点のNodeのArrayを返す
    public static ArrayList getEdgeNodeList(String edge_id) {
        return edge_map_.get(edge_id);
    }

    //引数の2点を利用したedgeが他にあれば，マイナスにする処理のためにtrue返す
    public static boolean checkMinusDirectionEdge(String nodeA,String nodeB) {
        for (int i=1; edge_map_.size()>i; i++) {
            if (edge_map_.get(i).contains(nodeA) && edge_map_.get(i).contains(nodeB)) {
                return true;
            }
        }
        return false;
    }

    //edgeを構成する2つの新しいnodeIDを渡して, 新しいedgeIDと対応させる
    public static void setEdgeMap(ArrayList nodes) {
        edge_map_.put(String.valueOf(edge_id_++), nodes);
    }
}
