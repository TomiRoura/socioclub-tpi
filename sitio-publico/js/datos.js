// =========================================================
// datos.js
// Clase Actividad y arreglo con todas las actividades del club.
// Lo usan actividades.html (catálogo) y preinscripcion.html (select).
// =========================================================

// ---------- Clase Actividad ----------
// Representa una actividad del club con sus datos principales.
class Actividad {
  constructor(nombre, disciplina, horario, edadMinima, edadMaxima, cupo, cuota, profesor) {
    this.nombre = nombre;
    this.disciplina = disciplina;
    this.horario = horario;
    this.edadMinima = edadMinima;
    this.edadMaxima = edadMaxima;
    this.cupo = cupo;
    this.cuota = cuota; // número en pesos, sin puntos (ej: 18000)
    this.profesor = profesor;
  }

  // Devuelve true si la edad está dentro del rango de la actividad.
  esAptaParaEdad(edad) {
    return edad >= this.edadMinima && edad <= this.edadMaxima;
  }

  // Devuelve el rango de edades como texto, por ejemplo "6 a 12 años".
  obtenerTextoEdades() {
    return this.edadMinima + " a " + this.edadMaxima + " años";
  }

  // Devuelve la cuota con formato de pesos argentinos, por ejemplo "$18.000".
  obtenerCuotaFormateada() {
    return "$" + this.cuota.toLocaleString("es-AR");
  }
}

// ---------- Arreglo de actividades ----------
// Datos exactos de la consigna. Cada elemento es un objeto Actividad.
const listaActividades = [
  new Actividad("Fútbol infantil", "Fútbol", "Lun y Mié 18:00", 6, 12, 40, 18000, "Diego Ferreyra"),
  new Actividad("Vóley mixto", "Vóley", "Mar y Jue 19:00", 13, 60, 24, 20000, "Laura Ceballos"),
  new Actividad("Básquet juvenil", "Básquet", "Lun y Vie 19:30", 12, 17, 20, 19000, "Martín Oviedo"),
  new Actividad("Natación", "Natación", "Mar y Jue 17:00", 5, 70, 30, 26000, "Sofía Bustos"),
  new Actividad("Patín artístico", "Patín", "Mié y Vie 17:30", 5, 15, 25, 21000, "Carla Peralta"),
  new Actividad("Ajedrez", "Ajedrez", "Sáb 10:00", 8, 99, 16, 9000, "Raúl Giménez"),
  new Actividad("Funcional adultos", "Fitness", "Lun, Mié y Vie 20:30", 18, 65, 30, 22000, "Paula Luna"),
  new Actividad("Yoga", "Bienestar", "Mar y Jue 9:00", 16, 99, 15, 17000, "Inés Arguello")
];
