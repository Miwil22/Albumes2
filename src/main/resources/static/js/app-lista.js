document.addEventListener('DOMContentLoaded', () =>{

  const table = document.querySelector('#listaAlbumes');
  if (!table) return; // evitar errores si no existe la tabla

  table.addEventListener('click', async (event) => {

    const link = event.target.closest('a');

    // solo manejar enlaces con la clase exacta 'borrarAlbumLink'
    const isDelete = link.classList.contains('borrarAlbumLink');
    if (!isDelete) return;

    event.preventDefault();

    const tr = link.closest('tr');
    const idEl = tr && tr.querySelector('.albumId');
    const id = idEl ? idEl.textContent.trim() : null;

    const url = "/admin/albumes/" + id + "/delete/confirm";
    try {
      const response = await fetch(url);
      if (!response.ok) throw new Error(`Response status: ${response.status}`);
      const html = await response.text();
      document.querySelector('#placeholder-modal').innerHTML = html;

      const modalEl = document.querySelector('#delete-modal');
      if (modalEl) {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
      } else {
        console.error('Modal no encontrado en el HTML recibido');
      }
    } catch (error) {
      console.error(error.message);
    }

  })

  const buscador = document.querySelector('#buscador');
  buscador.addEventListener('keyup', async () => {
    const url = "/admin/albumes/filter?";
    const queryParams = new URLSearchParams({titulo: buscador.value}).toString();
    try {
      const response = await fetch(url + queryParams);
      if (!response.ok) throw new Error(`Response status: ${response.status}`);

      const html = await response.text();
      document.querySelector('#listaAlbumes').innerHTML = html;
    } catch (error) {
      console.error(error.message);
    }
  })

});

