package ca.cmpt213.controller;

import ca.cmpt213.exception.InvalidElementTypeException;
import ca.cmpt213.exception.TokimonCardNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.cmpt213.model.TokimonCard;
import ca.cmpt213.model.TokimonCardList;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class TokimonCardController {
    private AtomicInteger nextId; // Next ID to assign to a new TokimonCard
    private TokimonCardList tokimonCardList;

    //GET and POST requests are the two most common HTTP request methods
    // GET requests are used to request data from a specified resource. GET requests should only
    // retrieve data and have no other effect on the data. Paramters are appended to the URL, making them visible in the URL.

    // POST requests are used to send data to a server to create/update a resource. The data sent to the server with POST
    // is stored in the request body of the HTTP request. Unlike GET requests, POST requests do not remain in the browser history.


    // the @GetMapping annotation tells Spring to use this method to handle specific URL GET requests
    // the argument provided such as "/tokimonCard" is the URL path that the method will handle. When
    // a GET request is made to this path, the annotated method is invoked to process the request.
    @GetMapping("/api/tokimon/all")
    public List<TokimonCard> getTokimonCards(HttpServletResponse response) {
        // The parameters are annotated with @RequestParam, which tells Spring to extract the value of the query
        // value="name" specifies the name of the request parameter to bind
        // defaultValue="tokiGuy" specifies the default value to use if the request parameter is not present
        // String name is the method parameter that will hold the value of the request parameter name, if not provided it will be "tokiGuy"
        //These parameters allow the method to accept dynamic values through the URL, making the method more flexible in handling different requests
        // Example: http://localhost:8080/tokimonCard?name=Tokimon&elementType=WATER, would result in name = "Tokimon" and elementType = "WATER"
        for (TokimonCard tokimonCard : tokimonCardList.getTokimonCards()) {
            System.out.println("ID: " + tokimonCard.getTid());
            System.out.println("Name: " + tokimonCard.getName());
            System.out.println("Element Type: " + tokimonCard.getElementType());

        }
        response.setStatus(HttpServletResponse.SC_OK); // Set the response status to 200 OK
        return tokimonCardList.getTokimonCards();
    }

    //this get request is used to get a specific tokimon card by its ID
    @GetMapping("/api/tokimon/{tid}")
    public TokimonCard getTokimonCard(@PathVariable long tid, HttpServletResponse response){
        System.out.println("GET /tokimonCard/" + tid);
        try {
            TokimonCard tokimonCard = tokimonCardList.getTokimonCard(tid);
            response.setStatus(HttpServletResponse.SC_OK); // Set the response status to 200 OK
            return tokimonCard;
        } catch (TokimonCardNotFoundException e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Set the response status to 404 Not Found
        }
        return null;
    }

    // the @PostMapping annotation tells Spring to use this method to handle specific URL POST requests
    // annotating a method indicates to Spring that this method should be invoked when the server receives a POST request to the specified URL
    // example: http://localhost:8080/tokimonCard with a POST request would invoke this method

    // the HttpServletResponse arg is used to interact with and manipulate the HTTP response that your server
    // sends back to the client. Specifically, it allows you to set the status code, headers, and the body of the response
    /// in this case we are setting the status code to 201 Created to indicate that the request has been fulfilled
    // and has resulted in one or more new resources being created

    @PostMapping("/api/tokimon/add")
    public TokimonCard addTokimonCard(@RequestParam String name, @RequestParam String elementType, HttpServletResponse response){
        System.out.println("POST /tokimonCard");

        try {
            // Create a new TokimonCard object and set its attributes using the request parameters
            TokimonCard newTokimonCard = new TokimonCard();
            newTokimonCard.setName(name);
            newTokimonCard.setElementType(validateElementType(elementType));
            newTokimonCard.setTid(nextId.getAndIncrement()); // Assign the next ID to the new TokimonCard using the AtomicInteger
            // .getAndIncrement() method - this method atomically increments the current value by one and returns the updated value
            tokimonCardList.addTokimonCard(newTokimonCard);

            response.setStatus(HttpServletResponse.SC_CREATED); // Set the response status to 201 Created
            return newTokimonCard;
        } catch (InvalidElementTypeException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Set the response status to 400 Bad Request
            return null;
        }
    }

    private TokimonCard.ElementType validateElementType(String elementType) {
        try { //if element type is valid, convert it to uppercase string and cast it to the ElementType enum
            return TokimonCard.ElementType.valueOf(elementType.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new InvalidElementTypeException("Invalid element type: " + elementType);
        }
    }

    // the @PutMapping annotation tells Spring to use this method to handle specific URL PUT requests

    // the @PathVariable annotation is used to extract values from the URL path and bind them to method parameters
    // example of @PathVariable usage: @GetMapping("/tokimonCard/{tid}") - the {tid} is a placeholder for the value that will be extracted
    // note: put requests UPDATE while post requests CREATE
    @PutMapping("/api/tokimon/edit/{tid}")
    public TokimonCard updateTokimonCard(@PathVariable long tid, @RequestBody TokimonCard newTokimonCard, HttpServletResponse response){
        System.out.println("PUT /tokimonCard/" + tid);
        try {
            newTokimonCard.setElementType(validateElementType(newTokimonCard.getElementType().name()));
            tokimonCardList.updateTokimonCard(tid, newTokimonCard);
            response.setStatus(HttpServletResponse.SC_OK); // Set the response status to 200 OK
            return newTokimonCard;
        } catch (InvalidElementTypeException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Set the response status to 400 Bad Request
            return null;
        }
    }

    @DeleteMapping("/api/tokimon/{tid}")
    public void deleteTokimonCard(@PathVariable long tid, HttpServletResponse response){
        System.out.println("DELETE /tokimonCard/" + tid);

        try {
            TokimonCard tokimonCard = tokimonCardList.getTokimonCard(tid);
            if (tokimonCard != null){
                tokimonCardList.deleteTokimonCard(tid);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // Set the response status to 204 NO CONTENT
            }
        } catch (TokimonCardNotFoundException e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Set the response status to 404 Not Found
        }
    }

    // the @PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization
    // this code is executed after initialization of the class but before the class is put into service
    @PostConstruct
    public void init() {
        System.out.println("POST CONSTRUCT CODE");
        nextId = new AtomicInteger(4); // Initialize the nextId to 0
        tokimonCardList = new TokimonCardList();
    }

}

