# User manual

* [General purpose](#general-purpose)
* [Model](#model)
    * [Main entities](#main-entities)
    * [Statuses](#statuses)
* [API](#api)
    * [Languages](#languages)    


### General purpose
Code compiler service can be used to compile&run program, 
create contest and run tests, that verify the answer 
with the output of the program

### Model
#### Main entities
* `Language` - code language, affects which compiler will be used
* `Compile submission` - record of results of compiling program
* `Run submission` - record of results of launch program
* `Contest` - event, that contains set of tests, 
memory and time restrictions
* `Test` - pair of input to program and expected output
* `Submission` - record of tests run result

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
### API

#### Languages
