<?php

class ReviewDb {

	/**
	 * 
	 * @var FoodoPDO
	 */
	private $pdo;
	
	public function __construct() {	
		$this->pdo = FoodoPDO::getInstance();
	}
	
	public function createTables() {	
		$this->createReviewTable();
	}
	
	private function createReviewTable() {
		$drop_sql = "DROP TABLE IF EXISTS reviews";
		
		$sql = "CREATE TABLE `reviews` (
				`id` INT(11) NOT NULL AUTO_INCREMENT ,
				`restaurant_id` INT(11) NOT NULL ,
				`review` TEXT NOT NULL ,
				`user_id` INT(11) NOT NULL,
				`created_at` TIMESTAMP DEFAULT NOW(),
				PRIMARY KEY (`id`)
				) ENGINE = MYISAM";
		
		$this->pdo->exec($drop_sql);
		$this->pdo->exec($sql);
	}

	public function selectAll() {
		$items = array();
		
		$sql = "SELECT * FROM reviews";
		
		foreach ($this->pdo->query($sql, PDO::FETCH_ASSOC) as $row) {
			$items[] = $this->createReview($row);
		}
		return $items;
	}
	
	public function selectFromId($id) {
		
		$stmt = $this->pdo->prepare("SELECT * FROM reviews WHERE id=?");
		
		$stmt->execute(array($id));
		$r = $stmt->fetch(PDO::FETCH_ASSOC);
		
		if ($r)
		{
			return $this->createReview($r);
		}
		else {
			return false;
		}
	}
	
	public function selectFromRestaurantId($id) {
	$stmt = $this->pdo->prepare("SELECT * FROM reviews WHERE restaurant_id=? ORDER BY created_at DESC");
		
		$stmt->execute(array($id));
		$r = $stmt->fetchAll(PDO::FETCH_ASSOC);
		
		$items = array();
		foreach ($r as $row)
		{
			$items[] = $this->createReview($row);
		}
		
		return $items;
	}
	
	public function insertReview($restaurant_id, $user_id, $review) {
		$sql = "INSERT INTO reviews (restaurant_id, user_id, review) VALUES (:rid, :uid, :review)";
		$q = $this->pdo->prepare($sql);
			
		if ($q->execute(array(':rid'=>$restaurant_id, ':uid'=>$user_id, ':review'=>$review))) {
			return $this->selectFromId($this->pdo->lastInsertId());
		}
		else {
			return 0;
		}
	}
	
	private function createReview($row) {
		$r = new Review();
		
		$r->setId($row['id']);
		$r->setReview($row['review']);
		$r->setRestaurantId($row['restaurant_id']);
		$r->setUserId($row['user_id']);
		$r->setCreated($row['created_at']);
		
		return $r;
	}
	
	
}