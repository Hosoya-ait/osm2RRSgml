import java.util.ArrayList;

//edge作成用クラス
public class MakeEdge {

    private static String tmpNodeID = new String();

    private NodeManager     nm;
    private EdgeManager     em;
    private BuildingManager bm;
    private RoadManager     rm;

    public MakeEdge(NodeManager     nm,
                    EdgeManager     em,
                    BuildingManager bm,
                    RoadManager     rm){
        this.nm = nm;
        this.em = em;
        this.bm = bm;
        this.rm = rm;
    }

    public void MakeEdge () {
        for (int i=1; i<=Integer.parseInt(bm.getBuildingNodeID()); i++) {
            ArrayList<String> tmpEdgeList = new ArrayList<String>();
            String tmpNode = (String)bm.getBuildingNodeList(String.valueOf(i)).get(0);

            for (int j=1; j<bm.getBuildingNodeList(String.valueOf(i)).size(); j++) {
                ArrayList<String> tmpNodeList = new ArrayList<String>();
                String tmpNodeNext = (String)bm.getBuildingNodeList(String.valueOf(i)).get(j);

                tmpNodeList.add(tmpNode);
                tmpNodeList.add(tmpNodeNext);

                nm.setUsedNodeList(tmpNode);
                nm.setUsedNodeList(tmpNodeNext);

                em.setEdgeMap(tmpNodeList);
                tmpEdgeList.add(em.getEdgeID());
                tmpNode = tmpNodeNext;
            }
            bm.setBuildingEdgeList(tmpEdgeList);
        }

        for (int i=1; i<=Integer.parseInt(rm.getRoadNodeID()); i++){
            ArrayList<String> tmpEdgeList = new ArrayList<String>();
            ArrayList<String> tmpMinusEdgeList = new ArrayList<String>();
            String tmpNode = (String)rm.getRoadNodeList(String.valueOf(i)).get(0);

            for (int j=1; j<rm.getRoadNodeList(String.valueOf(i)).size(); j++) {
                ArrayList tmpNodeList = new ArrayList();
                String tmpNodeNext = (String)rm.getRoadNodeList(String.valueOf(i)).get(j);

                tmpNodeList.add(tmpNode);
                tmpNodeList.add(tmpNodeNext);

                nm.setUsedNodeList(tmpNode);
                nm.setUsedNodeList(tmpNodeNext);

                //すでにあるEdgeか判定
                String checkEdgeId = new String();
                checkEdgeId = checkEdge(tmpNode, tmpNodeNext);

                if (checkEdgeId == "0") {
                    em.setEdgeMap(tmpNodeList);
                    tmpEdgeList.add(em.getEdgeID());
                }else{
                    tmpEdgeList.add(checkEdgeId);
                }
                tmpNode = tmpNodeNext;
            }
//            System.out.println("tmpEdgeList = "  +tmpEdgeList);


            // ここでやってるマイナスの処理多分間違ってる　全部マイナスにしてるから
            rm.setRoadMap(tmpEdgeList);
        }
    }

    private String checkEdge(String node1,String node2){
        this.tmpNodeID = "0";

        for (int i=1; i<=Integer.parseInt(em.getEdgeID()); i++) {

            if (em.getEdgeNodeList(String.valueOf(i)).contains(node1)) {
                if (em.getEdgeNodeList(String.valueOf(i)).contains(node2)) {
                    this.tmpNodeID = String.valueOf(i);
                }
            }
        }
        return this.tmpNodeID;
    }
}
