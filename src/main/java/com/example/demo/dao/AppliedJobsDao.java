package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ApppliedJobs;
import com.example.demo.model.User;

public interface AppliedJobsDao extends JpaRepository<ApppliedJobs, String>{
	List <ApppliedJobs> findByuserRelationshipNo(String userRelationshipNo);

}
