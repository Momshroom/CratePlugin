# CrateReloaded

[![Build Status](https://github.com/Hazebyte/CrateReloaded/workflows/CI/badge.svg)](https://github.com/Hazebyte/CrateReloaded/actions)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Version](https://img.shields.io/badge/version-2.3.16-green.svg)](https://github.com/Hazebyte/CrateReloaded/releases)
[![Discord](https://img.shields.io/discord/YOUR_DISCORD_ID?label=Discord&logo=discord)](https://discord.gg/0srgnnU1nbB8wMML)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.8--1.21-orange.svg)](https://www.spigotmc.org/)

A powerful and flexible crate plugin for Minecraft servers. Create customizable crates with animated openings, varied rewards, and a comprehensive claim system. Perfect for Spigot, Paper, and compatible server software.

---

## 📚 Documentation

- **[Wiki](https://github.com/Hazebyte/CrateReloaded/wiki)** - Comprehensive guides and tutorials
- **[API Documentation](https://github.com/Hazebyte/CrateReloadedAPI)** - For plugin developers
- **[Configuration Guide](https://github.com/Hazebyte/CrateReloaded/wiki/Configuration)** - All config options explained
- **[Examples](./examples/)** - Example crate configurations

---

## 🛠️ Development

### Prerequisites

Use Java 25 or newer. The Gradle build targets Paper API `26.1.2.build.60-stable`, matching Paper `26.1.2-60`.

```bash
java -version  # Should show Java 25+
```

### Building from Source

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Hazebyte/CrateReloaded.git
   cd CrateReloaded
   ```

2. **Initialize git submodules:**
   ```bash
   ./scripts/init-project.sh
   ```

3. **Build the plugin with Gradle:**
   ```bash
   ./gradlew clean pluginJar
   ```

4. **Find the built JAR:**
   - Gradle: `bukkit/build/libs/CrateReloaded-2.3.16.jar`

The build uses public API artifacts for Paper, PlaceholderAPI, Vault, CMI/CMILib, HolographicDisplays, and DecentHolograms. Local compile-only plugin jars may be placed in `libs/` or under `servers/**/plugins/`; those folders are ignored by git.

### Legacy Maven Build

The original Maven files are still present for reference, but the maintained build path for this fork is Gradle.

```bash
./gradlew clean pluginJar
```

<!--
3. **Build the plugin:**
   ```bash
   # Development build (no obfuscation)
   make build-dev

   # Production build (with ProGuard obfuscation)
   make

   # Just compile
   make compile
   ```

4. **Find the built JAR:**
   - Development: `bukkit/target/CrateReloaded-{version}.jar`
   - Production: `bin/CrateReloaded-{version}.jar` (after ProGuard)
-->

### Running Tests

```bash
./gradlew test
```

### Code Quality

CrateReloaded uses [Spotless](https://github.com/diffplug/spotless) with [Palantir Java Format](https://github.com/palantir/palantir-java-format) for code formatting:

```bash
# Check formatting
make lint

# Auto-fix formatting issues
make lint-fix
```
