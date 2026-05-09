# Contributing to RvSystem Monitor

First off, thank you for considering contributing to RvSystem Monitor! It's people like you who make this tool better for everyone.

## 🚀 How Can I Contribute?

### Reporting Bugs
- Use the **GitHub Issues** tab to report bugs.
- Provide a clear and descriptive title.
- Describe the exact steps which reproduce the problem.
- Include information about your device and Android version.

### Suggesting Enhancements
- Check the **GitHub Issues** to see if the enhancement has already been suggested.
- Provide a clear and descriptive title.
- Explain why this enhancement would be useful to most users.

### Pull Requests
1. **Fork the repo** and create your branch from `main`.
2. If you've added code that should be tested, **add tests**.
3. If you've changed APIs, **update the documentation**.
4. Ensure the test suite passes.
5. Make sure your code follows the existing style:
   - Run `./gradlew spotlessApply` for Kotlin code.
   - Run `cd rust && cargo fmt` for Rust code.
6. Issue that pull request!

## 🏗️ Technical Notes

- **Kotlin**: We use Jetpack Compose and Hilt. Please ensure new UI components are reusable and follow the Material 3 Expressive guidelines.
- **Rust**: Ensure all native code is memory-safe and efficient. Use `OnceCell` for caching where appropriate.

---
Happy coding!
