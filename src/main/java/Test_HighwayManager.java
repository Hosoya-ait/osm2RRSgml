import java.util.ArrayList;

public class Test_HighwayManager {
    public static void main(String[] args) {
        //highway作成
        ArrayList<String> test_highway = new ArrayList<String>();
        test_highway.add("1");
        test_highway.add("2");
        test_highway.add("3");

        HighwayManager hm = new HighwayManager();

        //highwayをセット
        hm.setTmpHighwayList(test_highway);

        //highwayを呼び出す
        ArrayList<ArrayList<String>> test_get_highway = new ArrayList<ArrayList<String>>();
        test_get_highway = hm.getTmpHighwayList();
        System.out.println(test_get_highway.get(0).get(0));
        System.out.println(test_get_highway.get(0).get(1));
        System.out.println(test_get_highway.get(0).get(2));

    }
}
