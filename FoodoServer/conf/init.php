<?php
error_reporting(E_ALL); 
ini_set("display_errors", 1);

defined('APPLICATION_PATH') || define('APPLICATION_PATH', realpath(dirname(__FILE__) . '/../src'));

function my_autoload($name) {
	include(APPLICATION_PATH . "/" . $name . '.php');
}
spl_autoload_register("my_autoload");
