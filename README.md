# city-lens-api

City Lens es una WebApp de Reportes Urbanos. Esta es la API de la aplicación.

| Alumno                         | No. Cuenta | Nombre de Usuario |
|:-------------------------------|:----------:|------------------:|
| Edgar José Reyes Montelongo    | 319023275  |   EdgarMontelongo |
 | Ángel Moisés González Corrales | 320234619  |         MoisesAGC |
 | Israel Rivera                  | 320490747  |         Orbitalx1 |
 | Paredes Zamudio Luis Daniel    | 318159926  |        wallsified |

## Dependencias y Herramientas Usadas en el Proyecto
- Se recomienda encarecidamente ocupar IntelliJ para la edición y ejecución de este proyecto. [Descargable Aquí](https://www.jetbrains.com/es-es/idea/download/?section=linux)
- Amazon Corretto 21 (Java / Kotlin) [Descargable Aquí](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
- Maven
- Spring Boot (Framework)
- Swagger (Para la documentación de la API)
- PostgreSQL 17.2

## Notas a la Ejecución
- En la ruta _src/main/sql/setup_ se encuentra un archivo nombrado _schema.sql_. Este
archivo incluye los comandos para la creación de las tablas de la BD. 
  - Para levantar la BD se puede ocupar el archivo _setup.sql_ incluido en la misma ruta que el archivo anterior.
- Posterior a levantar la BD es necesario ejecutar los archivos en  _src/main/sql/utils/_ en este orden
   -    _src/main/sql/utils/functions_
   -   _src/main/sql/utils/procedures_
   -   _src/main/sql/utils/triggers_
- El proyecto contiene configuración de Swagger, por lo que para poder
observar la documentación de la API creada, se ejecuta el proyecto desde
IntelliJ y, una vez haya cargado, uno accede a ella usando el [puerto 8080 
en localhost](http://localhost:8080/swagger-ui/index.html). 

Para unir el frontend, se siguen las instrucciones de [este repositorio](https://github.com/ingenieria-software-7009-2025-2/city-lens-front-end)

## Ejecución del proyecto con Docker Compose
Este proyecto puede ejecutarse fácilmente usando contenedores con Docker y Docker Compose, lo cual levanta automáticamente:

- Una base de datos PostgreSQL inicializada con los scripts SQL proporcionados.

- El backend (API en Kotlin).

- El frontend (React).

### Estructura esperada
Asegúrate de posicionar el archivo docker-compose.yml al mismo nivel que los dos repositorios (skops-api y skops-frontend). Por ejemplo:
```
.
├── city-lens-api/
├── city-lens-front-end/
└── docker-compose.yml
```
Nota: Este archivo debe colocarse fuera de las carpetas de skops-api y skops-frontend.

### Requisitos previos
Antes de comenzar, asegúrate de tener instalado:

- Docker
- Docker Compose (ya viene incluido en Docker Desktop)

Para verificar las versiones:
```
docker --version
docker-compose --version
```

### Pasos para ejecutar el proyecto
1. Clona ambos repositorios (front y back) en el mismo directorio.
2. Coloca el archivo docker-compose.yml junto a ambos repositorios, como se muestra arriba.
3. Desde el mismo nivel donde está docker-compose.yml, ejecuta:
```
docker-compose up --build
```
Esto realizará las siguientes acciones:
  1. Inicializará una base de datos PostgreSQL llamada city_lens.
  2. Ejecutará automáticamente los scripts de inicialización (schema.sql, funciones, procedimientos y triggers) desde city-lens-api/src/main/sql/setup/.
  3. Levantará el backend en http://localhost:8080.
  4. Levantará el frontend en http://localhost:5173.

4. Accede a los servicios en las rutas del paso anterior

### Detener los contenedores
Para detener y limpiar los contenedores:
```
docker-compose down -v
```