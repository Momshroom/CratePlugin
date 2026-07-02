# Local compile-only plugin jars

Put local/proprietary plugin API jars here when a public Maven artifact is not available or you need to test against a specific server jar.

These jars are intentionally ignored by git:

- `CMI*.jar`

The Gradle build also checks ignored local test servers under `servers/**/plugins/`.
