
# VibeForge Vian Launcher

A secure, feature-rich Android launcher with a sandboxed plugin system, encrypted database, and real-time system monitoring.

## 🚀 Building the APK on GitHub

This repository is configured with **GitHub Actions** to automatically build an APK every time you push code to the `main` branch.

### How to get your APK:
1.  Push your changes to GitHub.
2.  Go to the **Actions** tab in your GitHub repository.
3.  Click on the latest workflow run (e.g., "Android CI").
4.  Once the build is complete, scroll down to the **Artifacts** section.
5.  Download the `vian-launcher-debug` zip file, which contains your `.apk`.

### ⚠️ Installation Note:
*   **IMPORTANT**: If you have a previous version of this app (or any app with the same package name `com.vibeforge.vian.launcher.v2`) installed, you **MUST uninstall it first**. This is because the signing keys will differ between your local build and the GitHub Actions build.
*   If the **Package Installer crashes**, ensure you have uninstalled any existing version and that your device allows installation from unknown sources.

## 🛠️ Development Environment

You can use this repository as a full development environment:
- **GitHub Codespaces:** You can open this repo in a Codespace to edit code directly in your browser.
- **Android Studio:** Clone this repository and open it in Android Studio for full local development.

## 📦 Features
- **Plugin System (J2V8):** Run sandboxed JavaScript plugins.
- **Encrypted Database:** SQLCipher-backed Room database for secure storage.
- **Biometric Locking:** Secure your apps with fingerprint/face unlock.
- **System Dashboard:** Real-time RAM and Storage monitoring.
- **Internal Log Keeper:** Terminal-style logging for debugging.

## 📜 License
This project is licensed under the MIT License.
