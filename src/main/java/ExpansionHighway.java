import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpansionHighway {
    private static Double refDistance = 3.0;
    private static ArrayList checkedNodeList = new ArrayList();

    private NodeManager     nm;
    private HighwayManager  hm;
    private RoadManager     rm;

    public ExpansionHighway(NodeManager     nm,
                            HighwayManager  hm,
                            RoadManager     rm) {
        this.nm = nm;
        this.hm = hm;
        this.rm = rm;
    }

    public void ExpantionHighway() {
        //tmpHighwayList(Array<Array<String>>)の外側のArrayを回す　配列管理
        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {
            ArrayList<String> tmp = new ArrayList<String>();
            tmp = (ArrayList)hm.getTmpHighwayList().get(i);
            String tmpNode = tmp.get(0);

            //tmpHighwayList(Array)<Array<String>>の内側のArrayを回す　node管理
            for (int j=1; j<tmp.size(); j++) {
                ArrayList arrayListA = new ArrayList();
                ArrayList arrayListB = new ArrayList();
                //現在地のnodeと次のnodeの2点を保持　次点はjで管理故に1から始まる
                String pointA = tmpNode;
                String pointB = tmp.get(j);

                if (checkDistance(pointA,pointB)) {
                    //2点でそれぞれ三角形を作成
                    arrayListA = AddNode(pointA,pointB);
                    arrayListB = AddNode(pointB,pointA);
                    rm.setTmpRoadList(arrayListA);
                    rm.setTmpRoadList(arrayListB);
                    //2つの三角形を四角で結ぶ
                    AddSquareTmpRoad(arrayListA,arrayListB);
                }else{
                    //2点間の距離が短い場合，２点間でひし形(三角形2つ)を作成
                    AddNodeShort(pointA,pointB);
                }
                tmpNode = tmp.get(j);
            }
        }

        //tmpHiwayList中の全てのnodeに大して道同士を繋げる処理を行う
        for (int i=0; i<hm.getTmpHighwayList().size(); i++) {
            ArrayList<String> tmp = new ArrayList<String>();
            tmp = (ArrayList)hm.getTmpHighwayList().get(i);

            for (int j=0; j<tmp.size(); j++) {
                if (this.checkedNodeList.contains(tmp.get(j))) {

                } else {
                    ConnectRoad(tmp.get(j));
                    this.checkedNodeList.add(tmp.get(j));
                }
            }
        }
    }

    //highwaの各点を三角形状に拡張
    private ArrayList AddNode(String nodeNumA,String nodeNumB){
        ArrayList array = new ArrayList();
        array.add(nodeNumA);

        //ここcheckedDistanceの一部と被ってる
        double pointAx = nm.getX(nodeNumA);
        double pointAy = nm.getY(nodeNumA);
        double pointBx = nm.getX(nodeNumB);
        double pointBy = nm.getY(nodeNumB);

        double difPointX = pointBx - pointAx;
        double difPointY = pointBy - pointAy;

        double radian = Math.atan2(difPointY,difPointX);
        double degree = (radian*180)/Math.PI;

        double tmpX = Math.cos(radian)*refDistance*Math.sqrt(3);
        double tmpY = Math.sin(radian)*refDistance*Math.sqrt(3);

        Double radian1 = ((degree-90)*Math.PI)/180.0;
        Double radian2 = ((degree+90)*Math.PI)/180.0;

        Double point1x = Math.cos(radian1)*refDistance+tmpX+pointAx;
        Double point1y = Math.sin(radian1)*refDistance+tmpY+pointAy;
        Double point2x = Math.cos(radian2)*refDistance+tmpX+pointAx;
        Double point2y = Math.sin(radian2)*refDistance+tmpY+pointAy;

        HashMap<String,Double> map1 = new HashMap<String,Double>();
        HashMap<String,Double> map2 = new HashMap<String,Double>();

        map1.put("y",point1y);
        map1.put("x",point1x);
        map2.put("y",point2y);
        map2.put("x",point2x);

        array.add(nm.addGmlNode(map1));
        array.add(nm.addGmlNode(map2));
        array.add(nodeNumA);

        return array;

    }

    //highwayの2点間が短いとfalse
    private Boolean checkDistance(String nodeNumA,String nodeNumB){
        double pointAx = nm.getX(nodeNumA);
        double pointAy = nm.getY(nodeNumA);
        double pointBx = nm.getX(nodeNumB);
        double pointBy = nm.getY(nodeNumB);

        double distance = Math.sqrt(Math.pow((pointBx - pointAx),2) + Math.pow((pointBy - pointAy),2));

        if( distance > refDistance*2.5){ //本来は*2でいいはず 要検証
            return true;
        }else{
            return false;
        }

    }

    //2点の三角形同士を四角形で結ぶ
    private void AddSquareTmpRoad(ArrayList arrayA,ArrayList arrayB){
        ArrayList array = new ArrayList();
        array.add(arrayA.get(2));
        array.add(arrayA.get(1));
        array.add(arrayB.get(2));
        array.add(arrayB.get(1));
        array.add(arrayA.get(2));
        rm.setTmpRoadList(array);
    }

    //highwayの2点間が短い場合に2点間にひし形(三角形2つ)を作成
    private void AddNodeShort(String nodeNumA,String nodeNumB){
        //highwayのノード二つが十分に離れていない場合
        double pointAx = nm.getX(nodeNumA);
        double pointAy = nm.getY(nodeNumA);
        double pointBx = nm.getX(nodeNumB);
        double pointBy = nm.getY(nodeNumB);

        double difPointX = pointBx - pointAx;
        double difPointY = pointBy - pointAy;

        double radian = Math.atan2(difPointY,difPointX);
        double degree = (radian*180)/Math.PI;

        double halfDistance = Math.sqrt(Math.pow((pointBx - pointAx),2) + Math.pow((pointBy - pointAy),2))/2;

        double tmpX = Math.cos(radian)*halfDistance+pointAx;
        double tmpY = Math.sin(radian)*halfDistance+pointAy;

        Double radian1 = ((degree-90)*Math.PI)/180.0;
        Double radian2 = ((degree+90)*Math.PI)/180.0;

        Double point1x = Math.cos(radian1)*refDistance+tmpX;
        Double point1y = Math.sin(radian1)*refDistance+tmpY;
        Double point2x = Math.cos(radian2)*refDistance+tmpX;
        Double point2y = Math.sin(radian2)*refDistance+tmpY;

        HashMap<String,Double> map1 = new HashMap<String,Double>();
        HashMap<String,Double> map2 = new HashMap<String,Double>();

        map1.put("y",point1y);
        map1.put("x",point1x);
        map2.put("y",point2y);
        map2.put("x",point2x);

        ArrayList arrayA = new ArrayList();
        ArrayList arrayB = new ArrayList();

        String node_map1 = nm.addGmlNode(map1);
        String node_map2 = nm.addGmlNode(map2);

        arrayA.add(nodeNumA);
        arrayA.add(node_map1);
        arrayA.add(node_map2);
        arrayA.add(nodeNumA);

        arrayB.add(nodeNumB);
        arrayB.add(node_map2);
        arrayB.add(node_map1);
        arrayB.add(nodeNumB);

        rm.setTmpRoadList(arrayA);
        rm.setTmpRoadList(arrayB);
    }

    //道路同士の接続部分を作成
    private void ConnectRoad(String node){
        int roadNum = 0;
        ArrayList tmpRoadNodeList = new ArrayList();

        for (int i=1; i<=Integer.parseInt(rm.getRoadNodeID()); i++) {
            if (rm.getRoadNodeList(String.valueOf(i)).contains(node)) {
                roadNum++;
                tmpRoadNodeList.add(rm.getRoadNodeList(String.valueOf(i)).get(1));
                tmpRoadNodeList.add(rm.getRoadNodeList(String.valueOf(i)).get(2));
            }
        }

        switch (roadNum) {
            case 1:
                break;
            case 2:
                double degree00 = calcDegreePoint(node, (String)tmpRoadNodeList.get(0));
                if (degree00 < 0) {
                    degree00+=360;
                }
                double degree01 = calcDegreePoint(node, (String)tmpRoadNodeList.get(1));
                if (degree01 < 0) {
                    degree01+=360;
                }
                double degree02 = calcDegreePoint(node, (String)tmpRoadNodeList.get(2));
                if (degree02 < 0) {
                    degree02+=360;
                }
                double degree03 = calcDegreePoint(node, (String)tmpRoadNodeList.get(3));
                if (degree03 < 0) {
                    degree03+=360;
                }
                double degree0to3 = degree00 - degree03;
                if (degree0to3 < 0) {
                    degree0to3+=360;
                }
                double degree2to1 = degree02 - degree01;
                if (degree2to1 < 0) {
                    degree2to1+=360;
                }

                if (degree0to3 < 180) {
                    ArrayList array = new ArrayList();
                    array.add(node);
                    array.add(tmpRoadNodeList.get(3));
                    array.add(tmpRoadNodeList.get(0));
                    array.add(node);
                    rm.setTmpRoadList(array);
                }
                if (degree2to1 < 180) {
                    ArrayList array = new ArrayList();
                    array.add(node);
                    array.add(tmpRoadNodeList.get(1));
                    array.add(tmpRoadNodeList.get(2));
                    array.add(node);
                    rm.setTmpRoadList(array);
                }
                break;

            default:
                for (int i = 0; i <tmpRoadNodeList.size() ;i+=2 ) {
                    ArrayList array = new ArrayList();
                    double minDegree =360;
                    int usePoint = 0;

                    for (int k = 1; k< tmpRoadNodeList.size();k+=2 ) {
                        double degree1 = calcDegreePoint(node, (String)tmpRoadNodeList.get(i));

                        if (degree1 < 0) {
                            degree1+=360;
                        }
                        double degree2 = calcDegreePoint(node , (String)tmpRoadNodeList.get(k));

                        if (degree2 < 0) {
                            degree2+=360;
                        }
                        double difDegree = degree1 - degree2;
                        if (difDegree < 0) {
                            difDegree +=360;
                        }


                        if (minDegree > difDegree ) {
                            minDegree = difDegree;
                            usePoint = k;
                        }
                    }
                    array.add(node);
                    array.add(tmpRoadNodeList.get(usePoint));
                    array.add(tmpRoadNodeList.get(i));
                    array.add(node);
                    rm.setTmpRoadList(array);
                }

                break;
        }
    }
    //リファクタリングなし
    private double calcDegreeLine(String node1, String node2){
        double node1x = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node1)).get("x");
        double node1y = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node1)).get("y");
        double node2x = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node2)).get("x");
        double node2y = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node2)).get("y");
        double form1 = node1x*node2x+node1y*node2y;
        double form2 = Math.sqrt((Math.pow(node1x,2)+Math.pow(node1y,2))*(Math.pow(node2x,2)+Math.pow(node2y,2)));
        double degree = Math.acos(form1/form2) * 180.0 / Math.PI;

        return degree;
    }

    //接続候補の2点間の距離を計算
    private double calcDegreePoint(String node1,String node2){
        double node1x = nm.getX(node1);
        double node1y = nm.getY(node1);
        double node2x = nm.getX(node2);
        double node2y = nm.getY(node2);
        double degree = Math.atan2(node2y-node1y,node2x-node1x) * 180.0 / Math.PI;
        return degree;
    }


    /*
    public ExpansionHighway(){
        OsmToGmlConverter.tmpHighwayList.forEach(nodes -> {
            //nodes = ArrayList
            String tmpNode = new String();

            tmpNode = nodes.get(0);
            for (int i=1;i < nodes.size() ; i++) {

                ArrayList arrayListA = new ArrayList();
                ArrayList arrayListB = new ArrayList();

                String pointA = OsmToGmlConverter.linkInverseNodeID.get(tmpNode);
                String pointB = OsmToGmlConverter.linkInverseNodeID.get(nodes.get(i));

                if (checkDistance(pointA,pointB)) {
                    arrayListA = AddNode(pointA,pointB);
                    arrayListB = AddNode(pointB,pointA);
                    OsmToGmlConverter.tmpRoadList.add(arrayListA);
                    OsmToGmlConverter.tmpRoadList.add(arrayListB);
                    AddSquareTmpRoad(arrayListA,arrayListB);
                }else{
                    AddNodeShort(pointA,pointB);
                }
                tmpNode = nodes.get(i);
            }
        });

        OsmToGmlConverter.tmpHighwayList.forEach(nodes->{
            for (int i = 0; i< nodes.size();i++ ) {
                if (this.checkedNodeList.contains(nodes.get(i))) {

                }else{
                    ConnectRoad(nodes.get(i));
                    this.checkedNodeList.add(nodes.get(i));
                }
            }

        });

    }

    private ArrayList AddNode(String nodeNumA,String nodeNumB){

        ArrayList array = new ArrayList();
        array.add(OsmToGmlConverter.linkNodeID.get(nodeNumA));


        HashMap<String,Double> map1 = new HashMap<String,Double>();
        HashMap<String,Double> map2 = new HashMap<String,Double>();

        HashMap tmpMap = OsmToGmlConverter.nodeMap.get(nodeNumA);

        double pointAx = (double)tmpMap.get("x");
        double pointAy = (double)tmpMap.get("y");

        tmpMap = OsmToGmlConverter.nodeMap.get(nodeNumB);

        double pointBx = (double)tmpMap.get("x");
        double pointBy = (double)tmpMap.get("y");

        double difPointX = pointBx - pointAx;
        double difPointY = pointBy - pointAy;

        double radian = Math.atan2(difPointY,difPointX);
        double degree = (radian*180)/Math.PI;

        double tmpX = Math.cos(radian)*refDistance*Math.sqrt(3);
        double tmpY = Math.sin(radian)*refDistance*Math.sqrt(3);


        Double radian1 = ((degree-90)*Math.PI)/180.0;
        Double radian2 = ((degree+90)*Math.PI)/180.0;

        Double point1x = Math.cos(radian1)*refDistance+tmpX+pointAx;
        Double point1y = Math.sin(radian1)*refDistance+tmpY+pointAy;

        Double point2x = Math.cos(radian2)*refDistance+tmpX+pointAx;
        Double point2y = Math.sin(radian2)*refDistance+tmpY+pointAy;

        map1.put("y",point1y);
        map1.put("x",point1x);

        map2.put("y",point2y);
        map2.put("x",point2x);

        while(OsmToGmlConverter.nodeMap.containsKey(""+this.tmpNodeId)){
            this.tmpNodeId++;
        }

        OsmToGmlConverter.nodeMap.put(""+this.tmpNodeId,map1);
        OsmToGmlConverter.linkNodeID.put(""+this.tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+this.tmpNodeId);

        array.add(""+ OsmToGmlConverter.nodeMap.size());

        this.tmpNodeId++;

        while(OsmToGmlConverter.nodeMap.containsKey(""+this.tmpNodeId)){
            this.tmpNodeId++;
        }

        OsmToGmlConverter.nodeMap.put(""+this.tmpNodeId,map2);
        OsmToGmlConverter.linkNodeID.put(""+this.tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+this.tmpNodeId);

        array.add(""+ OsmToGmlConverter.nodeMap.size());

        this.tmpNodeId++;

        array.add(OsmToGmlConverter.linkNodeID.get(nodeNumA));

        return array;

    }


    private Boolean checkDistance(String nodeNumA,String nodeNumB){
        HashMap tmpMap = OsmToGmlConverter.nodeMap.get(nodeNumA);

        double pointAx = (double)tmpMap.get("x");
        double pointAy = (double)tmpMap.get("y");

        tmpMap = OsmToGmlConverter.nodeMap.get(nodeNumB);

        double pointBx = (double)tmpMap.get("x");
        double pointBy = (double)tmpMap.get("y");

        double distance = Math.sqrt(Math.pow((pointBx - pointAx),2) + Math.pow((pointBy - pointAy),2));

        if( distance > refDistance*2.5){ //本来は*2でいいはず　なんでうまくいかんのや、検証しなかん
            return true;
        }else{
            return false;
        }

    }

    private void AddSquareTmpRoad(ArrayList arrayA,ArrayList arrayB){
        ArrayList array = new ArrayList();
        array.add(arrayA.get(2));
        array.add(arrayA.get(1));
        array.add(arrayB.get(2));
        array.add(arrayB.get(1));
        array.add(arrayA.get(2));
        OsmToGmlConverter.addedConnectRoadList.add(array);
    }

    private void AddNodeShort(String nodeNumA,String nodeNumB){
        //highwayのノード二つが十分に離れていない場合
        ArrayList arrayA = new ArrayList();
        arrayA.add(OsmToGmlConverter.linkNodeID.get(nodeNumA));

        ArrayList arrayB = new ArrayList();
        arrayB.add(OsmToGmlConverter.linkNodeID.get(nodeNumB));

        String tmpPointNode;

        HashMap<String,Double> map1 = new HashMap<String,Double>();
        HashMap<String,Double> map2 = new HashMap<String,Double>();

        HashMap tmpMap = OsmToGmlConverter.nodeMap.get(nodeNumA);

        double pointAx = (double)tmpMap.get("x");
        double pointAy = (double)tmpMap.get("y");

        tmpMap = OsmToGmlConverter.nodeMap.get(nodeNumB);

        double pointBx = (double)tmpMap.get("x");
        double pointBy = (double)tmpMap.get("y");

        double difPointX = pointBx - pointAx;
        double difPointY = pointBy - pointAy;

        double radian = Math.atan2(difPointY,difPointX);
        double degree = (radian*180)/Math.PI;

        double halfDistance = Math.sqrt(Math.pow((pointBx - pointAx),2) + Math.pow((pointBy - pointAy),2))/2;

        double tmpX = Math.cos(radian)*halfDistance+pointAx;
        double tmpY = Math.sin(radian)*halfDistance+pointAy;


        Double radian1 = ((degree-90)*Math.PI)/180.0;
        Double radian2 = ((degree+90)*Math.PI)/180.0;

        Double point1x = Math.cos(radian1)*refDistance+tmpX;
        Double point1y = Math.sin(radian1)*refDistance+tmpY;

        Double point2x = Math.cos(radian2)*refDistance+tmpX;
        Double point2y = Math.sin(radian2)*refDistance+tmpY;

        map1.put("y",point1y);
        map1.put("x",point1x);

        map2.put("y",point2y);
        map2.put("x",point2x);

        while(OsmToGmlConverter.nodeMap.containsKey(""+this.tmpNodeId)){
            this.tmpNodeId++;
        }

        OsmToGmlConverter.nodeMap.put(""+this.tmpNodeId,map1);
        OsmToGmlConverter.linkNodeID.put(""+this.tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+this.tmpNodeId);

        arrayA.add(""+ OsmToGmlConverter.nodeMap.size());
        tmpPointNode = ""+ OsmToGmlConverter.nodeMap.size();


        this.tmpNodeId++;

        while(OsmToGmlConverter.nodeMap.containsKey(""+this.tmpNodeId)){
            this.tmpNodeId++;
        }

        OsmToGmlConverter.nodeMap.put(""+this.tmpNodeId,map2);
        OsmToGmlConverter.linkNodeID.put(""+this.tmpNodeId,""+ OsmToGmlConverter.nodeMap.size());
        OsmToGmlConverter.linkInverseNodeID.put(""+ OsmToGmlConverter.nodeMap.size(),""+this.tmpNodeId);

        arrayA.add(""+ OsmToGmlConverter.nodeMap.size());
        arrayB.add(""+ OsmToGmlConverter.nodeMap.size());

        this.tmpNodeId++;


        arrayB.add(tmpPointNode);

        arrayA.add(OsmToGmlConverter.linkNodeID.get(nodeNumA));
        arrayB.add(OsmToGmlConverter.linkNodeID.get(nodeNumB));

        OsmToGmlConverter.tmpRoadList.add(arrayA);
        OsmToGmlConverter.tmpRoadList.add(arrayB);

    }
    private void ConnectRoad(String node){
        int roadNum = 0;
        ArrayList tmpRoadNodeList = new ArrayList();
        for (int i = 0; i< OsmToGmlConverter.tmpRoadList.size(); i++ ) {
            if (((ArrayList) OsmToGmlConverter.tmpRoadList.get(i)).contains(node)) {
                roadNum++;
                tmpRoadNodeList.add(OsmToGmlConverter.linkInverseNodeID.get(((ArrayList) OsmToGmlConverter.tmpRoadList.get(i)).get(1)));
                tmpRoadNodeList.add(OsmToGmlConverter.linkInverseNodeID.get(((ArrayList) OsmToGmlConverter.tmpRoadList.get(i)).get(2)));
            }
        }
        switch (roadNum) {
            case 1:
                break;
            case 2:

                double degree00 = calcDegreePoint(OsmToGmlConverter.linkInverseNodeID.get(node),""+tmpRoadNodeList.get(0));
                if (degree00 < 0) {
                    degree00+=360;
                }
                double degree01 = calcDegreePoint(OsmToGmlConverter.linkInverseNodeID.get(node),""+tmpRoadNodeList.get(1));
                if (degree01 < 0) {
                    degree01+=360;
                }
                double degree02 = calcDegreePoint(OsmToGmlConverter.linkInverseNodeID.get(node),""+tmpRoadNodeList.get(2));
                if (degree02 < 0) {
                    degree02+=360;
                }
                double degree03 = calcDegreePoint(OsmToGmlConverter.linkInverseNodeID.get(node),""+tmpRoadNodeList.get(3));
                if (degree03 < 0) {
                    degree03+=360;
                }
                double degree0to3 = degree00 - degree03;
                if (degree0to3 < 0) {
                    degree0to3+=360;
                }
                double degree2to1 = degree02 - degree01;
                if (degree2to1 < 0) {
                    degree2to1+=360;
                }

                if (degree0to3 < 180) {
                    ArrayList array = new ArrayList();
                    array.add(node);
                    array.add(OsmToGmlConverter.linkNodeID.get(tmpRoadNodeList.get(3)));
                    array.add(OsmToGmlConverter.linkNodeID.get(tmpRoadNodeList.get(0)));
                    array.add(node);
                    OsmToGmlConverter.addedConnectRoadList.add(array);
                }
                if (degree2to1 < 180) {
                    ArrayList array = new ArrayList();
                    array.add(node);
                    array.add(OsmToGmlConverter.linkNodeID.get(tmpRoadNodeList.get(1)));
                    array.add(OsmToGmlConverter.linkNodeID.get(tmpRoadNodeList.get(2)));
                    array.add(node);

                    OsmToGmlConverter.addedConnectRoadList.add(array);
                }

                break;
            default:
                for (int i = 0; i <tmpRoadNodeList.size() ;i+=2 ) {
                    ArrayList array = new ArrayList();
                    double minDegree =360;
                    int usePoint = 0;

                    for (int k = 1; k< tmpRoadNodeList.size();k+=2 ) {
                        double degree1 = calcDegreePoint(OsmToGmlConverter.linkInverseNodeID.get(node),""+tmpRoadNodeList.get(i));

                        if (degree1 < 0) {
                            degree1+=360;
                        }
                        double degree2 = calcDegreePoint(OsmToGmlConverter.linkInverseNodeID.get(node) ,""+tmpRoadNodeList.get(k));

                        if (degree2 < 0) {
                            degree2+=360;
                        }
                        double difDegree = degree1 - degree2;
                        if (difDegree < 0) {
                            difDegree +=360;
                        }


                        if (minDegree > difDegree ) {
                            minDegree = difDegree;
                            usePoint = k;
                        }
                    }
                    array.add(node);

                    array.add(OsmToGmlConverter.linkNodeID.get(tmpRoadNodeList.get(usePoint)));
                    array.add(OsmToGmlConverter.linkNodeID.get(tmpRoadNodeList.get(i)));
                    array.add(node);
                    OsmToGmlConverter.addedConnectRoadList.add(array);
                }

                break;
        }
    }
    private double calcDegreeLine(String node1, String node2){
        double node1x = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node1)).get("x");
        double node1y = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node1)).get("y");
        double node2x = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node2)).get("x");
        double node2y = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node2)).get("y");
        double form1 = node1x*node2x+node1y*node2y;
        double form2 = Math.sqrt((Math.pow(node1x,2)+Math.pow(node1y,2))*(Math.pow(node2x,2)+Math.pow(node2y,2)));
        double degree = Math.acos(form1/form2) * 180.0 / Math.PI;

        return degree;
    }

    private double calcDegreePoint(String node1,String node2){
        double node1x = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node1)).get("x");
        double node1y = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node1)).get("y");
        double node2x = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node2)).get("x");
        double node2y = (double)((HashMap) OsmToGmlConverter.nodeMap.get(node2)).get("y");
        double degree = Math.atan2(node2y-node1y,node2x-node1x) * 180.0 / Math.PI;

        return degree;

    }
    */
}
