## Trip Planning API

This is a simple REST API for managing trips and guides.
It is implemented using Javalin and JPA/Hibernate.
The API includes JWT-based authentication and integrates with an external Packing List API.

It is prepared for deployment following the tutorials here
.

In case you need to set up a Droplet at Digital Ocean,
you can use this link for tutorials: Digital Ocean tutorials

### How to run

1. Create a PostgreSQL database in your local instance called tripdb

2. Configure environment variables (for example in a .env file):
      DB_URL=jdbc:postgresql://localhost:5432/tripdb
      DB_USER=<your_db_user>
      DB_PASSWORD=<your_db_password>
      JWT_SECRET=<your_secret>
      PACKING_BASE_URL=https://packingapi.cphbusinessapps.dk/packinglist

3. Run the main method in the config.Populate class to populate the database with sample data

4. Run the main method in the Main class to start the server on port 7070

5. See all routes in your browser at http://localhost:7070/routes

6. Try requesting http://localhost:7070/trips to see the list of trips

7. Use the dev.http file to test the available routes (GET, POST, PUT, DELETE)

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

| User Story | Points (Max) | Implementation & Progress                                                                                      |
|-------------|--------------|----------------------------------------------------------------------------------------------------------------|
| **US1: Database Configuration** | 10 | ✅ Implemented HibernateConfig, Trip & Guide entities, Populator with sample data. Fully functional and tested. |
| **US2: DAOs and DTOs** | 10 | ✅ DAO layer complete using generics and DTO mapping. CRUD works with database.                                 |
| **US3: REST API Endpoints** | 15 | ✅ All routes implemented (GET, POST, PUT, DELETE, addGuideToTrip). Tested manually via `dev.http`.             |
| **US4: Filter by Category** | 10 | ✅ Implemented category filtering using Streams and JPA query. Returns trips by category.                       |
| **US5: Total Trip Value per Guide** | 10 |✅ Calculating the sum of each, and show it in JSON.                                                              |
| **US6: External Packing API Integration** | 15 | ✅ Working integration with external Packing API. ZonedDateTime handled via `JavaTimeModule`.                   |
| **US7: REST Testing** | 20 | 🟡 Started – tests for basic CRUD working, but JWT-protected routes still need token handling.                 |
| **US8: Security (JWT + Roles)** | 10 | 🟡 Login endpoint + JWT roles implemented. Works manually, tests pending.                                      |

---

### Total Progress Summary

| Category | Progress                       |
|-----------|--------------------------------|
| Fully Completed | ✅ US1, US2, US3, US4, US5, US6 |
| Partially Completed | 🟡 US7, US8                    |
| Not Started | ❌ None                         |
