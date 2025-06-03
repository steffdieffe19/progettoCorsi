package com.example.demo.ClientFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class Client {
    @FeignClient(name = "docenteClient", url = "http://localhost:8081")
    public interface DocenteClient {
        @GetMapping("/docenti/{id}")
        void getDocenteById(@PathVariable("id") Long id);
    }
}
