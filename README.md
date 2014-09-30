# Greyscalr

This is a dummy project written in Scala. Its main use is to study Scala and
some related Stuff, like Play and Akka. I'm using MongoDB to store data.

Greyscalr is a Play application that will run a web service that receives an
image via POST and converts it to a greyscale image. After that the image is 
uploaded  to Amazon S3 and our web service will provide an URL to download the
greyscaled image.

## Uploading an image using POST

To upload an image, you must use a POST method with the image attached to
`/convert`. The web service will return an answer like this:


```json
{
    "id": "37d2bb01-7dca-4ef5-9b55-91a31a9bg234",
    "status": "CREATED"
}
```

You can use httpie for testing:

```
http -f POST http://localhost:9000/convert file@golden\ gate.jpg
```

## Obtaining image status using GET

Calling `/convert/{id}` using GET, you can obtain the current workflow status:

```json
{
    "id": "37d2bb01-7dca-4ef5-9b55-91a31a9bg234",
    "status": "FINISHED",
    "url": "https://greyscalr.s3.amazonaws.com/37d2bb01-7dca-4ef5-9b55-91a31a9bg234"
}
```

Calling `/convert` (without an id!), you can receive a list of all available
image conversions.

## Possible status

* `CREATED`: the image was registered in MongoDB and should be converted soon.
* `CONVERTING`: the image is under conversion to greyscale.
* `UPLOADING`: the image is being uploaded.
* `FINISHED`: the image was converted and uploaded. At this point, a URL should
be available.
* `ERROR`: oops. Something went wrong.

## TO DO

* Automated tests
* Code always can be improved

