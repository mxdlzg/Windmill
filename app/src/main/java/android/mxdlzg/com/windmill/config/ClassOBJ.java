package android.mxdlzg.com.windmill.config;

/**
 * Created by 廷江 on 2017/3/12.
 */
public class ClassOBJ {
    private int num;
    private String name;
    private String position;
    private String techer;
    private int start,end;
    private int day,index;
    private float score;
    private boolean multiplePos = false;
    private String[] weeks;

    public ClassOBJ(){
        this.num = -1;
    }

    public ClassOBJ(int num, String name,String techer,int day,int index,float score, boolean multiplePos) {
        this.num = num;
        this.name = name;
        this.techer = techer;
        this.day = day;
        this.index = index;
        this.score = score;
        this.multiplePos = multiplePos;
    }


    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setTecher(String techer) {
        this.techer = techer;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setMultiplePos(boolean multiplePos) {
        this.multiplePos = multiplePos;
    }

    public String getWeek(int index){
        return weeks[index];
    }

    public void setWeek(int index,String value){
        this.weeks[index] = value;
    }

    public void setWeeks(String[] weeks) {
        this.weeks = weeks;
        this.position = "";
    }

    public String[] getWeeks() {
        return weeks;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getTecher() {
        return techer;
    }

    public boolean isMultiplePos() {
        return multiplePos;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getNum() {
        return num;
    }

    public float getScore() {
        return score;
    }

    public String getALL(){
        String result = "";
        result += (num+";");
        result += (name+";");
        result += (techer+";");
        result += (day+";");
        result += (index+";");
        result += (score+"");
        return result;
    }

    public String getWeekString(){
        String result = "";
        for (int i = 0;i<20;i++){
            if (weeks[i] != null){
                result+=weeks[i];
                result+=",";
            }else {
                result+=",";
            }
        }
        return result;
    }
}
