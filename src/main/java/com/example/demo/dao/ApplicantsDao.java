package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Applicants;

public interface ApplicantsDao extends JpaRepository<Applicants, String>{

	
	List <Applicants> findByuserRelationshipNo(String userRelationshioNo);
}
