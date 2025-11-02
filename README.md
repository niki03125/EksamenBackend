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