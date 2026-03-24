package com.israel.alumnos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.israel.alumnos.model.Alumno;
import com.israel.alumnos.repository.AlumnoRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/alumnos")
@CrossOrigin(origins = "*")
public class AlumnoController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    // Metodo get para traer todos los alumnos de la base de datos
    @GetMapping("/traer-alumnos")
    public List<Alumno> TraerAlumnos() {
        return alumnoRepository.findAll();
    }

    @GetMapping("/traer-alumno/{id}")
    public ResponseEntity<Alumno> TraerUnAlumno(@PathVariable Long id) {
        return alumnoRepository.findById(id)
                .map(alumno -> ResponseEntity.ok(alumno))
                .orElse(ResponseEntity.notFound().build());
    }

    // Metodo para insertar un alumno en la base de datos
    @PostMapping("/insertar-alumnos")
    public ResponseEntity<?> insertarAlumno(@RequestBody Alumno alumno) {
        // Validaciones de obligatoriedad
        if (alumno.getNumeroControl() == null || alumno.getNumeroControl().trim().isEmpty() ||
            alumno.getNombre() == null || alumno.getNombre().trim().isEmpty() ||
            alumno.getApellidoPaterno() == null || alumno.getApellidoPaterno().trim().isEmpty() ||
            alumno.getApellidoMaterno() == null || alumno.getApellidoMaterno().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Todos los campos de nombre y apellidos son obligatorios.");
        }

        // Validación de solo letras en nombre y apellidos
        String nombreRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
        if (!alumno.getNombre().matches(nombreRegex) || 
            !alumno.getApellidoPaterno().matches(nombreRegex) || 
            !alumno.getApellidoMaterno().matches(nombreRegex)) {
            return ResponseEntity.badRequest().body("El nombre y apellidos solo deben contener letras.");
        }

        // Validación de email
        if (alumno.getEmail() == null || !alumno.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("El email debe ser válido y contener un @.");
        }

        // Validación de número de control (8 dígitos iniciando con 26)
        if (!alumno.getNumeroControl().matches("^26\\d{6}$")) {
            return ResponseEntity.badRequest().body("El número de control debe tener 8 dígitos e iniciar con 26.");
        }

        if (alumnoRepository.existsByNumeroControl(alumno.getNumeroControl())) {
            return ResponseEntity.badRequest().body("El número de control ya existe.");
        }

        // Imagen por defecto
        if (alumno.getImagenURL() == null || alumno.getImagenURL().trim().isEmpty()) {
            alumno.setImagenURL("https://cdn-icons-png.flaticon.com/512/3135/3135715.png");
        }

        return ResponseEntity.ok(alumnoRepository.save(alumno));
    }

    // Metodo para editar un alumno en la base de datos
    @PutMapping("/editar-alumnos/{id}")
    public ResponseEntity<?> actualizarAlumno(@PathVariable Long id, @RequestBody Alumno alumno) {
        // Validaciones de obligatoriedad
        if (alumno.getNumeroControl() == null || alumno.getNumeroControl().trim().isEmpty() ||
            alumno.getNombre() == null || alumno.getNombre().trim().isEmpty() ||
            alumno.getApellidoPaterno() == null || alumno.getApellidoPaterno().trim().isEmpty() ||
            alumno.getApellidoMaterno() == null || alumno.getApellidoMaterno().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Todos los campos de nombre y apellidos son obligatorios.");
        }

        // Validación de solo letras en nombre y apellidos
        String nombreRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
        if (!alumno.getNombre().matches(nombreRegex) || 
            !alumno.getApellidoPaterno().matches(nombreRegex) || 
            !alumno.getApellidoMaterno().matches(nombreRegex)) {
            return ResponseEntity.badRequest().body("El nombre y apellidos solo deben contener letras.");
        }

        // Validación de email
        if (alumno.getEmail() == null || !alumno.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("El email debe ser válido y contener un @.");
        }

        // Validación de número de control (8 dígitos iniciando con 26)
        if (!alumno.getNumeroControl().matches("^26\\d{6}$")) {
            return ResponseEntity.badRequest().body("El número de control debe tener 8 dígitos e iniciar con 26.");
        }

        if (alumnoRepository.existsByNumeroControlAndIdNot(alumno.getNumeroControl(), id)) {
            return ResponseEntity.badRequest().body("El número de control ya está en uso por otro alumno.");
        }

        return alumnoRepository.findById(id).map(alumnoExistente -> {
            alumnoExistente.setNombre(alumno.getNombre());
            alumnoExistente.setApellidoPaterno(alumno.getApellidoPaterno());
            alumnoExistente.setApellidoMaterno(alumno.getApellidoMaterno());
            alumnoExistente.setEmail(alumno.getEmail());
            alumnoExistente.setNumeroControl(alumno.getNumeroControl());
            alumnoExistente.setTelefono(alumno.getTelefono());
            alumnoExistente.setCarrera(alumno.getCarrera());
            if (alumno.getImagenURL() == null || alumno.getImagenURL().trim().isEmpty()) {
                alumnoExistente.setImagenURL("https://cdn-icons-png.flaticon.com/512/3135/3135715.png");
            } else {
                alumnoExistente.setImagenURL(alumno.getImagenURL());
            }
            Alumno actualizado = alumnoRepository.save(alumnoExistente);
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // metodo para eliminar un alumno de la base de datos
    @DeleteMapping("/eliminar-alumnos/{id}")
    public void eliminarAlumno(@PathVariable Long id) {
        alumnoRepository.deleteById(id);
    }

}
