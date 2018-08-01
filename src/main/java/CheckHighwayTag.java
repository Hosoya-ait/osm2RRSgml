import java.util.ArrayList;
class CheckHighwayTag {
    private static ArrayList<String> exclude_List_ = new ArrayList<String>();
    private static ArrayList<String> check_List_ = new ArrayList<String>();
    public CheckHighwayTag(){
        this.exclude_List_.add("footway");
        this.exclude_List_.add("motorway");
        this.exclude_List_.add("motorway_link");
        this.exclude_List_.add("area");
        this.exclude_List_.add("steps");

    }
    public void setCheckList(String str){
        this.check_List_.add(str);
    }
    public Boolean check(){
        String str = new String();
        for (int i=0; i<this.check_List_.size();i++ ) {
            str = this.check_List_.get(i);
            //System.out.println("tag:"+str);
            if (this.exclude_List_.contains(str)) {
                return false;
            }
        }
        return true;
    }
    public void clearCheckList(){
        this.check_List_ = new ArrayList<String>();
    }
}
