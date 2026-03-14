import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 1. Importamos el navegante
import { Mail, Lock } from 'lucide-react';
import '../style/Login.css';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate(); // 2. Inicializamos el navegante

  const handleLogin = async (e) => {
    e.preventDefault();
    
    // Mostramos en consola para depurar
    console.log("Intentando conectar con el backend...");

    try {
      // 3. Hacemos la petición real al controlador que me pasaste
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          correo: email,      // Coincide con tu request.get("correo") en Java
          password: password  // Coincide con tu request.get("password") en Java
        }),
      });

      if (response.ok) {
        const data = await response.json();
        console.log("Login exitoso:", data);
        
        // Guardamos los datos en el navegador por si los necesitas
        localStorage.setItem('user', JSON.stringify(data));

        // 4. ¡EL SALTO MÁGICO! Nos manda al dashboard
        navigate('/dashboard'); 
      } else {
        const errorData = await response.json();
        alert(errorData.message || "Credenciales incorrectas");
      }
    } catch (error) {
      console.error("Error de conexión:", error);
      alert("No se pudo conectar con el servidor. Revisa que el Backend esté encendido.");
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
            Iniciar sesion
          </button>
        </form>
      </div>
    </div>
  );
}