# Implement the following:

<aws_access_key_id> = "*******"
<secret_access_key> = "*******"
<product_sqs_queue_name> = "guhesanv-products" 
<widget_sqs_queue_name> = "guhesanv-widgets" 

Create a new Java Spring microservice that will allows users to interact with and ingest data. There will be two components to the service: a REST API to enact CRUD actions on specific data objects, and an SQS queue to ingest those same objects and store them in an in-memory db.

The database schema is provided under 'data/init_database.sql' file. The data objects contains Product and Widget and an unifying view that combines Products and Widgets under one view called "inventory_view".

- Create Spring Boot service to deploy API:
  - Create and deploy the microservice as a Spring Boot application so it can run as a standalone service.
- Create REST API using OpenAPI: 
  - All REST API endpoints should be exposed via OpenAPI Swagger for documenatation and testing.
- Store objects in in-memory H2 database operating using the H2 'postgresql' dialect.
  - Create an in-memory database as part of the microservice to hold data. See attached SQL file: 'data/init_database.sql'.
  - Define data structures required to store the data.
- Create CRUD endpoints for 'Product':
  - Define API endpoints for product that can create, delete, update, and read products. 
  - Only the read operation will connect directly to the database.
- Create CRUD endpoints for 'Widget':
  - Define API endpoints for widget that can create, delete, update, and read widgets. 
  - Only the read operation will connect directly to the database.
- During startup, ensure that the SQS 'Product' queue with name <product_sqs_queue_name> exists.
  - If it does not exist, create it.
- During startup, ensure that the SQS 'Widget' queue with name <widget_sqs_queue_name> exists.
  - If it does not exist, create it.
- Implement 'Product' publisher (SQS with queue name <product_sqs_queue_name>):
  - In the create, delete, and update endpoints, send the new/modified/deleted product to the SQS queue.
- Implement 'Widget' publisher (SQS with queue name <widget_sqs_queue_name>):
  - In the create, delete, and update endpoints, send the new/modified/deleted widget to the SQS queue.
- Implement 'Product' consumer (SQS with queue name <product_sqs_queue_name>):
  - Implement a polling strategy to pick new/modified/deleted products off the product queue and update the in-memory database accordingly.
- Implement 'Widget' consumer (SQS with queue name <widget_sqs_queue_name>):
  - Implement a polling strategy to pick new/modified/deleted widgets off the widget queue and update the in-memory database accordingly.
- All code should have unit tests with instructions on how to run the tests.
- Containerize the microservice:
  - Create a container to run the deployed microservice and document how to run the container.
- Document the codebase:
  - Develop documentation for the code base (internal code and deployment instructions).