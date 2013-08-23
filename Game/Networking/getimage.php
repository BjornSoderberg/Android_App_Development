<?php
	// Connect to MySQL
	$connect = mysql_connect("localhost", "root", "david") or die("Error connecting to database");
	
	// Select database
	mysql_select_db("android");
	
	
	$query = "
		SELECT name FROM images
	";
	
	$result = mysql_query($query);
	
	$length = mysql_num_rows($result);
	
	do {
		// Random index in database
		$index = rand(0, $length);
		
		$query = "
			SELECT name, link FROM images WHERE id = $index
		";
		
		$result = mysql_query($query);

		while($row = mysql_fetch_array($result)) {
			if(isset($row)) {
				$output[] = $row;
			}
		}
	} while(!isset($output));
	
	print(json_encode($output));
	mysql_close();






?>