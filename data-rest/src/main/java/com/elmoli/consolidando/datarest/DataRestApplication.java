package com.elmoli.consolidando.datarest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataRestApplication implements CommandLineRunner
{

 //   private final UserRepository userRepository;

    public static void main(String[] args)
    {
        SpringApplication.run(DataRestApplication.class, args);
    }

//    DataRestApplication(UserRepository userRepository)
//    {
//        this.userRepository = userRepository;
//    }

    @Override
    public void run(String... args) throws Exception
    {
//        userRepository.deleteAll();
//        
//        
//        
//        var user1 = new User("ramoncases@gmail.com", "Ramon", "Cases");
//        var user2 = new User("federicovelmonte@gmail.com", "Federico", "Velmonte");
//        userRepository.saveAll(List.of(user1, user2));
//        
//        user1.setName("New Name");
//        userRepository.save(user1);
    }

}
