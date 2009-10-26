<?php
	require('../conf/init.php');

	if ($_GET['setup']) {
		$rdb = new RestaurantDb();
		$rdb->createTables();
		$rdb->insertInitialData();
	}
	else {
		$controller = new ApiController();
		$controller->showAll();
	}