package com.cmkpmoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.cmkpmoney.api.model.Lancamento;
import com.cmkpmoney.api.model.Lancamento_;
import com.cmkpmoney.api.repository.filter.LancamentoFilter;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<Lancamento> filtrar(LancamentoFilter filter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);

		Root<Lancamento> root = criteria.from(Lancamento.class);//Quem faz os filtros

		// Criar as restrições
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		adcionarRestricoesDePaginacao(query, pageable);	
		
		return new PageImpl<>(query.getResultList(), pageable, total(filter)) ;
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) 
			predicates.add(builder.like(//Equivalente ao like em Sql, select * from tabela where descrição like "%descrisão%"
					builder.lower(root.get(Lancamento_.descricao)), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));

		if (lancamentoFilter.getDataVencimentoDe() != null)
			predicates.add(//tudo o que for maior ou igual a dataVencimento
					builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));
		
		if (lancamentoFilter.getDataVencimentoAte() != null) 
			predicates.add(//tudo o que for menor ou igual a dataVencimento
					builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private void adcionarRestricoesDePaginacao(TypedQuery<Lancamento> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalResgistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalResgistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalResgistrosPorPagina);
	}
	
	//query para localizar a quantidade de registros
	private Long total(LancamentoFilter filter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		//Adicionando filtro
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates); //Adicionado o Where
		
		criteria.select(builder.count(root)); //Fazendo um Select (*)count
		return manager.createQuery(criteria).getSingleResult();
	}
}
