const CRIMINAL_ARTICLE_DATA_URL = "./data/criminal-articles.json";
const VADE_MECUM_DATA_URL = "./data/vade-mecum.json";

const examples = [
  "O autor, armado, rendeu a vítima e levou o celular.",
  "O companheiro agrediu a vítima com socos e a ameaçou de morte em casa.",
  "O autor aplicou um golpe pelo WhatsApp e fez a vítima transferir dinheiro por Pix.",
  "O autor foi flagrado vendendo porções de cocaína e guardando dinheiro trocado.",
];

const form = document.querySelector("#search-form");
const input = document.querySelector("#case-description");
const submitButton = form.querySelector('button[type="submit"]');
const clearButton = document.querySelector("#clear-button");
const resultsSection = document.querySelector("#results");
const resultList = document.querySelector("#result-list");
const resultCount = document.querySelector("#result-count");
const examplesSection = document.querySelector("#examples");
const dialog = document.querySelector("#article-dialog");
const dialogBody = document.querySelector("#dialog-body");
const closeDialogButton = document.querySelector(".dialog-close");
const caseView = document.querySelector("#case-view");
const vadeView = document.querySelector("#vade-view");
const caseTab = document.querySelector("#case-tab");
const vadeTab = document.querySelector("#vade-tab");
const vadeShortcut = document.querySelector("#vade-shortcut");
const vadeForm = document.querySelector("#vade-search-form");
const vadeQueryInput = document.querySelector("#vade-query");
const vadeLawFilter = document.querySelector("#vade-law-filter");
const vadeResults = document.querySelector("#vade-results");
const vadeSourceStatus = document.querySelector("#vade-source-status");
const installButton = document.querySelector("#install-button");
const installDialog = document.querySelector("#install-dialog");
const closeInstallDialogButton = document.querySelector(".install-dialog-close");

let articles = [];
let currentMatches = [];
let currentDescription = "";
let lawDocuments = [];
let installPrompt;
let vadeIndex = { generatedAt: "" };

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (character) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;",
  })[character]);
}

function normalize(value) {
  return value
    .toLocaleLowerCase("pt-BR")
    .normalize("NFD")
    .replace(/\p{Mn}+/gu, "")
    .replace(/[^a-z0-9]+/g, " ")
    .trim();
}

function mentions(textTokens, term) {
  const termTokens = normalize(term).split(/ +/).filter(Boolean);
  if (termTokens.length === 0) return false;

  return termTokens.every((termToken) =>
    textTokens.some((token) => {
      const shortestLength = Math.min(token.length, termToken.length);
      let commonPrefixLength = 0;
      while (
        commonPrefixLength < token.length &&
        commonPrefixLength < termToken.length &&
        token[commonPrefixLength] === termToken[commonPrefixLength]
      ) {
        commonPrefixLength += 1;
      }
      const sharesPrefix = shortestLength >= 4 &&
        commonPrefixLength >= shortestLength - 1;
      return token === termToken || sharesPrefix;
    }),
  );
}

function confidence(strongCount, supportCount, conflictCount) {
  let level;
  if (strongCount >= 2 || (strongCount >= 1 && supportCount >= 2) || supportCount >= 4) {
    level = "high";
  } else if (strongCount >= 1 || supportCount >= 2) {
    level = "medium";
  } else {
    level = "low";
  }

  if (conflictCount >= 2) {
    level = level === "high" ? "medium" : "low";
  }
  return level;
}

function searchArticles(description) {
  const textTokens = normalize(description).split(/ +/).filter(Boolean);

  return articles
    .map((article) => {
      const strongMatches = article.strongKeywords.filter((term) => mentions(textTokens, term));
      const supportMatches = article.keywords.filter((term) => mentions(textTokens, term));
      const conflicts = article.disqualifiers.filter((term) => mentions(textTokens, term));
      const score = strongMatches.length * 3 + supportMatches.length - conflicts.length * 2;

      if ((!strongMatches.length && !supportMatches.length) || score <= 0) return null;
      return {
        article,
        score,
        strongMatches,
        supportMatches,
        conflicts,
        confidence: confidence(strongMatches.length, supportMatches.length, conflicts.length),
        matchedTerms: [...strongMatches, ...supportMatches],
      };
    })
    .filter(Boolean)
    .sort((left, right) =>
      ["low", "medium", "high"].indexOf(right.confidence) -
        ["low", "medium", "high"].indexOf(left.confidence) ||
      right.score - left.score ||
      (left.article.article < right.article.article ? -1 : left.article.article > right.article.article ? 1 : 0),
    );
}

function renderResults(matches) {
  currentMatches = matches;
  resultsSection.hidden = false;
  examplesSection.hidden = true;
  resultCount.textContent = matches.length === 1
    ? "1 artigo possível"
    : `${matches.length} artigos possíveis`;

  if (matches.length === 0) {
    resultList.innerHTML = `
      <div class="empty-state">
        <h3>Nenhum artigo encontrado</h3>
        <p>Descreva melhor a conduta, o meio empregado e o resultado. Também vale citar o contexto e o vínculo entre as pessoas.</p>
      </div>`;
    return;
  }

  resultList.innerHTML = matches.map((match, index) => {
    const levelLabels = {
      high: "Mais termos coincidentes",
      medium: "Alguns termos coincidentes",
      low: "Poucos termos coincidentes",
    };
    const article = match.article;
    const conflictNote = match.conflicts.length
      ? `<p class="conflict-note">A busca também associou o relato a ${escapeHtml(match.conflicts.join(", "))}, termo(s) que a base relaciona a circunstâncias que podem mudar a análise.</p>`
      : "";
    const terms = match.matchedTerms.length
      ? `<p class="matched-terms"><strong>Termos de referência encontrados:</strong> ${escapeHtml(match.matchedTerms.slice(0, 6).join(", "))}.</p>`
      : "";

    return `
      <button class="result-card" type="button" data-match="${index}" aria-label="Entender a indicação e ver detalhes: ${escapeHtml(article.article)} — ${escapeHtml(article.title)}">
        <span class="result-topline">
          <span class="result-article">${escapeHtml(article.article)}</span>
          <span class="confidence ${match.confidence}">${levelLabels[match.confidence]}</span>
        </span>
        <p class="result-title">${escapeHtml(article.title)}</p>
        <p class="result-code">${escapeHtml(article.code)}</p>
        ${terms}
        ${conflictNote}
        <span class="card-action">Entender a indicação e o que verificar <span aria-hidden="true">→</span></span>
      </button>`;
  }).join("");
}

function bulletList(items) {
  return `<ul>${items.map((item) => `<li>${escapeHtml(item)}</li>`).join("")}</ul>`;
}

function showView(view) {
  const isVade = view === "vade";
  caseView.hidden = isVade;
  vadeView.hidden = !isVade;
  caseTab.classList.toggle("is-active", !isVade);
  caseTab.setAttribute("aria-selected", String(!isVade));
  vadeTab.classList.toggle("is-active", isVade);
  vadeTab.setAttribute("aria-selected", String(isVade));
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function renderVadeDocuments() {
  vadeResults.innerHTML = `
    <div class="vade-heading">
      <div>
        <p class="eyebrow"><span class="eyebrow-line"></span> BASE OFICIAL</p>
        <h2>Escolha uma legislação</h2>
      </div>
      <span class="result-count">${lawDocuments.length} fontes · atualização ${escapeHtml(vadeIndex.generatedAt)}</span>
    </div>
    <div class="law-grid">
      ${lawDocuments.map((document) => `
        <button class="law-card" type="button" data-law-card="${escapeHtml(document.id)}">
          <span class="law-card-icon" aria-hidden="true">§</span>
          <span class="law-card-text">
            <strong>${escapeHtml(document.title)}</strong>
            <span>${escapeHtml(document.code)}</span>
            <span class="law-card-count">${document.articles.length} dispositivos · disponível offline</span>
          </span>
          <span class="example-arrow" aria-hidden="true">→</span>
        </button>`).join("")}
    </div>`;
}

function vadeSearchText(document, article) {
  return normalize(`${document.title} ${document.code} ${article.label} ${article.text}`);
}

function findVadeArticles(query, documentId) {
  const normalizedQuery = normalize(query);
  const queryTokens = normalizedQuery.split(" ").filter(Boolean);

  return lawDocuments
    .filter((document) => documentId === "all" || document.id === documentId)
    .flatMap((document) => document.articles.map((article) => {
      const searchText = vadeSearchText(document, article);
      const normalizedLabel = normalize(article.label);
      const exactLabelMatch = normalizedLabel === normalizedQuery;
      const articleFamilyMatch = normalizedQuery.startsWith("art ") &&
        normalizedLabel.startsWith(`${normalizedQuery} `);
      const allTermsMatch = queryTokens.every((term) => searchText.includes(term));
      if (!allTermsMatch) return null;

      let score = 0;
      if (exactLabelMatch) score += 300;
      else if (articleFamilyMatch) score += 100;
      if (normalize(document.title).includes(normalizedQuery)) score += 35;
      if (normalize(document.code).includes(normalizedQuery)) score += 25;
      if (normalizedQuery && searchText.includes(normalizedQuery)) score += 15;
      if (normalizedLabel.includes(normalizedQuery)) score += 50;

      return { document, article, score };
    }))
    .filter(Boolean)
    .sort((left, right) =>
      right.score - left.score ||
      left.document.title.localeCompare(right.document.title, "pt-BR") ||
      left.article.label.localeCompare(right.article.label, "pt-BR"),
    );
}

function articleExcerpt(article, query) {
  const paragraphs = article.text.split(/\n+/).map((part) => part.trim()).filter(Boolean);
  const normalizedQuery = normalize(query);
  const matchingParagraph = paragraphs.find((paragraph) => {
    const normalizedParagraph = normalize(paragraph);
    return normalizedQuery
      ? normalizedParagraph.includes(normalizedQuery) ||
        normalizedQuery.split(" ").filter(Boolean).every((term) => normalizedParagraph.includes(term))
      : true;
  });
  const excerpt = matchingParagraph ?? paragraphs[0] ?? article.text;
  const trimmed = excerpt.length > 300 ? `${excerpt.slice(0, 297).trimEnd()}…` : excerpt;
  return escapeHtml(trimmed);
}

function renderVadeSearch(query) {
  const documentId = vadeLawFilter.value;
  if (!query.trim()) {
    if (documentId === "all") {
      renderVadeDocuments();
      return;
    }

    const selectedDocument = lawDocuments.find((document) => document.id === documentId);
    if (!selectedDocument) return;
    renderVadeArticles(
      selectedDocument.articles.slice(0, 60).map((article) => ({
        document: selectedDocument,
        article,
        score: 0,
      })),
      "",
      selectedDocument.articles.length,
    );
    return;
  }

  const results = findVadeArticles(query, documentId);
  renderVadeArticles(results.slice(0, 60), query, results.length);
}

function renderVadeArticles(results, query, totalCount) {
  const articleReference = query.trim()
    ? `Resultados para <strong>${escapeHtml(query.trim())}</strong>`
    : `Artigos de <strong>${escapeHtml(results[0]?.document.title ?? "esta legislação")}</strong>`;

  if (results.length === 0) {
    vadeResults.innerHTML = `
      <div class="empty-state">
        <h3>Nenhum dispositivo encontrado</h3>
        <p>Tente outro termo, o número do artigo ou uma busca em todas as leis.</p>
      </div>`;
    return;
  }

  vadeResults.innerHTML = `
    <div class="vade-heading">
      <div>
        <p class="eyebrow"><span class="eyebrow-line"></span> TEXTO DA LEI</p>
        <h2>${articleReference}</h2>
      </div>
      <span class="result-count">${totalCount} ${totalCount === 1 ? "dispositivo" : "dispositivos"}${totalCount > results.length ? ` · exibindo ${results.length}` : ""}</span>
    </div>
    <div class="vade-article-list">
      ${results.map(({ document, article }, index) => `
        <button class="vade-article-card" type="button" data-vade-result="${index}">
          <span class="vade-article-head">
            <span class="result-article">${escapeHtml(article.label)}</span>
            <span class="vade-document-name">${escapeHtml(document.title)}</span>
          </span>
          <span class="vade-article-excerpt">${articleExcerpt(article, query)}</span>
          <span class="card-action">Ler o dispositivo completo <span aria-hidden="true">→</span></span>
        </button>`).join("")}
    </div>`;

  vadeResults.currentResults = results;
}

function openVadeArticle(document, article) {
  dialog.classList.add("vade-reading-dialog");
  const safeSource = escapeHtml(document.source);
  dialogBody.innerHTML = `
    <p class="detail-article">${escapeHtml(article.label)}</p>
    <h2 class="detail-title" id="dialog-title">${escapeHtml(document.title)}</h2>
    <p class="detail-code">${escapeHtml(document.code)}</p>
    <section class="application-context">
      <h3>O que este texto ajuda a consultar</h3>
      <p>${escapeHtml(document.scope)}</p>
      <p>Este é o texto do dispositivo na consolidação oficial consultada em ${escapeHtml(vadeIndex.generatedAt)}. A correspondência na pesquisa localiza o artigo, mas não decide se ele se aplica a uma situação ou a uma pessoa.</p>
    </section>
    <section class="detail-section vade-full-text">
      <h3>Texto integral do dispositivo</h3>
      <p>${escapeHtml(article.text).replace(/\n/g, "<br><br>")}</p>
    </section>
    <p class="official-source-link"><a href="${safeSource}" target="_blank" rel="noopener noreferrer">Abrir e conferir a fonte oficial no Portal da Legislação ↗</a></p>
    <p class="detail-disclaimer">Consolidação salva para consulta informativa offline. Pode não incluir alterações publicadas após ${escapeHtml(vadeIndex.generatedAt)}. A fonte oficial e a legislação vigente devem prevalecer.</p>`;
  dialog.showModal();
}

function applicationContext(match) {
  const article = match.article;
  const articleNumber = article.article.match(/Art\.?\s*(\d+)/i)?.[1];
  const matchedTerms = match.matchedTerms.length
    ? `A busca encontrou no relato correspondências com estes termos de referência: ${match.matchedTerms.map((term) => `“${escapeHtml(term)}”`).join(", ")}. São aproximações de palavras — incluindo variações simples — associadas a este tipo penal na base; isso explica por que o artigo foi sugerido, mas não confirma que a conduta ou todos os requisitos legais ocorreram.`
    : "A busca encontrou correspondências parciais. Confira com atenção o texto legal e os elementos antes de considerar este artigo.";

  const contextualCircumstances = match.conflicts.length
    ? `<p>A busca também encontrou correspondências com estes termos de atenção: ${match.conflicts.map((term) => `“${escapeHtml(term)}”`).join(", ")}. A própria base os relaciona a possíveis circunstâncias que mudam a análise. Verifique se e como essas circunstâncias realmente ocorreram; elas podem exigir outro enquadramento ou a consideração de uma exceção.</p>`
    : "";

  return `
    <section class="application-context" aria-labelledby="application-context-title">
      <h3 id="application-context-title">Como esta indicação se relaciona ao relato</h3>
      <p class="case-report"><strong>Fatos informados:</strong> ${escapeHtml(currentDescription)}</p>
      <p>${matchedTerms}</p>
      <p><strong>Texto do artigo em linguagem direta:</strong> ${escapeHtml(article.legalText)}</p>
      <div class="context-checklist">
        <h4>Aspectos legais a confrontar com os fatos</h4>
        ${bulletList(article.elements)}
      </div>
      ${contextualCircumstances}
      <p class="context-limit">Para aplicar o artigo a uma pessoa, não basta aparecerem palavras parecidas: é necessário verificar cada requisito legal com os fatos e as provas do caso, no procedimento adequado. A sugestão não declara que houve crime nem que a pessoa mencionada é culpada.</p>
      ${articleNumber ? `<button class="vade-lookup-button" type="button" data-vade-lookup="${escapeHtml(articleNumber)}">Consultar Art. ${escapeHtml(articleNumber)} no Vade Mecum <span aria-hidden="true">→</span></button>` : ""}
    </section>
    <section class="detail-section people-context">
      <h3>O que isso pode significar para cada pessoa</h3>
      <p><strong>Pessoa investigada ou acusada:</strong> o artigo pode ser uma hipótese examinada pela investigação ou acusação, mas não se aplica automaticamente. A responsabilidade depende da prova dos fatos e dos requisitos legais; a pessoa tem direito de se defender e é presumida inocente até decisão condenatória definitiva.</p>
      <p><strong>Pessoa ofendida:</strong> a indicação ajuda a nomear uma possível infração e orientar o que relatar ou documentar. Por si só, ela não confirma autoria, não garante condenação ou indenização e não substitui a busca de proteção ou atendimento quando necessário.</p>
      <p><strong>Quem analisa o caso:</strong> precisa confrontar cada elemento legal com fatos e provas, conferir as circunstâncias e considerar outros enquadramentos possíveis. A lista automatizada serve só para consulta inicial.</p>
    </section>`;
}

function openArticle(match) {
  dialog.classList.remove("vade-reading-dialog");
  const { article } = match;
  const groupNames = {
    ADMINISTRACAO_PUBLICA: "Administração pública",
    ARMAS: "Armas",
    DIGNIDADE_SEXUAL: "Dignidade sexual",
    DROGAS: "Drogas",
    FE_PUBLICA: "Fé pública",
    HONRA: "Honra",
    LIBERDADE: "Liberdade",
    OUTROS: "Outros",
    PATRIMONIO: "Patrimônio",
    TRANSITO: "Trânsito",
    VIDA: "Vida",
    VIOLENCIA_DOMESTICA: "Violência doméstica",
  };

  dialogBody.innerHTML = `
    <p class="detail-article">${escapeHtml(article.article)}</p>
    <h2 class="detail-title" id="dialog-title">${escapeHtml(article.title)}</h2>
    <p class="detail-code">${escapeHtml(article.code)}</p>
    <span class="group-badge">${escapeHtml(groupNames[article.group] ?? "Outros")}</span>
    ${applicationContext(match)}
    <section class="detail-section">
      <h3>Redação cadastrada na base</h3>
      <p>${escapeHtml(article.legalText)}</p>
    </section>
    <section class="detail-section">
      <h3>Pena prevista em lei (em abstrato)</h3>
      <p>${escapeHtml(article.penalty)}</p>
      <p>Essa faixa não é aplicada automaticamente pela ferramenta nem significa que a pena máxima será imposta. Se houver condenação, a pena concreta depende da decisão judicial, das provas, das circunstâncias do caso e das regras legais aplicáveis.</p>
    </section>
    <section class="detail-section">
      <h3>Observações práticas</h3>
      ${bulletList(article.observations)}
    </section>
    <p class="detail-disclaimer">Conteúdo informativo e offline. Confira a redação oficial e as alterações legislativas antes de usar em peça ou parecer.</p>`;
  dialog.showModal();
}

function setLoading(isLoading) {
  resultsSection.setAttribute("aria-busy", String(isLoading));
  submitButton.disabled = isLoading;
  submitButton.querySelector("span").textContent = isLoading ? "Analisando…" : "Analisar caso";
}

async function submitSearch(event) {
  event?.preventDefault();
  const description = input.value.trim();
  if (!description || articles.length === 0) return;

  currentDescription = description;
  clearButton.disabled = false;
  setLoading(true);
  resultsSection.hidden = false;
  resultList.innerHTML = '<div class="empty-state"><p>Analisando relato…</p></div>';

  await new Promise((resolve) => window.setTimeout(resolve, 120));
  renderResults(searchArticles(description));
  setLoading(false);
  resultsSection.scrollIntoView({ behavior: "smooth", block: "start" });
}

form.addEventListener("submit", submitSearch);

input.addEventListener("input", () => {
  clearButton.disabled = input.value.length === 0 && currentMatches.length === 0;
});

input.addEventListener("keydown", (event) => {
  if (event.key === "Enter" && (event.ctrlKey || event.metaKey)) submitSearch(event);
});

clearButton.addEventListener("click", () => {
  input.value = "";
  currentMatches = [];
  currentDescription = "";
  resultsSection.hidden = true;
  examplesSection.hidden = false;
  clearButton.disabled = true;
  input.focus();
});

examplesSection.addEventListener("click", (event) => {
  const button = event.target.closest("[data-example]");
  if (!button) return;
  input.value = button.dataset.example;
  clearButton.disabled = false;
  submitSearch();
});

resultList.addEventListener("click", (event) => {
  const button = event.target.closest("[data-match]");
  if (!button) return;
  const match = currentMatches[Number(button.dataset.match)];
  if (match) openArticle(match);
});

caseTab.addEventListener("click", () => showView("case"));
vadeTab.addEventListener("click", () => showView("vade"));
vadeShortcut.addEventListener("click", () => showView("vade"));

vadeForm.addEventListener("submit", (event) => {
  event.preventDefault();
  renderVadeSearch(vadeQueryInput.value);
});

vadeQueryInput.addEventListener("input", () => {
  renderVadeSearch(vadeQueryInput.value);
});

vadeLawFilter.addEventListener("change", () => {
  renderVadeSearch(vadeQueryInput.value);
});

vadeResults.addEventListener("click", (event) => {
  const lawCard = event.target.closest("[data-law-card]");
  if (lawCard) {
    vadeLawFilter.value = lawCard.dataset.lawCard;
    renderVadeSearch("");
    return;
  }

  const resultCard = event.target.closest("[data-vade-result]");
  const result = resultCard && vadeResults.currentResults?.[Number(resultCard.dataset.vadeResult)];
  if (result) openVadeArticle(result.document, result.article);
});

dialogBody.addEventListener("click", (event) => {
  const lookupButton = event.target.closest("[data-vade-lookup]");
  if (!lookupButton) return;

  dialog.close();
  vadeLawFilter.value = "codigo-penal";
  vadeQueryInput.value = `Art. ${lookupButton.dataset.vadeLookup}`;
  showView("vade");
  renderVadeSearch(vadeQueryInput.value);
});

closeDialogButton.addEventListener("click", () => dialog.close());
dialog.addEventListener("click", (event) => {
  if (event.target === dialog) dialog.close();
});

closeInstallDialogButton.addEventListener("click", () => installDialog.close());
installDialog.addEventListener("click", (event) => {
  if (event.target === installDialog) installDialog.close();
});

function isInstalled() {
  return window.matchMedia("(display-mode: standalone)").matches ||
    window.navigator.standalone === true;
}

function shouldShowInstallButton() {
  const isAppleMobile = /iPhone|iPad|iPod/i.test(navigator.userAgent) ||
    (navigator.platform === "MacIntel" && navigator.maxTouchPoints > 1);
  installButton.hidden = isInstalled() || (!isAppleMobile && !installPrompt);
}

window.addEventListener("beforeinstallprompt", (event) => {
  event.preventDefault();
  installPrompt = event;
  shouldShowInstallButton();
});

window.addEventListener("appinstalled", () => {
  installPrompt = null;
  installButton.hidden = true;
});

installButton.addEventListener("click", async () => {
  if (!installPrompt) {
    installDialog.showModal();
    return;
  }

  const prompt = installPrompt;
  installPrompt = null;
  await prompt.prompt();
  shouldShowInstallButton();
});

async function loadAppData() {
  const [criminalResponse, vadeResponse] = await Promise.all([
    fetch(CRIMINAL_ARTICLE_DATA_URL),
    fetch(VADE_MECUM_DATA_URL),
  ]);
  if (!criminalResponse.ok) {
    throw new Error(`Não foi possível carregar a base de artigos penais (HTTP ${criminalResponse.status}).`);
  }
  if (!vadeResponse.ok) {
    throw new Error(`Não foi possível carregar o Vade Mecum local (HTTP ${vadeResponse.status}).`);
  }

  const [criminalData, indexData] = await Promise.all([
    criminalResponse.json(),
    vadeResponse.json(),
  ]);
  if (!Array.isArray(criminalData) || criminalData.length === 0) {
    throw new Error("A base de artigos penais está vazia ou em formato inválido.");
  }
  if (!Array.isArray(indexData.documents) || indexData.documents.length === 0) {
    throw new Error("A base do Vade Mecum está vazia ou em formato inválido.");
  }

  articles = criminalData;
  vadeIndex = indexData;
  lawDocuments = indexData.documents;
  lawDocuments.forEach((lawDocument) => {
    const option = window.document.createElement("option");
    option.value = lawDocument.id;
    option.textContent = lawDocument.title;
    vadeLawFilter.append(option);
  });
  const articleCount = lawDocuments.reduce((total, document) => total + document.articles.length, 0);
  vadeSourceStatus.textContent =
    `${lawDocuments.length} normas federais · ${articleCount.toLocaleString("pt-BR")} dispositivos · fonte oficial consultada em ${vadeIndex.generatedAt}.`;
  renderVadeDocuments();
}

loadAppData().catch((error) => {
  const panel = document.querySelector(".search-panel");
  panel.insertAdjacentHTML("beforeend", `
    <p class="conflict-note" role="alert">${escapeHtml(error.message)} Inicie o servidor local a partir da pasta do projeto para habilitar a consulta.</p>`);
  submitButton.disabled = true;
  vadeSourceStatus.textContent = `Não foi possível carregar a legislação: ${error.message}`;
  console.error("Erro ao carregar as bases locais do ChatJuridico.", error);
});

if ("serviceWorker" in navigator && window.isSecureContext) {
  navigator.serviceWorker.register("./service-worker.js")
    .catch((error) => console.error("Não foi possível habilitar o modo offline.", error));
}

shouldShowInstallButton();
