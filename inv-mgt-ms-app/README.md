# Inventory Management Microservice

This project implements a Spring Boot microservice for managing inventory items (Products and Widgets) with AWS SQS integration for asynchronous processing.

## Features

- RESTful API for CRUD operations on Products and Widgets
- AWS SQS integration for asynchronous processing
- In-memory H2 database (using PostgreSQL dialect)
- OpenAPI/Swagger documentation
- Unified inventory view combining Products and Widgets
- Comprehensive unit tests
- Docker containerization

## Project Structure

```
inv-mgt-ms-app/
├── src/
│   ├── main/
│   │   ├── java/com/company/inventory/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── exception/        # Exception handlers
│   │   │   ├── model/            # Domain models and DTOs
│   │   │   ├── repository/       # JPA repositories
│   │   │   ├── service/          # Business logic and services
│   │   │   └── InventoryApplication.java # Main application class
│   │   └── resources/
│   │       ├── application.properties   # Application configuration
│   │       ├── schema.sql               # Database schema
│   │       └── data.sql                 # Sample data
│   └── test/                      # Unit and integration tests
└── Dockerfile                     # Docker configuration
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven (or use the included Maven wrapper)
- Docker (for containerization)

### Running Locally

1. Clone the repository
2. Build the project:
   ```
   ./mvnw clean package
   ```
3. Run the application:
   ```
   ./mvnw spring-boot:run
   ```
4. Access the application at http://localhost:8080
5. Access the Swagger UI at http://localhost:8080/swagger-ui.html

### Running with Docker

1. Build the Docker image:
   ```
   docker build -t inventory-service .
   ```
2. Run the container:
   ```
   docker run -p 8080:8080 inventory-service
   ```
3. Access the application at http://localhost:8080

## API Documentation

The API is documented using OpenAPI (Swagger). Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

### API Endpoints

#### Products

- `GET /api/products`: List all products
- `GET /api/products/{id}`: Get a product by ID
- `GET /api/products/category/{category}`: Get products by category
- `GET /api/products/upc/{upcCode}`: Get a product by UPC code
- `GET /api/products/inStock/{inStock}`: Get products by stock status
- `POST /api/products`: Create a new product
- `PUT /api/products/{id}`: Update a product
- `DELETE /api/products/{id}`: Delete a product

#### Widgets

- `GET /api/widgets`: List all widgets
- `GET /api/widgets/{id}`: Get a widget by ID
- `GET /api/widgets/category/{category}`: Get widgets by category
- `GET /api/widgets/upc/{upcCode}`: Get a widget by UPC code
- `GET /api/widgets/available/{isAvailable}`: Get widgets by availability status
- `POST /api/widgets`: Create a new widget
- `PUT /api/widgets/{id}`: Update a widget
- `DELETE /api/widgets/{id}`: Delete a widget

#### Inventory

- `GET /api/inventory`: Get all inventory items (products and widgets)
- `GET /api/inventory/type/{itemType}`: Get inventory by type (Product or Widget)
- `GET /api/inventory/category/{category}`: Get inventory by category
- `GET /api/inventory/available/{available}`: Get inventory by availability
- `GET /api/inventory/maxPrice/{maxPrice}`: Get inventory below a price threshold

## AWS SQS Integration

The application integrates with AWS SQS for processing product and widget operations asynchronously. The configuration uses the following queues:

- Product Queue: `guhesanv-products`
- Widget Queue: `guhesanv-widgets`

For each create, update, or delete operation via the REST API, a message is sent to the corresponding queue. Consumer services poll these queues and update the database accordingly.

## Running Tests

Run the unit tests with:

```
./mvnw test
```

## Configuration

The application can be configured using the following environment variables:

- `AWS_REGION`: AWS region (default: us-east-1)
- `AWS_ACCESS_KEY`: AWS access key
- `AWS_SECRET_KEY`: AWS secret key
- `PRODUCT_QUEUE_NAME`: Name of the SQS queue for products
- `WIDGET_QUEUE_NAME`: Name of the SQS queue for widgets

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Data persistence
- [H2 Database](https://www.h2database.com/) - In-memory database
- [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/) - AWS integration
- [SpringDoc OpenAPI](https://springdoc.org/) - API documentation
- [Docker](https://www.docker.com/) - Containerization