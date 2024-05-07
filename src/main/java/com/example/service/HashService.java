package com.example.service;

import com.example.entity.EmployeeEntity;
import com.example.entity.HashEntity;
import com.example.repository.HashRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HashService {

    @Autowired
    private HashRepository hashRepository;


    @Autowired
    private HashCodeGenerator hashCodeGenerator;

    @Autowired
    private RestTemplate restTemplate;


    public HashEntity saveHash(String hash)
    {
        String hashKey = "test";
        HashEntity hashEntity = HashEntity.builder().hashData(hash).hashKey(hashKey).build();
      return  hashRepository.save(hashEntity);
    }

    public ResponseEntity<String> setRestTemplate(EmployeeEntity employeeEntity) {
        String data = employeeEntity.getEmployeeName() + employeeEntity.getEmpEmail() +
                employeeEntity.getEmpPan() + employeeEntity.getEmpAadhar() +
                employeeEntity.getEmpDesignation() + employeeEntity.getEmpFirstName() +
                employeeEntity.getEmpLastName() + employeeEntity.getZip();
        String hash = hashCodeGenerator.createHash(data.getBytes());
        HashEntity hashEntity = saveHash(hash);
//        employeeEntity.setEmployeeName("Sam");
        employeeEntity.setRequestId(hashEntity.getId());
        String url = "http://localhost:8080/api/cache/save";
        ResponseEntity<String> response = restTemplate.postForEntity(url,employeeEntity, String.class);
        return  response;
    }
}
