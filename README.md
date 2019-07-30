# jep-feed

An application which downloads the JDK Enhancement Proposals index, JEP 0, and turns it into an RSS feed document.

## Installation
### .jar

Download .jar files from https://github.com/lucidmachine/jep-feed and move them to your install directory.

### Native Binary 

Download executable binaries from https://github.com/lucidmachine/jep-feed and move them to a binary directory.

## Running
### .jar

```
> java -jar $INSTALL_DIR/jep-feed-1.0.0-standalone.jar
```

### Native Binary

```
> $INSTALL_DIR/jep-feed
```

## Building
### .jar
In order to build an executable .jar file issue the `uberjar` command to the build tool, Leiningen.

```
> lein uberjar
```

### Native Binary
In order to compile the program to a binary you will need to [install GraalVM and its native-image component](https://github.com/BrunoBonacci/graalvm-clojure/blob/master/doc/clojure-graalvm-native-binary.md#step1---download-and-install-graalvm). Then issue the `native` command to Leiningen.

```
> lein native
```
