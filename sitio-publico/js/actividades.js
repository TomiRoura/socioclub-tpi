// =========================================================
// actividades.js
// Dibuja el catálogo de actividades a partir de listaActividades
// (definida en datos.js).
// =========================================================

// ---------- Referencias a elementos del HTML ----------
const contenedorLista = document.getElementById("lista-actividades");
const textoContador = document.getElementById("contador-resultados");

// ---------- Función auxiliar ----------
// Crea un elemento con una clase y un texto (para no repetir código).
function crearElemento(etiqueta, clase, texto) {
  const elemento = document.createElement(etiqueta);
  if (clase !== "") {
    elemento.className = clase;
  }
  if (texto !== "") {
    elemento.textContent = texto;
  }
  return elemento;
}

// ---------- Crear una tarjeta ----------
// Recibe un objeto Actividad y devuelve un <article> listo para insertar.
function crearTarjeta(actividad) {
  const tarjeta = crearElemento("article", "tarjeta", "");

  // Título y disciplina
  tarjeta.appendChild(crearElemento("h3", "", actividad.nombre));
  tarjeta.appendChild(crearElemento("p", "etiqueta-disciplina", actividad.disciplina));

  // Lista con los datos de la actividad
  const listaDatos = crearElemento("ul", "datos-actividad", "");
  listaDatos.appendChild(crearElemento("li", "", "Días y horario: " + actividad.horario));
  listaDatos.appendChild(crearElemento("li", "", "Edades: " + actividad.obtenerTextoEdades()));
  listaDatos.appendChild(crearElemento("li", "", "Cupo: " + actividad.cupo + " personas"));
  listaDatos.appendChild(crearElemento("li", "", "Profesor/a: " + actividad.profesor));
  tarjeta.appendChild(listaDatos);

  // Precio de la cuota
  tarjeta.appendChild(crearElemento("p", "precio", actividad.obtenerCuotaFormateada() + " por mes"));

  // Enlace a la preinscripción con la actividad ya elegida
  const enlace = crearElemento("a", "boton", "Preinscribirme");
  enlace.href = "preinscripcion.html?actividad=" + encodeURIComponent(actividad.nombre);
  tarjeta.appendChild(enlace);

  return tarjeta;
}

// ---------- Dibujar la lista ----------
// Borra lo que había y vuelve a crear una tarjeta por cada actividad recibida.
function dibujarActividades(actividades) {
  contenedorLista.innerHTML = "";

  for (const actividad of actividades) {
    contenedorLista.appendChild(crearTarjeta(actividad));
  }

  // Mensaje con la cantidad de resultados
  if (actividades.length === 0) {
    textoContador.textContent = "No hay actividades que coincidan con tu búsqueda.";
  } else if (actividades.length === 1) {
    textoContador.textContent = "Se encontró 1 actividad.";
  } else {
    textoContador.textContent = "Se encontraron " + actividades.length + " actividades.";
  }
}

// ---------- Inicio ----------
// Al cargar la página se muestran todas las actividades.
dibujarActividades(listaActividades);
