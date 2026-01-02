-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: hms_fyp
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ai_reports`
--

DROP TABLE IF EXISTS `ai_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_reports` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `image_id` int NOT NULL,
  `prediction` varchar(100) NOT NULL,
  `confidence_score` decimal(5,2) DEFAULT NULL,
  `generated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_visible` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`report_id`),
  KEY `image_id` (`image_id`),
  CONSTRAINT `ai_reports_ibfk_1` FOREIGN KEY (`image_id`) REFERENCES `xray_images` (`image_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_reports`
--

LOCK TABLES `ai_reports` WRITE;
/*!40000 ALTER TABLE `ai_reports` DISABLE KEYS */;
INSERT INTO `ai_reports` VALUES (1,2,'PNEUMONIA',0.98,'2025-12-26 09:51:49',1),(2,3,'NORMAL',0.87,'2025-12-26 22:01:46',0),(3,4,'PNEUMONIA',0.98,'2025-12-30 16:47:23',1),(4,5,'PNEUMONIA',0.98,'2026-01-01 06:41:27',1);
/*!40000 ALTER TABLE `ai_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `status` enum('PENDING','APPROVED','REJECTED','UPLOADED','COMPLETED') DEFAULT 'PENDING',
  `is_seen` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`appointment_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,2,'2025-12-11','10:00:00','UPLOADED',1),(2,2,'2026-01-02','14:00:00','UPLOADED',1),(3,2,'2025-12-31','10:30:00','PENDING',1),(4,3,'2026-01-09','10:30:00','APPROVED',1),(5,5,'2026-01-10','10:00:00','APPROVED',1);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_diagnosis`
--

DROP TABLE IF EXISTS `doctor_diagnosis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_diagnosis` (
  `diagnosis_id` int NOT NULL AUTO_INCREMENT,
  `report_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `diagnosis_result` varchar(255) DEFAULT NULL,
  `comments` text,
  `diagnosis_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `report_file_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`diagnosis_id`),
  KEY `report_id` (`report_id`),
  KEY `doctor_id` (`doctor_id`),
  CONSTRAINT `doctor_diagnosis_ibfk_1` FOREIGN KEY (`report_id`) REFERENCES `ai_reports` (`report_id`) ON DELETE CASCADE,
  CONSTRAINT `doctor_diagnosis_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_diagnosis`
--

LOCK TABLES `doctor_diagnosis` WRITE;
/*!40000 ALTER TABLE `doctor_diagnosis` DISABLE KEYS */;
INSERT INTO `doctor_diagnosis` VALUES (1,1,1,'FINAL_DIAGNOSIS','nice!!','2025-12-26 14:07:34','D:\\fyp\\fyp2\\reports\\1_1767114860117_BBMK3113 2025C IA Jason Ho Yu Heng 7.pdf'),(2,3,1,NULL,NULL,'2025-12-31 01:24:25','D:\\fyp\\fyp2\\reports\\3_1767144295511_1_1767110805892_BBMK3113_ Ind Assg_ B230111B (1).pdf'),(3,4,1,'FINAL_DIAGNOSIS','no problem','2026-01-01 06:44:44','D:\\fyp\\fyp2\\reports\\4_1767278747583_aaaaaaaaaa.pdf');
/*!40000 ALTER TABLE `doctor_diagnosis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
  `doctor_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `specialization` varchar(100) DEFAULT NULL,
  `license_number` varchar(50) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`doctor_id`),
  UNIQUE KEY `license_number` (`license_number`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `doctors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (1,10,'cccccccc','ccccccccc','ccccccccccc'),(2,15,'aaaaaaaaaa','1111111111','aaaaaaaaaaa'),(3,18,'aaaaaaaaaa','1111111111111','aaaaaaaaaaaaaaa');
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `message` text NOT NULL,
  `notification_type` varchar(50) DEFAULT NULL,
  `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('SENT','READ') DEFAULT 'SENT',
  PRIMARY KEY (`notification_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nurses`
--

DROP TABLE IF EXISTS `nurses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nurses` (
  `nurse_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `department` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`nurse_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `nurses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nurses`
--

LOCK TABLES `nurses` WRITE;
/*!40000 ALTER TABLE `nurses` DISABLE KEYS */;
INSERT INTO `nurses` VALUES (1,11,'aaaaaaa'),(2,16,'aaaaaaaaaaa');
/*!40000 ALTER TABLE `nurses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patients` (
  `patient_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `ic_number` varchar(20) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `gender` enum('MALE','FEMALE') DEFAULT NULL,
  `address` text,
  PRIMARY KEY (`patient_id`),
  UNIQUE KEY `ic_number` (`ic_number`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `patients_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES (2,8,'a','a','2025-12-20','MALE','a'),(3,13,'aaa','aaa','2025-12-25','MALE',NULL),(4,14,'aaaaaaaaa','aaaaaaa',NULL,'MALE','aaaaaaaaaa'),(5,17,'jason','111111111111','2025-12-30','MALE','1,jalan temenggoong 5,\nTaman A,\n81300 Skudai\nJohor');
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('PATIENT','NURSE','DOCTOR','ADMIN') NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'doctor1','password','DOCTOR','doctor1@hospital.com',NULL,'ACTIVE'),(2,'nurse1','password','NURSE','nurse1@hospital.com',NULL,'ACTIVE'),(3,'patient1','password','PATIENT','patient1@hospital.com',NULL,'ACTIVE'),(8,'a','qweasd','PATIENT','a','a','ACTIVE'),(9,'admin','password','ADMIN','a','a','ACTIVE'),(10,'c','cccccccc','DOCTOR','c','c','ACTIVE'),(11,'aaaaaa','aaaaaaaaaaaa','NURSE','aaaaaa','aaaaaaaa','ACTIVE'),(12,'test','testing','PATIENT','a','1','ACTIVE'),(13,'tests','testing','PATIENT','a','1','ACTIVE'),(14,'111111','111111111111','PATIENT','1111111','11111111','ACTIVE'),(15,'zzzzzzz','zzzzzzzzzz','DOCTOR','zzzzzzzzzzzz','zzzzzzzzzzzzz','ACTIVE'),(16,'wwwwwwwwww','wwwwwwwwwwwwww','NURSE','wwwwwwwwwwwwwwwwww','w11111111111','ACTIVE'),(17,'usera','aaaaaa','PATIENT','jason@gmail.com','018-9082422','ACTIVE'),(18,'aaaaaaa','aaaaaaaaaa','DOCTOR','jason@gmail.com','111111111111','ACTIVE');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xray_images`
--

DROP TABLE IF EXISTS `xray_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xray_images` (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `uploaded_by` int DEFAULT NULL,
  `image_path` varchar(255) NOT NULL,
  `upload_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`),
  KEY `appointment_id` (`appointment_id`),
  KEY `uploaded_by` (`uploaded_by`),
  CONSTRAINT `xray_images_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`) ON DELETE CASCADE,
  CONSTRAINT `xray_images_ibfk_2` FOREIGN KEY (`uploaded_by`) REFERENCES `nurses` (`nurse_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xray_images`
--

LOCK TABLES `xray_images` WRITE;
/*!40000 ALTER TABLE `xray_images` DISABLE KEYS */;
INSERT INTO `xray_images` VALUES (1,1,1,'D:\\fyp\\fyp2\\uploads\\1_1766771203320_images.jpg','2025-12-26 09:46:43'),(2,2,1,'D:\\fyp\\fyp2\\uploads\\2_1766771507056_images.jpg','2025-12-26 09:51:47'),(3,1,1,'D:\\fyp\\fyp2\\uploads\\1_1766815302689_nsclc_cavity.jpg','2025-12-26 22:01:43'),(4,2,1,'D:\\fyp\\fyp2\\uploads\\2_1767142040292_images.jpg','2025-12-30 16:47:20'),(5,1,1,'D:\\fyp\\fyp2\\uploads\\1_1767278484258_images.jpg','2026-01-01 06:41:24');
/*!40000 ALTER TABLE `xray_images` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-03  3:25:58
