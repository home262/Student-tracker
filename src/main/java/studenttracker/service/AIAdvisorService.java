package studenttracker.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import studenttracker.model.Attendance;
import studenttracker.model.Grade;
import studenttracker.model.Student;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ SCHOLAR'S SANCTUM — ORACLE AI · Powered by Groq (FREE) ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ 1. Go to https://console.groq.com ║
 * ║ 2. Create a free API key (no credit card) ║
 * ║ 3. Paste it in GROQ_API_KEY below and rebuild ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
public class AIAdvisorService {

    // ── ⚙ PASTE YOUR FREE GROQ KEY HERE ─────────────────────────────────────
    private static final String GROQ_API_KEY = "YOUR_GROQ_API_KEY_HERE";
    // ─────────────────────────────────────────────────────────────────────────

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_MODEL = "llama-3.3-70b-versatile";

    // Each entry: {"role":"user"|"assistant", "content":"..."}
    private final List<String[]> history = new ArrayList<>();

    /** Clears conversation history (called on Clear Chat). */
    public void clearHistory() {
        history.clear();
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Sends a user message in the context of an ongoing conversation.
     * The full student academic profile is embedded in the system prompt so
     * Groq knows the student's data at every turn.
     *
     * @param student      The logged-in student.
     * @param gpa          Pre-calculated GPA.
     * @param userMessage  The raw text the student typed or selected.
     * @param fullAnalysis If true, the prompt explicitly asks for a full review.
     */
    public CompletableFuture<String> sendMessageAsync(Student student, double gpa,
            String userMessage, boolean fullAnalysis) {

        return CompletableFuture.supplyAsync(() -> {
            if (GROQ_API_KEY.equals("YOUR_GROQ_API_KEY_HERE")) {
                return buildFallbackAdvice(student, gpa) +
                        "\n\n─────────────────────────────────────" +
                        "\n⚙  TO ENABLE REAL AI:" +
                        "\n1. Go to https://console.groq.com" +
                        "\n2. Create a free API key (no credit card)" +
                        "\n3. Paste it in AIAdvisorService.java → GROQ_API_KEY" +
                        "\n4. Rebuild the project";
            }
            try {
                // Add user turn to history
                history.add(new String[] { "user", userMessage });

                String systemPrompt = buildSystemPrompt(student, gpa, fullAnalysis);
                String reply = callGroq(systemPrompt);

                // Add assistant turn to history
                history.add(new String[] { "assistant", reply });
                return reply;
            } catch (Exception ex) {
                history.remove(history.size() - 1); // remove the failed user turn
                return buildFallbackAdvice(student, gpa)
                        + "\n\n[Oracle connection failed: " + ex.getMessage() + "]";
            }
        });
    }

    // ── Legacy API kept for compatibility ─────────────────────────────────────

    public CompletableFuture<String> getAdviceAsync(Student student, double gpa,
            List<Grade> grades, List<Attendance> attendanceList) {
        return sendMessageAsync(student, gpa,
                "Give me a full analysis of my academic standing.", true);
    }

    // ── System prompt (includes full student data) ─────────────────────────────

    private String buildSystemPrompt(Student student, double gpa, boolean fullAnalysis) {
        String firstName = student.getName().split(" ")[0];
        StringBuilder profile = new StringBuilder();
        profile.append("Student name: ").append(student.getName()).append("\n");
        profile.append("Department: ").append(student.getDepartment()).append("\n");
        profile.append("GPA: ").append(String.format("%.2f / 4.00", gpa)).append("\n\n");

        List<Grade> grades = student.getGrades();
        if (grades != null && !grades.isEmpty()) {
            profile.append("Grades:\n");
            for (Grade g : grades) {
                String cn = g.getCourse() != null ? g.getCourse().getCourseName() : "Unknown";
                profile.append("  - ").append(cn).append(": ")
                        .append(g.calculateLetter())
                        .append(" (").append(String.format("%.0f", g.getScore())).append("%)\n");
            }
        } else {
            profile.append("Grades: none recorded yet.\n");
        }

        List<Attendance> att = student.getAttendanceRecords();
        if (att != null && !att.isEmpty()) {
            long absent = att.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
            double rate = (double) (att.size() - absent) / att.size() * 100;
            profile.append("\nAttendance rate: ").append(String.format("%.1f%%", rate)).append("\n");
            Map<String, Long> absMap = att.stream()
                    .filter(a -> "Absent".equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.groupingBy(
                            a -> a.getCourse() != null ? a.getCourse().getCourseName() : "Unknown",
                            Collectors.counting()));
            absMap.forEach((c, n) -> profile.append("  - ").append(c).append(": ").append(n).append(" absence(s)\n"));
        } else {
            profile.append("\nAttendance: no records yet.\n");
        }

        String base = "You are the Oracle of Scholar's Sanctum — a wise, mystical academic advisor "
                + "who speaks in a fantasy RPG style (like a sage wizard), but gives genuinely useful, "
                + "specific academic advice. Use words like scholar, quests, battles, honor, dark omens, "
                + "the Sanctum naturally throughout your responses. "
                + "Be warm, direct, and vary your responses — never repeat the same phrasing twice. "
                + "When asked the same type of question, approach it from a different angle each time. "
                + "Use **bold** markers around key terms and course names. Keep responses under 350 words.\n\n"
                + "Here is the scholar's full academic profile:\n" + profile;

        if (fullAnalysis) {
            base += "\nThe scholar has requested a FULL ANALYSIS. Cover: GPA status, "
                    + "best and worst courses, attendance, and 3-5 prioritised action items.";
        }

        return base;
    }

    // ── Groq HTTP call with conversation history ───────────────────────────────

    private String callGroq(String systemPrompt) throws Exception {
        URL url = new URL(GROQ_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + GROQ_API_KEY);
        conn.setDoOutput(true);
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(60_000);

        // Build messages array: system + full history
        StringBuilder messages = new StringBuilder();
        messages.append("[");
        messages.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"}");
        for (String[] turn : history) {
            messages.append(",{\"role\":\"").append(turn[0]).append("\",\"content\":\"")
                    .append(escapeJson(turn[1])).append("\"}");
        }
        messages.append("]");

        String body = "{\"model\":\"" + GROQ_MODEL + "\","
                + "\"messages\":" + messages + ","
                + "\"max_tokens\":900,"
                + "\"temperature\":0.82}"; // slightly higher temp = more varied responses

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();
        String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        if (status != 200) {
            String hint = extractJsonString(response, "message");
            throw new IOException("Groq " + status + ": "
                    + (hint.isEmpty() ? response.substring(0, Math.min(300, response.length())) : hint));
        }

        String content = extractJsonString(response, "content");
        if (content.isEmpty())
            throw new IOException("Empty response from Groq.");
        return content;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private String extractJsonString(String json, String key) {
        String marker = "\"" + key + "\":";
        int ki = json.indexOf(marker);
        if (ki == -1)
            return "";
        int start = json.indexOf('"', ki + marker.length());
        if (start == -1)
            return "";
        start++;
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String buildFallbackAdvice(Student student, double gpa) {
        String name = student.getName().split(" ")[0];
        String gpaLine;
        if (gpa >= 3.7)
            gpaLine = "Your Honor score of " + String.format("%.2f", gpa) + " marks you as an elite scholar.";
        else if (gpa >= 3.0)
            gpaLine = "Your Honor score of " + String.format("%.2f", gpa) + " reflects solid standing.";
        else if (gpa >= 2.0)
            gpaLine = "Your Honor score of " + String.format("%.2f", gpa) + " signals risk — action required.";
        else
            gpaLine = "Your Honor score of " + String.format("%.2f", gpa) + " is critical. Seek help now.";

        return "⚔ Oracle's Counsel for Scholar " + name + "\n\n"
                + "HONOR & STANDING\n" + gpaLine + "\n\n"
                + "— The Oracle has spoken. May your quests be victorious, Scholar " + name + ".";
    }
}
