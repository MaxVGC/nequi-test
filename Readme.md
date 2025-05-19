# Nequi Test Application

Este proyecto es una API REST desarrollada con **Spring Boot** para la gestión de productos, sucursales y franquicias. Incluye validaciones robustas para los datos de entrada utilizando anotaciones estándar de Jakarta Validation y mensajes personalizados.

## Características

- Gestión de productos, sucursales y franquicias.
- Validaciones personalizadas para los DTOs.
- Arquitectura basada en DTOs y buenas prácticas de Spring Boot.
- Configuración de mensajes de validación internacionalizables (i18n).

## Requisitos

- Java 17 o superior
- Maven 3.8+
- (Opcional) Docker para despliegue

## Ejecución local

1. Clona el repositorio:
    ```sh
    git clone https://github.com/tu-usuario/nequi-test.git
    cd nequi-test
    ```
2. Añade las variables de entorno para la conexion con la base de datos (unicamente PostgreSQL):
    ```sh
    DB_URL= example:jdbc:postgresql://localhost:5432/nequi_test
    DB_USERNAME= tu_usuario
    DB_PASSWORD= tu_contraseña
    ```
3. Compila y ejecuta la aplicación:
    ```sh
    mvn spring-boot:run
    ```
4. La API estará disponible en:  
   [http://localhost:8080](http://localhost:8080)


