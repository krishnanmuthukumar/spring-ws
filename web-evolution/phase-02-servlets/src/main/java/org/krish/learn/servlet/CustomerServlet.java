package org.krish.learn.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private String id;
	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("Servlet initialized. Instance ID:" + System.identityHashCode(this));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println(
				"Instance ID: " + System.identityHashCode(this) + " | Thread: " + Thread.currentThread().getName());
		String id = request.getParameter("id");

		if (id == null || id.isBlank()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required");
			return;
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String name = "Customer-" + id;
		String email = "customer" + id + "@example.com";
		String json = """
				{
					"id" : "%s",
					"name" : "%s",
					"email" : "%s",
					"status" : "ACTIVE"
				}
				""".formatted(id, name, email);

		response.getWriter().write(json);
	}

	@Override
	public void destroy() {
		super.destroy();
		System.out.println("Servlet Destroyed. Instance ID:" + System.identityHashCode(this));
	}
}