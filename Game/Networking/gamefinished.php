<?php
	require("config.inc.php");
	
	if(!empty($_POST)) {
	
	$score = 'score'.$_POST['index'].'_';
	$round = -1;
	
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
		
		// 5 is the number of games in a match
		for($i = 0; $i < 5; $i++) {
		// score is declared at the top. It is score1_ or score 2_
			if($row[$score.$i] == -1) {
				$round = $i;
				break;
			}
		}
		
		// Checks if the round is available (by comparing to what rounds the opponent has played)
		if($_POST['index'] == 1) $index = 2;
		else $index = 1;
		
		$score2 = "score".$index."_";
		$round2 = -1;
		
		for($i = 0; $i < 5; $i++) {
		// score is declared at the top. It is score1_ or score 2_
			if($row[$score2.$i] == -1) {
				$round2 = $i;
				break;
			}
		}
		
		if($round2 + 1 < $round) {
			$response["success"] = 0;
			$response["message"] = "This round is currently unavailable!";
            die(json_encode($response)); 
		}
		
	
		// Should also check if the previous score is not zero
		$query = "
			UPDATE games
			SET " . $score . $round . " = :score, last_play_time = CURRENT_TIMESTAMP
			WHERE id = :id
		";
		
		$query_params = array(
			':score' => $_POST['score'],
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