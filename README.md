# SocioClub — Club Social y Deportivo Los Algarrobos

Trabajo práctico integrador de la materia **Taller de Construcción de Software** (Universidad Siglo 21).

## Estructura

- `/sitio-publico`: sitio web público del club (HTML, CSS y JavaScript).
- `/backend`: servidor / API.
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

Los tests (`mvnw test`) necesitan MySQL prendido.

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

