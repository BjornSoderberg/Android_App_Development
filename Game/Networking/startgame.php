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
				images
			) VALUES (
				:username1,
				:username2,
				:images
			)
		";	
		
		// Gets five random games. Here, images 
		// must be chosen based on earlier 
		// games and images
		
		// 5 is number of rounds
		$images = array();
		for($i = 0;  $i < 5; $i++) {
			$row = get_image_word_and_link($db, $images);
			$images['round'.$i] = $row;
		}	
		
		$query_params = array(
			':username1' => $_POST['username1'],
			':username2' => $_POST['username2'],
			':images' => json_encode($images)
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
	
	// Function copy and pasted from getimage.php
	function get_image_word_and_link($db, $images) {
		$query = " 
            SELECT id 
            FROM images
        "; 
         
        $query_params = array( 
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
		
		$rows = $stmt->fetchAll();
		$length = count($rows);
	
		do {
		
			$query = " 
				SELECT *
				FROM images 
				WHERE 
					id = :id
			"; 
			 
			$query_params = array( 
				':id' => rand(0, $length)
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
		} while($row == false && !same_words($row, $images));
		
		return $row;
	}
	
	// Should make it impossible for the same word to be used twice in the same game
	function same_words($row, $images) {
		if(sizeof($images) === 0) return false;
		for($i = 0; $i < sizeof($images); $i++) {
			if($row['name'] === $images[$i]['name']) return true;
		}
		
		return false;
	}
?>