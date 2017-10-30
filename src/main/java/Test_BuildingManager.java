import java.util.ArrayList;
import java.util.HashMap;

public class Test_BuildingManager {
    public static void setTestNode (double x, double y, String test_id, NodeManager nm) {
        HashMap<String,Double> test_map = new HashMap<String,Double>();
        test_map.put("x",x);
        test_map.put("y",y);
        String osm_id  = test_id;
        nm.addGmlNode(osm_id,test_map);
        System.out.print("追加したnodeの座標：x = " + x + ",y = " + y + "　追加したosmID：" + test_id);
        System.out.println("　付与したnodeID：" + nm.getGmlID(osm_id));
    }
    public static void setTestEdge (String nodeA,String nodeB, EdgeManager em) {
        ArrayList<String> test_add_nodes = new ArrayList<String>();
        test_add_nodes.add(nodeA);
        test_add_nodes.add(nodeB);
        em.setEdgeMap(test_add_nodes);

        ArrayList<String> test_pull_nodes = new ArrayList<String>();
        test_pull_nodes = em.getEdgeNodeList(em.getEdgeID());
        //System.out.println("追加したedgeのnodes：nodeA = " + nodeA + ", nodeB = " + nodeB);
        System.out.print("追加されたedgeのnodes：" + test_pull_nodes);
        System.out.println("　追加したedgeID = " + em.getEdgeID());
    }

    public static void main(String[] args) {
        NodeManager nm = new NodeManager();

        //test用のnodeを作成
        setTestNode(0.0,1.0,"100", nm);
        setTestNode(2.0,3.0,"101", nm);
        setTestNode(4.0,5.0,"102", nm);

        EdgeManager em = new EdgeManager();

        //test用のedgeを作成
        setTestEdge(nm.getGmlID("100"),nm.getGmlID("101"), em);
        setTestEdge(nm.getGmlID("101"),nm.getGmlID("102"), em);
        setTestEdge(nm.getGmlID("100"),nm.getGmlID("102"), em);

        BuildingManager bm = new BuildingManager();

        //nodeでbuildingを作成
        ArrayList<String> add_building_nodes = new ArrayList<String>();
        add_building_nodes.add(nm.getGmlID("100"));
        add_building_nodes.add(nm.getGmlID("101"));
        add_building_nodes.add(nm.getGmlID("102"));
        bm.setBuildingNodeList(add_building_nodes);
        System.out.println("追加したbuildingのnodes：nodeA = " + nm.getGmlID("100") + ", nodeB = " + nm.getGmlID("101") + ", nodeC = " + nm.getGmlID("102"));
        ArrayList<String> pull_building_nodes = new ArrayList<String>();
        pull_building_nodes = bm.getBuildingNodeList(bm.getBuildingNodeID());
        System.out.print("追加されたbuildingのnodes：" + pull_building_nodes.get(0) + "," + pull_building_nodes.get(1) + "," + pull_building_nodes.get(2));
        System.out.println("　追加したbuilding_node_id_ = " + bm.getBuildingNodeID());
        //getBuildingContainNodeの動作確認
        ArrayList<String> test_contain_nodes = bm.getBuildingContainNode(nm.getGmlID("100"));
        System.out.println("検索したnodeID:" + nm.getGmlID("100"));
        System.out.println("buildingのnodesが帰ってきてればgetBuildingContainNodeは動作してる：" + test_contain_nodes);

        //edgeでbuildingを作成
        ArrayList<String> add_building_edges = new ArrayList<String>();
        for (int i=1; i<=Integer.parseInt(em.getEdgeID()); i++) {
            add_building_edges.add(String.valueOf(i));
        }
        bm.setBuildingEdgeList(add_building_edges);
        ArrayList<String> pull_building_edges = new ArrayList<String>();
        pull_building_edges = bm.getBuildingEdgeList(bm.getBuildingEdgeID());
        System.out.print("追加されたbuildingのedges:" + pull_building_edges);
        System.out.println("　追加したbuilding_edge_id_ = " + bm.getBuildingEdgeID());
        //getBuildingContainEdgeの動作確認
        ArrayList<String> test_contain_edges = bm.getBuildingContainEdge(em.getEdgeID());
        System.out.println("検索したedgeID:" + em.getEdgeID());
        System.out.println("buildingのedgesが帰ってきてればgetBuildinContainEdgeは動作してる：" + test_contain_edges);


    }
}
