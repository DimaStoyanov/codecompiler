# User manual

* [General purpose](#general-purpose)
* [Workflow](#workflow)
* [Model](#model)
    * [Main entities](#main-entities)
    * [Statuses](#statuses)
    * [Response](#response)
* [API](#api)
    * [Languages](#languages)    
    * [Compile submissions](#compile-submissions)
    * [Run submissions](#run-submissions)
    * [Contests](#contests)
    * [Submissions](#submissions)


### General purpose
Code compiler service can be used to compile&run program, 
create contest and run tests, that verify the answer 
with the output of the program

### Workflow
For compile and run code workflow contains from 2 action:
1. Create compile task
2. Create run task
Note, that run task can contain input. This input will be written into `input.txt` file.
Your program can read this input and write output to `stdout` or `output.txt` file.

For run tests workflow is following:
1. Create contest
2. Create submission (one contest may have multiple submissions)
3. Check status of submission
Program also can read input from `input.txt` file and can write output to `stdout` or `output.txt`.


### Model
#### Main entities
* `Language` - code language, affects which compiler will be used
* `Compile submission` - record of results of compiling program
* `Run submission` - record of results of launch program
* `Contest` - event, that contains set of tests, 
memory and time restrictions
* `Test` - pair of input to program and expected output
* `Submission` - record of tests run result (bind to specific contest)  

[Up](#user-manual)

#### Statuses
Submission compile&run asynchronously, 
so you can check it status while it still running. Status can be one of following list: 
* `WAITING_IN_QUEUE` - means that program not started to compile yet
* `COMPILING` - process of compiling program is in progress
* `COMPILATION_ERROR` - compilation fails with error (in that case cmpErr will not be null)
* `RUNNING_TEST` - test with number `testNumber` is running now
* `RUNTIME_ERROR` - an error occurred while running test with number `testNumber`
* `TIME_LIMIT` - means that you program too slow 
* `MEMORY_LIMIT` - means that you program uses too much memory
* `SERVER_ERROR` - some internal error (server side)
* `WRONG_ANSWER` - test with number `testNumber` does not pass
* `OK` - all tests passed  
Compile and Run result contains the same statuses, excluded some async statuses (waiting, running, e.t.c.)

[Up](#user-manual)
#### Response
Response contain id and status of task.   
Also can contain language info (if required), spent time,
used memory (not always available).  
Tasks, which includes compilation contain compilation error, if present.
Tasks, which includes launch may contain input, output and stderr

[Up](#user-manual)
### API

#### Languages
##### Get - `/languages/`  - retrieve list of available languages with some info.   

Request example:  
```
curl -X GET "http://localhost:8080/languages/" -H "accept: */*"
```
Response example:  
```
[
  {
    "language": "JAVA",
    "languageName": "Java 8u171",
    "sourceFileExt": "java",
    "compiledFileExt": "class",
    "notes": "Submission should be in 1 file (1 public class).\nPublic class should have name \"Main\".\nMain class must have main method with signature \"public static void main(String[] args)\" No external dependencies (via maven, gradle, e.t.c) supported (at least now)"
  }
]
```
[Up](#user-manual)
#### Compile submissions
##### POST - `/compile-submissions/` - create new compile task.   
Body contains 2 mandatory fields: 
* `language` - One from available list
* `sourceCode` - code to compile  

Request example:  
```
    curl -X POST "http://localhost:8080/compile-submissions/" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"language\": \"JAVA\", \"sourceCode\": \"public static void main(){}\"}"
```
Response example:  
```
{
  "id": "5bd0dc7164c1cf2a708e70c2",
  "language": "JAVA",
  "languageName": "Java 8u171",
  "status": "COMPILATION_ERROR",
  "cmpErr": "code/Main.java:1: error: class, interface, or enum expected\npublic static void main(){}\n              ^\n1 error\n"
}
```

##### GET - `/compile-submissions/{id}` - retrieve compile submission result  
Path variable `id `can be obtain from the compile-submission creation response.

Request example:
```
curl -X GET "http://localhost:8080/compile-submissions/5bd0dc7164c1cf2a708e70c2" -H "accept: */*"
```

Response example:
```
{
  "id": "5bd0dc7164c1cf2a708e70c2",
  "language": "JAVA",
  "languageName": "Java 8u171",
  "status": "COMPILATION_ERROR",
  "cmpErr": "code/Main.java:1: error: class, interface, or enum expected\npublic static void main(){}\n              ^\n1 error\n"
}
```
[Up](#user-manual)
#### Run submissions
##### POST - `/run-submissions/` - create new launch program task.   
Launch compiled program with specified restrictions. Compile task should have finished successfully.    
Body contains fields:
* `submissionId` - mandatory, id from compile submission
* `input` - optional, input to program
* `executionTimeLimit` - optional, duration in millis, 
after which program will be killed 
* `memoryLimit` - optional, maximum RAM size in bytes (if exceeds, container will be killed with OOM exception)  
If time limit or memory limit not specified, default value will be applied (256MB and 5s)    

Request example:
```
curl -X POST "http://localhost:8080/run-submissions/" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"submissionId\": \"5bd0e03f64c1cf2a708e70c4\"}"
```
Response example:
```
{
  "id": "5bd0e05764c1cf2a708e70c5",
  "language": "JAVA",
  "languageName": "Java 8u171",
  "status": "RUNTIME_ERROR",
  "input": null,
  "output": "",
  "stderr": "Error: Main method not found in class Main, please define the main method as:\n   public static void main(String[] args)\nor a JavaFX application class must extend javafx.application.Application\n",
  "memory": null,
  "time": 1007
}
```

##### GET - `/run-submissions/{id}` - retrieve result of launch program.  
Path variable `id `can be obtain from the run-submission creation response.

Request example:
```
curl -X GET "http://localhost:8080/run-submissions/5bd0e14164c1cf2a708e70c7" -H "accept: */*"
```
Response example:
```
{
  "id": "5bd0e14164c1cf2a708e70c7",
  "language": "JAVA",
  "languageName": "Java 8u171",
  "status": "OK",
  "input": null,
  "output": "Hello World!\n",
  "stderr": "",
  "memory": null,
  "time": 1099
}
```
[Up](#user-manual)
#### Contests
##### POST - `/contests/` - create new contest
Body contains next fields:
* `name` - mandatory field
* `tests` - mandatory field, list of test objects 
(see [Model](#model))
* `timeLimit` - optional, time duration in millis, after which program will be killed
(used in test launch)
* `memoryLimit` - optional, maximum RAM size in bytes  
If memory or time limit is not specified, 
default value will be applied (256MB and 2s)

Request example:
```
curl -X POST "http://localhost:8080/contests/" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Example\", \"tests\": [ { \"expectedOutput\": \"Hello World!\", \"input\": \"string\" } ]}"
```
Response example:
``` 
{
  "id": "5bd0e28964c1cf2a708e70c8"
}
```

#### Submissions
##### POST - `/submissions/` - create new submission (async operation)
Body contains following mandatory fields:
* `contestId` - id returned from creation contest request
* `submission` - object, that contains:
    * `language` - one from available list
    * `sourceCode` - code to be compiled&launched

Request example:
```
curl -X POST "http://localhost:8080/submissions/" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"contestId\": \"5bd0e28964c1cf2a708e70c8\", \"submission\": { \"language\": \"JAVA\", \"sourceCode\": \"print(123)\" }}"
```
Response example:
```
{
  "id": "5bd0e48b64c1cf2a708e70c9"
}
```

##### GET - `/submissions/{id}` - retrieve status of submission.  
Accepts two arbitrary parameters:
* `withLang` - include language info in response or not
* `withSource` - include code in response or not

Request example:
```
curl -X GET "http://localhost:8080/submissions/5bd0e48b64c1cf2a708e70c9?withLang=true&withSource=false" -H "accept: */*"
```
Response example:
```
{
  "status": "WRONG_ANSWER",
  "language": "JAVA",
  "languageName": "Java 8u171",
  "testNumber": 3,
  "cmpErr": null,
  "memory": [
    null,
    null,
    null
  ],
  "time": [
    1266,
    1246,
    1094
  ],
  "sourceCode": null
}
```
[Up](#user-manual)
