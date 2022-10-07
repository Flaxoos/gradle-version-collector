# gradle-version-collector
Script to collect gradle dependency versions to a central file

The script will go over all the `gradle.kts` files and move any version definitions to a Versions.kt file to be placed in your buildSrc directory

so
```kotlin
implementation("com.stuff:thing:1.2.3")
```
will change to
```kotlin
implementation("com.stuff:thing:$thingVersion")
```
and your Versions file will contain an entry
```kotlin
const val thingVersion = "1.2.3"
```
