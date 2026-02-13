(function () {
  function getCookie(name) {
    const v = document.cookie.match('(?:^|;)\\s*' + name + '\\s*=\\s*([^;]+)');
    return v ? decodeURIComponent(v[1]) : null;
  }

  document.addEventListener('DOMContentLoaded', function () {
    const name = 'visitasApp';
    let val = parseInt(getCookie(name), 10);
    const el = document.getElementById('visitas-count');
    if (el) el.textContent = val.toString();
  });
})();

