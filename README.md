# ChatJuridico

Aplicativo de apoio informativo para consulta inicial de temas jurídicos e estudo do Direito.

[Abrir o ChatJuridico](https://williambr-pixel.github.io/assistente-criminal/) ·
[Ver o código-fonte](https://github.com/williambr-pixel/assistente-criminal) ·
[Relatar um problema](https://github.com/williambr-pixel/assistente-criminal/issues)

> **Aviso:** o ChatJuridico não substitui orientação de profissional habilitado. As sugestões da
> análise são referências para estudo e pesquisa; não confirmam a ocorrência de crime, autoria ou
> responsabilidade. Confira a legislação vigente e procure assistência profissional para decisões
> sobre um caso concreto.

## Recursos

- **Análise inicial de relatos:** compara palavras do relato com referências de artigos penais na
  base local. Não pesquisa jurisprudência nem decide se houve crime.
- **Vade Mecum offline:** consulta a quinze normas federais, incluindo Constituição, códigos Penal
  e de Processo Penal, Lei Maria da Penha, Lei de Drogas, Estatuto do Desarmamento, ECA e leis
  sobre tortura, lavagem de dinheiro e organização criminosa.
- **Fontes oficiais:** as referências legais apresentam links para consultar a legislação no Portal
  da Legislação da Presidência da República.
- **Glossário jurídico:** pesquisa definições introdutórias de termos jurídicos.
- **Ajuda local:** responde perguntas frequentes e explica termos do glossário neste aparelho, sem
  API externa nem histórico salvo. Não avalia casos nem presta aconselhamento jurídico individual.
- **Indicações de leitura:** sugestões de livros para apoiar os estudos de Direito.

O texto legislativo armazenado no dispositivo pode não refletir alterações recentes. Use os links
para conferir a redação oficial e atualizada antes de se basear em qualquer informação.

## Aplicativo web

A versão web é uma aplicação instalável (PWA). Abra
<https://williambr-pixel.github.io/assistente-criminal/> no navegador. Em dispositivos móveis,
use a opção do navegador para adicionar o app à tela inicial. Alguns recursos e conteúdos ficam
disponíveis offline depois que a aplicação e seus dados são carregados.

Para executar localmente no Windows, abra o PowerShell na raiz do repositório:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\serve-web.ps1
```

Depois, acesse <http://127.0.0.1:8000/web/> no mesmo computador. Para atualizar a cópia local da
legislação a partir das fontes oficiais, execute:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\update-vade-mecum.ps1
```

## Desenvolvimento

O repositório também contém o projeto Android multi-módulo baseado em Kotlin e Jetpack Compose.
Para instruções de contribuição e verificação, consulte
[CONTRIBUTING.md](CONTRIBUTING.md). Consulte a [licença](LICENSE) para os termos de uso do código.
