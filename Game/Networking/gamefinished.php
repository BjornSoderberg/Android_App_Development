<?php
	require("config.inc.php");
	
	if(!empty($_POST)) {
	
		$mIndex = $_POST['index'];
		if($mIndex == 1) $oIndex = 2;
		else $oIndex = 1;
		
		$query = "
			SELECT *
			FROM games
			WHERE id = :id
		";
		
		$query_params = array(
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
		
		$row = $stmt->fetch();
		
		
		// Update
		$game_state = $row['game_state'];
		
		if($game_state != $mIndex) {
			$response["success"] = 0;
			$response["message"] = "There was an error when submitting the score!";
			die(json_encode($response));
		}
		
		if($row['game_score'.$oIndex] == -1) {
			$game_state = $oIndex;
			$query = "
				UPDATE games
				SET game_score" . $mIndex . " = :game_score".$mIndex.",
					game_state = :game_state,
					last_play_time = CURRENT_TIMESTAMP
				WHERE id = :id
			";
		
			$query_params = array(
				':game_score'.$mIndex => $_POST['score'],
				':game_state' => $game_state,
				':id' => $_POST['id']
			);
		}
		else {
			$game_state = $mIndex;
			
			if($_POST['score'] > $row['game_score'.$oIndex]) $s = "score".$mIndex." = score".$mIndex." + 1";
			if($_POST['score'] == $row['game_score'.$oIndex]) $s = "";
			if($_POST['score'] < $row['game_score'.$oIndex]) $s = "score".$oIndex." = score".$oIndex." + 1";
			
			$query = "
			UPDATE games
			SET game_score1 = -1,
				game_score2 = -1,
				game_state = :game_state,
				last_play_time = CURRENT_TIMESTAMP,
				".$s."
			WHERE id = :id
			";
			
			$query_params = array(
			':game_state' => $game_state,
			':id' => $_POST['id']
		);
		}
		
		
		
		
		
		try {
			$stmt = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch (PDOException $ex) {
			$response["success"] = 0;
			$response["message"] = $ex->getMessage();
            die(json_encode($response).$ex->getMessage()); 
		}
		
		
		
		
		// Get new information
		$query = "
			SELECT *
			FROM games
			WHERE id = :id
		";
		
		$query_params = array(
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
		
		$row = $stmt->fetch();
		
		
		$response["success"] = 1;
		$response["message"] = "Round successfully completed! You got " . $_POST['score'] ." points!";
		$response["game"] = $row;
		die(json_encode($response));
	
	
	
	}
?>