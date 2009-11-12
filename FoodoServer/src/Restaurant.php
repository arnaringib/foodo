<?php

class Restaurant
{

	private $id;
	private $name;
	private $description;
	private $rating;
	private $rating_count;
	private $phone;
	private $lat;
	private $lng;
	private $created_at;
	private $modified_at;
	
	private $address;
	private $zip;
	private $city;
	private $website;
	private $email;
	
	private $pricegroup;
	private $types = array();
	
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
	
	public function setRatingCount($count)
	{
		$this->rating_count = $count;
	}

	public function getRatingCount()
	{
		return $this->rating_count;
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
	
	public function setAddress($address)
	{
		$this->address = $address;
	}
	
	public function getAddress() {
		return $this->address;
	}
	
	public function setZip($zip)
	{
		$this->zip = $zip;
	}
	
	public function getZip() {
		return $this->zip;
	}
	
	public function setCity($city) {
		$this->city = $city;
	}
	
	public function getCity() {
		return $this->city;
	}
	
	public function setWebsite($website) {
		$this->website = $website;
	}
	
	public function getWebsite() {
		return $this->website;
	}
	
	public function setEmail($email) {
		$this->email = $email;
	}
	
	public function getEmail() {
		return $this->email;
	}
	
	public function setPricegroup($group) {
		$this->pricegroup = $group;
	}
	
	public function getPricegroup() {
		return $this->pricegroup;
	}
	
	/**
	 * 
	 * @param $types String
	 * @return void
	 */
	public function setTypes($types) {
		$this->types = explode(",",$types);
	}
	
	/**
	 * @return array
	 */
	public function getTypes() {
		return $this->types;
	}
	
	public function toArray() {
		return array(
			"id" => $this->getId() * 1,
			"name" => $this->getName(),
			"rating" => number_format($this->getRating(), 1),
			"rating_count" => $this->getRatingCount(),
			"lat" => $this->getLat(),
			"lng" => $this->getLng(),
			"phone" => $this->getPhone(),
			"address" => $this->getAddress(),
			"zip" => $this->getZip(),
			"city" => $this->getCity(),
			"website" => $this->getWebsite(),
			"email" => $this->getEmail(),
			"pricegroup" => $this->getPricegroup(),
			"types" => $this->getTypes()
		);
	}

}