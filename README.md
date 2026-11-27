# Maven Central proxying

GitHub Actions to route Maven and Gradle builds through a Maven Central mirror.

## `maven-central-mirror`

Makes all later Maven and Gradle builds in the job download Maven Central artifacts from a mirror.

```
Maven  ──► MAVEN_ARGS --global-settings settings.xml ──┐
mvnw   ──► MVNW_REPOURL (Maven distribution) ──────────┼──► mirror ──► Maven Central
Gradle ──► ~/.gradle/init.d/maven-mirror.init.gradle ──┘
```

| Input      | Description                                          |
|------------|------------------------------------------------------|
| `url`      | Mirror base URL, e.g. `https://repo.example.com/central` |
| `username` | Mirror username                                      |
| `password` | Mirror password or token                             |

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
