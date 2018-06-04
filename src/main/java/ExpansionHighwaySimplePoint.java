import java.util.*;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

public class ExpansionHighwaySimplePoint {
    private static Double default_road_distance_ = 1.5;
    private static Double default_road_width_ = 1.5;

    //nodeに対して接続しているnodeのリスト
    private HashMap<String,ArrayList<String>> node_Connect_Way_Map = new HashMap<String,ArrayList<String>>();
    //そのnodeを元に作成したroadのnodeリスト
    private HashMap<String,ArrayList<String>> node_have_road_nodes = new HashMap<String,ArrayList<String>>();
    //roadのnodeリストを作成した際に新しく作られたnodemap
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


    public void ExpantionHighway() {
        //HighwayListに入っている全てのnodeに対して接続されているnodeを保存
        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {
            ArrayList<String> check_list = (ArrayList)hm.getTmpHighwayList().get(i);
            for (int j=0; j<check_list.size(); j++) {
                if (node_Connect_Way_Map.containsKey(check_list.get(j)) != true) {
                    node_Connect_Way_Map.put(check_list.get(j),nodeConnectedHighwayNode(check_list.get(j)));
                }
            }
        }

        //System.out.println("makePointRoad");

        for(Map.Entry<String,ArrayList<String>> entry : node_Connect_Way_Map.entrySet()){
            System.out.println();
            //System.out.println("NodeID:"+entry.getKey());
            //System.out.println("connectNum:"+entry.getValue().size());
            node_have_road_nodes.put( entry.getKey() , makePointRoad( entry.getKey() ) );
        }
        makeNodeRoad();

    }

    //引数のnodeに隣接しているnodeのリストを返す
    private ArrayList<String> nodeConnectedHighwayNode(String node_ID){
        ArrayList<String> connected_node = new ArrayList<String>();
        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {
            ArrayList<String> check_highway = (ArrayList)hm.getTmpHighwayList().get(i);
            int node_index = check_highway.indexOf(node_ID);
            if(node_index >= 0){
                if(node_index == 0){
                    connected_node.add(check_highway.get(1));
                }else if(node_index ==check_highway.size()-1){
                    connected_node.add(check_highway.get(node_index-1));
                }else{
                    connected_node.add(check_highway.get(node_index+1));
                    connected_node.add(check_highway.get(node_index-1));
                }
            }
        }

        //System.out.println();
        //System.out.println("nodeID:"+node_ID);
        //System.out.println("connectNodeSize:"+connected_node.size());
        return connected_node;
    }

    private ArrayList<String> makePointRoad(String node_ID){

        ArrayList<Double> degree_list = new ArrayList<Double>();
        ArrayList<Line2D.Double> line_list = new ArrayList<Line2D.Double>();
        ArrayList<String> connected_Node_List = node_Connect_Way_Map.get(node_ID);

        Point2D.Double point_start = new Point2D.Double(nm.getX(node_ID),nm.getY(node_ID));
        Point2D.Double point_end = new Point2D.Double();
        Point2D.Double point_def = new Point2D.Double();
        double radian_start_end;
        double radian_make_1;
        double radian_make_2;
        double degree;
        Point2D.Double point_tmp = new Point2D.Double();
        Point2D.Double point_make_1 = new Point2D.Double();
        Point2D.Double point_make_2 = new Point2D.Double();
        //System.out.println("listSize:"+connected_Node_List.size());

        for(String connect_node : connected_Node_List){
            //System.out.println("connectNode:"+connect_node);

            //point_startを起点にθ方向に1.5x√3 の距離にpoint_tmpを設定し、point_tmpを起点に正三角形の2点をpoint_makeとして作成
            point_end.setLocation(nm.getX(connect_node),nm.getY(connect_node));
            point_def.setLocation(point_end.getX()-point_start.getX(),point_end.getY()-point_start.getY());
            radian_start_end = Math.atan2(point_def.getY(),point_def.getX());
            degree = (radian_start_end*180)/Math.PI;
            point_tmp.setLocation(Math.cos(radian_start_end)*default_road_distance_*Math.sqrt(3),Math.sin(radian_start_end)*default_road_distance_*Math.sqrt(3));
            radian_make_2 = ((degree-90)*Math.PI)/180.0;
            radian_make_1 = ((degree+90)*Math.PI)/180.0;

            point_make_1.setLocation(Math.cos(radian_make_1)*default_road_width_+point_tmp.getX()+point_start.getX(),
                                     Math.sin(radian_make_1)*default_road_width_+point_tmp.getY()+point_start.getY());

            point_make_2.setLocation(Math.cos(radian_make_2)*default_road_width_+point_tmp.getX()+point_start.getX(),
                                     Math.sin(radian_make_2)*default_road_width_+point_tmp.getY()+point_start.getY());

            Line2D.Double line = new Line2D.Double(point_make_1,point_make_2);

            //引いた線の並び替え処理 connect_nodeへの角度が大きい順にdegreeとlineのリストを並び替え
            if(degree_list.size() != 0){
                for(int i=0;i<degree_list.size();i++){
                    if(degree > degree_list.get(i)){
                        degree_list.add(i,degree);
                        line_list.add(i,line);
                        // System.out.println("insertDegreeList");
                        // System.out.println("i:"+i);
                        // System.out.println("degree:"+degree);
                        // System.out.println("lineP1 X:"+line.getX1()+" Y:"+line.getY1());
                        // System.out.println("lineP2 X:"+line.getX2()+" Y:"+line.getY2());
                        break;
                    }

                    if(i == degree_list.size()-1){
                        degree_list.add(degree);
                        line_list.add(line);
                        // System.out.println("addDegreeList");
                        // System.out.println("degree:"+degree);
                        // System.out.println("lineP1 X:"+line.getX1()+" Y:"+line.getY1());
                        // System.out.println("lineP2 X:"+line.getX2()+" Y:"+line.getY2());
                        break;
                    }
                }
            }else{
                degree_list.add(degree);
                line_list.add(line);
                // System.out.println("addFirstLine");
                // System.out.println("degree:"+degree);
                // System.out.println("lineP1 X:"+line.getX1()+" Y:"+line.getY1());
                // System.out.println("lineP2 X:"+line.getX2()+" Y:"+line.getY2());
            }
            //System.out.println("degreeListSize:"+degree_list.size());
        }


        //補正処理　lineが交差しているか確認し、交差しているなら交点で2本のlineを繋げ直す
        // System.out.println("引いた線が交差しているか判定");
        if(line_list.size() > 1){
            Line2D.Double line1 = line_list.get(line_list.size()-1);
            for(int i=0;i<line_list.size();i++){
                Line2D.Double line2 = line_list.get(i);
                if(line1.intersectsLine(line2)){
                    // System.out.println("交差時処理");
                    Point2D.Double correct_point = correctionPoint(line1,line2);
                    if(i == 0){
                        ((Line2D.Double)line_list.get(line_list.size()-1)).setLine(line1.getP1(),correct_point);
                    }else{
                        ((Line2D.Double)line_list.get(i-1)).setLine(line1.getP1(),correct_point);
                    }
                    ((Line2D.Double)line_list.get(i)).setLine(correct_point,line2.getP2());
                }
                line1 = line2;
            }
        }


        // System.out.println("line補正後");
        ArrayList<Point2D.Double> point_list= new ArrayList<Point2D.Double>();
        for(Line2D.Double line:line_list){
            point_list.add((Point2D.Double)line.getP1());
            point_list.add((Point2D.Double)line.getP2());
            // System.out.println("lineP1 X:"+line.getX1()+" Y:"+line.getY1());
            // System.out.println("lineP2 X:"+line.getX2()+" Y:"+line.getY2());
        }


        //System.out.println("makeroad");

        ArrayList<String> tmp_Road_Node = new ArrayList<String>();
        HashMap<String,Double> map = new HashMap<String,Double>();
        Point2D.Double tmp = new Point2D.Double();
        Point2D.Double point = new Point2D.Double();
        String first_node = null;
        String current_node = null;

        ArrayList<String> makedNodeList = new ArrayList<String>();

        //作成した正三角形の2点をnmに追加し、
        //正三角形同士の間を埋める道路を作成するためのedgeを構成するnodeをmakeNodeListに記憶する
        for(int i=point_list.size()-1;i>=0; i--){
            map = new HashMap<String,Double>();
            //System.out.println("i:"+i);
            point.setLocation(point_list.get(i));
            //System.out.println("P X:"+point.getX()+" Y:"+point.getY());
            if(point.getX() == tmp.getX() && point.getY() == tmp.getY()){
                //System.out.println("前のポイントと同じなためcontinue");
                makedNodeList.add(current_node);
                continue;
            }

            map.put("y",point.getY());
            map.put("x",point.getX());
            //System.out.println("Map P X:"+map.get("x")+" Y:"+map.get("y"));
            current_node = nm.addGmlNode(map); //gmlに記載するnodeID番号が帰ってくる
            //System.out.println("newNodeID:"+current_node);
            tmp_Road_Node.add(current_node);

            //他のroadに繋がるedgeを構成するnodeIDを保存
            if(i % 2 == 1){
                makedNodeList.add(current_node);
            }

            if(first_node ==null){
                first_node = current_node;
                //System.out.println("firstNodeをセット");
            }
            tmp.setLocation(point);
        }

        //道の始点または終点の場合にnodeを一つ追加して三角形にする
        if (tmp_Road_Node.size()==2) {
            tmp_Road_Node.add(node_ID);
        }

        tmp_Road_Node.add(first_node);
        maked_new_node_map.put(node_ID,makedNodeList);
        //System.out.println("firstnode:"+first_node);

        //rm.setTmpRoadList(tmp_Road_Node);
        //System.out.println("tmp_road_node確認");
        for(String node:tmp_Road_Node){
            //System.out.println("nodeID:"+node);
            //System.out.println("X:"+nm.getX(node)+" Y:"+nm.getY(node));
        }

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
        Double du = (b.getX() - a.getX())*(d.getY() - c.getY()) - (b.getY() - a.getY())*(d.getX() - c.getX());
        Double u = ((c.getX() - a.getX()) * (d.getY() - c.getY()) - (c.getY() - a.getY())*(d.getX() - c.getX()))/du;
        Double tmp_X = a.getX() + u * (b.getX() - a.getX());
        Double tmp_Y = a.getY() + u * (b.getY() - a.getY());

        Point2D.Double correct_point = new Point2D.Double(tmp_X,tmp_Y);

        return correct_point;
    }

    private void makeNodeRoad(){
        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {
            ArrayList<String> check_list = (ArrayList)hm.getTmpHighwayList().get(i);
            for (int j=0; j<check_list.size()-1; j++) {
                System.out.println("j:"+j);
                System.out.println("check_listSize:"+check_list.size());
                String node1 = check_list.get(j);
                String node2 = check_list.get(j+1);
                int use_line_node1 = nearestLine(node1,node2);
                int use_line_node2 = nearestLine(node2,node1);
                ArrayList<String> node1_list = node_have_road_nodes.get(node1);
                ArrayList<String> node2_list = node_have_road_nodes.get(node2);

                if (checkTooClose(node1,node2)) {
                    System.out.println("二点が近い場合の処理");

                    Point2D.Double node1Point = new Point2D.Double(nm.getX(node1),nm.getY(node1));
                    Point2D.Double node2Point = new Point2D.Double(nm.getX(node2),nm.getY(node2));
                    double sita = Math.atan2(node2Point.getY()-node1Point.getY(),node2Point.getX()-node1Point.getX());

                    Point2D.Double middlePoint = new Point2D.Double((node2Point.getX()+node1Point.getX())/2,(node2Point.getY()+node1Point.getY())/2);

                    double degree = (sita*180)/Math.PI;

                    Double sitaMinus = ((degree-90)*Math.PI)/180.0;
                    Double sitaPlus = ((degree+90)*Math.PI)/180.0;

                    Point2D.Double minusPoint = new Point2D.Double(Math.cos(sitaMinus)*default_road_width_+middlePoint.getX(),Math.sin(sitaMinus)*default_road_width_+middlePoint.getY());
                    Point2D.Double plusPoint = new Point2D.Double(Math.cos(sitaPlus)*default_road_width_+middlePoint.getX(),Math.sin(sitaPlus)*default_road_width_+middlePoint.getY());

                    HashMap<String,Double> mapMinus = new HashMap<String,Double>();
                    mapMinus.put("y",minusPoint.getY());
                    mapMinus.put("x",minusPoint.getX());
                    String minusNodeID = nm.addGmlNode(mapMinus);

                    HashMap<String,Double> mapPlus = new HashMap<String,Double>();
                    mapPlus.put("y",plusPoint.getY());
                    mapPlus.put("x",plusPoint.getX());
                    String plusNodeID = nm.addGmlNode(mapPlus);


                    ((ArrayList)node_have_road_nodes.get(node1)).set(use_line_node1,minusNodeID);
                    ((ArrayList)node_have_road_nodes.get(node1)).set(use_line_node1+1,plusNodeID);
                    if (use_line_node1 == 0) {
                        System.out.println("使うnodeが0の場合");
                        ((ArrayList)node_have_road_nodes.get(node1)).set(node1_list.size()-1,minusNodeID);
                    }else if (use_line_node1 == node1_list.size()-1) {
                        System.out.println("使うnodeがlistの最大値の場合");
                        ((ArrayList)node_have_road_nodes.get(node1)).set(0,plusNodeID);
                    }else{
                        System.out.println("使うnodeがlistの最初または最後でない場合");
                    }


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
                }else{
                    //間の道を作成
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

        for(Map.Entry<String,ArrayList<String>> entry : node_have_road_nodes.entrySet()){
            System.out.println();
            System.out.println("NodeID:"+entry.getKey());
            System.out.println("haveRoadNodeNum:"+entry.getValue().size());

            rm.setTmpRoadList(entry.getValue());

        }

    }

    //ここまでに一つのhighwayのnode周辺に,他のhighwayのnode方向に対するroad用の頂点と辺（line）を作成した。
    //そこから、それらのline間を埋めるlineとhighwayのnode間の距離3のlineとの交差した数を返す関数。
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
                System.out.println("線が交差 i:"+i);
                startLineNum = i;
                break;
            }
        }
        return startLineNum;
    }

    //highwayの2点間が3*√5より近ければtrueを返す関数　3(道路幅)
    private Boolean checkTooClose(String node1,String node2){
        Point2D.Double point1 = new Point2D.Double(nm.getX(node1),nm.getY(node1));
        Point2D.Double point2 = new Point2D.Double(nm.getX(node2),nm.getY(node2));
        Double distance = Math.hypot(point1.getX()-point2.getX(),point1.getY()-point2.getY());

        if (distance <= default_road_distance_*2*Math.sqrt(3)) {
            return true;
        }else{
            return false;
        }
    }

}
