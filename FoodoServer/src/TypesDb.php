<?php
class TypesDb{
	
	/**
	 * 
	 * @var FoodoPDO
	 */
	private $pdo;
	
	public function __construct() {	
		$this->pdo = FoodoPDO::getInstance();
	}
	
	public function selectAllTypes(){
		$items = array();
		
		$sql = "SELECT
				T.*
				FROM
				types T			
				";
		
		foreach ($this->pdo->query($sql, PDO::FETCH_ASSOC) as $row) {
			$items[] = $this->createRestaurant($row);
		}
		return $items;
		
	}
	
	private function createType($row){
		$r = new Restaurant();
		
		$r->set
	}
	
}