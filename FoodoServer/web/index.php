<?php
	require('../conf/init.php');
	
	if ($_GET['setup']) {
		$rdb = new RestaurantDb();
		$rdb->createTable();
		$rdb->insertInitialData();
	}
	else {
		$controller = new ApiController();
		$controller->showAll();
	}