# Batch collecting java 8 Streams

Some code I've written for batch collecting Java-8 streams:


```java
someHumongousDataStream
.map(element -> doSomeBlackVoodooMagic(element))
.collect(BatchCollector.collect(
	 List<Element> elements -> sendToSomeDestination(elements),
	 1000));
```

  Feel free to do with it as you like :)
