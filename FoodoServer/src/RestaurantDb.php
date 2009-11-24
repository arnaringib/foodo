<?php

class RestaurantDb {

	/**
	 * 
	 * @var FoodoPDO
	 */
	private $pdo;
	
	public function __construct() {	
		$this->pdo = FoodoPDO::getInstance();
	}
	
	public function createTables() {		
		$this->createRestaurantTable();
		$this->createRatingTable();
		$this->createTypesTable();
		$this->createRestaurantsTypesTable();
		
		$this->insertTypesData();
		$this->insertRestaurantsTypesData();
	}
	
	private function createRatingTable() {
		$drop_sql = "DROP TABLE IF EXISTS ratings";
		
		$sql = "CREATE TABLE `ratings` (
				`id` INT(11) NOT NULL AUTO_INCREMENT ,
				`restaurant_id` INT(11) NOT NULL ,
				`rating` INT(1) NOT NULL ,
				`user_id` INT(11) NOT NULL ,
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
			  `pricegroup` int(1) default NULL,
			  PRIMARY KEY  (`id`)
			) ENGINE=MyISAM  DEFAULT CHARSET=utf8;";
		
		$this->pdo->exec($drop_sql);
		$this->pdo->exec($sql);
	}
	
	private function createTypesTable(){
		$drop_sql = "DROP TABLE IF EXISTS `types`";
		
		$sql = "CREATE TABLE IF NOT EXISTS `types` (
				`id` int(11) NOT NULL auto_increment,
				`type` varchar(32) NOT NULL,
				PRIMARY KEY (`id`)
				) ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		
		$this->pdo->exec($drop_sql);
		$this->pdo->exec($sql);
	}
	
	private function createRestaurantsTypesTable(){
		$drop_sql = "DROP TABLE IF EXISTS `restaurantstypes`";
		
		$sql = "CREATE TABLE IF NOT EXISTS `restaurantstypes` (
				`rid` int(11) NOT NULL,
				`tid` int(11) NOT NULL,
				PRIMARY KEY (`rid`,`tid`)
				) ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		
		$this->pdo->exec($drop_sql);
		$this->pdo->exec($sql);
	}
	
	private function insertRestaurantsTypesData() {
		$insert_sql = "INSERT INTO `restaurantstypes` (`rid`, `tid`)
					VALUES
					(1, 15),
					(1, 12),
					(2, 12),
					(2, 25),
					(2, 20),
					(3, 19),
					(3, 30),
					(3, 11);";
		
		$this->pdo->exec($insert_sql);
	}
	
	public function insertTypesData() {
		$insert_sql = "INSERT INTO `types` (`id`,`type`)
				VALUES
				(NULL, 'Bakery'),
				(NULL, 'Barbecue'),
				(NULL, 'Bistro'),
				(NULL, 'Buffet'),
				(NULL, 'Café'),
				(NULL, 'Casual'),
				(NULL, 'Chicken'),
				(NULL, 'Chinese'),
				(NULL, 'Coffeehouse'),
				(NULL, 'Diner'),
				(NULL, 'Drive-in'),
				(NULL, 'Fast food'),
				(NULL, 'Fish'),
				(NULL, 'Grilled'),
				(NULL, 'Hamburger'),
				(NULL, 'Health food'),
				(NULL, 'Hot Dog'),
				(NULL, 'Ice cream'),
				(NULL, 'Indian'),
				(NULL, 'Italian'),
				(NULL, 'Japanese'),
				(NULL, 'Juice Bar'),
				(NULL, 'Mexican'),
				(NULL, 'Pizza'),
				(NULL, 'Pub'),
				(NULL, 'Sandwich'),
				(NULL, 'Seafood'),
				(NULL, 'Steak'),
				(NULL, 'Steakhouse'),
				(NULL, 'Take-out'),
				(NULL, 'Taverns'),
				(NULL, 'Thai'),
				(NULL, 'Vegetarian')
				;";
		$this->pdo->exec($insert_sql);			
	}
	
	public function insertInitialData() {
		$insert_sql = "INSERT INTO `restaurants` (`id`,`name`,`description`,`phone`,`lat`,`lng`,`created_at`, `pricegroup`)
			VALUES
			(1,'Burger Joint','Awesome burgers','8483756',64139078,-21936757,'2009-10-08 10:16:00', 3),
			(2,'Pizza Joint','The best Pizza Joint',NULL,64135603,-21954812,'2009-10-08 10:16:20', 2),
			(3,'Asian Joint','blabla',NULL,64136603,-21953812,'2009-10-08 10:16:20', 1);";		
		$this->pdo->exec($insert_sql);

		
		$insert_sql = "INSERT INTO ratings (restaurant_id, rating) VALUES
			(1, 5),
			(1, 5),
			(2, 4),
			(3, 3);";
		$this->pdo->exec($insert_sql);
		
	}

	public function selectAllTypes(){
		$items = array();
		
		$sql = "SELECT
				T.*
				FROM
				types T			
				";
		
		foreach ($this->pdo->query($sql, PDO::FETCH_ASSOC) as $row) {
			$items[] = $this->createType($row);
		}
		return $items;
		
	}

	public function selectAll() {
		$items = array();
		
		$sql = "SELECT 
				R.*, 
				FORMAT(AVG(ratings.rating),1) as rating, 
				COUNT(ratings.id) as rating_count,
				(
					SELECT GROUP_CONCAT(tid) as types FROM restaurantstypes X, types T
					WHERE X.rid = R.id
					AND X.tid = T.id
					GROUP BY X.rid
				) as types
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
	
	public function rate($id, $rating, $user_id) {
		
		$sql = "INSERT INTO ratings (restaurant_id, rating, user_id) VALUES (:rid, :rating, :uid)";
		$q = $this->pdo->prepare($sql);
		return $q->execute(array(':rid'=>$id, ':rating'=>$rating, ':uid'=>$user_id));
	}
	
	public function updateRate($rid, $rating, $uid) {
		
		$sql = "UPDATE ratings SET rating = " . $rating . " WHERE restaurant_id = " . $rid . " AND user_id = " . $uid . ";";
		$q = $this->pdo->prepare($sql);
		return $q->execute(array(':rid'=>$rid, ':rating'=>$rating, ':uid'=>$uid));
	}
	
	public function selectFromId($id) {
		
		$stmt = $this->pdo->prepare("
				SELECT 
				R.*, 
				FORMAT(AVG(ratings.rating),1) as rating, 
				COUNT(ratings.id) as rating_count,
				(
					SELECT GROUP_CONCAT(tid) as types FROM restaurantstypes X, types T
					WHERE X.rid = R.id
					AND X.tid = T.id
					GROUP BY X.rid
				) as types
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
	
	private function createType($row){
		$r = new Types();
		
		$r->setId($row['id']);
		$r->setTypes($row['type']);
		
		return $r;
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
		
		$r->setPricegroup($row['pricegroup']);
		$r->setTypes($row['types']);
		
		//$r->setModified($row['modified_at']);

		return $r;
	}
	
	
}