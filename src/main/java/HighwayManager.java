import java.util.ArrayList;

public class HighwayManager {
    private ArrayList<ArrayList<String>> tmpHighwayList = new ArrayList<ArrayList<String>>();

    public ArrayList<ArrayList<String>> getTmpHighwayList() {
        return tmpHighwayList;
    }

    public void setTmpHighwayList(ArrayList<String> highwayNodes) {
        tmpHighwayList.add(highwayNodes);
    }
}
