import java.util.ArrayList;
import java.util.HashMap;

public class Test_BuildingManager {
    public static void setTestNode (double x, double y, String test_id) {
        HashMap<String,Double> test_map = new HashMap<String,Double>();
        test_map.put("x",x);
        test_map.put("y",y);
        String osm_id  = test_id;
        NodeManager.addGmlNode(osm_id,test_map);
        System.out.print("追加したnodeの座標：x = " + x + ",y = " + y + "　追加したosmID：" + test_id);
        System.out.println("　付与したnodeID：" + NodeManager.getGmlID(osm_id));
    }
    public static void setTestEdge (String nodeA,String nodeB) {
        ArrayList<String> test_add_nodes = new ArrayList<String>();
        test_add_nodes.add(nodeA);
        test_add_nodes.add(nodeB);
        EdgeManager.setEdgeMap(test_add_nodes);

        ArrayList<String> test_pull_nodes = new ArrayList<String>();
        test_pull_nodes = EdgeManager.getEdgeNodeList(EdgeManager.getEdgeID());
        //System.out.println("追加したedgeのnodes：nodeA = " + nodeA + ", nodeB = " + nodeB);
        System.out.print("追加されたedgeのnodes：" + test_pull_nodes);
        System.out.println("　追加したedgeID = " + EdgeManager.getEdgeID());
    }

    public static void main(String[] args) {
        //test用のnodeを作成
        setTestNode(0.0,1.0,"100");
        setTestNode(2.0,3.0,"101");
        setTestNode(4.0,5.0,"102");

        //test用のedgeを作成
        setTestEdge(NodeManager.getGmlID("100"),NodeManager.getGmlID("101"));
        setTestEdge(NodeManager.getGmlID("101"),NodeManager.getGmlID("102"));
        setTestEdge(NodeManager.getGmlID("100"),NodeManager.getGmlID("102"));

        //nodeでbuildingを作成
        ArrayList<String> add_building_nodes = new ArrayList<String>();
        add_building_nodes.add(NodeManager.getGmlID("100"));
        add_building_nodes.add(NodeManager.getGmlID("101"));
        add_building_nodes.add(NodeManager.getGmlID("102"));
        BuildingManager.setBuildingNodeList(add_building_nodes);
        System.out.println("追加したbuildingのnodes：nodeA = " + NodeManager.getGmlID("100") + ", nodeB = " + NodeManager.getGmlID("101") + ", nodeC = " + NodeManager.getGmlID("102"));
        ArrayList<String> pull_building_nodes = new ArrayList<String>();
        pull_building_nodes = BuildingManager.getBuildingNodeList(BuildingManager.getBuildingNodeID());
        System.out.print("追加されたbuildingのnodes：" + pull_building_nodes.get(0) + "," + pull_building_nodes.get(1) + "," + pull_building_nodes.get(2));
        System.out.println("　追加したbuilding_node_id_ = " + BuildingManager.getBuildingNodeID());
        //getBuildingContainNodeの動作確認
        ArrayList<String> test_contain_nodes = BuildingManager.getBuildingContainNode(NodeManager.getGmlID("100"));
        System.out.println("検索したnodeID:" + NodeManager.getGmlID("100"));
        System.out.println("buildingのnodesが帰ってきてればgetBuildingContainNodeは動作してる：" + test_contain_nodes);

        //edgeでbuildingを作成
        ArrayList<String> add_building_edges = new ArrayList<String>();
        for (int i=1; i<=Integer.parseInt(EdgeManager.getEdgeID()); i++) {
            add_building_edges.add(String.valueOf(i));
        }
        BuildingManager.setBuildingEdgeList(add_building_edges);
        ArrayList<String> pull_building_edges = new ArrayList<String>();
        pull_building_edges = BuildingManager.getBuildingEdgeList(BuildingManager.getBuildingEdgeID());
        System.out.print("追加されたbuildingのedges:" + pull_building_edges);
        System.out.println("　追加したbuilding_edge_id_ = " + BuildingManager.getBuildingEdgeID());
        //getBuildingContainEdgeの動作確認
        ArrayList<String> test_contain_edges = BuildingManager.getBuildingContainEdge(EdgeManager.getEdgeID());
        System.out.println("検索したedgeID:" + EdgeManager.getEdgeID());
        System.out.println("buildingのedgesが帰ってきてればgetBuildinContainEdgeは動作してる：" + test_contain_edges);


    }
}
