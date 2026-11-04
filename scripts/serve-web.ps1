$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$files = @{
    "/web/" = @{ Path = Join-Path $repoRoot "web\index.html"; Type = "text/html; charset=utf-8" }
    "/web/index.html" = @{ Path = Join-Path $repoRoot "web\index.html"; Type = "text/html; charset=utf-8" }
    "/web/styles.css" = @{ Path = Join-Path $repoRoot "web\styles.css"; Type = "text/css; charset=utf-8" }
    "/web/app.js" = @{ Path = Join-Path $repoRoot "web\app.js"; Type = "text/javascript; charset=utf-8" }
    "/web/manifest.webmanifest" = @{ Path = Join-Path $repoRoot "web\manifest.webmanifest"; Type = "application/manifest+json; charset=utf-8" }
    "/web/service-worker.js" = @{ Path = Join-Path $repoRoot "web\service-worker.js"; Type = "text/javascript; charset=utf-8" }
    "/web/favicon.svg" = @{ Path = Join-Path $repoRoot "web\favicon.svg"; Type = "image/svg+xml" }
    "/web/icons/icon-192.png" = @{ Path = Join-Path $repoRoot "web\icons\icon-192.png"; Type = "image/png" }
    "/web/icons/icon-512.png" = @{ Path = Join-Path $repoRoot "web\icons\icon-512.png"; Type = "image/png" }
    "/web/data/criminal-articles.json" = @{ Path = Join-Path $repoRoot "web\data\criminal-articles.json"; Type = "application/json; charset=utf-8" }
    "/web/data/vade-mecum.json" = @{ Path = Join-Path $repoRoot "web\data\vade-mecum.json"; Type = "application/json; charset=utf-8" }
}

$listener = [System.Net.HttpListener]::new()
$listener.Prefixes.Add("http://127.0.0.1:8000/")
$listener.Start()
Write-Output "Assistente Criminal: http://127.0.0.1:8000/web/"
Write-Output "O servidor atende apenas este computador. Use um endereco HTTPS publicado para instalar no celular."

try {
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $response = $context.Response
        $path = $context.Request.Url.AbsolutePath
        if ($context.Request.HttpMethod -ne "GET" -or -not $files.ContainsKey($path)) {
            $response.StatusCode = 404
            $response.Close()
            continue
        }

        $file = $files[$path]
        if (-not (Test-Path -LiteralPath $file.Path -PathType Leaf)) {
            $response.StatusCode = 404
            $response.Close()
            continue
        }

        $bytes = [System.IO.File]::ReadAllBytes($file.Path)
        $response.StatusCode = 200
        $response.ContentType = $file.Type
        $response.ContentLength64 = $bytes.Length
        $response.Headers["X-Content-Type-Options"] = "nosniff"
        if ($path -eq "/web/service-worker.js") {
            $response.Headers["Cache-Control"] = "no-cache"
        }
        $response.OutputStream.Write($bytes, 0, $bytes.Length)
        $response.Close()
    }
}
finally {
    $listener.Stop()
    $listener.Close()
}
