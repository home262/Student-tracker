package studenttracker.ai;

import studenttracker.model.Student;

public class AIReportGenerator {

    //---------------------------------------FIELDS------------------------------------------
    private String apiKey;
    private String apiUrl;
    private String model;
    private int maxTokens;

    //---------------------------CONSTRUCTORS TO INITIALIZE DATA-----------------------------
    public AIReportGenerator() {
    }

    public AIReportGenerator(String apiKey, String apiUrl,
                             String model, int maxTokens) {

        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
        this.maxTokens = maxTokens;
    }

    //-----------------------------------SETTERS----------------------------------------------
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    //-----------------------------------GETTERS----------------------------------------------
    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getModel() {
        return model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    //-----------------------------------METHODS----------------------------------------------
    public String generateInsight(Student student) {
        return "";
    }

    public String generateWarning(Student student) {
        return "";
    }

    public String predictPerformance(Student student) {
        return "";
    }

    public String callAPI(String prompt) {
        return "";
    }
}