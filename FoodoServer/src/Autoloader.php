<?php

class Autoloader {

	public function autoload($name)
	{
		include(APPLICATION_PATH . $name . '.php');
	} 
	
}