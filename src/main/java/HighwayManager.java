import java.util.ArrayList;

public class HighwayManager {
    private ArrayList<ArrayList<String>> tmpHighwayList = new ArrayList<ArrayList<String>>();

    public ArrayList getTmpHighwayList() {
        return tmpHighwayList;
    }

    public void setTmpHighwayList(ArrayList highwayNodes) {
        tmpHighwayList.add(highwayNodes);
    }
}
