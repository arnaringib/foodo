<?php
class Types{
	private $id;
	private $type;
	
	public function setId($id){
		$this->id = $id;	
	}
	
	public function getId(){
		return $this->id;
	}
	
	public function setTypes($type){
		$this->type = $type;		
	}
	
	public function getTypes(){
		return $this->type;
	}
	
	public function toArray(){
		return array(
			"id" => $this->getId() * 1,
			"type" => $this->getTypes()
		);
	}
}