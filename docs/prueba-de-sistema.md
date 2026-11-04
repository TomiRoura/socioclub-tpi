# Prueba de sistema (Parte E)

Casos de punta a punta, con la base recién cargada con `backend/sql/socioclub.sql`, el backend andando y la app React conectada a la API (`REACT_APP_USAR_DATOS_LOCALES=false`). Hacerlos en este orden.

La columna "Resultado obtenido" tiene lo que dio la corrida del 03/11/2026. Antes de entregar, repetirlos y sacar capturas para el informe.

| N.º | Caso | Pasos | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|
| 1 | Inscripción exitosa (app) | En la app, ingresar con DNI 40123456 (Lucía). Ir a Ajedrez > Ver detalle > Inscribirme. Ir a Mis actividades. | Mensaje "Listo, quedaste inscripto en Ajedrez". Ajedrez pasa a "Sin cupo". En Mis actividades aparece Ajedrez y la cuota estimada. | OK. POST /api/inscripciones respondió 201 y Ajedrez quedó con cupo 0. |
| 2 | RN1: actividad sin cupo (secretaría) | En `localhost:8080`, Inscribir: socio Herrera, Camila; actividad Yoga. Inscribir. | No se guarda. Mensaje "La actividad no tiene cupo disponible (RN1)". | OK, se mostró el mensaje con (RN1). |
| 3 | RN2: inscripción duplicada (app) | Ingresar con DNI 52345678 (Mateo). En el catálogo, Inscribirme en Fútbol infantil. | Mensaje "El socio ya está inscripto en esta actividad (RN2)". | OK. 409 con regla RN2. |
| 4 | RN3: edad fuera de rango (app) | Con Mateo (12 años), Inscribirme en Vóley mixto (13 a 60). | Mensaje "La edad permitida para esta actividad es de 13 a 60 años (RN3)". | OK. 409 con regla RN3. |
| 5 | RN4: socio moroso (secretaría) | Inscribir: socio Paz, Jorge (MOROSO); actividad Natación. | Mensaje "Debe regularizar su cuota en secretaría (RN4)". | OK, se mostró el mensaje con (RN4). |
| 6 | RN5: máximo de 3 actividades (app) | Ingresar con DNI 36123987 (Sergio). Inscribirme en Natación. | Mensaje "Alcanzó el máximo de 3 actividades (RN5)". | OK. 409 con regla RN5. |
| 7 | Baja lógica y cupo liberado (secretaría) | Actividades > Yoga > Inscriptos > Dar de baja a Ponce, Gabriel. Después inscribir a Herrera, Camila en Yoga. | Gabriel deja de figurar (la inscripción queda en BAJA, no se borra). Yoga muestra 1 lugar y Camila entra. | OK (también lo cubre el test `laBajaLiberaElCupo`). |

Las mismas reglas también se prueban de forma automática:

- `backend/src/test/java/com/socioclub/service/InscripcionServiceTest.java` (JUnit, `mvnw test`): 10 pruebas sobre RN1 a RN5, baja y eliminación.
- `backend/postman/SocioClub.postman_collection.json`: 26 pedidos con 39 controles. Corrida completa sin fallas.
- `app-socios` (`npm test`): 26 pruebas de Jest y React Testing Library.
