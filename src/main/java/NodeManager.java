import java.util.ArrayList;
import java.util.HashMap;

public class NodeManager {
    // ******  要相談事項  ******
    //maxGmlNodeIdをintにしているため，メソッドにて引数の問題が随所で発生している.
    //addGmlNodeとaddOsmNodeについて，
    //  osmIDではなくgmlでのnodeIDでxy座標を管理するように変更したため，
    //  addOsmNodeメソッドの後始末をどうするか.

    //gmlに書き込むnodeIDとx、yの値の対応付け
    private HashMap<String,HashMap<String,Double>> gmlNodeCoordinate = new HashMap<String,HashMap<String,Double>>();
    //osmIDからnodeIDへの対応
    private HashMap<String,String> referenceOsmIDToGmlID = new HashMap<String,String>();
    //nodeIDからosmIDへの対応
    private HashMap<String,String> referenceGmlIDToOsmID = new HashMap<String,String>();
    //gmlに書き込むnodeIDを一時的に保持
    private int maxGmlNodeId = 1;
    //edgeで使用したnodeを保持
    private ArrayList<String> used_node_list_ = new ArrayList<String>();

    //nodeIDからosmIDを取得  引数の型を調べる
    public String getOsmID(String node_id){
        return referenceGmlIDToOsmID.get(node_id);
    }
    //osmIDからnodeIDを取得　引数の型を調べる
    public String getGmlID(String osm_id){
        return referenceOsmIDToGmlID.get(osm_id);
    }
    //nodeIDの示すnodeのx座標
    public double getX(String node_id){ return gmlNodeCoordinate.get(node_id).get("x"); }
    //nodeIDの示すnodeのy座標
    public double getY(String node_id){
        return gmlNodeCoordinate.get(node_id).get("y");
    }
    //nodeがすでに使用されているか調べてboolを返す
    public boolean checkUsedNodeList(String node_id){
        if(used_node_list_.contains(node_id)){
            return true;
        }
        return false;
    }
    public int getNodeSize() {
        return maxGmlNodeId;
    }
    public boolean containsNode(int id){
        return gmlNodeCoordinate.containsKey(String.valueOf(id));
    }

    //gml用のnodeをnodeIDと共に追加
    //引数の型を合わせないと　考えるのはあとで　maxGmlNodeIdの++忘れない
    //osm_idとxyをマップしないことになったため，ここでosmIDとnodeIDを合わせることにした
    public String addGmlNode(String osmNodeId,HashMap nodeCoordinate){
        String gmlNodeId = String.valueOf(maxGmlNodeId);
        gmlNodeCoordinate.put(gmlNodeId, nodeCoordinate);
        referenceOsmIDToGmlID.put(osmNodeId, gmlNodeId);
        referenceGmlIDToOsmID.put(gmlNodeId, osmNodeId);
        maxGmlNodeId++;
        
        return gmlNodeId;
    }
    public String addGmlNode(HashMap nodeXY) {
        String node_id = String.valueOf(maxGmlNodeId);
        gmlNodeCoordinate.put(node_id, nodeXY);
        maxGmlNodeId++;
        
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
