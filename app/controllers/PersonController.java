package controllers;


import models.Person;
import play.libs.*;
import play.libs.oauth.OAuth;
import play.libs.oauth.OAuth.ConsumerKey;
import play.libs.oauth.OAuth.RequestToken;
import play.libs.oauth.OAuth.ServiceInfo;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.libs.oauth.OAuth.OAuthCalculator;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class PersonController extends Controller {

    private final FormFactory formFactory;

	private WSClient wsClient ;
	
	static final ConsumerKey key = new ConsumerKey("xqOTwsISbQCbVjjBAjEXE9a8y", "fgFfGwnabhWoCZiqgQikkK42dMZtXRYfQnetjSzUZOkhVqftVr");
	
	private static final ServiceInfo SERVICE_INFO =
	        new ServiceInfo("https://api.twitter.com/oauth/request_token",
	            "https://api.twitter.com/oauth/access_token",
	            "https://api.twitter.com/oauth/authorize",
	            key);
	
	private static final OAuth TWITTER = new OAuth(SERVICE_INFO);
	
	
	public CompletionStage<Result> homeTimeline() {
        Optional<RequestToken> sessionTokenPair = getSessionTokenPair();
        if (sessionTokenPair.isPresent()) {
            return wsClient.url("https://api.twitter.com/1.1/statuses/home_timeline.json")
                    .sign(new OAuthCalculator(PersonController.key, sessionTokenPair.get()))
                    .get()
                    .thenApply(result -> ok(result.asJson()));
        }
        return CompletableFuture.completedFuture(redirect(routes.PersonController.auth()));
    }

    public Result auth() {
        String verifier = request().getQueryString("oauth_verifier");
        if (Strings.isNullOrEmpty(verifier)) {
            String url = routes.PersonController.auth().absoluteURL(request());
            RequestToken requestToken = TWITTER.retrieveRequestToken(url);
            saveSessionTokenPair(requestToken);
            return redirect(TWITTER.redirectUrl(requestToken.token));
        } else {
        	System.out.println("else condition");
            RequestToken requestToken = getSessionTokenPair().get();
            RequestToken accessToken = TWITTER.retrieveAccessToken(requestToken, verifier);
            saveSessionTokenPair(accessToken);
            return redirect(routes.PersonController.personsList());
        }
    }

    private void saveSessionTokenPair(RequestToken requestToken) {
        session("token", requestToken.token);
        session("secret", requestToken.secret);
    }

    private Optional<RequestToken> getSessionTokenPair() {
        if (session().containsKey("token")) {
            return Optional.ofNullable(new RequestToken(session("token"), session("secret")));
        }
        return Optional.empty();
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    @Inject
    public PersonController(FormFactory formFactory, WSClient wsClient ) {
        this.formFactory = formFactory;
        this.wsClient = wsClient ;
    }

    private static ObjectNode responseCreator(Object body, boolean status){
    	ObjectNode response = Json.newObject();
    	response.put("isSuccessfull", status);
        if (body instanceof String) {
            response.put("body", (String) body);
        }
        else {
            response.put("body", (JsonNode) body);
        }
    	return response; 
    }
    


    public Result addPerson() {
        
        JsonNode json = request().body().asJson();
        if (json == null){
            return badRequest(PersonController.responseCreator(
              "Expecting Json data", false));
        }
        Person person = new Person();
        person.setName(json.get("name").asText());
        System.out.println("<=================>");
        System.out.println(person.name);       
        System.out.println("<=================>");
        person.save();
        return created(PersonController.responseCreator(Json.toJson(person), true));
    }
    
    public Result personsList() {

    	List<Person> persons = Person.find.all();
  
    	ObjectMapper mapper = new ObjectMapper();
        try {
			return ok(PersonController.responseCreator(mapper.writeValueAsString(persons), true));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        return internalServerError("Server Error");
    }
    
    public Result deletePerson(long id){
    	Person p = Person.find.byId(id) ;
    	boolean status = p.delete();
    	return ok(PersonController.responseCreator("Person " + p.name + " deleted!", status));
    }
    
    public Result soapServiceCaller(Float amount){
    	
    	String from = "PKR" ;
    	String to = "USD" ;
    	
    	String wsReq = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
				  "<soap12:Body><ConversionRate xmlns=\"http://www.webserviceX.NET/\">" +
                  "<FromCurrency>"+from+"</FromCurrency>" +
                  "<ToCurrency>"+to+"</ToCurrency>"+
				  "</ConversionRate></soap12:Body></soap12:Envelope>";
    	CompletionStage<WSResponse> response = wsClient.url("http://www.webservicex.net/CurrencyConvertor.asmx").
    			setHeader("content-type", "application/soap+xml").post(wsReq) ;
    	
    	CompletableFuture<Document> cf = response.toCompletableFuture().thenApply(WSResponse::asXml);
    	
    	Document d = cf.join();
    	
    	System.out.println("amount = " + amount);
    	
    	String rate = d.getElementsByTagName("ConversionRateResult").item(0).getTextContent();
    
    	System.out.println("rate = " + rate);
    	
    	
    	
    	Float total = amount * Float.parseFloat(rate);
    	
    	return ok(PersonController.responseCreator(amount + " us$ = " + total + " UK pound", true));
    	
//    	return ok();
    }
    
public Result soapSecondServiceCaller(String country){
    	
    	
    	String wsReq = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
    	 "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
    	  "<soap12:Body>" +
    	    "<GetCitiesByCountry xmlns=\"http://www.webserviceX.NET\">" +
    	      "<CountryName>" + country + "</CountryName>" +
    	    "</GetCitiesByCountry>" +
    	  "</soap12:Body>" +
    	"</soap12:Envelope>";
    	      
    	CompletionStage<WSResponse> response = wsClient.url("http://www.webservicex.net/globalweather.asmx").
    			setHeader("content-type", "application/soap+xml; charset=utf-8").post(wsReq) ;
    	
    	CompletableFuture<Document> cf = response.toCompletableFuture().thenApply(WSResponse::asXml);
    	
    	Document d = cf.join();
    	
    	System.out.println(d.toString());
    	
    	String cities = d.getElementsByTagName("GetCitiesByCountryResult").item(0).getTextContent();
    
    	System.out.println("Cities = " + cities);
    	
    	return ok(PersonController.responseCreator(" cities = " + cities, true));
    	
//    	return ok();
    }

}
