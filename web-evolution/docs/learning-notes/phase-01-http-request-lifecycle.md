 
## Get Request
    The field values were sent as query parameters for GET request. Since GET form submissions have no request body, the content type header is not set. The query parameters are visible in the URL.

## Post Request
    The field values were sent as form data for POST request. Content type header is set to `application/x-www-form-urlencoded` for POST request. The form data is not visible in the URL.

## Reflection, deliverables, and checklist

### Which component creates the request and response?
    Browser creates the request and server creates the response. 
    
    Browser -> HTTP Request -> Local Server
    Browser <- HTTP Response <- Local Server
### Why can a GET be bookmarked while POST normally is not?
    GET request parameters are visible in the URL, so they can be bookmarked. POST request parameters are sent in the request body, so they cannot be bookmarked.
    