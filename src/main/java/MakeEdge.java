import java.util.ArrayList;

//edge作成用クラス
public class MakeEdge {

    private static int  tmpEdgeId= 1;
    private static int tmpBuildingId = 1;
    private static int tmpRoadId = 1;

    private static String tmpNodeID = new String();
    public MakeEdge(){

        OsmToGmlConverter.tmpBuildingList.forEach(nodes -> {
            //nodes = ArrayList
            String tmpNode = new String();
            ArrayList<String> tmpEdgeList = new ArrayList<String>();
            tmpNode = nodes.get(0);
            for (int i=1;i<nodes.size() ; i++) {
                ArrayList<String> tmpNodeList = new ArrayList<String>();

                tmpNodeList.add(tmpNode);
                tmpNodeList.add(nodes.get(i));

                OsmToGmlConverter.usedNodeList.add(tmpNode);
                OsmToGmlConverter.usedNodeList.add(nodes.get(i));

                OsmToGmlConverter.edgeMap.put(""+tmpEdgeId,tmpNodeList);
                tmpEdgeList.add(""+tmpEdgeId);
                tmpNode = nodes.get(i);
                tmpEdgeId++;
            }
            //BuildingMapを書く
            OsmToGmlConverter.buildingMap.put(""+tmpBuildingId,tmpEdgeList);

            tmpBuildingId++;
        });


        OsmToGmlConverter.tmpRoadList.forEach(nodes -> {
            //nodes = ArrayList
            String tmpNode = new String();
            ArrayList<String> tmpEdgeList = new ArrayList<String>();
            tmpNode = nodes.get(0);
            for (int i=1;i<nodes.size() ; i++) {
                ArrayList tmpNodeList = new ArrayList();
                String checkEdgeId = new String();

                tmpNodeList.add(tmpNode);
                tmpNodeList.add(nodes.get(i));

                OsmToGmlConverter.usedNodeList.add(tmpNode);
                OsmToGmlConverter.usedNodeList.add(nodes.get(i));

                //すでにあるEdgeか判定
                checkEdgeId = checkEdge(tmpNode,nodes.get(i));

                if (checkEdgeId == "0") {
                    OsmToGmlConverter.edgeMap.put(""+tmpEdgeId,tmpNodeList);
                    tmpEdgeList.add(""+tmpEdgeId);
                    tmpEdgeId++;
                }else{
                    tmpEdgeList.add(""+checkEdgeId);
                }
                tmpNode = nodes.get(i);
            }
            OsmToGmlConverter.roadMap.put(""+tmpRoadId,tmpEdgeList);

            tmpRoadId++;
            //RoadMapを書く
        });
        OsmToGmlConverter.addedConnectRoadList.forEach(nodes -> {
            //nodes = ArrayList
            String tmpNode = new String();
            ArrayList<String> tmpMinusEdgeList = new ArrayList<String>();
            ArrayList<String> tmpEdgeList = new ArrayList<String>();
            tmpNode = nodes.get(0);
            for (int i=1;i<nodes.size() ; i++) {
                ArrayList<String> tmpNodeList = new ArrayList<String>();
                String checkEdgeId = new String();

                tmpNodeList.add(tmpNode);
                tmpNodeList.add(nodes.get(i));

                OsmToGmlConverter.usedNodeList.add(tmpNode);
                OsmToGmlConverter.usedNodeList.add(nodes.get(i));

                //すでにあるEdgeか判定
                checkEdgeId = checkEdge(tmpNode,nodes.get(i));

                if (checkEdgeId == "0") {
                    OsmToGmlConverter.edgeMap.put(""+tmpEdgeId,tmpNodeList);
                    tmpEdgeList.add(""+tmpEdgeId);
                    tmpEdgeId++;
                }else{
                    tmpEdgeList.add(""+checkEdgeId);
                    tmpMinusEdgeList.add(""+checkEdgeId);
                }
                tmpNode = nodes.get(i);
            }
            OsmToGmlConverter.roadMap.put(""+tmpRoadId,tmpEdgeList);
            OsmToGmlConverter.minusDirectionEdgeMap.put(""+tmpRoadId,tmpMinusEdgeList);

            tmpRoadId++;
            //RoadMapを書く
        });

    }
    private String checkEdge(String node1,String node2){
        this.tmpNodeID = "0";
        OsmToGmlConverter.edgeMap.forEach((id, edgeNodes)->{

            if (edgeNodes.contains(node1)) {
                if (edgeNodes.contains(node2)) {
                    this.tmpNodeID = id;
                }
            }
        });
        return this.tmpNodeID;
    }
}
