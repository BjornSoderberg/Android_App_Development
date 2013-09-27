<?php
	require("config.inc.php");
	
	if(!empty($_POST)) {
		 $query = " 
            SELECT 
                * 
            FROM games 
            WHERE 
                id = :id
			LIMIT 1
        "; 
         
        $query_params = array( 
            ':id' => $_POST['id'] 
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
		
		// If it is the player's turn, set success to 1
		if($row['game_state'] == $_POST['mIndex']) {
			
			$query = " 
				UPDATE games
				SET 
					play_time".$_POST['mIndex']." = CURRENT_TIMESTAMP
				WHERE id = :id
			"; 
			 
			$query_params = array( 
				':id' => $_POST['id'] 
			); 
			 
			try 
			{ 
				$stmt = $db->prepare($query); 
				$result = $stmt->execute($query_params); 
			} 
			catch(PDOException $ex) 
			{ 
				
				$response["success"] = 0;
				$response["message"] = "Database Error 11";
				die(json_encode($response).$ex->getMessage());  
			}
			
			$response["success"] = 1;
			$response["message"] = "You Can Play!";
            die(json_encode($response)); 
		} else {
			$response["success"] = 2;
			$response["message"] = "It is not your turn!";
			$response["game"] = $row;
            die(json_encode($response)); 
		}
	}
?>