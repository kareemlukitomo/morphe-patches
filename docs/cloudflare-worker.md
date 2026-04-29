# Cloudflare `patches.json` Worker

This repository includes an optional Worker project that exposes the latest signed bundle as a simple `patches.json` endpoint.

Target hostname:
- `https://morphe.kareem.one/patches.json`

## What It Does

- Redirects any non-`/patches.json` request to the repository homepage.
- Optionally redirects non-canonical hosts back to `PRIMARY_HOST`.
- Reads the public GitHub releases Atom feed for this repository.
- Selects the newest release authored by an allowed actor.
- Optionally requires a detached `.asc` signature before serving that release.
- Returns a Morphe-style bundle document with `version`, `created_at`, `download_url`, and `signature_download_url`.

## Default Asset Expectations

The Worker derives release assets from the tag name:

- bundle: `patches-<version>.mpp`
- signature: `patches-<version>.mpp.asc`

For tag `v1.2.3`, the Worker expects:

- `patches-1.2.3.mpp`
- `patches-1.2.3.mpp.asc`

## Tracked Files

- [cloudflare/patches-json/wrangler.toml](../cloudflare/patches-json/wrangler.toml)
- [cloudflare/patches-json/src/index.js](../cloudflare/patches-json/src/index.js)
- [scripts/sign-latest-release.py](../scripts/sign-latest-release.py)

## Config

The Worker uses only public metadata:

- `PRIMARY_HOST`
- `GITHUB_OWNER`
- `GITHUB_REPO`
- `REPO_HOMEPAGE`
- `ALLOWED_GITHUB_ACTORS`
- `ALLOW_PRERELEASE`
- `REQUIRE_SIGNATURE`

No secret is required to run the Worker itself.

## Deploy

1. Confirm the `morphe.kareem.one` route entries in `wrangler.toml`.
2. Authenticate with Cloudflare.
3. Deploy from `cloudflare/patches-json/`:

```bash
npx wrangler deploy
```

## Signing

If `REQUIRE_SIGNATURE = "true"`, the newest unsigned GitHub release is ignored until the matching `.asc` asset exists.

Use the repo task to sign and optionally upload the missing detached signature:

```bash
mise run sign-release
```
