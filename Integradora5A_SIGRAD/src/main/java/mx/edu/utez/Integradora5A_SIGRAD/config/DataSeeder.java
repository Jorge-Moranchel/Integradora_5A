package mx.edu.utez.Integradora5A_SIGRAD.config;

import mx.edu.utez.Integradora5A_SIGRAD.model.Area;
import mx.edu.utez.Integradora5A_SIGRAD.model.Reserva;
import mx.edu.utez.Integradora5A_SIGRAD.model.Usuario;
import mx.edu.utez.Integradora5A_SIGRAD.repository.AreaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.ReservaRepository;
import mx.edu.utez.Integradora5A_SIGRAD.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, AreaRepository areaRepo, ReservaRepository reservaRepo) {
        return args -> {
            if (usuarioRepo.count() == 0) {

                // ==========================================
                // 1. CREAR MULTIPLES USUARIOS (Tu equipo)
                // ==========================================
                Usuario u1 = new Usuario();
                u1.setNombre("Osvaldo Enrique Meza"); u1.setMatricula("2024UTEZ001"); u1.setTelefono("7771112233");
                u1.setCarrera("Desarrollo de Software"); u1.setEmailInstitucional("20243ds051@utez.edu.mx");
                u1.setContrasena("12345"); u1.setRol("ADMIN"); u1.setEstado(true);
                u1.setValidado(true); // ✅ Validado desde el inicio

                Usuario u2 = new Usuario();
                u2.setNombre("Jorge Emanuel Moranchel"); u2.setMatricula("2024UTEZ002"); u2.setTelefono("7774445566");
                u2.setCarrera("Desarrollo de Software"); u2.setEmailInstitucional("jorge@utez.edu.mx");
                u2.setContrasena("12345"); u2.setRol("ESTUDIANTE"); u2.setEstado(true);
                u2.setValidado(true); // ✅ Validado desde el inicio

                Usuario u3 = new Usuario();
                u3.setNombre("Alan Gadiel Araujo"); u3.setMatricula("2024UTEZ003"); u3.setTelefono("7777778899");
                u3.setCarrera("Redes Digitales"); u3.setEmailInstitucional("alan@utez.edu.mx");
                u3.setContrasena("12345"); u3.setRol("DOCENTE"); u3.setEstado(true);
                u3.setValidado(true); // ✅ Validado desde el inicio

                usuarioRepo.saveAll(Arrays.asList(u1, u2, u3));

                // ==========================================
                // 2. CREAR 5 ÁREAS DEPORTIVAS DIFERENTES
                // ==========================================
                Area a1 = new Area();
                a1.setNombre("Cancha de Fútbol Rápido"); a1.setTipo("Fútbol"); a1.setUbicacion("Campus Temixco - Sur");
                a1.setHoraApertura("07:00"); a1.setHoraCierre("20:00"); a1.setEstado("disponible");

                Area a2 = new Area();
                a2.setNombre("Cancha de Básquetbol Techada"); a2.setTipo("Básquetbol"); a2.setUbicacion("Edificio D");
                a2.setHoraApertura("07:00"); a2.setHoraCierre("21:00"); a2.setEstado("disponible");

                Area a3 = new Area();
                a3.setNombre("Cancha de Voleibol"); a3.setTipo("Voleibol"); a3.setUbicacion("Zona de Cafetería");
                a3.setHoraApertura("08:00"); a3.setHoraCierre("18:00"); a3.setEstado("disponible");

                Area a4 = new Area();
                a4.setNombre("Gimnasio de Pesas"); a4.setTipo("Gimnasio"); a4.setUbicacion("Edificio C");
                a4.setHoraApertura("06:00"); a4.setHoraCierre("22:00"); a4.setEstado("disponible");

                Area a5 = new Area();
                a5.setNombre("Pista de Atletismo"); a5.setTipo("Atletismo"); a5.setUbicacion("Estadio Principal");
                a5.setHoraApertura("06:00"); a5.setHoraCierre("20:00"); a5.setEstado("mantenimiento"); // Una bloqueada

                areaRepo.saveAll(Arrays.asList(a1, a2, a3, a4, a5));

                // ==========================================
                // 3. CREAR RESERVAS PARA HOY (Para disparar la ocupación)
                // ==========================================
                String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // 5 Reservas para hoy en distintas canchas
                crearReserva(reservaRepo, u1, a1, hoy, "08:00", "10:00", "Torneo Interno", "CONFIRMADA");
                crearReserva(reservaRepo, u2, a1, hoy, "10:00", "12:00", "Retas 5to Cuatri", "CONFIRMADA");
                crearReserva(reservaRepo, u3, a2, hoy, "14:00", "16:00", "Práctica", "CONFIRMADA");
                crearReserva(reservaRepo, u1, a4, hoy, "07:00", "09:00", "Rutina mañana", "CONFIRMADA");
                crearReserva(reservaRepo, u2, a3, hoy, "12:00", "14:00", "Torneo Voleibol", "CONFIRMADA");

                // Esta reserva es de hoy pero está CANCELADA (El dashboard NO debe contarla)
                crearReserva(reservaRepo, u3, a2, hoy, "16:00", "18:00", "Cancelado por lluvia", "CANCELADA");

                // ==========================================
                // 4. CREAR RESERVAS HISTÓRICAS (Para la gráfica mensual)
                // ==========================================
                // Noviembre y Diciembre 2025
                crearReserva(reservaRepo, u1, a1, "2025-11-15", "10:00", "12:00", "Clase Deportes", "CONFIRMADA");
                crearReserva(reservaRepo, u2, a2, "2025-12-05", "14:00", "16:00", "Torneo Fin de Año", "CONFIRMADA");
                crearReserva(reservaRepo, u3, a1, "2025-12-10", "12:00", "14:00", "Amistoso", "CONFIRMADA");

                // Enero y Febrero 2026
                crearReserva(reservaRepo, u1, a4, "2026-01-20", "08:00", "10:00", "Propósitos de año nuevo", "CONFIRMADA");
                crearReserva(reservaRepo, u2, a4, "2026-01-22", "08:00", "10:00", "Gimnasio", "CONFIRMADA");
                crearReserva(reservaRepo, u3, a3, "2026-02-14", "16:00", "18:00", "Retas de la amistad", "CONFIRMADA");
                crearReserva(reservaRepo, u1, a1, "2026-02-28", "10:00", "12:00", "Fútbol", "CONFIRMADA");

                // Una a futuro (Abril 2026)
                crearReserva(reservaRepo, u2, a2, "2026-04-10", "12:00", "14:00", "Torneo Primavera", "CONFIRMADA");

                System.out.println("✅ Base de datos H2 poblada con datos masivos exitosamente.");
            }
        };
    }

    // Método auxiliar para no repetir tantas líneas de código
    private void crearReserva(ReservaRepository repo, Usuario u, Area a, String fecha, String inicio, String fin, String desc, String estado) {
        Reserva r = new Reserva();
        r.setUsuario(u);
        r.setArea(a);
        r.setFecha(fecha);
        r.setHoraInicio(inicio);
        r.setHoraFin(fin);
        r.setDescripcion(desc);
        r.setEstado(estado);
        repo.save(r);
    }
}