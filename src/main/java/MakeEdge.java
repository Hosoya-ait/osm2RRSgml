import java.util.ArrayList;

//edge作成用クラス
public class MakeEdge {

    private NodeManager     nm;
    private EdgeManager     em;
    private BuildingManager bm;
    private RoadManager     rm;

    public MakeEdge(NodeManager     nm,
                    EdgeManager     em,
                    BuildingManager bm,
                    RoadManager     rm){
        this.nm = nm;
        this.em = em;
        this.bm = bm;
        this.rm = rm;
    }

    public void makeNodeToEdge () {
        int building_highest_num =  Integer.parseInt(bm.getBuildingNodeID());
        for (int i=1; i<=building_highest_num; i++) {
            if (bm.containRemoveBuildingList(String.valueOf(i))==false) {
              ArrayList<String> write_nodebuilding = bm.getBuildingNodeList(""+i);
              ArrayList<String> edge_list_to_make_building = makeEdge(write_nodebuilding);
              bm.setBuildingEdgeList(edge_list_to_make_building);
            }

        }

        int road_highest_num = Integer.parseInt(rm.getRoadNodeID());
        for (int i=1; i<=road_highest_num; i++){
            if (rm.containRemoveRoadList(String.valueOf(i)) == false) {
              ArrayList<String> write_noderoad = rm.getRoadNodeList(""+i);
              ArrayList<String> edge_list_to_make_road = makeEdge(write_noderoad);
              rm.setRoadMap(edge_list_to_make_road);
            }

        }
    }

//渡されたnodeの入っているlistに対して入ってる順番にedgeを作る、また作ったedgeのlistを返す
    private ArrayList<String> makeEdge(ArrayList<String> write_node){
        String tmp_node = write_node.get(0);
        ArrayList<String> object_edge_list = new ArrayList<String>();
        for (int j=1; j<write_node.size(); j++) {
            ArrayList<String> node_List_to_make_edge = new ArrayList<String>();
            String next_tmp_node = write_node.get(j);

            node_List_to_make_edge.add(tmp_node);
            node_List_to_make_edge.add(next_tmp_node);
            if (nm.containsNode(j)) {
                //System.out.println("存在しているnode");
            }else{
                //System.out.println("存在しないnode");
            }

            nm.setUsedNodeList(tmp_node);
            nm.setUsedNodeList(next_tmp_node);

            //すでにあるEdgeか判定
            String check_edge_ID = new String();
            check_edge_ID = em.checkExistEdge(tmp_node, next_tmp_node);
            if (check_edge_ID == "0") {
                em.setEdgeMap(node_List_to_make_edge);
                object_edge_list.add(em.getEdgeID());
            }else{
                object_edge_list.add(check_edge_ID);
            }
            tmp_node = next_tmp_node;
        }
        return object_edge_list;
    }
}
