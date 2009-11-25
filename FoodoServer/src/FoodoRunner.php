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
				
				$db3 = new ReviewDb();
				$db3->createTables();
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
		
		try {

			//Reviews
			if (preg_match("/api\/reviews\/create/", self::$uri, $matches)) {
				$controller = new RestaurantController();
				
				//TODO validate user
				//throw new FoodoException("Bad user key");
				
				if (count($_POST) > 0 AND isset($_POST['user_id']) AND isset($_POST['restaurant_id']) AND isset($_POST['review'])) {
					$controller->createReview($_POST['restaurant_id'], $_POST['user_id'], $_POST['review']);
				}
				else {
					throw new FoodoException("Data missing for review");	
				}
			}
			elseif (preg_match("/api\/restaurant\/id\/([0-9]+)\/reviews/", self::$uri, $matches))
			{
				$controller = new RestaurantController();
				$controller->showReviewsFromId($matches[1]);
			}
			//Restaurants
			elseif (preg_match("/api\/restaurant\/id\/([0-9]+)\/rate\/([0-9].[0-9]|[0-9])\/user_id\/([0-9]+)/", self::$uri, $matches)) {
				$controller = new RestaurantController();
				$controller->rateRestaurant($matches[1], number_format($matches[2],1), $matches[3]);
			}
			elseif (preg_match("/api\/restaurant\/id\/([0-9]+)/", self::$uri, $matches))
			{
				$controller = new RestaurantController();
				$controller->showFromId($matches[1]);
			}
			elseif (preg_match("/api\/restaurant/", self::$uri, $matches)) {
				$controller = new RestaurantController();
				$controller->showAll();
			}
			//User
			elseif (preg_match("/api\/user\/login\/(.*)\/(.*)/", self::$uri, $matches)) {
				$controller = new UserController();
				$controller->login($matches[1], $matches[2]);
			}
			elseif (preg_match("/api\/user\/signup\/(.*)\/(.*)\/(.*)\/(.*)/", self::$uri, $matches)) {
				$controller = new UserController();
				$controller->signup($matches[1], $matches[2], $matches[3], $matches[4]);
			}
			//Types
			elseif (preg_match("/api\/types/", self::$uri, $matches)){
				$controller = new RestaurantController();
				$controller->showAllTypes();
			}
			else {
				echo json_encode(array(
					"responseData" => '',
					"responseDetails" => "Bad request",
					"responseCode" => 404
				));
			}
		}
		catch (FoodoException $e) {
			echo json_encode(array(
					"responseData" => "FoodoException",
					"responseDetails" => $e->getMessage(),
					"responseCode" => 500
			));
		}
		catch (Exception $e) {
			echo json_encode(array(
					"responseData" => 'Exception',
					"responseDetails" => $e->getMessage(),
					"responseCode" => 500
			));
		}
	}
	
	private static function resolveURI() {
		self::$uri = $_SERVER["REQUEST_URI"];
		$request = explode("/", self::$uri);
		
		self::$scope = isset($request[1]) ? $request[1] : null;
	}
	
}
