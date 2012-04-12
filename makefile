all: clean TRB
TRB:
	javac TRB.java
clean: 
	rm *.class