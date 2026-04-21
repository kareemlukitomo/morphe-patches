# Kareem Patches for Morphe

Targeted Morphe-compatible patches for small app-specific fixes. This repository currently focuses on Instagram share links.

## Supported App

Current public support:
- Instagram `424.0.0.49.64`
- Instagram `425.0.0.0.0` experimental

Current patch set:
- `Change Instagram share domain`

## Use In Morphe

Add this repository as a patch source in Morphe:
- [Add source in Morphe](https://morphe.software/add-source?github=kareemlukitomo/morphe-patches)
- Manual source URL: `https://github.com/kareemlukitomo/morphe-patches`

Notes:
- Releases publish a `.mpp` patch bundle on GitHub.

## Release Flow

- Development happens on feature branches and lands on `dev` before `main`.
- Semantic versioning and semantic commits drive automated releases.
- Pushing to `dev` or `main` runs [release.yml](.github/workflows/release.yml).
- Release metadata is written to `patches-bundle.json` and `patches-list.json`.

## Build From Source

Requirements:
- JDK 17
- Access to `https://maven.pkg.github.com/MorpheApp/registry`

Example:

```bash
export GITHUB_ACTOR=<github-user>
export GITHUB_TOKEN=<github-token>
JAVA_HOME=/path/to/jdk17 \
PATH=/path/to/jdk17/bin:$PATH \
./gradlew :patches:buildAndroid generatePatchesList --no-daemon
```

Artifacts are written under `patches/build/libs/`.

## Maintainer Notes

- Contribution guide: [CONTRIBUTING.md](CONTRIBUTING.md)

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE), with additional conditions under GPLv3 Section 7.

- `Morphe` is referenced only to describe compatibility.
- Derivative works must use distinct branding as described in [NOTICE](NOTICE).

See [LICENSE](LICENSE) and [NOTICE](NOTICE) for the full terms.
