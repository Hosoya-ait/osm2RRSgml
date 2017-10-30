import java.util.HashMap;

public class Test_NodeManager {
    public static void main(String[] args){
        NodeManager nm = new NodeManager();

        //addGmlNodeメソッドのテスト
        HashMap<String,Double> test_map = new HashMap<String,Double>();
        test_map.put("x",300.0);
        test_map.put("y",400.0);
        String osm_id  = "3";
        nm.addGmlNode(osm_id,test_map);
        System.out.println("付与したnodeID：" + nm.getGmlID(osm_id));   //1

        String node_id = nm.getGmlID(osm_id);
        System.out.println("付与したosmID：" + nm.getOsmID(node_id));  //3
        System.out.println("登録したx座標：" + nm.getX(node_id));      //300.0
        System.out.println("登録したy座標：" + nm.getY(node_id));      //400.0

        String osm_id2 = "4";
        nm.addGmlNode(osm_id2,test_map);
        System.out.println("2回目のnode追加でnodeIDが＋１されているか確認: " + nm.getGmlID(osm_id2)); //2


        //setUsedNodeListメソッドのテスト
        nm.setUsedNodeList(osm_id);
        System.out.println("trueが返ってたらsetUsedNodeList機能してる : " + nm.checkUsedNodeList(osm_id)); //true
    }
}
