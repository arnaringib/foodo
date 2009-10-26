<?php

class ApiController {
	public function showAll() 
	{
		$db = new RestaurantDb();
		
		$items = array();
		foreach ($db->selectAll() as $item)
		{	
			$items[] = $item->toArray();
		}
		
		/*
		 
		 {"responseData":{"Restaurants":[{"id":1,"name":"Burger Joint","lat":64139078,"lng":-21936757,"rating":"5.0"},{"id":2,"name":"Pizza Joint","lat":64135603,"lng":-21954812,"rating":"4.0"},{"id":3,"name":"Asian Joint","lat":64136603,"lng":-21953812,"rating":"3.0"}]},"responseDetails":"OK","responseStatus":"200"} 
		 
		 */
		$result = array(
			"responseData" => array("Restaurants" => $items),
			"responseDetails" => "OK",
			"responseCode" => 200
		);
		echo json_encode($result);
	}
}