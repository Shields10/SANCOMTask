package com.example.demo.dao;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Jobs;
	public interface JobDao extends JpaRepository<Jobs, String>{
		
		//List <User> findByUserEmail(String userEmail);
	}

