package com.example.service;


import com.example.entity.EmployeeEntity;
import com.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmpService {
    @Autowired
    private EmployeeRepository repository;


    @Autowired
    private RestTemplate restTemplate;

//    @CachePut(cacheNames = "employeCache", key = "#entity.id")
    public EmployeeEntity save(EmployeeEntity entity) {
        return repository.save(entity);
    }


}
