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
			  `name` varchar(255) NOT NULL,
			  `description` text,
			  `phone` varchar(32) default NULL,
			  `address` varchar(40) NOT NULL default '',
			  `zip` int(11) default NULL,
			  `city` varchar(30) default NULL,
			  `website` varchar(50) default NULL,
			  `email` varchar(30) default NULL,
			  `img` varchar(50) default NULL,
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
	
	public function rate($id, $rating) {
		
		$sql = "INSERT INTO ratings (restaurant_id, rating) VALUES (:rid, :rating)";
		$q = $this->pdo->prepare($sql);
		return $q->execute(array(':rid'=>$id, ':rating'=>$rating));
	}
	
	public function selectFromId($id) {
		
		$stmt = $this->pdo->prepare("SELECT 
				R.*, 
				FORMAT(AVG(ratings.rating),1) as rating, 
				COUNT(ratings.id) as rating_count
				FROM 
				restaurants R
				LEFT JOIN ratings 
				ON R.id = ratings.restaurant_id
				WHERE R.id=?
				GROUP BY R.id");
		$stmt->execute(array($id));
		$r = $stmt->fetch(PDO::FETCH_ASSOC);
		
		if ($r)
		{
			return $this->createRestaurant($r);
		}
		else {
			return false;
		}
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
		$r->setAddress($row['address']);
		$r->setZip($row['zip']);
		$r->setCity($row['city']);
		$r->setWebsite($row['website']);
		$r->setEmail($row['email']);
		
		//$r->setModified($row['modified_at']);

		return $r;
	}
	
	
}