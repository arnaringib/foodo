<?php

class UserDb {
	
	/**
	 * @var FoodoPDO
	 */
	private $pdo;
	
	public function __construct() {	
		$this->pdo = FoodoPDO::getInstance();
	}
	
	public function createTables() {		
		$this->createUserTable();
	}
	
	private function createUserTable() {
		$drop_sql = "DROP TABLE IF EXISTS users";
		
		$sql = "CREATE TABLE `users` (
				`id` INT(11) NOT NULL AUTO_INCREMENT ,
				'firstName' VARCHAR(40) NOT NULL,
				'lastName' VARCHAR(40) NOT NULL,
				`email` VARCHAR(255) NOT NULL ,
				`password` VARCHAR(128) NOT NULL ,
				`apikey` CHAR(40) NOT NULL ,
				PRIMARY KEY (`id`)
				) ENGINE = MYISAM";
		
		$this->pdo->exec($drop_sql);
		$this->pdo->exec($sql);
	}
	
	public function insertInitialData() {
		$insert_sql = "INSERT INTO `users` (`email`,`password`, `apikey`)
			VALUES
			('sij16@hi.is', '".SHA1("test")."', '".SHA1(MD5(RAND() . time()))."');";		
		$this->pdo->exec($insert_sql);
		
	}
	
	public function selectAll() {
		$items = array();
		
		$sql = "SELECT * FROM sers";  
		
		foreach ($this->pdo->query($sql, PDO::FETCH_ASSOC) as $row) {
			$items[] = $this->createUser($row);
		}
		return $items;
	}
	
	public function addUser($firstName, $lastName, $email, $password) {
		$apikey = SHA1($email . $password . RAND() . time());
		$stmt = $this->pdo->prepare("INSERT INTO `users` (`firstName`, `lastName`, `email`,`password`, `apikey`) VALUES (?, ?, ?, SHA1(?), ?)");
		
		if ($stmt->execute(array($firstName, $lastName, $email, $password, $apikey))) {
			return $this->pdo->lastInsertId();
		}
		else {
			return 0;
		}
	}
	
	/**
	 * 
	 * @param $email email we want to check if exists
	 * @return boolean
	 */
	public function userExists($email) {
		$stmt = $this->pdo->prepare("SELECT email FROM users WHERE email=?");
		$stmt->execute(array($email));
		$r = $stmt->fetch(PDO::FETCH_ASSOC);
		if ($r)
			return true;
		else
			return false;
	}
	
	/**
	 * 
	 * @param $email
	 * @param $password
	 * @return User
	 */
	public function getFromEmailPassword($email, $password) {
		$stmt = $this->pdo->prepare("SELECT id, email, apikey FROM users WHERE email=? AND password=?");
		$stmt->execute(array($email, $password));
		$r = $stmt->fetch(PDO::FETCH_ASSOC);
		
		if ($r) {
			return $this->createUser($r);
		}
		else {
			return false;
		}
	}
	
	public function selectFromId($id) {
		
		$stmt = $this->pdo->prepare("SELECT id, email, apikey FROM users WHERE id=?");
		$stmt->execute(array($id));
		$r = $stmt->fetch(PDO::FETCH_ASSOC);
		
		if ($r)
		{
			return $this->createUser($r);
		}
		else {
			return false;
		}
	}
	
	private function createUser($row) {
		$u = new User();
		
		$u->setId($row['id']);
		$u->setFirstName($row['firstName']);
		$u->setLastName($row['lastName']);
		$u->setEmail($row['email']);
		$u->setApiKey($row['apikey']);
		
		return $u;
	}
	
}