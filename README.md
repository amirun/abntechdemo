# DEMO APPLICATION


## Project Dependencies
1. Java 17+
2. Maven 3.6.3 or newer
3. Spring Boot 3.1.4
4. Lombok
5. Spring Security
6. Spring Data Mongo
7. Query DSL
8. Junit
9. Junit TestContainers
10. Docker Daemon

## Getting Started

There are multiple ways to do this.
####1. Start application using `docker-compose` from project root. Requires docker. 
   ```bash
   docker-compose up -d
   ```
   - No manual intervention necessary
   - This will spin up a container with the latest mongo image and create appropriate db users(Ref:`data/mongo/users.js` ) for the application to access the db.
   - Build the docker image of the application using the maven and jdk. (Ref: `Dockerfile` in project root)
   - Deploy the application to `port 8080` with specified environment variables.

####2. Use test containers for running application. Requires docker.
   ```bash
   mvn spring-boot:test-run
   ```
- No manual intervention necessary.
- Application will start in test profile, running directly on port 8080 with a temporary mongo db started by test containers. This use the class `TestAbnTechDemoNosqlApplication.java` to start the application instead of `AbnTechDemoNosqlApplication.java`.

####3. Start application by manually by running:
   ```bash
   mvn spring-boot:run
   ```
   - However, you will need to provide the mongo db connection string via application.properties file by defining properties.
     - `SPRING_DATA_MONGODB_URI=<your db uri with credentials>`
     - `SPRING_DATA_MONGODB_DATABASE=<your databse name>`

## Reference Documentation
Refer to attached [DEMO PROJECT.pdf](DEMO PROJECT.pdf) in project root.

## API Guides

Swagger-UI: http://localhost:8080/swagger-ui/index.html#/

#### Authentication: POST `/authenticate`
Sample request:

URI:`http://localhost:8080/authenticate`

Request Body:
```json
{
  "userName": "Patrick",
  "password": "pAssw0rd"
}
```
Response body:
```json
{
  "jwttoken": "eyJhbGciOiJIUzUxMiJ9...CcWhdhvGdDYnNdW8fQoA"
}
```

- You can change the `userName` to any string
- Do not modify the `password` value. This is a dummy password via application.properties key `security.dummy.password`
User management is not in the scope of this project.
- Use the value of `jwttoken`, to authenticate all below APIs.
  Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmciLCJpYXQiOjE2OTcwMTg5MzEsImV4cCI6MTY5NzAyMDczMX0.SDPp9Q4r-3MjNz_dOMUKQ6T3t-oK2vzbG3tQ-zh3fxeo9PqsPsiShGq4M9V7godI4so4ofgvct8u8j-_hMYEng'

#### Create Recipe: POST `/recipes`
All fields are mandatory and have appropriate validations.

Payload format
```
RecipeDTO {
name*	            string              not blank
isVegetarian*	    boolean             true/false
numberOfServings    integer($int32)     max: 100            min: 1
instructions*	    string              maxLength: 1500     minLength: 1
ingredients*	    [string]            not empty array
}
```

Sample request:

URI: `http://localhost:8080/recipes`

Request Body:
```json
  {
     "name": "Garlic Roasted Brussels Sprouts",
     "numberOfServings": 4,
     "instructions": "1. Preheat the oven to 400°F (200°C).\n2. Trim and halve Brussels sprouts.\n3. Toss them with minced garlic, olive oil, salt, and pepper.\n4. Spread and roast them on a baking sheet. \n5. Serve hot.",
     "ingredients": [
          "Brussels sprouts",
          "Garlic (minced)",
          "Olive oil",
          "Salt",
          "Pepper"
     ],
     "isVegetarian": true
  }
```
Response body:
```json
{
  "id": "6526659475a638432407b7eb",
  "name": "Garlic Roasted Brussels Sprouts",
  "isVegetarian": true,
  "numberOfServings": 4,
  "instructions": "1. Preheat the oven to 400°F (200°C).\n2. Trim and halve Brussels sprouts.\n3. Toss them with minced garlic, olive oil, salt, and pepper.\n4. Spread and roast them on a baking sheet. \n5. Serve hot.",
  "ingredients": [
    "Brussels sprouts",
    "Garlic (minced)",
    "Olive oil",
    "Salt",
    "Pepper"
  ]
}
```

More samples provided for testing purposes: `data/Recipes/Recipes.json` [Recipes.json](data/Recipes/Recipes.json)

#### Get Recipe by ID: GET `/recipes/{id}`
Fetch recipe matching the given ID.

Parameters:
- `id` (path) - The ID of the recipe to fetch.

Sample Request:

URI: `http://localhost:8080/recipes/6526659475a638432407b7eb`

Response body:
```json
{
  "id": "6526659475a638432407b7eb",
  "name": "Garlic Roasted Brussels Sprouts",
  "isVegetarian": true,
  "numberOfServings": 4,
  "instructions": "1. Preheat the oven to 400°F (200°C).\n2. Trim and halve Brussels sprouts.\n3. Toss them with minced garlic, olive oil, salt, and pepper.\n4. Spread and roast them on a baking sheet. \n5. Serve hot.",
  "ingredients": [
    "Brussels sprouts",
    "Garlic (minced)",
    "Olive oil",
    "Salt",
    "Pepper"
  ]
}
```

#### Update Recipe: PUT `/recipes/{id}`
Update an existing recipe.

Parameters:
- `id` (path) - The ID of the recipe to update.

Sample Request:

URI: `http://localhost:8080/recipes/6526659475a638432407b7eb`

Request Body:
```json
{
  "name": "Roasted Brussels Sprouts",
  "isVegetarian": true,
  "numberOfServings": 4,
  "instructions": "No further instructions",
  "ingredients": [
    "Brussels sprouts",
    "Olive oil",
    "Salt",
    "Pepper"
  ]
}
```
Response Body:
```json
{
  "id": "6526659475a638432407b7eb",
  "name": "Roasted Brussels Sprouts",
  "isVegetarian": true,
  "numberOfServings": 4,
  "instructions": "No further instructions",
  "ingredients": [
    "Brussels sprouts",
    "Olive oil",
    "Salt",
    "Pepper"
  ]
}
```

#### Delete Recipe: DELETE `/recipes/{id}`
Delete a recipe specified by ID.

Parameters:
- `id` (path) - The ID of the recipe to delete.

Sample request:

URI: `http://localhost:8080/recipes/6526659475a638432407b7eb`

Response 200 OK, body:

`true`

#### Get All Recipes with Pagination: GET `/recipes`
Get all recipes with pagination.

Parameters:
- `pageNo` (query) - The page number (required). First page is 0.
- `size` (query) - The page size (required).

Sample request:

URI: `http://localhost:8080/recipes?pageNo=0&size=5`

Response body:
```json
[
  {
    "id": "6526605075a638432407b7ea",
    "name": "Recipe 1",
    "isVegetarian": ...
  },
  {
    "id": "65266abb75a638432407b7ec",
    "name": "Garlic Roasted Brussels Sprouts",
    "isVegetarian": ...
  },
     .
     .
     .
  {
    "id": "65266abd75a638432407b7ed",
    "name": "Roasted Brussels Sprouts",
    "isVegetarian": ..
  }
]
```

#### Filter Recipes: GET `/recipes/filterRecipes`
Filter recipes based on specified parameters.

Parameters
- `isVegetarian`(boolean query) - Filter by whether the recipe is vegetarian. 
- `numberOfServings` (numeric query) - Filter by the number of servings. 
- `ingredientsToInclude` (multivalued query) - Filter by ingredients to include. Case-sensitive.
- `ingredientsToExclude` (multivalued query) - Filter by ingredients to exclude. Case-sensitive.
- `instructionTextFilter` (text query) - Filter by instruction text.

Sample Request 1 : Fetching all vegetarian recipes, with serving size 4, has potatoes, no salmon and can be cooked in a oven.

URI: `http://localhost:8080/recipes/filterRecipes?isVegetarian=true&numberOfServings=4&ingredientsToInclude=Potatoes&ingredientsToExclude=salmon&instructionTextFilter=oven`

Response body:
```json
[
  {
    "id": "65266c6c75a638432407b7ee",
    "name": "Cajun Spiced Potatoes",
    "isVegetarian": true,
    "numberOfServings": 4,
    "instructions": "1. Preheat the oven to 425°F (220°C).\n2. Wash and scrub the potatoes. Cut them into bite-sized pieces.\n3. In a bowl, toss the potato pieces with olive oil, Cajun spice mix, salt, and pepper.\n4. Spread the seasoned potatoes on a baking sheet.\n5. Roast in the preheated oven for 25-30 minutes or until the potatoes are crispy and golden brown.\n6. Serve hot and enjoy!",
    "ingredients": [
      "Potatoes",
      "Cajun spice mix",
      "Olive oil",
      "Salt",
      "Pepper"
    ]
  }
]
```

Sample Request 2 : Fetch all non-vegetarian recipes, with 2 servings, which has 'Onion' and 'Garlic', but not 'Beef', cooked in pan.

URI: `http://localhost:8080/recipes/filterRecipes?isVegetarian=false&numberOfServings=2&ingredientsToInclude=Onion&ingredientsToInclude=Garlic&ingredientsToExclude=Beef&ingredientsToExclude=White%20wine&instructionTextFilter=pan`

Response body:
```json
[
  {
    "id": "65266f1875a638432407b7ef",
    "name": "Pan-Seared Salmon with Garlic Butter",
    "isVegetarian": false,
    "numberOfServings": 2,
    "instructions": "1. Heat a pan over medium-h...on top.",
    "ingredients": [
      "Salmon fillets",
      "Garlic",
      "Onion",
      "Olive oil",
      "Butter",
      "Salt",
      "Pepper"
    ]
  },
  {
    "id": "65266f3375a638432407b7f1",
    "name": "Spicy Chicken Stir-Fry",
    "isVegetarian": false,
    "numberOfServings": 2,
    "instructions": "1. Heat a pan over medium-high ... hot.",
    "ingredients": [
      "Chicken",
      "Onion",
      "Garlic",
      "Assorted vegetables",
      "Stir-fry sauce",
      "Spices"
    ]
  }
]
```