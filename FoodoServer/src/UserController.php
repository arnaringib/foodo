<?php

/*
 * /api/user/signup/email/pass < returns key
 * /api/user/login/email/pass < returns key
 * 
 */

class UserController 
{
	private $db;
	
	public function __construct() {
		$this->db = new UserDb();
	}
	
	public function login($email, $password) 
	{
		$u = $this->db->getFromEmailPassword($email, SHA1($password)); 
		if ($u) {
			$result = array(
				"responseData" => array("User" => $u->toArray()),
				"responseDetails" => "OK",
				"responseCode" => 200
			);	
		}
		else {
			$result = array(
				"responseData" => array("Error"=>"Bad login"),
				"responseDetails" => "OK",
				"responseCode" => 404
			);
		}
		
		echo json_encode($result);
	}
	
	public function signup($firstName, $lastName, $email, $password) {
		if ($this->db->userExists($email))
		{
			$result = array(
				"responseData" => array("Error"=>"User exsists"),
				"responseDetails" => "User error",
				"responseCode" => 404
			);
		}
		else {
			$id = $this->db->addUser($firstName, $lastName, $email, $password);
			if ($id) {
				$user = $this->db->selectFromId($id);
				$result = array(
					"responseData" => array("User" => $user->toArray()),
					"responseDetails" => "User created",
					"responseCode" => 200
				);	
			}
			else {
				$result = array(
					"responseData" => array("Error"=>"Error creating user"),
					"responseDetails" => "User error",
					"responseCode" => 404
				);	
			}
		}
		echo json_encode($result);
	}
	
}