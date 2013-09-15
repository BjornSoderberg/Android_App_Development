<?php
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
		if(sizeof($images) == 0) return false;
		for($i = 0; $i < sizeof($images); $i++) {
			if($row['name'] == $images[$i]['name']) return true;
		}
		
		return false;
	}
?>