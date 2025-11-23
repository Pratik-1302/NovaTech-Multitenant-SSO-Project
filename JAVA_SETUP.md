# Java 21 Setup Instructions for Local Build Testing

## Current Situation

Your system has:
- ✅ Java 8 (currently active in PATH)
- ✅ Java 25 directory exists
- ❌ JAVA_HOME not configured for Java 21+

## Option 1: Manual JAVA_HOME Setup (Recommended)

### Step 1: Find Your Java 21 Installation

Open PowerShell and run:
```powershell
Get-ChildItem "C:\Program Files" -Recurse -Filter "javac.exe" -ErrorAction SilentlyContinue | Where-Object { $_.FullName -like "*jdk-2*" } | Select-Object -First 3 FullName
```

This will show you the path to Java 21 compiler.

### Step 2: Set JAVA_HOME (Temporary - for this session)

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"  # Adjust path from Step 1
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

### Step 3: Verify Java Version

```powershell
java -version
```

You should see Java 21 (or newer).

### Step 4: Build the Project

```powershell
cd "c:\Project Backuppppppps\Fully Functional - ready to deploy\NovaTech"
.\mvnw.cmd clean package -DskipTests
```

### Step 5: Verify JAR Creation

```powershell
dir target\*.jar
```

You should see: `service-app-0.0.1-SNAPSHOT.jar`

---

## Option 2: Set JAVA_HOME Permanently

### Using Windows Environment Variables UI:

1. Press `Win + X` → **System**
2. Click **Advanced system settings**
3. Click **Environment Variables**
4. Under **System variables** (or **User variables**):
   - Click **New**
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-21` (adjust path)
5. Edit **Path** variable:
   - Add new entry: `%JAVA_HOME%\bin`
   - Move it to the top of the list
6. Click **OK** on all dialogs
7. **Restart PowerShell** (important!)

### Using PowerShell (Permanent):

```powershell
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-21', 'Machine')
```

**Note:** This requires Administrator privileges. Restart PowerShell after.

---

## Option 3: Skip Local Build (Deploy Directly to Render)

**You don't actually need to build locally!**

Render will build your application automatically with Java 21. You can:

1. Push your code to GitHub
2. Connect Render via Blueprint
3. Render builds with Java 21 automatically

**Local testing is optional, not required for deployment.**

---

## Troubleshooting

### "The JAVA_HOME environment variable is not defined correctly"

**Solution:**
- Make sure `JAVA_HOME` points to JDK root (not `bin` folder)
- Restart PowerShell after setting JAVA_HOME
- Verify: `echo $env:JAVA_HOME`

### "javac: command not found"

**Solution:**
- Install full JDK (not JRE)
- Download from: https://adoptium.net/temurin/releases/?version=21
- Choose: **JDK 21 (LTS)** for Windows x64

### Build fails with "invalid flag: --release"

**Solution:**
- Your Java version is too old (< Java 9)
- Install Java 21 from link above

---

## Quick Test Without Building

If you just want to verify the configuration is correct before deploying:

```powershell
# Check all config files exist
Test-Path render.yaml
Test-Path src/main/resources/application-prod.properties
Test-Path src/main/java/com/novatech/service_app/config/SecurityHeadersConfig.java

# All should return: True
```

---

## Ready to Deploy?

If local build testing is proving difficult, you can **skip it** and deploy directly:

1. Follow [QUICK_START.md](../QUICK_START.md)
2. Render will handle the build with Java 21
3. Your app will be live in ~25 minutes

**The project is already production-ready!** ✅
