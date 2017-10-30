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
    //building_node_list_のkeyを1~管理する変数
    private int building_edge_id_ = 0;


    public ArrayList getBuildingNodeList(String building_node_id){
        return building_node_list_.get(building_node_id);
    }
    public ArrayList getBuildingEdgeList(String building_edge_id){
        return building_edge_list_.get(building_edge_id);
    }
    public String getBuildingNodeID(){
        return String.valueOf(building_node_id_);
    }
    public String getBuildingEdgeID(){
        return String.valueOf(building_edge_id_);
    }
    //引数のnodeを含んだbuildingのnode集をArrayで返す
    public ArrayList getBuildingContainNode(String node_id){
        for(int i=1; building_node_id_>=i; i++) {
            if (building_node_list_.get(String.valueOf(i)).contains(node_id)) {
                return building_node_list_.get(String.valueOf(i));
            }
        }
        return null;
    }
    //引数のedgeを含んだbuildingのedge集をArrayで返す
    public ArrayList getBuildingContainEdge(String edge_id){
        for(int i=1; building_edge_id_>=i; i++){
            if(building_edge_list_.get(String.valueOf(i)).contains(edge_id)){
                return building_edge_list_.get(String.valueOf(i));
            }
        }
        return null;
    }

    //道路と建物の接続時に作成する2つのnodeを追加するメソッドを考える

    public void setBuildingNodeList(ArrayList nodes){
        building_node_id_++;
        building_node_list_.put(String.valueOf(building_node_id_),nodes);
    }
    public void setBuildingEdgeList(ArrayList edges){
        building_edge_id_++;
        building_edge_list_.put(String.valueOf(building_edge_id_),edges);
    }
}
