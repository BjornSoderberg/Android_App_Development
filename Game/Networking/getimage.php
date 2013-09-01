<?php
	require("config.inc.php");
	
	
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
		} while(!isset($row));
		
        die(json_encode($row));  
?>