package org.krish.learn.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomerServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(CustomerServlet.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain");
		String id = request.getParameter("id");
		if (id != null && !id.isEmpty()) {
			try {
				Long.parseLong(id);
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Invalid customer ID format.");
				return;
			}
			long customerId = Long.parseLong(id);
			String sql = "SELECT * FROM customer WHERE customer_id = ?";
			try (Connection connection = DatabaseConnection.getConnection()) {
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					statement.setLong(1, customerId);
					try (ResultSet result = statement.executeQuery()) {
						if (result.next()) {
							String resultString = "Customer ID: " + result.getLong("customer_id") + ", Name: "
									+ result.getString("first_name") + " " + result.getString("last_name")
									+ ", Email: " + result.getString("email");
							response.getWriter().println(resultString);
						}
					}
				}
			} catch (IllegalStateException e) {
				LOGGER.log(Level.SEVERE, "Database configuration is missing while retrieving customers", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter()
						.println("Database configuration is missing. Check the application server environment.");
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Unable to retrieve customers", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Unable to retrieve customers");
			}
		} else {
			String sql = "SELECT * FROM customer";
			try (Connection connection = DatabaseConnection.getConnection()) {
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					try (ResultSet result = statement.executeQuery()) {
						while (result.next()) {
							String resultString = "Customer ID: " + result.getLong("customer_id") + ", Name: "
									+ result.getString("first_name") + " " + result.getString("last_name")
									+ ", Email: " + result.getString("email");
							response.getWriter().println(resultString);
						}
					}
				}
			} catch (IllegalStateException e) {
				LOGGER.log(Level.SEVERE, "Database configuration is missing while retrieving customers", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter()
						.println("Database configuration is missing. Check the application server environment.");
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Unable to retrieve customers", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Unable to retrieve customers");
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String firstName = request.getParameter("first_name");
		String lastName = request.getParameter("last_name");
		String email = request.getParameter("email");

		String sql = "INSERT INTO customer (first_name, last_name, email) VALUES (?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, firstName);
				statement.setString(2, lastName);
				statement.setString(3, email);
				int rowsInserted = statement.executeUpdate();
				if (rowsInserted > 0) {
					response.getWriter().println("Customer added successfully.");
				} else {
					response.getWriter().println("Failed to add customer.");
				}
			}
		} catch (IllegalStateException e) {
			LOGGER.log(Level.SEVERE, "Database configuration is missing while adding a customer", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter()
					.println("Database configuration is missing. Check the application server environment.");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Unable to add customer", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to add customer");
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("id");
		String firstName = request.getParameter("first_name");
		String lastName = request.getParameter("last_name");
		String email = request.getParameter("email");

		if (id == null || id.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Customer ID is required for update.");
			return;
		}

		try {
			Long.parseLong(id);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid customer ID format.");
			return;
		}
		long customerId = Long.parseLong(id);
		String sql = "UPDATE customer SET first_name = ?, last_name = ?, email = ? WHERE customer_id = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, firstName);
				statement.setString(2, lastName);
				statement.setString(3, email);
				statement.setLong(4, customerId);
				int rowsUpdated = statement.executeUpdate();
				if (rowsUpdated > 0) {
					response.getWriter().println("Customer updated successfully.");
				} else {
					response.getWriter().println("No customer found with the given ID.");
				}
			}
		} catch (IllegalStateException e) {
			LOGGER.log(Level.SEVERE, "Database configuration is missing while updating a customer", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter()
					.println("Database configuration is missing. Check the application server environment.");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Unable to update customer", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to update customer");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("id");
		if (id == null || id.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Customer ID is required for deletion.");
			return;
		}
		try {
			Long.parseLong(id);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid customer ID format.");
			return;
		}
		long customerId = Long.parseLong(id);
		String sql = "DELETE FROM customer WHERE customer_id = ?";

		try (Connection connection = DatabaseConnection.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setLong(1, customerId);
				int rowsDeleted = statement.executeUpdate();
				if (rowsDeleted > 0) {
					response.getWriter().println("Customer deleted successfully.");
				} else {
					response.getWriter().println("No customer found with the given ID.");
				}
			}
		} catch (IllegalStateException e) {
			LOGGER.log(Level.SEVERE, "Database configuration is missing while deleting a customer", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter()
					.println("Database configuration is missing. Check the application server environment.");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Unable to delete customer", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Unable to delete customer");
		}
	}
}
