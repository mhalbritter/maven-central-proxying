# Maven Central proxying

GitHub Actions to route Maven and Gradle builds through a Maven Central mirror.

## `maven-central-mirror`

Makes all later Maven and Gradle builds in the job download Maven Central artifacts from a mirror.

```
Maven  ──► MAVEN_ARGS / MAVEN_CONFIG --global-settings settings.xml ──┐
mvnw   ──► MVNW_REPOURL (Maven distribution) ─────────────────────────┼──► mirror ──► Maven Central
Gradle ──► ~/.gradle/init.d/maven-mirror.init.gradle ─────────────────┘
```

| Input      | Description                                          |
|------------|------------------------------------------------------|
| `url`      | Mirror base URL, e.g. `https://repo.example.com/central` |
| `username` | Mirror username                                      |
| `password` | Mirror password or token                             |

### Maven Wrapper

`mvnw` downloads Maven through the mirror only if it is an [Apache Maven Wrapper](https://maven.apache.org/tools/wrapper/) 3.2.0 or newer. Older wrappers, e.g. Takari's `io.takari:maven-wrapper`, ignore the mirror and download Maven from Maven Central.

Check the version in the `mvnw` header:

```
# Apache Maven Wrapper startup batch script, version 3.3.4   ← supported
# Maven2 Start Up Batch script                               ← too old
```

Update it, keeping the Maven version from `.mvn/wrapper/maven-wrapper.properties`:

```bash
./mvnw -N org.apache.maven.plugins:maven-wrapper-plugin:3.3.4:wrapper
```

This replaces `mvnw`, `mvnw.cmd` and `maven-wrapper.properties`, and deletes `.mvn/wrapper/maven-wrapper.jar`.

## `blackhole-maven-central`

Makes Maven Central unreachable for the rest of the job. Use it before the mirror action to prove builds don't bypass the mirror.

| Input   | Default                                  | Description                            |
|---------|------------------------------------------|----------------------------------------|
| `hosts` | `repo.maven.apache.org repo1.maven.org`  | Space-separated Maven Central hostnames |

It doesn't catch hardcoded IPs, or containers that have their own DNS.

## Usage

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    env:
      # Step conditions can't read secrets
      HAS_MAVEN_MIRROR: ${{ secrets.MAVEN_MIRROR_URL != '' }}
    steps:
      - uses: actions/checkout@v7
      - uses: actions/setup-java@v6
        with:
          distribution: 'temurin'
          java-version: '25'
          cache: 'maven' # or 'gradle'

      # Optional: proves the build doesn't bypass the mirror
      - name: Blackhole Maven Central
        if: ${{ env.HAS_MAVEN_MIRROR == 'true' }}
        uses: mhalbritter/maven-central-proxying/.github/actions/blackhole-maven-central@main

      - name: Set up Maven Central mirror
        if: ${{ env.HAS_MAVEN_MIRROR == 'true' }}
        uses: mhalbritter/maven-central-proxying/.github/actions/maven-central-mirror@main
        with:
          url: ${{ secrets.MAVEN_MIRROR_URL }}
          username: ${{ secrets.MAVEN_MIRROR_USERNAME }}
          password: ${{ secrets.MAVEN_MIRROR_PASSWORD }}

      # Maven
      - run: ./mvnw --batch-mode verify
      # Gradle
      - run: ./gradlew build
```

## Running locally

Uses the same files as the action.

```bash
# Read by settings.xml and the init script
export MAVEN_MIRROR_URL=https://repo.example.com/central
export MAVEN_MIRROR_USERNAME=me
export MAVEN_MIRROR_PASSWORD=secret

base=https://raw.githubusercontent.com/mhalbritter/maven-central-proxying/main/.github/actions/maven-central-mirror
```

### Maven:

```bash
curl -fsSLO "$base/settings.xml"
./mvnw --global-settings settings.xml -Dmaven.repo.local=/tmp/mirror-test-m2 verify
```

Drop `-Dmaven.repo.local` to use your Maven cache.

### Gradle:

```bash
curl -fsSLO "$base/maven-mirror.init.gradle"
GRADLE_USER_HOME=/tmp/mirror-test-gradle ./gradlew -I maven-mirror.init.gradle build --info
```

Drop the `GRADLE_USER_HOME` variable to use your Gradle cache.

