include Make.rules
JAVAC=javac
JFLAGS=-source 1.4
JAVADOC=javadoc
JAVADOCFLAGS=-source 1.4 -author -use -linksource -private 

default: all


.PHONY: javadocclean clean all javadoc

javadocclean:
	-rm -rf javadoc/*	

clean: objclean distclean


all:
	$(JAVAC) $(JFLAGS) Xsi0.java

javadoc: javadocclean
	$(JAVADOC) $(JAVADOCFLAGS) -d javadoc -subpackages pkgxsi0 Xsi0.java
