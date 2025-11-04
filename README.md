## Backend exam assignments fall 2025

This is a simple REST API for managing candidates and skils.
It is implemented using Javalin and JPA/Hibernate.
The API includes JWT-based authentication and integrates with an external Skills API.

### How to run

1. Create a PostgreSQL database in your local instance called eksamensdb

2. Configure environment variables (for example in a .env file):
      DB_URL=jdbc:postgresql://localhost:5432/eksamensdb
      DB_USER=<your_db_user>
      DB_PASSWORD=<your_db_password>
      JWT_SECRET=<your_secret>
      
3. Run the main method in the config.Populate class to populate the database with sample data

4. Run the main method in the Main class to start the server on port 7007

5. See all routes in your browser at http://localhost:7007/routes

6. Try requesting http://localhost:7007/candidates to see the list of candidates

7. Use the requests.http file to test the available routes (GET, POST, PUT, DELETE)

## Docker commands

```bash
docker-compose up -d
docker-compose down
docker logs -f  watchtower
docker logs watchtower
docker logs hotelAPI
docker logs db
docker container ls
docker rmi <image_id>
docker stop <container_id>
docker rm <container_id>
```
## User Story Progress Overview

| User Story                               | Implementation & Progress                                                                                           |
|------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| **US1: As a system administrator**       | ✅ Implemented HibernateConfig, Candidate & Skill entities, Populator with sample data. Fully functional and tested. |
| **US2: As a developer - CRUD**           | ✅ DAO layer complete using generics and DTO mapping. CRUD works with database.                                      |
| **US3: As a REST API consomer**          | ✅ All routes implemented (GET, POST, PUT, DELETE, addSkill). Tested manually via `requests.http`.                   |
| **US4: As a recriuter - category**       | ✅ Implemented category filtering using Streams and JPA query. Returns candidates by category.                       |
| **US5: As a recriuter - market insights** | 🟡 See popularity and salary for each candidate's skill - Using fetched api data.                                   |
| **US6: As an analyst**                   | 🟡 Working with external API.                                                                                       |
| **US7: REST Testing**                    | ✅ Tests for basic CRUD working, but JWT-protected routes still need token handling.                                 |
| **US8: Security (JWT + Roles)**          | 🟡 Login endpoint + JWT roles implemented. Works manually, tests pending.                                           |

---

## Guiding Grade Criteria Progress Overview
| Dimension                       | Points (Max) | Implementation & Progress                                |
|---------------------------------|--------------|----------------------------------------------------------|
| **REST Design and correctness** | 20           | ✅ Endpoints, controller, status codes, DTO shapes.       |
| **DATA MODEL & JPA MAPPING**    | 20           | ✅ Relations, cascading, constraints.                     |
| **SKILL-STATS API INTEGRATION** | 15           | ❌ Correct call, enrichment logic, inclusion in responses. |
| **SECURITY (JWT + ROLES)**      | 10           | ❌ add roles to endpoints, adjust REST Assured test to JWT. |
| **TESTING**                     | 20           | ✅ Coverage of success & failures paths, isolations.      |
| **ERROR HANDLING & VALIDATION** | 10           | ✅ Consistent JSON errors, fields errors.                  |
| **CODE QUALITY & READ-ME**      | 5            | ✅ Structure, clarity and how to run.                     |
---

### Total Progress Summary

| Category | Progress                  |
|-----------|---------------------------|
| Fully Completed | ✅ US1, US2, US3, US4, US7 |
| Partially Completed | 🟡 US5, US6, US8          |
| Not Started | ❌ None                    |
