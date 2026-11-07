
## Exercise 1

This exercise is a minimal deployable web application. `src/main/webapp/index.html` is the welcome page, `WEB-INF/web.xml` declares the Servlet 6.0 web application, and `pom.xml` packages the app as a WAR. The Servlet API has `provided` scope because Tomcat supplies it at runtime.

From this directory, build the WAR:

```bash
mvn clean package
```

Copy `target/phase-02-servlets.war` into Tomcat 10.1's `webapps` directory and start Tomcat. Open `http://localhost:8080/phase-02-servlets/`; the welcome page should appear. To undeploy, stop Tomcat and remove both the WAR and its expanded `phase-02-servlets` directory from `webapps`, then copy the WAR back and restart to redeploy.

On Windows, use `copy target\phase-02-servlets.war <TOMCAT_HOME>\webapps\` for deployment. Replace `<TOMCAT_HOME>` with the directory where Tomcat is installed.

## Exercise 2

Added a `HelloServlet` class that extends `HttpServlet`. The servlet is mapped to `/hello` in `WEB-INF/web.xml`. When you visit `http://localhost:8080/phase-02-servlets/hello`, the servlet responds with "Hello from a Servlet!".

Added `HelloWebServlet` class with `@WebServlet("/hello-web")` annotation. When you visit `http://localhost:8080/phase-02-servlets/hello-web`, the servlet responds with "Hello from a Web Servlet!".

Both implementations work independently, demonstrating two ways to map servlets: via `web.xml` and via annotations.