-- ====================================================================
-- InterviewHub: Database Schema Definition
-- Multi-Interviewer Interview Management & Performance Tracking System
-- Target RDBMS: MySQL 8.0+
-- Relational Model: 3NF with Referential Integrity & Check Constraints
-- ====================================================================

CREATE DATABASE IF NOT EXISTS interviewhub_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE interviewhub_db;

-- Drop child tables first to respect foreign keys
DROP TABLE IF EXISTS skill_scores;
DROP TABLE IF EXISTS interview_feedback;
DROP TABLE IF EXISTS interviews;
DROP TABLE IF EXISTS interview_requests;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS placement_drives;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS interviewers;
DROP TABLE IF EXISTS candidates;
DROP TABLE IF EXISTS users;

-- 1. Users Table (Core authentication and role management)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CANDIDATE', 'INTERVIEWER') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- 2. Candidates Table (College students & job seekers)
CREATE TABLE candidates (
    candidate_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    cgpa DECIMAL(4, 2) NOT NULL CHECK (cgpa >= 0.00 AND cgpa <= 10.00),
    graduation_year INT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    CONSTRAINT fk_candidate_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_candidate_dept (department),
    INDEX idx_candidate_cgpa (cgpa)
) ENGINE=InnoDB;

-- 3. Interviewers Table (Industry experts & HR managers)
CREATE TABLE interviewers (
    interviewer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    expertise VARCHAR(255) NOT NULL,
    experience_years INT NOT NULL DEFAULT 0 CHECK (experience_years >= 0),
    specialization VARCHAR(100) NOT NULL,
    organization VARCHAR(150) NOT NULL,
    CONSTRAINT fk_interviewer_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_interviewer_spec (specialization)
) ENGINE=InnoDB;

-- 4. Companies Table (Placement drive sponsors)
CREATE TABLE companies (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL UNIQUE,
    industry VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- 5. Placement Drives Table (Campus placement drives)
CREATE TABLE placement_drives (
    drive_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    drive_name VARCHAR(150) NOT NULL,
    eligibility_cgpa DECIMAL(4, 2) NOT NULL CHECK (eligibility_cgpa >= 0.00 AND eligibility_cgpa <= 10.00),
    eligible_department VARCHAR(100) NOT NULL,
    application_deadline DATE NOT NULL,
    status ENUM('OPEN', 'CLOSED', 'COMPLETED') NOT NULL DEFAULT 'OPEN',
    CONSTRAINT fk_drive_company FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE,
    INDEX idx_drive_status (status)
) ENGINE=InnoDB;

-- 6. Applications Table (Student applications to placement drives)
CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    drive_id INT NOT NULL,
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    application_status ENUM('APPLIED', 'SHORTLISTED', 'REJECTED', 'SELECTED') NOT NULL DEFAULT 'APPLIED',
    CONSTRAINT fk_app_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    CONSTRAINT fk_app_drive FOREIGN KEY (drive_id) REFERENCES placement_drives(drive_id) ON DELETE CASCADE,
    CONSTRAINT uq_candidate_drive UNIQUE (candidate_id, drive_id)
) ENGINE=InnoDB;

-- 7. Questions Table (Question bank categorized into Technical and HR)
CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    question_text TEXT NOT NULL,
    category ENUM('TECHNICAL', 'HR') NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL DEFAULT 'MEDIUM',
    specialization VARCHAR(100) NOT NULL,
    created_by INT NULL,
    CONSTRAINT fk_question_user FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_question_category (category)
) ENGINE=InnoDB;

-- 8. Interview Requests Table (Candidate requests to interviewers)
CREATE TABLE interview_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    interviewer_id INT NOT NULL,
    interview_type ENUM('TECHNICAL', 'HR') NOT NULL,
    preferred_date DATE NOT NULL,
    preferred_time TIME NOT NULL,
    request_status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_req_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_interviewer FOREIGN KEY (interviewer_id) REFERENCES interviewers(interviewer_id) ON DELETE CASCADE,
    INDEX idx_req_status (request_status),
    INDEX idx_req_candidate (candidate_id),
    INDEX idx_req_interviewer (interviewer_id)
) ENGINE=InnoDB;

-- 9. Interviews Table (Scheduled, completed, or cancelled interviews)
CREATE TABLE interviews (
    interview_id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    interviewer_id INT NOT NULL,
    request_id INT NULL UNIQUE,
    interview_type ENUM('TECHNICAL', 'HR') NOT NULL,
    interview_date DATE NOT NULL,
    interview_time TIME NOT NULL,
    interview_status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    overall_score DECIMAL(5, 2) NULL CHECK (overall_score IS NULL OR (overall_score >= 0.00 AND overall_score <= 100.00)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_int_candidate FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE,
    CONSTRAINT fk_int_interviewer FOREIGN KEY (interviewer_id) REFERENCES interviewers(interviewer_id) ON DELETE CASCADE,
    CONSTRAINT fk_int_request FOREIGN KEY (request_id) REFERENCES interview_requests(request_id) ON DELETE SET NULL,
    INDEX idx_int_status (interview_status),
    INDEX idx_int_candidate (candidate_id),
    INDEX idx_int_interviewer (interviewer_id),
    INDEX idx_int_date (interview_date)
) ENGINE=InnoDB;

-- 10. Interview Feedback Table (Interviewer evaluations)
CREATE TABLE interview_feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    interview_id INT NOT NULL UNIQUE,
    interviewer_id INT NOT NULL,
    feedback_text TEXT NOT NULL,
    overall_score DECIMAL(5, 2) NOT NULL CHECK (overall_score >= 0.00 AND overall_score <= 100.00),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feed_interview FOREIGN KEY (interview_id) REFERENCES interviews(interview_id) ON DELETE CASCADE,
    CONSTRAINT fk_feed_interviewer FOREIGN KEY (interviewer_id) REFERENCES interviewers(interviewer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Skill Scores Table (Skill-wise granular evaluation)
CREATE TABLE skill_scores (
    skill_score_id INT AUTO_INCREMENT PRIMARY KEY,
    feedback_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    score DECIMAL(5, 2) NOT NULL CHECK (score >= 0.00 AND score <= 100.00),
    CONSTRAINT fk_skill_feedback FOREIGN KEY (feedback_id) REFERENCES interview_feedback(feedback_id) ON DELETE CASCADE,
    INDEX idx_skill_name (skill_name)
) ENGINE=InnoDB;
