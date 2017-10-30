import java.util.ArrayList;
import java.util.HashMap;

public class ExpansionHighway {
    private static Double refDistance = 3.0;
    private static int tmpNodeId = 1;
    private static ArrayList checkedNodeList = new ArrayList();
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

}
