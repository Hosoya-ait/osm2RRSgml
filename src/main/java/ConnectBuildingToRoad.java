import java.util.ArrayList;
import java.util.HashMap;
class ConnectBuildingToRoad {
    private HashMap<String,String> alreadyConnectBuilding = new HashMap<String,String>();

    public void connect(){

        for (int i=0; i<OsmToGmlConverter.tmpBuildingList.size();i++ ) {
            if (checkCrossingRoad(OsmToGmlConverter.tmpBuildingList.get(i))) {
                connectObject(OsmToGmlConverter.tmpBuildingList.get(i),i);
            }
        }

    }
    private void connectObject(ArrayList building_List ,int building_ID){
        //保存用
        String nearest_Shape_Type = "none";
        String nearest_Shape_ID = "none";
        //buildingのnodeIDの保存　roadのnodeIDの保存
        ArrayList<Double> cross_point = new ArrayList<Double>();
        Double nearest_X = 0.0;
        Double nearest_Y = 0.0;
        int connect_edge_point_A = 0;
        int connect_edge_point_B = 0;
        Double nearest_distance = 1000.;;
        int connect_building_edge_point_A = 0;
        int connect_building_edge_point_B = 0;


        //計算用
        HashMap<String,Double> tmp_Map = new HashMap<String,Double>();
        ArrayList<String> tmp_arr = new ArrayList<String>();
        String point_A = new String();
        String point_B = new String();
        Double point_A_X;
        Double point_A_Y;
        Double point_B_X;
        Double point_B_Y;
        Double mid_point_X;
        Double mid_point_Y;
        Double dif_Point_X;
        Double dif_Point_Y;
        Double radian;
        Double degree;

        String tmp_point_A = new String();
        String tmp_point_B = new String();

        Double tmp_point_A_X;
        Double tmp_point_A_Y;
        Double tmp_point_B_X;
        Double tmp_point_B_Y;

        Double distance;


        for (int i=0; i<building_List.size()-1;i++ ) {
            point_A = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(i));
            point_B = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(i+1));

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_A);
            point_A_X = tmp_Map.get("x");
            point_A_Y = tmp_Map.get("y");

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_B);
            point_B_X = tmp_Map.get("x");
            point_B_Y = tmp_Map.get("y");

            mid_point_X = (point_A_X+point_B_X)/2;
            mid_point_Y = (point_A_Y+point_B_Y)/2;

            dif_Point_X = point_B_X - point_A_X;
            dif_Point_Y = point_B_Y - point_A_Y;

            radian = Math.atan2(dif_Point_Y,dif_Point_X);
            degree = (radian*180)/Math.PI;

            Double radian_90 = ((degree+90)*Math.PI)/180.0;

            Double tmp_X = Math.cos(radian_90)*50.0+mid_point_X;
            Double tmp_Y = Math.sin(radian_90)*50.0+mid_point_Y;


            for (int k = 0; k<OsmToGmlConverter.tmpRoadList.size();k++ ) {
                tmp_arr = OsmToGmlConverter.tmpRoadList.get(k);
                for (int m = 0; m<tmp_arr.size()-1;m++ ) {
                    tmp_point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_arr.get(m));
                    tmp_point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_arr.get(m+1));

                    tmp_Map = OsmToGmlConverter.nodeMap.get(tmp_point_A);
                    tmp_point_A_X = tmp_Map.get("x");
                    tmp_point_A_Y = tmp_Map.get("y");

                    tmp_Map = OsmToGmlConverter.nodeMap.get(tmp_point_B);
                    tmp_point_B_X = tmp_Map.get("x");
                    tmp_point_B_Y = tmp_Map.get("y");

                    if (checkCrossingLineSegment(mid_point_X,mid_point_Y,tmp_X,tmp_Y,tmp_point_A_X,tmp_point_A_Y,tmp_point_B_X,tmp_point_B_Y)) {
                        //直行している線分が交差している場合
                        cross_point = CheckCrossingPoint(mid_point_X,mid_point_Y,tmp_X,tmp_Y,tmp_point_A_X,tmp_point_A_Y,tmp_point_B_X,tmp_point_B_Y);
                        distance = Math.sqrt((cross_point.get(0) - mid_point_X)*(cross_point.get(0) - mid_point_X) +(cross_point.get(1) - mid_point_Y)*(cross_point.get(1) - mid_point_Y));
                        if (checkContainNode(cross_point.get(0),cross_point.get(1))) {
                            if (checkCrossingBuilding(mid_point_X,mid_point_Y,cross_point.get(0),cross_point.get(1))) {
                                if(distance != 0){
                                    if (nearest_Shape_Type == "none") {
                                        nearest_Shape_Type = "road";
                                        nearest_Shape_ID = ""+k;
                                        nearest_X = cross_point.get(0);
                                        nearest_Y = cross_point.get(1);
                                        connect_edge_point_A = m;
                                        connect_edge_point_B = m+1;
                                        nearest_distance = distance;
                                        connect_building_edge_point_A = i;
                                        connect_building_edge_point_B = i+1;
                                    }
                                    if (distance < nearest_distance) {
                                        nearest_Shape_Type = "road";
                                        nearest_Shape_ID = ""+k;
                                        nearest_X = cross_point.get(0);
                                        nearest_Y = cross_point.get(1);
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
            for (int k = 0; k<OsmToGmlConverter.addedConnectRoadList.size();k++ ) {
                tmp_arr = OsmToGmlConverter.addedConnectRoadList.get(k);
                for (int m = 0; m<tmp_arr.size()-1;m++ ) {
                    tmp_point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_arr.get(m));
                    tmp_point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_arr.get(m+1));

                    tmp_Map = OsmToGmlConverter.nodeMap.get(tmp_point_A);
                    tmp_point_A_X = tmp_Map.get("x");
                    tmp_point_A_Y = tmp_Map.get("y");

                    tmp_Map = OsmToGmlConverter.nodeMap.get(tmp_point_B);
                    tmp_point_B_X = tmp_Map.get("x");
                    tmp_point_B_Y = tmp_Map.get("y");

                    if (checkCrossingLineSegment(mid_point_X,mid_point_Y,tmp_X,tmp_Y,tmp_point_A_X,tmp_point_A_Y,tmp_point_B_X,tmp_point_B_Y)) {
                        //直行している線分が交差している場合
                        cross_point = CheckCrossingPoint(mid_point_X,mid_point_Y,tmp_X,tmp_Y,tmp_point_A_X,tmp_point_A_Y,tmp_point_B_X,tmp_point_B_Y);
                        distance = Math.sqrt((cross_point.get(0) - mid_point_X)*(cross_point.get(0) - mid_point_X) +(cross_point.get(1) - mid_point_Y)*(cross_point.get(1) - mid_point_Y));
                        if (checkContainNode(cross_point.get(0),cross_point.get(1))) {
                            if (checkCrossingBuilding(mid_point_X,mid_point_Y,cross_point.get(0),cross_point.get(1))) {
                                if(distance != 0){
                                    if (nearest_Shape_Type == "none") {
                                        nearest_Shape_Type = "addroad";
                                        nearest_Shape_ID = ""+k;
                                        nearest_X = cross_point.get(0);
                                        nearest_Y = cross_point.get(1);
                                        connect_edge_point_A = m;
                                        connect_edge_point_B = m+1;
                                        nearest_distance = distance;
                                        connect_building_edge_point_A = i;
                                        connect_building_edge_point_B = i+1;
                                    }
                                    if (distance < nearest_distance) {
                                        nearest_Shape_Type = "addroad";
                                        nearest_Shape_ID = ""+k;
                                        nearest_X = cross_point.get(0);
                                        nearest_Y = cross_point.get(1);
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
            if (nearest_Shape_Type == "none") {
                for (int k = 0; k<OsmToGmlConverter.tmpBuildingList.size();k++ ) {
                    if (k != building_ID) {
                        tmp_arr = OsmToGmlConverter.tmpBuildingList.get(k);

                        for (int m = 0; m<tmp_arr.size()-1;m++ ) {
                            tmp_point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_arr.get(m));
                            tmp_point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_arr.get(m+1));



                            tmp_Map = OsmToGmlConverter.nodeMap.get(tmp_point_A);
                            tmp_point_A_X = tmp_Map.get("x");
                            tmp_point_A_Y = tmp_Map.get("y");

                            tmp_Map = OsmToGmlConverter.nodeMap.get(tmp_point_B);
                            tmp_point_B_X = tmp_Map.get("x");
                            tmp_point_B_Y = tmp_Map.get("y");

                            if (checkCrossingLineSegment(mid_point_X,mid_point_Y,tmp_X,tmp_Y,tmp_point_A_X,tmp_point_A_Y,tmp_point_B_X,tmp_point_B_Y)) {
                                //直行している線分が交差している場合
                                cross_point = CheckCrossingPoint(mid_point_X,mid_point_Y,tmp_X,tmp_Y,tmp_point_A_X,tmp_point_A_Y,tmp_point_B_X,tmp_point_B_Y);
                                distance = Math.sqrt((cross_point.get(0) - mid_point_X)*(cross_point.get(0) - mid_point_X) +(cross_point.get(1) - mid_point_Y)*(cross_point.get(1) - mid_point_Y));
                                if (checkContainNode(cross_point.get(0),cross_point.get(1))) {
                                    if (checkCrossingBuilding(mid_point_X,mid_point_Y,cross_point.get(0),cross_point.get(1))) {
                                        if (this.alreadyConnectBuilding.containsKey(""+building_ID) == false) {

                                            if (distance != 0) {
                                                if (nearest_Shape_Type == "none") {
                                                    nearest_Shape_Type = "building";
                                                    nearest_Shape_ID = ""+k;
                                                    nearest_X = cross_point.get(0);
                                                    nearest_Y = cross_point.get(1);
                                                    connect_edge_point_A = m;
                                                    connect_edge_point_B = m+1;
                                                    nearest_distance = distance;
                                                    connect_building_edge_point_A = i;
                                                    connect_building_edge_point_B = i+1;
                                                }
                                                if (distance < nearest_distance) {
                                                    nearest_Shape_Type = "building";
                                                    nearest_Shape_ID = ""+k;
                                                    nearest_X = cross_point.get(0);
                                                    nearest_Y = cross_point.get(1);
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
        Double building_plus_X;
        Double building_plus_Y;
        HashMap<String,Double> add_node_map_plus = new HashMap<String,Double>();
        int tmpNodeId = 0;
        String add_plus_node = new String();
        Double building_minus_X;
        Double building_minus_Y;
        HashMap<String,Double> add_node_map_minus = new HashMap<String,Double>();
        String add_minus_node = new String();
        Double road_plus_X;
        Double road_plus_Y;
        String add_plus_road_node = new String();
        Double road_minus_X;
        Double road_minus_Y;
        String add_minus_road_node = new String();
        ArrayList<String> addRoadArr = new ArrayList<String>();
        ArrayList<String> tmp_shape = new ArrayList<String>();

        switch (nearest_Shape_Type) {
            case "none":

            break;

            case "addroad":

            point_A = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(connect_building_edge_point_A));
            point_B = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(connect_building_edge_point_B));

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_A);
            point_A_X = tmp_Map.get("x");
            point_A_Y = tmp_Map.get("y");

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_B);
            point_B_X = tmp_Map.get("x");
            point_B_Y = tmp_Map.get("y");

            mid_point_X = (point_A_X+point_B_X)/2;
            mid_point_Y = (point_A_Y+point_B_Y)/2;

            dif_Point_X = point_B_X - point_A_X;
            dif_Point_Y = point_B_Y - point_A_Y;

            radian = Math.atan2(dif_Point_Y,dif_Point_X);
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            building_plus_X = Math.cos(radian)*1.0+mid_point_X;
            building_plus_Y = Math.sin(radian)*1.0+mid_point_Y;

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",building_plus_Y);
            add_node_map_plus.put("x",building_plus_X);


            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_plus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_plus_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            building_minus_X = Math.cos(radian_180)*1.0+mid_point_X;
            building_minus_Y = Math.sin(radian_180)*1.0+mid_point_Y;

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",building_minus_Y);
            add_node_map_minus.put("x",building_minus_X);

            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_minus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_minus_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(building_ID)).add(connect_building_edge_point_B,add_minus_node);
            ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(building_ID)).add(connect_building_edge_point_B+1,add_plus_node);
            System.out.println(connect_edge_point_B);
            //roadの方にnode追加
            tmp_shape = OsmToGmlConverter.addedConnectRoadList.get(Integer.parseInt(nearest_Shape_ID));
            point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_shape.get(connect_edge_point_A));
            point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_shape.get(connect_edge_point_B));

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_A);
            point_A_X = tmp_Map.get("x");
            point_A_Y = tmp_Map.get("y");

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_B);
            point_B_X = tmp_Map.get("x");
            point_B_Y = tmp_Map.get("y");

            mid_point_X = (point_A_X+point_B_X)/2;
            mid_point_Y = (point_A_Y+point_B_Y)/2;

            dif_Point_X = point_B_X - point_A_X;
            dif_Point_Y = point_B_Y - point_A_Y;

            radian = Math.atan2(dif_Point_Y,dif_Point_X);
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            road_plus_X = Math.cos(radian)*1.0+nearest_X;
            road_plus_Y = Math.sin(radian)*1.0+nearest_Y;

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",road_plus_Y);
            add_node_map_plus.put("x",road_plus_X);

            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_plus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_plus_road_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            road_minus_X = Math.cos(radian_180)*1.0+nearest_X;
            road_minus_Y = Math.sin(radian_180)*1.0+nearest_Y;

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",road_minus_Y);
            add_node_map_minus.put("x",road_minus_X);

            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_minus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_minus_road_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            ((ArrayList)OsmToGmlConverter.addedConnectRoadList.get(Integer.parseInt(nearest_Shape_ID))).add(connect_edge_point_B,add_minus_road_node);
            ((ArrayList)OsmToGmlConverter.addedConnectRoadList.get(Integer.parseInt(nearest_Shape_ID))).add(connect_edge_point_B+1,add_plus_road_node);

            ArrayList<String> addRoadArr3 = new ArrayList<String>();
            addRoadArr3.add(add_plus_node);
            addRoadArr3.add(add_plus_road_node);
            addRoadArr3.add(add_minus_road_node);
            addRoadArr3.add(add_minus_node);
            addRoadArr3.add(add_plus_node);

            OsmToGmlConverter.tmpRoadList.add(addRoadArr3);

            break;

            case "road":


            point_A = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(connect_building_edge_point_A));
            point_B = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(connect_building_edge_point_B));

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_A);
            point_A_X = tmp_Map.get("x");
            point_A_Y = tmp_Map.get("y");

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_B);
            point_B_X = tmp_Map.get("x");
            point_B_Y = tmp_Map.get("y");

            mid_point_X = (point_A_X+point_B_X)/2;
            mid_point_Y = (point_A_Y+point_B_Y)/2;

            dif_Point_X = point_B_X - point_A_X;
            dif_Point_Y = point_B_Y - point_A_Y;

            radian = Math.atan2(dif_Point_Y,dif_Point_X);
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            building_plus_X = Math.cos(radian)*1.0+mid_point_X;
            building_plus_Y = Math.sin(radian)*1.0+mid_point_Y;

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",building_plus_Y);
            add_node_map_plus.put("x",building_plus_X);


            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_plus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_plus_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            building_minus_X = Math.cos(radian_180)*1.0+mid_point_X;
            building_minus_Y = Math.sin(radian_180)*1.0+mid_point_Y;

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",building_minus_Y);
            add_node_map_minus.put("x",building_minus_X);

            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_minus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_minus_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(building_ID)).add(connect_building_edge_point_B,add_minus_node);
            ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(building_ID)).add(connect_building_edge_point_B+1,add_plus_node);
            System.out.println(connect_edge_point_B);
            //roadの方にnode追加
            tmp_shape = OsmToGmlConverter.tmpRoadList.get(Integer.parseInt(nearest_Shape_ID));
            point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_shape.get(connect_edge_point_A));
            point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_shape.get(connect_edge_point_B));

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_A);
            point_A_X = tmp_Map.get("x");
            point_A_Y = tmp_Map.get("y");

            tmp_Map = OsmToGmlConverter.nodeMap.get(point_B);
            point_B_X = tmp_Map.get("x");
            point_B_Y = tmp_Map.get("y");

            mid_point_X = (point_A_X+point_B_X)/2;
            mid_point_Y = (point_A_Y+point_B_Y)/2;

            dif_Point_X = point_B_X - point_A_X;
            dif_Point_Y = point_B_Y - point_A_Y;

            radian = Math.atan2(dif_Point_Y,dif_Point_X);
            degree = (radian*180)/Math.PI;

            radian_180 = ((degree+180)*Math.PI)/180.0;

            road_plus_X = Math.cos(radian)*1.0+nearest_X;
            road_plus_Y = Math.sin(radian)*1.0+nearest_Y;

            add_node_map_plus = new HashMap<String,Double>();

            add_node_map_plus.put("y",road_plus_Y);
            add_node_map_plus.put("x",road_plus_X);

            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_plus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_plus_road_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            road_minus_X = Math.cos(radian_180)*1.0+nearest_X;
            road_minus_Y = Math.sin(radian_180)*1.0+nearest_Y;

            add_node_map_minus = new HashMap<String,Double>();

            add_node_map_minus.put("y",road_minus_Y);
            add_node_map_minus.put("x",road_minus_X);

            while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                tmpNodeId++;
            }

            OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_minus);
            OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
            OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

            add_minus_road_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

            ((ArrayList)OsmToGmlConverter.tmpRoadList.get(Integer.parseInt(nearest_Shape_ID))).add(connect_edge_point_B,add_minus_road_node);
            ((ArrayList)OsmToGmlConverter.tmpRoadList.get(Integer.parseInt(nearest_Shape_ID))).add(connect_edge_point_B+1,add_plus_road_node);

            ArrayList<String> addRoadArr2 = new ArrayList<String>();
            addRoadArr2.add(add_plus_node);
            addRoadArr2.add(add_plus_road_node);
            addRoadArr2.add(add_minus_road_node);
            addRoadArr2.add(add_minus_node);
            addRoadArr2.add(add_plus_node);

            OsmToGmlConverter.tmpRoadList.add(addRoadArr2);


            break;

            case "building":

            point_A = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(connect_building_edge_point_A));
            point_B = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(connect_building_edge_point_B));

                        tmp_Map = OsmToGmlConverter.nodeMap.get(point_A);
                        point_A_X = tmp_Map.get("x");
                        point_A_Y = tmp_Map.get("y");

                        tmp_Map = OsmToGmlConverter.nodeMap.get(point_B);
                        point_B_X = tmp_Map.get("x");
                        point_B_Y = tmp_Map.get("y");

                        mid_point_X = (point_A_X+point_B_X)/2;
                        mid_point_Y = (point_A_Y+point_B_Y)/2;

                        dif_Point_X = point_B_X - point_A_X;
                        dif_Point_Y = point_B_Y - point_A_Y;

                        radian = Math.atan2(dif_Point_Y,dif_Point_X);
                        degree = (radian*180)/Math.PI;

                        radian_180 = ((degree+180)*Math.PI)/180.0;

                        building_plus_X = Math.cos(radian)*1.0+mid_point_X;
                        building_plus_Y = Math.sin(radian)*1.0+mid_point_Y;

                        add_node_map_plus = new HashMap<String,Double>();

                        add_node_map_plus.put("y",building_plus_Y);
                        add_node_map_plus.put("x",building_plus_X);

                        while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                            tmpNodeId++;
                        }

                        OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_plus);
                        OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
                        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

                        add_plus_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

                        building_minus_X = Math.cos(radian_180)*1.0+mid_point_X;
                        building_minus_Y = Math.sin(radian_180)*1.0+mid_point_Y;

                        add_node_map_minus = new HashMap<String,Double>();

                        add_node_map_minus.put("y",building_minus_Y);
                        add_node_map_minus.put("x",building_minus_X);

                        while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                            tmpNodeId++;
                        }

                        OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_minus);
                        OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
                        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

                        add_minus_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

                        ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(building_ID)).add(connect_building_edge_point_B,add_minus_node);
                        ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(building_ID)).add(connect_building_edge_point_B+1,add_plus_node);

                        //接続先のBuildingの方にnode追加
                        tmp_shape = OsmToGmlConverter.tmpBuildingList.get(Integer.parseInt(nearest_Shape_ID));
                        point_A = OsmToGmlConverter.linkInverseNodeID.get(tmp_shape.get(connect_edge_point_A));
                        point_B = OsmToGmlConverter.linkInverseNodeID.get(tmp_shape.get(connect_edge_point_B));

                        tmp_Map = OsmToGmlConverter.nodeMap.get(point_A);
                        point_A_X = tmp_Map.get("x");
                        point_A_Y = tmp_Map.get("y");

                        tmp_Map = OsmToGmlConverter.nodeMap.get(point_B);
                        point_B_X = tmp_Map.get("x");
                        point_B_Y = tmp_Map.get("y");

                        mid_point_X = (point_A_X+point_B_X)/2;
                        mid_point_Y = (point_A_Y+point_B_Y)/2;

                        dif_Point_X = point_B_X - point_A_X;
                        dif_Point_Y = point_B_Y - point_A_Y;

                        radian = Math.atan2(dif_Point_Y,dif_Point_X);
                        degree = (radian*180)/Math.PI;

                        radian_180 = ((degree+180)*Math.PI)/180.0;

                        road_plus_X = Math.cos(radian)*1.0+nearest_X;
                        road_plus_Y = Math.sin(radian)*1.0+nearest_Y;

                        add_node_map_plus = new HashMap<String,Double>();

                        add_node_map_plus.put("y",road_plus_Y);
                        add_node_map_plus.put("x",road_plus_X);

                        while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                            tmpNodeId++;
                        }

                        OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_plus);
                        OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
                        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

                        add_plus_road_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

                        road_minus_X = Math.cos(radian_180)*1.0+nearest_X;
                        road_minus_Y = Math.sin(radian_180)*1.0+nearest_Y;

                        add_node_map_minus = new HashMap<String,Double>();

                        add_node_map_minus.put("y",road_minus_Y);
                        add_node_map_minus.put("x",road_minus_X);

                        while(OsmToGmlConverter.nodeMap.containsKey(""+tmpNodeId)){
                            tmpNodeId++;
                        }

                        OsmToGmlConverter.nodeMap.put(""+tmpNodeId,add_node_map_minus);
                        OsmToGmlConverter.linkNodeID.put(""+tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
                        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+tmpNodeId);

                        add_minus_road_node = OsmToGmlConverter.linkNodeID.get(""+tmpNodeId);

                        ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(Integer.parseInt(nearest_Shape_ID))).add(connect_edge_point_B,add_minus_road_node);
                        ((ArrayList)OsmToGmlConverter.tmpBuildingList.get(Integer.parseInt(nearest_Shape_ID))).add(connect_edge_point_B+1,add_plus_road_node);

                        ArrayList<String> addRoadArr1 = new ArrayList<String>();
                        addRoadArr1.add(add_plus_node);
                        addRoadArr1.add(add_minus_road_node);
                        addRoadArr1.add(add_plus_road_node);
                        addRoadArr1.add(add_minus_node);
                        addRoadArr1.add(add_plus_node);

                        OsmToGmlConverter.tmpRoadList.add(addRoadArr1);

                        this.alreadyConnectBuilding.put(nearest_Shape_ID,""+building_ID);


            break;

        }

        return;
    }



    private ArrayList<Double> CheckCrossingPoint(Double ax,Double ay,Double bx,Double by,Double cx,Double cy,Double dx,Double dy){
        ArrayList<Double> xy = new ArrayList<Double>();
        //参照url https://gist.github.com/yoshiki/7702066
        Double d = (bx - ax)*(dy - cy) - (by - ay)*(dx - cx);
        Double u = ((cx - ax)*(dy - cy) - (cy - ay)*(dx - cx))/d;
        Double tmp_X = ax + u * (bx - ax);
        Double tmp_Y = ay + u * (by - ay);
        xy.add(tmp_X);
        xy.add(tmp_Y);
        return xy;
    }


    private Boolean checkCrossingRoad(ArrayList building_List){
        ArrayList<String> check_Road = new ArrayList<String>();
        //すべてのRoadの線分に対して交差しているか判定交差していなければtrueが帰る
        String point_A = new String();
        String point_B = new String();
        String point_C = new String();
        String point_D = new String();

        HashMap<String,Double> map_A = new HashMap<String,Double>();
        HashMap<String,Double> map_B = new HashMap<String,Double>();
        HashMap<String,Double> map_C = new HashMap<String,Double>();
        HashMap<String,Double> map_D = new HashMap<String,Double>();

        for (int i = 0; i<OsmToGmlConverter.tmpRoadList.size();i++ ) {
            check_Road = OsmToGmlConverter.tmpRoadList.get(i);
            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k));
                point_B = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k+1));
                for (int m = 0; m<building_List.size()-1;m++ ) {
                    point_C = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(m));
                    point_D = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(m+1));

                    map_A = OsmToGmlConverter.nodeMap.get(point_A);
                    map_B = OsmToGmlConverter.nodeMap.get(point_B);
                    map_C = OsmToGmlConverter.nodeMap.get(point_C);
                    map_D = OsmToGmlConverter.nodeMap.get(point_D);

                    if (checkCrossingLineSegment(map_A.get("x"),map_A.get("y"),map_B.get("x"),map_B.get("y"),map_C.get("x"),map_C.get("y"),map_D.get("x"),map_D.get("y"))) {
                        return false;
                    }
                }
            }
        }
        for (int i = 0; i<OsmToGmlConverter.addedConnectRoadList.size();i++ ) {
            check_Road = OsmToGmlConverter.addedConnectRoadList.get(i);
            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k));
                point_B = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k+1));
                for (int m = 0; m<building_List.size()-1;m++ ) {
                    point_C = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(m));
                    point_D = OsmToGmlConverter.linkInverseNodeID.get(building_List.get(m+1));

                    map_A = OsmToGmlConverter.nodeMap.get(point_A);
                    map_B = OsmToGmlConverter.nodeMap.get(point_B);
                    map_C = OsmToGmlConverter.nodeMap.get(point_C);
                    map_D = OsmToGmlConverter.nodeMap.get(point_D);

                    if (checkCrossingLineSegment(map_A.get("x"),map_A.get("y"),map_B.get("x"),map_B.get("y"),map_C.get("x"),map_C.get("y"),map_D.get("x"),map_D.get("y"))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Boolean checkCrossingBuilding(Double origin_X,Double origin_Y,Double connect_X,Double connect_Y){
        ArrayList<String> check_Road = new ArrayList<String>();
        //すべてのRoadの線分に対して交差しているか判定交差していなければtrueが帰る
        String point_A = new String();
        String point_B = new String();

        HashMap<String,Double> map_A = new HashMap<String,Double>();
        HashMap<String,Double> map_B = new HashMap<String,Double>();

        for (int i = 0; i<OsmToGmlConverter.tmpBuildingList.size();i++ ) {
            check_Road = OsmToGmlConverter.tmpBuildingList.get(i);
            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k));
                point_B = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k+1));

                map_A = OsmToGmlConverter.nodeMap.get(point_A);
                map_B = OsmToGmlConverter.nodeMap.get(point_B);

                if (checkCrossingLineSegment(origin_X,origin_Y,connect_X,connect_Y,map_A.get("x"),map_A.get("y"),map_B.get("x"),map_B.get("y"))) {
                    return false;
                }
            }
        }
        for (int i = 0; i<OsmToGmlConverter.tmpRoadList.size();i++ ) {
            check_Road = OsmToGmlConverter.tmpRoadList.get(i);
            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k));
                point_B = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k+1));

                map_A = OsmToGmlConverter.nodeMap.get(point_A);
                map_B = OsmToGmlConverter.nodeMap.get(point_B);

                if (checkCrossingLineSegment(origin_X,origin_Y,connect_X,connect_Y,map_A.get("x"),map_A.get("y"),map_B.get("x"),map_B.get("y"))) {
                    return false;
                }
            }
        }
        for (int i = 0; i<OsmToGmlConverter.addedConnectRoadList.size();i++ ) {
            check_Road = OsmToGmlConverter.addedConnectRoadList.get(i);
            for (int k = 0; k<check_Road.size()-1;k++ ) {
                point_A = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k));
                point_B = OsmToGmlConverter.linkInverseNodeID.get(check_Road.get(k+1));

                map_A = OsmToGmlConverter.nodeMap.get(point_A);
                map_B = OsmToGmlConverter.nodeMap.get(point_B);

                if (checkCrossingLineSegment(origin_X,origin_Y,connect_X,connect_Y,map_A.get("x"),map_A.get("y"),map_B.get("x"),map_B.get("y"))) {
                    return false;
                }
            }
        }
        return true;
    }
    private Boolean checkCrossingLineSegment(Double ax,Double ay,Double bx,Double by,Double cx,Double cy,Double dx,Double dy){
        //２線分を比べ交差していたらtrueを返す
        Double ta = (cx - dx) * (ay - cy) + (cy - dy) * (cx - ax);
        Double tb = (cx - dx) * (by - cy) + (cy - dy) * (cx - bx);
        Double tc = (ax - bx) * (cy - ay) + (ay - by) * (ax - cx);
        Double td = (ax - bx) * (dy - ay) + (ay - by) * (ax - dx);
        if ((tc * td) < 0 && (ta * tb) < 0) {
            return true;
        }
        return false;
    }

    private Boolean checkContainNode(Double x,Double y){
        //x,yから指定した円の範囲にnodeがあった場合falseを返す なかった場合はtrue
        Double distance;
        for (int i=1; i<=OsmToGmlConverter.nodeMap.size();i++ ) {
            HashMap<String,Double> tmp_map = OsmToGmlConverter.nodeMap.get(OsmToGmlConverter.linkInverseNodeID.get(""+i));
            distance = (tmp_map.get("x")-x)*(tmp_map.get("x")-x)+(tmp_map.get("y")-y)*(tmp_map.get("y")-y);
            distance = Math.sqrt(distance);
            if (distance < 1.1) {
                return false;
            }
        }
        return true;
    }

}
