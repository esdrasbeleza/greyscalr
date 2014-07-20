# Greyscalr

This is a dummy project written in Scala. Its main use is to study Scala and
some related Stuff, like Play and Akka.

It's a Play application that will run a webservice that receives a image via
POST and converts it to a greyscale image.

## Uploading an image using POST

To upload an image, you must use a POST method with an attached image to
`/convert`. The web service will return this answer like this:

```json
{
    "id": "53cc0c70c2e67df003ba26e2",
    "status": "CREATED"
}
```

## Obtaining image status using GET

Calling `/convert/{id}` using GET, you can obtain the current workflow status:

```json
{
    "id": "53cc0c70c2e67df003ba26e2",
    "status": "IMAGE READY"
}
```

## Possible status

To be documented 

## Final status

To be documented

