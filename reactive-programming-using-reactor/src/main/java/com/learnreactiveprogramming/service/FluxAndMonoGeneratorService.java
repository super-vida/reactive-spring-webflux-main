package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux(){
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log();
    }

    public Flux<String> namesFlux_map(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                //.map(s -> s.toUpperCase())
                .log();
    }

    public Flux<String> namesFlux_flatmap(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap( s -> splitString(s))
                .log();
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap( s -> splitString_async(s))
                .log();
    }
    public Flux<String> namesFlux_concatmap_async(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap( s -> splitString_async(s))
                .log();
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return  Flux.fromArray(charArray);
    }

    public Flux<String> splitString_async(String name){
        var charArray = name.split("");
        //var delay = new Random().nextInt(1000);
        var delay = 1000;
        return  Flux.fromArray(charArray).delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFlux_immutable(){
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;

    }

    public Mono nameMono(){
        return Mono.just("alex").log();
    }

    public Mono<List<String>> nameMono_flatMap(int stringLength){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> nameMono_flatMapMany(int stringLength){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var listArray = List.of(charArray);
        return Mono.just(listArray);
    }

    public Flux<String> namesFlux_transform(int stringLength){

        Function <Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap( s -> splitString(s))
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength){

        Function <Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap( s -> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filterMap);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap( s -> splitString(s))
                .switchIfEmpty(defaultFlux)
                .log();
    }





    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux().subscribe(name -> System.out.println(name));
        fluxAndMonoGeneratorService.nameMono().subscribe(name -> System.out.println(name));
    }

}
