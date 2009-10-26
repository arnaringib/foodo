<?php

class FoodoRunner {

	public static $scope;
	public static $uri;
	
	public static function run() {
		self::resolveURI();
		
		switch(self::$scope)
		{
			case "api":
				self::runApi();
				break;
			default:
				echo "<h1>Foodo</h1>";
				break;
		}
	}
	
	private static function runApi() {
		
		$controller = new RestaurantController();
		
		if (preg_match("/api\/restaurant\/id\/([0-9]+)\/rate\/([0-9].[0-9]|[0-9])/", self::$uri, $matches)) {
			$controller->rateRestaurant($matches[1], number_format($matches[2],1));
		}
		elseif (preg_match("/api\/restaurant\/id\/([0-9]+)/", self::$uri, $matches))
		{
			$controller->showFromId($matches[1]);
		}
		else {
			$controller->showAll();	
		}
	}
	
	private static function resolveURI() {
		self::$uri = $_SERVER["REQUEST_URI"];
		$request = explode("/", self::$uri);
		
		self::$scope = isset($request[1]) ? $request[1] : null;
	}
	
}