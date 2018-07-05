import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;
import java.awt.geom.Point2D;

public class AreaManager {
    //building_node_id_,building_edge_id_はこのクラス内部ではint,外部ではStringで扱う

    //各areaが保持するnode
    private HashMap<Point,ArrayList<String>> area_node_map_ = new HashMap<Point,ArrayList<String>>();
    //各areaが保持するroad
    private HashMap<Point,ArrayList<String>> area_road_map_ = new HashMap<Point,ArrayList<String>>();
    //各areaが保持するbuilding
    private HashMap<Point,ArrayList<String>> area_building_map_ = new HashMap<Point,ArrayList<String>>();

    //各areaが保持するnode
    private HashMap<Point,ArrayList<String>> area_search_node_map_ = new HashMap<Point,ArrayList<String>>();
    //各areaが保持するroad
    private HashMap<Point,ArrayList<String>> area_search_road_map_ = new HashMap<Point,ArrayList<String>>();
    //各areaが保持するbuilding
    private HashMap<Point,ArrayList<String>> area_search_building_map_ = new HashMap<Point,ArrayList<String>>();


    private HashMap<String,Point> nodeBelongAreaMap = new HashMap<String,Point>();

    private HashMap<String,Point> buildingBelongAreaMap = new HashMap<String,Point>();

    private HashMap<String,Point> roadBelongAreaMap = new HashMap<String,Point>();



    //areaのXの最大値
    private int area_width_point_max;
    //areaのYの最大値
    private int area_height_point_max;

    private Double max_x;
    private Double max_y;
    private Double min_x;
    private Double min_y;

    //default value 25.0
    double areaBorderDistance = 100.0;

    NodeManager nodeManager;
    BuildingManager buildingManager;
    RoadManager roadManager;


    public AreaManager(NodeManager nodeManager,BuildingManager buildingManager,RoadManager roadManager){
        this.nodeManager = nodeManager;
        this.buildingManager = buildingManager;
        this.roadManager = roadManager;

        initCoordinateSetting();
        allNodePut();
        allBuildingPut();
        allRoadPut();
    }

    private void initCoordinateSetting(){
        System.out.println("AreaManagerInit");
        Double max_x=null;
        Double min_x=null;
        Double max_y=null;
        Double min_y=null;
        for(int i = 1;i<nodeManager.getNodeSize();i++){
            System.out.println("i:"+i+"size:"+nodeManager.getNodeSize());
            String id = String.valueOf(i);
            Double x = nodeManager.getX(id);
            Double y = nodeManager.getY(id);

            if(max_x==null) max_x = x;
            if(min_x==null) min_x = x;
            if(max_y==null) max_y = y;
            if(min_y==null) min_y = y;

            if (x > max_x) {
                max_x = x;
            }else if (x < min_x) {
                min_x = x;
            }
            if (y > max_y) {
                max_y = y;
            }else if (y < min_y) {
                min_y = y;
            }
        }

        double x_distance = max_x - min_x;
        double y_distance = max_y - min_y;
        int area_max_X = (int)(x_distance/areaBorderDistance);
        int area_max_Y = (int)(y_distance/areaBorderDistance);

        for(int i = 0;i<=area_max_Y;i++){
            for(int j = 0;j<=area_max_X;j++){
                Point point = new Point(j,i);
                ArrayList<String> nodeList = new ArrayList<String>();
                ArrayList<String> buildingList = new ArrayList<String>();
                ArrayList<String> roadList = new ArrayList<String>();
                this.area_node_map_.put(point,nodeList);
                this.area_building_map_.put(point,buildingList);
                this.area_road_map_.put(point,roadList);
                ArrayList<String> nodeSearchList = new ArrayList<String>();
                ArrayList<String> buildingSearchList = new ArrayList<String>();
                ArrayList<String> roadSearchList = new ArrayList<String>();
                this.area_search_node_map_.put(point,nodeSearchList);
                this.area_search_building_map_.put(point,buildingSearchList);
                this.area_search_road_map_.put(point,roadSearchList);
            }
        }
        this.area_width_point_max = area_max_X;
        this.area_height_point_max = area_max_Y;

        this.max_x = max_x;
        this.min_x = min_x;
        this.max_y = max_y;
        this.min_y = min_y;

        // System.out.println("area_max_X:"+area_max_X);
        // System.out.println("area_max_Y:"+area_max_Y);
        // System.out.println("max_x:"+max_x);
        // System.out.println("max_y:"+max_y);
        // System.out.println("min_x:"+min_x);
        // System.out.println("min_y:"+min_y);
    }

    private void allNodePut(){
        System.out.println("allnodePut");
        for(int i = 1;i<nodeManager.getNodeSize();i++){
            System.out.println("i:"+i+"size:"+nodeManager.getNodeSize());
            String id = String.valueOf(i);
            setNode(id);
        }
    }
    private void allBuildingPut(){
        System.out.println("allBuildingPut");
        int buildingSize = Integer.parseInt(buildingManager.getBuildingNodeID());
        for(int i = 1;i <= buildingSize;i++){
            String id = String.valueOf(i);
            setBuilding(id);
        }
    }
    private void allRoadPut(){
        System.out.println("allRoadPut");
        int roadSize = Integer.parseInt(roadManager.getRoadNodeID());
        for(int i = 1;i<=roadSize;i++){
            String id = String.valueOf(i);
            setRoad(id);
        }
    }


    public ArrayList<String> getNodeList(Point area){
        return area_node_map_ .get(area);
    }
    public ArrayList<String> getBuildingList(Point area){
        return area_building_map_.get(area);
    }
    public ArrayList<String> getRoadList(Point area){
        return area_road_map_.get(area);
    }

    public ArrayList<String> getSearchNodeList(Point area){
        return area_search_node_map_ .get(area);
    }
    public ArrayList<String> getSearchBuildingList(Point area){
        return area_search_building_map_.get(area);
    }
    public ArrayList<String> getSearchRoadList(Point area){
        return area_search_road_map_.get(area);
    }


    private Point checkNodeAreaPoint(String node_ID){
        Point2D.Double nodePoint = new Point2D.Double(nodeManager.getX(node_ID),nodeManager.getY(node_ID));
        //System.out.println("X:"+nodePoint.getX());
        //System.out.println("Y:"+nodePoint.getY());
        return calcAreaPoint(nodePoint);
    }

    private Point checkBuildingAreaPoint(String building_ID){
        ArrayList<String> buildingList = buildingManager.getBuildingNodeList(building_ID);
        double x_sum = 0.0;
        double y_sum = 0.0;
        for (int i= 0; i< buildingList.size()-1;i++ ) {
            String node_ID = buildingList.get(i);
            Point2D.Double buildingPoint = new Point2D.Double(nodeManager.getX(node_ID),nodeManager.getY(node_ID));
            x_sum += buildingPoint.getX();
            y_sum += buildingPoint.getY();
        }
        Point2D.Double centerPoint = new Point2D.Double(x_sum/((double)buildingList.size()-1),y_sum/((double)buildingList.size()-1));
        return calcAreaPoint(centerPoint);
    }

    private Point checkRoadArea(String road_ID){
        ArrayList<String> roadList = roadManager.getRoadNodeList(road_ID);
        double x_sum = 0.0;
        double y_sum = 0.0;
        for (int i= 0; i< roadList.size()-1;i++ ) {
            String node_ID = roadList.get(i);
            Point2D.Double roadPoint = new Point2D.Double(nodeManager.getX(node_ID),nodeManager.getY(node_ID));
            x_sum += roadPoint.getX();
            y_sum += roadPoint.getY();
        }
        Point2D.Double centerPoint = new Point2D.Double(x_sum/((double)roadList.size()-1),y_sum/((double)roadList.size()-1));
        return calcAreaPoint(centerPoint);
    }

    private Point calcAreaPoint(Point2D.Double calcPoint){
        // System.out.println("X:"+calcPoint.getX());
        // System.out.println("Y:"+calcPoint.getY());
        // System.out.println("min_x:"+min_x);
        // System.out.println("min_y:"+min_y);
        // System.out.println("areaBorderDistance:"+areaBorderDistance);
        int nodeX = (int)((calcPoint.getX()-min_x)/areaBorderDistance);
        int nodeY = (int)((calcPoint.getY()-min_y)/areaBorderDistance);

        Point point = new Point(nodeX,nodeY);
        return point;
    }

    public Point nodeBelongArea(String node_ID){
        return nodeBelongAreaMap.get(node_ID);
    }
    public Point buildingBelongArea(String building_ID){
        return buildingBelongAreaMap.get(building_ID);
    }
    public Point roadBelongArea(String road_ID){
        return roadBelongAreaMap.get(road_ID);
    }




    public void setNode(String node_ID){
        Point setPoint = checkNodeAreaPoint(node_ID);
        area_node_map_.get(setPoint).add(node_ID);
        nodeBelongAreaMap.put(node_ID,setPoint);

        System.out.println("node_ID:"+node_ID);
        System.out.println("setPoint:"+setPoint.getX()+"."+setPoint.getY());

        setSearchNode(node_ID,setPoint);


    }

    public void setBuilding(String building_ID){
        Point setPoint = checkBuildingAreaPoint(building_ID);
        area_building_map_.get(setPoint).add(building_ID);
        buildingBelongAreaMap.put(building_ID,setPoint);

        System.out.println("building_ID:"+building_ID);
        System.out.println("setPoint:"+setPoint.getX()+"."+setPoint.getY());

        setSearchBuilding(building_ID,setPoint);
    }

    public void setRoad(String road_ID){
        Point setPoint = checkRoadArea(road_ID);
        area_road_map_.get(setPoint).add(road_ID);
        roadBelongAreaMap.put(road_ID,setPoint);

        System.out.println("road_ID:"+road_ID);
        System.out.println("setPoint:"+setPoint.getX()+"."+setPoint.getY());

        setSearchRoad(road_ID,setPoint);
    }


    private void setSearchNode(String node_ID,Point setPoint){
        area_search_node_map_.get(setPoint).add(node_ID);


        if(setPoint.x != 0){
            Point point = new Point(setPoint.x-1,setPoint.y);
            area_search_node_map_.get(point).add(node_ID);
        }
        if(setPoint.y != 0){
            Point point = new Point(setPoint.x,setPoint.y-1);
            area_search_node_map_.get(point).add(node_ID);
        }
        if(setPoint.x != 0 && setPoint.y != 0){
            Point point = new Point(setPoint.x-1,setPoint.y-1);
            area_search_node_map_.get(point).add(node_ID);
        }


        if(setPoint.x != area_width_point_max && setPoint.y != 0){
            Point point = new Point(setPoint.x+1,setPoint.y-1);
            area_search_node_map_.get(point).add(node_ID);
        }

        if(setPoint.x != 0 && setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x-1,setPoint.y+1);
            area_search_node_map_.get(point).add(node_ID);
        }



        if(setPoint.x != area_width_point_max){
            Point point = new Point(setPoint.x+1,setPoint.y);
            area_search_node_map_.get(point).add(node_ID);
        }
        if(setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x,setPoint.y+1);
            area_search_node_map_.get(point).add(node_ID);
        }
        if(setPoint.x != area_width_point_max && setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x+1,setPoint.y+1);
            area_search_node_map_.get(point).add(node_ID);
        }
    }

    private void setSearchBuilding(String building_ID,Point setPoint){
        area_search_building_map_.get(setPoint).add(building_ID);

        if(setPoint.x != 0){
            Point point = new Point(setPoint.x-1,setPoint.y);
            area_search_building_map_.get(point).add(building_ID);
        }
        if(setPoint.y != 0){
            Point point = new Point(setPoint.x,setPoint.y-1);
            area_search_building_map_.get(point).add(building_ID);
        }
        if(setPoint.x != 0 && setPoint.y != 0){
            Point point = new Point(setPoint.x-1,setPoint.y-1);
            area_search_building_map_.get(point).add(building_ID);
        }


        if(setPoint.x != area_width_point_max && setPoint.y != 0){
            Point point = new Point(setPoint.x+1,setPoint.y-1);
            area_search_building_map_.get(point).add(building_ID);
        }

        if(setPoint.x != 0 && setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x-1,setPoint.y+1);
            area_search_building_map_.get(point).add(building_ID);
        }



        if(setPoint.x != area_width_point_max){
            Point point = new Point(setPoint.x+1,setPoint.y);
            area_search_building_map_.get(point).add(building_ID);
        }
        if(setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x,setPoint.y+1);
            area_search_building_map_.get(point).add(building_ID);
        }
        if(setPoint.x != area_width_point_max && setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x+1,setPoint.y+1);
            area_search_building_map_.get(point).add(building_ID);
        }
    }

    private void setSearchRoad(String road_ID,Point setPoint){
        area_search_road_map_.get(setPoint).add(road_ID);

        if(setPoint.x != 0){
            Point point = new Point(setPoint.x-1,setPoint.y);
            area_search_road_map_.get(point).add(road_ID);
        }
        if(setPoint.y != 0){
            Point point = new Point(setPoint.x,setPoint.y-1);
            area_search_road_map_.get(point).add(road_ID);
        }
        if(setPoint.x != 0 && setPoint.y != 0){
            Point point = new Point(setPoint.x-1,setPoint.y-1);
            area_search_road_map_.get(point).add(road_ID);
        }


        if(setPoint.x != area_width_point_max && setPoint.y != 0){
            Point point = new Point(setPoint.x+1,setPoint.y-1);
            area_search_road_map_.get(point).add(road_ID);
        }

        if(setPoint.x != 0 && setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x-1,setPoint.y+1);
            area_search_road_map_.get(point).add(road_ID);
        }



        if(setPoint.x != area_width_point_max){
            Point point = new Point(setPoint.x+1,setPoint.y);
            area_search_road_map_.get(point).add(road_ID);
        }
        if(setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x,setPoint.y+1);
            area_search_road_map_.get(point).add(road_ID);
        }
        if(setPoint.x != area_width_point_max && setPoint.y != area_height_point_max){
            Point point = new Point(setPoint.x+1,setPoint.y+1);
            area_search_road_map_.get(point).add(road_ID);
        }
    }




}
