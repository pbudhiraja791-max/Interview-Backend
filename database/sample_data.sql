-- ====================================================================
-- InterviewHub: Sample Data Seed Script
-- Populates Admin, Candidates, Interviewers, Questions, Companies, Drives,
-- Applications, Interviews, Feedback, and Skill Scores.
-- ====================================================================

USE interviewhub_db;

-- 1. Insert Users (Admin, Interviewers, Candidates)
-- Admin Password: Admin@123
INSERT INTO users (user_id, name, email, password_hash, password_salt, role, status) VALUES
(1, 'System Administrator', 'admin@interviewhub.com', '+5wtv982JTwtU0bD+IX/lXgiETiKQ3FiknO2wUqkt9M=', 'fNwhqhQT4+c4IWyhomcS3w==', 'ADMIN', 'ACTIVE');

-- Admin Account: Parth Budhiraja | Email: pbudhiraja791@gmail.com | Password: 123456
INSERT INTO users (user_id, name, email, password_hash, password_salt, role, status) VALUES
(10, 'Parth Budhiraja', 'pbudhiraja791@gmail.com', 'CqRw4lnjBtc8KZbw0HaMvgrJid3qig6YAur64NbCn+w=', 'mH3kPqL1xZ+9vW2nYsT8dA==', 'ADMIN', 'ACTIVE');

-- Interviewers Password: Password@123
INSERT INTO users (user_id, name, email, password_hash, password_salt, role, status) VALUES
(2, 'Dr. Sarah Jenkins', 'sarah.swe@techcorp.com', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'INTERVIEWER', 'ACTIVE'),
(3, 'David Miller', 'david.hr@globalhire.com', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'INTERVIEWER', 'ACTIVE'),
(4, 'Priya Nair', 'priya.data@innovate.com', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'INTERVIEWER', 'ACTIVE');

-- Candidates Password: Password@123
INSERT INTO users (user_id, name, email, password_hash, password_salt, role, status) VALUES
(5, 'Rahul Sharma', 'rahul.sharma@college.edu', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'CANDIDATE', 'ACTIVE'),
(6, 'Ananya Patel', 'ananya.patel@college.edu', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'CANDIDATE', 'ACTIVE'),
(7, 'Vikram Singh', 'vikram.singh@college.edu', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'CANDIDATE', 'ACTIVE'),
(8, 'Neha Gupta', 'neha.gupta@college.edu', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'CANDIDATE', 'ACTIVE'),
(9, 'Arjun Reddy', 'arjun.reddy@college.edu', 'DmT998AqIr0NmwNwn3GLOTvqCp9q5vH6anrJUZoQ55Y=', 'ko5IguSKFvRHpLdtYIKUQQ==', 'CANDIDATE', 'ACTIVE');

-- 2. Insert Candidates
INSERT INTO candidates (candidate_id, user_id, student_id, department, cgpa, graduation_year, specialization) VALUES
(1, 5, 'STU202401', 'Computer Science', 8.85, 2025, 'Software Engineering'),
(2, 6, 'STU202402', 'Information Technology', 9.10, 2025, 'Cloud Computing'),
(3, 7, 'STU202403', 'Computer Science', 7.45, 2025, 'Full Stack Development'),
(4, 8, 'STU202404', 'Electronics and Communication', 8.20, 2025, 'Embedded Systems'),
(5, 9, 'STU202405', 'Computer Science', 6.80, 2025, 'Cyber Security');

-- 3. Insert Interviewers
INSERT INTO interviewers (interviewer_id, user_id, expertise, experience_years, specialization, organization) VALUES
(1, 2, 'Core Java, Microservices, Distributed Systems, Algorithms', 10, 'Software Engineering', 'TechCorp Global'),
(2, 3, 'Behavioral Assessment, Leadership, Culture Fit, Communication', 8, 'Human Resources', 'GlobalHire Talent'),
(3, 4, 'Relational Databases, Data Structures, System Design', 6, 'Backend Systems', 'InnovateX Labs');

-- 4. Insert Companies
INSERT INTO companies (company_id, company_name, industry, location) VALUES
(1, 'Google', 'Information Technology', 'Bangalore, India'),
(2, 'Microsoft', 'Enterprise Software', 'Hyderabad, India'),
(3, 'Amazon', 'E-Commerce & Cloud', 'Bangalore, India'),
(4, 'TCS Digital', 'Consulting & Services', 'Delhi NCR, India');

-- 5. Insert Placement Drives
INSERT INTO placement_drives (drive_id, company_id, drive_name, eligibility_cgpa, eligible_department, application_deadline, status) VALUES
(1, 1, 'Google Summer SWE Drive 2025', 8.50, 'Computer Science', '2025-06-30', 'OPEN'),
(2, 2, 'Microsoft Software Engineer Graduate Program', 8.00, 'Computer Science', '2025-05-31', 'OPEN'),
(3, 3, 'Amazon Cloud Support Associate Drive', 7.50, 'Information Technology', '2025-07-15', 'OPEN'),
(4, 4, 'TCS Digital National Qualifier Test (NQT)', 6.50, 'All Engineering', '2025-08-01', 'OPEN');

-- 6. Insert Applications
INSERT INTO applications (application_id, candidate_id, drive_id, application_date, application_status) VALUES
(1, 1, 1, '2025-01-10 10:00:00', 'SHORTLISTED'),
(2, 1, 2, '2025-01-12 11:30:00', 'APPLIED'),
(3, 2, 1, '2025-01-15 09:15:00', 'SHORTLISTED'),
(4, 3, 4, '2025-01-18 14:00:00', 'APPLIED'),
(5, 2, 3, '2025-01-20 10:30:00', 'SELECTED'),
(6, 4, 4, '2025-01-22 09:00:00', 'SHORTLISTED'),
(7, 5, 4, '2025-01-25 11:00:00', 'APPLIED');

-- 7. Insert Questions into Question Bank
INSERT INTO questions (question_id, question_text, category, difficulty, specialization, created_by) VALUES
(1, 'Explain the internal working of HashMap in Java and how collisions are resolved.', 'TECHNICAL', 'MEDIUM', 'Java Core', 1),
(2, 'What is the difference between synchronized block and synchronized method in Java?', 'TECHNICAL', 'MEDIUM', 'Multithreading', 1),
(3, 'Describe the ACID properties in relational databases and how transactions guarantee isolation.', 'TECHNICAL', 'MEDIUM', 'Databases', 1),
(4, 'Explain Producer-Consumer problem and how wait() and notify() are used safely with a condition loop.', 'TECHNICAL', 'HARD', 'Concurrency', 1),
(5, 'Describe a situation where you had a conflict with a team member and how you resolved it.', 'HR', 'EASY', 'Behavioral', 1),
(6, 'Where do you see yourself in five years and how does this role align with your aspirations?', 'HR', 'EASY', 'Career Vision', 1),
(7, 'Tell me about a challenging technical bug you encountered and the methodical steps you took to diagnose and fix it.', 'HR', 'MEDIUM', 'Problem Solving', 1);

-- 8. Insert Interview Requests
INSERT INTO interview_requests (request_id, candidate_id, interviewer_id, interview_type, preferred_date, preferred_time, request_status) VALUES
(1, 1, 1, 'TECHNICAL', '2025-01-05', '10:00:00', 'ACCEPTED'),
(2, 1, 1, 'TECHNICAL', '2025-01-20', '11:00:00', 'ACCEPTED'),
(3, 1, 2, 'HR', '2025-02-01', '14:00:00', 'ACCEPTED'),
(4, 1, 1, 'TECHNICAL', '2025-02-15', '10:30:00', 'ACCEPTED'),
(5, 2, 1, 'TECHNICAL', '2025-02-18', '15:00:00', 'ACCEPTED'),
(6, 3, 3, 'TECHNICAL', '2025-02-25', '16:00:00', 'ACCEPTED'),
(7, 3, 2, 'HR', '2025-03-05', '11:00:00', 'ACCEPTED'),
(8, 4, 1, 'TECHNICAL', '2025-03-10', '10:00:00', 'ACCEPTED'),
(9, 5, 2, 'HR', '2025-03-15', '14:00:00', 'ACCEPTED'),
(10, 2, 3, 'TECHNICAL', '2025-03-20', '15:30:00', 'PENDING');

-- 9. Insert Interviews
-- Rahul Sharma (candidate 1): 4 interviews showing clear progression 55 -> 68 -> 78 -> 90
-- Ananya Patel (candidate 2): 2 interviews (88, 92)
-- Vikram Singh (candidate 3): 2 interviews (72, 80)
-- Neha Gupta   (candidate 4): 1 interview  (85)
-- Arjun Reddy  (candidate 5): 1 interview  (70)
INSERT INTO interviews (interview_id, candidate_id, interviewer_id, request_id, interview_type, interview_date, interview_time, interview_status, overall_score) VALUES
(1, 1, 1, 1, 'TECHNICAL', '2025-01-05', '10:00:00', 'COMPLETED', 55.00),
(2, 1, 1, 2, 'TECHNICAL', '2025-01-20', '11:00:00', 'COMPLETED', 68.00),
(3, 1, 2, 3, 'HR',        '2025-02-01', '14:00:00', 'COMPLETED', 78.00),
(4, 1, 1, 4, 'TECHNICAL', '2025-02-15', '10:30:00', 'COMPLETED', 90.00),
(5, 2, 1, 5, 'TECHNICAL', '2025-02-18', '15:00:00', 'COMPLETED', 88.00),
(6, 3, 3, 6, 'TECHNICAL', '2025-02-25', '16:00:00', 'COMPLETED', 72.00),
(7, 3, 2, 7, 'HR',        '2025-03-05', '11:00:00', 'COMPLETED', 80.00),
(8, 4, 1, 8, 'TECHNICAL', '2025-03-10', '10:00:00', 'COMPLETED', 85.00),
(9, 5, 2, 9, 'HR',        '2025-03-15', '14:00:00', 'COMPLETED', 70.00),
(10,2, 1, 5, 'TECHNICAL', '2025-03-18', '15:00:00', 'COMPLETED', 92.00);

-- 10. Insert Feedback
INSERT INTO interview_feedback (feedback_id, interview_id, interviewer_id, feedback_text, overall_score, submitted_at) VALUES
(1, 1, 1, 'Candidate demonstrated foundational awareness of OOP but struggled with concurrency and collection internals.', 55.00, '2025-01-05 11:00:00'),
(2, 2, 1, 'Noticable improvement in Java collections and exception handling. Needs more clarity on thread synchronization.', 68.00, '2025-01-20 12:00:00'),
(3, 3, 2, 'Good communication skills, structured answers, and honest self-assessment during behavioral queries.', 78.00, '2025-02-01 15:00:00'),
(4, 4, 1, 'Outstanding performance! Successfully implemented lock-free patterns and articulated complex DB indexing strategies clearly.', 90.00, '2025-02-15 11:30:00'),
(5, 5, 1, 'Strong grasp of distributed systems, high confidence, and clean code articulation.', 88.00, '2025-02-18 16:00:00'),
(6, 6, 3, 'Solid understanding of database design. Could improve on algorithm complexity analysis.', 72.00, '2025-02-25 17:00:00'),
(7, 7, 2, 'Excellent communication and team orientation. Very honest about limitations and demonstrated growth mindset.', 80.00, '2025-03-05 12:00:00'),
(8, 8, 1, 'Exceptional problem-solving ability. Comfortable with multithreading and JDBC. Ready for senior interviews.', 85.00, '2025-03-10 11:00:00'),
(9, 9, 2, 'Good personality fit and clear career vision. Work on structured storytelling for behavioral rounds.', 70.00, '2025-03-15 15:00:00'),
(10,10, 1, 'Remarkable consistency. Demonstrated deep understanding of cloud systems and distributed architecture.', 92.00, '2025-03-18 16:30:00');

-- 11. Insert Skill Scores (Demonstrating category-wise progression)
-- Interview 1 (Rahul - Score 55)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(1, 'Technical Knowledge', 50.00),
(1, 'Problem Solving', 55.00),
(1, 'Communication Skills', 60.00),
(1, 'Confidence', 50.00),
(1, 'Logical Thinking', 55.00),
(1, 'Subject Knowledge', 60.00);

-- Interview 2 (Rahul - Score 68)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(2, 'Technical Knowledge', 65.00),
(2, 'Problem Solving', 70.00),
(2, 'Communication Skills', 65.00),
(2, 'Confidence', 68.00),
(2, 'Logical Thinking', 70.00),
(2, 'Subject Knowledge', 70.00);

-- Interview 3 (Rahul - Score 78)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(3, 'Technical Knowledge', 75.00),
(3, 'Problem Solving', 75.00),
(3, 'Communication Skills', 82.00),
(3, 'Confidence', 80.00),
(3, 'Logical Thinking', 78.00),
(3, 'Subject Knowledge', 78.00);

-- Interview 4 (Rahul - Score 90)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(4, 'Technical Knowledge', 92.00),
(4, 'Problem Solving', 90.00),
(4, 'Communication Skills', 88.00),
(4, 'Confidence', 90.00),
(4, 'Logical Thinking', 92.00),
(4, 'Subject Knowledge', 88.00);

-- Interview 5 (Ananya - Score 88)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(5, 'Technical Knowledge', 90.00),
(5, 'Problem Solving', 88.00),
(5, 'Communication Skills', 85.00),
(5, 'Confidence', 88.00),
(5, 'Logical Thinking', 90.00),
(5, 'Subject Knowledge', 87.00);

-- Interview 6 (Vikram - Score 72)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(6, 'Technical Knowledge', 70.00),
(6, 'Problem Solving', 72.00),
(6, 'Communication Skills', 68.00),
(6, 'Confidence', 72.00),
(6, 'Logical Thinking', 75.00),
(6, 'Subject Knowledge', 75.00);

-- Interview 7 (Vikram - Score 80)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(7, 'Technical Knowledge', 75.00),
(7, 'Problem Solving', 78.00),
(7, 'Communication Skills', 85.00),
(7, 'Confidence', 82.00),
(7, 'Logical Thinking', 80.00),
(7, 'Subject Knowledge', 80.00);

-- Interview 8 (Neha - Score 85)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(8, 'Technical Knowledge', 88.00),
(8, 'Problem Solving', 85.00),
(8, 'Communication Skills', 82.00),
(8, 'Confidence', 84.00),
(8, 'Logical Thinking', 87.00),
(8, 'Subject Knowledge', 84.00);

-- Interview 9 (Arjun - Score 70)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(9, 'Technical Knowledge', 65.00),
(9, 'Problem Solving', 68.00),
(9, 'Communication Skills', 75.00),
(9, 'Confidence', 72.00),
(9, 'Logical Thinking', 70.00),
(9, 'Subject Knowledge', 70.00);

-- Interview 10 (Ananya - Score 92)
INSERT INTO skill_scores (feedback_id, skill_name, score) VALUES
(10, 'Technical Knowledge', 94.00),
(10, 'Problem Solving', 92.00),
(10, 'Communication Skills', 90.00),
(10, 'Confidence', 92.00),
(10, 'Logical Thinking', 93.00),
(10, 'Subject Knowledge', 91.00);
