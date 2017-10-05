import java.util.HashMap;
import java.util.ArrayList;

public class Test {

	public Test() {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("Hello World!");
		HashMap<String, ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
    map = Converter.buildingMap;

		map.forEach((id,edges)->{
			System.out.println(id);
			//edges.forEach(idedge -> {
				//System.out.println(idedge);
			//});
		});
	}

}	
