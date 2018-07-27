import java.util.ArrayList;

//edge作成用クラス
public class MakeEdge {

    private NodeManager     nm;
    private EdgeManager     em;
    private BuildingManager bm;
    private RoadManager     rm;

    public MakeEdge(
            NodeManager     nm,
            EdgeManager     em,
            BuildingManager bm,
            RoadManager     rm)
    {
            this.nm = nm;
            this.em = em;
            this.bm = bm;
            this.rm = rm;
    }

    //建物と拡張道路の辺の作成・登録と、
    //他のオブジェクトとかぶる辺を逆方向にと保持する関数
    public void makeNodeToEdge () {

        //建物の辺作成の処理
        //建物総数を取得
        int building_highest_num =  Integer.parseInt(bm.getBuildingNodeID());
        //建物を全て回す
        for (int i=1; i<=building_highest_num; i++) {
            //除外するリストに含まれていない建物に対して処理を実行
            if (bm.containRemoveBuildingList(String.valueOf(i))==false) {
                //建物を構成するnode集合を取得
                ArrayList<String> write_nodebuilding = bm.getBuildingNodeList(""+i);
                //makeEdge関数で建物を構成するedgeの集合を作成・取得
                ArrayList<String> edge_list_to_make_building = makeEdge(write_nodebuilding);
                //edgeを作成した建物としてid付け(1~)と,逆方向にする辺を調べ登録する処理
                bm.setBuildingEdgeList(edge_list_to_make_building);
            }

        }

        //道路の辺作成の処理
        //作成されている拡張道路の数を取得
        int road_highest_num = Integer.parseInt(rm.getRoadNodeID());
        //作成されている拡張道路を全て回す
        for (int i=1; i<=road_highest_num; i++){
            //削除対象の拡張道路でない場合
            if (rm.containRemoveRoadList(String.valueOf(i)) == false) {
                //対象の拡張道路を構成しているnode集合を取得
                ArrayList<String> write_noderoad = rm.getRoadNodeList(""+i);
                //makeEdge関数で拡張道路を構成するedgeの集合を作成・取得
                ArrayList<String> edge_list_to_make_road = makeEdge(write_noderoad);
                //edgeを作成した拡張道路としてid付け(1~)と,逆方向にする辺を調べ登録する処理
                rm.setRoadMap(edge_list_to_make_road);
            }

        }
    }

    //渡されたnode集合に対して、nodeが入っている順番にedgeを作る。
    //また、作ったedgeのlistを返す
    private ArrayList<String> makeEdge(ArrayList<String> write_node){
        //++++++++++++++++++++++++++++++++++++++++++++++++++
        //ここのtmp_nodeの初期化は以下のfor文内に組み込めるはず
        String tmp_node = write_node.get(0);
        //++++++++++++++++++++++++++++++++++++++++++++++++++

        //建物を構成するEdgeのリストとして使う
        ArrayList<String> object_edge_list = new ArrayList<String>();

        for (int j=1; j<write_node.size(); j++) {
            //1辺を扱う変数を用意
            ArrayList<String> node_List_to_make_edge = new ArrayList<String>();
            //辺を構成する2点のnodeの後ろのnodeを取得
            String next_tmp_node = write_node.get(j);

            //辺を扱う変数に辺を構成する2点のnodeを格納
            node_List_to_make_edge.add(tmp_node);
            node_List_to_make_edge.add(next_tmp_node);

            //++++++++++++++特に意味をなさない処理+++++++++++++++++++
            if (nm.containsNode(j)) {
                //System.out.println("存在しているnode");
            }else {
                //System.out.println("存在しないnode");
            }
            //++++++++++++++++++++++++++++++++++++++++++++++++++++

            //使用した2点のnodeをnodeManagerに使用されているnodeとして登録
            nm.setUsedNodeList(tmp_node);
            nm.setUsedNodeList(next_tmp_node);

            //対象としているEdgeをemに作成し、建物を構成するEdgeのリストに追加する
            //対象としているEdgeがすでにemに存在するEdgeである場合は、emに作成せずにリストへ追加する
            String check_edge_ID = new String();
            check_edge_ID = em.checkExistEdge(tmp_node, next_tmp_node);
            if (check_edge_ID == "0") {
                em.setEdgeMap(node_List_to_make_edge);
                object_edge_list.add(em.getEdgeID());
            }else{
                object_edge_list.add(check_edge_ID);
            }

            //++++++++++++++++++++++++++++++++++++++++++++++++++
            //for文の外にあったtmp_nodeを次のnodeへ更新する処理
            tmp_node = next_tmp_node;
            //++++++++++++++++++++++++++++++++++++++++++++++++++
        }
        return object_edge_list;
    }
}
