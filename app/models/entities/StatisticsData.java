package models.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class StatisticsData {

    @JsonProperty("top-5-languages-used")
    private Map<String, Long> language;
    @JsonProperty("top-2-submissions-attempted")
    private Map<String, Long> title;
    @JsonProperty("submissions-per-level")
    private Map<String, Long> level;
    @JsonProperty("total-submissions")
    private long total;


    public Map<String, Long> getLanguage() {
        return language;
    }

    public void setLanguage(Map<String, Long> language) {
        this.language = language;
    }

    public Map<String, Long> getTitle() {
        return title;
    }

    public void setTitle(Map<String, Long> title) {
        this.title = title;
    }

    public Map<String, Long> getLevel() {
        return level;
    }

    public void setLevel(Map<String, Long> level) {
        this.level = level;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}