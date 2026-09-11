-- MySQL dump 10.13  Distrib 8.4.11, for Linux (x86_64)
--
-- Host: localhost    Database: ticket_pro_db
-- ------------------------------------------------------
-- Server version	8.4.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `venue_id` binary(16) DEFAULT NULL,
  `public_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_events_venue` (`venue_id`),
  CONSTRAINT `fk_events_venue` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0','Vietnam Music Festival 2026','A large-scale live music festival featuring Vietnamese artists.','2026-09-12 19:00:00','2026-09-12 23:00:00','UPCOMING','https://res.cloudinary.com/hrpgmq0t/image/upload/v1789117332/events/seatmaps/zrlfolcsygndg4ksubrq.jpg',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','zrlfolcsygndg4ksubrq'),(_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0','Tech Conference Vietnam 2026','Technology conference for developers, startups and technology companies.','2026-09-20 08:30:00','2026-09-20 17:30:00','UPCOMING','https://res.cloudinary.com/hrpgmq0t/image/upload/v1789111949/events/seatmaps/gyxaehjvoaqokhgabpjs.jpg',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','gyxaehjvoaqokhgabpjs'),(_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0','Business Networking Night','Networking event for entrepreneurs and business professionals.','2026-10-05 18:30:00','2026-10-05 22:00:00','UPCOMING','https://res.cloudinary.com/hrpgmq0t/image/upload/v1789112126/events/seatmaps/mjyixy4sj8x8ioq1me4m.jpg',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','mjyixy4sj8x8ioq1me4m'),(_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0','Comedy Night 2026','A live comedy show with popular comedians.','2026-10-18 19:30:00','2026-10-18 22:00:00','UPCOMING','https://res.cloudinary.com/hrpgmq0t/image/upload/v1789112301/events/seatmaps/lcznqjnkayjkxbnoe0ra.jpg',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','lcznqjnkayjkxbnoe0ra'),(_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0','New Year Countdown 2027','Countdown party welcoming the new year.','2026-12-31 20:00:00','2027-01-01 00:30:00','UPCOMING','https://res.cloudinary.com/hrpgmq0t/image/upload/v1789112331/events/seatmaps/eooncaivztuuiyndjvw5.jpg',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','eooncaivztuuiyndjvw5'),(_binary 'Nînf\ˆΩBà∫\÷@ØP\ÿ\'','Sky Tour 2026','DHG','2026-09-10 18:21:00','2026-09-26 18:21:00','CANCELLED',NULL,_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0',NULL),(_binary '´π‘†\Ÿ\‡GØ\Ûlí2]T','iPhone 18 event','The iPhone 18 event, also known as Apple\'s \"Surprise and Shine\" event, is set to take place on September 9, 2026. This event will unveil the iPhone 18 Pro and Pro Max, featuring the most advanced camera system in an iPhone, and significant improvements in battery life and performance. The event will also introduce Apple\'s first foldable iPhone, the iPhone Duo, which is expected to have a unique design and advanced features. The event is anticipated to be Apple\'s biggest launch event of the year, with a focus on premium products and innovative technology. ','2026-09-09 16:27:00','2026-09-10 16:27:00','FINISHED','https://res.cloudinary.com/hrpgmq0t/image/upload/v1789120991/events/seatmaps/oblimjokgpqyx2yn6zr4.jpg',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','events/seatmaps/oblimjokgpqyx2yn6zr4'),(_binary '\»\√\÷¿xB±\’6X*ïñ','Sky Tour 2026','Sky Tour l√† tour di·ªÖn l·ªõn ·ªü Vi·ªát Nam c·ªßa ngh·ªá sƒ© S∆°n T√πng M-TP.','2026-09-17 07:00:00','2026-09-19 23:21:00','UPCOMING','https://res.cloudinary.com/hrpgmq0t/image/upload/v1789112360/events/seatmaps/sstb0nv5omoq6ezekhft.jpg',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','sstb0nv5omoq6ezekhft');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` binary(16) NOT NULL,
  `quantity` int DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `seat_id` binary(16) DEFAULT NULL,
  `order_id` binary(16) DEFAULT NULL,
  `tickettype_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_items_order` (`order_id`),
  KEY `fk_order_items_ticket_type` (`tickettype_id`),
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_items_ticket_type` FOREIGN KEY (`tickettype_id`) REFERENCES `ticket_types` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (_binary 'p\0\0\0\0\0\0\0\0\0\0\0\0\0\0',1,1500000.00,_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary 'p\0\0\0\0\0\0\0\0\0\0\0\0\0\0',1,1500000.00,_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary 'p\0\0\0\0\0\0\0\0\0\0\0\0\0\0',1,800000.00,_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary 'p\0\0\0\0\0\0\0\0\0\0\0\0\0\0',1,800000.00,_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary 'p\0\0\0\0\0\0\0\0\0\0\0\0\0\0',1,1200000.00,_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0	',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary 'ô^\\ |O≠Ç+\Ë\Ô∂3:',1,700000.00,NULL,_binary '\rê\·A\…G◊∏ª så4Å\Î',_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` binary(16) NOT NULL,
  `total_amount` decimal(19,2) NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `user_id` binary(16) DEFAULT NULL,
  `promotion_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_orders_user` (`user_id`),
  KEY `fk_orders_promotion` (`promotion_id`),
  CONSTRAINT `fk_orders_promotion` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (_binary '\rê\·A\…G◊∏ª så4Å\Î',700000.00,'PENDING','2026-09-07 20:48:30',NULL,NULL),(_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',2700000.00,'PAID','2026-09-01 09:15:00',_binary '\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"',_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',800000.00,'PAID','2026-09-01 10:20:00',_binary '3333333333333333',NULL),(_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',700000.00,'PAID','2026-09-01 11:05:00',_binary 'DDDDDDDDDDDDDDDD',_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',1200000.00,'PAID','2026-09-01 13:30:00',_binary 'UUUUUUUUUUUUUUUU',_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',500000.00,'PENDING','2026-08-22 23:30:00',_binary 'ffffffffffffffff',NULL),(_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',750000.00,'PAID','2026-09-01 14:10:00',_binary 'wwwwwwwwwwwwwwww',_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0',1200000.00,'PAID','2026-09-01 15:40:00',_binary 'àààààààààààààààà',NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` binary(16) NOT NULL,
  `order_id` binary(16) DEFAULT NULL,
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_method` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `payment_status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_payments_order` (`order_id`),
  CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (_binary 'Ä\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0','TXN202608200001','VNPAY',2700000.00,'SUCCESS'),(_binary 'Ä\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0','TXN202608200002','MOMO',800000.00,'SUCCESS'),(_binary 'Ä\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0','TXN202608200003','BANK_TRANSFER',700000.00,'SUCCESS'),(_binary 'Ä\0\0\0\0\0\0\0\0\0\0\0\0\0\0',_binary '`\0\0\0\0\0\0\0\0\0\0\0\0\0\0','TXN202608200004','VNPAY',1200000.00,'SUCCESS');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotions` (
  `id` binary(16) NOT NULL,
  `code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `discount_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `discount_value` decimal(19,2) DEFAULT NULL,
  `max_usage` int DEFAULT NULL,
  `used_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_promotions_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotions`
--

LOCK TABLES `promotions` WRITE;
/*!40000 ALTER TABLE `promotions` DISABLE KEYS */;
INSERT INTO `promotions` VALUES (_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0','WELCOME10','PERCENTAGE',10.00,100,2),(_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0','SUMMER100K','FIXED',100000.00,50,1),(_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0','VIP20','PERCENTAGE',20.00,20,1),(_binary 'P\0\0\0\0\0\0\0\0\0\0\0\0\0\0','EVENT50K','FIXED',50000.00,100,2);
/*!40000 ALTER TABLE `promotions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` binary(16) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_roles_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (_binary '™™™™™™™™™™™™™™™™','Administrator','ADMIN'),(_binary 'ªªªªªªªªªªªªªªªª','CUSTOMER','CUSTOMER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seats`
--

DROP TABLE IF EXISTS `seats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seats` (
  `id` binary(16) NOT NULL,
  `seat_row` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `seat_number` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` int NOT NULL,
  `tickettype_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_seats_ticket_type` (`tickettype_id`),
  CONSTRAINT `fk_seats_ticket_type` FOREIGN KEY (`tickettype_id`) REFERENCES `ticket_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seats`
--

LOCK TABLES `seats` WRITE;
/*!40000 ALTER TABLE `seats` DISABLE KEYS */;
INSERT INTO `seats` VALUES (_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','A','01','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','A','02','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','A','03','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','A','04','AVAILABLE',0,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','A','05','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','A','06','AVAILABLE',0,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','B','01','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','B','02','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0	','B','03','AVAILABLE',0,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','B','04','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','C','01','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','C','02','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','C','03','SOLD',1,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '@\0\0\0\0\0\0\0\0\0\0\0\0\0\0','C','04','AVAILABLE',0,_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0');
/*!40000 ALTER TABLE `seats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_types`
--

DROP TABLE IF EXISTS `ticket_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_types` (
  `id` binary(16) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `total_quantity` int NOT NULL,
  `available_quantity` int DEFAULT NULL,
  `event_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ticket_types_event` (`event_id`),
  CONSTRAINT `fk_ticket_types_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_ticket_available` CHECK ((`available_quantity` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_types`
--

LOCK TABLES `ticket_types` WRITE;
/*!40000 ALTER TABLE `ticket_types` DISABLE KEYS */;
INSERT INTO `ticket_types` VALUES (_binary '\’*Ω1Dúö5Bà\Õ$à!','VIP',3000000.00,10000,10000,_binary '\»\√\÷¿xB±\’6X*ïñ'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','VIP',1500000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','Standard',800000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','Economy',500000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','Premium',1200000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','Standard',700000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','VIP',900000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','Standard',500000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','VIP',1000000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0	','Standard',600000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','VIP',2000000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary '0\0\0\0\0\0\0\0\0\0\0\0\0\0\0','Standard',1000000.00,10,10,_binary ' \0\0\0\0\0\0\0\0\0\0\0\0\0\0'),(_binary 'NJ¢+©\ÏOΩEHÃëF,8','VVIP',5000000.00,5000,5000,_binary '\»\√\÷¿xB±\’6X*ïñ'),(_binary 'õ\∆JCá≥GfµMv˘K\Z(Z','VIP',4000000.00,1000,1000,_binary '´π‘†\Ÿ\‡GØ\Ûlí2]T');
/*!40000 ALTER TABLE `ticket_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `user_id` binary(16) NOT NULL,
  `role_id` binary(16) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (_binary '',_binary '™™™™™™™™™™™™™™™™'),(_binary 'cÖ\Ê{˛Awù\÷\Ë&\‘ƒ∑º',_binary '™™™™™™™™™™™™™™™™'),(_binary '\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"',_binary 'ªªªªªªªªªªªªªªªª'),(_binary '3333333333333333',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'DDDDDDDDDDDDDDDD',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'UUUUUUUUUUUUUUUU',_binary 'ªªªªªªªªªªªªªªªª'),(_binary ']\ˆ3%©gI\Òö•/Wìè`',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'ffffffffffffffff',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'jM\0\ÛG@JäßvT\◊\Û\·\ıZ',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'wwwwwwwwwwwwwwww',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'àààààààààààààààà',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'äImOó\Ê^¸Ü€â',_binary 'ªªªªªªªªªªªªªªªª'),(_binary 'ï¸¿∫\ŸBWú5g`ÎçÅ\Í',_binary 'ªªªªªªªªªªªªªªªª');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` binary(16) NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fullname` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `provider` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'LOCAL',
  `provider_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_provider` (`provider`,`provider_id`),
  UNIQUE KEY `user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (_binary '','admin@eventhub.vn','System Admin','$2a$10$9sV1aZIEcPCX.FhnpZWjsO7OIeILfVM57OKtneETihr7x/r/0cD3C','LOCAL',NULL,1),(_binary '\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"','nguyenan@gmail.com','Nguyen An','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','LOCAL',NULL,1),(_binary '3333333333333333','tranbinh@gmail.com','Tran Binh','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','LOCAL',NULL,1),(_binary 'DDDDDDDDDDDDDDDD','lechi@gmail.com','Le Chi','$2a$10$9sV1aZIEcPCX.FhnpZWjsO7OIeILfVM57OKtneETihr7x/r/0cD3C','LOCAL',NULL,1),(_binary 'UUUUUUUUUUUUUUUU','phamduc@gmail.com','Pham Duc','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','LOCAL',NULL,1),(_binary ']\ˆ3%©gI\Òö•/Wìè`','phtuan14@gmail.com','Phan Anh Tuan',NULL,'GOOGLE','102287146044690051039',1),(_binary 'cÖ\Ê{˛Awù\÷\Ë&\‘ƒ∑º','tuan.shltr@gmail.com','Tu·∫•n Phan Anh',NULL,'GOOGLE','112495066051206649008',1),(_binary 'ffffffffffffffff','hoangminh@gmail.com','Hoang Minh','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','LOCAL',NULL,1),(_binary 'jM\0\ÛG@JäßvT\◊\Û\·\ıZ','phnphn0018@gmail.com','Phn Phn',NULL,'GOOGLE','101126722484063837919',1),(_binary 'wwwwwwwwwwwwwwww','vothu@gmail.com','Vo Thu','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','LOCAL',NULL,1),(_binary 'àààààààààààààààà','doanlong@gmail.com','Doan Long','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','LOCAL',NULL,1),(_binary 'äImOó\Ê^¸Ü€â','patman4477@gmail.com','Man Pat',NULL,'GOOGLE','105331877529900731196',1),(_binary 'ï¸¿∫\ŸBWú5g`ÎçÅ\Í','j14.tuan@gmail.com','Jone Jj',NULL,'FACEBOOK','1783551952958117',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `venues`
--

DROP TABLE IF EXISTS `venues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venues` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` tinytext COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venues`
--

LOCK TABLES `venues` WRITE;
/*!40000 ALTER TABLE `venues` DISABLE KEYS */;
INSERT INTO `venues` VALUES (_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','National Convention Center','Pham Hung, Nam Tu Liem, Ha Noi',4000),(_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','Quan Ngua Sports Palace','Van Cao, Ba Dinh, Ha Noi',3000),(_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\0','SECC','799 Nguyen Van Linh, District 7, Ho Chi Minh City',5000);
/*!40000 ALTER TABLE `venues` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-11 16:45:43
