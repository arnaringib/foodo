<?php

class RestaurantController 
{
	private $db;
	
	public function __construct() {
		$this->db = new RestaurantDb();
	}
	public function showAll() 
	{
		
		$items = array();
		$r = $this->db->selectAll();
		
		if (count($r) > 0)
		{
			foreach ($r as $item)
			{	
				$items[] = $item->toArray();
			}
		}
		
		
		$result = array(
			"responseData" => array("Restaurants" => $items),
			"responseDetails" => "OK",
			"responseCode" => 200
		);
		echo json_encode($result);
	}
	
	public function rateRestaurant($id, $rating)
	{
		$this->db->rate($id, $rating);
		$this->showFromId($id);
	}
	
	public function showFromId($id) {
		$r = $this->db->selectFromId(intval($id));
		
		if ($r)
		{
			$result = array(
				"responseData" => array("Restaurants" => $r->toArray()),
				"responseDetails" => "OK",
				"responseCode" => 200
			);
		}
		else {
			$result = array(
				"responseData" => "",
				"responseDetails" => "Restaurant not found",
				"responseCode" => 404
			);
		}
		echo json_encode($result);
	}
}