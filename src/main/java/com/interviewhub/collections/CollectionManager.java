package com.interviewhub.collections;

import com.interviewhub.model.Candidate;
import com.interviewhub.model.Interview;
import com.interviewhub.model.Interviewer;
import com.interviewhub.model.User;

import java.util.*;
import java.util.stream.Collectors;


public class CollectionManager {

    // 1. ArrayLists for storing temporary working datasets
    private final List<Candidate> candidateList = new ArrayList<>();
    private final List<Interviewer> interviewerList = new ArrayList<>();
    private final List<Interview> interviewList = new ArrayList<>();

    // 2. HashMaps for O(1) fast lookup caches
    private final Map<Integer, User> userCache = new HashMap<>();
    private final Map<Integer, Candidate> candidateCache = new HashMap<>();
    private final Map<Integer, Interview> interviewCache = new HashMap<>();

    // 3. HashSets for instant duplicate detection
    private final Set<String> registeredEmailSet = new HashSet<>();
    private final Set<String> studentIdSet = new HashSet<>();

    public CollectionManager() {
    }

    /**
     * Initializes the collection cache with fresh data fetched from MySQL.
     */
    public synchronized void syncData(List<Candidate> candidates, List<Interviewer> interviewers, List<Interview> interviews) {
        clearAll();

        if (candidates != null) {
            for (Candidate c : candidates) {
                addCandidate(c);
            }
        }

        if (interviewers != null) {
            for (Interviewer i : interviewers) {
                addInterviewer(i);
            }
        }

        if (interviews != null) {
            for (Interview inv : interviews) {
                addInterview(inv);
            }
        }
    }

    public synchronized void clearAll() {
        candidateList.clear();
        interviewerList.clear();
        interviewList.clear();
        userCache.clear();
        candidateCache.clear();
        interviewCache.clear();
        registeredEmailSet.clear();
        studentIdSet.clear();
    }

    // ==================== CANDIDATE MANAGEMENT (ArrayList, HashMap, HashSet) ====================

    /**
     * Adds candidate to working ArrayList, HashMap cache, and validates uniqueness via HashSet.
     */
    public synchronized boolean addCandidate(Candidate candidate) {
        if (candidate == null) return false;

        String email = candidate.getEmail().toLowerCase();
        if (registeredEmailSet.contains(email)) {
            return false; // Duplicate email detected by HashSet
        }

        registeredEmailSet.add(email);
        studentIdSet.add(candidate.getStudentId());
        candidateList.add(candidate);
        candidateCache.put(candidate.getCandidateId(), candidate);
        userCache.put(candidate.getUserId(), candidate);
        return true;
    }

    /**
     * Demonstrates Iterator removal from ArrayList and cache synchronization.
     */
    public synchronized boolean removeCandidateById(int candidateId) {
        Iterator<Candidate> iterator = candidateList.iterator();
        while (iterator.hasNext()) {
            Candidate c = iterator.next();
            if (c.getCandidateId() == candidateId) {
                iterator.remove(); // Safe removal using Iterator
                candidateCache.remove(candidateId);
                userCache.remove(c.getUserId());
                registeredEmailSet.remove(c.getEmail().toLowerCase());
                studentIdSet.remove(c.getStudentId());
                return true;
            }
        }
        return false;
    }

    /**
     * Demonstrates O(1) HashMap search.
     */
    public Candidate findCandidateById(int candidateId) {
        return candidateCache.get(candidateId);
    }

    /**
     * Demonstrates HashSet duplicate check.
     */
    public boolean isEmailRegistered(String email) {
        if (email == null) return false;
        return registeredEmailSet.contains(email.trim().toLowerCase());
    }

    // ==================== INTERVIEWER MANAGEMENT ====================

    public synchronized void addInterviewer(Interviewer interviewer) {
        if (interviewer != null) {
            interviewerList.add(interviewer);
            userCache.put(interviewer.getUserId(), interviewer);
            registeredEmailSet.add(interviewer.getEmail().toLowerCase());
        }
    }

    // ==================== INTERVIEW MANAGEMENT ====================

    public synchronized void addInterview(Interview interview) {
        if (interview != null) {
            interviewList.add(interview);
            interviewCache.put(interview.getInterviewId(), interview);
        }
    }

    public Interview findInterviewById(int interviewId) {
        return interviewCache.get(interviewId);
    }

    // ==================== COMPARATOR SORTING DEMONSTRATIONS ====================

    /**
     * Sorts candidates by CGPA in descending order (highest first).
     */
    public List<Candidate> sortCandidatesByCgpaDescending() {
        List<Candidate> sorted = new ArrayList<>(candidateList);
        sorted.sort(new Comparator<Candidate>() {
            @Override
            public int compare(Candidate c1, Candidate c2) {
                return Double.compare(c2.getCgpa(), c1.getCgpa());
            }
        });
        return sorted;
    }

    /**
     * Sorts candidates by Name in alphabetical order.
     */
    public List<Candidate> sortCandidatesByName() {
        List<Candidate> sorted = new ArrayList<>(candidateList);
        sorted.sort(Comparator.comparing(Candidate::getName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    /**
     * Sorts interviews chronologically by interview date and time.
     */
    public List<Interview> sortInterviewsByDate() {
        List<Interview> sorted = new ArrayList<>(interviewList);
        sorted.sort(Comparator.comparing(Interview::getInterviewDate)
                .thenComparing(Interview::getInterviewTime));
        return sorted;
    }

    /**
     * Filters candidates by department using enhanced for-loop.
     */
    public List<Candidate> filterCandidatesByDepartment(String department) {
        List<Candidate> filtered = new ArrayList<>();
        if (department == null) return filtered;

        for (Candidate c : candidateList) { // Demonstrating enhanced for-loop
            if (c.getDepartment().equalsIgnoreCase(department.trim())) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    /**
     * Filters candidates by specialization.
     */
    public List<Candidate> filterCandidatesBySpecialization(String specialization) {
        List<Candidate> filtered = new ArrayList<>();
        if (specialization == null) return filtered;

        for (Candidate c : candidateList) {
            if (c.getSpecialization().toLowerCase().contains(specialization.trim().toLowerCase())) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    /**
     * Milestone 2 Feature: Top Five Candidates by Average Interview Score.
     * Combines Interview records with candidate lookups and sorts descending.
     */
    public List<Map.Entry<Candidate, Double>> getTopFiveCandidatesByScore() {
        // Map: candidateId -> List of completed interview scores
        Map<Integer, List<Double>> scoresMap = new HashMap<>();
        for (Interview inv : interviewList) {
            if ("COMPLETED".equalsIgnoreCase(inv.getInterviewStatus()) && inv.getOverallScore() != null) {
                scoresMap.computeIfAbsent(inv.getCandidateId(), k -> new ArrayList<>()).add(inv.getOverallScore());
            }
        }

        // Calculate averages for each candidate
        List<Map.Entry<Candidate, Double>> candidateAverages = new ArrayList<>();
        for (Map.Entry<Integer, List<Double>> entry : scoresMap.entrySet()) {
            Candidate c = candidateCache.get(entry.getKey());
            if (c != null) {
                double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                candidateAverages.add(new AbstractMap.SimpleEntry<>(c, avg));
            }
        }

        // Sort descending by average score using Comparator
        candidateAverages.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // Return up to top 5
        return candidateAverages.stream().limit(5).collect(Collectors.toList());
    }

    public List<Candidate> getCandidateList() {
        return Collections.unmodifiableList(candidateList);
    }

    public List<Interviewer> getInterviewerList() {
        return Collections.unmodifiableList(interviewerList);
    }

    public List<Interview> getInterviewList() {
        return Collections.unmodifiableList(interviewList);
    }
}
