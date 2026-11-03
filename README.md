# SocioClub — Club Social y Deportivo Los Algarrobos

Trabajo práctico integrador de la materia **Taller de Construcción de Software** (Universidad Siglo 21).

## Estructura

- `/sitio-publico`: sitio web público del club (HTML, CSS y JavaScript).
- `/backend`: Spring Boot con la secretaría (Thymeleaf), la API REST, la base MySQL y la colección de Postman.
- `/app-socios`: app "Mi Club" para socios (React).

## Integrantes

-

## Requisitos

- MySQL 8 (probado con 8.4).
- JDK 17 o más nuevo (`java -version`).
- Node.js 18 o más nuevo para la app de socios.
- Maven no hace falta: el backend trae `mvnw`, que lo descarga la primera vez.

## 1. Crear la base de datos

El script `backend/sql/socioclub.sql` crea la base `socioclub_g13`, las tablas, los datos de prueba y el usuario
de la app (`socioclub_g13` / `SocioClub_2026`). Hay que correrlo con **root**:

- **Workbench**: File > Open SQL Script, elegir `backend/sql/socioclub.sql` y ejecutar con el rayo.
- **cmd** (no PowerShell: rompe las tildes), parados en la raíz del repo:

  ```
  "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p < backend\sql\socioclub.sql
  ```

Volver a correrlo borra la base y la carga de nuevo. **Conviene hacerlo antes de cada demo.**

## 2. Levantar el backend

Parados en la carpeta `backend`:

| Consola     | Comando                     |
|-------------|-----------------------------|
| cmd         | `mvnw spring-boot:run`      |
| PowerShell  | `.\mvnw spring-boot:run`    |
| Git Bash    | `./mvnw spring-boot:run`    |

La primera vez descarga Maven y las dependencias (tarda unos minutos). Queda en **http://localhost:8080**:

- Secretaría (Thymeleaf): http://localhost:8080
- API REST: http://localhost:8080/api/actividades

También se puede abrir la carpeta `backend` en IntelliJ, VS Code (Extension Pack for Java) o Eclipse/STS
como proyecto Maven y correr `SocioclubApplication`.

Para usar otro usuario de MySQL, definir las variables de entorno `DB_USER` y `DB_PASSWORD`.

## 3. Levantar la app de socios

Parados en `app-socios`:

```
npm install
npm start
```

Por defecto la app usa datos locales. Para conectarla con el backend, crear `app-socios/.env` con:

```
REACT_APP_USAR_DATOS_LOCALES=false
```

y volver a correr `npm start`. Se abre en http://localhost:3000.

## 4. Probar la API con Postman

Importar `backend/postman/SocioClub.postman_collection.json`. Trae los endpoints del Anexo A con sus casos de
éxito y de error (RN1 a RN5, 404, 409) y un test en cada uno. Recargar la base antes de correr la colección entera.

## DNI de prueba

| DNI      | Socio           | Qué prueba |
|----------|-----------------|------------|
| 40123456 | Lucía Fernández | Inscripción exitosa en Ajedrez (toma el último lugar). Reinscripción en Vóley después de una baja. |
| 52345678 | Mateo Ríos      | RN2 en Fútbol. RN3 en Vóley (tiene 12). Éxito en Básquet con la edad justa (hacerlo al final). |
| 9876543  | Marta Gómez     | Tres actividades: 15 % de descuento (RN6). DNI de 7 dígitos. |
| 30111222 | Jorge Paz       | RN4: moroso. Igual puede darse de baja de Funcional. |
| 24567890 | Ricardo Sosa    | RN4: moroso, sin actividades. |
| 36123987 | Sergio Molina   | RN5: ya tiene tres actividades (probar con Natación). |
| 50876543 | Tomás Álvarez   | RN3: supera la edad máxima de Fútbol infantil. |
| 57654321 | Emma Quiroga    | RN3: no llega a la edad mínima de Vóley. |
| 51678234 | Valentina Ruiz  | RN3: tiene 13, uno más que el máximo de Fútbol infantil. |
| 44123789 | Camila Herrera  | RN1: Yoga no tiene cupo. |
| 20345678 | Gabriel Ponce   | Baja de Yoga: después queda 1 lugar (hacerla después del caso de Camila). |
| 12345678 | (no existe)     | Socio inexistente (404). |

Las fechas de nacimiento mantienen estos casos hasta marzo de 2027.
