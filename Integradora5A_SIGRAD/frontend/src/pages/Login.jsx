import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mail, Lock } from 'lucide-react';
import Swal from 'sweetalert2';
import '../Style/Login.css';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    console.log("Intentando conectar con el backend...");

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          correo: email,
          password: password,
          esWeb: "true" // ✅ LE AVISAMOS AL BACKEND QUE SOMOS LA WEB
        }),
      });

      const data = await response.json();

      if (response.ok) {
        console.log("Login exitoso:", data);

        // ✅ GUARDAMOS EL USUARIO Y EL PASE DE ENTRADA (TOKEN)
        localStorage.setItem('user', JSON.stringify(data));
        localStorage.setItem('adminToken', 'autenticado');

        Swal.fire({
          icon: 'success',
          title: '¡Bienvenido!',
          text: data.message || 'Inicio de sesión correcto',
          showConfirmButton: false,
          timer: 2000,
          background: '#fff',
          iconColor: '#00a854'
        }).then(() => {
          navigate('/dashboard');
        });

      } else {
        Swal.fire({
          icon: 'error',
          title: '¡Ups!',
          text: data.message || data.mensaje || "Credenciales incorrectas",
          confirmButtonColor: '#d33',
          confirmButtonText: 'Intentar otra vez'
        });
      }
    } catch (error) {
      console.error("Error de conexión:", error);
      Swal.fire({
        icon: 'warning',
        title: 'Sin conexión',
        text: 'No se pudo conectar con el servidor. Revisa que el Backend esté encendido en IntelliJ.',
        confirmButtonColor: '#f8bb86',
        confirmButtonText: 'Entendido'
      });
    }
  };

  return (
      <div className="login-screen">
        <div className="login-card">
          <h1 className="login-title">RESERVA<br />DEPORTIVA</h1>
          <p className="login-subtitle">Bienvenido</p>

          <form onSubmit={handleLogin}>
            <div className="input-container">
              <label>Correo:</label>
              <div className="input-field-group">
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <Mail className="input-icon" size={20} />
              </div>
            </div>

            <div className="input-container">
              <label>Contraseña:</label>
              <div className="input-field-group">
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <Lock className="input-icon" size={20} />
              </div>
            </div>

            <button type="submit" className="btn-submit">
              Iniciar sesión
            </button>
          </form>
        </div>
      </div>
  );
}