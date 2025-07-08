package com.spaceoperators.repository;

import com.spaceoperators.model.EQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EQuestionRepository extends JpaRepository<EQuestion, Long> {

}
