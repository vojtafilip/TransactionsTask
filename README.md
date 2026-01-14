
# Transactions Task

This is a Kotlin Multiplatform project.
See https://gist.github.com/maio/7ff652b8f27ae0513ad609107ce5c3fe for requirements.


### Notes

* project uses in-memory database H2. Possible to configure another database in application.conf
* no authentication
* no https – may be configured in application.conf
* size of the CSV file is limited to 1000000 bytes, may be configured in application.conf
* **server** part contains the complete solution for given requirements.
* **webFrontend** is just a demo in Kotlin Compose for Web, which calls the server.
* code contains some TODOs, for future improvements.



### API description

#### POST /transactions - upload CSV file with transactions.

Responses:
* 200 OK if file is valid and uploaded successfully. With JSON response:
```
{
  "insertedCount":123,
  "failedToInsert":[10000001,10000002,10000003,10000004,10000005,10000006,10000007]
}
```
**insertedCount** counts how many transactions were inserted successfully; **failedToInsert** contains references of not stored transactions (because of already stored).

* 413 Payload Too Large if file is too large.
* 415 Unsupported Media Type if file is not CSV.
* 400 Bad Request if file is invalid.

#### GET /transactions - get all stored transactions as HTML or JSON.

Parameters:
* format – html (default) or json
* limit – how many transactions to return (default 100)
* cursor – cursor for pagination (default is empty)

Response:
* HTML sorted table with transactions. Contains the pagination link if needed.
* or JSON of transactions – see TransactionsGetApiSpecification test for details.


### Project modules

* [/modules/server](./modules/server/src/main/kotlin) is for the Ktor server application.

* [/modules/webFrontend](./modules/webFrontend/src) Compose web app, frontend for the server.


### Build and Run

#### server (Ktor application):
  ```shell
  ./gradlew :modules:server:run
  ```
* Now you can access the server at http://localhost:5000/
* Use POST http://localhost:5000/transactions to upload CSV file.
* Use GET http://localhost:5000/transactions to see transactions as HTML.
* Use GET http://localhost:5000/transactions?format=json to see transactions as JSON.

####  web frontend (Compose app):
  ```shell
  ./gradlew :modules:webFrontend:wasmJsBrowserDevelopmentRun
  ```

* Now you can access the frontend at http://localhost:8080/

####  server tests:
  ```shell
  ./gradlew :modules:server:test
  ```

#### web frontend tests:
Set CHROME_BIN env variable (e.g. CHROME_BIN=/snap/bin/chromium)
  ```shell
  ./gradlew :modules:webFrontend:cleanWasmJsBrowserTest :modules:webFrontend:wasmJsBrowserTest
  ```

### Run manual test

1. Start server
2. Run script uploadTestFile.sh in [/manual_tests](./manual_tests)
3. Check transactions in browser: http://localhost:5000/transactions
4. Try with your CSV files...
