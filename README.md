# Cash Flow Visualization Tool

## Project Overview

The **Cash Flow Visualization Tool** is a web application that allows users to visualize and analyze cash flows between entities such as companies and individuals. The frontend is built with Angular, and the backend uses Spring Boot with PostgreSQL for data storage.

Application production can be accessed from here: https://cash-flow-visualization-tool.marielle.ee/ 

## Project Structure

- **Frontend**: Angular-based visualization tool for cash flow.
- **Backend**: Spring Boot RESTful API for handling data and business logic.
- **Database**: PostgreSQL is used for storing transactions and entities data.

## Dependencies
- Node.js 20.18.0
- Angular CLI 18.2.0
- Java 17
- Maven 3.8.4
- PostgreSQL 15.4:
     - schema: app_cashflow
- Docker (for developement)
- Spring Boot 3.3.3

## Installation and Setup

### Postgres database
1. Install PostgreSQL
2. Create a volume and network for the database:
```bash
docker volume create postgres-data
docker network create -d overlay --attachable postgres-net
```
(In windows run one by one)

3. Create postgres service:

In Mac:
```bash
docker service create \
  --name postgres \
  --network postgres-net \
  --mount type=volume,src=postgres-data,dst=/var/lib/postgresql/data \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=password \
  postgres:15.4
```

In Windows:
```bash
docker service create `
  --name postgres `
  --network postgres-net `
  --mount type=volume,src=postgres-data,dst=/var/lib/postgresql/data `
  -p 5432:5432 `
  -e POSTGRES_PASSWORD=password `
  postgres:15.4
```

4. Set up schema:

In mac:
```bash
docker run -it --rm \
  --network postgres-net \
  postgres:15.4 \
  psql -h postgres -U postgres -c "CREATE SCHEMA IF NOT EXISTS app_cashflow";
```

In windows:
```bash
docker run -it --rm ^
  --network postgres-net ^
  postgres:15.4 ^
  psql -h postgres -U postgres -c "CREATE SCHEMA IF NOT EXISTS app_cashflow";
```

### Backend

1. Clone the repository:
```bash
git clone https://gitlab.com/your-repo/cash-flow-visualization-tool.git
```

2. Navigate to the backend directory:
```bash
cd backend
```

3. Build the project using Maven:
```bash
mvn clean install
```

4. Start the Spring Boot server:
```bash
mvn spring-boot:run
```

5. Ensure PostgreSQL is running and create a database for the application.

6. Update `application.yml` with your PostgreSQL credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=password
 ```

### Frontend

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
npm start
```

4. The frontend application will be available at `http://localhost:4200/`.

## Setting up with Docker compose

1. Run the following command to start the application:
```bash
docker-compose up
```
It will be available at `http://localhost:4200/`.

## Building for Production

### Frontend

1. Build the production version:
```bash
npm run build
```

2. Serve the built files using an SSR server:
```bash
npm run serve:ssr:cash-flow-visualization-frontend
```

### Backend

1. Create a production build:
```bash
mvn clean package
```

2. Run the production jar:
```bash
java -jar target/cash-flow-visualization-tool-0.0.1-SNAPSHOT.jar
```

## Testing

### Backend

Run tests with Maven:
```bash
mvn test
```

