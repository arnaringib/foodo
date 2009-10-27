<?php
header('Content-Type: text/plain; charset=UTF-8');

mysql_connect("host", "user", "password") or die(mysql_error());
mysql_select_db("db") or die(mysql_error());

if (file_exists('restaurantsWithGps.xml')) {
    $xml = simplexml_load_file('restaurantsWithGps.xml');
	
 	foreach($xml->restaurant as $i) {
echo $i->title;
		mysql_query("INSERT INTO restaurants
		(name, address, zip, city, phone, website, email, img, lat, longt)       
		 VALUES('$i->title','$i->address',$i->zip, '$i->city', '$i->phone', '$i->website','$i->email','$i->img',$i->lat,$i->long);")   
		or die(mysql_error()); 
	}
} else {
    exit('Failed to open restaurantsWithGps.xml.');
	
}


?>
