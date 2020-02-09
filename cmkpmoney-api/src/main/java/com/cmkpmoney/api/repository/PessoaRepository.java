package com.cmkpmoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmkpmoney.api.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

}
