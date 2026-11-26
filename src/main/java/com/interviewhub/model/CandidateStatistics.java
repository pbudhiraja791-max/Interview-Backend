package com.interviewhub.model;

import java.util.Map;

/**
 * Candidate Performance Statistics Model
 * Calculates and formats comprehensive candidate performance metrics across interviews.
 * Handles division by zero, first score comparison, and skill-wise breakdown.
 */
public class CandidateStatistics {

    private int candidateId;
    private String candidateName;
    private int totalAttended;
    private int totalCompleted;
    private double averageScore;
    private double highestScore;
    private double lowestScore;
    private Double firstScore;
    private Double latestScore;
    private Double improvementPercentage;
    private String strongestSkill;
    private double strongestSkillScore;
    private String weakestSkill;
    private double weakestSkillScore;
    private Map<String, Double> skillAverages;

    public CandidateStatistics() {
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public int getTotalAttended() {
        return totalAttended;
    }

    public void setTotalAttended(int totalAttended) {
        this.totalAttended = totalAttended;
    }

    public int getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(int totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(double highestScore) {
        this.highestScore = highestScore;
    }

    public double getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(double lowestScore) {
        this.lowestScore = lowestScore;
    }

    public Double getFirstScore() {
        return firstScore;
    }

    public void setFirstScore(Double firstScore) {
        this.firstScore = firstScore;
    }

    public Double getLatestScore() {
        return latestScore;
    }

    public void setLatestScore(Double latestScore) {
        this.latestScore = latestScore;
    }

    public Double getImprovementPercentage() {
        return improvementPercentage;
    }

    public void setImprovementPercentage(Double improvementPercentage) {
        this.improvementPercentage = improvementPercentage;
    }

    public String getStrongestSkill() {
        return strongestSkill;
    }

    public void setStrongestSkill(String strongestSkill) {
        this.strongestSkill = strongestSkill;
    }

    public double getStrongestSkillScore() {
        return strongestSkillScore;
    }

    public void setStrongestSkillScore(double strongestSkillScore) {
        this.strongestSkillScore = strongestSkillScore;
    }

    public String getWeakestSkill() {
        return weakestSkill;
    }

    public void setWeakestSkill(String weakestSkill) {
        this.weakestSkill = weakestSkill;
    }

    public double getWeakestSkillScore() {
        return weakestSkillScore;
    }

    public void setWeakestSkillScore(double weakestSkillScore) {
        this.weakestSkillScore = weakestSkillScore;
    }

    public Map<String, Double> getSkillAverages() {
        return skillAverages;
    }

    public void setSkillAverages(Map<String, Double> skillAverages) {
        this.skillAverages = skillAverages;
    }

    /**
     * Formats performance statistics using StringBuilder.
     */
    public String toFormattedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================\n");
        sb.append(String.format("  COMPREHENSIVE PERFORMANCE REPORT\n"));
        sb.append(String.format("  Candidate : %s  (ID: %d)\n",
                candidateName != null ? candidateName : "Candidate", candidateId));
        sb.append("==============================================================\n");
        sb.append(String.format("Total Interviews Attended  : %d\n", totalAttended));
        sb.append(String.format("Total Completed Interviews : %d\n", totalCompleted));

        if (totalCompleted == 0) {
            sb.append("--------------------------------------------------------------\n");
            sb.append("No completed interview evaluations recorded yet.\n");
            sb.append("==============================================================\n");
            return sb.toString();
        }

        sb.append(String.format("Average Overall Score      : %.2f / 100.00\n", averageScore));
        sb.append(String.format("Highest Score Achieved     : %.2f / 100.00\n", highestScore));
        sb.append(String.format("Lowest Score Achieved      : %.2f / 100.00\n", lowestScore));

        // Performance Grade
        sb.append(String.format("Performance Grade          : %s\n", getPerformanceGrade(averageScore)));
        sb.append(String.format("Readiness Level            : %s\n", getReadinessLevel(averageScore)));

        if (firstScore != null && latestScore != null) {
            sb.append(String.format("First Interview Score      : %.2f / 100.00\n", firstScore));
            sb.append(String.format("Latest Interview Score     : %.2f / 100.00\n", latestScore));
            if (improvementPercentage != null) {
                String prefix = improvementPercentage >= 0 ? "+" : "";
                String trend = improvementPercentage > 5 ? " [IMPROVING ▲]"
                        : improvementPercentage < -5 ? " [DECLINING ▼]" : " [STABLE ►]";
                sb.append(String.format("Score Trend                : %s%.2f%%%s\n",
                        prefix, improvementPercentage, trend));
            } else if (firstScore == 0.0) {
                sb.append("Score Trend                : Baseline initialized (first score was 0.00)\n");
            }
        }

        // Visual score bar
        sb.append("--------------------------------------------------------------\n");
        sb.append(String.format("Score Progress Bar         : %s  %.0f%%\n",
                buildScoreBar(averageScore), averageScore));

        sb.append("--------------------------------------------------------------\n");
        sb.append("SKILL ANALYSIS:\n");
        if (strongestSkill != null) {
            sb.append(String.format("  * Strongest Competency   : %-25s (Avg: %.2f / 100.00)\n",
                    strongestSkill, strongestSkillScore));
        }
        if (weakestSkill != null) {
            sb.append(String.format("  * Areas for Growth       : %-25s (Avg: %.2f / 100.00)\n",
                    weakestSkill, weakestSkillScore));
        }

        if (skillAverages != null && !skillAverages.isEmpty()) {
            sb.append("--------------------------------------------------------------\n");
            sb.append("Skill-wise Average Breakdown:\n");
            for (Map.Entry<String, Double> entry : skillAverages.entrySet()) {
                sb.append(String.format("  - %-25s : %5.2f / 100  %s\n",
                        entry.getKey(), entry.getValue(),
                        buildScoreBar(entry.getValue())));
            }
        }

        sb.append("--------------------------------------------------------------\n");
        sb.append(getPlacementRecommendation(averageScore));
        sb.append("==============================================================\n");
        return sb.toString();
    }

    private String getPerformanceGrade(double score) {
        if (score >= 90) return "A+  (Outstanding)";
        if (score >= 80) return "A   (Excellent)";
        if (score >= 70) return "B+  (Very Good)";
        if (score >= 60) return "B   (Good)";
        if (score >= 50) return "C   (Average)";
        if (score >= 40) return "D   (Below Average)";
        return                  "F   (Needs Significant Improvement)";
    }

    private String getReadinessLevel(double score) {
        if (score >= 85) return "PLACEMENT READY          - Highly competitive for top-tier companies";
        if (score >= 70) return "INTERVIEW READY          - Suitable for mid-level company interviews";
        if (score >= 55) return "DEVELOPING               - Continue practice, focus on weak areas";
        return                  "NEEDS PREPARATION        - Recommend intensive preparation before applying";
    }

    private String buildScoreBar(double score) {
        int filled = (int) Math.round(score / 10);
        filled = Math.min(filled, 10);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? "##" : "--");
        }
        bar.append("]");
        return bar.toString();
    }

    private String getPlacementRecommendation(double score) {
        StringBuilder rec = new StringBuilder("PLACEMENT RECOMMENDATION:\n");
        if (score >= 85) {
            rec.append("  Eligible for top-tier companies: Google, Microsoft, Amazon.\n");
            rec.append("  Focus: System Design and leadership scenarios in next rounds.\n");
        } else if (score >= 70) {
            rec.append("  Eligible for mid-tier companies: TCS Digital, Infosys, Wipro.\n");
            rec.append("  Focus: Strengthen DSA and project-based problem solving.\n");
        } else if (score >= 55) {
            rec.append("  Consider service-based companies and startups.\n");
            rec.append("  Focus: Core Java, SQL, and behavioral interview prep.\n");
        } else {
            rec.append("  Recommend 4-6 weeks of intensive preparation.\n");
            rec.append("  Focus: Fundamentals, mock interviews, and aptitude tests.\n");
        }
        return rec.toString();
    }
}
