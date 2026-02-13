console.log('🚀 app-lista.js v20260213-FIXED-DELETE cargado');

document.addEventListener('DOMContentLoaded', () =>{

  const table = document.querySelector('#listaAlbumes');
  if (!table) return;

  table.addEventListener('click', async (event) => {

    const link = event.target.closest('a');
    const isDelete = link && link.classList.contains('borrarAlbumLink');
    if (!isDelete) return;

    event.preventDefault();
    event.stopPropagation();

    const tr = link.closest('tr');
    const idEl = tr && tr.querySelector('.albumId');
    const id = idEl ? idEl.textContent.trim() : null;

    if (!id) {
      console.error('No se pudo encontrar el ID del álbum');
      return;
    }

    const url = "/admin/albumes/" + id + "/delete/confirm";
    try {
      const response = await fetch(url);
      if (!response.ok) throw new Error(`Response status: ${response.status}`);
      const html = await response.text();

      const placeholder = document.querySelector('#placeholder-modal');
      placeholder.innerHTML = html;

      const modalEl = document.querySelector('#delete-modal');
      if (modalEl) {
        console.log('Modal creado, mostrando...');

        // Mostrar modal manualmente sin Bootstrap Modal
        modalEl.style.display = 'block';
        modalEl.classList.add('show');
        modalEl.setAttribute('aria-modal', 'true');
        modalEl.removeAttribute('aria-hidden');
        document.body.classList.add('modal-open');

        // Crear backdrop manualmente
        const backdrop = document.createElement('div');
        backdrop.className = 'modal-backdrop fade show';
        document.body.appendChild(backdrop);

        console.log('Modal mostrado correctamente');

        const closeModal = () => {
          modalEl.style.display = 'none';
          modalEl.classList.remove('show');
          modalEl.setAttribute('aria-hidden', 'true');
          modalEl.removeAttribute('aria-modal');
          document.body.classList.remove('modal-open');
          backdrop.remove();
        };

        // Cerrar con botón X
        const btnClose = modalEl.querySelector('.btn-close');
        if (btnClose) {
          btnClose.onclick = closeModal;
        }

        // Cerrar con botón Cancelar
        const btnCancel = modalEl.querySelector('.btn-secondary');
        if (btnCancel) {
          btnCancel.onclick = closeModal;
        }

        // Hacer funcionar el botón Eliminar - INTERCEPTAR TODO EL MODAL
        const deleteForm = modalEl.querySelector('form[method="post"]');

        if (deleteForm) {
          console.log('Form encontrado: true');

          // Interceptar TODOS los clics en el modal
          modalEl.addEventListener('click', (e) => {
            // Verificar si es el botón Eliminar o cualquier elemento dentro de él
            const isDeleteButton = e.target.classList.contains('btn-danger') ||
                                   e.target.closest('.btn-danger');

            const isCancelButton = e.target.classList.contains('btn-secondary') ||
                                   e.target.closest('.btn-secondary');

            const isCloseButton = e.target.classList.contains('btn-close') ||
                                  e.target.closest('.btn-close');

            if (isDeleteButton && !isCancelButton && !isCloseButton) {
              e.preventDefault();
              e.stopPropagation();
              e.stopImmediatePropagation();
              console.log('Eliminando álbum...');
              deleteForm.submit();
              return false;
            }
          }, true); // Usar capture para interceptar ANTES

          console.log('Listener configurado correctamente');
        }

        // Cerrar al hacer clic en backdrop
        backdrop.onclick = closeModal;

        // ESC para cerrar
        document.addEventListener('keydown', function escHandler(e) {
          if (e.key === 'Escape') {
            closeModal();
            document.removeEventListener('keydown', escHandler);
          }
        });

        console.log('Modal mostrado correctamente');

      } else {
        console.error('Modal no encontrado en el HTML recibido');
      }
    } catch (error) {
      console.error('Error al cargar el modal:', error.message);
    }

  })

  const buscador = document.querySelector('#buscador');
  if (buscador) {
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
  }

});

