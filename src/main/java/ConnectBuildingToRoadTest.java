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
            ArrayList<String> connect_building = building_manager_.getBuildingNodeList(String.valueOf(building_id));
            //System.out.println();
            System.out.println("connectBuildingToRoad:"+building_id+"/"+building_list_size);
            //System.out.println("buildingID:"+building_id);

            Point buildingArea = area_manager_.buildingBelongArea(String.valueOf(building_id));
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



        for (int i=0; i<building_List.size()-1;i++ ) {
            //System.out.println();
            //System.out.println("edge:"+i+","+(i+1));

            start_point.setLocation(node_manager_.getX(building_List.get(i)),node_manager_.getY(building_List.get(i)));
            end_point.setLocation(node_manager_.getX(building_List.get(i+1)),node_manager_.getY(building_List.get(i+1)));

            if(Math.hypot(start_point.getX() - end_point.getX(),start_point.getY() - end_point.getY()) < 2.0){
                //System.out.println("建物がわのedgeが短い");
                continue;
            }


            middle_point.setLocation((start_point.getX()+end_point.getX())/2,(start_point.getY()+end_point.getY())/2);
            difference_point.setLocation(start_point.getX()-end_point.getX(),start_point.getY()-end_point.getY());

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            Double radian_90 = ((degree-90)*Math.PI)/180.0;

            //tmp_XY
            Point2D.Double check_point = new Point2D.Double();
            check_point.setLocation(Math.cos(radian_90)*this.search_object_distance+middle_point.getX(),Math.sin(radian_90)*this.search_object_distance+middle_point.getY());

            int road_list_size = Integer.parseInt(road_manager_.getRoadNodeID());

            ArrayList<String> check_array = building_manager_.getBuildingConnectedRoad(""+building_ID);

            Point buildingArea = area_manager_.buildingBelongArea(String.valueOf(building_ID));
            //System.out.println("Point:"+buildingArea.x+"."+buildingArea.y);
            ArrayList<String> roadList = area_manager_.getSearchRoadList(buildingArea);
            //System.out.println("roadListSize:"+roadList.size());

            //for (int k = 0; k<roadList.size();k++ ) {
            for (int k = 1; k<=road_list_size;k++ ) {
                //String checkRoadID = roadList.get(k);

                if (check_array.contains(k+"")) {
                    //System.out.println("自分に接続されているため除外");
                  continue;
                }
                if (road_manager_.containRemoveRoadList(k+"")) {
                    //System.out.println("除外させるリストに入っている");
                  continue;
                }


                check_arr = road_manager_.getRoadNodeList(String.valueOf(k));
                for (int m = 0; m<check_arr.size()-1;m++ ) {
                    tmp_start_point.setLocation(node_manager_.getX(check_arr.get(m)),node_manager_.getY(check_arr.get(m)));
                    tmp_end_point.setLocation(node_manager_.getX(check_arr.get(m+1)),node_manager_.getY(check_arr.get(m+1)));


                    if (checkCrossingLineSegment(middle_point,check_point,tmp_start_point,tmp_end_point)==false) continue;
                    //System.out.println("線分が交差している");
                    //直行している線分が交差している場合
                    //System.out.println("Road");
                    cross_point = CheckCrossingPoint(middle_point,check_point,tmp_start_point,tmp_end_point);
                    distance = Math.hypot(cross_point.getX() - middle_point.getX(),cross_point.getY() - middle_point.getY());
                    //System.out.println("CrossP X:"+cross_point.getX()+" Y:"+cross_point.getY());
                    //System.out.println("distance:"+distance);
                    //System.out.println("nearstDistance"+nearest_distance);

                    if(checkProperDegree(middle_point,check_point,tmp_start_point,tmp_end_point)) continue;
                    //System.out.println("角度が急でない");

                    if (checkContainNode(cross_point,buildingArea)==false) continue;
                    //System.out.println("node近くになし");

                    if (checkCrossingBuilding(middle_point,cross_point,""+building_ID,""+k,"road",m,buildingArea)==false)continue;

                    //System.out.println("重なっているEDGEなし");
                    //System.out.println("追加処理");

                    if(distance == 0)continue;
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
                if (k == building_ID)continue;
                if (building_manager_.containRemoveBuildingList(""+k))continue;

                check_arr = building_manager_.getBuildingNodeList(String.valueOf(k));

                for (int m = 0; m<check_arr.size()-1;m++ ) {

                    tmp_start_point.setLocation(node_manager_.getX(check_arr.get(m)),node_manager_.getY(check_arr.get(m)));
                    tmp_end_point.setLocation(node_manager_.getX(check_arr.get(m+1)),node_manager_.getY(check_arr.get(m+1)));


                    if (checkCrossingLineSegment(middle_point,check_point,tmp_start_point,tmp_end_point)==false) continue;
                    //直行している線分が交差している場合
                    //System.out.println("Building");
                    cross_point = CheckCrossingPoint(middle_point,check_point,tmp_start_point,tmp_end_point);
                    distance = Math.hypot(cross_point.getX() - middle_point.getX(),cross_point.getY() - middle_point.getY());
                    //System.out.println("CrossP X:"+cross_point.getX()+" Y:"+cross_point.getY());
                    //System.out.println("distance:"+distance);
                    //System.out.println("nearstDistance"+nearest_distance);

                    //角度計算
                    if(checkProperDegree(middle_point,check_point,tmp_start_point,tmp_end_point)) continue;
                    //System.out.println("角度が急ではない");

                    if (checkContainNode(cross_point,buildingArea) ==false) continue;
                    //System.out.println("node近くになし");

                    if (checkCrossingBuilding(middle_point,cross_point,""+building_ID,k+"","building",m,buildingArea) == false) continue;
                    //System.out.println("重なっているEDGEなし");

                    if (this.alreadyConnectBuilding.containsKey(""+building_ID)){
                        if (((ArrayList)this.alreadyConnectBuilding.get(""+building_ID)).contains(k+"")) {
                            //System.out.println("すでにこのBuildingに接続されているBuilding");
                            continue;
                        }
                    }

                    //System.out.println("追加処理");

                    if (distance == 0)continue;
                    if (nearest_Shape_Type == "none") {
                        nearest_Shape_Type = "building";
                        nearest_Shape_ID = k+"";
                        nearest_point.setLocation(cross_point);
                        connect_edge_point_A = m;
                        connect_edge_point_B = m+1;
                        nearest_distance = distance;
                        connect_building_edge_point_A = i;
                        connect_building_edge_point_B = i+1;
                    }else if (distance < nearest_distance) {
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
        //接続処理
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


        switch (nearest_Shape_Type) {
            case "none":
            //removeRelatedObjects(""+building_ID);
            //System.out.println("---------------接続道路なし---------------------");

            break;

            case "road":



            point_A.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_A)),node_manager_.getY(building_List.get(connect_building_edge_point_A)));

            point_B.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_B)),node_manager_.getY(building_List.get(connect_building_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            building_point_start.setLocation(Math.cos(radian)*default_road_width+middle_point.getX(),Math.sin(radian)*default_road_width+middle_point.getY());

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",building_point_start.getY());
            add_node_map_plus.put("x",building_point_start.getX());

            plus_node_ID = node_manager_.addGmlNode(add_node_map_plus);
            area_manager_.setNode(plus_node_ID);

            building_point_end.setLocation(Math.cos(radian_180)*default_road_width+middle_point.getX(),Math.sin(radian_180)*default_road_width+middle_point.getY());

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",building_point_end.getY());
            add_node_map_minus.put("x",building_point_end.getX());

            minus_node_ID = node_manager_.addGmlNode(add_node_map_minus);
            area_manager_.setNode(minus_node_ID);

            building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B,minus_node_ID);
            building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B+1,plus_node_ID);
            //System.out.println(connect_edge_point_B);

            //roadの方にnode追加


            tmp_shape = road_manager_.getRoadNodeList(nearest_Shape_ID);

            point_A.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_A)),node_manager_.getY(tmp_shape.get(connect_edge_point_A)));

            point_B.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_B)),node_manager_.getY(tmp_shape.get(connect_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;


            road_plus_X = Math.cos(radian)*default_road_width+nearest_point.getX();
            road_plus_Y = Math.sin(radian)*default_road_width+nearest_point.getY();

            road_point_start.setLocation(road_plus_X,road_plus_Y);

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",road_point_start.getY());
            add_node_map_plus.put("x",road_point_start.getX());

            plus_road_node_ID  = node_manager_.addGmlNode(add_node_map_plus);
            area_manager_.setNode(plus_road_node_ID);

            road_minus_X = Math.cos(radian_180)*default_road_width+nearest_point.getX();
            road_minus_Y = Math.sin(radian_180)*default_road_width+nearest_point.getY();

            road_point_end.setLocation(road_minus_X,road_minus_Y);

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",road_point_end.getY());
            add_node_map_minus.put("x",road_point_end.getX());

            minus_road_node_ID = node_manager_.addGmlNode(add_node_map_minus);
            area_manager_.setNode(minus_road_node_ID);

            road_manager_.insertRoadInNode(nearest_Shape_ID,connect_edge_point_B,minus_road_node_ID);
            road_manager_.insertRoadInNode(nearest_Shape_ID,connect_edge_point_B+1,plus_road_node_ID );

            ArrayList<String> addRoadArr2 = new ArrayList<String>();

            //System.out.println("addroadNode:");
            //System.out.println(plus_node_ID);
            //System.out.println(plus_road_node_ID);
            //System.out.println(minus_road_node_ID);
            //System.out.println(minus_node_ID);


            addRoadArr2.add(plus_node_ID);
            addRoadArr2.add(plus_road_node_ID );
            addRoadArr2.add(minus_road_node_ID);
            addRoadArr2.add(minus_node_ID);
            addRoadArr2.add(plus_node_ID);

            road_manager_.setTmpRoadList(addRoadArr2);
            road_manager_.setRoadConnectedObject(nearest_Shape_ID,road_manager_.getRoadNodeID(),""+building_ID);
            area_manager_.setRoad(road_manager_.getRoadNodeID());
            break;


            case "building":

            point_A.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_A)),node_manager_.getY(building_List.get(connect_building_edge_point_A)));

            point_B.setLocation(node_manager_.getX(building_List.get(connect_building_edge_point_B)),node_manager_.getY(building_List.get(connect_building_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            building_point_start.setLocation(Math.cos(radian)*default_road_width+middle_point.getX(),Math.sin(radian)*default_road_width+middle_point.getY());

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",building_point_start.getY());
            add_node_map_plus.put("x",building_point_start.getX());

            plus_node_ID = node_manager_.addGmlNode(add_node_map_plus);
            area_manager_.setNode(plus_node_ID);

            building_point_end.setLocation(Math.cos(radian_180)*default_road_width+middle_point.getX(),Math.sin(radian_180)*default_road_width+middle_point.getY());

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",building_point_end.getY());
            add_node_map_minus.put("x",building_point_end.getX());

            minus_node_ID = node_manager_.addGmlNode(add_node_map_minus);
            area_manager_.setNode(minus_node_ID);

            building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B,minus_node_ID);
            building_manager_.insertBuildingInNode(String.valueOf(building_ID),connect_building_edge_point_B+1,plus_node_ID);
            //System.out.println(connect_edge_point_B);

            //接続先のBuildingの方にnode追加

            tmp_shape = building_manager_.getBuildingNodeList(nearest_Shape_ID);

            point_A.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_A)),node_manager_.getY(tmp_shape.get(connect_edge_point_A)));

            point_B.setLocation(node_manager_.getX(tmp_shape.get(connect_edge_point_B)),node_manager_.getY(tmp_shape.get(connect_edge_point_B)));

            middle_point.setLocation((point_A.getX()+point_B.getX())/2,(point_A.getY()+point_B.getY())/2);

            difference_point.setLocation(point_B.getX() - point_A.getX(),point_B.getY() - point_A.getY());

            radian = Math.atan2(difference_point.getY(),difference_point.getX());
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;


            road_plus_X = Math.cos(radian)*default_road_width+nearest_point.getX();
            road_plus_Y = Math.sin(radian)*default_road_width+nearest_point.getY();

            road_point_start.setLocation(road_plus_X,road_plus_Y);

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",road_point_start.getY());
            add_node_map_plus.put("x",road_point_start.getX());

            plus_road_node_ID = node_manager_.addGmlNode(add_node_map_plus);
            area_manager_.setNode(plus_road_node_ID);

            road_minus_X = Math.cos(radian_180)*default_road_width+nearest_point.getX();
            road_minus_Y = Math.sin(radian_180)*default_road_width+nearest_point.getY();

            road_point_end.setLocation(road_minus_X,road_minus_Y);

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",road_point_end.getY());
            add_node_map_minus.put("x",road_point_end.getX());

            minus_road_node_ID = node_manager_.addGmlNode(add_node_map_minus);
            area_manager_.setNode(minus_road_node_ID);

            building_manager_.insertBuildingInNode(nearest_Shape_ID,connect_edge_point_B,minus_road_node_ID);
            building_manager_.insertBuildingInNode(nearest_Shape_ID,connect_edge_point_B+1,plus_road_node_ID);

            ArrayList<String> addRoadArr1 = new ArrayList<String>();

            //System.out.println("addroadNode:");
            //System.out.println(plus_node_ID);
            //System.out.println(plus_road_node_ID);
            //System.out.println(minus_road_node_ID);
            //System.out.println(minus_node_ID);


            addRoadArr1.add(plus_node_ID);
            addRoadArr1.add(minus_road_node_ID);
            addRoadArr1.add(plus_road_node_ID);
            addRoadArr1.add(minus_node_ID);
            addRoadArr1.add(plus_node_ID);

            road_manager_.setTmpRoadList(addRoadArr1);
            building_manager_.setBuildingConnectedObject(nearest_Shape_ID,road_manager_.getRoadNodeID(),""+building_ID);
            area_manager_.setRoad(road_manager_.getRoadNodeID());

            if(this.alreadyConnectBuilding.containsKey(nearest_Shape_ID)){
                ((ArrayList)this.alreadyConnectBuilding.get(nearest_Shape_ID)).add(""+building_ID);
            }else{
                ArrayList<String> list = new ArrayList<String>();
                list.add(""+building_ID);
                this.alreadyConnectBuilding.put(nearest_Shape_ID,list);
            }
            break;
        }
        return;
    }

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

        for (int i = 1; i<road_list_size;i++ ) {

            //String roadID = roadList.get(i);
            //他の建物から接続している道を判定に入れないように処理
            if(((ArrayList)building_manager_.getBuildingConnectedRoad(building_ID)).contains(String.valueOf(i))) continue;


            ArrayList<String> check_Road = road_manager_.getRoadNodeList(String.valueOf(i));

            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A.setLocation(node_manager_.getX(check_Road.get(k)),node_manager_.getY(check_Road.get(k)));
                point_B.setLocation(node_manager_.getX(check_Road.get(k+1)),node_manager_.getY(check_Road.get(k+1)));

                for (int m = 0; m<building_List.size()-1;m++ ) {
                    point_C.setLocation(node_manager_.getX(building_List.get(m)),node_manager_.getY(building_List.get(m)));
                    point_D.setLocation(node_manager_.getX(building_List.get(m+1)),node_manager_.getY(building_List.get(m+1)));

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

            ArrayList<String> check_Road = building_manager_.getBuildingNodeList(String.valueOf(i));

            if (i == Integer.parseInt(building_ID)){
                //System.out.println("自分自身のためcontinue");
                continue;
            }


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


    private Boolean checkCrossingLineSegment(Point2D.Double a,Point2D.Double b,Point2D.Double c,Point2D.Double d){
        //２線分を比べ交差していたらtrueを返す

        Line2D.Double aa = new Line2D.Double(a,b);
        Line2D.Double bb = new Line2D.Double(c,d);
        if (aa.intersectsLine(bb)) {
          return true;
        }
        return false;
    }

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

    private Boolean checkProperDegree(Point2D.Double a,Point2D.Double b,Point2D.Double c,Point2D.Double d){
      //角度が急担っていないか判定

      //ベクトルの長さを計算する

      double length_A = Math.hypot(b.getX()-a.getX(),b.getY()-a.getY());
      double length_B = Math.hypot(d.getX()-c.getX(),d.getY()-c.getY());
      Point2D.Double va = new Point2D.Double(b.getX()-a.getX(),b.getY()-a.getY());
      Point2D.Double vb = new Point2D.Double(d.getX()-c.getX(),d.getY()-c.getY());

      double dot_product = va.getX() * vb.getX() + va.getY() * vb.getY();
	     //内積とベクトル長さを使ってcosθを求める
	    double cos_sita = dot_product / ( length_A * length_B );

	    //cosθからθを求める
	    double sita = Math.acos( cos_sita );

	    //ラジアンでなく0～180の角度でほしい場合はコメント外す
	    sita = sita * 180.0 / Math.PI;

	    double degree = sita%180.0;
        //System.out.println("角度:"+degree);
      if(degree < 135 && degree > 45){
        return false;
      }
      //System.out.println("角度が急なためcontinue");
      return true;
    }
}
