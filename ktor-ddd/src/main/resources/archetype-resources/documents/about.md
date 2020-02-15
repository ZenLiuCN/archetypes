# About 
[TOC]
## Summary
This archetype package use tech for development some ddd(domain-driven-design ) like architecture 

## Components
1. ebean as ORM
2. ktor as HTTP service
3. kotlin as Main language
5. hikariCP as ConnectPool
6. caffeine for cache
7. junit5 for testing

and some more ...

## Structure

```js
|`root`
    |`app` (application module)
        |`api` (http api )
        |`service` (applicaiton layer service)
        |`support` (other )
    |`core` (domain module)
        |`common` (base model define)
        |`port`    (as named)
        |`dto`    (as named)
        |`user`   (sub domain user)
            |`schema` (domain define)
                |`entity` (domain entity define)
            |`usecase` (domain usecase define )
                `UserUsecase.kt` (usecase interface)
                `UserUsecaseFactory.kt` (as named)
    |`infrastructure` (infrastructure modules)
```
## Testing
 U should found testing example in `core` and  `app` module.
 
 `core testing`: this should testing all domain actions
 `app testing`:  this should testing application level api behaviours

## Discussions

1. we should expose only `usecase`,`dto`,`port` to other modules
2. we `MAY` not need `Aggregate Root`
3. we `MAY` make `usecase` as `Aggregate Root`
4. I do like treat DDD more than (even not ) some *particular* architectrue; Maybe we should just focus on `do it right` and `do it easy` 
and `upgrade it easy`
