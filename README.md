## QardyLink
A simple NFC tag management tool using the Java Swing framework.

### Features
- Manage NFC tags with a user-friendly Java Swing interface.

- Windows executable installer provided for easy setup.

### Installation
#### Windows
A Windows installer is available as QardyLin-1.0.exe in the windowsApp folder. It includes all dependencies-no separate Java installation required.

<a href="windowsApp/QardyLin-1.0.exe" download>Download QardyLin-1.0.exe</a>

To install:

1. Navigate to the windowsApp folder.

2. Double-click QardyLin-1.0.exe and follow the installation prompts.

3. After installation, launch QardyLink from the Start Menu or Desktop  shortcut.

### Build & Packaging Instructions


- Create classes:


```javac -d out src/*.java ```

Copy images:


``` xcopy /E /I images out\images```

Create jar file:


```cd out```

and 

```jar cfe app.jar QardylAppLink *```

Test the app before packing:


```java -jar app.jar```

Package the app as a Windows installer:


```jpackage --type exe --input out --dest "D:\QardyLinkApp" --temp "D:\QardyLinkTemp" --name QardyLink --main-jar app.jar --main-class QardylAppLink --icon appcon.ico --win-shortcut --win-menu --add-modules java.smartcardio,java.desktop```

### Notes
- The installer bundles a custom Java runtime, so users do not need to pre-install Java.

- For advanced deployment or customization, tools like jpackage, Launch4j, or Inno Setup can be used to further tailor the installer and runtime environmen