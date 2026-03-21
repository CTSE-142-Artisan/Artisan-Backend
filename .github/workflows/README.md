## Snyk workflow

This repository includes `snyk.yml` to scan the Maven dependencies for:

- `api-gateway`
- `listing-service`
- `order-service`
- `review-service`
- `user-service`

### Required GitHub secret

- `SNYK_TOKEN`

Create an API token in Snyk, then add it as a repository secret in GitHub.

### What the workflow does

- Runs `snyk test` on pull requests and pushes to `dev`, `main`, or `master`.
- Uploads SARIF output so findings appear in GitHub code scanning.
- Runs `snyk monitor` on pushes so Snyk keeps tracking the latest dependency snapshot.

### Notes

- The workflow currently scans open source Maven dependencies. If you also want Snyk Code or container image scans, add separate jobs for those.
