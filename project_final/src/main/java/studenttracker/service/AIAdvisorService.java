package studenttracker.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import studenttracker.model.Student;
import studenttracker.model.Grade;
import studenttracker.model.Attendance;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AIAdvisorService {
    
    // Attempt to load from environment variable, otherwise leave empty for user to fill
    private static final String API_KEY = System.getenv("GEMINI_API_KEY") != null ? 
                                            System.getenv("GEMINI_API_KEY") : 
                                            "YOUR_API_KEY_HERE"; 
                                            
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private final HttpClient httpClient;
    private final Gson gson;

    public AIAdvisorService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    /**
     * Asynchronously gets advice from the AI Oracle based on student stats.
     */
    public CompletableFuture<String> getAdviceAsync(Student student, double gpa, List<Grade> grades, List<Attendance> attendanceList) {
        String prompt = buildPrompt(student, gpa, grades, attendanceList);
        String requestBody = buildRequestBody(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return parseResponse(response.body());
                    } else {
                        System.err.println("API Error: " + response.body());
                        return "The Oracle is clouded right now. Try again later. (Error " + response.statusCode() + ")";
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return "A magical interference occurred: " + ex.getMessage();
                });
    }

    private String buildPrompt(Student student, double gpa, List<Grade> grades, List<Attendance> attendanceList) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are 'The Oracle', an AI advisor in a fantasy-themed student tracker called Scholar's Sanctum. ");
        sb.append("Speak in a helpful, slightly mystical, fantasy tone. ");
        sb.append("Summarize the student's status and give them quest-like advice on what they should do next.\n\n");
        sb.append("Student Name: ").append(student.getName()).append("\n");
        sb.append("Honor (GPA): ").append(String.format("%.2f", gpa)).append("\n");
        
        sb.append("Recent Battle Scores (Grades):\n");
        if (grades != null && !grades.isEmpty()) {
            for (int i = 0; i < Math.min(grades.size(), 5); i++) {
                Grade g = grades.get(i);
                String cId = g.getCourse() != null ? g.getCourse().getCourseId() : "Unknown";
                sb.append("- Course ID ").append(cId).append(": ").append(g.getScore()).append("\n");
            }
        } else {
            sb.append("- No recent battles recorded.\n");
        }

        sb.append("Presence (Attendance issues):\n");
        long absences = attendanceList != null ? attendanceList.stream().filter(a -> a.getStatus().equalsIgnoreCase("Absent")).count() : 0;
        sb.append("- Total Absences: ").append(absences).append("\n");
        
        sb.append("\nPlease provide a 2-3 paragraph response summarizing their standing and giving actionable advice.");
        return sb.toString();
    }

    private String buildRequestBody(String prompt) {
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);

        JsonArray parts = new JsonArray();
        parts.add(part);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject body = new JsonObject();
        body.add("contents", contents);

        return gson.toJson(body);
    }

    private String parseResponse(String jsonResponse) {
        try {
            JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
            return responseObj.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "The Oracle spoke in riddles I could not understand.";
        }
    }
}
