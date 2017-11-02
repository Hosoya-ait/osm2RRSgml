import java.util.ArrayList;
import java.util.HashMap;

public class NodeManager {
    // ******  要相談事項  ******
    //node_id_をintにしているため，メソッドにて引数の問題が随所で発生している.
    //addGmlNodeとaddOsmNodeについて，
    //  osmIDではなくgmlでのnodeIDでxy座標を管理するように変更したため，
    //  addOsmNodeメソッドの後始末をどうするか.

    //gmlに書き込むnodeIDとx、yの値の対応付け
    private HashMap<String,HashMap<String,Double>> gml_node_map_ = new HashMap<String,HashMap<String,Double>>();
    //osmIDからnodeIDへの対応
    private HashMap<String,String> osm_to_gml_id_ = new HashMap<String,String>();
    //nodeIDからosmIDへの対応
    private HashMap<String,String> gml_to_osm_id_ = new HashMap<String,String>();
    //gmlに書き込むnodeIDを一時的に保持
    private int node_id_ = 1;
    //edgeで使用したnodeを保持
    private ArrayList<String> used_node_list_ = new ArrayList<String>();

    //nodeIDからosmIDを取得  引数の型を調べる
    public String getOsmID(String node_id){
        return gml_to_osm_id_.get(node_id);
    }
    //osmIDからnodeIDを取得　引数の型を調べる
    public String getGmlID(String osm_id){
        return osm_to_gml_id_.get(osm_id);
    }
    //nodeIDの示すnodeのx座標
    public double getX(String node_id){
        return gml_node_map_.get(node_id).get("x");
    }
    //nodeIDの示すnodeのy座標
    public double getY(String node_id){
        return gml_node_map_.get(node_id).get("y");
    }
    //nodeがすでに使用されているか調べてboolを返す
    public boolean checkUsedNodeList(String node_id){
        if(used_node_list_.contains(node_id)){
            return true;
        }
        return false;
    }
    public int getNodeSize() {
        return node_id_;
    }

    //gml用のnodeをnodeIDと共に追加
    //引数の型を合わせないと　考えるのはあとで　node_id_の++忘れない
    //osm_idとxyをマップしないことになったため，ここでosmIDとnodeIDを合わせることにした
    public String addGmlNode(String osm_id,HashMap nodeXY){
        String node_id = String.valueOf(node_id_++);
        gml_node_map_.put(node_id, nodeXY);

        osm_to_gml_id_.put(osm_id, node_id);
        gml_to_osm_id_.put(node_id, osm_id);

        return node_id;
    }
    public String addGmlNode(HashMap nodeXY) {
        String node_id = String.valueOf(node_id_++);
        gml_node_map_.put(node_id, nodeXY);

        return node_id;
    }
    //osmのあるnodeのx,yの座標情報をnodeIDに流すはずだったが，
    //新クラス構造案にてxy管理はgmlNodeMapのみなので、IDの関連付けのみが残る
    //中身の処理は”addGmlNode”メソッドに移した↑
    public void addOsmNode(String osm_id){

    }
    //nodeを使用済みとみなす
    //引数の型をintのままにするか
    public void setUsedNodeList(String node_id){
        used_node_list_.add(node_id);
    }

}
