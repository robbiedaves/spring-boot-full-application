# Spring Boot Full Application

This is taken from the tutorial:
https://springframework.guru/spring-boot-web-application-part-1-spring-initializr/

This application has the following:
* Spring-boot
* Web MVC
* Thymeleaf
* Security
* JPA
* H2


### Part 1
Create the application using the spring-boot initilizer with the components listed above.
I also added the bootstrap and jquery webjars by adding the following into the pom:

```xml
<!-- Webjars -->
<dependencies>
    <dependency>
	    <groupId>org.webjars</groupId>
	    <artifactId>bootstrap</artifactId>
	    <version>3.3.5</version>
    </dependency>

    <dependency>
	    <groupId>org.webjars</groupId>
	    <artifactId>jquery</artifactId>
	    <version>3.3.1</version>
    </dependency>
</dependencies>
```

Use this Maven command to list the dependencies
```
mvn dependency:tree
``` 


### Part 2 - Thymeleaf and Spring MVC
Now lets get Thymeleaf and Spring MVC working

By default, Spring Boot configures the Thymeleaf template engine to read template files from /resources/templates

Add a new index.html file in the templates folder
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">

    <title>Spring Framework Guru</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<h1>Hello</h1>

<h2>robxx - spring-boot-full-application</h2>
</body>
</html>
```

Then add a new IndexController.java in a new package called controllers
```java
package com.robxx.springbootapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/")
    String index() {
        return "index";
    }
}
```

Now if you run the application from the main method in SpringBootFullApplication class and goto http://localhost:8080/ you will get a sign on dialog box.
This is because we have included the security module in the pom.

To get around this we can add a simple security class, but we will extend it later.

To accommodate the Spring Security configuration, I created a new package and added the SecurityConfiguration  class. By annotating the class with the @Configuration  annotation, I’m telling Spring this is a configuration class. When doing Java configuration for Spring Security, you need to extend the WebSecurityConfigurerAdapater  class and override the configure method.
```java
package com.robxx.springbootapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll();
    }
}
```

Now goto http://localhost:8080/ and you should see the page display correctly :)

#### Static Resources with Spring Boot
Spring Boot will automatically serve static resources from the path /resources/static. By a generally accepted convention, you will typically put CSS files in /resources/static/css, Javascript files in /resources/static/js, and images in resources/static/images.

Add 3 new folders in the resources / static folder called css, js and images
Add some empty files to the folders:
```css
/* main.css file */
```

```JavaScript
// main.js file
```

And add a an icon into the images folder.

Note: I've done this so git won't delete the folders.

Now to add bootstrap and jquery to the index.html

Add the following to the head section of index.html
```html
    <script th:src="@{/webjars/jquery/3.3.1/jquery.min.js}"></script>
    <script th:src="@{/webjars/bootstrap/3.3.5/js/bootstrap.min.js}"></script>

    <link rel="stylesheet" th:href="@{/webjars/bootstrap/3.3.5/css/bootstrap.min.css}" />
    <link href="../static/css/main.css" th:href="@{css/main.css}" rel="stylesheet" media="screen"/>
```

Now re-visit the web page and you should see it styled correctly.

### Part 3 - Persistence layer with Spring Boot, H2 and Spring Data JPA.
