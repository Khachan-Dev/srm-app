self.addEventListener('install', (e) => {
 console.log('Service Worker installed');
});

self.addEventListener('fetch', (e) => {
 // كود بسيط باش يخدم أوفلاين مستقبلاً
 e.respondWith(fetch(e.request).catch(() => caches.match(e.request)));
});