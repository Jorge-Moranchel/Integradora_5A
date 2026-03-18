import Swal from 'sweetalert2';

const handleLogin = async (e) => {
    e.preventDefault();

    try {
        const res = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ correo, password })
        });

        const data = await res.json();

        if (res.ok) {
            // ALERTA DE ÉXITO
            Swal.fire({
                icon: 'success',
                title: data.message,
                showConfirmButton: false,
                timer: 2000,
                background: '#fff',
                iconColor: '#00a854' // El verde de la UTEZ
            }).then(() => {
                window.location.href = "/dashboard";
            });
        } else {
            // ALERTA DE ERROR BONITA
            Swal.fire({
                icon: 'error',
                title: '¡Ups!',
                text: data.message,
                confirmButtonColor: '#d33',
                confirmButtonText: 'Intentar otra vez'
            });
        }
    } catch (err) {
        Swal.fire('Error', 'El servidor no responde. Revisa la conexión.', 'error');
    }
};