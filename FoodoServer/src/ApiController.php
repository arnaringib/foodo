<?php

class ApiController {
	public function showAll() 
	{
		$db = new RestaurantDb();
		
		$items = array();
		$r = $db->selectAll();
		
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
}