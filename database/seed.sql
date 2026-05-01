INSERT INTO users (id, username, email, password_hash, role) VALUES
(1, 'admin', 'admin@devpulse.local', '$2b$12$fbghkGiEsi1uqnS7Jui1qucxA0Ug.su0PoEMYhGuMPX8V.TnBChTO', 'ADMIN'),
(2, 'viewer', 'viewer@devpulse.local', '$2b$12$6OcFH8YJ29Kq7sXlxUzAauaR6fZx0ikJRABq7Q9mDY9VJ18FVw7AO', 'VIEWER');

INSERT INTO repositories (id, name, github_repo_url, description, created_at) VALUES
(1, 'devpulse-api', 'https://github.com/example/devpulse-api', 'Spring Boot API for DevPulse', NOW() - INTERVAL 10 DAY),
(2, 'devpulse-web', 'https://github.com/example/devpulse-web', 'React dashboard for DevPulse', NOW() - INTERVAL 8 DAY);

INSERT INTO pipelines (id, repository_id, branch_name, status, triggered_at, completed_at, commit_sha, commit_message) VALUES
(1, 1, 'main', 'SUCCESS', NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY + INTERVAL 8 MINUTE, 'a4d8c91', 'Add JWT authentication flow'),
(2, 1, 'main', 'FAILED', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY + INTERVAL 5 MINUTE, 'f72b3aa', 'Wire GitHub repository import'),
(3, 1, 'release/1.1', 'RUNNING', NOW() - INTERVAL 25 MINUTE, NULL, '3bc9f20', 'Prepare production rollout'),
(4, 2, 'main', 'SUCCESS', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY + INTERVAL 6 MINUTE, '9e81adc', 'Build deployment chart'),
(5, 2, 'feature/release-table', 'PENDING', NOW() - INTERVAL 12 MINUTE, NULL, 'd660f13', 'Add release creation modal');

INSERT INTO releases (id, repository_id, version_tag, environment, status, deployed_at, notes) VALUES
(1, 1, 'v1.0.0', 'DEV', 'DEPLOYED', NOW() - INTERVAL 6 DAY, 'Initial backend deployment'),
(2, 2, 'v1.0.0', 'STAGING', 'DEPLOYED', NOW() - INTERVAL 3 DAY, 'Dashboard staging rollout'),
(3, 1, 'v1.1.0', 'PROD', 'IN_PROGRESS', NOW() - INTERVAL 20 MINUTE, 'Production rollout in progress');

INSERT INTO commit_snapshots (repository_id, sha, message, author, timestamp) VALUES
(1, 'a4d8c91b4ad0f7e514ed32acbc22e52f29d61393', 'Add JWT authentication flow', 'Avery Stone', NOW() - INTERVAL 6 DAY),
(1, 'f72b3aa89d042fc1c801cd819c90d4f2d3a23476', 'Wire GitHub repository import', 'Mina Park', NOW() - INTERVAL 4 DAY),
(1, '3bc9f20069f967f54f65b752b4a051cb9f870130', 'Prepare production rollout', 'Sam Rivera', NOW() - INTERVAL 25 MINUTE),
(2, '9e81adcca6a4239c0ddc4e492ac8a844567aac1a', 'Build deployment chart', 'Noah Chen', NOW() - INTERVAL 3 DAY),
(2, 'd660f133a728283f97379d7c7d82e74fc6e25f45', 'Add release creation modal', 'Priya Nair', NOW() - INTERVAL 12 MINUTE);
