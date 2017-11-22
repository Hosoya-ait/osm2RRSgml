class ConnectBuildingToRoad {

import java.awt.geom.Point2D;
import java.awt.geon.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

    private HashMap<String,String> alreadyConnectBuilding = new HashMap<String,String>();
    private NodeManager     node_manager_;
    private EdgeManager     edge_manager_;
    private BuildingManager building_manager_;
    private RoadManager     road_manager_;

    private HashMap<String,ConnectBuildingInfo> Connect_Building_Info_Map = new HashMap<String,ConnectBuildingInfo>();

    public ConnectBuildingToRoad(NodeManager nm,EdgeManager em,BuildingManager bm,RoadManager rm){
        this.node_manager_ = nm;
        this.edge_manager_ = em;
        this.building_manager_ = bm;
        this.road_manager_ = rm;
    }

    public void connect(){
        int building_list_size = building_manager_.getBuildingNodeID();
        for (int building_id = 1; i<=building_list_size ; building_id++ ) {
            ArrayList<String> connect_building = building_manager_.getBuildingNodeList(String.valueOf(building_id));
            if (checkCrossingRoad(connect_building)) {
                connectObject(connect_building,building_id);
            }
        }
    }

    private void connectObject(ArrayList<String> building_List ,int building_ID){
        //保存用
        String nearest_Shape_Type = "none";
        String nearest_Shape_ID = "none";
        //buildingのnodeIDの保存　roadのnodeIDの保存
        Point2D cross_point = new Point2D();

        Point2D nearest_point = new Point2D();
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

        Point2D start_point = new Point2D();
        Point2D end_point = new Point2D();
        Point2D middle_point = new Point2D();
        Point2D difference_point = new Point2D();
        Double radian;
        Double degree;

        String tmp_start_point_ID = null;
        String tmp_end_point_ID = null;

        Point2D tmp_start_point = new Point2D();
        Point2D tmp_end_point = new Point2D();

        Double distance;

        System.out.println();
        System.out.println("buildingID:"+building_ID);

        for (int i=0; i<building_List.size()-1;i++ ) {
            System.out.println();
            System.out.println("edge:"+i+","+(i+1));

            start_point.setLocation(node_manager_.getX(building_List.get(i)),node_manager_.getY(building_List.get(i)));
            end_point.setLocation(node_manager_.getX(building_List.get(i+1)),node_manager_.getY(building_List.get(i+1)));

            middle_point.setLocation((start_point.getX()+end_point.getX())/2,(start_point.getY()+end_point.getY())/2);
            difference_point.setLocation(start_point.getX()-end_point.getX(),start_point.getY()-end_point.getY())

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            Double radian_90 = ((degree+90)*Math.PI)/180.0;

            //tmp_XY
            Point2D check_point = new Point2D();
            check_point.setLocation(Math.cos(radian_90)*30.0+middle_point.getX(),Math.sin(radian_90)*30.0+middle_point.getY());

            int road_list_size = road_manager_.getRoadNodeID();
            for (int k = 1; k<=road_list_size;k++ ) {

                check_arr = road_manager_.getRoadNodeList(String.valueOf(k);

                for (int m = 0; m<check_arr.size()-1;m++ ) {
                    tmp_start_point.setLocation(node_manager_.getX(building_List.get(m)),node_manager_.getY(building_List.get(m)));
                    tmp_end_point.setLocation(node_manager_.getX(building_List.get(m+1)),node_manager_.getY(building_List.get(m+1)));

                    if (checkCrossingLineSegment(middle_point,check_point,tmp_start_point,tmp_end_point)) {
                        //直行している線分が交差している場合
                        System.out.println("Road");
                        cross_point = CheckCrossingPoint(middle_point,check_point,tmp_start_point,tmp_end_point);
                        distance = Math.hypot(cross_point.getX() - middle_point.getX(),cross_point.getY() - middle_point.getY());
                        System.out.println("distance:"+distance);
                        System.out.println("nearstDistance"+nearest_distance);

                        if (checkContainNode(cross_point)) {

                            System.out.println("node近くになし");

                            if (checkCrossingBuilding(middle_point,cross_point,""+building_ID,""+k)) {
                                System.out.println("重なっているEDGEなし");
                                System.out.println("追加処理");
                                if(distance != 0){
                                    if (nearest_Shape_Type == "none") {
                                        nearest_Shape_Type = "road";
                                        nearest_Shape_ID = ""+k;
                                        nearest_point.setLocation(cross_point);
                                        connect_edge_point_A = m;
                                        connect_edge_point_B = m+1;
                                        nearest_distance = distance;
                                        connect_building_edge_point_A = i;
                                        connect_building_edge_point_B = i+1;
                                    }else if(distance < nearest_distance || nearest_Shape_Type == "building") {
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
                        }
                    }
                }
            }

            if (nearest_Shape_Type == "none" || nearest_Shape_Type == "building") {
                int building_list_size = building_manager_.getBuildingNodeID();
                for (int k = 1; k<building_list_size;k++ ) {
                    if (k != building_ID) {
                        tmp_arr = building_manager_.getBuildingNodeList(k);

                        for (int m = 0; m<tmp_arr.size()-1;m++ ) {
                            tmp_start_point.setLocation(node_manager_.getX(building_List.get(m)),node_manager_.getY(building_List.get(m)));
                            tmp_end_point.setLocation(node_manager_.getX(building_List.get(m+1)),node_manager_.getY(building_List.get(m+1)));

                            if (checkCrossingLineSegment(middle_point,check_point,tmp_start_point,tmp_end_point)) {
                                //直行している線分が交差している場合
                                System.out.println("Building");
                                cross_point = CheckCrossingPoint(middle_point,check_point,tmp_start_point,tmp_end_point);
                                distance = Math.hypot(cross_point.getX() - middle_point.getX(),cross_point.getY() - middle_point.getY());
                                System.out.println("distance:"+distance);
                                System.out.println("nearstDistance"+nearest_distance);
                                if (checkContainNode(cross_point)) {

                                    System.out.println("node近くになし");

                                    if (checkCrossingBuilding(middle_point,cross_point,""+building_ID,""+k)) {
                                        System.out.println("重なっているEDGEなし");
                                        if (this.alreadyConnectBuilding.containsKey(""+building_ID) == false) {

                                            System.out.println("追加処理");
                                            if (distance != 0) {
                                                if (nearest_Shape_Type == "none") {
                                                    nearest_Shape_Type = "building";
                                                    nearest_Shape_ID = ""+k;
                                                    nearest_point.setLocation(cross_point);
                                                    connect_edge_point_A = m;
                                                    connect_edge_point_B = m+1;
                                                    nearest_distance = distance;
                                                    connect_building_edge_point_A = i;
                                                    connect_building_edge_point_B = i+1;
                                                }else if (distance < nearest_distance) {
                                                    nearest_Shape_Type = "building";
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //接続処理
        Double radian_180;

        Point2D building_point_start = new Point2D();

        Double building_plus_X;
        Double building_plus_Y;
        HashMap<String,Double> add_node_map_plus = new HashMap<String,Double>();
        int tmpNodeId = 0;
        String add_plus_node = new String();

        Point2D building_point_end = new Point2D();

        Double building_minus_X;
        Double building_minus_Y;
        HashMap<String,Double> add_node_map_minus = new HashMap<String,Double>();
        String add_minus_node = new String();

        Point2D road_point_start = new Point2D();

        Double road_plus_X;
        Double road_plus_Y;

        String add_plus_road_node = new String();

        Point2D road_point_end = new Point2D();

        Double road_minus_X;
        Double road_minus_Y;
        String add_minus_road_node = new String();
        ArrayList<String> addRoadArr = new ArrayList<String>();
        ArrayList<String> tmp_shape = new ArrayList<String>();

        switch (nearest_Shape_Type) {
            case "none":

            break;

            case "road":

            point_A.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_A)),node_manager_.getY(building_List.get(connect_building_edge_point_A)));

            point_B.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_B)),node_manager_.getY(building_List.get(connect_building_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            building_point_start.setLocation(Math.cos(radian)*1.0+middle_point.getX(),Math.sin(radian)*1.0+middle_point.getY());

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",building_point_start.getY());
            add_node_map_plus.put("x",building_point_start.getX());

            String plus_node_ID = node_manager_.addGmlNode(add_node_map_plus);

            building_point_end.setLocation(Math.cos(radian_180)*1.0+middle_point.getX(),Math.sin(radian_180)*1.0+middle_point.getY());

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",building_point_end.getY());
            add_node_map_minus.put("x",building_point_end.getX());

            String minus_node_ID = node_manager_.addGmlNode(add_node_map_minus);

            building_manager_.insertBuildingInNode(building_ID,connect_building_edge_point_B,minus_node_ID);
            building_manager_.insertBuildingInNode(building_ID,connect_building_edge_point_B+1,plus_node_ID);
            System.out.println(connect_edge_point_B);

            //roadの方にnode追加


            tmp_shape = road_manager_.getRoadNodeList(Integer.parseInt(nearest_Shape_ID));

            point_A.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_A)),node_manager_.getY(tmp_shape.get(connect_edge_point_A)));

            point_B.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_B)),node_manager_.getY(tmp_shape.get(connect_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(dif_Point_Y,dif_Point_X);
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;


            road_plus_X = Math.cos(radian)*1.0+nearest_point.getX();
            road_plus_Y = Math.sin(radian)*1.0+nearest_point.getY();

            road_point_start.setLocation(road_plus_X,road_plus_Y);

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",road_point_start.getY());
            add_node_map_plus.put("x",road_point_start.getX());

            String plus_road_node_ID = node_manager_.addGmlNode(add_node_map_plus);


            road_minus_X = Math.cos(radian_180)*1.0+nearest_point.getX();
            road_minus_Y = Math.sin(radian_180)*1.0+nearest_point.getY();

            road_point_end.setLocation(road_minus_X,road_minus_Y);

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",road_point_end.getY());
            add_node_map_minus.put("x",road_point_end.getX());

            String minus_road_node_ID = node_manager_.addGmlNode(add_node_map_minus);

            road_manager_.insertRoadInNode(nearest_Shape_ID,connect_edge_point_B,minus_road_node_ID);
            road_manager_.insertRoadInNode(nearest_Shape_ID,connect_edge_point_B+1,plus_road_node_ID);

            ArrayList<String> addRoadArr2 = new ArrayList<String>();

            addRoadArr2.add(plus_node_ID);
            addRoadArr2.add(plus_road_node_ID);
            addRoadArr2.add(minus_road_node_ID);
            addRoadArr2.add(minus_node_ID);
            addRoadArr2.add(plus_node_ID);

            road_manager_.setTmpRoadList(addRoadArr2);

            break;

            case "building":

            point_A.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_A)),node_manager_.getY(building_List.get(connect_building_edge_point_A)));

            point_B.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_B)),node_manager_.getY(building_List.get(connect_building_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            building_point_start.setLocation(Math.cos(radian)*1.0+middle_point.getX(),Math.sin(radian)*1.0+middle_point.getY());

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",building_point_start.getY());
            add_node_map_plus.put("x",building_point_start.getX());

            String plus_node_ID = node_manager_.addGmlNode(add_node_map_plus);

            building_point_end.setLocation(Math.cos(radian_180)*1.0+middle_point.getX(),Math.sin(radian_180)*1.0+middle_point.getY());

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",building_point_end.getY());
            add_node_map_minus.put("x",building_point_end.getX());

            String minus_node_ID = node_manager_.addGmlNode(add_node_map_minus);

            building_manager_.insertBuildingInNode(building_ID,connect_building_edge_point_B,minus_node_ID);
            building_manager_.insertBuildingInNode(building_ID,connect_building_edge_point_B+1,plus_node_ID);
            System.out.println(connect_edge_point_B);

            //接続先のBuildingの方にnode追加

            tmp_shape = building_manager_.getBuildingNodeList(Integer.parseInt(nearest_Shape_ID));

            point_A.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_A)),node_manager_.getY(tmp_shape.get(connect_edge_point_A)));

            point_B.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_B)),node_manager_.getY(tmp_shape.get(connect_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(dif_Point_Y,dif_Point_X);
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;


            road_plus_X = Math.cos(radian)*1.0+nearest_point.getX();
            road_plus_Y = Math.sin(radian)*1.0+nearest_point.getY();

            road_point_start.setLocation(road_plus_X,road_plus_Y);

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",road_point_start.getY());
            add_node_map_plus.put("x",road_point_start.getX());

            String plus_road_node_ID = node_manager_.addGmlNode(add_node_map_plus);


            road_minus_X = Math.cos(radian_180)*1.0+nearest_point.getX();
            road_minus_Y = Math.sin(radian_180)*1.0+nearest_point.getY();

            road_point_end.setLocation(road_minus_X,road_minus_Y);

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",road_point_end.getY());
            add_node_map_minus.put("x",road_point_end.getX());

            String minus_road_node_ID = node_manager_.addGmlNode(add_node_map_minus);

            road_manager_.insertBuildingInNode(nearest_Shape_ID,connect_edge_point_B,minus_road_node_ID);
            road_manager_.insertBuildingInNode(nearest_Shape_ID,connect_edge_point_B+1,plus_road_node_ID);

            ArrayList<String> addRoadArr1 = new ArrayList<String>();

            addRoadArr1.add(plus_node_ID);
            addRoadArr1.add(plus_road_node_ID);
            addRoadArr1.add(minus_road_node_ID);
            addRoadArr1.add(minus_node_ID);
            addRoadArr1.add(plus_node_ID);

            road_manager_.setTmpRoadList(addRoadArr1);

            this.alreadyConnectBuilding.put(nearest_Shape_ID,""+building_ID);
            break;
        }
        return;
    }

    private Point2D CheckCrossingPoint(Point2D a,Point2D b,Point2D c,Point2D d){
        Point2D xy = new Point2D();
        //参照url https://gist.github.com/yoshiki/7702066
        Double d = (b.getX() - a.getX())*(d.getY() - c.getY()) - (b.getY() - a.getY())*(d.getX() - c.getX());
        Double u = ((c.getX() - a.getX())*(d.getY() - c.getY()) - (c.getY() - a.getY())*(d.getX() - c.getX()))/d;
        Double tmp_X = a.getX() + u * (b.getX() - a.getX());
        Double tmp_Y = a.getY() + u * (b.getY() - a.getY());
        xy.setLocation(tmp_X,tmp_Y);
        return xy;
    }

    private Boolean checkCrossingRoad(ArrayList<String> building_List){
        ArrayList<String> check_Road = new ArrayList<String>();
        //すべてのRoadの線分に対して交差しているか判定交差していなければtrueが帰る
        Point2D point_A = new Point2D();
        Point2D point_B = new Point2D();
        Point2D point_C = new Point2D();
        Point2D point_D = new Point2D();

        int road_list_size = road_manager_.getRoadNodeID();
        for (int i = 1; i<road_list_size;i++ ) {
            check_Road = road_manager_.getRoadNodeList(i);

            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
                point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));

                for (int m = 0; m<building_List.size()-1;m++ ) {
                    point_C.setLocation(node_manager_.getX(building_List.get(m)),node_manager_.getY(building_List.get(m)));
                    point_D.setLocation(node_manager_.getX(building_List.get(m+1)),node_manager_.getY(building_List.get(m+1)));

                    if (checkCrossingLineSegment(point_A,point_B,point_C,point_D)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Boolean checkCrossingBuilding(Point2D origin_point,Point2D connect_point,String building_ID,String object_ID){
        ArrayList<String> check_Road = new ArrayList<String>();
        //すべてのRoadの線分に対して交差しているか判定交差していなければtrueが帰る

        System.out.println("building_ID:"+building_ID);
        //仮処理
        Point2D point_A = new Point2D();
        Point2D point_B = new Point2D();

        int building_list_size = building_manager_.getBuildingNodeID();

        for (int i = 1; i<building_list_size;i++ ) {
            check_Road = building_manager_.getBuildingNodeList(i);

            if (i == Integer.parseInt(building_ID)) continue;

            for (int k = 0; k<check_Road.size()-1;k++ ) {

                point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(k));
                point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(k+1));

                if (checkCrossingLineSegment(origin_point,connect_point,point_A,point_B)) {
                    System.out.println("building:"+i);
                    System.out.println("edgeA:"+k);
                    System.out.println("edgeB:"+(k+1));
                    return false;
                }
            }
        }
        int road_list_size = road_manager_.getRoadNodeID();

        for (int i = 1; i<road_list_size;i++ ) {
            check_Road = road_manager_.getRoadNodeList(i);

            if (i == Integer.parseInt(object_ID)) continue;

            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(k));
                point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(k+1));

                if (checkCrossingLineSegment(origin_point,connect_point,point_A,point_B)) {
                    System.out.println("road:"+i);
                    System.out.println("edgeA:"+k);
                    System.out.println("edgeB:"+(k+1));
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean checkCrossingLineSegment(Point2D a,Point2D b,Point2D c,Point2D d){
        //２線分を比べ交差していたらtrueを返す
        Double ta = (c.getX() - d.getX()) * (a.getY() - c.getY()) + (c.getY() - d.getY()) * (c.getX() - a.getX());
        Double tb = (c.getX() - d.getX()) * (b.getY() - c.getY()) + (c.getY() - d.getY()) * (c.getX() - a.getX());
        Double tc = (a.getX() - b.getX()) * (c.getY() - a.getY()) + (a.getY() - b.getY()) * (a.getX() - c.getX());
        Double td = (a.getX() - b.getX()) * (d.getY() - a.getY()) + (a.getY() - b.getY()) * (a.getX() - d.getX());
        if ((tc * td) < 0 && (ta * tb) < 0) {
            return true;
        }
        return false;
    }

    private Boolean checkContainNode(Point2D point){
        //x,yから指定した円の範囲にnodeがあった場合falseを返す なかった場合はtrue
        Double distance;
        int node_size = node_manager_.getNodeSize();
        for (int i=1; i<=node_size;i++ ) {
            Point2D comp_point = new Point2D();
            comp_point.setLocation(node_manager_.getX(String.valueOf(i)),node_manager_.getY(String.valueOf(i)));
            distance = Math.hypot(comp_point.getX()-point.getX(),comp_point.getY()-point.getY());
            if (distance < 1.1) {
                return false;
            }
        }
        return true;
    }
}
