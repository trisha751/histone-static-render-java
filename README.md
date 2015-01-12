Histone Static Site Renderer (java version)
===========================================

Histone — powerful and flexible template engine, which can be used for HTML - code generation as well as any other kind of text - documents. Histone implementations exists for the web - browser as well as for the server (Java and PHP), it allows you to use same templates on the server and on the client. Built - in extension mechanism allows you to extend default template engine features, by adding your own methods and properties for the particular project. Templates has clean and simple syntax and can be stored either as source code or as compiled code that can be executed with the maximum performance wherever it's needed.

Histone Template Engine Java Implementation
-------------------------------------------

[Project web site](http://weblab.megafon.ru/histone/)
[Documentation](http://weblab.megafon.ru/histone/documentation/)
[For contributors](http://weblab.megafon.ru/histone/contributors/#Java)

Using Histone Static Renderer from Maven
----------------------------------------
To use histone in your maven project you should add histone repository to your pom.xml or to maven `settings.xml` file
```xml
<repository>
	<id>central</id>
	<snapshots>
		<enabled>false</enabled>
	</snapshots>
	<name>release</name>
	<url>http://weblab.megafon.ru/maven/release-weblab</url>
</repository>
<repository>
	<id>snapshots</id>
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
	<name>snapshot</name>
	<url>http://weblab.megafon.ru/maven/snapshot-weblab</url>
</repository>
```
and then add new maven dependency to your pom.xml
```xml
<dependency>
    <groupId>ru.histone</groupId>
    <artifactId>histone-static-render</artifactId>
    <version>VERSION</version>
</dependency>
```
