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

We are going to use H2 database, and H2 comes with a nice console

However, as we are using spring security, we need to allow the console access.

First, lets configure the h2 console.

Normally, you’d configure the H2 database in the web.xml file as a servlet, but Spring Boot is going to use an embedded instance of Tomcat, so we don’t have access to the web.xml file. Spring Boot does provide us a mechanism to use for declaring servlets via a Spring Boot ServletRegistrationBean.

Add this to the configuration folder:
```java
package com.robxx.springbootapp.configuration;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {

    @Bean
    ServletRegistrationBean h2servletRegistrion() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/console/*");
        return registrationBean;
    }
}
```
If you are not using Spring Security with the H2 database console, this is all you need to do. When you run your Spring Boot application, you’ll now be able to access the H2 database console at http://localhost:8080/console

CAUTION: This is not a Spring Security Configuration that you would want to use for a production website. These settings are only to support development of a Spring Boot web application and enable access to the H2 database console. I cannot think of an example where you’d actually want the H2 database console exposed on a production database.

Now change the SecurityConfiguration class to include the console route
```java
package com.robxx.springbootapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll().and()
                .authorizeRequests().antMatchers("/console/**").permitAll();

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
}
```

Now goto http://localhost:8080/console

The settings are:
- Driver Class =    org.h2.Driver
- JDBC URL  =  jdbc:h2:mem:testdb
- User Name	=    sa
- Password =   blank

##### JPA Entities

Add a new package called domain and add a JPA entity java class called Product.java
```java
package com.robxx.springbootapp.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Version
    private Integer version;

    private String productId;
    private String description;
    private String imageUrl;
    private BigDecimal price;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
```

Now add a repository

Add a new package called repositories 

Now add a new class called ProductRepository.java
```java
package com.robxx.springbootapp.repositories;

import com.robxx.springbootapp.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
``` 

##### Spring Data JPA Repository Test Configuration
Add the following class to configure the JPA repository

Note: This is in the configuration package in the test folder only.
```java
package com.robxx.springbootapp.configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.robxx.springbootapp.domain"})
@EnableJpaRepositories(basePackages = {"com.robxx.springbootapp.repositories"})
@EnableTransactionManagement
public class RepositoryConfiguration {

}
```

While this is an empty Java class file, each of the annotations is very important.

* @Configuration  tells the Spring Framework this is a Java configuration class.
* @EnableAutoConfiguration tells Spring Boot to do its auto configuration magic. This is what has Spring Boot automatically create the Spring Beans with sensible defaults for our tests.
* @EntityScan specifies the packages to look for JPA Entities.
* @EnableJpaRepositories enables the auto configuration of Spring Data JPA.
* @EnableTransactionManagement Enables Spring’s annotation driven transaction management

To perform the tests, add a new class in the test -> repositories package called ProductRepositoryTest:
```java
package com.robxx.springbootapp.repositories;

import com.robxx.springbootapp.domain.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductRepositoryTest {

    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Test
    public void testSaveProduct(){
        //setup product
        Product product = new Product();
        product.setDescription("Spring Framework Guru Shirt");
        product.setPrice(new BigDecimal("18.95"));
        product.setProductId("1234");

        //save product, verify has ID value after save
        assertNull(product.getId()); //null before save
        productRepository.save(product);
        assertNotNull(product.getId()); //not null after save

        //fetch from DB
        Product fetchedProduct = productRepository.findOne(product.getId());

        //should not be null
        assertNotNull(fetchedProduct);

        //should equal
        assertEquals(product.getId(), fetchedProduct.getId());
        assertEquals(product.getDescription(), fetchedProduct.getDescription());

        //update description and save
        fetchedProduct.setDescription("New Description");
        productRepository.save(fetchedProduct);

        //get from DB, should be updated
        Product fetchedUpdatedProduct = productRepository.findOne(fetchedProduct.getId());
        assertEquals(fetchedProduct.getDescription(), fetchedUpdatedProduct.getDescription());

        //verify count of products in DB
        long productCount = productRepository.count();
        assertEquals(productCount, 3);

        //get all products, list should only have three
        Iterable<Product> products = productRepository.findAll();

        int count = 0;

        for(Product p : products){
            count++;
        }

        assertEquals(count, 3);
    }
}
```

Now we can add a product loader. This is because we are using an in memory database.

```java
package com.robxx.springbootapp.bootstrap;

import com.robxx.springbootapp.domain.Product;
import com.robxx.springbootapp.repositories.ProductRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductLoader implements ApplicationListener<ContextRefreshedEvent> {

    private ProductRepository productRepository;

    private Logger log = Logger.getLogger(ProductLoader.class);

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        Product shirt = new Product();
        shirt.setDescription("Spring Framework Guru Shirt");
        shirt.setPrice(new BigDecimal("18.95"));
        shirt.setImageUrl("https://springframework.guru/wp-content/uploads/2015/04/spring_framework_guru_shirt-rf412049699c14ba5b68bb1c09182bfa2_8nax2_512.jpg");
        shirt.setProductId("235268845711068308");
        productRepository.save(shirt);

        log.info("Saved Shirt - id: " + shirt.getId());

        Product mug = new Product();
        mug.setDescription("Spring Framework Guru Mug");
        mug.setImageUrl("https://springframework.guru/wp-content/uploads/2015/04/spring_framework_guru_coffee_mug-r11e7694903c348e1a667dfd2f1474d95_x7j54_8byvr_512.jpg");
        mug.setProductId("168639393495335947");
        productRepository.save(mug);

        log.info("Saved Mug - id:" + mug.getId());
    }
}
```

Now we can run the tests and start the application and check the h2 database and see 2 products written by the product loader.

### Part 4 - Spring MVC

MVC Overview
* Model - Model refers to a data model, or some type of data structure. For example a web page showing a list of products, the ‘model’ would contain a list of product data.
* View - The view layer, in Java frequently a JSP. This will take data from the Model and render the view.
* Controller - Can be described like a traffic cop. It will take an incoming request, decide what to do with it, then direct the resulting action. For example, the controller could get a view product request. It will direct a service to get the product data, then direct to the product view and provide the ‘model’ (product data) to the view.

First create a new package called services and add a new interface called ProductService.java
```java
package com.robxx.springbootapp.services;

import com.robxx.springbootapp.domain.Product;

public interface ProductService {
    Iterable<Product> listAllProducts();

    Product getProductById(Integer id);

    Product saveProduct(Product product);

    void deleteProduct(Integer id);
}
```

Next create a new class called ProductController in the controllers package and add the following:
```java
package com.robxx.springbootapp.controllers;

import com.robxx.springbootapp.domain.Product;
import com.robxx.springbootapp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProductController {

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping("product/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        return "productform";
    }

    @RequestMapping(value="product", method = RequestMethod.POST)
    public String saveProduct(Product product) {
        productService.saveProduct(product);
        return "redirect:/product/" + product.getId();
    }

    @RequestMapping("product/{id}")
    public String showProduct(@PathVariable Integer id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "productshow";
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("products", productService.listAllProducts());
        return "products";
    }

    @RequestMapping("product/edit/{id")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "productform";
    }

    @RequestMapping("product/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
```

Now add the implementation of our ProductService interface by adding ProductServiceImpl in the services package

```java
package com.robxx.springbootapp.services;

import com.robxx.springbootapp.domain.Product;
import com.robxx.springbootapp.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Iterable<Product> listAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findOne(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.delete(id);
    }
}
```

##### Tymeleaf and Tymeleaf fragments

Thymeleaf fragments are a very powerful feature of Thymeleaf. They allow you to define repeatable chunks of code for your website. Once you define a Thymeleaf fragment, you can reuse it in other Thymeleaf templates. This works great for components you wish to reuse across your web pages.

Create a new folder in the templates folder called fragments

Now we are going to create a standard header fragment and a menuu fragment.

Add a new file called headerinc.html and copy the code from the index.html head section:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en" th:fragment="head">

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <script th:src="@{/webjars/jquery/3.3.1/jquery.min.js}"></script>
    <script th:src="@{/webjars/bootstrap/3.3.5/js/bootstrap.min.js}"></script>

    <link rel="stylesheet" th:href="@{/webjars/bootstrap/3.3.5/css/bootstrap.min.css}" />
    <link href="../static/css/main.css" th:href="@{css/main.css}" rel="stylesheet" media="screen"/>

</head>
<body>

</body>
</html>
```

Next add another fragment called header.html with the following code:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">

</head>
<body>

<div class="container">
    <div th:fragment="header">
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <div class="navbar-header">
                    <a class="navbar-brand" href="#" th:href="@{/}">Home</a>
                    <ul class="nav navbar-nav">
                        <li><a href="#" th:href="@{/products}">Products</a></li>
                        <li><a href="#" th:href="@{/product/new}">Create Product</a></li>
                    </ul>

                </div>
            </div>
        </nav>

        <div class="jumbotron">
            <div class="row text-center">
                <div class="">
                    <h2>Spring Framework Guru</h2>

                    <h3>Spring Boot Web App</h3>
                </div>
            </div>
            <div class="row text-center">
                <img src="../../static/images/spring-io.png" width="400"
                     th:src="@{/images/spring-io.png}"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

Thymeleaf adds these to templates using comments

Now in index.html change the file to be as following:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">
    <title>robxx spring boot full application</title>
    <!--/*/ <th:block th:include="fragments/headerinc :: head"></th:block> /*/-->
</head>
<body>

<div class="container">
    <!--/*/ <th:block th:include="fragments/header :: header"></th:block> /*/-->
</div>
</body>
</html>
```

Now go to the browser and open the page.
If you view the source, you will see that the fragments have been included.

##### Thymeleaf Views for CRUD Application

Now to add the product views:

productshow.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">
    <title>robxx spring boot full application</title>
    <!--/*/ <th:block th:include="fragments/headerinc :: head"></th:block> /*/-->
</head>
<body>
<div class="container">
    <!--/*/ <th:block th:include="fragments/header :: header"></th:block> /*/-->

    <h2>Product Details</h2>
    <div>
        <form class="form-horizontal">
            <div class="form-group">
                <label class="col-sm-2 control-label">Product Id:</label>
                <div class="col-sm-10">
                    <p class="form-control-static" th:text="${product.id}">Product Id</p></div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Description:</label>
                <div class="col-sm-10">
                    <p class="form-control-static" th:text="${product.description}">description</p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Price:</label>
                <div class="col-sm-10">
                    <p class="form-control-static" th:text="${product.price}">Priceaddd</p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Image Url:</label>
                <div class="col-sm-10">
                    <p class="form-control-static" th:text="${product.imageUrl}">url....</p>
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>
```

products.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">
    <title>robxx spring boot full application</title>
    <!--/*/ <th:block th:include="fragments/headerinc :: head"></th:block> /*/-->
</head>
<body>
<div class="container">
    <!--/*/ <th:block th:include="fragments/header :: header"></th:block> /*/-->
    <div th:if="${not #lists.isEmpty(products)}">
        <h2>Product List</h2>
        <table class="table table-striped">
            <tr>
                <th>Id</th>
                <th>Product Id</th>
                <th>Description</th>
                <th>Price</th>
                <th>View</th>
                <th>Edit</th>
                <th>Delete</th>
            </tr>
            <tr th:each="product : ${products}">
                <td th:text="${product.id}"><a href="/product/${product.id}">Id</a></td>
                <td th:text="${product.productId}">Product Id</td>
                <td th:text="${product.description}">descirption</td>
                <td th:text="${product.price}">price</td>
                <td><a th:href="${'/product/' + product.id}">View</a></td>
                <td><a th:href="${'/product/edit/' + product.id}">Edit</a></td>
                <td><a th:href="${'/product/delete/' + product.id}">Delete</a></td>
            </tr>
        </table>

    </div>
</div>

</body>
</html>
```

productform.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">
    <title>robxx spring boot full application</title>
    <!--/*/ <th:block th:include="fragments/headerinc :: head"></th:block> /*/-->
</head>
<body>
<div class="container">
    <!--/*/ <th:block th:include="fragments/header :: header"></th:block> /*/-->

    <h2>Product Details</h2>
    <div>
        <form class="form-horizontal" th:object="${product}" th:action="@{/product}" method="post">
            <input type="hidden" th:field="*{id}"/>
            <input type="hidden" th:field="*{version}"/>
            <div class="form-group">
                <label class="col-sm-2 control-label">Description:</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" th:field="*{description}"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Price:</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" th:field="*{price}"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Image Url:</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" th:field="*{imageUrl}"/>
                </div>
            </div>
            <div class="row">
                <button type="submit" class="btn btn-default">Submit</button>
            </div>
        </form>
    </div>
</div>

</body>
</html>
```

Bug: The product ID for the second product seems to be missing! Find out why.

