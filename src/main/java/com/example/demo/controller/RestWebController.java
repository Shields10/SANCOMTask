package com.example.demo.controller;


import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.UserDao;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;



@RestController
public class RestWebController {
	@Autowired
	private UserDao  userDao;
	
	@PostMapping("restapi/login")
	public HashMap<String, String> loginVerification(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {

		  System.out.println(payload);
		   String password= payload.get("password").toString();
		   String userEmail= payload.get("userEmail").toString();
		   System.out.println("password "+password+"userEmail"+userEmail);

		  HashMap<String, String> response = new HashMap<>();
			List<User> userDetails = userDao.findByUserEmail(userEmail);
			 System.out.println("userDetails"+userDetails.toString());
			  String passwordFromDb=null; String relNo=null;
			 for (int i = 0; i < userDetails.size(); i++) {
				     passwordFromDb = userDetails.get(i).getPassword();
				     relNo = userDetails.get(i).getUserRelationshipNo();

				}
			 
			if (userDetails.size()>0) {
			// compare password 
				if (password.equals(passwordFromDb)){
					response.put("error", "false");
					response.put("relno", relNo);
				}else {
					response.put("error", "incorrect");
				}
			}else {
				response.put("error", "userdoesnotexist");
			}
			
			System.out.println("Response is"+response.toString());
			return response;
		  
		}
	
	
	
	
	

	
	
	
/*
	@PostMapping("/restapi/login")
   public ResponseEntity<?> getSearchResultViaAjax( @RequestBody LoginForm loginForm, Errors errors) {


        Response result = new Response();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {

            result.setMsg(errors.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);

        }

        List<User> users = userService.login(loginForm);
        System.out.println("users "+users);
        
     
      
        if (users.isEmpty()) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        result.setResult(users);

        System.out.println("result is"+result.getMsg());
        return ResponseEntity.ok(result);
*/
    }
	
	

