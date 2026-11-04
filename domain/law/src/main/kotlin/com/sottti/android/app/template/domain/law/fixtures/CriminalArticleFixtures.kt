package com.sottti.android.app.template.domain.law.fixtures

import com.sottti.android.app.template.domain.law.model.CrimeGroup
import com.sottti.android.app.template.domain.law.model.CriminalArticle
import com.sottti.android.app.template.domain.law.model.CriminalArticleId

public val fixtureFurto: CriminalArticle = CriminalArticle(
    id = CriminalArticleId("cp-155"),
    code = "Código Penal",
    article = "Art. 155",
    title = "Furto",
    group = CrimeGroup.PATRIMONIO,
    legalText = "Subtrair, para si ou para outrem, coisa alheia móvel.",
    penalty = "Reclusão, de 1 (um) a 4 (quatro) anos, e multa.",
    elements = listOf(
        "Subtração de coisa alheia móvel",
        "Ânimo de assenhoreamento definitivo",
        "Ausência de violência ou grave ameaça",
    ),
    observations = listOf(
        "Se a subtração é praticada com violência ou grave ameaça, o caso é de roubo (art. 157).",
        "O furto privilegiado pode ser reconhecido quando o agente é primário e a coisa é de pequeno valor.",
    ),
    strongKeywords = listOf("furto", "furtou", "furtaram", "subtraiu", "subtração"),
    keywords = listOf(
        "celular",
        "carteira",
        "bicicleta",
        "loja",
        "apoderou",
        "levou sem que a vítima percebesse",
    ),
    disqualifiers = listOf("arma", "grave ameaça", "violência", "assalto"),
)

public val fixtureRoubo: CriminalArticle = CriminalArticle(
    id = CriminalArticleId("cp-157"),
    code = "Código Penal",
    article = "Art. 157",
    title = "Roubo",
    group = CrimeGroup.PATRIMONIO,
    legalText = "Subtrair coisa móvel alheia, para si ou para outrem, mediante grave ameaça ou " +
        "violência a pessoa, ou depois de havê-la, por qualquer meio, reduzido à " +
        "impossibilidade de resistência.",
    penalty = "Reclusão, de 4 (quatro) a 10 (dez) anos, e multa.",
    elements = listOf(
        "Subtração de coisa alheia móvel",
        "Grave ameaça ou violência contra a pessoa",
        "Finalidade de assenhoreamento definitivo",
    ),
    observations = listOf(
        "O emprego de arma ou o concurso de duas ou mais pessoas majora a pena (art. 157, § 2º).",
        "Se da violência resulta morte, aplica-se o latrocínio (art. 157, § 3º).",
    ),
    strongKeywords = listOf("roubo", "assalto", "assaltou", "assaltaram", "assaltante"),
    keywords = listOf(
        "arma",
        "armado",
        "violência",
        "grave ameaça",
        "ameaçou",
        "celular",
        "dinheiro",
        "rendeu",
    ),
    disqualifiers = listOf("sem violência", "furto"),
)

public val fixtureAmeaca: CriminalArticle = CriminalArticle(
    id = CriminalArticleId("cp-147"),
    code = "Código Penal",
    article = "Art. 147",
    title = "Ameaça",
    group = CrimeGroup.LIBERDADE,
    legalText = "Ameaçar alguém, por palavra, escrito ou gesto, ou qualquer outro meio simbólico, " +
        "de causar-lhe mal injusto e grave.",
    penalty = "Detenção, de 1 (um) a 6 (seis) meses, ou multa.",
    elements = listOf(
        "Ameaça de mal injusto e grave",
        "Meio verbal, escrito, gestual ou simbólico",
        "Capacidade de intimidar a vítima",
    ),
    observations = listOf(
        "A ameaça praticada contra a mulher no contexto de violência doméstica autoriza medidas protetivas.",
        "Se houver agressão física, o caso também pode configurar lesão corporal (art. 129).",
    ),
    strongKeywords = listOf("ameaça", "ameaçou", "ameaçando", "ameaçaram"),
    keywords = listOf("vou te matar", "vou acabar com você", "medo", "intimidação", "mensagem"),
    disqualifiers = listOf("agressão física", "soco", "empurrão"),
)

public val fixtureCriminalArticles: List<CriminalArticle> = listOf(
    fixtureFurto,
    fixtureRoubo,
    fixtureAmeaca,
)
