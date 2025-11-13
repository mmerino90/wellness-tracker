# Quick Start Guide - Wellness Tracker

## Environment Setup

### Option 1: Using Maven (Recommended)

**Step 1: Install Maven**
- Download from: https://maven.apache.org/download.cgi
- Extract to a location (e.g., `C:\Maven\apache-maven-3.9.x`)
- Add to PATH: `C:\Maven\apache-maven-3.9.x\bin`
- Verify: Open PowerShell and run `mvn --version`

**Step 2: Build and Run**
```powershell
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn javafx:run

# Create executable JAR
mvn clean package
java -jar target/wellness-tracker-1.0.0.jar
```

### Option 2: Using JavaFX SDK Directly

**Step 1: Download JavaFX SDK**
- Visit: https://gluonhq.com/products/javafx/
- Download JavaFX SDK 21 (matching your JDK version: 21.0.8)
- Extract to `C:\Program Files\javafx-sdk-21.0.1` (or similar location)

**Step 2: Compile with PowerShell**
```powershell
# Set JavaFX path
$JAVAFX_HOME = "C:\Program Files\javafx-sdk-21.0.1"

# Get all Java source files
$javaFiles = Get-ChildItem -Path "src/main" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

# Compile
javac -d bin `
  --module-path "$JAVAFX_HOME\lib" `
  --add-modules javafx.controls,javafx.fxml,javafx.graphics `
  -encoding UTF-8 `
  -source 21 `
  -target 21 `
  $javaFiles

# Run (copy FXML files first)
Copy-Item -Path "src/main/resources/*.fxml" -Destination "bin" -Force
Copy-Item -Path "src/main/resources/*.css" -Destination "bin" -Force

java --module-path "$JAVAFX_HOME\lib" `
     --add-modules javafx.controls,javafx.fxml,javafx.graphics `
     -cp bin `
     javafx.Main
```

**Step 3: Using the Build Script (Easy)**
```powershell
# Make the script executable
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process

# Run the build script
.\build.ps1

# Then run the compiled application
java --module-path "C:\Program Files\javafx-sdk-21.0.1\lib" `
     --add-modules javafx.controls,javafx.fxml,javafx.graphics `
     -cp bin `
     javafx.Main
```

### Option 3: Using IDE (IntelliJ IDEA - Easiest)

1. Open the project folder in IntelliJ IDEA
2. Right-click on `pom.xml` → "Add as Maven Project"
3. IntelliJ will automatically download dependencies
4. Click "Run" or press Shift+F10

## Troubleshooting

### Issue: "module not found: javafx.controls"
**Solution:** JavaFX SDK is not in the module path. Verify the path to JavaFX SDK is correct.

### Issue: "Cannot find symbol" for FXML files
**Solution:** FXML files must be in the classpath. Ensure `src/main/resources/*.fxml` files are copied to the `bin` directory.

### Issue: Maven command not found
**Solution:** Maven is not installed or not in PATH. Use Option 2 (Direct JavaFX SDK) or Option 3 (IDE) instead.

### Issue: JDK version mismatch
**Solution:** Ensure your JDK matches version 21. Run `javac -version` to verify.

## Recommended Development Workflow

1. Use **IntelliJ IDEA** for the best experience (auto-completion, debugging, Maven integration)
2. Commit changes: `git add . && git commit -m "your message"`
3. Push to GitHub: `git push origin main`
4. Run tests before committing: `mvn test` or use IDE's test runner

## Project Structure

```
wellness-tracker-1/
├── src/
│   ├── main/
│   │   ├── controller/      # Business logic controllers
│   │   ├── database/        # Database management
│   │   ├── javafx/          # GUI components and Main
│   │   ├── model/           # Data models
│   │   ├── utils/           # Utility classes
│   │   └── resources/       # FXML and CSS files
│   └── tests/               # Test files
├── bin/                     # Compiled output
├── lib/                     # Dependencies (if not using Maven)
├── pom.xml                  # Maven configuration
└── build.ps1               # PowerShell build script
```

## Useful Commands

### Compile Only
```bash
mvn compile
```

### Run Tests
```bash
mvn test
```

### Run Application
```bash
mvn javafx:run
```

### Create Fat JAR
```bash
mvn clean assembly:single
```

### Clean Build
```bash
mvn clean
```

### View Project Info
```bash
mvn project-info-reports:dependencies
```
