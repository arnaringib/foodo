<?php

class User {
	
	private $id;
	private $email;
	private $apikey;
	
	public function setId($id) {
		$this->id = $id;
	}
	
	public function getId() {
		return $this->id;
	}
	
	public function setEmail($email) {
		$this->email = $email;
	}
	
	public function getEmail() {
		return $this->email;
	}
	
	public function setApiKey($key) {
		$this->key = $key;
	}
	
	public function getApiKey() {
		return $this->key;
	}
	
	public function toArray() {
		return array(
			"id" => $this->getId(),
			"email" => $this->getEmail(),
			"apikey" => $this->getApiKey()
		);
	}
}