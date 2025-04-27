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
- En la ruta _src/main/sql_ se encuentra un archivo nombrado _schema.sql_. Este
archivo incluye los comandos para la creación de las tablas de la BD. 
  - Para levantar la BD se puede ocupar el archivo _setup.sql_ incluido en la misma ruta que el archivo anterior. 
- El proyecto contiene configuración de Swagger, por lo que para poder
observar la documentación de la API creada, se ejecuta el proyecto desde
IntelliJ y, una vez haya cargado, uno accede a ella usando el [puerto 8080 
en localhost](http://localhost:8080/swagger-ui/index.html). 

Para unir el frontend, se siguen las instrucciones de [este repositorio](https://github.com/ingenieria-software-7009-2025-2/city-lens-front-end)
