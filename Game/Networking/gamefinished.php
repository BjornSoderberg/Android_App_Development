<?php
	require("config.inc.php");
	require("getimage.php");
	
	if(!empty($_POST)) {
	
		$mIndex = $_POST['index'];
		if($mIndex == 1) $oIndex = 2;
		else $oIndex = 1;
		
		if(isset($_POST['score'])) $score = $_POST['score'];
		else $score = 0;
		
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
		
		if(!$row) {
			$response["success"] = 0;
			$response["message"] = "No data fetched";
            die(json_encode($response)); 
		}
		
		// Update
		$game_state = $row['game_state'];
		$play_time = $row['play_time'.$mIndex];
		
		// if it has passed more than 6 minutes since the game was started, the score is set to 0
		if(strtotime($play_time) + 6 * 60 < strtotime("now")) {
			$score = 0;
		}
		
		if($game_state != $mIndex) {
			$response["success"] = 0;
			$response["message"] = "There was an error when submitting the score!";
			die(json_encode($response));
		}
		
		// if the score of the opponent is not set, 
		//update the player's score and finish the turn
		if($row['game_score'.$oIndex] == -1) {
			$game_state = $oIndex;
			$query = "
				UPDATE games
				SET game_score" . $mIndex . " = :game_score".$mIndex.",
					game_state = :game_state,
					last_play_time = CURRENT_TIMESTAMP,
					play_time".$mIndex." = 0
				WHERE id = :id
			";
		
			$query_params = array(
				':game_score'.$mIndex => $score,
				':game_state' => $game_state,
				':id' => $_POST['id']
			);
		}
		// if the opponents score is set, start a new round
		// the turn is not finished
		else {
			$game_state = $mIndex;
			
			// s contains the string sent into the query
			// e.g. "score1 = score1 + 1
			// one is added to the winning player's score
			// the ', ' in the beginning ends the line before
			if($_POST['score'] > $row['game_score'.$oIndex]) $s = ", score".$mIndex." = score".$mIndex." + 1";
			if($_POST['score'] == $row['game_score'.$oIndex]) $s = "";
			if($_POST['score'] < $row['game_score'.$oIndex]) $s = ", score".$oIndex." = score".$oIndex." + 1";
			
			// this updates the images
			$image = array();
			for($i = 0;  $i < 5; $i++) {
				$image_row = get_image_word_and_link($db, $images);
				$images['round'.$i] = $image_row;
			}
			
			$query = "
				UPDATE games
				SET 
					prev_game_score".$oIndex." = game_score".$oIndex.",
					prev_game_score".$mIndex." = ".$_POST['score'].",
					game_score1 = -1,
					game_score2 = -1,
					game_state = :game_state,
					images = :images,
					last_play_time = CURRENT_TIMESTAMP,
					play_time".$mIndex." = 0
					".$s."
				WHERE id = :id
			";
			
			$query_params = array(
				':game_state' => $game_state,
				':images' => json_encode($images),
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