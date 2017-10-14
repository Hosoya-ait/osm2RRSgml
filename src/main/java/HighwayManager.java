import java.util.ArrayList;

public class HighwayManager {
    private static ArrayList<ArrayList<String>> tmpHighwayList = new ArrayList<ArrayList<String>>();

    public static ArrayList getTmpHighwayList() {
        return tmpHighwayList;
    }

    public static void setTmpHighwayList(ArrayList highwayNodes) {
        tmpHighwayList.add(highwayNodes);
    }
}
