## Streaming Geo Data

https://maps.googleapis.com/maps/api/staticmap?center=40.11320389333333,-88.22640001833334&size=640x640&scale=2&zoom=19&format=jpg&maptype=satellite

We use WebSocket to implement position streaming. Each location is assigned a WS server listening on a dedicated port.

Ports:

- **80** **443**: nginx gateway, handles TLS, then reverse proxy to **8080**
- **8080**: main ExpressJS server
- [**2000**, **2100**]: WebSocket servers 

## API

An `error` field will be included in response JSON if server side error occurred during handling of the request.

All API calls are authenticated with the user's access token, which should be provided as HTTP cookie. Every time a user logins, old token will be invalidated and replaced with a new one.

So the token is also used to identify the calling user's identity for access control purposes.

### Summary

no auth:

- `POST  /users/reg`               try to register
- `POST  /users/reg/v`              validate registration 


auth required:

- `GET    /user`                   get profile of the authenticated user
- `PUT    /user`                   update profile of the authenticated user
- `GET    /user/rec`               get recommendations for the authenticated user
- `POST   /user/upload`            upload file (currently, just the avatar image)

- `POST   /users`                  register new user
- `POST   /users/:username`        login
- `GET    /users/:username`        get user profile of username
- `POST   /users/:username/follow` follow a user
- `DELETE /users/:username/follow` unfollow a user
- `GET    /users/search`           search for user

- `GET    /locs`                   show all locations available
- `POST   /locs`                   post new appearance at location
- `GET    /locs/:locId`            get information about a specific location
- `GET    /locs/top`               get hottest locations
     
- `GET    /msgs`                   get messages of authenticated user
- `POST   /msgs`                   send message
- `DELETE /msgs`                   delete message

An `error` field in response JSON indicates error. Otherwise, the operation is successful.

--------

`GET    /user`

**resopnse**

```json
{
  "name": "foobar",
  "avatarUrl": "https://w.t.file",
  "gender": "male",
  "major": "Computer Science",
  "bio": "fffooooo barrrr",
  "hobbies": ["a", "b", "c", "d"]
}
```

`PUT    /user`                  

**request**

same format as get

**resopnse**

```json
{

}
```

`GET    /user/rec`              

**resopnse**

```json
{
"rec": [{"name":"1", "gender":"..."},{"...": "..."}]
}
```

`POST   /users`                 

**request**

```json
{
   
}
```

**resopnse**
```json
```

`GET    /users/:username`       

**request**
```json
```

**resopnse**
```json
```

`POST   /users/:username/follow`

**request**
```json
```

**resopnse**
```json
```

`DELETE /users/:username/follow`

**request**
```json
```

**resopnse**
```json
```

`GET    /users/search`          

**request**
```json
```

**resopnse**
```json
```

`GET    /locs`                  

**request**
```json
```

**resopnse**
```json
```

`POST   /locs`                  

**request**
```json
```

**resopnse**
```json
```

`GET    /locs/:locId`           

**request**
```json
```

**resopnse**
```json
```

`GET    /locs/top`              

**request**
```json
```

**resopnse**
```json
```

`GET    /msgs`                  

**request**
```json
```

**resopnse**
```json
```

`POST   /msgs`                  

**request**
```json
```

**resopnse**
```json
```

`DELETE /msgs`   

**request**
```json
```

**resopnse**
```json
```
