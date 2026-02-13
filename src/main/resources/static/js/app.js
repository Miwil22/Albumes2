document.addEventListener('DOMContentLoaded', () =>{

    // Enlace logout
    const lnkSalir = document.querySelector('#logoutLink');
    lnkSalir.addEventListener('click',  (event) => {
        event.preventDefault();
        document.querySelector('#logoutForm').submit();
    })

});
