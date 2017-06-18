Welcome to PhonePeDemo App.

What are we trying to achieve using this App?

Designing an efficient in-memory queueing system with low latency requirements.
Functional specification:
	- Queue holds JSON messages
	- Allow subscription of consumers to certain messages that match a particular expression
	- Consumers register callbacks that will be invoked whenever there is a new message
	- Queue will have one producer and multiple consumers
	- Consumers might have dependency relationships between them. For ex, if there are three consumers A, B and C. One dependency relationship can be that C cannot consumer a particular message be
fore A and B have consumed it. C -> (A,B) (-> means must process after)
	- Queue is bounded in size and completely held in-memory. Size is configurable.
	- Handle concurrent writes and reads consistently between producer and consumers.
	- Provide retry mechanism to handle failures in message processing.

Assumptions Made :
Hard coded JSON messages are produced to the queue for the ease of implementation.
A Single consumer can consume only one message.
All Dependent Consumers will be subscribed to same message only.
The Consumer dependency is simulated introduced and owned by the application.
We write a custom retry mechanism for a max retry limit instead of leveraging available @Retryable annotations from libraries
We introduce the different types of callbacks which can be registered by the client.

Design patterns used :
Observer (PubSub type)
Factory

How the App works?
PhonePeDemoApp :-
This is the testfile which executes the app.

The purpose of each classes, objects and methods are explained as javadocs or comments.

Our Producer and Consumer do not know about each other.
QueueSystem works like a uber controller on which all the other objects interact and which has knowledge about all the other objects as well.
Producer and consumer interact with QueueSystem with the help of interface objects as we do not want them to directly have access to implementation.
QueueSystem has its own dependency manager and it creates dependencies between various consumers.
