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
