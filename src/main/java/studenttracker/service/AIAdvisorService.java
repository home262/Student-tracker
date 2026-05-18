package studenttracker.service;

import studenttracker.model.Attendance;
import studenttracker.model.Grade;
import studenttracker.model.Student;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AIAdvisorService {

    public CompletableFuture<String> getAdviceAsync(Student student, double gpa,
            List<Grade> grades, List<Attendance> attendanceList) {
        return CompletableFuture.supplyAsync(() -> buildAdvice(student, gpa, grades, attendanceList));
    }

    private String buildAdvice(Student student, double gpa,
            List<Grade> grades, List<Attendance> attendanceList) {

        String name = student.getName().split(" ")[0]; // first name

        // ── GPA analysis ─────────────────────────────────────────────
        String gpaStatus;
        String gpaAdvice;
        if (gpa >= 3.7) {
            gpaStatus = "Honor Roll";
            gpaAdvice = "Your Honor score of " + String.format("%.2f", gpa) +
                " places you among the Sanctum's most celebrated scholars. " +
                "The ancient tomes record few who shine this brightly. " +
                "Keep your momentum — the path to mastery is built on consistency.";
        } else if (gpa >= 3.0) {
            gpaStatus = "Good Standing";
            gpaAdvice = "Your Honor score of " + String.format("%.2f", gpa) +
                " reflects solid dedication to your craft. " +
                "You stand on firm ground within these halls. " +
                "A focused push in your weaker quests could elevate you to the Honor Roll.";
        } else if (gpa >= 2.0) {
            gpaStatus = "At Risk";
            gpaAdvice = "Your Honor score of " + String.format("%.2f", gpa) +
                " signals that your scholarly flame burns dimmer than it should. " +
                "The Sanctum urges you to seek your instructors' counsel and revisit core materials. " +
                "Each quest retaken with full effort can reclaim your standing.";
        } else {
            gpaStatus = "Critical — Action Required";
            gpaAdvice = "Your Honor score of " + String.format("%.2f", gpa) +
                " has drawn the concern of the Sanctum's wardens. " +
                "Urgent action is required — attend every session, seek tutoring, " +
                "and communicate with your instructors without delay. " +
                "The road back is steep, but scholars have walked it before.";
        }

        // ── Worst courses ─────────────────────────────────────────────
        String coursesAdvice = "";
        if (grades != null && !grades.isEmpty()) {
            List<Grade> sorted = grades.stream()
                .sorted(Comparator.comparingDouble(Grade::getScore))
                .collect(Collectors.toList());

            Grade worst = sorted.get(0);
            String worstName = worst.getCourse() != null ? worst.getCourse().getCourseName() : "an unnamed quest";
            String worstLetter = worst.calculateLetter();

            if (sorted.size() >= 2) {
                Grade second = sorted.get(1);
                String secondName = second.getCourse() != null ? second.getCourse().getCourseName() : "another quest";
                coursesAdvice = "The scrolls reveal that your most challenging quests are **" + worstName +
                    "** (Grade: " + worstLetter + ", Score: " + String.format("%.0f", worst.getScore()) + ") " +
                    "and **" + secondName + "** (Grade: " + second.calculateLetter() +
                    ", Score: " + String.format("%.0f", second.getScore()) + "). " +
                    "Dedicate extra hours of study to these disciplines — " +
                    "request a counsel session with their instructors and form study alliances with your peers.";
            } else {
                coursesAdvice = "The scrolls show your most challenging quest is **" + worstName +
                    "** with a grade of " + worstLetter + " (" + String.format("%.0f", worst.getScore()) + "). " +
                    "Seek guidance from its instructor and dedicate focused study sessions to mastering it.";
            }
        } else {
            coursesAdvice = "No battle scores have yet been recorded in the archives. " +
                "Ensure your instructors log your scores so the Oracle may offer wiser counsel next time.";
        }

        // ── Attendance analysis ───────────────────────────────────────
        String attendanceAdvice = "";
        if (attendanceList != null && !attendanceList.isEmpty()) {
            long absent = attendanceList.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Absent")).count();
            long total = attendanceList.size();
            double rate = (double)(total - absent) / total * 100;

            // Per-course absence map
            Map<String, Long> absencesByCourse = attendanceList.stream()
                .filter(a -> a.getStatus().equalsIgnoreCase("Absent"))
                .collect(Collectors.groupingBy(
                    a -> a.getCourse() != null ? a.getCourse().getCourseName() : "Unknown",
                    Collectors.counting()));

            List<Map.Entry<String, Long>> atRisk = absencesByCourse.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

            if (rate >= 90) {
                attendanceAdvice = "Your presence in the halls is exemplary at " +
                    String.format("%.0f%%", rate) + " — a mark of true dedication. " +
                    "Consistent attendance is one of the most powerful advantages a scholar can possess. Keep this standard.";
            } else if (rate >= 75) {
                attendanceAdvice = "Your hall presence stands at " + String.format("%.0f%%", rate) + ". " +
                    "While acceptable, there is room to improve. " +
                    (atRisk.isEmpty() ? "Guard against further absences in all your courses." :
                        "Pay particular attention to **" + atRisk.get(0).getKey() + "** where " +
                        atRisk.get(0).getValue() + " absences have been recorded.");
            } else {
                attendanceAdvice = "⚠ Dark Omen Alert: Your presence rate of " +
                    String.format("%.0f%%", rate) + " is below the Sanctum's required 75% threshold. " +
                    "This endangers your eligibility for final trials. " +
                    (atRisk.isEmpty() ? "Attend every session without exception going forward." :
                        "Your most urgent concern is **" + atRisk.get(0).getKey() +
                        "** with " + atRisk.get(0).getValue() + " absences — " +
                        "one more may bar you from its final examination.");
            }
        } else {
            attendanceAdvice = "No presence records have been entered yet. " +
                "Once attendance is logged, the Oracle can alert you to any dark omens on your path.";
        }

        // ── Build final response ──────────────────────────────────────
        return "⚔ Oracle's Counsel for Scholar " + name + " [Status: " + gpaStatus + "]\n\n" +
            "HONOR & STANDING\n" +
            gpaAdvice + "\n\n" +
            "QUEST PERFORMANCE\n" +
            coursesAdvice + "\n\n" +
            "HALL PRESENCE\n" +
            attendanceAdvice + "\n\n" +
            "— The Oracle has spoken. May your quests be victorious, Scholar " + name + ".";
    }
}
