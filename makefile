all: imetl.jar imetl_gcj imetl_wrap

imetl_gcj: *.java
	gcj --main=imetl.Tool -o imetl_gcj *.java

imetl_wrap: imetl.jar
	cat stub.sh imetl.jar > imetl_wrap && chmod +x imetl_wrap

imetl.jar: *.class
	(cd ..; jar -cfe imetl/imetl.jar imetl.Tool imetl/*.class)

%.class: *.java
	javac *.java

clean:
	rm *.class
