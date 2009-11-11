<?php

class FoodoRunner {

	public static $scope;
	public static $uri;
	
	public static function run() {
		self::resolveURI();
		
		switch(self::$scope)
		{
			case "setup":
				$db = new RestaurantDb();
				$db->createTables();
				$db->insertInitialData();
				
				$db2 = new UserDb();
				$db2->createTables();
				$db2->insertInitialData();
				break;
			case "api":
				self::runApi();
				break;
			default:
				echo "<h1>Foodo</h1>";
				break;
		}
	}
	
	private static function runApi() {
		
		if (preg_match("/api\/restaurant\/id\/([0-9]+)\/rate\/([0-9].[0-9]|[0-9])/", self::$uri, $matches)) {
			$controller = new RestaurantController();
			$controller->rateRestaurant($matches[1], number_format($matches[2],1));
		}
		elseif (preg_match("/api\/restaurant\/id\/([0-9]+)/", self::$uri, $matches))
		{
			$controller = new RestaurantController();
			$controller->showFromId($matches[1]);
		}
		elseif (preg_match("/api\/user\/login\/(.*)\/(.*)/", self::$uri, $matches)) {
			$controller = new UserController();
			$controller->login($matches[1], $matches[2]);
		}
		elseif (preg_match("/api\/user\/signup\/(.*)\/(.*)/", self::$uri, $matches)) {
			$controller = new UserController();
			$controller->signup($matches[1], $matches[2]);
		}
		else {
			echo json_encode(array(
				"responseData" => '',
				"responseDetails" => "Bad request",
				"responseCode" => 404
			));
		}
	}
	
	private static function resolveURI() {
		self::$uri = $_SERVER["REQUEST_URI"];
		$request = explode("/", self::$uri);
		
		self::$scope = isset($request[1]) ? $request[1] : null;
	}
	
}