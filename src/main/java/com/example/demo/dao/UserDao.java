package com.example.demo.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.User;

public interface UserDao extends JpaRepository<User, String>{
	
	List <User> findByUserEmail(String userEmail);
}
