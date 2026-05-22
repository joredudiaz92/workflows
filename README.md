## Continuous Integration (CI) Workflow

This repository uses GitHub Actions to automatically build, test, lint, and analyze code coverage for every code change. The workflow configuration is located in `.github/workflows/ci.yml`.

### Workflow Triggers

The CI pipeline is fully automated and runs under the following conditions:
* **Pushes:** Runs on a `push` to any single-level branch (`branches: ['*']`).
* **Pull Requests:** Runs on a `pull_request` targeting any branch, including multi-level feature branches (`branches: ['**']`).

---

### Pipeline Steps & Jobs

The workflow runs a single `build` job on an `ubuntu-latest` runner, executing the following sequential steps:

1. **Checkout Code:** Uses `actions/checkout` to clone the repository code into the runner.
2. **Build Project:** Compiles the project using `spring-io/spring-gradle-build-action` pre-configured with **Java 25**.
3. **Run Tests:** Grants execution permissions to the Gradle wrapper (`chmod +x gradlew`) and runs `./gradlew test` to execute the test suite.
4. **Process Coverage Report:** Uses `madrapps/jacoco-report` to parse the JaCoCo XML report generated at `${{ github.workspace }}/build/reports/jacoco/test/jacocoTestReport.xml`.
5. **Enforce Coverage Threshold:** A custom GitHub script checks if the overall code coverage falls below **80%**. If it does, the workflow explicitly fails.
6. **Code Style Linting:** Uses `dbelyaev/action-checkstyle` to validate the code formatting against your configuration file located at `/config/checkstyle/checkstyle.xml`.
    * **Note:** For Pull Requests, formatting errors are reported directly inside the GitHub PR conversation tab (`github-pr-review` reporter).

---

### Required Repository Setup

For this workflow to execute successfully without permission errors, you must ensure the following settings are configured in GitHub:

1. **GitHub Token Permissions:**
   The workflow relies on the automatic `${{ secrets.GITHUB_TOKEN }}` to publish JaCoCo reports and post inline Checkstyle PR reviews.
    * Navigate to **Settings > Actions > General**.
    * Under **Workflow permissions**, select **Read and write permissions**.
    * Click **Save**.

2. **Project Files:**
    * Your project must have a valid Gradle wrapper (`gradlew` script) in the root directory.
    * Your Gradle build file must be configured to generate a JaCoCo XML report at `build/reports/jacoco/test/jacocoTestReport.xml`.
    * A Checkstyle configuration file must exist precisely at `config/checkstyle/checkstyle.xml`.


## Jenkins CI/CD Pipeline for Java Application

This repository contains a `Jenkinsfile` that defines a declarative CI/CD pipeline. The pipeline automates building a Java application using Gradle, creating a Docker image, and pushing it to Docker Hub.

### Pipeline Workflow

[Start] ──> [Build Java App] ──> [Build Docker Image] ──> [Push to Docker Hub] ──> [Post-Clean]

1. **Build Java App**: Grants execution permissions to the Gradle wrapper and builds the application. It skips tests and checkstyle verification to speed up the build.
2. **Build Docker Image**: Creates a localized Docker image using a `Dockerfile` located in the root directory. It tags the image with the current Jenkins build number.
3. **Push to Docker Hub**: Logs into Docker Hub using secured credentials, uploads the build-specific tagged image, and updates the `latest` tag.
4. **Post-Clean (Always)**: Deletes the locally generated Docker images from the Jenkins agent to optimize disk space.

### Environment Variables

The pipeline configures the following global variables:
* `REGISTRY_USER`: The Docker Hub namespace (`joredudiaz92`).
* `IMAGE_NAME`: The repository name on Docker Hub (`workflows`).
* `IMAGE_TAG`: Automatically set to the active Jenkins `${BUILD_NUMBER}`.
* `IMAGE_FULL`: The complete target image path (`joredudiaz92/workflows:${BUILD_NUMBER}`).

### Required Repository Setup

To run this pipeline successfully, ensure your GitHub/GitLab repository contains the following structure and assets:

#### 1. Files in Repository Root
* **`gradlew` & `gradle/`**: The Gradle wrapper script and its support files must be present in the root folder.
* **`Dockerfile`**: A standard Dockerfile in the root folder configured to package the generated Java artifact (typically a `.jar` file from `build/libs/`).

#### 2. Jenkins Prerequisites
* **Jenkins Plugins**: Install the **Pipeline** plugin and the **Docker Pipeline** plugin on your Jenkins server.
* **Docker Engine**: The Jenkins agent executing the job must have Docker installed and the `jenkins` user must have permissions to run Docker commands.

#### 3. Credential Configuration
You must store your Docker Hub access tokens inside Jenkins:
1. Navigate to **Manage Jenkins** > **Credentials** > **System** > **Global credentials**.
2. Add a new credential of type **Username with password**.
3. Set the **ID** strictly to `docker-hub-credentials`.
4. Input your Docker Hub username and a personal access token (recommended) or password.