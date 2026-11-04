package com.sottti.android.app.template.presentation.law.search.di

import javax.inject.Qualifier

/**
 * Identifica o escopo de teste injetado na ViewModel da busca.
 *
 * Em produção o valor é nulo e a ViewModel usa `viewModelScope`. Nos testes de unidade
 * o escopo do `runTest` é injetado, seguindo o padrão do módulo `characters-list`.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class CaseSearchTestScope
