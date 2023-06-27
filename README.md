# remapper-lib
Redundant object-to-object mapper.

## Use Case
Consider we have to copy objects, for certain use cases, like copying a DTO to repository model so it can be serialized by ORM. 
In some cases, we create same classes and objects with slightly different name.

For e.g.
```
data class PersonDto(
    val name: String,
    val age: UInt,
)
```
and
```
data class PersonModel(
    val name: String,
    val age: UInt,
)
```

This is oversimplified example, but it does explain the problem, when dealing with such objects, to copy Dto to model we either write `toModel` or similar converter methods, or we call constructors.
This is hectic in many cases where we are just calling constructors for copying object's data.

What if we can automate it?

## ReMapper to the rescue! 
Something like:
```
 val personDto = PersonDto("John Doe", 30U)
 val reMapper = ReMapper()
 val personModel = reMapper.map(personDto, PersonModel::class)
```

ReMapper does exactly this. 🔥

You can also use extension function:
```
val personModel = personDto.mapTo(PersonModel::class)
```


## Get it now!
To add this in your project use following declaration:
```
<dependency>
  <groupId>com.github.talhahasanzia</groupId>
  <artifactId>remapper-lib</artifactId>
  <version>1.0.1</version>
</dependency>
```

Package is distributed using Github Packages, for more info checkout [this article from Github](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).

## Limitations
ReMapper uses reflection to copy fields, if field name or type is not matched, it will not work.
