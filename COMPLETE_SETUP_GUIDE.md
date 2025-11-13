# Wellness Tracker - Complete Setup Guide

## Overview

This guide will help you set up and build the Wellness Tracker project. We provide three methods:
1. **Maven (Recommended)** - Easiest, handles all dependencies
2. **Direct JavaFX SDK** - More control, no build tool needed
3. **IDE Setup** - Best for development (IntelliJ IDEA, VS Code, Eclipse)

---

## Method 1: Maven Setup (Recommended) ⭐

### Prerequisites
- Java Development Kit (JDK) 21 or higher

### Installation Steps

#### Step 1: Download and Install Maven

1. Visit: https://maven.apache.org/download.cgi
2. Download "Binary zip archive" (e.g., `apache-maven-3.9.5-bin.zip`)
3. Extract to a permanent location:
   ```
   C:\Maven\apache-maven-3.9.5
   ```

#### Step 2: Add Maven to System PATH

**Windows:**
1. Right-click on "This PC" or "My Computer" → Properties
2. Click "Advanced system settings" → "Environment Variables"
3. Under "System variables", click "New"
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Maven\apache-maven-3.9.5`
4. Find "Path" in System variables → Click "Edit"
5. Click "New" and add: `%MAVEN_HOME%\bin`
6. Click OK on all dialogs
7. **Restart PowerShell** for changes to take effect

**Verify Installation:**
```powershell
mvn --version
```

You should see:
```
Apache Maven 3.9.5
...
Java version: 21.0.8
```

#### Step 3: Build and Run

```powershell
# Navigate to project
cd C:\Users\mmeri\wellness-tracker-1

# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn javafx:run

# Package as JAR (executable)
mvn clean package
java -jar target/wellness-tracker-1.0.0.jar
```

---

## Method 2: Direct JavaFX SDK Setup

### Prerequisites
- Java Development Kit (JDK) 21 or higher
- JavaFX SDK 21

### Installation Steps

#### Step 1: Download JavaFX SDK

1. Visit: https://gluonhq.com/products/javafx/
2. Download **JavaFX SDK 21.0.1** (matches your JDK)
3. Extract to: `C:\Program Files\javafx-sdk-21.0.1`

#### Step 2: Verify Installation

```powershell
# Verify the lib directory exists
Test-Path "C:\Program Files\javafx-sdk-21.0.1\lib"
# Should return: True
```

#### Step 3: Compile Using PowerShell

```powershell
# Define paths
$JAVAFX_HOME = "C:\Program Files\javafx-sdk-21.0.1"
$PROJECT_ROOT = "C:\Users\mmeri\wellness-tracker-1"
$SRC_DIR = "$PROJECT_ROOT\src\main"
$BIN_DIR = "$PROJECT_ROOT\bin"

# Create bin directory if it doesn't exist
if (-not (Test-Path $BIN_DIR)) {
    New-Item -ItemType Directory -Path $BIN_DIR -Force | Out-Null
}

# Get all Java source files
$javaFiles = Get-ChildItem -Path $SRC_DIR -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

# Compile
javac -d $BIN_DIR `
  --module-path "$JAVAFX_HOME\lib" `
  --add-modules javafx.controls,javafx.fxml,javafx.graphics `
  -encoding UTF-8 `
  -source 21 `
  -target 21 `
  $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilation successful!"
} else {
    Write-Host "✗ Compilation failed!"
    exit 1
}

# Copy FXML and CSS resources
Copy-Item -Path "$SRC_DIR\resources\*.fxml" -Destination $BIN_DIR -Force
Copy-Item -Path "$SRC_DIR\resources\*.css" -Destination $BIN_DIR -Force

# Run the application
java --module-path "$JAVAFX_HOME\lib" `
     --add-modules javafx.controls,javafx.fxml,javafx.graphics `
     -cp $BIN_DIR `
     javafx.Main
```

#### Step 4: Using the Provided Build Script (Easier)

```powershell
# Navigate to project
cd C:\Users\mmeri\wellness-tracker-1

# Make script executable
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process

# Run the build script
.\build.ps1

# If successful, run the application
$JAVAFX_HOME = "C:\Program Files\javafx-sdk-21.0.1"
java --module-path "$JAVAFX_HOME\lib" `
     --add-modules javafx.controls,javafx.fxml,javafx.graphics `
     -cp bin `
     javafx.Main
```

---

## Method 3: IDE Setup (Best for Development)

### IntelliJ IDEA Setup

1. **Install IntelliJ IDEA**
   - Download from: https://www.jetbrains.com/idea/
   - Install Community or Professional Edition

2. **Open Project**
   - Launch IntelliJ IDEA
   - Click "Open"
   - Navigate to `C:\Users\mmeri\wellness-tracker-1`
   - Click "OK"

3. **Configure JDK**
   - Go to: File → Project Structure → Project
   - Set "SDK" to JDK 21
   - Click "Apply" and "OK"

4. **Add Maven Project**
   - Right-click on `pom.xml` in the project tree
   - Select "Add as Maven Project"
   - IntelliJ will automatically download all dependencies
   - Wait for indexing to complete (check bottom status bar)

5. **Run the Application**
   - Click Run → Run... (or press Shift+F10)
   - Select "Main" class
   - IntelliJ will compile and run

6. **Run Tests**
   - Right-click on `src/tests` folder
   - Select "Run All Tests"

### VS Code Setup

1. **Install Extensions**
   - Install "Extension Pack for Java" by Microsoft
   - Install "JavaFX Support" extension

2. **Configure JDK**
   - Open Command Palette (Ctrl+Shift+P)
   - Type "Java: Configure Runtime"
   - Set JDK 21 as default

3. **Maven Integration**
   - Install Maven (see Method 1)
   - VS Code will auto-detect and show Maven tasks

4. **Build and Run**
   - Open Command Palette → "Run Maven command"
   - Select: `javafx:run`

---

## Troubleshooting

### Problem: "module not found: javafx.controls"

**Cause:** JavaFX SDK path is incorrect or not found

**Solutions:**
- Verify JavaFX is installed: `Test-Path "C:\Program Files\javafx-sdk-21.0.1\lib"`
- Check the module-path in your command matches the installation location
- Try downloading again from: https://gluonhq.com/products/javafx/

### Problem: "javac: Invalid filename"

**Cause:** PowerShell quoting issues with multiple files

**Solution:**
```powershell
# Use the build script instead:
.\build.ps1
```

### Problem: "Cannot find symbol" for FXML files at runtime

**Cause:** FXML resources not copied to bin directory

**Solution:**
```powershell
Copy-Item -Path "src/main/resources/*.fxml" -Destination "bin" -Force
Copy-Item -Path "src/main/resources/*.css" -Destination "bin" -Force
```

### Problem: Maven command not found

**Cause:** Maven not installed or PATH not updated

**Solution:**
1. Verify Maven is installed: `Test-Path "C:\Maven\apache-maven-3.9.5\bin\mvn.cmd"`
2. Restart PowerShell after adding to PATH
3. Use Method 2 (Direct JavaFX SDK) or Method 3 (IDE) instead

### Problem: JDK version mismatch

**Cause:** JDK version doesn't match project requirements

**Solution:**
```powershell
# Check Java version
java -version
javac -version

# Should show: version "21" or similar
```

If not, download JDK 21 from: https://www.oracle.com/java/technologies/downloads/

### Problem: "src/main/resources/*.fxml not found"

**Cause:** Source files structure is different

**Solution:**
```powershell
# Check actual structure
Get-ChildItem -Path "src/main" -Recurse -Filter "*.fxml"

# Adjust paths if needed
```

---

## Quick Reference Commands

### Maven Commands
```powershell
mvn clean              # Remove build artifacts
mvn compile            # Compile source code
mvn test               # Run tests
mvn javafx:run         # Run the application
mvn package            # Create JAR file
mvn clean package      # Clean build
mvn -X clean compile   # Compile with debug output
```

### Git Commands
```powershell
git status             # Check status
git add .              # Stage all changes
git commit -m "message" # Commit changes
git push origin main   # Push to GitHub
git log --oneline      # View commit history
```

### Java Compilation (Manual)
```powershell
# List all Java files
Get-ChildItem -Path "src/main" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

# Compile with full control
javac -d bin -encoding UTF-8 -source 21 -target 21 @sources.txt
```

---

## Project Structure

```
wellness-tracker-1/
├── src/
│   ├── main/
│   │   ├── controller/          # MVC Controllers (Habits, Mood, User)
│   │   │   ├── HabitController.java
│   │   │   ├── MoodController.java
│   │   │   └── UserController.java
│   │   ├── database/            # Database management
│   │   │   └── DatabaseManager.java
│   │   ├── javafx/              # GUI Views and Main entry point
│   │   │   ├── Main.java        # Application entry point
│   │   │   ├── DashboardView.java
│   │   │   ├── HabitView.java
│   │   │   ├── MoodTrackerView.java
│   │   │   ├── AnalyticsView.java
│   │   │   ├── LoginView.java
│   │   │   └── RegisterView.java
│   │   ├── model/               # Data Models
│   │   │   ├── User.java
│   │   │   ├── Habit.java
│   │   │   ├── MoodEntry.java
│   │   │   └── Challenge.java
│   │   ├── utils/               # Utility Classes
│   │   │   └── PasswordUtil.java
│   │   └── resources/           # FXML and CSS
│   │       ├── login.fxml
│   │       ├── register.fxml
│   │       ├── main.fxml
│   │       ├── habits.fxml
│   │       ├── mood_tracker.fxml
│   │       ├── analytics.fxml
│   │       └── style.css
│   └── tests/                   # Test files
│       ├── UserTest.java
│       ├── HabitTest.java
│       ├── MoodControllerTest.java
│       └── IntegrationTests.java
├── bin/                         # Compiled classes (generated)
├── target/                      # Maven build output (generated)
├── pom.xml                      # Maven configuration
├── build.ps1                    # PowerShell build script
├── .gitignore                   # Git ignore rules
├── README.md                    # Project documentation
└── SETUP_AND_BUILD.md          # Build instructions
```

---

## Next Steps

After successful setup:

1. **Explore the codebase** - Start in `Main.java`
2. **Run tests** - Execute: `mvn test`
3. **Start the app** - Execute: `mvn javafx:run`
4. **Make changes** - Edit source files and commit to git
5. **Push to GitHub** - `git push origin main`

For development, we recommend using **IntelliJ IDEA** with the pom.xml for the best experience.

---

## Additional Resources

- [JavaFX Documentation](https://openjfx.io/)
- [Maven Guide](https://maven.apache.org/guides/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [SQLite JDBC](https://github.com/xerial/sqlite-jdbc)
- [GitHub Guides](https://guides.github.com/)

