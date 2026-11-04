$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$webDataDirectory = Join-Path $repoRoot "web\data"
$documents = @(
    @{
        id = "constituicao"
        title = "Constituição Federal"
        code = "Constituição da República Federativa do Brasil"
        scope = "Reúne direitos e garantias fundamentais e organiza o Estado. No âmbito penal, é essencial para conferir direitos da pessoa investigada, acusada, presa e ofendida e os limites da atuação estatal."
        source = "https://www.planalto.gov.br/ccivil_03/constituicao/constituicao.htm"
        minimumArticles = 180
    },
    @{
        id = "codigo-penal"
        title = "Código Penal"
        code = "Decreto-Lei nº 2.848, de 7 de dezembro de 1940"
        scope = "Define infrações penais e penas em abstrato. A descrição de um artigo, por si só, não prova os fatos nem determina a pena concreta de uma pessoa."
        source = "https://www.planalto.gov.br/ccivil_03/decreto-lei/del2848compilado.htm"
        minimumArticles = 300
    },
    @{
        id = "codigo-processo-penal"
        title = "Código de Processo Penal"
        code = "Decreto-Lei nº 3.689, de 3 de outubro de 1941"
        scope = "Reúne regras do processo penal, como investigação, provas, medidas cautelares, procedimentos e recursos. Não substitui a análise dos fatos nem a consulta às decisões e alterações legislativas aplicáveis."
        source = "https://www.planalto.gov.br/ccivil_03/decreto-lei/del3689.htm"
        minimumArticles = 400
    },
    @{
        id = "lei-maria-da-penha"
        title = "Lei Maria da Penha"
        code = "Lei nº 11.340, de 7 de agosto de 2006"
        scope = "Estabelece mecanismos de prevenção e enfrentamento da violência doméstica e familiar contra a mulher, inclusive medidas protetivas. A necessidade de proteção deve ser avaliada no caso concreto."
        source = "https://www.planalto.gov.br/ccivil_03/_ato2004-2006/2006/lei/l11340.htm"
        minimumArticles = 35
    },
    @{
        id = "lei-de-drogas"
        title = "Lei de Drogas"
        code = "Lei nº 11.343, de 23 de agosto de 2006"
        scope = "Institui o Sisnad e disciplina prevenção, atenção, reinserção social e crimes relacionados a drogas. A distinção entre condutas depende dos fatos, das provas e dos critérios legais."
        source = "https://www.planalto.gov.br/ccivil_03/_ato2004-2006/2006/lei/l11343.htm"
        minimumArticles = 55
    },
    @{
        id = "lei-de-execucao-penal"
        title = "Lei de Execução Penal"
        code = "Lei nº 7.210, de 11 de julho de 1984"
        scope = "Disciplina a execução de sentenças e medidas penais, os direitos e deveres de pessoas condenadas ou internadas e a atuação dos órgãos de execução penal."
        source = "https://www.planalto.gov.br/ccivil_03/leis/l7210.htm"
        minimumArticles = 150
    }
)

function Get-ResponseText {
    param([Parameter(Mandatory = $true)]$Response)

    $stream = $Response.RawContentStream
    $stream.Position = 0
    $bytes = [byte[]]::new([int]$stream.Length)
    $bytesRead = $stream.Read($bytes, 0, $bytes.Length)

    if ($bytesRead -ge 2 -and $bytes[0] -eq 0xFF -and $bytes[1] -eq 0xFE) {
        return [System.Text.Encoding]::Unicode.GetString($bytes)
    }
    if ($bytesRead -ge 2 -and $bytes[0] -eq 0xFE -and $bytes[1] -eq 0xFF) {
        return [System.Text.Encoding]::BigEndianUnicode.GetString($bytes)
    }
    if ($bytesRead -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        return [string]$Response.Content
    }

    return [string]$Response.Content
}

function ConvertTo-PlainText {
    param([Parameter(Mandatory = $true)][string]$Html)

    $withLineBreaks = [System.Text.RegularExpressions.Regex]::Replace(
        $Html,
        '(?i)<br\s*/?>|</(?:p|div|li|tr|h[1-6])\s*>',
        "`n"
    )
    $withoutScripts = [System.Text.RegularExpressions.Regex]::Replace(
        $withLineBreaks,
        '(?is)<(?:script|style)\b[^>]*>.*?</(?:script|style)\s*>',
        " "
    )
    $withoutTags = [System.Text.RegularExpressions.Regex]::Replace($withoutScripts, '(?s)<[^>]+>', " ")
    $decoded = [System.Net.WebUtility]::HtmlDecode($withoutTags)
    foreach ($pair in @(
        @(0x80, 0x20AC), @(0x82, 0x201A), @(0x83, 0x0192), @(0x84, 0x201E),
        @(0x85, 0x2026), @(0x86, 0x2020), @(0x87, 0x2021), @(0x88, 0x02C6),
        @(0x89, 0x2030), @(0x8A, 0x0160), @(0x8B, 0x2039), @(0x8C, 0x0152),
        @(0x8E, 0x017D), @(0x91, 0x2018), @(0x92, 0x2019), @(0x93, 0x201C),
        @(0x94, 0x201D), @(0x95, 0x2022), @(0x96, 0x2013), @(0x97, 0x2014),
        @(0x98, 0x02DC), @(0x99, 0x2122), @(0x9A, 0x0161), @(0x9B, 0x203A),
        @(0x9C, 0x0153), @(0x9E, 0x017E), @(0x9F, 0x0178)
    )) {
        $decoded = $decoded.Replace([char]$pair[0], [char]$pair[1])
    }

    return [System.Text.RegularExpressions.Regex]::Replace($decoded, '\s+', ' ').Trim()
}

$index = [ordered]@{
    generatedAt = (Get-Date).ToString("yyyy-MM-dd")
    publisher = "Presidência da República — Portal da Legislação"
    documents = @()
}

foreach ($document in $documents) {
    Write-Output "Importando $($document.title) da fonte oficial..."
    $response = Invoke-WebRequest -Uri $document.source -UseBasicParsing -TimeoutSec 90
    $html = Get-ResponseText -Response $response
    $paragraphs = [System.Text.RegularExpressions.Regex]::Matches(
        $html,
        '(?is)<p\b[^>]*>.*?</p>'
    )
    if ($paragraphs.Count -eq 0) {
        throw "A fonte oficial de $($document.title) não contém parágrafos reconhecíveis."
    }

    $articles = [System.Collections.Generic.List[object]]::new()
    $articleIndexes = @{}
    $currentArticleIndex = -1
    foreach ($paragraph in $paragraphs) {
        $text = ConvertTo-PlainText -Html $paragraph.Value
        $match = [System.Text.RegularExpressions.Regex]::Match(
            $text,
            '^\s*Art\.?\s*(?<number>\d+)\s*(?<ordinal>[º°ªo])?(?:-(?<suffix>[A-Za-z]))?'
        )
        if (-not $match.Success) {
            if (
                $currentArticleIndex -ge 0 -and
                $text.Length -gt 0 -and
                $text -notmatch '^(?i:PARTE|LIVRO|T[IÍ]TULO|CAP[IÍ]TULO|SE[CÇ][AÃ]O|DISPOSI[CÇ][ÃA]O)\b'
            ) {
                if ($articles[$currentArticleIndex].text -notlike "*$text*") {
                    $articles[$currentArticleIndex].text += "`n`n$text"
                }
            }
            continue
        }

        $articleNumber = $match.Groups["number"].Value
        if ($match.Groups["ordinal"].Success) {
            $articleNumber += $match.Groups["ordinal"].Value.ToLowerInvariant()
        }
        if ($match.Groups["suffix"].Success) {
            $articleNumber += "-" + $match.Groups["suffix"].Value.ToUpperInvariant()
        }
        $label = "Art. $articleNumber"

        if ($articleIndexes.ContainsKey($label)) {
            $currentArticleIndex = $articleIndexes[$label]
            if ($articles[$currentArticleIndex].text -notlike "*$text*") {
                $articles[$currentArticleIndex].text += "`n`n$text"
            }
            continue
        }

        $currentArticleIndex = $articles.Count
        $articleIndexes[$label] = $currentArticleIndex
        $articles.Add([ordered]@{
            number = $articleNumber
            label = $label
            text = $text
        })
    }

    if ($articles.Count -lt $document.minimumArticles) {
        throw "Foram encontrados apenas $($articles.Count) artigos em $($document.title); mínimo esperado: $($document.minimumArticles)."
    }

    $index.documents += [ordered]@{
        id = $document.id
        title = $document.title
        code = $document.code
        scope = $document.scope
        source = $document.source
        articles = @($articles.ToArray())
    }
    Write-Output "$($document.title): $($articles.Count) dispositivos indexados."
}

New-Item -ItemType Directory -Force -Path $webDataDirectory | Out-Null
$json = ConvertTo-Json -InputObject $index -Depth 10
[System.IO.File]::WriteAllText(
    (Join-Path $webDataDirectory "vade-mecum.json"),
    $json,
    [System.Text.UTF8Encoding]::new($false)
)
Copy-Item `
    -LiteralPath (Join-Path $repoRoot "data\law\src\main\assets\criminal_articles.json") `
    -Destination (Join-Path $webDataDirectory "criminal-articles.json") `
    -Force

Write-Output "Índice do Vade Mecum e base criminal exportados para web\data."
