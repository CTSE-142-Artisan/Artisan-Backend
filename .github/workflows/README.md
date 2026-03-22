# GitHub Actions Workflows

This directory contains the continuous integration (CI), continuous deployment (CD), and security scanning workflows for the Artisan-Backend repository.

## CI Workflows
Each microservice has its own dedicated CI workflow to ensure isolated builds and testing. These run on pull requests and pushes to tracking branches.
- `api-gateway-ci.yml`
- `listing-service-ci.yml`
- `order-service-ci.yml`
- `review-service-ci.yml`
- `user-service-ci.yml`

## Azure Container App Deploy (`azure-containerapp-deploy.yml`)
This workflow handles the continuous deployment of the microservices to Azure Container Apps. Ensure the necessary Azure credentials and active environment values are configured in the repository secrets for this to run successfully.

## Snyk Security (`snyk.yml`)

This workflow scans the Maven dependencies for all microservices in the repository to detect vulnerabilities.

### Required GitHub secret
- `SNYK_TOKEN` (Create an API token in Snyk, then add it as a repository secret in GitHub).

### What the workflow does
- Runs `snyk test` on pull requests and pushes to `dev`, `main`, or `master`.
- Uploads SARIF output so findings appear in GitHub code scanning.
- Runs `snyk monitor` on pushes so Snyk keeps tracking the latest dependency snapshot.

### Notes
- The workflow currently scans open source Maven dependencies. If you also want Snyk Code or container image scans, add separate jobs for those.
