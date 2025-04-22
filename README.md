# 🚀 Spring Boot Prueba Técnica

Este proyecto es una API RESTful construida con Spring Boot. Permite la gestión de usuarios, incluyendo creación, obtención, actualización total y parcial, y eliminación. La autenticación se realiza mediante **JWT**.

---

## 🔧 Tecnologías utilizadas

- Java 21
- Spring Boot 3.x
- Spring Security con JWT
- JPA + Hibernate
- Maven
- **H2 Database (en memoria)**
- ModelMapper
- Jakarta Validation

---

## ▶️ Cómo ejecutar el proyecto

1. Clona el repositorio:
   ```bash
   git clone https://github.com/sebaleivasilva/spring-boot-prueba-tecnica.git
   cd spring-boot-prueba-tecnica

    Ejecuta la aplicación:

    ./mvnw spring-boot:run

La API estará disponible en: http://localhost:8080
🗃️ Base de datos (H2 en memoria)

Este proyecto utiliza una base de datos H2 en memoria, por lo que no necesitas instalar nada adicional.
⚙️ Configuración

Las credenciales están en el archivo src/main/resources/application.properties:

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=prueba
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

🔍 Acceder a la consola web de H2

Puedes conectarte a la base de datos desde el navegador con:

👉 http://localhost:8080/h2-console
Parámetros para ingresar:

    JDBC URL: jdbc:h2:mem:testdb

    User Name: prueba

    Password: (dejar vacío si no se modificó)

Haz clic en “Connect” para ver las tablas y datos en memoria.

🔐 Autenticación con JWT

Todos los endpoints (excepto crear usuario) requieren enviar el token JWT en el header Authorization:

Authorization: Bearer <tu_token_jwt>

📫 Endpoints disponibles

📍 POST /usuario/crearUsuario

Descripción: Crear un nuevo usuario. No requiere autenticación.

Request body:

{
  "nombre": "Juan Rodriguez",
  "correo": "juan@dominio.cl",
  "contraseña": "hunter28",
  "telefonos": [
    {
      "numero": "1234567",
      "codigoCiudad": "1",
      "codigoPais": "57"
    }
  ]
}

🔑 Importante: El campo "id" devuelto en la respuesta es el identificador único del usuario.
Este id se debe utilizar en los endpoints que requieren una ruta con /{id} como:

    GET /usuario/obtenerUsuario/{id}

    PUT /usuario/actualizarUsuario/{id}

    PATCH /usuario/actualizarUsuarioParcial/{id}

    DELETE /usuario/eliminarUsuario/{id}

📍 GET /usuario/obtenerUsuario/{id}

Descripción: Obtener un usuario por ID.

Headers:

Authorization: Bearer <tu_token>

📍 PUT /usuario/actualizarUsuario/{id}

Descripción: Actualiza completamente al usuario.

Headers:

Authorization: Bearer <tu_token>

Request body:

{
  "nombre": "Jose Lopez",
  "correo": "juan@dominio.cl",
  "contraseña": "hunter28",
  "telefonos": [
    {
      "numero": "1234567",
      "codigoCiudad": "1",
      "codigoPais": "57"
    }
  ]
}

📍 PATCH /usuario/actualizarUsuarioParcial/{id}

Descripción: Actualiza parcialmente los datos del usuario (uno o varios campos).

Headers:

Authorization: Bearer <tu_token>

Ejemplo de request para cambiar solo el nombre:

{
  "nombre": "Diego Lopez"
}

📍 DELETE /usuario/eliminarUsuario/{id}

Descripción: Elimina un usuario por ID.

Headers:

Authorization: Bearer <tu_token>

📘 Swagger (próximamente)

Una vez habilitado, podrás acceder a la documentación desde:

http://localhost:8080/swagger-ui/index.html

🧪 Probar con Postman

Puedes importar la colección Postman incluida en este repositorio para probar fácilmente todos los endpoints.
