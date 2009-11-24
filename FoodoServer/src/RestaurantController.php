<?php

class RestaurantController 
{
	private $db;
	private $review_db;
	
	public function __construct() {
		$this->db = new RestaurantDb();
		$this->review_db = new ReviewDb();
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
	
	public function showAllTypes(){
		$items = array();
		$r = $this->db->selectAllTypes();
		
		if(count($r) > 0 ){
			foreach($r as $item){
				$items[] = $item->toArray();
			}
		}
		
		$result = array(
			"responseData" => array("Types" => $items),
			"responseDetails" => "OK",
			"responseCode" => 200
		);
		echo json_encode($result);
	}
	
	public function rateRestaurant($id, $rating, $user_id)
	{
		$this->db->rate($id, $rating, $user_id);
		$this->showFromId($id);
	}
	
	public function updateRateRestaurant($rid, $rating, $uid)
	{
		$this->db->updateRate($rid, $rating, $uid);
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
	
	public function createReview($restaurant_id, $user_id, $review) {
		$review = $this->review_db->insertReview($restaurant_id, $user_id, $review);
	
		if ($review)
		{
			$result = array(
				"responseData" => array("review" => $review->toArray()),
				"responseDetails" => "OK",
				"responseCode" => 200
			);	
		}
		else {
			$result = array(
				"responseData" => "",
				"responseDetails" => "Could not create review",
				"responseCode" => 404
			);
		}
		echo json_encode($result);
	}
	
	public function showReviewsFromId($restaurant_id) {
		$restaurant = $this->db->selectFromId($restaurant_id);
		
		if ($restaurant)
		{
			$reviews = $this->review_db->selectFromRestaurantId($restaurant->getId());
			for ($i = 0; $i < count($reviews); $i++)
			{
				$reviews[$i] = $reviews[$i]->toArray();
			}
			
			$result = array(
				"responseData" => array(
					"reviews" => $reviews,
					"restaurant" => $restaurant->toArray()
				),
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