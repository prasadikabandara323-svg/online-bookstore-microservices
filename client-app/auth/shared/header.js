// Injects the shared top header bar into any auth page.
// Usage: <div id="site-header-root"></div> then <script src="../shared/header.js"></script>
document.addEventListener("DOMContentLoaded", () => {
  const root = document.getElementById("site-header-root");
  if (!root) return;

  root.innerHTML = `
    <header class="site-header">
      <a class="brand" href="../book-catalog/index.html">
      <img src="../shared/assets/logo.jpg" alt="Gayel Online Book Store" class="logo-img" />
        <span class="name">Gayel Online Book Store</span>
      </a>
      <a class="back-link" href="../book-catalog/index.html">← Back to Store</a>
    </header>
  `;
});
