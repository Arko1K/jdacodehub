package models.entities;


import java.util.List;
import java.util.Map;

public class SearchData {

    private List<Map> result;
    private long count;


    public SearchData(List<Map> result, long count) {
        this.result = result;
        this.count = count;
    }


    public List<Map> getResult() {
        return result;
    }

    public void setResult(List<Map> result) {
        this.result = result;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}