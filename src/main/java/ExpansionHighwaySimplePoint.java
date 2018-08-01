import java.util.*;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

public class ExpansionHighwaySimplePoint {
    //道路を正三角形状に拡張する際の正三角形の高さ
    private static Double TRIANGLE_ROAD_HEIGHT = 1.5*Math.sqrt(3);
    /*
    道路を拡張する際に、highwayの辺に対して垂直方向に2点nodeを作成する．
    highwayの1点と作成した2点は1辺が3mの正三角形となる．
    作成した2点は正三角形の1辺を形成しており、それぞれの点はhighwayの辺から上下に、正三角形の1辺の半分の距離に作成されるため、
    widthには基本的な幅員(3m)の半分の値を設定する．
    */
    private static Double HALF_OF_EXTENDED_ROAD_WIDTH = 1.5;

    //highway内のnodeごとに、接続されているhighway内のnode集合
    private HashMap<String,ArrayList<String>> connectedNodesForHighwayNode = new HashMap<String,ArrayList<String>>();
    //connectedNodesForHighwayNodeを元に作成されるroadのnodeリスト　内容はconnectedNodesForHighwayNodeと同じ。書く場所が変わって名前変えてるだけ。
    private HashMap<String,ArrayList<String>> node_have_road_nodes = new HashMap<String,ArrayList<String>>();
    /*
    node_have_road_nodesについて、他のhighwayのnode方向に対して垂直に作成されたlineのそれぞれの片側のnodeを保持するMap.
    最後に道路を作成する際のnode選択で使用される。
    highwayのnode周辺のlineを　-180°~180°　の範囲で（line(矢印)の向きも含めて）反時計回りに整理し、
    矢印の終点(矢印で三角のある方　←　こっちじゃない)にあるnodeを保持する。
    */
    private HashMap<String,ArrayList<String>> maked_new_node_map = new HashMap<String,ArrayList<String>>();

    //すでに接続済みのnode
    private ArrayList<String> connected_node_list = new ArrayList<String>();

    private NodeManager    nm;
    private HighwayManager hm;
    private RoadManager    rm;

    public ExpansionHighwaySimplePoint(NodeManager nm,HighwayManager hm,RoadManager rm) {
        this.nm = nm;
        this.hm = hm;
        this.rm = rm;
    }


    public void expantionHighway() {
        //各highwayに入っている全てのnodeに対して、接続されているnodeを保存
        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {
            ArrayList<String> highway = (ArrayList)hm.getTmpHighwayList().get(i);
            for (int j=0; j<highway.size(); j++) {
                if (connectedNodesForHighwayNode.containsKey(highway.get(j)) != true) {
                    connectedNodesForHighwayNode.put(highway.get(j), connectedNodes(highway.get(j)));
                }
            }
        }

        //全てのhighwayのnodeに対して，拡張したnode集合を紐付け Entry<K,V>
        //forじゃなくEntry使ってるのはおしゃれか？
        for(Map.Entry<String,ArrayList<String>> entry : connectedNodesForHighwayNode.entrySet()){
            node_have_road_nodes.put(entry.getKey(), makePointForExtendedRoad(entry.getKey()));
        }
        //拡張したnode集合を用いて道路を作成
        makeNodeRoad();

    }

    //引数のnodeに隣接しているnode集合を返す
    private ArrayList<String>  connectedNodes(String gmlNodeID){
        ArrayList<String> connectedNodes = new ArrayList<>();
        //全てのhighwayについて、引数のnodeを含むものに対して調べる
        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {
            ArrayList<String> oneOfHighwayList = (ArrayList)hm.getTmpHighwayList().get(i);
            int nodeNumber = oneOfHighwayList.indexOf(gmlNodeID);

            if(nodeNumber >= 0){
                //引数のnodeがhighway内において最初にある場合
                if(nodeNumber == 0){
                    //そのhighway内にて2番目にあるnodeと繋がる　● - ○   ●:調べるnode  ○:調べるnodeに接続しているnode
                    connectedNodes.add(oneOfHighwayList.get(1));
                }
                //引数のnodeがhighway内において最後にある場合
                else if(nodeNumber == oneOfHighwayList.size()-1) {
                    //そのhighway内にて最後から1つ手前のnodeと繋がる　○ - ●
                    connectedNodes.add(oneOfHighwayList.get(nodeNumber-1));
                }
                //引数のnodeがhighway内において最初と最後でない場合
                else {
                    //そのhighway内にて引数のnodeの前後のnodeと繋がる ○ - ● - ○
                    connectedNodes.add(oneOfHighwayList.get(nodeNumber+1));
                    connectedNodes.add(oneOfHighwayList.get(nodeNumber-1));
                }
            }
        }

        return connectedNodes;
    }

    /*
    highwayのnode(highwayNode)を一つずつ拡張し,
    反時計まわりに並び替え、
    交差した部分を調整し、
    tmp_Road_NodeとmakeNodeListを作成する。
    */
    private ArrayList<String> makePointForExtendedRoad(String highwayNode){

        ArrayList<Double> degreeList = new ArrayList<Double>();
        ArrayList<Line2D.Double> triangleBottomLineList = new ArrayList<Line2D.Double>();
        ArrayList<String> connectedNodes = connectedNodesForHighwayNode.get(highwayNode);

        //引数のhighwayのnodeをスタート地点として設定
        Point2D.Double start = new Point2D.Double(nm.getX(highwayNode),nm.getY(highwayNode));
        Point2D.Double end = new Point2D.Double();
        Point2D.Double difference = new Point2D.Double();
        double radianForStartToEnd;
        double radianPlus90;
        double radianMinus90;
        double degree;
        Point2D.Double triangleBottom = new Point2D.Double();
        Point2D.Double radianPlus90Point = new Point2D.Double();
        Point2D.Double radianMinus90Point = new Point2D.Double();

        //現在対象としているhighwayのnode1つ(highwayNode)に接続しているhighwayのnode分回す
        for(String connectedNode : connectedNodes){

            //引数のnodeに接続しているnodeをゴール地点として設定
            end.setLocation(nm.getX(connectedNode),nm.getY(connectedNode));
            //以下でスタート地点とゴール地点との角度を出すためだけの変数を設定
            difference.setLocation(end.getX()-start.getX(),end.getY()-start.getY());
            //atan2関数は　Y/X　で計算される傾き(tan)をラジアン値で返す x軸に対しての角度が出る（-180°〜180°）
            radianForStartToEnd = Math.atan2(difference.getY(),difference.getX());
            //atan2関数の値を度数（°）に変換している
            degree = (radianForStartToEnd*180)/Math.PI;
            //startを起点にθ方向に1.5x√3 の距離にtriangleBottomを設定
            triangleBottom.setLocation(Math.cos(radianForStartToEnd)*TRIANGLE_ROAD_HEIGHT,Math.sin(radianForStartToEnd)*TRIANGLE_ROAD_HEIGHT);
            //triangleBottomを起点に正三角形の底辺の2点をpoint_makeとしてそれぞれ作成
            radianPlus90  = ((degree+90)*Math.PI)/180.0;
            radianMinus90 = ((degree-90)*Math.PI)/180.0;
            radianPlus90Point.setLocation (Math.cos(radianPlus90) *HALF_OF_EXTENDED_ROAD_WIDTH+triangleBottom.getX()+start.getX(),
                                           Math.sin(radianPlus90) *HALF_OF_EXTENDED_ROAD_WIDTH+triangleBottom.getY()+start.getY());
            radianMinus90Point.setLocation(Math.cos(radianMinus90)*HALF_OF_EXTENDED_ROAD_WIDTH+triangleBottom.getX()+start.getX(),
                                           Math.sin(radianMinus90)*HALF_OF_EXTENDED_ROAD_WIDTH+triangleBottom.getY()+start.getY());
            //radianPlus90Pointをline(矢印)の始点、radianMinus90Pointを終点として作成
            Line2D.Double triangleBottomLine = new Line2D.Double(radianPlus90Point,radianMinus90Point);


            /*
            作成したlineを反時計回りで（lineに対する垂線の）角度が大きい順に並び替える処理
            lineの向きは総じて時計回りである点に注意！！
            角度の範囲は-180°~180°で行われる
            degreeListとtriangleBottomLineListを作成
            */
            if(degreeList.size() != 0){

                for(int i=0;i<degreeList.size();i++){

                    /*
                    調べているdegreeがlistのi番目のdegreeより大きい場合、i番目に調べているdegreeを格納する
                    その際、i番目以降に格納されているものはi+1番目にずれて格納される
                    */
                    if(degree > degreeList.get(i)){
                        degreeList.add(i,degree);
                        triangleBottomLineList.add(i,triangleBottomLine);
                        break;
                    }

                    //調べているdegreeがlistのどの内容よりも小さかった場合、普通にケツに格納する
                    if(i == degreeList.size()-1){
                        degreeList.add(degree);
                        triangleBottomLineList.add(triangleBottomLine);
                        break;
                    }
                }
            }
            //角度の調べが最初の場合は素直にdegreeとlineをリストへ入れる
            else{
                degreeList.add(degree);
                triangleBottomLineList.add(triangleBottomLine);
            }
        }


        //交差したtriangleBottomLineの始点終点補正処理
        //triangleBottomLineが交差している場合に、2本のtriangleBottomLineの始点と終点を交点で設定し直す
        if(triangleBottomLineList.size() > 1){
            Line2D.Double line1 = triangleBottomLineList.get(triangleBottomLineList.size()-1);
            for(int i=0;i<triangleBottomLineList.size();i++){
                Line2D.Double line2 = triangleBottomLineList.get(i);
                if(line1.intersectsLine(line2)){
                    Point2D.Double correct_point = correctionPoint(line1,line2);
                    if(i == 0){
                        ((Line2D.Double)triangleBottomLineList.get(triangleBottomLineList.size()-1)).setLine(line1.getP1(),correct_point);
                    }else{
                        ((Line2D.Double)triangleBottomLineList.get(i-1)).setLine(line1.getP1(),correct_point);
                    }
                    ((Line2D.Double)triangleBottomLineList.get(i)).setLine(correct_point,line2.getP2());
                }
                line1 = line2;
            }
        }

        //調整した交点はダブルカウントしつつ、反時計回りでlineのnodeをpointo_listに格納する処理
        ArrayList<Point2D.Double> point_list= new ArrayList<Point2D.Double>();
        for(Line2D.Double line:triangleBottomLineList) {
            point_list.add((Point2D.Double) line.getP1());
            point_list.add((Point2D.Double) line.getP2());
//            System.out.println("line.getP1 = " + line.getP1());
//            System.out.println("line.getP2 = " + line.getP2());
        }

        ArrayList<String> tmp_Road_Node = new ArrayList<String>();
        HashMap<String,Double> map = new HashMap<String,Double>();
        Point2D.Double tmp = new Point2D.Double();
        Point2D.Double point = new Point2D.Double();
        String first_node = null;
        String current_node = null;

        ArrayList<String> makedNodeList = new ArrayList<String>();

        /*
        道路の拡張時に作成したnodeをnmに追加する
        各lineの向きの終点をmakeNodeListに格納
        lineの交点のダブルカウントの削除処理もある
        point_listは反時計回りに処理している
        */
        for(int i=point_list.size()-1;i>=0; i--){

            map = new HashMap<String,Double>();
            point.setLocation(point_list.get(i));

            /*
            lineの交差処理で得られた交点がpoint_listではダブルカウントされているため、ダブルカウントの削除処理。
            ここの削除処理が発生する場所は交差した2つのlineのうち、反時計回りで後ろ側のlineの終点に当たる。
            したがって、current_node（前側のlineの始点）をmakeNodeListに入れることで、makeNodeListの漏れを防止している。
            */
            if(point.getX() == tmp.getX() && point.getY() == tmp.getY()){
                makedNodeList.add(current_node);
                continue;
            }

            map.put("y",point.getY());
            map.put("x",point.getX());
            //gmlに記載するnodeID番号が帰ってくる
            current_node = nm.addGmlNode(map);
            //highwayのnode周辺に作成するnodeとして現在のnodeを格納
            tmp_Road_Node.add(current_node);

//            各lineの終点をmekedNodeListへ格納
            if(i % 2 == 1){
                makedNodeList.add(current_node);
            }
/*
            道路の面のnode管理は、始まりのnodeと終わりのnodeが同じになるように反時計回りに一周する。
            終わりのnode用に始まりのnodeをここで保持しておく
*/
            if(first_node ==null){
                first_node = current_node;
            }
            //line同士の交点はダブルカウントされているため、座標で判断できるようにtmpのpointを作成
            tmp.setLocation(point);
        }
        System.out.println("makedNodeList = " + makedNodeList);

/*
        調べているnodeがhighwayの端である場合
        拡張で作成した2点と、調べているnode1点の合計3点で、三角形の道路を作成する。
*/
        if (tmp_Road_Node.size()==2) {
            tmp_Road_Node.add(highwayNode);
        }

        //道路の面のnode管理で、終わりのnodeを格納
        tmp_Road_Node.add(first_node);

        //makedNodeListを格納
        maked_new_node_map.put(highwayNode,makedNodeList);

        //残骸
        //rm.setTmpRoadList(tmp_Road_Node);

        System.out.println("tmp_Road_Node = " + tmp_Road_Node);
        return tmp_Road_Node;
    }


    //2直線の交点座標を求める関数
    private Point2D.Double correctionPoint(Line2D.Double line1,Line2D.Double line2){
        //直角にする処理がない

        Point2D a = line1.getP1(); //始点
        Point2D b = line1.getP2(); //終点
        Point2D c = line2.getP1(); //始点
        Point2D d = line2.getP2(); //終点
        //参照url https://gist.github.com/yoshiki/7702066
        Double du    = ( b.getX() - a.getX()) * (d.getY() - c.getY()) - (b.getY() - a.getY()) * (d.getX() - c.getX());
        Double  u    = ((c.getX() - a.getX()) * (d.getY() - c.getY()) - (c.getY() - a.getY()) * (d.getX() - c.getX())) / du;
        Double tmp_X = a.getX() + u * (b.getX() - a.getX());
        Double tmp_Y = a.getY() + u * (b.getY() - a.getY());

        Point2D.Double correct_point = new Point2D.Double(tmp_X,tmp_Y);

        return correct_point;
    }

    //highwayの2点間の距離が短い場合の道路作成  (node_have_road_nodesの調整)
    //四角形部分の作成 (rmへ追加)
    //2点間の距離が短い場合を含めた node_have_road_nodes をrmへ追加
    private void makeNodeRoad(){

        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {

            ArrayList<String> check_list = (ArrayList)hm.getTmpHighwayList().get(i);

            for (int j=0; j<check_list.size()-1; j++) {

                //highwayのnodeを2つ取り出す
                String node1 = check_list.get(j);
                String node2 = check_list.get(j+1);

                //highwayの辺と交差するlineに含まれているmakeNodeList内のnodeについて，node_have_road_nodesにおけるindexを返す
                int use_line_node1 = nearestLine(node1,node2);
                int use_line_node2 = nearestLine(node2,node1);

                ArrayList<String> node1_list = node_have_road_nodes.get(node1);
                ArrayList<String> node2_list = node_have_road_nodes.get(node2);

                //highwayのnode同士の距離が狭い( < (1.5√3 * 2) ) 場合、
                //2点間の垂直2等分線上に新たに2点nodeを作成し、node1側とnode2側それぞれのnode_have_road_nodesを作成した2点で調整
                if (checkTooClose(node1,node2)) {
                    System.out.println("二点が近い場合の処理");

                    //node1とnode2のpoint作成と角度sitaを計算
                    Point2D.Double node1Point = new Point2D.Double(nm.getX(node1),nm.getY(node1));
                    Point2D.Double node2Point = new Point2D.Double(nm.getX(node2),nm.getY(node2));
                    double sita = Math.atan2(node2Point.getY()-node1Point.getY(),node2Point.getX()-node1Point.getX());
                    double degree = (sita*180)/Math.PI;

                    //node1とnode2の間の中心点を作成
                    Point2D.Double middlePoint = new Point2D.Double((node2Point.getX()+node1Point.getX())/2,(node2Point.getY()+node1Point.getY())/2);

                    //中点の上下に2点作成　つまり，node1とnode2との垂直2等分線上にnodeを2点作成する．
                    Double sitaMinus = ((degree-90)*Math.PI)/180.0;
                    Double sitaPlus = ((degree+90)*Math.PI)/180.0;
                    Point2D.Double minusPoint = new Point2D.Double(Math.cos(sitaMinus)*HALF_OF_EXTENDED_ROAD_WIDTH+middlePoint.getX(),Math.sin(sitaMinus)*HALF_OF_EXTENDED_ROAD_WIDTH+middlePoint.getY());
                    Point2D.Double plusPoint = new Point2D.Double(Math.cos(sitaPlus)*HALF_OF_EXTENDED_ROAD_WIDTH+middlePoint.getX(),Math.sin(sitaPlus)*HALF_OF_EXTENDED_ROAD_WIDTH+middlePoint.getY());

                    //作成したnodeの2点をnmに追加
                    HashMap<String,Double> mapMinus = new HashMap<String,Double>();
                    mapMinus.put("y",minusPoint.getY());
                    mapMinus.put("x",minusPoint.getX());
                    String minusNodeID = nm.addGmlNode(mapMinus);

                    HashMap<String,Double> mapPlus = new HashMap<String,Double>();
                    mapPlus.put("y",plusPoint.getY());
                    mapPlus.put("x",plusPoint.getX());
                    String plusNodeID = nm.addGmlNode(mapPlus);


                    //作成した2点と交換しなければならないnodeを交換する．
                    ((ArrayList)node_have_road_nodes.get(node1)).set(use_line_node1,minusNodeID);
                    ((ArrayList)node_have_road_nodes.get(node1)).set(use_line_node1+1,plusNodeID);
                    //node_have_road_nodesの最初と最後は同じnodeである．
                    //ここでは，その同じnodeの片側が作成した2点のどちらかにされた時、もう片方も同じ変更したnodeにする処理
                    if (use_line_node1 == 0) {
                        System.out.println("使うnodeが0の場合");
                        ((ArrayList)node_have_road_nodes.get(node1)).set(node1_list.size()-1,minusNodeID);
                    }
                    else if (use_line_node1 == node1_list.size()-1) {
                        System.out.println("使うnodeがlistの最大値の場合");
                        ((ArrayList)node_have_road_nodes.get(node1)).set(0,plusNodeID);
                    }
                    else {
                        System.out.println("使うnodeがlistの最初または最後でない場合");
                    }

                    //node2について上記の1段落と同じ処理
                    ((ArrayList)node_have_road_nodes.get(node2)).set(use_line_node2,plusNodeID);
                    ((ArrayList)node_have_road_nodes.get(node2)).set(use_line_node2+1,minusNodeID);
                    if (use_line_node2 == 0) {
                        System.out.println("使うnodeが0の場合");
                        ((ArrayList)node_have_road_nodes.get(node2)).set(node2_list.size()-1,plusNodeID);
                    }else if (use_line_node2 == node2_list.size()-1) {
                        System.out.println("使うnodeがlistの最小値の場合");
                        ((ArrayList)node_have_road_nodes.get(node2)).set(0,minusNodeID);
                    }else{
                        System.out.println("使うnodeがlistの最初または最後でない場合");
                    }
                }
                else {
                    //highwayのnode周辺のnodeで作成した道路同士の間を埋める細長い四角形の道路を作成する処理
                    ArrayList<String> squareRoad = new ArrayList<String>();
                    squareRoad.add(node1_list.get(use_line_node1));
                    squareRoad.add(node2_list.get(use_line_node2+1));
                    squareRoad.add(node2_list.get(use_line_node2));
                    squareRoad.add(node1_list.get(use_line_node1+1));
                    squareRoad.add(node1_list.get(use_line_node1));
                    rm.setTmpRoadList(squareRoad);
                }
            }
        }

        //highwayのnode周辺のnodeで作成できる道路を作成
        for(Map.Entry<String,ArrayList<String>> entry : node_have_road_nodes.entrySet()){
            System.out.println();
            System.out.println("NodeID:"+entry.getKey());
            System.out.println("haveRoadNodeNum:"+entry.getValue().size());

            rm.setTmpRoadList(entry.getValue());

        }

    }

    //ここまでに一つのhighwayのnode周辺に,他のhighwayのnode方向に対するroad用の頂点と辺（line）を作成した。
    //highwayの辺と交差するlineに関するmakeNodeListに含まれているnodeについて，node_have_road_nodes内でのindexを返す
    private int nearestLine(String originNode,String connectNode){
        Point2D.Double originPoint = new Point2D.Double(nm.getX(originNode),nm.getY(originNode));
        Point2D.Double connectPoint = new Point2D.Double(nm.getX(connectNode),nm.getY(connectNode));
        //double sita = Math.atan2(originPoint.getY()-connectPoint.getY(),originPoint.getX()-connectPoint.getX());

        double sita = Math.atan2(connectPoint.getY()-originPoint.getY(),connectPoint.getX()-originPoint.getX());
        Point2D.Double checkPoint = new Point2D.Double(Math.cos(sita)*3.0+originPoint.getX(),Math.sin(sita)*3.0+originPoint.getY());
        Line2D.Double originToConnectLine = new Line2D.Double(originPoint,checkPoint);

        ArrayList<String> checkList = this.node_have_road_nodes.get(originNode);
        int startLineNum = 0;

        ArrayList<String> useNodeList = this.maked_new_node_map.get(originNode);


        for(int i=0;i<checkList.size()-1;i++){
            //接続に使わないnodeを飛ばす
            if(useNodeList.contains(checkList.get(i)) == false)continue;

            Point2D.Double point1 = new Point2D.Double(nm.getX(checkList.get(i)),nm.getY(checkList.get(i)));
            Point2D.Double point2 = new Point2D.Double(nm.getX(checkList.get(i+1)),nm.getY(checkList.get(i+1)));
            Line2D.Double listLine = new Line2D.Double(point1,point2);
            if (originToConnectLine.intersectsLine(listLine)) {
                System.out.println("線が交差 i(makeNodeの点):"+i);
                startLineNum = i;
                break;
            }
        }
        //makeNodeListの中でhighwayの辺と交差するlineに関するnodeのindexを返す
        return startLineNum;
    }

    /*
    highwayの2点間が3*√3より近ければtrueを返す関数
    3mは最低限の基本幅員
    3√3とは　1辺が3mの正三角形の高さ(1.5√3)の2個分
    */
    private Boolean checkTooClose(String node1,String node2){

        Point2D.Double point1 = new Point2D.Double(nm.getX(node1),nm.getY(node1));
        Point2D.Double point2 = new Point2D.Double(nm.getX(node2),nm.getY(node2));
        Double distance = Math.hypot(point1.getX()-point2.getX(),point1.getY()-point2.getY());

        if (distance <= TRIANGLE_ROAD_HEIGHT*2) {
            return true;
        }
        else {
            return false;
        }
    }

}
