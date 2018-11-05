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
* `Run submission` - record of results of program launch
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
* `COMPILATION_ERROR` - compilation fails with error (in that case `cmpErr` will not be null)
* `RUNNING_TEST` - test with number `testNumber` is running now
* `RUNTIME_ERROR` - an error occurred while running test with number `testNumber`
* `TIME_LIMIT` - means that you program too slow 
* `MEMORY_LIMIT` - means that you program uses too much memory
* `SERVER_ERROR` - some internal error (server side)
* `WRONG_ANSWER` - test with number `testNumber` does not pass
* `OK` - all tests passed  

Compile statuses:
* `OK` 
* `COMPILATION_ERROR` 
* `SERVER_ERROR`

Run statuses:
* `OK`
* `RUNTIME_ERROR`
* `SERVER_ERROR`
* `MEMORY_LIMIT`
* `TIME_LIMIT`

[Up](#user-manual)
#### Response
Response contain id and status of task.   
Also can contain language info (if required), spent time.
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
{
  "statusCode": 103,
  "description": "Requested data has been successfully retrieved",
  "content": [
    {
      "language": "JAVA",
      "name": "Java 11.0.1",
      "notes": "Submission should be in 1 file (1 public class).\nPublic class should have name \"Main\".\nMain class must have main method with signature \"public static void main(String[] args)\" No external dependencies (via maven, gradle, e.t.c) supported (at least now)"
    }
  ]
}
```
[Up](#user-manual)
#### Compile submissions
##### POST - `/compilation/submissions` - create new compile task.   
Body contains 2 mandatory fields: 
* `language` - One from available list
* `sourceCode` - code to compile  


Response example:  
```
{
  "statusCode": 101,
  "description": "New data has been successfully created and persisted",
  "content": {
    "id": "5be0916f156f6a093cfbcff9",
    "language": "JAVA",
    "languageName": "Java 11.0.1",
    "status": "OK",
    "cmpErr": ""
  }
}
```

##### GET - `/compilation/submissions/{id}` - retrieve compile submission result  
Path variable `id `can be obtain from the compile-submission creation response.



Response example:
```
{
  "statusCode": 103,
  "description": "Requested data has been successfully retrieved",
  "content": {
    "id": "5be091c2156f6a093cfbcffa",
    "language": "JAVA",
    "languageName": "Java 11.0.1",
    "status": "COMPILATION_ERROR",
    "cmpErr": "code/Main.java:10: error: not a statement\n                writer.write(br.readLine());weq\n                                            ^\ncode/Main.java:10: error: ';' expected\n                writer.write(br.readLine());weq\n                                               ^\n2 errors\n"
  }
}
```
[Up](#user-manual)
#### Run submissions
##### POST - `/run/submissions` - create new launch program task.   
Launch compiled program with specified restrictions. Compile task should have finished successfully.    
Body contains fields:
* `submissionId` - mandatory, id from compile submission
* `input` - optional, input to program
* `timeLimit` - optional, duration in millis, 
after which program will be killed 
* `memoryLimit` - optional, maximum RAM size in bytes (if exceeds, container will be killed with OOM exception)  
If time limit or memory limit not specified, default value will be applied (256MB and 5s)    

Request example:
Response example:
```
{
  "statusCode": 101,
  "description": "New data has been successfully created and persisted",
  "content": {
    "id": "5be09462156f6a169894f15e",
    "language": "JAVA",
    "languageName": "Java 11.0.1",
    "status": "RUNTIME_ERROR",
    "input": null,
    "output": "",
    "stderr": "Exception in thread \"main\" java.io.FileNotFoundException: input.txt (No such file or directory)\n\tat java.base/java.io.FileInputStream.open0(Native Method)\n\tat java.base/java.io.FileInputStream.open(FileInputStream.java:219)\n\tat java.base/java.io.FileInputStream.<init>(FileInputStream.java:157)\n\tat java.base/java.io.FileInputStream.<init>(FileInputStream.java:112)\n\tat java.base/java.io.FileReader.<init>(FileReader.java:60)\n\tat Main.main(Main.java:8)\n",
    "time": 578
  }
}
```

##### GET - `/run/submissions/{id}` - retrieve result of launch program.  
Path variable `id `can be obtain from the run-submission creation response.


Response example:
```
{
  "statusCode": 103,
  "description": "Requested data has been successfully retrieved",
  "content": {
    "id": "5be09481156f6a169894f15f",
    "language": "JAVA",
    "languageName": "Java 11.0.1",
    "status": "OK",
    "input": "123",
    "output": "123",
    "stderr": "",
    "time": 402
  }
}
```
[Up](#user-manual)
#### Contests
##### POST - `/contests` - create new contest
Body contains next fields:
* `name` - mandatory field
* `tests` - mandatory field, list of test objects 
(see [Model](#model))
* `timeLimit` - optional, time duration in millis, after which program will be killed
(used in test launch)
* `memoryLimit` - optional, maximum RAM size in bytes  
If memory or time limit is not specified, 
default value will be applied (256MB and 2s)


Response example:
``` 
{
  "statusCode": 101,
  "description": "New data has been successfully created and persisted",
  "content": {
    "name": "new test",
    "tests": [
      {
        "input": "123",
        "output": null
      },
      {
        "input": "123",
        "output": null
      },
      {
        "input": "123",
        "output": null
      },
      {
        "input": "123",
        "output": null
      },
      {
        "input": "123",
        "output": null
      },
      {
        "input": "123",
        "output": null
      },
      {
        "input": "123",
        "output": null
      }
    ],
    "timeLimit": 2000,
    "memoryLimit": 268435456
  }
}
```

#### Submissions
##### POST - `/submissions/` - create new submission (async operation)
Body contains following mandatory fields:
* `contestId` - id returned from creation contest request
* `submission` - object, that contains:
    * `language` - one from available list
    * `sourceCode` - code to be compiled&launched


Response example:
```
{
  "statusCode": 101,
  "description": "New data has been successfully created and persisted",
  "content": {
    "id": "5be09879156f6a2e5cd0174c",
    "status": "WAITING_IN_QUEUE",
    "language": null,
    "languageName": null,
    "testNumber": null,
    "cmpErr": null,
    "time": null,
    "sourceCode": null
  }
}
```

##### GET - `/submissions/{id}` - retrieve status of submission.  
Accepts two arbitrary query parameters:
* `withLang` - include language info in response or not
* `withSource` - include code in response or not


Response example:
```
{
  "statusCode": 103,
  "description": "Requested data has been successfully retrieved",
  "content": {
    "id": "5be09879156f6a2e5cd0174c",
    "status": "OK",
    "language": "JAVA",
    "languageName": "Java 11.0.1",
    "testNumber": 7,
    "cmpErr": null,
    "time": [
      677,
      709,
      590,
      702,
      649,
      552,
      692
    ],
    "sourceCode": "import java.io.BufferedReader;\nimport java.io.FileReader;\nimport java.io.PrintWriter;\nimport java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) throws Exception {\n        try (BufferedReader br = new BufferedReader(new FileReader(\"input.txt\"))) {\n            try (PrintWriter writer = new PrintWriter(\"output.txt\")) {\n                writer.write(br.readLine());\n            }\n        }\n    }\n}"
  }
}
```
[Up](#user-manual)
