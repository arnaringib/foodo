<?php

class Review
{
	private $id;
	private $review;
	private $user_id;
	private $restaurant_id;

	private $created_at;
	
	public function setId($id)
	{
		$this->id = $id;
	}

	public function getId()
	{
		return $this->id;
	}

	public function setReview($review)
	{
		$this->review = $review;
	}

	public function getReview() {
		return $this->review;
	}
	
	public function setUserId($id) {
		$this->user_id = $id;
	}
	
	public function getUserId() {
		return $this->user_id;
	}
	
	public function setRestaurantId($id) {
		$this->restaurant_id = $id;
	}
	
	public function getRestaurantId() {
		return $this->restaurant_id;
	}
	
	public function setCreated($date) {
		$this->created_at = $date;
	}
	
	public function getCreatedAt() {
		return $this->created_at;
	}
	
	public function toArray() {
		return array(
			"id" => $this->getId() * 1,
			"review" => $this->getReview(),
			"user_id" => $this->getUserId(),
			"restaurant_id" => $this->getRestaurantId(),
			"created_at" => $this->getCreatedAt()
		);
	}

}