import java.util.ArrayList;
import java.util.HashMap;

public class Test_EdgeManager {
    public static void setTestNode (double x, double y, String test_id, NodeManager nm) {
        HashMap<String,Double> test_map = new HashMap<String,Double>();
        test_map.put("x",x);
        test_map.put("y",y);
        String osm_id  = test_id;
        nm.addGmlNode(osm_id,test_map);
        System.out.println("追加したnodeの座標：x = " + x + "y = " + y + "　追加したosmID：" + test_id);
        System.out.println("付与したnodeID：" + nm.getGmlID(osm_id));
    }

    public static void main(String[] args) {
        //データ管理用のManagerクラスのインスタンスを生成
        NodeManager nm = new NodeManager();

        //まずテスト用のnodeデータを作成する
        setTestNode(0.0,1.0,"100", nm);
        setTestNode(2.0,3.0,"101", nm);
        setTestNode(4.0,5.0,"102", nm);

        //辺を構成する2つの新しいnodeでArrayを作成
        ArrayList<String> test_edge = new ArrayList<String>();
        test_edge.add(nm.getGmlID("100"));
        test_edge.add(nm.getGmlID("101"));
        System.out.println("新しいnodeID:" + nm.getGmlID("100") + "と"
                + nm.getGmlID("101") + "で辺を作成");

        //EdgeManagerのsetEdgeMapのテスト
        EdgeManager em = new EdgeManager();
        em.setEdgeMap(test_edge);
        System.out.println("初めてのedgeID付加の確認．1ならば良し：getEdgeID() = " + em.getEdgeID());

        //付与したedgeIDからnodeIDを取り出してみる
        ArrayList<String> pull_edge = em.getEdgeNodeList(em.getEdgeID());
        System.out.println("付与したedgeIDからnodeIDを取り出してみる");
        System.out.println("nodeIDその１：" + pull_edge.get(0));
        System.out.println("nodeIDその２：" + pull_edge.get(1));
        System.out.println("辺を構成しているnodeIDが出ていればedge_map_は機能している");

        //最後にcheckMinusDirectionEdge()の動作確認
            //check用に辺を2本作る
            ArrayList<String> test_edge2 = new ArrayList<String>();
            test_edge2.add(nm.getGmlID("101"));
            test_edge2.add(nm.getGmlID("102"));
            em.setEdgeMap(test_edge2);
            System.out.println("新しい辺を追加　edgeID = " + em.getEdgeID() + "　辺のnodeIDは"
                    + nm.getGmlID("101") + "," + nm.getGmlID("102"));
            ArrayList<String> test_edge3 = new ArrayList<String>();
            test_edge3.add(nm.getGmlID("100"));
            test_edge3.add(nm.getGmlID("102"));
            em.setEdgeMap(test_edge3);
            System.out.println("新しい辺を追加　edgeID = " + em.getEdgeID() + "　辺のnodeIDは"
                    + nm.getGmlID("100") + "," + nm.getGmlID("102"));

            //checkMinusDirectionEdge()の動作確認
            ArrayList<String> pull_edge2 = em.getEdgeNodeList(em.getEdgeID());
            System.out.println("checkする辺のnodeIDは：" + pull_edge2.get(0) + "," + pull_edge2.get(1));
            if (em.checkMinusDirectionEdge(pull_edge2.get(0),pull_edge2.get(1))) {
                System.out.println("checkMinusDirectionEdge()は動作した");
            } else {
                System.out.println("checkMinusDirectionEdge()の動作確認失敗");
            }
    }
}
