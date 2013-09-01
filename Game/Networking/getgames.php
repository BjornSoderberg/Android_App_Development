<?php
	require("config.inc.php");
	
	if(!empty($_POST)) {
		$query = "
			SELECT *
			FROM games
			WHERE username1 = :username
				OR username2 = :username
			ORDER BY last_play_time DESC
		";
	
		$query_params = array(
			':username' => $_POST['username']
		);
		
		try {
			$stmt = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch (PDOException $ex) {
			$response["success"] = 0;
			$response["message"] = "Database Error 1. Please Try Again!";
            die(json_encode($response).$ex->getMessage()); 
		}
		
		$rows = $stmt->fetchAll();
		
		
		$response["success"] = 1;
		$response["message"] = count($rows) . " Games Found!";
		$response["number_of_games"] = count($rows);
		
		if($rows) {
			for ($i = 0; $i < count($rows); $i++) {
				$response["game".$i] = $rows[$i];
			}
		}
		
        die(json_encode($response));
	
	
	
	}
?>