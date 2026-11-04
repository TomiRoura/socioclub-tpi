# SocioClub — Club Social y Deportivo Los Algarrobos

Trabajo práctico integrador de la materia **Taller de Construcción de Software** (Universidad Siglo 21).

## Estructura

- `/sitio-publico`: sitio web público del club (HTML, CSS y JavaScript).
- `/backend`: proyecto Spring Boot con el sistema de secretaría (Thymeleaf) y la API REST que usa la app.
- `/backend/postman`: colección de Postman con todos los endpoints y los casos de error.
- `/app-socios`: aplicación para socios.

## Base de datos

Hace falta MySQL 8 (lo probamos con la 8.4) y JDK 17 o más nuevo. Maven no hace falta instalarlo porque el backend trae `mvnw`.

### Crear la base

El script `backend/sql/socioclub.sql` arma todo de cero: la base `socioclub_g13`, las tablas, los datos de prueba y el usuario que usa el backend. Hay que correrlo con root u otro usuario administrador.

- Con MySQL Workbench: *File > Open SQL Script*, elegir el script y ejecutarlo entero con el botón del rayo.
- Desde cmd, parados en la raíz del repo:

  ```
  "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p < backend\sql\socioclub.sql
  ```

- En Linux o Mac: `mysql -u root -p < backend/sql/socioclub.sql`

Ojo con PowerShell: no acepta el `<`, y si lo cargan de otra forma se rompen las tildes ("FÃºtbol").

Para volver a los datos del principio (por ejemplo antes de una demo) se corre el script de nuevo.

### Usuario de la base

El backend entra con el usuario `socioclub_g13` y la contraseña `SocioClub_2026`, que los crea el mismo script. Si alguien prefiere usar su root, puede definir las variables de entorno `DB_USER` y `DB_PASSWORD` (en IntelliJ van en la Run Configuration).

### Levantar el backend

Desde la carpeta `backend`:

```
mvnw spring-boot:run
```

Ese es para cmd. En PowerShell va `.\mvnw spring-boot:run` y en Git Bash, Linux o Mac `./mvnw spring-boot:run`. Queda andando en `http://localhost:8080`.

Los tests (`mvnw test`) necesitan MySQL prendido. Prueban las reglas RN1 a RN5 contra los datos del script y no dejan cambios en la base.

### Sistema de secretaría

Con el backend andando, entrar a `http://localhost:8080`. Tiene:

- **Actividades**: listado con cupo disponible y filtro por disciplina, alta, edición, eliminación (solo si no tiene inscriptos activos) y listado de inscriptos de cada una, con baja.
- **Socios**: listado paginado de a 10 con búsqueda por apellido, alta y edición. Al editar se ven sus actividades activas.
- **Inscribir**: se elige socio y actividad; si una regla lo rechaza aparece el mensaje con la regla, por ejemplo "La actividad no tiene cupo disponible (RN1)".

Las reglas RN1 a RN5 están en `InscripcionService`, y las usan igual las pantallas y la API. El máximo de actividades (RN5) es la constante `MAX_ACTIVIDADES` de esa clase.

### API REST

Todo bajo `http://localhost:8080/api`:

| Verbo | Ruta | Respuestas |
|---|---|---|
| GET | `/actividades` (acepta `?disciplina=Fútbol`) | 200 |
| GET | `/actividades/{id}` | 200 / 404 |
| POST | `/actividades` | 201 / 400 |
| PUT | `/actividades/{id}` | 200 / 404 |
| DELETE | `/actividades/{id}` | 204 / 404 / 409 si tiene inscriptos activos |
| GET | `/socios/dni/{dni}` | 200 / 404 |
| GET | `/socios/{id}/inscripciones` | 200 / 404 |
| POST | `/inscripciones` con `{ "socioId": 1, "actividadId": 6 }` | 201 / 404 / 409 |
| PUT | `/inscripciones/{id}/baja` | 200 / 404 |

Cuando una regla rechaza la inscripción devuelve 409 con `{ "error": "La actividad no tiene cupo disponible", "regla": "RN1" }`. CORS está abierto para la app en `http://localhost` con cualquier puerto (si el 3000 está ocupado, React arranca en el 3001).

### Postman

Importar `backend/postman/SocioClub.postman_collection.json`. Antes de correrla hay que volver a cargar el script SQL, porque la colección inscribe y da de baja. Se puede correr entera con el *Runner* en el orden en que está: cada pedido tiene sus tests (código de estado y regla esperada).

## App de socios (React)

Hace falta Node 18 o más nuevo. Desde la carpeta `app-socios`:

```
npm install
npm start
```

Abre `http://localhost:3000`. Por defecto usa los datos locales que trae el código. Para que use el backend hay que crear el archivo `app-socios/.env` con:

```
REACT_APP_USAR_DATOS_LOCALES=false
```

y volver a correr `npm start` (con el backend levantado). Los tests de Jest y React Testing Library se corren con `npm test`.

## Sitio público

Son archivos estáticos: alcanza con abrir `sitio-publico/index.html` en el navegador (o con Live Server de VS Code).

### DNI para probar

| DNI | Socio | Para qué sirve |
|---|---|---|
| 40123456 | Lucía Fernández | Se puede anotar en Ajedrez y se lleva el último lugar. También sirve para reinscribirse en Vóley después de una baja |
| 52345678 | Mateo Ríos | RN2: ya está en Fútbol. RN3 en Vóley (tiene 12 y el mínimo es 13). En Básquet entra justo con 12. Dejarlo para el final porque queda con tres actividades |
| 9876543 | Marta Gómez | Tiene tres actividades, le toca el 15 % de descuento (RN6). Además el DNI es de 7 dígitos |
| 30111222 | Jorge Paz | RN4: debe la cuota. Igual se puede dar de baja de Funcional |
| 24567890 | Ricardo Sosa | RN4: debe la cuota y no tiene actividades |
| 36123987 | Sergio Molina | RN5: ya tiene tres actividades (probar con Natación) |
| 50876543 | Tomás Álvarez | RN3: se pasa de la edad de Fútbol infantil |
| 57654321 | Emma Quiroga | RN3: no llega a la edad de Vóley |
| 51678234 | Valentina Ruiz | RN3: tiene 13, uno más que el máximo de Fútbol infantil |
| 44123789 | Camila Herrera | RN1: Yoga está lleno |
| 20345678 | Gabriel Ponce | Si se da de baja de Yoga queda un lugar (hacerlo después de probar con Camila) |
| 12345678 | (no existe) | Socio que no existe |

Recién cargado el script, a Ajedrez le queda un lugar y Yoga está lleno.

## Integrantes
```
Loyola Vazquez Martin - 43043048
Tillard Franco Tomas - 42783715
```
