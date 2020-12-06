package com.example.demo.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Applicants;

public interface ApplicantsDao extends CrudRepository<Applicants, String>{

}
