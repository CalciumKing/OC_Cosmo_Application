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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (13,30,45,2,'Hey!!!','Haircut (No Blow Dry)',5.00,'2025-04-04','0xff00ffff','something here maybe again!!!?',1),(9,30,60,2,'Jake Smith','Hair Coloring',60.00,'2025-04-04','salmon',NULL,2),(10,0,45,3,'Samantha Lee','Manicure',20.00,'2025-04-04','green',NULL,3),(10,15,45,2,'Joe Joe Joe','Haircut (No Blow Dry)',5.00,'2025-07-23','0x000080ff','working yes',4),(11,0,15,4,'Ava Martinez','Eyebrow Tinting',15.00,'2025-04-04','gold',NULL,5),(11,15,60,2,'Noah Wilson','Pedicure',30.00,'2025-04-04','red',NULL,6),(12,15,30,1,'Olivia Garcia','Waxing',25.00,'2025-04-04','brown',NULL,7),(13,0,45,3,'Mason Brown','Gel Nails',40.00,'2025-04-04','pink',NULL,8),(9,15,60,1,'Isabella Hall','Hair Perm',70.00,'2025-04-03','coral',NULL,9),(10,30,30,2,'Ethan Clark','Shampoo & Blow-dry',18.00,'2025-04-03','lightblue',NULL,10),(11,15,15,4,'Sophia Lewis','Nail Art',10.00,'2025-04-03','pink',NULL,11),(11,30,60,1,'James Young','Massage Therapy',50.00,'2025-04-03','lightgreen',NULL,12),(13,0,30,3,'Amelia King','Makeup Application',45.00,'2025-04-03','purple',NULL,13),(13,45,30,2,'Logan Wright','Scalp Treatment',28.00,'2025-04-03','yellow',NULL,14),(10,30,120,5,'John Pork','Foot Massage',999.99,'2025-04-09','black',NULL,15),(10,30,45,2,'Brian Smith','Coloring',45.00,'2025-04-07','#87CEFA',NULL,16),(13,0,90,3,'Cathy Tran','Perm',60.00,'2025-04-08','#FFD700',NULL,17),(11,15,30,4,'David Lee','Beard Trim',15.00,'2025-04-09','#98FB98',NULL,18),(14,45,60,5,'Ella Park','Hair Styling',35.00,'2025-04-10','#FFA07A',NULL,19),(9,30,120,6,'Frankie Wong','Color + Cut',70.00,'2025-04-11','#D8BFD8',NULL,20),(9,30,45,2,'me','Gel Nail Polish & Manicure',15.00,'2025-04-12','0xff00ffff',NULL,21),(9,30,45,2,'john pork 2','Hot Oil Manicure',12.00,'2025-04-18','0xffff66ff','note goes here, bro is literally john pork what is he doing here',22),(10,30,45,2,'bob','Haircut (No Blow Dry)',5.00,'2025-04-24','0xff0000ff','idk this guy',23),(1,45,30,2,'joe','Braids or Twists',10.00,'2025-04-24','0xff0000ff','',24),(13,45,30,2,'joe','Braids or Twists',10.00,'2025-04-24','0x00ff00ff','',25),(13,45,30,2,'','Braids or Twists',10.00,'2025-04-24','0xff00ffff','',26),(13,45,30,2,'test','Braids or Twists',10.00,'2025-04-24','0x808000ff','',27),(8,0,60,2,'test','Haircut (Blow Dry)',8.00,'2025-04-24','0x00ffffff','idk bro',28),(8,0,60,2,'test','Haircut (Blow Dry)',8.00,'2025-04-28','0x00ffffff','something here',29),(8,0,60,2,'test','Haircut (Blow Dry)',8.00,'2025-04-29','0x0000ffff','',30),(12,0,60,2,'mason','Haircut (Blow Dry)',8.00,'2025-04-25','0x800080ff','and a free foot massage',31),(12,0,60,2,'test','Haircut (Blow Dry)',8.00,'2025-04-25','0x008080ff','test',32),(8,0,60,2,'test','Haircut (Blow Dry)',8.00,'2025-04-24','0x808080ff','',33),(8,0,60,2,'another test','Haircut (Blow Dry)',8.00,'2025-04-24','0xff0000ff','',34),(8,0,60,2,'hopefully last test','Haircut (Blow Dry)',8.00,'2025-04-24','0x008080ff','nope',35),(8,0,60,2,'test','Haircut (Blow Dry)',8.00,'2025-04-24','0x00ffffff','',36);
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

-- Dump completed on 2025-05-07 12:04:05
