package com.ayushya.spring.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayushya.spring.bean.TechFeedback;
import com.ayushya.spring.repository.TechnicianFeedbackRepository;

@RestController
@RequestMapping("/feedback/customer")
public class TechFeedbackController {
	
	@Autowired
	private TechnicianFeedbackRepository repository;
	
	@PostMapping(value = "/{ticket_number}")
	public TechFeedback saveTechFeedBack(@PathVariable String ticket_number, @Valid @RequestBody TechFeedback techFeedback) {
		return repository.save(techFeedback);
	}

}
