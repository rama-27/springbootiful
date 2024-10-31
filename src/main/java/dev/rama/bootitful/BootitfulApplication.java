package dev.rama.bootitful;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Collection;

@SpringBootApplication
public class BootitfulApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootitfulApplication.class, args);
    }

    @Bean
    BoredClient boredClient(WebClient.Builder builder){
        var wc=builder
                .baseUrl("https://www.boredapi.com/api/").build();
        var wca = WebClientAdapter.forClient(wc);
        return HttpServiceProxyFactory
                .builder(wca)
                .build()
                .createClient(BoredClient.class);
    }
}

@Controller
@RequiredArgsConstructor
class CustomerController{
   private final CustomerRepository repository;

   private final BoredClient boredClient;

   @SchemaMapping(typeName = "Customer")
   Activity suggestActivity (Customer customer){
       return this.boredClient.suggestSomethingToDo();
   }

   @QueryMapping
    Collection <Customer> customersByName( @Argument String name){
       return this.repository.findByName(name);
   }


   @ResponseBody
   @GetMapping("/customers")
    Iterable<Customer> customers(){
       return this.repository.findAll();
   }
}

interface BoredClient{
    @GetExchange("/activity")
    Activity suggestSomethingToDo();
}
record Activity(String activity, int participants){
}

interface CustomerRepository extends CrudRepository<Customer, Long> {
    Collection<Customer> findByName(String name);
}

record Customer(@Id  Integer id, String name) {
}