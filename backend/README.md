# Backend SocioClub

Un solo proyecto Spring Boot (Maven) con dos caras que comparten la misma capa de servicio:

- **Secretaría** (Parte B): controladores MVC + vistas Thymeleaf.
- **API REST** (Parte C): controladores `@RestController` bajo `/api`, que consume la app React.

Cómo crear la base y levantarlo: ver el README de la raíz.

## Paquetes (arquitectura en capas)

```
com.losalgarrobos.socioclub
├── model           Entidades JPA: Socio, Actividad, Inscripcion + enums EstadoCuota y EstadoInscripcion
├── repository      Interfaces que extienden JpaRepository (Spring Data arma el SQL)
├── service         Lógica y reglas de negocio RN1 a RN5 + excepciones
├── dto             InscripcionRequest: cuerpo { socioId, actividadId }
├── controller      Controladores MVC de la secretaría (devuelven vistas Thymeleaf)
│   └── api         Controladores REST (devuelven JSON) + manejador de errores de la API
└── config          CORS para http://localhost:3000
```

Regla: **ningún controlador ni vista accede a un repositorio**. Todo pasa por los servicios.

```
Vista Thymeleaf ─┐                                ┌─> Repositorio ─> MySQL
                 ├─> Controlador ─> Servicio ─────┤
App React ───────┘   (MVC o REST)   (reglas)      └─> Repositorio ─> MySQL
```

`InscripcionController` (secretaría) e `InscripcionRestController` (API) llaman al mismo
`InscripcionService.inscribir()`. Las reglas se escriben una sola vez y se validan igual entren por donde entren.

## Reglas de negocio (InscripcionService)

Orden de evaluación (el mismo que `app-socios/src/utils/reglas.js`): **RN4 → RN2 → RN5 → RN3 → RN1**.

| Regla | Falla cuando | Con qué se controla | Mensaje |
|-------|--------------|---------------------|---------|
| RN4 | `estadoCuota` es MOROSO | campo del socio | Debe regularizar su cuota en secretaría |
| RN2 | ya tiene una ACTIVA en esa actividad | `existsBySocioIdAndActividadIdAndEstado` | El socio ya está inscripto en esta actividad |
| RN5 | ya tiene `MAX_ACTIVIDADES` ACTIVA | `countBySocioIdAndEstado` | Alcanzó el máximo de 3 actividades |
| RN3 | la edad no está entre mínima y máxima (incluidas) | `Socio.calcularEdad()` + `Actividad.esAptaParaEdad()` | La edad permitida para esta actividad es de X a Y años |
| RN1 | las ACTIVA igualan o superan el cupo | `contarPorActividadYEstado` (@Query) | La actividad no tiene cupo disponible |

- **Cambio en vivo "máximo de 3 a 4"**: `InscripcionService.MAX_ACTIVIDADES` (el mensaje usa la constante)
  y `MAX_ACTIVIDADES` en `app-socios/src/utils/constantes.js`.
- RN6 (descuento) se calcula en la app. La ficha del socio en la secretaría usa los mismos porcentajes
  (`InscripcionService.calcularCuotaMensual`).
- RN7 (DNI) se valida en la app y también en `SocioService` y en la base (`ck_socios_dni`).
- Baja lógica: la inscripción pasa a BAJA. Todas las consultas cuentan solo las ACTIVA, así quien se dio de
  baja puede volver y el cupo se libera solo.

## Repositorios

| Método | Tipo | Para qué |
|--------|------|----------|
| `SocioRepository.findByDni` | derivado | Ingreso a la app y DNI repetido |
| `SocioRepository.findByApellidoContainingIgnoreCase(apellido, pageable)` | derivado + paginación | Listado de socios |
| `SocioRepository.findAllByOrderByApellidoAscNombreAsc` | derivado | Desplegable del formulario de inscripción |
| `ActividadRepository.findByDisciplinaOrderByIdAsc` | derivado | `?disciplina=` |
| `ActividadRepository.findAllByOrderByIdAsc` | derivado | Listado de actividades |
| `InscripcionRepository.findBySocioIdAndEstadoOrderByFechaInscripcionAsc` | derivado | Inscripciones activas de un socio |
| `InscripcionRepository.findByActividadIdAndEstadoOrderByFechaInscripcionAsc` | derivado | Inscriptos de una actividad |
| `InscripcionRepository.existsBySocioIdAndActividadIdAndEstado` | derivado | RN2 |
| `InscripcionRepository.countBySocioIdAndEstado` | derivado | RN5 |
| `InscripcionRepository.contarPorActividadYEstado` | `@Query` (JPQL) | RN1, cupo disponible, borrar actividad |

Con `spring.jpa.show-sql=true` la consola muestra el SQL que Spring arma a partir de cada nombre.

## Pantallas de la secretaría

| Ruta | Qué hace |
|------|----------|
| `/` | Inicio: ocupación de cada actividad y accesos rápidos |
| `/actividades` | Listado con cupo disponible, filtro por disciplina, editar, eliminar, ver inscriptos |
| `/actividades/nueva`, `/actividades/{id}/editar` | Alta y modificación (mismo formulario, `th:object`/`th:field`) |
| `/actividades/{id}/inscriptos` | Inscriptos activos, cupo y baja |
| `/socios?apellido=&pagina=` | Listado paginado (10 por página) con búsqueda por apellido |
| `/socios/nuevo`, `/socios/{id}/editar` | Alta y modificación (incluye el estado de la cuota) |
| `/socios/{id}` | Ficha: actividades activas, cuota estimada con descuento, baja |
| `/inscripciones/nueva` | Inscribir: elegir socio y actividad. Las actividades llenas se muestran igual para poder demostrar RN1 |

Encabezado, mensajes y pie son fragmentos comunes (`templates/fragmentos.html`).

## Endpoints de la API (Anexo A)

| Verbo | Ruta | Respuestas |
|-------|------|-----------|
| GET | `/api/actividades` (acepta `?disciplina=Fútbol`) | 200 |
| GET | `/api/actividades/{id}` | 200 / 404 |
| POST | `/api/actividades` | 201 / 400 |
| PUT | `/api/actividades/{id}` | 200 / 400 / 404 |
| DELETE | `/api/actividades/{id}` | 204 / 404 / 409 si tiene inscriptos activos |
| GET | `/api/socios/dni/{dni}` | 200 / 404 |
| GET | `/api/socios/{id}/inscripciones` | 200 / 404 |
| POST | `/api/inscripciones` con `{ "socioId": 3, "actividadId": 1 }` | 201 / 404 / 409 |
| PUT | `/api/inscripciones/{id}/baja` | 200 / 404 |

Errores: `{ "error": "La actividad no tiene cupo disponible", "regla": "RN1" }`. `regla` va solo en los 409 de
RN1 a RN5. Los arma `ManejadorErroresApi` (`@RestControllerAdvice`): sin él, Spring devuelve su error por defecto
con `"error": "Conflict"` y la app perdería el motivo.

El DELETE de cualquiera de las 8 actividades iniciales da 409 (todas tienen inscriptos). Para probar el 204,
crear una actividad y borrar esa (la colección de Postman lo hace).

## Preguntas seguras en la defensa

**¿Por qué la regla del cupo está en el servicio y no en el controlador?** Porque hay dos controladores
(secretaría y API) que inscriben. Si la regla estuviera en uno, el otro la saltearía. En el servicio se escribe
una vez y vale para los dos.

**¿Qué es un bean y qué es la inyección?** Un objeto que crea y administra Spring (las clases con `@Service`,
`@Controller`, `@RestController`, `@Repository`, `@Configuration`). Cada servicio recibe lo que necesita por el
constructor: Spring ve el parámetro `ActividadRepository`, busca el bean de ese tipo y lo pasa.

**¿Cómo arma Spring el SQL de un método del repositorio?** Lee el nombre: `findByApellidoContainingIgnoreCase`
es `WHERE UPPER(apellido) LIKE UPPER('%texto%')`. `findBySocioIdAndEstado` recorre la relación `socio.id`.
Con `@Query` lo escribimos a mano en JPQL (entidades y atributos, no tablas).

**¿Cómo se traduce la relación Socio–Inscripcion–Actividad en tablas?** `Inscripcion` es el lado dueño:
tiene `@ManyToOne` + `@JoinColumn(name = "socio_id")` y `actividad_id`, que son las claves foráneas.
Socio y Actividad tienen el lado inverso, `@OneToMany(mappedBy = "socio")`, que no crea columnas.

**El ciclo infinito del JSON.** Socio tiene una lista de inscripciones y cada Inscripcion apunta a su Socio.
Al convertir a JSON, Jackson iría de uno al otro sin fin y la respuesta termina en `StackOverflowError`
(o un JSON cortado). Se resolvió con `@JsonIgnore` en `Inscripcion.socio` y en las dos listas
`inscripciones`. Así el JSON de una inscripción trae la actividad completa y no trae el socio, que es lo que
espera la app. Para el informe: sacar un momento los `@JsonIgnore`, pedir `GET /api/socios/3/inscripciones`,
anotar el error y volver a ponerlos.

**¿Por qué el navegador bloquea sin CORS?** La app (`localhost:3000`) y la API (`localhost:8080`) son orígenes
distintos. El navegador solo entrega la respuesta si el servidor responde con `Access-Control-Allow-Origin`
para ese origen. Antes de un POST con JSON manda un OPTIONS (preflight). Está en `config/CorsConfig.java`.

**¿Qué hace REST a esta API?** Recursos con URL propia (`/api/actividades/6`), verbos HTTP con su significado
(GET consulta, POST crea, PUT modifica, DELETE elimina), sin estado (cada petición trae todo lo que necesita:
no hay sesión) y representación en JSON. SOAP, en cambio, usa siempre POST con un sobre XML y un contrato WSDL.

**`cupoDisponible`** no es una columna: es `@Transient` en Actividad y el servicio lo calcula
(`cupoMaximo` menos las ACTIVA, sin bajar de 0) antes de devolver cualquier actividad.
