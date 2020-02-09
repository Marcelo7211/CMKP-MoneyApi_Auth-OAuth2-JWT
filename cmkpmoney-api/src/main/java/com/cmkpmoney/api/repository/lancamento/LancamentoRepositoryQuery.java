package com.cmkpmoney.api.repository.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cmkpmoney.api.model.Lancamento;
import com.cmkpmoney.api.repository.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {

	public Page<Lancamento> filtrar (LancamentoFilter filter, Pageable pageable);
}
