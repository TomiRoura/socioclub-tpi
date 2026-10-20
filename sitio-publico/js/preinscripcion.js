// =========================================================
// preinscripcion.js
// Carga las actividades en el select, valida el formulario y
// muestra un mensaje de confirmación (todavía no hay backend).
// =========================================================

// ---------- Referencias a elementos del HTML ----------
const formulario = document.getElementById("formulario-preinscripcion");
const inputNombre = document.getElementById("nombre");
const inputDni = document.getElementById("dni");
const inputEmail = document.getElementById("email");
const inputEdad = document.getElementById("edad");
const selectActividad = document.getElementById("actividad");
const mensajeConfirmacion = document.getElementById("mensaje-confirmacion");
const textoConfirmacion = document.getElementById("texto-confirmacion");
const botonVolver = document.getElementById("boton-volver");
const botonOtra = document.getElementById("boton-otra");

// ---------- Expresiones regulares ----------
// DNI: solo números, entre 7 y 8 dígitos (sin puntos).
const patronDni = /^[0-9]{7,8}$/;
// Email: algo@algo.algo, sin espacios.
const patronEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// ---------- Cargar actividades en el select ----------
function cargarActividades() {
  for (const actividad of listaActividades) {
    const opcion = document.createElement("option");
    opcion.value = actividad.nombre;
    opcion.textContent = actividad.nombre + " (" + actividad.obtenerTextoEdades() + ")";
    selectActividad.appendChild(opcion);
  }
}

// ---------- Preseleccionar actividad desde la URL ----------
// Si venimos desde una tarjeta del catálogo, la URL trae
// "?actividad=Yoga". Usamos location.search para leer ese dato
// y dejar la actividad ya elegida en el select.
function preseleccionarActividad() {
  const parametros = new URLSearchParams(location.search);
  const actividadElegida = parametros.get("actividad");

  if (actividadElegida !== null) {
    selectActividad.value = actividadElegida;
  }
}

// ---------- Buscar una actividad por nombre ----------
function buscarActividad(nombre) {
  for (const actividad of listaActividades) {
    if (actividad.nombre === nombre) {
      return actividad;
    }
  }
  return null;
}

// ---------- Mostrar y limpiar errores ----------
// Escribe el mensaje en el <p> de error que está debajo del campo
// y marca el campo en rojo.
function mostrarError(campo, mensaje) {
  const parrafoError = document.getElementById("error-" + campo.id);
  parrafoError.textContent = mensaje;
  campo.classList.add("campo-invalido");
}

function limpiarError(campo) {
  const parrafoError = document.getElementById("error-" + campo.id);
  parrafoError.textContent = "";
  campo.classList.remove("campo-invalido");
}

// ---------- Validaciones de cada campo ----------
// Cada función devuelve true si el campo está bien y false si tiene error.

function validarNombre() {
  const valor = inputNombre.value.trim();
  if (valor === "") {
    mostrarError(inputNombre, "Ingresá tu nombre y apellido.");
    return false;
  }
  if (valor.length < 3) {
    mostrarError(inputNombre, "El nombre es demasiado corto.");
    return false;
  }
  limpiarError(inputNombre);
  return true;
}

function validarDni() {
  const valor = inputDni.value.trim();
  if (valor === "") {
    mostrarError(inputDni, "Ingresá tu DNI.");
    return false;
  }
  if (!patronDni.test(valor)) {
    mostrarError(inputDni, "El DNI debe tener 7 u 8 números, sin puntos ni espacios.");
    return false;
  }
  limpiarError(inputDni);
  return true;
}

function validarEmail() {
  const valor = inputEmail.value.trim();
  if (valor === "") {
    mostrarError(inputEmail, "Ingresá tu correo electrónico.");
    return false;
  }
  if (!patronEmail.test(valor)) {
    mostrarError(inputEmail, "El correo no es válido. Ejemplo: nombre@ejemplo.com");
    return false;
  }
  limpiarError(inputEmail);
  return true;
}

function validarEdad() {
  const valor = inputEdad.value.trim();
  const edad = Number(valor);
  if (valor === "") {
    mostrarError(inputEdad, "Ingresá la edad.");
    return false;
  }
  if (!Number.isInteger(edad) || edad < 1 || edad > 99) {
    mostrarError(inputEdad, "La edad debe ser un número entero entre 1 y 99.");
    return false;
  }
  limpiarError(inputEdad);
  return true;
}

// La actividad se valida después de la edad, porque usa esAptaParaEdad().
function validarActividad(edadEsValida) {
  const actividad = buscarActividad(selectActividad.value);
  if (actividad === null) {
    mostrarError(selectActividad, "Elegí una actividad.");
    return false;
  }
  if (edadEsValida && !actividad.esAptaParaEdad(Number(inputEdad.value))) {
    mostrarError(selectActividad, "Esta actividad es para personas de " + actividad.obtenerTextoEdades() + ".");
    return false;
  }
  limpiarError(selectActividad);
  return true;
}

// ---------- Envío del formulario ----------
function enviarFormulario(evento) {
  // Evita que la página se recargue (no hay backend todavía)
  evento.preventDefault();

  // Se validan todos los campos para mostrar todos los errores juntos
  const nombreValido = validarNombre();
  const dniValido = validarDni();
  const emailValido = validarEmail();
  const edadValida = validarEdad();
  const actividadValida = validarActividad(edadValida);

  if (nombreValido && dniValido && emailValido && edadValida && actividadValida) {
    // Todo bien: se oculta el formulario y se muestra la confirmación
    textoConfirmacion.textContent =
      "Gracias, " + inputNombre.value.trim() + ". Recibimos tu preinscripción a " +
      selectActividad.value + ". Te vamos a escribir a " + inputEmail.value.trim() +
      " para confirmar tu lugar.";
    formulario.hidden = true;
    mensajeConfirmacion.hidden = false;
  }
}

// ---------- Nueva preinscripción ----------
// Vacía el formulario y lo vuelve a mostrar.
function nuevaPreinscripcion() {
  formulario.reset();
  mensajeConfirmacion.hidden = true;
  formulario.hidden = false;
  inputNombre.focus();
}

// ---------- Botón Volver ----------
// Usa el objeto history para volver a la página anterior.
// Si la persona entró directo (no hay página anterior), la llevamos al inicio.
function volver() {
  if (history.length > 1) {
    history.back();
  } else {
    location.href = "index.html";
  }
}

// ---------- Eventos ----------
formulario.addEventListener("submit", enviarFormulario);
botonVolver.addEventListener("click", volver);
botonOtra.addEventListener("click", nuevaPreinscripcion);

// Cuando la persona corrige un campo con error, se vuelve a validar al salir de él.
inputNombre.addEventListener("blur", function () {
  if (inputNombre.classList.contains("campo-invalido")) {
    validarNombre();
  }
});
inputDni.addEventListener("blur", function () {
  if (inputDni.classList.contains("campo-invalido")) {
    validarDni();
  }
});
inputEmail.addEventListener("blur", function () {
  if (inputEmail.classList.contains("campo-invalido")) {
    validarEmail();
  }
});

// ---------- Inicio ----------
cargarActividades();
preseleccionarActividad();
