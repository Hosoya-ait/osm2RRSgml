import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.geom.Line2D;
import java.awt.Point;

class ConnectBuildingToRoadTest {

    private HashMap<String,ArrayList<String>> alreadyConnectBuilding = new HashMap<String,ArrayList<String>>();
    private NodeManager     node_manager_ = new NodeManager();
    private BuildingManager building_manager_ = new BuildingManager();
    private RoadManager     road_manager_ = new RoadManager();
    private AreaManager     area_manager_;
    private Double search_object_distance = 20.0;
    private Double default_road_width = 0.75;
    private HashMap<String,String> tmp_crosspoint_object_edges = new HashMap<String,String>();

    private HashMap<String,ArrayList<String>> BuildingConnectedBuilding = new HashMap<String,ArrayList<String>>();
    private HashMap<String,ArrayList<String>> BuildingConnectedRoad = new HashMap<String,ArrayList<String>>();

    public ConnectBuildingToRoadTest(NodeManager node_manager_,BuildingManager building_manager_,RoadManager road_manager_,AreaManager area_manager_ ){
        this.node_manager_ = node_manager_;
        this.building_manager_ = building_manager_;
        this.road_manager_ = road_manager_ ;
        this.area_manager_ = area_manager_ ;
    }

    public void connect(){
        int building_list_size = Integer.parseInt(building_manager_.getBuildingNodeID());
        for (int building_id = 1; building_id<=building_list_size ; building_id++ ) {
            //buildingを構成するNodeのListを取得する。　Listの始めと終わりのNodeは同じもの。
            ArrayList<String> connect_building = building_manager_.getBuildingNodeList(String.valueOf(building_id));
            //System.out.println();
            System.out.println("connectBuildingToRoad:"+building_id+"/"+building_list_size);
            //System.out.println("buildingID:"+building_id);

            //建物が所属している範囲（区間）を取得
            Point buildingArea = area_manager_.buildingBelongArea(String.valueOf(building_id));
            //拡張した道路と建物が交差していればfalseを返す関数で分岐
            if (checkCrossingRoad(String.valueOf(building_id),connect_building,buildingArea)) {

                connectObject(connect_building,building_id);
            }else{
              //removeRelatedObjects(""+building_id);
            }
        }
    }

    private void connectObject(ArrayList<String> building_List ,int building_ID){
        //保存用
        String nearest_Shape_Type = "none";
        String nearest_Shape_ID = "none";
        //buildingのnodeIDの保存　roadのnodeIDの保存
        Point2D.Double cross_point = new Point2D.Double(0.0,0.0);

        Point2D.Double nearest_point = new Point2D.Double(0.0,0.0);
        int connect_edge_point_A = 0;
        int connect_edge_point_B = 0;
        Double nearest_distance = 1000.0;
        int connect_building_edge_point_A = 0;
        int connect_building_edge_point_B = 0;

        //計算用
        HashMap<String,Double> tmp_Map = new HashMap<String,Double>();
        ArrayList<String> check_arr = new ArrayList<String>();
        String start_point_ID = null;
        String end_point_ID = null;

        Point2D.Double start_point = new Point2D.Double();
        Point2D.Double end_point = new Point2D.Double();
        Point2D.Double middle_point = new Point2D.Double();
        Point2D.Double difference_point = new Point2D.Double();
        Double radian;
        Double degree;

        String tmp_start_point_ID = null;
        String tmp_end_point_ID = null;

        Point2D.Double tmp_start_point = new Point2D.Double();
        Point2D.Double tmp_end_point = new Point2D.Double();

        Double distance;



        Point2D.Double point_A = new Point2D.Double();
        Point2D.Double point_B = new Point2D.Double();
        Point2D.Double point_C = new Point2D.Double();
        Point2D.Double point_D = new Point2D.Double();



        //建物を構成しているnode集合を回す
        for (int i=0; i<building_List.size()-1;i++ ) {
            //System.out.println();
            //System.out.println("edge:"+i+","+(i+1));

            //建物の2点をstart,endとしてgmlの座標でそれぞれ取得
            start_point.setLocation(node_manager_.getX(building_List.get(i)),node_manager_.getY(building_List.get(i)));
            end_point.setLocation(node_manager_.getX(building_List.get(i+1)),node_manager_.getY(building_List.get(i+1)));

            //2点間のユークリッド距離を調べ、それが接続道路の幅より小さい時、その2点間の辺は接続用としては使用しない。
            if(Math.hypot(start_point.getX() - end_point.getX(),start_point.getY() - end_point.getY()) < 2.0){
                continue;
            }

            //以降、建物の2点間が接続道路を作成するのに十分な幅がある場合の処理である。

            //start,endの中間点を作成
            middle_point.setLocation((start_point.getX()+end_point.getX())/2,(start_point.getY()+end_point.getY())/2);

            //start,endの差から、角度を得る
            difference_point.setLocation(start_point.getX()-end_point.getX(),start_point.getY()-end_point.getY());
            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;




            //＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋

            //-90度した角度をラジアンへ変換      建物の外側に推薦を引くための角度であるが、建物が反時計回りでないと意味がない。
            //建物のnodeが反時計回りになっているのか確認する必要あり．
            //osmのwayで管理されている通りのnode順で建物は本プログラムにて管理されている。
            //道路のように反時計回りに統一するような処理がどこにもない。
            //したがって、全ての建物が反時計回りになっている保証はない。
            //ここでの角度は建物を構成するnode集合が反時計回りに管理されて初めて生きてくるため、現在バグの原因になっていると思われ
            //2018/07/12
            Double radian_90 = ((degree-90)*Math.PI)/180.0;


            //＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋



            //tmp_XY ??
            Point2D.Double check_point = new Point2D.Double();
            //checkpointを建物の1辺の中点から垂直方向へ20.0の距離に作成　search_object_distance = 20.0
            check_point.setLocation(Math.cos(radian_90)*this.search_object_distance+middle_point.getX(),Math.sin(radian_90)*this.search_object_distance+middle_point.getY());

            //road数を取得
            int road_list_size = Integer.parseInt(road_manager_.getRoadNodeID());

            //建物に接続された道路があるかチェック
            ArrayList<String> check_array = building_manager_.getBuildingConnectedRoad(""+building_ID);

            //建物が属するエリアを取得
            Point buildingArea = area_manager_.buildingBelongArea(String.valueOf(building_ID));
            //System.out.println("Point:"+buildingArea.x+"."+buildingArea.y);
            //建物が所属するエリアから探せる道路のリストを取得
            ArrayList<String> roadList = area_manager_.getSearchRoadList(buildingArea);
            //System.out.println("roadListSize:"+roadList.size());




            //for (int k = 0; k<roadList.size();k++ ) {
            //1からのforで良いのか要検証　check_arrayとの数字の関係性を確認しておく　大丈夫そう
            //拡張道路のidだけ回す
            for (int k = 1; k<=road_list_size;k++ ) {
                //String checkRoadID = roadList.get(k);

                //既に自分(建物)に接続されている道路であるため、新しく接続を行う道路の候補としては除外する
                if (check_array.contains(k+"")) {
                    //System.out.println("自分に接続されているため除外");
                  continue;
                }
                //road_manager_の除外リストに含まれた道路である場合は除外する。
                if (road_manager_.containRemoveRoadList(k+"")) {
                    //System.out.println("除外させるリストに入っている");
                  continue;
                }

                //kの番号に対応するroadIDに紐づけられた　拡張後のNodelistを取得
                check_arr = road_manager_.getRoadNodeList(String.valueOf(k));

                //拡張後のroadのnode集合を回す　-1の理由は、最初と最後のnodeが同じであり最後をカウントしないためである。
                for (int m = 0; m<check_arr.size()-1;m++ ) {
                    //2点のnodeの座標を取得
                    tmp_start_point.setLocation(node_manager_.getX(check_arr.get(m)),node_manager_.getY(check_arr.get(m)));
                    tmp_end_point.setLocation(node_manager_.getX(check_arr.get(m+1)),node_manager_.getY(check_arr.get(m+1)));

                    //建物の1辺の中点から垂直に(外側へ向かってかどうかは検証次第)20.0の距離まで引いた線と拡張道路の1辺が交差するか
                    //交差していなかったら次の1辺へ移行する。
                    if (checkCrossingLineSegment(middle_point,check_point,tmp_start_point,tmp_end_point)==false) continue;

                    //建物の1辺から引いた垂線と拡張道路の1辺が直行している場合

                    //System.out.println("Road");
                    //
                    //2辺の線分の交点座標を計算して返す関数を使って交点を取得
                    cross_point = CheckCrossingPoint(middle_point,check_point,tmp_start_point,tmp_end_point);
                    //建物の1辺の中点と、そこから引いた垂線と拡張道路の1辺との交点、との間にあるユークリッド距離を取得
                    distance = Math.hypot(cross_point.getX() - middle_point.getX(),cross_point.getY() - middle_point.getY());
                    //System.out.println("CrossP X:"+cross_point.getX()+" Y:"+cross_point.getY());
                    //System.out.println("distance:"+distance);
                    //System.out.println("nearstDistance"+nearest_distance);

                    //角度が急であるかどうかを判定している　45°〜135°の範囲内であればfalse
                    if(checkProperDegree(middle_point,check_point,tmp_start_point,tmp_end_point)) continue;
                    //System.out.println("角度が急でない");

                    //角度が急でない場合の処理が続く

                    //建物の1辺から引いた垂線と拡張道路の1辺との交点と,
                    //建物の所属する範囲から検索可能な全てのnode、
                    //の距離が0.75より大きいならばtrue そうでなければfalse
                    if (checkContainNode(cross_point,buildingArea)==false) continue;
                    //System.out.println("node近くになし");

                    //接続道路を作成したい建物から伸ばした垂線と、
                    //引数で指定されているobject(建物もしくは道路)の辺が交差している場合falseを返す関数
                    if (checkCrossingBuilding(middle_point,cross_point,""+building_ID,""+k,"road",m,buildingArea)==false)continue;

                    //ここまでで、伸ばしたい垂線に交差するような別のオブジェクトがない状況ができているはず　要検証

                    //System.out.println("重なっているEDGEなし");
                    //System.out.println("追加処理");

                    //これ上3つのif文の処理(いずれも全探索)する前に書いといたら処理めっちゃ軽くなることね？
                    if(distance == 0)continue;

                    //初めての接続候補の時
                    //大元のfor文は建物からの接続道路の接続先として「道路」を探索していることから
                    //nearestは道路に確定している状況
                    //接続道路を作成する際の建物の辺と道路の辺を保持する　その他諸々
                    if (nearest_Shape_Type == "none") {
                        nearest_Shape_Type = "road";
                        nearest_Shape_ID = "" + k;
                        nearest_point.setLocation(cross_point);
                        connect_edge_point_A = m;
                        connect_edge_point_B = m + 1;
                        nearest_distance = distance;
                        connect_building_edge_point_A = i;
                        connect_building_edge_point_B = i + 1;
                    }
                    //すでに最も近い接続道路の接続先がある場合
                    //大元のfor文は道路を探索していることから、
                    //さらに近い道路の接続先候補が見つかった場合と、
                    //距離に関係なしに、建物じゃなく道路に接続ができることがわかっている場合に以下の処理（上記if文と同じ処理）
                    else if(distance < nearest_distance || nearest_Shape_Type == "building") {
                        nearest_Shape_Type = "road";
                        nearest_Shape_ID = ""+k;
                        nearest_point.setLocation(cross_point);
                        connect_edge_point_A = m;
                        connect_edge_point_B = m+1;
                        nearest_distance = distance;
                        connect_building_edge_point_A = i;
                        connect_building_edge_point_B = i+1;
                    }
                }
            }
            if (nearest_Shape_Type == "road"){
                //System.out.println("roadに繋げられるためbuildingの処理は行わない");
                continue;
            }

            int building_list_size = Integer.parseInt(building_manager_.getBuildingNodeID());

            ArrayList<String> buildingList = area_manager_.getSearchBuildingList(buildingArea);

            //for (int k = 0; k<buildingList.size();k++ ) {
            for (int k = 1; k<building_list_size;k++ ) {
                //String buildingID = buildingList.get(k);

                //System.out.println("building:"+k+"/"+buildingList.size());
                //接続先対象が自分自身の場合は意味ないので処理をパス
                if (k == building_ID)continue;

                //除外対象の建物リストに含まれる建物だった場合、処理をパス
                if (building_manager_.containRemoveBuildingList(""+k))continue;

                //接続先対象の建物を構成するnode集合をcheck_arrとして取得
                check_arr = building_manager_.getBuildingNodeList(String.valueOf(k));

                //接続先対象の建物を構成するnode集合を回す。
                //建物のnode集合は始めと最後のnodeが同じなので、"-1"によって全てのnodeを回すfor文になる。
                for (int m = 0; m<check_arr.size()-1;m++ ) {

                    //接続先対象の建物を構成する2点のnodeの座標をそれぞれ取得
                    tmp_start_point.setLocation(node_manager_.getX(check_arr.get(m)),node_manager_.getY(check_arr.get(m)));
                    tmp_end_point.setLocation(node_manager_.getX(check_arr.get(m+1)),node_manager_.getY(check_arr.get(m+1)));


                    //第1,2引数と第3,4引数で作られる２線分を比べ交差していたらtrueを返す関数
                    //接続元となる建物の1辺の中点とそこから引いた垂線とobject(建物あるいは道路)の辺の交点でできる線分と、
                    //接続先対象の建物の1辺が交差している場合trueなので、if文はスルーされる。
                    if (checkCrossingLineSegment(middle_point,check_point,tmp_start_point,tmp_end_point)==false) continue;

                    //接続先対象が建物である場合の処理が続く

                    //建物の1辺の中点から垂直に引いた線が、別の建物の1辺と交差した交点の座標をcross_pointとして取得する。
                    //また、建物の1辺の中点と垂線の交点とのユークリッド距離をdistaneとして取得。
                    cross_point = CheckCrossingPoint(middle_point,check_point,tmp_start_point,tmp_end_point);
                    distance = Math.hypot(cross_point.getX() - middle_point.getX(),cross_point.getY() - middle_point.getY());

                    //角度が急であるかどうかを判定している　45°〜135°の範囲内であればfalse
                    if(checkProperDegree(middle_point,check_point,tmp_start_point,tmp_end_point)) continue;

                    //建物の1辺から引いた垂線と拡張道路の1辺との交点と,
                    //建物の所属する範囲から検索可能な全てのnode、
                    //の距離が0.75より大きいならばtrue そうでなければfalse
                    if (checkContainNode(cross_point,buildingArea) ==false) continue;

                    //接続道路を作成したい建物から伸ばした垂線と、
                    //引数で指定されているobject(建物もしくは道路)の辺が交差している場合falseを返す関数
                    //つまり建物の1辺から引いた垂線上に別のobject(建物あるいは道路)の辺が交差しているかどうかを判断している。
                    if (checkCrossingBuilding(middle_point,cross_point,""+building_ID,k+"","building",m,buildingArea) == false) continue;


                    //建物から引いた垂線上では(接続道路を拡張した後で重なるかは考慮されていない)、
                    //他のobject(建物あるいは道路)に被っておらず、
                    //問題なく接続道路を作成できる状況である。


                    //接続道路の接続元の建物が、すでに接続されている建物の場合
                    if (this.alreadyConnectBuilding.containsKey(""+building_ID)){

                        //接続元の建物と接続道路で接続されている建物のうち、現在接続先として調べようとしている建物がある場合
                        //つまり、接続先の候補として調べている建物が、すでに接続元の建物と接続されている場合
                        //接続道路を作成する処理は意味ないからパスされる。
                        if (((ArrayList)this.alreadyConnectBuilding.get(""+building_ID)).contains(k+"")) {
                            //System.out.println("すでにこのBuildingに接続されているBuilding");
                            continue;
                        }
                    }

                    //System.out.println("追加処理");

                    ////これ上3つのif文の処理(いずれも全探索)する前に書いといたら処理めっちゃ軽くなることね？
                    if (distance == 0)continue;

                    //初めての接続候補の時　（処理の流れ的に道路と接続できなかった建物について
                    //大元のfor文は建物からの接続道路の接続先として「建物」を探索していることから
                    //nearestは建物に確定している状況
                    //接続道路を作成する際の、接続元の建物の辺と接続先の建物の辺を保持する　その他諸々
                    if (nearest_Shape_Type == "none") {
                        nearest_Shape_Type = "building";
                        nearest_Shape_ID = k+"";
                        nearest_point.setLocation(cross_point);
                        connect_edge_point_A = m;
                        connect_edge_point_B = m+1;
                        nearest_distance = distance;
                        connect_building_edge_point_A = i;
                        connect_building_edge_point_B = i+1;
                    }
                    //道路とは違い、道路を優先的にする必要はない。
                    //したがって、接続できる建物のうち、一番近いものを優先する条件文がきている。
                    //処理内容は上記のif文に同じ。
                    else if (distance < nearest_distance) {
                        nearest_Shape_Type = "building";
                        nearest_Shape_ID = k+"";
                        nearest_point.setLocation(cross_point);
                        connect_edge_point_A = m;
                        connect_edge_point_B = m+1;
                        nearest_distance = distance;
                        connect_building_edge_point_A = i;
                        connect_building_edge_point_B = i+1;
                    }
                }
            }
        }


        //  -- 接続道路の作成処理 --//

        //怒涛の変数宣言ゾーン
        Double radian_180;

        Point2D.Double building_point_start = new Point2D.Double();

        Double building_plus_X;
        Double building_plus_Y;
        HashMap<String,Double> add_node_map_plus = new HashMap<String,Double>();
        int tmpNodeId = 0;
        String add_plus_node = new String();

        Point2D.Double building_point_end = new Point2D.Double();

        Double building_minus_X;
        Double building_minus_Y;
        HashMap<String,Double> add_node_map_minus = new HashMap<String,Double>();
        String add_minus_node = new String();

        Point2D.Double road_point_start = new Point2D.Double();

        Double road_plus_X;
        Double road_plus_Y;

        String add_plus_road_node = new String();

        Point2D.Double road_point_end = new Point2D.Double();

        Double road_minus_X;
        Double road_minus_Y;
        String add_minus_road_node = new String();
        ArrayList<String> addRoadArr = new ArrayList<String>();
        ArrayList<String> tmp_shape = new ArrayList<String>();

        String plus_node_ID = new String();
        String minus_node_ID = new String();
        String plus_road_node_ID  = new String();
        String minus_road_node_ID = new String();


        //建物から接続道路を作成する際、接続先のオブジェクトごとに処理を分ける
        switch (nearest_Shape_Type) {
            case "none":
                //removeRelatedObjects(""+building_ID);
                //System.out.println("---------------接続道路なし---------------------");

            break;

            case "road":

                //　--接続道路の接続元である建物の1辺についてnode追加の処理-- //

                //接続道路を作成するための建物の1辺を構成している2点の座標を取得
                point_A.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_A)),node_manager_.getY(building_List.get(connect_building_edge_point_A)));
                point_B.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_B)),node_manager_.getY(building_List.get(connect_building_edge_point_B)));
                //接続道路を作成するための建物の1辺の中点
                middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);
                //接続道路を作成するための建物の1辺を構成している2点の差を表す座標、なす角のラジアン・角度を取得
                difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());
                radian = Math.atan2(difference_point.getY(),difference_point.getX());
                degree = (radian*180)/Math.PI;

                //接続道路を作成するための建物の1辺の中点から、その1辺方向に接続道路の幅員の半分の距離(プラス：xy正方向)にある位置を取得
                building_point_start.setLocation(Math.cos(radian)*default_road_width+middle_point.getX(),Math.sin(radian)*default_road_width+middle_point.getY());
                //続いてその位置の保持
                add_node_map_plus = new HashMap<String,Double>();
                add_node_map_plus.put("y",building_point_start.getY());
                add_node_map_plus.put("x",building_point_start.getX());
                //続いてその位置のnode登録
                plus_node_ID = node_manager_.addGmlNode(add_node_map_plus);
                area_manager_.setNode(plus_node_ID);

                //上記のプラス方向の処理について
                //-180°方向(マイナス：x,y負方向)の処理
                radian_180 = ((degree+180)*Math.PI)/180.0;
                building_point_end.setLocation(Math.cos(radian_180)*default_road_width+middle_point.getX(),Math.sin(radian_180)*default_road_width+middle_point.getY());
                add_node_map_minus = new HashMap<String,Double>();
                add_node_map_minus.put("y",building_point_end.getY());
                add_node_map_minus.put("x",building_point_end.getX());
                minus_node_ID = node_manager_.addGmlNode(add_node_map_minus);
                area_manager_.setNode(minus_node_ID);

                //建物の1辺に接続道路用の2点を追加する処理
                //connect_building_edge_point_B(+1)の値はトレースして正しいことを確認済み(insertBuildingInNode関数内でのadd関数の使い方が味噌) 2018/7/24
                building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B,minus_node_ID);
                building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B+1,plus_node_ID);



                // --接続道路の接続先である道路の1辺についてnode追加の処理-- //

                //接続道路の接続先の拡張道路を構成しているnode集合を取得
                tmp_shape = road_manager_.getRoadNodeList(nearest_Shape_ID);
                //接続道路を作成するための道路の1辺を構成している2点の座標を取得
                point_A.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_A)),node_manager_.getY(tmp_shape.get(connect_edge_point_A)));
                point_B.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_B)),node_manager_.getY(tmp_shape.get(connect_edge_point_B)));
                //接続道路を作成するための道路の1辺の中点
                middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);
                ////接続道路を作成するための建物の1辺を構成している2点の差を表す座標、なす角のラジアン・角度を取得
                difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());
                radian = Math.atan2(difference_point.getY(),difference_point.getX());
                degree = (radian*180)/Math.PI;


                //接続道路を作成するための道路の1辺の中点から、その1辺方向に接続道路の幅員の半分の距離(プラス：xy正方向)にある位置を取得
                //道路の時と若干書き方が違うが処理は同じ
                road_plus_X = Math.cos(radian)*default_road_width+nearest_point.getX();
                road_plus_Y = Math.sin(radian)*default_road_width+nearest_point.getY();
                road_point_start.setLocation(road_plus_X,road_plus_Y);
                //続いてその位置の保持
                add_node_map_plus = new HashMap<String,Double>();
                add_node_map_plus.put("y",road_point_start.getY());
                add_node_map_plus.put("x",road_point_start.getX());
                //続いてその位置のnode登録
                plus_road_node_ID  = node_manager_.addGmlNode(add_node_map_plus);
                area_manager_.setNode(plus_road_node_ID);


                //上記のプラス方向の処理について
                //-180°方向(マイナス：x,y負方向)の処理
                radian_180 = ((degree+180)*Math.PI)/180.0;
                road_minus_X = Math.cos(radian_180)*default_road_width+nearest_point.getX();
                road_minus_Y = Math.sin(radian_180)*default_road_width+nearest_point.getY();
                road_point_end.setLocation(road_minus_X,road_minus_Y);
                add_node_map_minus = new HashMap<String,Double>();
                add_node_map_minus.put("y",road_point_end.getY());
                add_node_map_minus.put("x",road_point_end.getX());
                minus_road_node_ID = node_manager_.addGmlNode(add_node_map_minus);
                area_manager_.setNode(minus_road_node_ID);

                //道路の1辺に接続道路用の2点を追加する処理
                road_manager_.insertRoadInNode(nearest_Shape_ID,connect_edge_point_B,minus_road_node_ID);
                road_manager_.insertRoadInNode(nearest_Shape_ID,connect_edge_point_B+1,plus_road_node_ID );


                ArrayList<String> addRoadArr2 = new ArrayList<String>();

//                System.out.println();
//                System.out.println("[ ConnectRoad_NodeList ]");
//                System.out.println("plus_node_ID       : " + plus_node_ID);
//                System.out.println("plus_road_node_ID  : " + plus_road_node_ID);
//                System.out.println("minus_road_node_ID : " + minus_road_node_ID);
//                System.out.println("minus_node_ID      : " + minus_node_ID);
//                System.out.println("plus_node_ID       : " + plus_node_ID);
//                System.out.println();



/*              ////WARNING////WARNING////WARNING////WARNING////WARNING////WARNING////WARNING//////

                このプログラムでは建物は時計回りに管理されている前提で組まれているのか確認をとる必要がある

                //////WARNING////WARNING////WARNING////WARNING////WARNING////WARNING////WARNING////
*/



                //建物を構成するnodeは時計回りに管理されている　！？
                //よって、接続道路を構成する順番は以下の通りに登録して成り立つ
                addRoadArr2.add(plus_node_ID);
                addRoadArr2.add(plus_road_node_ID );
                addRoadArr2.add(minus_road_node_ID);
                addRoadArr2.add(minus_node_ID);
                addRoadArr2.add(plus_node_ID);

                //作成した接続道路をmanagerに登録する処理
                road_manager_.setTmpRoadList(addRoadArr2);
                //「接続されている」という情報を登録する処理
                road_manager_.setRoadConnectedObject(nearest_Shape_ID,road_manager_.getRoadNodeID(),""+building_ID);
                area_manager_.setRoad(road_manager_.getRoadNodeID());
                break;



            case "building":

                //ここの前半の処理部分はおそらく case"road": の前半部分と同じなため、switchの外へ出すことで重複分を省略できると思われ。


                //　--接続道路の接続元である建物の1辺についてnode追加の処理-- //

                //接続道路を作成するための接続元の建物の1辺を構成している2点の座標を取得
                point_A.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_A)),node_manager_.getY(building_List.get(connect_building_edge_point_A)));
                point_B.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_B)),node_manager_.getY(building_List.get(connect_building_edge_point_B)));
                //接続道路を作成するための建物の1辺の中点
                middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);
                //接続道路を作成するための建物の1辺を構成している2点の差を表す座標、なす角のラジアン・角度を取得
                difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());
                radian = Math.atan2(difference_point.getY(),difference_point.getX());
                degree = (radian*180)/Math.PI;


                //接続道路を作成するための接続元建物の1辺の中点から、その1辺方向に接続道路の幅員の半分の距離(プラス：xy正方向)にある位置を取得
                building_point_start.setLocation(Math.cos(radian)*default_road_width+middle_point.getX(),Math.sin(radian)*default_road_width+middle_point.getY());
                //続いてその位置の保持
                add_node_map_plus = new HashMap<String,Double>();
                add_node_map_plus.put("y",building_point_start.getY());
                add_node_map_plus.put("x",building_point_start.getX());
                //続いてその位置のnode登録
                plus_node_ID = node_manager_.addGmlNode(add_node_map_plus);
                area_manager_.setNode(plus_node_ID);


                //上記のプラス方向の処理について
                //-180°方向(マイナス：x,y負方向)の処理
                radian_180 = ((degree+180)*Math.PI)/180.0;
                building_point_end.setLocation(Math.cos(radian_180)*default_road_width+middle_point.getX(),Math.sin(radian_180)*default_road_width+middle_point.getY());
                add_node_map_minus = new HashMap<String,Double>();
                add_node_map_minus.put("y",building_point_end.getY());
                add_node_map_minus.put("x",building_point_end.getX());
                minus_node_ID = node_manager_.addGmlNode(add_node_map_minus);
                area_manager_.setNode(minus_node_ID);


                //接続元建物の1辺に接続道路用の2点を追加する処理
                building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B,minus_node_ID);
                building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B+1,plus_node_ID);


                // --接続道路の接続先である建物の1辺についてnode追加の処理-- //

                //接続道路の接続先の建物を構成しているnode集合を取得
                tmp_shape = building_manager_.getBuildingNodeList(nearest_Shape_ID);

                //以下は上記の処理に同じ
                point_A.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_A)),node_manager_.getY(tmp_shape.get(connect_edge_point_A)));
                point_B.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_B)),node_manager_.getY(tmp_shape.get(connect_edge_point_B)));
                middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);
                difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());
                radian = Math.atan2(difference_point.getY(),difference_point.getX());
                degree = (radian*180)/Math.PI;


                //プラスの処理
                road_plus_X = Math.cos(radian)*default_road_width+nearest_point.getX();
                road_plus_Y = Math.sin(radian)*default_road_width+nearest_point.getY();
                road_point_start.setLocation(road_plus_X,road_plus_Y);
                add_node_map_plus = new HashMap<String,Double>();
                add_node_map_plus.put("y",road_point_start.getY());
                add_node_map_plus.put("x",road_point_start.getX());
                plus_road_node_ID = node_manager_.addGmlNode(add_node_map_plus);
                area_manager_.setNode(plus_road_node_ID);


                //マイナスの処理
                radian_180 = ((degree+180)*Math.PI)/180.0;
                road_minus_X = Math.cos(radian_180)*default_road_width+nearest_point.getX();
                road_minus_Y = Math.sin(radian_180)*default_road_width+nearest_point.getY();
                road_point_end.setLocation(road_minus_X,road_minus_Y);
                add_node_map_minus = new HashMap<String,Double>();
                add_node_map_minus.put("y",road_point_end.getY());
                add_node_map_minus.put("x",road_point_end.getX());
                minus_road_node_ID = node_manager_.addGmlNode(add_node_map_minus);
                area_manager_.setNode(minus_road_node_ID);


                //接続先建物の1辺に接続道路用の2点を追加する処理
                building_manager_.insertBuildingInNode(nearest_Shape_ID,connect_edge_point_B,minus_road_node_ID);
                building_manager_.insertBuildingInNode(nearest_Shape_ID,connect_edge_point_B+1,plus_road_node_ID);



                ArrayList<String> addRoadArr1 = new ArrayList<String>();

                //System.out.println("addroadNode:");
                //System.out.println(plus_node_ID);
                //System.out.println(plus_road_node_ID);
                //System.out.println(minus_road_node_ID);
                //System.out.println(minus_node_ID);


                //作成した接続道路の作成
                //建物はどちらも時計回りのため、プラスマイナスが交互に来ることで辻褄があう。
                addRoadArr1.add(plus_node_ID);
                addRoadArr1.add(minus_road_node_ID);
                addRoadArr1.add(plus_road_node_ID);
                addRoadArr1.add(minus_node_ID);
                addRoadArr1.add(plus_node_ID);

                //作成した接続道路の登録
                road_manager_.setTmpRoadList(addRoadArr1);
                building_manager_.setBuildingConnectedObject(nearest_Shape_ID,road_manager_.getRoadNodeID(),""+building_ID);
                area_manager_.setRoad(road_manager_.getRoadNodeID());


                //すでに接続されている建物であることをalready変数に登録していく処理
                if(this.alreadyConnectBuilding.containsKey(nearest_Shape_ID)){
                    ((ArrayList)this.alreadyConnectBuilding.get(nearest_Shape_ID)).add(""+building_ID);
                }
                else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(""+building_ID);
                    this.alreadyConnectBuilding.put(nearest_Shape_ID,list);
                }

                break;
        }
        return;
    }

    //2辺の線分の交点座標を計算して返す関数
    private Point2D.Double CheckCrossingPoint(Point2D.Double a,Point2D.Double b,Point2D.Double c,Point2D.Double d){
        // double radian_line1 = Math.atan2(line1.getY2()-line1.getY1(),line1.getX2()-line1.getX1());
        // double radian_line2 = Math.atan2(line2.getY2()-line2.getY1(),line2.getX2()-line2.getX1());
        // double degree_line1 = (radian_line1*180)/Math.PI;
        // double degree_line2 = (radian_line2*180)/Math.PI;
        // double radian_line1_90 = ((degree_line1+90)*Math.PI)/180.0;
        // double radian_line2_90 = ((degree_line2+90)*Math.PI)/180.0;
        //
        // Point2D.Double line1_vertical_point = new Point2D.Double(Math.cos(radian_line1_90)*100.0+line1.getX2(),Math.sin(radian_line1_90)*10.0+line1.getY2());
        // Point2D.Double line2_vertical_point = new Point2D.Double(Math.cos(radian_line2_90)*100.0+line2.getX1(),Math.sin(radian_line2_90)*10.0+line2.getY1());
        //
        // Line2D.Double line1_vertical = new Line2D.Double(line1.getP2(),line1_vertical_point);
        // Line2D.Double line2_vertical = new Line2D.Double(line2.getP1(),line2_vertical_point);
        //
        //
        // double A = line1_vertical.getY1()/line1_vertical.getX1();
        // double B = ( ( line1_vertical.getY1()*line1_vertical.getX2() ) - ( line1_vertical.getY2()*line1_vertical.getX1() ) ) / ( line1_vertical.getX2() - line1_vertical.getX1() );
        // double C = line2_vertical.getY1()/line2_vertical.getX1();
        // double D = ( ( line2_vertical.getY1()*line2_vertical.getX2() ) - ( line2_vertical.getY2()*line2_vertical.getX1() ) ) / ( line2_vertical.getX2() - line2_vertical.getX1() );
        //
        // double X = (D-B) / (A-C);
        // double Y = A*X+B;
        //
        // Point2D.Double correct_point = new Point2D.Double(X,Y);
        //
        // return correct_point;

        Point2D.Double xy = new Point2D.Double();
        //参照url https://gist.github.com/yoshiki/7702066
        Double du = (b.getX() - a.getX())*(d.getY() - c.getY()) - (b.getY() - a.getY())*(d.getX() - c.getX());
        Double u = ((c.getX() - a.getX()) * (d.getY() - c.getY()) - (c.getY() - a.getY())*(d.getX() - c.getX()))/du;
        Double tmp_X = a.getX() + u * (b.getX() - a.getX());
        Double tmp_Y = a.getY() + u * (b.getY() - a.getY());
        xy.setLocation(tmp_X,tmp_Y);
        return xy;
    }

    // private Boolean checkCrossingRoad(String building_ID,ArrayList<String> building_List,Point buildingArea){
    //     //ArrayList<String> check_Road = new ArrayList<String>();
    //     //すべてのRoadの線分に対して交差しているか判定交差していなければtrueが帰る
    //     Point2D.Double point_A = new Point2D.Double();
    //     Point2D.Double point_B = new Point2D.Double();
    //     Point2D.Double point_C = new Point2D.Double();
    //     Point2D.Double point_D = new Point2D.Double();
    //
    //     int road_list_size = Integer.parseInt(road_manager_.getRoadNodeID());
    //
    //     ArrayList<String> roadList = area_manager_.getSearchRoadList(buildingArea);
    //
    //     for (int i = 0; i<roadList.size();i++ ) {
    //
    //         String roadID = roadList.get(i);
    //         //他の建物から接続している道を判定に入れないように処理
    //         if(((ArrayList)building_manager_.getBuildingConnectedRoad(building_ID)).contains(roadID)) continue;
    //
    //
    //         ArrayList<String> check_Road = road_manager_.getRoadNodeList(roadID);
    //
    //         for (int k = 0; k<check_Road.size()-1;k++ ) {
    //             point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
    //             point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));
    //
    //             for (int m = 0; m<building_List.size()-1;m++ ) {
    //                 point_C.setLocation(node_manager_.getX(building_List.get(m)),node_manager_.getY(building_List.get(m)));
    //                 point_D.setLocation(node_manager_.getX(building_List.get(m+1)),node_manager_.getY(building_List.get(m+1)));
    //
    //                 if (checkCrossingLineSegment(point_A,point_B,point_C,point_D)) {
    //                     //System.out.println("道にかぶっています");
    //                     //System.out.println("roadID:"+roadID);
    //                     //System.out.println("roadnodeID_1:"+(k+1));
    //                     //System.out.println("roadnodeID_2:"+(k+2));
    //                     //System.out.println("BuildingnodeID_1:"+(m+1));
    //                     //System.out.println("BuildingnodeID_2:"+(m+2));
    //                     return false;
    //                 }
    //             }
    //         }
    //     }
    //     return true;
    // }


    private Boolean checkCrossingRoad(String building_ID,ArrayList<String> building_List,Point buildingArea){
        //ArrayList<String> check_Road = new ArrayList<String>();
        //すべてのRoadの線分に対して交差しているか判定交差していなければtrueが帰る
        Point2D.Double point_A = new Point2D.Double();
        Point2D.Double point_B = new Point2D.Double();
        Point2D.Double point_C = new Point2D.Double();
        Point2D.Double point_D = new Point2D.Double();

        int road_list_size = Integer.parseInt(road_manager_.getRoadNodeID());

        //ArrayList<String> roadList = area_manager_.getSearchRoadList(buildingArea);

        //ここAreaManagerのSearch変数で省略できそうじゃない？　2018/07/09細谷
        for (int i = 1; i<road_list_size;i++ ) {

            //String roadID = roadList.get(i);
            //他の建物から接続している道を判定に入れないように処理
            if(((ArrayList)building_manager_.getBuildingConnectedRoad(building_ID)).contains(String.valueOf(i))) continue;


            //拡張後のRoadのNodeListを取得する
            ArrayList<String> check_Road = road_manager_.getRoadNodeList(String.valueOf(i));

            for (int k = 0; k<check_Road.size()-1;k++ ) {
                //Roadを拡張構成するNodeListの内、2点の座標を取得
                point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
                point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));

                //buildingを構成するNodeListのうち、最初と最後の重複したNodeのうち最後を省いた分回す
                for (int m = 0; m<building_List.size()-1;m++ ) {
                    //buildingを構成するNodeListの内、2点の座標を取得
                    point_C.setLocation(node_manager_.getX(building_List.get(m)),node_manager_.getY(building_List.get(m)));
                    point_D.setLocation(node_manager_.getX(building_List.get(m+1)),node_manager_.getY(building_List.get(m+1)));

                    //AB,CDで作られる2線分が交差しているか判断
                    //交差していたらtrueになる関数
                    if (checkCrossingLineSegment(point_A,point_B,point_C,point_D)) {
                        //System.out.println("道にかぶっています");
                        //System.out.println("roadID:"+roadID);
                        //System.out.println("roadnodeID_1:"+(k+1));
                        //System.out.println("roadnodeID_2:"+(k+2));
                        //System.out.println("BuildingnodeID_1:"+(m+1));
                        //System.out.println("BuildingnodeID_2:"+(m+2));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // private Boolean checkCrossingBuilding(Point2D.Double origin_point,
    //                                       Point2D.Double connect_point,
    //                                       String building_ID,
    //                                       String object_ID,
    //                                       String object_type,
    //                                       int object_edge_num,
    //                                       Point buildingArea){
    //     //すべてのRoadの線分に対して交差しているか判定交差していなければtrueが帰る
    //     //System.out.println("交差判定");
    //     //System.out.println("接続先:"+object_type+"  ID:"+object_ID);
    //     //System.out.println("edgeNumber:"+object_edge_num);
    //     //System.out.println("building_ID:"+building_ID);
    //     //仮処理
    //     Point2D.Double point_A = new Point2D.Double();
    //     Point2D.Double point_B = new Point2D.Double();
    //
    //     int building_list_size = Integer.parseInt(building_manager_.getBuildingNodeID());
    //
    //
    //     ArrayList<String> buildingList = area_manager_.getSearchBuildingList(buildingArea);
    //     for (int i = 0; i<buildingList.size();i++ ) {
    //         String buildingID = buildingList.get(i);
    //
    //         ArrayList<String> check_Road = building_manager_.getBuildingNodeList(buildingID);
    //
    //         if (buildingID == building_ID){
    //             //System.out.println("自分自身のためcontinue");
    //             continue;
    //         }
    //
    //
    //         for (int k = 0; k<check_Road.size()-1;k++ ) {
    //             //if (object_type == "building" &&  i == Integer.parseInt(object_ID) && k == object_edge_num) continue;
    //             if (object_type == "building" &&  buildingID == object_ID && object_edge_num == k){
    //                 //System.out.println("接続したい先の建物のEDGEなためcontinue");
    //                 continue;
    //             }
    //             point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
    //             point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));
    //
    //             if (checkCrossingLineSegment(origin_point,connect_point,point_A,point_B)) {
    //                 //System.out.println("建物に交差");
    //                 //System.out.println("building:"+buildingID);
    //                 //System.out.println("edgeA:"+k);
    //                 //System.out.println("edgeB:"+(k+1));
    //                 return false;
    //             }
    //         }
    //     }
    //     int road_list_size = Integer.parseInt(road_manager_.getRoadNodeID());
    //
    //     ArrayList<String> roadList = area_manager_.getSearchRoadList(buildingArea);
    //
    //     for (int i = 0; i<roadList.size();i++ ) {
    //
    //         String roadID = roadList.get(i);
    //
    //         ArrayList<String> check_Road = road_manager_.getRoadNodeList(roadID);
    //
    //         // if (object_type == "road" &&  i == Integer.parseInt(object_ID) ) {
    //         //     //System.out.println("接続したい先の道のEDGEなためcontinue");
    //         //     System.out.println("i:"+i);
    //         //     System.out.println("object_ID:"+object_ID);
    //         //     continue;
    //         // }
    //
    //         for (int k = 0; k<check_Road.size()-1;k++ ) {
    //             if (object_type == "road" &&  roadID == object_ID && k == object_edge_num) continue;
    //             point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
    //             point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));
    //
    //             if (checkCrossingLineSegment(origin_point,connect_point,point_A,point_B)) {
    //                 //System.out.println("道に交差");
    //                 //System.out.println("road:"+roadID);
    //                 //System.out.println("edgeA:"+k);
    //                 //System.out.println("edgeB:"+(k+1));
    //                 return false;
    //             }
    //         }
    //     }
    //     return true;
    // }


    //middle_point,
    //cross_point,
    //""+building_ID(接続道路を作成しようとしているbuildingのid番号),
    //""+k(k番目の拡張道路　[1~k~roadのsize]),
    //"road",
    //m (拡張道路のnodeのm番目 [0~m~拡張道路のnode数]),
    //buildingArea,
    //いやこれSearch変数使って全探索の部分省けるでしょ 2018/07/12
    //接続道路を作成したい建物から伸ばした垂線と、
    //引数で指定されているobject(建物もしくは道路)の辺が交差している場合falseを返す関数
    private Boolean checkCrossingBuilding(Point2D.Double origin_point,
                                          Point2D.Double connect_point,
                                          String building_ID,
                                          String object_ID,
                                          String object_type,
                                          int object_edge_num,
                                          Point buildingArea){
        Point2D.Double point_A = new Point2D.Double();
        Point2D.Double point_B = new Point2D.Double();

        int building_list_size = Integer.parseInt(building_manager_.getBuildingNodeID());


        //ArrayList<String> buildingList = area_manager_.getSearchBuildingList(buildingArea);
        for (int i = 1; i<building_list_size;i++) {
            //String buildingID = buildingList.get(i);

            //対象が自分自身のため処理はスルー
            if (i == Integer.parseInt(building_ID)){
                //System.out.println("自分自身のためcontinue");
                continue;
            }

            //建物を構成するnodeをcheck＿Roadに格納
            ArrayList<String> check_Road = building_manager_.getBuildingNodeList(String.valueOf(i));

            //建物のnodeの分だけ回す
            for (int k = 0; k<check_Road.size()-1;k++ ) {
                //if (object_type == "building" &&  i == Integer.parseInt(object_ID) && k == object_edge_num) continue;
                if (object_type == "building" &&  i == Integer.parseInt(object_ID) && object_edge_num == k){
                    //System.out.println("接続したい先の建物のEDGEなためcontinue");
                    continue;
                }
                point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
                point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));

                if (checkCrossingLineSegment(origin_point,connect_point,point_A,point_B)) {

                    return false;
                }
            }
        }

        int road_list_size = Integer.parseInt(road_manager_.getRoadNodeID());

        //ArrayList<String> roadList = area_manager_.getSearchRoadList(buildingArea);

        for (int i = 1; i<road_list_size;i++ ) {

            //String roadID = roadList.get(i);

            ArrayList<String> check_Road = road_manager_.getRoadNodeList(String.valueOf(i));

            for (int k = 0; k<check_Road.size()-1;k++ ) {
                if (object_type == "road" &&  i == Integer.parseInt(object_ID) && k == object_edge_num) continue;
                point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
                point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));

                if (checkCrossingLineSegment(origin_point,connect_point,point_A,point_B)) {

                    return false;
                }
            }
        }
        return true;
    }


    //第1,2引数と第3,4引数で作られる２線分を比べ交差していたらtrueを返す
    private Boolean checkCrossingLineSegment(Point2D.Double a,Point2D.Double b,Point2D.Double c,Point2D.Double d){

        Line2D.Double aa = new Line2D.Double(a,b);
        Line2D.Double bb = new Line2D.Double(c,d);
        if (aa.intersectsLine(bb)) {
          return true;
        }
        return false;
    }

    //建物の1辺から引いた垂線と拡張道路の1辺との交点と,
    //建物の所属する範囲から検索可能な全てのnodeの距離が0.75より大きいならばtrue そうでなければfalse
    //default_road_width=0.75 接続道路の幅は1.5に固定していて(森島)　cross_pointを中心に左右に開いて作るため、1.5の半分の値が設定されている。
    //                                  　   　cross_point,      buildingArea
    private Boolean checkContainNode(Point2D.Double point,Point buildingArea){
        //x,yから指定した円の範囲にnodeがあった場合falseを返す なかった場合はtrue
        Double distance;
        int node_size = node_manager_.getNodeSize();
        ArrayList<String> nodeList = area_manager_.getSearchNodeList(buildingArea);

        for (int i=0; i<nodeList.size();i++ ) {
            String nodeID = nodeList.get(i);
            Point2D.Double comp_point = new Point2D.Double(node_manager_.getX(nodeID),node_manager_.getY(nodeID));
            distance = Math.hypot(comp_point.getX()-point.getX(),comp_point.getY()-point.getY());
            if (distance < default_road_width) {
                return false;
            }
        }
        return true;
    }


    public NodeManager getNodeManeger(){
        return node_manager_;
    }
    public BuildingManager getBuildingManeger(){
        return building_manager_;
    }
    public RoadManager getRoadManeger(){
        return road_manager_;
    }

    public void removeRelatedObjects(String building_id){
      ArrayList<String> check_road_array = building_manager_.getBuildingConnectedRoad(building_id);
      ArrayList<String> check_building_array = building_manager_.getBuildingConnectedBuilding(building_id);

      building_manager_.setRemoveBuildingList(building_id);

      if (check_road_array.size() > 0) {
        for (int i=0; i<check_road_array.size();i++ ) {
          removeRelatedRoadObjects(check_road_array.get(i));
        }
      }
      if (check_building_array.size()>0) {
        for (int i=0; i<check_building_array.size();i++ ) {
          removeRelatedObjects(check_building_array.get(i));
        }
      }
      return;
    }

    public void removeRelatedRoadObjects(String road_id){
      ArrayList<String> check_road_array = road_manager_.getRoadConnectedRoad(road_id);
      ArrayList<String> check_building_array = road_manager_.getRoadConnectedBuilding(road_id);
      road_manager_.setRemoveRoadList(road_id);

      if (check_road_array.size() > 0) {
        for (int i=0; i<check_road_array.size();i++ ) {
          removeRelatedRoadObjects(check_road_array.get(i));
        }
      }
      if (check_building_array.size()>0) {
        for (int i=0; i<check_building_array.size();i++ ) {
          removeRelatedObjects(check_building_array.get(i));
        }
      }
      return;
    }

    //角度が45°〜135°に収まって入ればfalseを返す
    //45°〜135°で負の角度は考慮しなくていいのか検証
    //  おそらく、道路の反対側の辺から接続道路が伸びるエラーは、建物が反時計に管理されていない場合に45°~135°と合間って起きていると考えられる。
    //計算方法については要調査 大丈夫そう
    //角度が急になっていないか判定          middle_point,     check_point, tmp_start_point,   tmp_end_point
    private Boolean checkProperDegree(Point2D.Double a,Point2D.Double b,Point2D.Double c,Point2D.Double d){

        /*
            [ベクトルの内積の公式1]
            a = {2,3} , b = {1,4} のベクトル{x,y}があった時
            内積〈a,b〉 = 2*1 + 3*4
                       = 14
            と求められる。

            [ベクトルの内積の公式2]
            内積〈a,b〉= ||a|| * ||b|| * cosθ　　（||a||,||b|| : ベクトルa,bのユークリッド距離）
            これより、
                cosθ = 〈a,b〉/ (||a|| * ||b||)
            によってcosθを求めることができる。
         */

        //第1,2引数の2点間の距離と第3,4引数の2点間の距離を取得
        //||a||,||b||の計算をしている
        double length_A = Math.hypot(b.getX()-a.getX(),b.getY()-a.getY());
        double length_B = Math.hypot(d.getX()-c.getX(),d.getY()-c.getY());

        //第1,2引数、第3,4引数　各2点間の原点からの相対位置(つまりベクトル)を保持
        Point2D.Double va = new Point2D.Double(b.getX()-a.getX(),b.getY()-a.getY());
        Point2D.Double vb = new Point2D.Double(d.getX()-c.getX(),d.getY()-c.getY());

        //内積〈a,b〉の計算をしている。公式1
        double dot_product = va.getX() * vb.getX() + va.getY() * vb.getY();

        //公式2　[ cosθ = 〈a,b〉/ (||a|| * ||b||) ] の実行
        //第1,2引数の線分と第3,4引数の線分のベクトル間のcosθを取得
	    double cos_sita = dot_product / ( length_A * length_B );

	    //cosθからθを求める
        //acosはアークコサイン(逆余弦：cos^-1)の関数
	    double sita = Math.acos( cos_sita );

	    //ラジアンでなく0～180の角度でほしい場合はコメント外す　　??
        //弧度法(~π)から度数法(~°)へ変換
	    sita = sita * 180.0 / Math.PI;

	    //以下の計算により、-179° <= degree <= 179° と範囲調整される
	    double degree = sita%180.0;
        //System.out.println("角度:"+degree);
        if(45 < degree && degree < 135){
            return false;
        }
        //System.out.println("角度が急なためcontinue");
        return true;
    }
}
