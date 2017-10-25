import java.util.HashMap;

public class Test_NodeManager {
    public static void main(String[] args){
        //addGmlNodeメソッドのテスト
        HashMap<String,Double> test_map = new HashMap<String,Double>();
        test_map.put("x",300.0);
        test_map.put("y",400.0);
        String osm_id  = "3";
        NodeManager.addGmlNode(osm_id,test_map);
        System.out.println("付与したnodeID：" + NodeManager.getGmlID(osm_id));   //1

        String node_id = NodeManager.getGmlID(osm_id);
        System.out.println("付与したosmID：" + NodeManager.getOsmID(node_id));  //3
        System.out.println("登録したx座標：" + NodeManager.getX(node_id));      //300.0
        System.out.println("登録したy座標：" + NodeManager.getY(node_id));      //400.0

        String osm_id2 = "4";
        NodeManager.addGmlNode(osm_id2,test_map);
        System.out.println("2回目のnode追加でnodeIDが＋１されているか確認: " + NodeManager.getGmlID(osm_id2)); //2


        //setUsedNodeListメソッドのテスト
        NodeManager.setUsedNodeList(osm_id);
        System.out.println("trueが返ってたらsetUsedNodeList機能してる : " + NodeManager.checkUsedNodeList(osm_id)); //true
    }
}
