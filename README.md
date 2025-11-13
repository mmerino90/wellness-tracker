# Wellness Tracker

A comprehensive JavaFX-based desktop application for tracking daily wellness activities including habits, mood entries, and personal challenges. Built with Object-Oriented Programming principles, secure authentication, and SQLite database persistence.

## 🌟 Features

### User Management
- ✅ Secure user registration with input validation
- ✅ Password hashing with SHA-256 and salt
- ✅ User authentication and session management
- ✅ Profile management and password changes
- ✅ Account deletion with cascading data cleanup

### Habit Tracking
- ✅ Create, edit, and delete habits
- ✅ Automatic streak counting
- ✅ Daily completion tracking
- ✅ Completion rate calculations over custom periods
- ✅ Habit categories and frequency tracking
- ✅ Soft delete support for inactive habits

### Mood Tracking
- ✅ Log daily mood entries with context
- ✅ Track emotional states and energy levels
- ✅ Add activity notes with moods
- ✅ View historical mood data
- ✅ Calculate average mood over periods
- ✅ Find most common mood patterns

### Analytics & Insights
- ✅ Habit completion statistics
- ✅ Mood trend analysis
- ✅ Date-range queries for historical analysis
- ✅ Statistical calculations (average, frequency, etc.)

### Technical Features
- ✅ SQLite database with automatic schema initialization
- ✅ Secure password management with salt and hashing
- ✅ Prepared statements to prevent SQL injection
- ✅ Comprehensive error handling and logging
- ✅ 100% Javadoc documentation
- ✅ Unit test framework in place

## 📋 Prerequisites

- **Java Development Kit (JDK):** 11 or higher
- **JavaFX SDK:** 11 or higher
- **Build Tool:** Maven or Gradle (optional)
- **IDE:** IntelliJ IDEA, Eclipse, or VS Code (recommended)

## 🚀 Quick Start

### Setup Instructions

⭐ **For detailed setup instructions, see:**
- 📖 [Complete Setup Guide](COMPLETE_SETUP_GUIDE.md) - 3 methods (Maven, JavaFX SDK, IDE)
- ⚡ [Quick Start Guide](QUICK_START.md) - Quick reference commands
- 🔧 [Build Configuration](BUILD_CONFIGURATION.md) - Configuration summary

### Quick Command Reference

**Using Maven (Recommended):**
```bash
mvn javafx:run              # Run the application
mvn test                    # Run all tests
mvn clean package           # Create executable JAR
```

**Using PowerShell Build Script:**
```powershell
.\build.ps1                 # Compile with JavaFX SDK
```

### First Time Usage
1. Choose your build method (see guides above)
2. Follow the setup instructions for your method
3. Run the application with appropriate command
4. Register a new account
5. Login with your credentials
6. Start tracking your wellness journey!

## 📁 Project Structure

```
wellness-tracker-1/
├── src/
│   ├── main/
│   │   ├── javafx/              # GUI Controllers
│   │   │   ├── Main.java
│   │   │   ├── LoginView.java
│   │   │   ├── DashboardView.java
│   │   │   └── HabitView.java
│   │   ├── model/               # Data Models
│   │   │   ├── User.java
│   │   │   ├── Habit.java
│   │   │   ├── MoodEntry.java
│   │   │   └── Challenge.java
│   │   ├── controller/          # Business Logic
│   │   │   ├── UserController.java
│   │   │   ├── HabitController.java
│   │   │   └── MoodController.java
│   │   ├── database/            # Database Management
│   │   │   ├── DatabaseManager.java
│   │   │   └── wellness_tracker.db
│   │   ├── utils/               # Utility Classes
│   │   │   └── PasswordUtil.java
│   │   └── resources/           # FXML & CSS
│   │       ├── login.fxml
│   │       ├── main.fxml
│   │       └── style.css
│   └── tests/                   # Unit Tests
│       ├── UserTest.java
│       └── HabitTest.java
├── scripts/                     # Database Scripts
│   └── init_db.sql
├── docs/                        # Documentation
│   ├── architecture.pdf
│   └── README.md
├── build/                       # Compiled Output
│   └── wellness-tracker.jar
├── API_REFERENCE.md             # Complete API Documentation
├── IMPLEMENTATION_PROGRESS.md   # Development Progress
├── SETUP_AND_BUILD.md          # Detailed Setup Guide
└── README.md                    # This file
```

## 🔐 Security Features

### Password Security
- SHA-256 hashing with 16-byte random salt
- Secure comparison prevents timing attacks
- Passwords validated on both client and server side
- Password change requires verification of old password

### Database Security
- Prepared statements prevent SQL injection
- Input validation on all user inputs
- Cascading deletes maintain referential integrity
- Automatic connection lifecycle management

### Best Practices
- No plain text password storage
- Comprehensive input validation
- Proper error handling without exposing system details
- Session management through controller state

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY,
    username TEXT UNIQUE,
    email TEXT UNIQUE,
    password_hash TEXT,
    first_name TEXT,
    last_name TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Habits Table
```sql
CREATE TABLE habits (
    habit_id INTEGER PRIMARY KEY,
    user_id INTEGER,
    habit_name TEXT,
    description TEXT,
    category TEXT,
    frequency TEXT,          -- daily, weekly, monthly
    streak_count INTEGER,
    is_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### Mood Entries Table
```sql
CREATE TABLE mood_entries (
    entry_id INTEGER PRIMARY KEY,
    user_id INTEGER,
    mood_level TEXT,
    emotional_context TEXT,
    notes TEXT,
    activities TEXT,
    energy_level TEXT,       -- low, medium, high
    timestamp TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

For complete schema details, see `SETUP_AND_BUILD.md`

## 🎓 API Usage Examples

### User Authentication
```java
UserController controller = new UserController();

// Register new user
User newUser = controller.registerUser("john_doe", "john@example.com", 
                                       "password123", "John", "Doe");

// Login
User user = controller.authenticate("john_doe", "password123");
```

### Habit Management
```java
HabitController habitController = new HabitController();

// Create habit
Habit habit = new Habit();
habit.setUserId(1);
habit.setHabitName("Exercise");
habit.setFrequency("daily");
int habitId = habitController.createHabit(habit);

// Mark as completed
habitController.incrementStreak(habitId);

// Get statistics
double rate = habitController.getCompletionRate(habitId, 30);
```

### Mood Tracking
```java
MoodController moodController = new MoodController();

// Log mood
MoodEntry mood = new MoodEntry();
mood.setUserId(1);
mood.setMoodLevel("8");
mood.setEmotionalContext("Happy");
moodController.createMoodEntry(mood);

// Get analytics
double avgMood = moodController.getAverageMood(1, 7);
```

For complete API reference, see `API_REFERENCE.md`

## 🧪 Testing

### Running Unit Tests
```bash
# Using Maven
mvn test

# Using JUnit directly
junit org.junit.runner.JUnitCore tests.UserTest
junit org.junit.runner.JUnitCore tests.HabitTest
```

### Test Coverage
- User authentication and registration
- Habit CRUD operations and streak tracking
- Mood entry logging and analytics
- Database operations
- Password hashing and verification

## 📖 Documentation

- **[API Reference](API_REFERENCE.md)** - Complete API documentation with examples
- **[Implementation Progress](IMPLEMENTATION_PROGRESS.md)** - Detailed development progress
- **[Setup and Build Guide](SETUP_AND_BUILD.md)** - Comprehensive setup instructions
- **[JavaDoc](src/main/)** - Inline code documentation

## 🛠️ Development

### Setting Up Development Environment

1. **Clone repository and navigate to project**
   ```bash
   git clone https://github.com/mmerino90/wellness-tracker.git
   cd wellness-tracker
   ```

2. **Install dependencies** (see SETUP_AND_BUILD.md)

3. **Configure IDE** (IntelliJ IDEA recommended)

4. **Build and run**
   ```bash
   mvn clean javafx:run
   ```

### Code Style
- Follow Google Java Style Guide
- Use meaningful variable and method names
- Add Javadoc comments to public methods
- Organize imports properly
- Use final for constants

### Contributing
1. Create a feature branch: `git checkout -b feature/your-feature`
2. Make your changes with clear commit messages
3. Push to the branch: `git push origin feature/your-feature`
4. Submit a pull request

## 🐛 Known Issues

- Dashboard navigation not yet implemented (in progress)
- Charts and analytics visualization pending
- User interface enhancements in development

## 📅 Development Roadmap

### Phase 1 (In Progress)
- ✅ User authentication system
- ✅ Habit tracker core
- ✅ Mood tracking system
- 🔄 Dashboard UI
- 🔄 View components

### Phase 2 (Planned)
- Analytics and visualization
- Challenge system implementation
- Statistics and reporting
- Data export functionality

### Phase 3 (Planned)
- Mobile app companion
- Cloud synchronization
- Social features
- Advanced analytics

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

**Manuel Merino**
- GitHub: [@mmerino90](https://github.com/mmerino90)
- Email: contact@example.com

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## 📞 Support

For issues, questions, or suggestions:
1. Check existing issues on GitHub
2. Create a new issue with detailed description
3. Include error messages and steps to reproduce

## 🙏 Acknowledgments

- JavaFX for excellent GUI framework
- SQLite for lightweight database solution
- Java community for best practices and patterns

## 📊 Statistics

- **Total Java Files:** 9+ (implemented)
- **Database Tables:** 5
- **Lines of Code:** 2000+
- **Code Documentation:** 100%
- **Test Coverage:** Expanding
- **Development Time:** Ongoing

## 🔗 Useful Links

- [JavaFX Documentation](https://openjfx.io/)
- [SQLite Official Site](https://www.sqlite.org/)
- [Java Security Best Practices](https://owasp.org/www-community/vulnerabilities/Sensitive_Data_Exposure)
- [Maven Official Site](https://maven.apache.org/)

---

**Last Updated:** November 13, 2024  
**Current Version:** 0.1.0 (Alpha)  
**Status:** Active Development

