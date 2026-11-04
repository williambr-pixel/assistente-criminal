const CACHE_NAME = "chatjuridico-v4";
const APP_SHELL = [
  "./",
  "./index.html",
  "./styles.css",
  "./app.js",
  "./manifest.webmanifest",
  "./favicon.svg",
  "./icons/icon.svg",
  "./data/criminal-articles.json",
  "./data/vade-mecum.json",
  "./data/legal-glossary.json",
  "./icons/icon-192.png",
  "./icons/icon-512.png",
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => cache.addAll(APP_SHELL))
      .then(() => self.skipWaiting()),
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(
        keys
          .filter((key) =>
            (key.startsWith("assistente-criminal-") || key.startsWith("chatjuridico-")) &&
            key !== CACHE_NAME,
          )
          .map((key) => caches.delete(key)),
      ))
      .then(() => self.clients.claim()),
  );
});

self.addEventListener("fetch", (event) => {
  if (event.request.method !== "GET") return;
  const requestUrl = new URL(event.request.url);
  if (requestUrl.origin !== self.location.origin) return;

  event.respondWith((async () => {
    const cachedResponse = await caches.match(event.request, { ignoreSearch: true });
    try {
      const response = await fetch(event.request);
      if (response.ok) {
        const responseCopy = response.clone();
        await caches.open(CACHE_NAME).then((cache) => cache.put(event.request, responseCopy));
        return response;
      }
      return cachedResponse || response;
    } catch (error) {
      if (cachedResponse) return cachedResponse;
      if (event.request.mode === "navigate") {
        const offlinePage = await caches.match("./index.html");
        if (offlinePage) return offlinePage;
      }
      throw error;
    }
  })());
});
