# Bit ![](https://github.com/ceticamarco/bit/actions/workflows/bit.yml/badge.svg)
Bit is a simple, web-based, self-hostable text sharing platform written in Java and Spring.
You can access it [from here](https://bit.marcocetica.com).

The frontend is also open-source, and it's available [on this page](https://github.com/ceticamarco/bit_frontend).

## General
**Bit** can be used both from the [frontend](https://bit.marcocetica.com) and through the REST API.
Before using it, read the following technical notices:

1. By default, each new _"text"_(from now on: **post**) added to the platform 
is anonymous and expires after one week. In order to associate a new post to an identity, 
you first need to register a new user account. The registration process requires a unique **username**, 
a unique **email address** and a **password**. User accounts can **NOT** be modified or recovered; 
therefore if you lose your password, you will need to create a new account using a new email address.

2. Each user account is associated with a _user role_ that determines the granular access
to the _special_ endpoints. The _user role_ is set by default to **unprivileged** at registration
time and can be altered only by manually modifying the database.

3. Special endpoints are only accessible to the **privileged** user class.

4. Posts published with a valid user identity can be altered or deleted. In order to do that, you
need to authenticate yourself with your credentials within the update/delete request. Anonymous
posts, on the other hand, can **NOT** be altered or removed; if you think that a certain content
goes against the terms of service, you can email the owner of the **bit** instance.

5. The expiration date controls whether the post can be showed or not, once the current date
is greater or equal than the expiration date, the post is classified as **expired** and thus
shall not be showed. Expired posts are **NOT** deleted but may be manually removed by the
instance owner.

6. The **bit** platform is _stateless_, hence there is no such thing as user session. Every time
you need to use your user account for a certain operation(e.g., create a new post with an identity
or delete an existing, non-anonymous post) you will need to provide your user credentials.

7. Deleting an existing user, will result in a [cascade delete](https://learn.microsoft.com/en-us/ef/core/saving/cascade-delete#:~:text=Cascading%20deletes%20are%20needed%20when%20a%20dependent/child%20entity%20can%20no%20longer%20be%20associated%20with%20its%20current%20principal/parent.%20This%20can%20happen%20because%20the%20principal/parent%20is%20deleted%2C%20or%20it%20can%20happen%20when%20the%20principal/parent%20still%20exists%20but%20the%20dependent/child%20is%20no%20longer%20associated%20with%20it.)
of any existing post associated with that user.

8. User signup can be disabled by setting the environment variable `BIT_DISABLE_SIGNUP` to `1`.
By default, user registration is enabled(see `docker-compose.yml`).

## Database
New posts are stored on a relational database(PostgreSQL) using the Spring ORM system(Hibernate).
The architecture of the bit platform consists of two tables: **bt_users** and **bt_posts**.
The former stores the user accounts, and it's defined as follows:

| Column     | Data Type         | Nullable |
|------------|-------------------|----------|
| user ID    | `String`          | `false`  |
| created at | `YYYY-MM-DD Date` | `false`  |
| email      | `String`          | `false`  |
| password   | `BCrypt`          | `false`  |
| role       | `UserRole`        | `false`  |  
| username   | `String`          | `false`  |

The latter, instead, stores the posts, and it's defined as follows:

| Column          | Data Type               | Nullable  |
|-----------------|-------------------------|-----------|
| post ID         | `String`                | `false`   |
| content         | `String`                | `false`   |
| created at      | `YYYY-MM-DD date`       | `false`   |
| expiration date | `YYYY-MM-DD date`       | `true`    |
| title           | `String`                | `false`   |
| user ID         | `Foreign key constrain` | `true`    |

The user password is stored using a `BCrypt` based hash. Each post can be associated with one 
user(eventually zero) while each user can be associate with multiple posts(eventually zero).
The relationship is of the type _"one to many"_ from the user's perspective.

## Deploy
In order to deploy the **bit** platform, you will need to install Docker/Podman and docker-compose.
Once done that, you can easily launch the backend using the following command:
```sh
$> docker-compose up -d
```
By default, the following parameters are used:
- `SERVER_PORT`: "3000";
- `POSTGRES_USER`: "bituser";
- `POSTGRES_PASSWORD`: "qwerty1234";
- `POSTGRES_DB`: "bit";
- `SPRING_SECURITY_USER_NAME`: "admin";
- `SPRING_SECURITY_USER_PASSWORD`: "admin".

Be sure to update these values by editing the`docker-compose.yml` file. Once the containers
are deployed, you can expose the application through a reverse proxy:
```nginx
location /api {
    proxy_pass http://127.0.0.1:3000;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

## Endpoints
The backend exposes the following REST APIs:

### `POST` New User(`/api/users/new`):
_Description_: Add new user.  
_Parameters_: **username**(`string`), **email**(`string`) **password**(`string`).

### `DELETE` Delete User(`/api/users/delete`):
_Description_: Delete an existing user.  
_Parameters_: **email**(`string`) **password**(`string`).

### `GET` User List(`/api/users`):
**(special endpoint)**  
_Description_: Retrieve all users.  
_Parameters_: **email**(`string`), **password**(`string`).

### `GET` Post List(`/api/posts`):
**(special endpoint)**  
_Description_: Retrieve all posts.  
_Parameters_: **email**(`string`), **password**(`string`).

### `GET` Post By ID(`/api/posts/{postID}`):
_Description_: Search a post by its ID.  
_Parameters_: none.

### `GET` Post By Title(`/api/posts/bytitle`):
**(special endpoint)**  
_Description_: Search a post by its title.  
_Parameters_: **title**(`string`), **user**(`User`).

### `POST` New Post(`/api/posts/new`):
_Description_: Add a new post.  
_Parameters_: **title**(`string`), **content**(`string`), **expirationDate**(`YYYY-MM-DD`),
**user**(`User`).

### `PUT` Edit Post(`/api/posts/{postID}`):
_Description_: Update an existing, non-anonymous post.  
_Parameters_: **title**(`string`), **content**(`string`), **user**(`User`).

### `DELETE` Delete Post(`/api/posts/{postID}`):
_Description_: Delete an existing, non-anonymous post.  
_Parameters_: **user**(`User`).


## Examples
Below there are some practical examples on how to use the REST API:

1. **Add a non-anonymous, perpetual post**(_note: the user must exist_)  

`POST` request to `/api/posts/new` with the following body:
```json
{
  "title": "Hello World",
  "content": "This is a example text snippet",
  "expirationDate": null,
  "user": {
    "email": "john@example.com",
    "password": "very_bad_pw"
  }
}
```

2. **Add an anonymous post with expiration date set to January 25, 2024**

`POST` request to `/api/posts/new` with the following body:
```json
{
  "title": "Hello World",
  "content": "This is a example text snippet",
  "expirationDate": "2024-01-25"
}
```

3. **Delete post "`afj45c`"**

`DELETE` request to `/api/posts/afj45c` with the following body:
```json
{
  "email": "john@example.com",
  "password": "very_bad_pw"
}
```

4. **Get list of all post whose title is "`foo`"**

`GET` request to `/api/posts/bytitle` with the following body:
```json
{
  "title": "foo",
  "user": {
    "email": "john@example.com",
    "password": "very_bad_pw"
  }
}
```

In this case user `john@example.com` is a user of the class `PRIVILEGED`.

## Unit tests
The **bit** platform provides some unit tests for the _post_ and the _user_ controllers. You can
find them in `src/test`. The unit tests are automatically executed during the container bootstrap
process, to manually run them issue the following command:
```sh
$> ./mvnw test
```

## License
This software is released under the GPLv3 license. You can find a copy of the license with this
repository or by visiting the [following page](https://choosealicense.com/licenses/gpl-3.0/).
