// =========================================================
// actividades.js
// Dibuja el catálogo de actividades a partir de listaActividades
// (definida en datos.js) y lo filtra por disciplina y edad.
// =========================================================

// ---------- Referencias a elementos del HTML ----------
const contenedorLista = document.getElementById("lista-actividades");
const textoContador = document.getElementById("contador-resultados");
const selectDisciplina = document.getElementById("filtro-disciplina");
const inputEdad = document.getElementById("filtro-edad");
const botonLimpiar = document.getElementById("boton-limpiar");

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
  // "revelar" hace que la tarjeta aparezca con animación (ver efectos.js)
  const tarjeta = crearElemento("article", "tarjeta revelar", "");

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

  // Muestra con animación las tarjetas nuevas que ya están en pantalla
  revisarElementosVisibles();
}

// ---------- Cargar opciones del select ----------
// Agrega una opción por cada disciplina, sin repetir.
function cargarDisciplinas() {
  const disciplinas = [];

  for (const actividad of listaActividades) {
    if (!disciplinas.includes(actividad.disciplina)) {
      disciplinas.push(actividad.disciplina);
    }
  }

  for (const disciplina of disciplinas) {
    const opcion = document.createElement("option");
    opcion.value = disciplina;
    opcion.textContent = disciplina;
    selectDisciplina.appendChild(opcion);
  }
}

// ---------- Aplicar filtros ----------
// Lee los dos filtros, arma un arreglo nuevo con las actividades que
// cumplen ambos y redibuja la lista.
function aplicarFiltros() {
  const disciplinaElegida = selectDisciplina.value;
  const textoEdad = inputEdad.value;
  const edad = Number(textoEdad);

  const actividadesFiltradas = [];

  for (const actividad of listaActividades) {
    // Filtro por disciplina ("todas" significa que no se filtra)
    const cumpleDisciplina = disciplinaElegida === "todas" || actividad.disciplina === disciplinaElegida;

    // Filtro por edad (si el campo está vacío no se filtra)
    const cumpleEdad = textoEdad === "" || actividad.esAptaParaEdad(edad);

    if (cumpleDisciplina && cumpleEdad) {
      actividadesFiltradas.push(actividad);
    }
  }

  dibujarActividades(actividadesFiltradas);
}

// ---------- Limpiar filtros ----------
// Vuelve los filtros a su valor inicial y muestra todo.
function limpiarFiltros() {
  selectDisciplina.value = "todas";
  inputEdad.value = "";
  aplicarFiltros();
}

// ---------- Eventos ----------
// "change" se dispara al elegir otra opción del select.
// "input" se dispara cada vez que se escribe o borra un número.
selectDisciplina.addEventListener("change", aplicarFiltros);
inputEdad.addEventListener("input", aplicarFiltros);
botonLimpiar.addEventListener("click", limpiarFiltros);

// ---------- Inicio ----------
// Al cargar la página se llenan las disciplinas y se muestran todas las actividades.
cargarDisciplinas();
dibujarActividades(listaActividades);
