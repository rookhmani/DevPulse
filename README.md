# DevPulse

DevPulse is a full-stack CI/CD Release Pipeline Tracker and Dashboard. It tracks GitHub repositories, pipeline runs, deployment releases, live pipeline status, and release metrics across DEV, STAGING, and PROD environments.

## Architecture

- `backend/`: Spring Boot 3 REST API with Spring Data JPA, JWT security, role-based access, GitHub API integration, and MySQL/PostgreSQL support.
- `frontend/`: React + Vite dashboard with Axios, React Router v6, Recharts, protected routes, and 15-second polling for real-time status.
- `database/seed.sql`: sample SQL for 2 repositories, 5 pipeline runs, 3 releases, commit snapshots, and demo users.
- `docker-compose.yml`: production-style local deployment with MySQL, the Spring Boot API, and an nginx-served React build.

## Docker Deployment

```bash
cd Project
copy .env.example .env
docker compose up --build
```

Open `http://localhost:3000`. The frontend proxies `/api` to the backend container.

For production, change these values before deploying:

- `JWT_SECRET`
- database username/password/root password in `docker-compose.yml`
- `CORS_ALLOWED_ORIGIN`
- `GITHUB_TOKEN` if you need private repositories or higher GitHub API rate limits

## Backend Setup

1. Create a MySQL database named `devpulse`.
2. Update `backend/src/main/resources/application.properties` with your database credentials.
3. Optional: set `github.token` for private repositories or higher GitHub API rate limits.
4. Start the API:

```bash
cd backend
mvn spring-boot:run
```

Default API URL: `http://localhost:8080`

Health check:

```bash
curl http://localhost:8080/api/health
```

Demo users created automatically:

- Admin: `admin@devpulse.local` / `admin123`
- Viewer: `viewer@devpulse.local` / `viewer123`

## Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Default UI URL: `http://localhost:3000`

Set a custom API URL with `VITE_API_BASE_URL=http://localhost:8080/api` if needed.

## Seed Data

After Hibernate creates the tables, run:

```bash
mysql -u devpulse_user -p devpulse < database/seed.sql
```

The app also creates demo users on startup, so skip the first `INSERT INTO users` block if those accounts already exist.

## API Examples

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@devpulse.local","password":"admin123"}'
```

List repositories:

```bash
curl http://localhost:8080/api/repositories \
  -H "Authorization: Bearer <JWT>"
```

Add repository:

```bash
curl -X POST http://localhost:8080/api/repositories \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"githubRepoUrl":"https://github.com/spring-projects/spring-boot"}'
```

Create pipeline:

```bash
curl -X POST http://localhost:8080/api/pipelines \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"repositoryId":1,"branchName":"main","status":"RUNNING","commitSha":"abc1234","commitMessage":"Ship release candidate"}'
```

Update pipeline status:

```bash
curl -X PATCH http://localhost:8080/api/pipelines/1/status \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"status":"SUCCESS"}'
```

Create release:

```bash
curl -X POST http://localhost:8080/api/releases \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"repositoryId":1,"versionTag":"v1.2.0","environment":"PROD","status":"DEPLOYED","notes":"Production release"}'
```

Dashboard metrics:

```bash
curl http://localhost:8080/api/dashboard/metrics \
  -H "Authorization: Bearer <JWT>"
```
