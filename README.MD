## Getting Started
To get started with this project, follow these steps:

## Start PostgreSQL
Use Docker Compose to bring up the PostgreSQL service:
docker-compose up

# Run the Application
 Unfortunately, the application itself isn't dockerized yet, so you'll need to run it manually.

# Access the API
Once the application is running, you can interact with it using Swagger UI at:

http://localhost:8080/swagger-ui/index.html#/


## Running Tests
All tests are located in the test folder.
You can run them to ensure everything is working as expected.


Versions V3 and V4, which utilize asynchronous processing and do not return updated data, are also under consideration.
## Improvements:
Timestamp Addition: I propose adding a timestamp to messages during updates. 
This will help in determining the recency of data when multiple messages are received.

Split Batch Processing: I prefer options that involve split batched processes (V2/V4). 

Memory Management: A single server may encounter memory limitations when handling 1,000,000 records, although it performs well with 100,000 records.

Custom Message Queue: We considered implementing a custom message queue for sequential processing.
This could be achieved by configuring CORE_COUNT_OF_THREADS = 1 and MAX_COUNT_OF_THREADS = 1, 
along with a new LinkedBlockingQueue<>(). This would create a single-threaded queue that waits for other requests,
potentially allowing the garbage collector to clean up unused memory during this waiting period. However,
this solution is less effective when a response needs to be returned, and the memory savings are minimal.

Normal Batch Splitting: Updating 1,000,000 records is likely not a typical customer request. 
Therefore, this task could be split into several mini-batches at the API Gateway stage and distributed across instances. 
My V2 solution handles 100,000 records efficiently. This approach can also be managed through a message broker, 
allowing us to implement retry mechanisms (note that retries in Spring may fail if the application goes down).