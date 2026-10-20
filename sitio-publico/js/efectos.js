// =========================================================
// efectos.js
// Efectos visuales al hacer scroll. Se usa en las 3 páginas.
// - El encabezado se esconde al bajar y vuelve a aparecer al subir.
// - Una barra arriba muestra cuánto de la página se recorrió.
// - Los bloques con la clase "revelar" aparecen deslizándose:
//   desde abajo cuando bajás y desde arriba cuando subís.
// - Un botón flotante lleva de vuelta al principio.
// =========================================================

// ---------- Referencias a elementos del HTML ----------
const encabezado = document.querySelector(".encabezado");
const barraProgreso = document.getElementById("barra-progreso");
const botonArriba = document.getElementById("boton-arriba");

// Guarda la posición del scroll anterior para saber si se sube o se baja.
let ultimaPosicion = window.scrollY;

// Esta clase avisa al CSS que JavaScript está funcionando.
// Si JS no carga, los bloques "revelar" se ven normalmente.
document.documentElement.classList.add("con-efectos");

// ---------- Encabezado ----------
// Bajando: se esconde para dejar más lugar. Subiendo: vuelve a aparecer.
function actualizarEncabezado() {
  const posicionActual = window.scrollY;

  if (posicionActual > ultimaPosicion && posicionActual > 120) {
    encabezado.classList.add("encabezado-oculto");
  } else {
    encabezado.classList.remove("encabezado-oculto");
  }

  // Apenas se scrollea un poco, el encabezado toma sombra
  if (posicionActual > 10) {
    encabezado.classList.add("encabezado-con-sombra");
  } else {
    encabezado.classList.remove("encabezado-con-sombra");
  }

  ultimaPosicion = posicionActual;
}

// ---------- Barra de progreso ----------
// Calcula qué porcentaje de la página ya se recorrió.
function actualizarBarraProgreso() {
  const alturaTotal = document.documentElement.scrollHeight - window.innerHeight;
  let porcentaje = 0;

  if (alturaTotal > 0) {
    porcentaje = (window.scrollY / alturaTotal) * 100;
  }

  barraProgreso.style.width = porcentaje + "%";
}

// ---------- Botón "volver arriba" ----------
// Solo se muestra cuando ya se bajó bastante.
function actualizarBotonArriba() {
  if (window.scrollY > 400) {
    botonArriba.classList.add("mostrar");
  } else {
    botonArriba.classList.remove("mostrar");
  }
}

function irArriba() {
  window.scrollTo({ top: 0, behavior: "smooth" });
}

// ---------- Bloques que aparecen ----------
// Revisa cada elemento con clase "revelar":
// - si está dentro de la pantalla, le agrega "visible" (aparece).
// - si salió por arriba, le pone "desde-arriba" para que al subir
//   entre bajando desde arriba.
// - si salió por abajo, le saca "desde-arriba" para que al bajar
//   entre subiendo desde abajo.
// Es una función global porque actividades.js la llama después de
// redibujar las tarjetas.
function revisarElementosVisibles() {
  const elementos = document.querySelectorAll(".revelar");
  const altoVentana = window.innerHeight;

  for (const elemento of elementos) {
    const posicion = elemento.getBoundingClientRect();

    const estaEnPantalla = posicion.top < altoVentana * 0.92 && posicion.bottom > altoVentana * 0.08;

    if (estaEnPantalla) {
      elemento.classList.add("visible");
    } else {
      elemento.classList.remove("visible");

      if (posicion.bottom <= altoVentana * 0.08) {
        elemento.classList.add("desde-arriba");
      } else {
        elemento.classList.remove("desde-arriba");
      }
    }
  }
}

// ---------- Todo lo que pasa al scrollear ----------
function alHacerScroll() {
  actualizarEncabezado();
  actualizarBarraProgreso();
  actualizarBotonArriba();
  revisarElementosVisibles();
}

// ---------- Eventos ----------
window.addEventListener("scroll", alHacerScroll);
window.addEventListener("resize", revisarElementosVisibles);
botonArriba.addEventListener("click", irArriba);

// ---------- Inicio ----------
// Muestra los bloques que ya se ven al cargar la página.
revisarElementosVisibles();
actualizarBarraProgreso();
