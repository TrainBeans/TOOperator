# TOOperator

Train Order Operator MVP that exposes existing Spring Boot tools from one browser entry point.

[![CI Smoke](https://github.com/TrainBeans/TOOperator/actions/workflows/ci-smoke.yml/badge.svg)](https://github.com/TrainBeans/TOOperator/actions/workflows/ci-smoke.yml)

`CI Smoke` runs on push to `main`, pull requests, and manual dispatch.

## What this MVP does

- Hosts a single landing page at `/`.
- Proxies to Train Order app at `/train-order/`.
- Proxies to Clearance Card app at `/clearance-card/`.
- Runs everything on one physical machine with Docker Compose.

## Repository layout

- `docker-compose.yml` - orchestrates all services.
- `portal/index.html` - common entry page.
- `portal/nginx.conf` - reverse proxy and routing rules.
- `scripts/validate-compose.sh` - tiny validation harness.
- `scripts/health-check.sh` - endpoint health checks for portal plus upstream `/actuator/health` readiness.
- `scripts/smoke-test.sh` - basic functional smoke test of the launcher page.
- `scripts/ci-smoke.sh` - CI-friendly end-to-end smoke run (build, start, test, teardown).
- `scripts/run-app.sh` - local startup helper for running the full MVP stack.
- `scripts/stop-app.sh` - stops the running stack.
- `.github/workflows/ci-smoke.yml` - GitHub Actions workflow for CI smoke validation.
- `.github/workflows/docker-images.yml` - builds and publishes TrainOrder and ClearanceCard container images to GHCR on pushes to `main`.
- `deploy/to-operator.service` - optional `systemd` unit for auto-start on boot.

## Prerequisites

- Docker Engine with Compose plugin (`docker compose`).
- Local sibling repositories:
  - `../TrainOrder`
  - `../ClearanceCard`

## Validate the stack config

```bash
chmod +x scripts/validate-compose.sh
./scripts/validate-compose.sh
```

## Start the MVP

```bash
docker compose up --build
```

Open:

- `http://localhost:8080/`

Or use the helper script:

```bash
chmod +x scripts/run-app.sh
./scripts/run-app.sh
```

Optional script flags:

```bash
./scripts/run-app.sh --foreground
./scripts/run-app.sh --skip-health-check
```

## Stop the MVP

```bash
chmod +x scripts/stop-app.sh
./scripts/stop-app.sh
```

## Data persistence

The Train Order and Clearance Card apps persist their data to `/app/data` inside
their containers. `docker-compose.yml` mounts named Docker volumes there so data
survives container restarts and recreation:

- `trainorder-data` -> `trainorder:/app/data`
- `clearancecard-data` -> `clearancecard:/app/data`

Data is retained across `docker compose down` (and `./scripts/stop-app.sh`,
which uses `down` without `-v`). To remove the persisted data, explicitly run:

```bash
docker compose down -v
```

Inspect the volumes with:

```bash
docker volume ls | grep to-operator
```

## Health checks and smoke tests

```bash
chmod +x scripts/health-check.sh scripts/smoke-test.sh
./scripts/health-check.sh
./scripts/smoke-test.sh
```

You can pass a custom base URL to either script:

```bash
./scripts/health-check.sh http://localhost:8080
./scripts/smoke-test.sh http://localhost:8080
```

## CI smoke runner

`scripts/ci-smoke.sh` is the one-command smoke flow for CI or local verification.

```bash
chmod +x scripts/ci-smoke.sh
./scripts/ci-smoke.sh
```

Optional custom base URL:

```bash
./scripts/ci-smoke.sh http://localhost:8080
```

## GitHub Actions workflow

`/.github/workflows/ci-smoke.yml` runs the smoke flow on push, pull request, and manual dispatch.
It checks out `TOOperator`, pulls prebuilt TrainOrder and ClearanceCard images from GHCR, then executes `scripts/ci-smoke.sh`.

`scripts/ci-smoke.sh` supports prebuilt-image mode by setting:

```bash
export CI_SMOKE_SKIP_BUILD=true
export TRAIN_ORDER_IMAGE=ghcr.io/trainbeans/tooperator-trainorder:latest
export CLEARANCE_CARD_IMAGE=ghcr.io/trainbeans/tooperator-clearancecard:latest
```

## Optional environment overrides

If your repositories are not in default sibling paths, set these before running `docker compose`:

```bash
export TRAIN_ORDER_PATH=/absolute/path/to/TrainOrder
export CLEARANCE_CARD_PATH=/absolute/path/to/ClearanceCard
export CLEARANCE_CARD_DOCKERFILE=Dockerfile
export PORTAL_PORT=8080
```

## Best deployment approach for one physical machine

For your use case, a **single Docker Compose project** is the most practical deployment method:

- One command to start/stop all apps.
- Strong isolation between services while sharing one host.
- Stable local networking by service name (`trainorder`, `clearancecard`).
- Easy updates of each app independently.

For production-style reliability, run Compose as a `systemd` service on Linux so it restarts on boot and after failures.

### Optional `systemd` setup

`deploy/to-operator.service` assumes this repo lives at `/home/paul/Trainbeans/TOOperator`.

```bash
sudo cp deploy/to-operator.service /etc/systemd/system/to-operator.service
sudo systemctl daemon-reload
sudo systemctl enable --now to-operator.service
```

Check status:

```bash
systemctl status to-operator.service
```
