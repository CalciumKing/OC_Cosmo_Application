-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: cosmo
-- ------------------------------------------------------
-- Server version	8.0.34

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
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `startHour` int NOT NULL,
  `startMinute` int NOT NULL,
  `duration` int NOT NULL,
  `userID` int NOT NULL,
  `custName` varchar(50) NOT NULL,
  `service` varchar(50) NOT NULL,
  `cost` decimal(5,2) NOT NULL,
  `appDate` date NOT NULL,
  `color` varchar(10) NOT NULL,
  `note` varchar(100) DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (8,0,30,1,'Casey Lee','Pedicure',15.00,'2025-05-27','cyan',NULL,1),(8,45,90,1,'Casey Hall','Cap Highlight',25.00,'2025-05-27','purple',NULL,2),(10,30,30,1,'Peyton Lee','Makeup Application',10.00,'2025-05-27','orange',NULL,3),(11,15,15,1,'Riley Smith','Paraffin Wax Drip',5.00,'2025-05-27','red',NULL,4),(11,45,90,1,'Riley Brown','Color Retouch',30.00,'2025-05-27','salmon','Requested stylist A',5),(9,30,15,1,'Morgan Smith','Pedicure',15.00,'2025-05-28','cyan',NULL,6),(12,15,60,1,'Taylor Jones','Eyebrow Wax',8.00,'2025-05-28','green',NULL,7),(13,30,90,2,'Skyler Smith','Facial',25.00,'2025-05-28','pink','Customer allergic to latex',8),(8,0,90,3,'Jordan Smith','Updo',35.00,'2025-05-28','gold',NULL,9),(8,15,60,1,'Skyler Brown','Cap Highlight',25.00,'2025-05-29','purple',NULL,10),(8,45,90,2,'Riley Davis','Paraffin Wax Drip',5.00,'2025-05-29','red','Customer allergic to latex',11),(8,0,15,2,'Morgan Davis','Color Retouch',30.00,'2025-05-30','salmon',NULL,12),(12,30,15,2,'Casey Davis','Pedicure',15.00,'2025-05-30','cyan','First-time client',13),(14,0,45,3,'Peyton Smith','Facial',25.00,'2025-05-30','pink','Customer allergic to latex',14),(13,15,30,3,'Peyton Davis','Shampoo & Blow Dry',10.00,'2025-05-30','lightblue','Bring color sample',15),(11,30,15,3,'Jamie Garcia','Makeup Application',10.00,'2025-05-30','orange','First-time client',16),(10,0,60,1,'Alex Brown','Facial',25.00,'2025-06-01','pink','Requested stylist A',17),(12,45,60,3,'Morgan Lee','Color Retouch',30.00,'2025-06-01','salmon','First-time client',18),(9,30,15,3,'Taylor Lopez','Updo',35.00,'2025-06-01','gold','Customer allergic to latex',19),(9,45,15,3,'Casey Brown','Cap Highlight',25.00,'2025-06-01','purple','Requested stylist A',20),(12,30,30,3,'Skyler Lopez','Updo',35.00,'2025-06-01','gold','Customer allergic to latex',21),(10,45,60,2,'Peyton Johnson','Makeup Application',10.00,'2025-06-02','orange','Requested stylist A',22),(8,0,15,3,'Casey Smith','Cap Highlight',25.00,'2025-06-02','purple','Customer allergic to latex',23),(11,15,15,3,'Alex Smith','Haircut',20.00,'2025-06-02','blue','Customer allergic to latex',24),(12,15,30,3,'Alex Johnson','Eyebrow Wax',8.00,'2025-06-03','green','Requested stylist A',25),(14,0,15,3,'Jordan Lopez','Updo',35.00,'2025-06-03','gold','Customer allergic to latex',26),(13,30,60,1,'Morgan Garcia','Color Retouch',30.00,'2025-06-04','salmon','Requested stylist A',27),(13,0,30,1,'Kendall Lopez','Paraffin Wax Drip',5.00,'2025-06-06','red',NULL,28),(14,45,90,2,'Skyler Lopez','Facial',25.00,'2025-06-06','pink','First-time client',29),(8,45,15,2,'Jamie Jones','Haircut',20.00,'2025-06-06','blue',NULL,30),(11,30,90,2,'Riley Garcia','Shampoo & Blow Dry',10.00,'2025-06-06','lightblue','Requested stylist A',31),(14,15,60,2,'Casey Brown','Updo',35.00,'2025-06-06','gold','Requested stylist A',32),(12,45,30,2,'Peyton Davis','Haircut',20.00,'2025-06-07','blue','First-time client',33),(12,15,30,3,'Casey Brown','Eyebrow Wax',8.00,'2025-06-08','green','First-time client',34),(13,30,90,3,'Skyler Lee','Paraffin Wax Drip',5.00,'2025-06-08','red','Customer allergic to latex',35),(10,0,90,3,'Skyler Jones','Color Retouch',30.00,'2025-06-08','salmon','Bring color sample',36),(14,0,90,3,'Kendall Brown','Color Retouch',30.00,'2025-06-08','salmon','Bring color sample',37),(9,15,30,1,'Taylor Lee','Shampoo & Blow Dry',10.00,'2025-06-09','lightblue','Bring color sample',38),(8,30,60,2,'Riley Johnson','Eyebrow Wax',8.00,'2025-06-09','green','Customer allergic to latex',39),(13,45,60,3,'Jordan Jones','Shampoo & Blow Dry',10.00,'2025-06-09','lightblue','First-time client',40),(14,45,45,3,'Casey Lee','Paraffin Wax Drip',5.00,'2025-06-09','red','Customer allergic to latex',41),(12,15,45,3,'Taylor Lopez','Shampoo & Blow Dry',10.00,'2025-06-09','lightblue','Customer allergic to latex',42),(10,45,60,1,'Jamie Johnson','Updo',35.00,'2025-06-10','gold','Bring color sample',43),(8,15,30,3,'Jordan Brown','Haircut',20.00,'2025-06-10','blue','First-time client',44),(8,30,60,3,'Casey Johnson','Makeup Application',10.00,'2025-06-10','orange',NULL,45),(11,0,30,3,'Kendall Davis','Updo',35.00,'2025-06-10','gold','Customer allergic to latex',46),(8,0,60,3,'Jamie Smith','Updo',35.00,'2025-06-10','gold','Customer allergic to latex',47),(12,0,15,1,'Jordan Miller','Facial',25.00,'2025-06-11','pink',NULL,48),(10,0,15,2,'Taylor Davis','Facial',25.00,'2025-06-11','pink','Requested stylist A',49),(9,30,45,2,'Jamie Lopez','Updo',35.00,'2025-06-11','gold',NULL,50),(13,15,90,1,'Casey Jones','Facial',25.00,'2025-06-12','pink','First-time client',51),(10,30,60,1,'Skyler Lee','Cap Highlight',25.00,'2025-06-12','purple',NULL,52),(11,15,30,3,'Alex Davis','Shampoo & Blow Dry',10.00,'2025-06-12','lightblue','Bring color sample',53),(14,0,60,2,'Peyton Smith','Eyebrow Wax',8.00,'2025-06-13','green','Bring color sample',54),(9,45,15,2,'Peyton Lopez','Paraffin Wax Drip',5.00,'2025-06-13','red','Requested stylist A',55),(14,0,90,1,'Taylor Davis','Eyebrow Wax',8.00,'2025-06-14','green',NULL,56),(9,45,60,1,'Kendall Lopez','Color Retouch',30.00,'2025-06-14','salmon','Bring color sample',57),(9,0,45,1,'Morgan Davis','Pedicure',15.00,'2025-06-15','cyan',NULL,58),(8,0,90,2,'Skyler Smith','Shampoo & Blow Dry',10.00,'2025-06-16','lightblue','Bring color sample',59),(11,45,60,2,'Casey Miller','Facial',25.00,'2025-06-16','pink','Customer allergic to latex',60),(14,45,30,3,'Jordan Smith','Shampoo & Blow Dry',10.00,'2025-06-16','lightblue','Customer allergic to latex',61);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-27 10:32:33
