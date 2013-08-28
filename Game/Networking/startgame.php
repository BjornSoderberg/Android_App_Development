<?php
	require("config.inc.php");
	
	if(!empty($_POST)) {
		
		$query = " 
            SELECT 
                1 
            FROM users 
            WHERE 
                username = :username
        "; 
         
        $query_params = array( 
            ':username' => $_POST['username1'] 
        ); 
         
        try 
        { 
            $stmt = $db->prepare($query); 
            $result = $stmt->execute($query_params); 
        } 
        catch(PDOException $ex) 
        { 
            
            $response["success"] = 0;
			$response["message"] = "Database Error 1";
            die(json_encode($response).$ex->getMessage());  
        } 
         
        $row = $stmt->fetch(); 
         
        if(!$row) 
        { 
            
            $response["success"] = 0;
			$response["message"] = "Database Error 2";
            die(json_encode($response)); 
        } 
		
		$query = " 
            SELECT 
                1 
            FROM users 
            WHERE 
                username = :username
        "; 
         
        $query_params = array( 
            ':username' => $_POST['username2'] 
        ); 
         
        try 
        { 
            $stmt = $db->prepare($query); 
            $result = $stmt->execute($query_params); 
        } 
        catch(PDOException $ex) 
        { 
            
            $response["success"] = 0;
			$response["message"] = "Database Error 3";
            die(json_encode($response).$ex->getMessage());  
        } 
         
        $row = $stmt->fetch(); 
         
        if(!$row) 
        { 
            
            $response["success"] = 0;
			$response["message"] = "The second username does not exist!";
            die(json_encode($response)); 
        } 
		
		
		$query = "
			INSERT IGNORE INTO games (
				username1,
				username2,
				game_state,
				turn1,
				turn2,
				score1,
				score2
			) VALUES (
				:username1,
				:username2,
				:game_state,
				:turn1,
				:turn2,
				:score1,
				:score2
			)
		
		";
		
		
		
		
		
		$query_params = array(
			':username1' => $_POST['username1'],
			':username2' => $_POST['username2'],
			':game_state' => 0,
			':turn1' => 0,
			':turn2' => 0,
			':score1' => 0,
			':score2' => 0
		);
		
		try {
			$stmt = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch (PDOException $e) {
			$response["success"] = 0;
			$response["message"] = "Database Error 1";
            die(json_encode($response).$e->getMessage()); 
		}
		
			
		$response["success"] = 1;
		$response["message"] = "You have successfully created a game!";
        die(json_encode($response)); 
	}
?>