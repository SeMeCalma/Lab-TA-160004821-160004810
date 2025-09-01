# Laboratorio-TA-02
160004821-160004810
- `UI JavaFX para Universidad (JDK 23)`
- `Ruta del MainApp.java src/main/java/edu/universidad/ui/MainApp.java`
## Requisitos

- JDK **23** instalado.
- Maven 3.9+.

- ## Ejecutar por consola (main de consola)
```bash
mvn -DskipTests exec:java -Dexec.mainClass=edu.universidad.App
```

## Ejecutar interfaz JavaFX (main de UI)
```bash
mvn -DskipTests exec:java -Dexec.mainClass=edu.universidad.ui.MainApp
```

> No se necesita `--module-path` ni `--add-modules` al usar Maven; las dependencias JavaFX se resuelven por clase.

## IDEs

- **IntelliJ IDEA**:
    - Añade dos configuraciones *Run/Debug* (Application):
        - `edu.universidad.App`
        - `edu.universidad.ui.MainApp`
    - VM Options: *(vacío)* — usando Maven/Classpath no es necesario.
- **VS Code**:
    - Usa el plugin *Extension Pack for Java*.
    - Ejecuta desde el panel de Maven con el goal anterior o desde el icono de run en `MainApp`.
