<?php

class Restaurant
{

	private $id;
	private $name;
	private $description;
	private $rating;
	private $phone;
	private $lat;
	private $lng;
	private $created_at;
	private $modified_at;

	public function setId($id)
	{
		$this->id = $id;
	}

	public function getId()
	{
		return $this->id;
	}

	public function setName($name)
	{
		$this->name = $name;
	}

	public function getName()
	{
		return $this->name;
	}

	public function setDescription($description)
	{
		$this->description = $description;
	}

	public function getDescription()
	{
		return $this->description;
	}

	public function setRating($rating)
	{
		$this->rating = $rating;
	}

	public function getRating()
	{
		return $this->rating;
	}

	public function setPhone($phone)
	{
		$this->phone = $phone;
	}

	public function getPhone()
	{
		return $this->phone;
	}

	public function setLat($lat)
	{
		$this->lat = $lat;
	}

	public function getLat()
	{
		return $this->lat;
	}

	public function setLng($lng)
	{
		$this->lng = $lng;
	}

	public function getLng()
	{
		return $this->lng;
	}

	public function setCreated($created_at)
	{
		$this->created_at = $created_at;
	}

	public function getCreatedt()
	{
		return $this->created_at;
	}

	public function setModified($modified_at)
	{
		$this->modified_at = $modified_at;
	}

	public function getModified()
	{
		return $this->modified_at;
	}
	
	public function toArray() {
		return array(
			"id" => $this->getId() * 1,
			"name" => $this->getName(),
			"rating" => number_format($this->getRating(), 1),
			"lat" => $this->getLat(),
			"lng" => $this->getLng()
		);
	}

}