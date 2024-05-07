package com.example.repository;

import com.example.entity.HashEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HashRepository extends CrudRepository<HashEntity , Long> {
}
