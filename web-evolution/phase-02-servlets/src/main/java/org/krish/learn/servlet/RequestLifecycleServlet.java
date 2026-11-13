package org.krish.learn.servlet;

import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Demonstrates request data, response metadata, and servlet lifecycle callbacks. */
public class RequestLifecycleServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        getServletContext().log("RequestLifecycleServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        writeResponse(response, "GET", request.getParameter("name"), null,
                request.getHeader("User-Agent"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        writeResponse(response, "POST", request.getParameter("name"),
                request.getParameter("message"), request.getHeader("User-Agent"));
    }

    private void writeResponse(HttpServletResponse response, String method,
            String name, String message, String userAgent) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("X-Servlet-Exercise", "requests-responses-lifecycle");

        response.getWriter().printf("""
                <!doctype html>
                <html lang="en">
                <head><meta charset="UTF-8"><title>Request details</title></head>
                <body>
                  <h1>Request details</h1>
                  <dl>
                    <dt>Method</dt><dd>%s</dd>
                    <dt>Name</dt><dd>%s</dd>
                    <dt>Message</dt><dd>%s</dd>
                    <dt>User-Agent</dt><dd>%s</dd>
                  </dl>
                </body>
                </html>
                """,
                method, name, message, userAgent);
    }

    @Override
    public void destroy() {
        getServletContext().log("RequestLifecycleServlet destroyed");
        super.destroy();
    }
}
