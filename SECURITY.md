# Security Policy

Thank you for helping us keep the Task Management Backend and its users safe. This document describes how we handle security reports and the expectations we have when collaborating on a fix.

## Supported Versions

Security updates are applied to actively developed branches only. We currently release fixes to:

| Version | Supported |
|---------|-----------|
| `main` branch (`0.0.1-SNAPSHOT`) | ✅ |

Archived releases, forks, or custom deployments are not maintained by the project team. If you are running a fork, please port any patches that we publish to `main`.

## Reporting a Vulnerability

Please report suspected vulnerabilities through the repository's private advisory workflow: [https://github.com/genzitizens/TaskManagement-Backend/security/advisories/new](https://github.com/genzitizens/TaskManagement-Backend/security/advisories/new). The advisory form keeps the report private between you and the maintainers until we publish a fix.

When filing a report, include as much detail as possible:

- A clear description of the issue and the impact you observed.
- Steps to reproduce, including sample requests, payloads, or configuration files.
- The commit, tag, or container image you tested against.
- Any suggested mitigations.

If you are unable to use the advisory workflow, you may alternatively open an issue marked **Security**. Avoid posting proof-of-concept code or exploit details in public issues until we have coordinated a fix.

### Response Expectations

- **Acknowledgement:** We aim to acknowledge new reports within **2 business days**.
- **Status updates:** We will provide progress updates at least every **7 days** while we work on the fix.
- **Disclosure:** We prefer coordinated disclosure. We will agree on a public disclosure date with you once a fix is available.

## Coordinated Disclosure Process

1. We investigate the report and reproduce the vulnerability.
2. If remediation is required, we prepare a fix and validation tests, then review and merge the changes into `main`.
3. Once the fix is available, we publish an advisory summarizing the impact, affected versions, and mitigation steps. When possible, we credit reporters who wish to be acknowledged.
4. After the fix is public, feel free to disclose the issue following the agreed timeline. Please give users sufficient time to update before releasing exploit details.

## Hardening Guidance

While we work on fixes, you can reduce exposure by following these deployment best practices:

- Run the service behind an authenticated reverse proxy or API gateway so that only trusted clients can access management APIs.
- Store secrets such as database credentials in environment variables or a secret manager, and rotate them if a breach is suspected.
- Keep your runtime environment up to date with the latest Java and operating system security patches.
- Monitor logs for unexpected authentication failures, privilege escalations, or API traffic spikes.

We appreciate your help in improving the security of Task Management Backend. If you have any questions about this policy, please open a discussion thread so that we can clarify the process for everyone.
