<?php

class RestaurantDb {
	
	private $pdo;
	
	public function __construct() {	
		$this->pdo = new FoodoPDO();
	}
	
	public function createTables() {		
		$this->createRestaurantTable();
		$this->createRatingTable();
	}
	
	private function createRatingTable() {
		$drop_sql = "DROP TABLE IF EXISTS ratings";
		
		$sql = "CREATE TABLE `ratings` (
				`id` INT(11) NOT NULL AUTO_INCREMENT ,
				`restaurant_id` INT(11) NOT NULL ,
				`rating` INT(1) NOT NULL ,
				PRIMARY KEY (`id`)
				) ENGINE = MYISAM";
		
		$this->pdo->exec($drop_sql);
		$this->pdo->exec($sql);
	}
	
	private function createRestaurantTable() {
		$drop_sql = "DROP TABLE IF EXISTS `restaurants`";
		
		$sql = "CREATE TABLE IF NOT EXISTS `restaurants` (
			  `id` int(11) NOT NULL auto_increment,
			  `name` varchar(255) character set latin1 NOT NULL,
			  `description` text character set latin1,
			  `phone` varchar(32) character set latin1 default NULL,
			  `lat` int(11) default NULL,
			  `lng` int(11) default NULL,
			  `created_at` datetime default NULL,
			  PRIMARY KEY  (`id`)
			) ENGINE=MyISAM  DEFAULT CHARSET=utf8;";
		
		$this->pdo->exec($drop_sql);
		$this->pdo->exec($sql);
	}
	
	public function insertInitialData() {
		$insert_sql = "INSERT INTO `restaurants` (`id`,`name`,`description`,`phone`,`lat`,`lng`,`created_at`)
			VALUES
			(1,'Burger Joint','Awesome burgers','8483756',64139078,-21936757,'2009-10-08 10:16:00'),
			(2,'Pizza Joint','The best Pizza Joint',NULL,64135603,-21954812,'2009-10-08 10:16:20'),
			(3,'Asian Joint','blabla',NULL,64136603,-21953812,'2009-10-08 10:16:20');";		
		$this->pdo->exec($insert_sql);

		
		$insert_sql = "INSERT INTO ratings (restaurant_id, rating) VALUES
			(1, 5),
			(1, 5),
			(2, 4),
			(3, 3);";
		$this->pdo->exec($insert_sql);
	}
	
	public function selectAll() {
		$items = array();
		
		
		$sql = "SELECT 
				R.*, 
				FORMAT(AVG(ratings.rating),1) as rating, 
				COUNT(ratings.id) as rating_count
				FROM 
				restaurants R
				LEFT JOIN ratings 
				ON R.id = ratings.restaurant_id
				GROUP BY R.id";
		
		foreach ($this->pdo->query($sql, PDO::FETCH_ASSOC) as $row) {
			$items[] = $this->createRestaurant($row);
		}
		return $items;
	}
	
	private function createRestaurant($row) {
		$r = new Restaurant();
		
		$r->setId($row['id']);
		$r->setName($row['name']);
		$r->setDescription($row['description']);
		$r->setRating($row['rating']);
		$r->setRatingCount($row['rating_count']);
		$r->setPhone($row['phone']);
		$r->setLat($row['lat']);
		$r->setLng($row['lng']);
		$r->setCreated($row['created_at']);
		//$r->setModified($row['modified_at']);

		return $r;
	}
	
	
}