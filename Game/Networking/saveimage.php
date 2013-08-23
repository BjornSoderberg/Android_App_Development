<?php

	require("config.inc.php");
	
	//if(!empty($_POST)) {
		//$name = $_POST['name'];
		//$link = md5($name . rand(0, 1000);
		$name = "hello";
		$link = "testing";
		
		$query = "INSERT INTO images ( name, link ) VALUES ( :name, :link ) ";
		
		$query_params = array(
			':name' => $name,
			':link' => $link
		);
		
		try {
			$stmt   = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch (PDOException $e) {
			die("Error");
		}

		echo "added";
	//}

?>