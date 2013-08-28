<?php
	require("config.inc.php");
	
	if(!empty($_POST)) {
	
	$score = 'score'.$_POST['index'];
	
	// :score will be "score" + mIndex
		$query = "
			UPDATE games
			SET score" .$_POST['index']. " = :score, last_play_time = CURRENT_TIMESTAMP
			WHERE id = :id
			LIMIT 1
		";
		
		$query_params = array(
			':score' => $_POST['score'] . $_POST['index'],
			':id' => $_POST['id']
		);
		
		try {
			$stmt = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch (PDOException $ex) {
			$response["success"] = 0;
			$response["message"] = $ex->getMessage();
            die(json_encode($response).$ex->getMessage()); 
		}
		
		$rows = $stmt->fetchAll();
		
		
		$response["success"] = 1;
		$response["message"] = "Round successfully completed! You got " . $_POST['score'] ." points!";
		die(json_encode($response));
	
	
	
	}
?>