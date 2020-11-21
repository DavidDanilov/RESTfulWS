package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet
 * 
 * Implements GET, POST, PATCH and DELETE methods of RESTful Web Service for interaction with MS
 * SQL DB
 * 
 */

@Path("/")
public class App {

	@GET
	@Path("/employees")
	// Type of Response body
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings({ "unchecked" })
	public Response getEmployees() {

		// Returned Object
		JSONArray result = new JSONArray();
		// Returned Status Code
		int statusCode = 404;

		// Set of non-string SQL data type
		Set<String> numericSqlDataTypes = new HashSet<>(
				Arrays.asList("int", "bigint", "decimal", "numeric", "smallint", "tinyint", "float", "real"));

		// SQL Query Constructor
		StringBuilder selectSql = new StringBuilder("SELECT * FROM Employees");

		// Connection to DB
		try (Connection connection = DataSource.dataSourceFactory().getConnection();
				Statement statement = connection.createStatement();) {
			// Result of query execution
			ResultSet resultSet = statement.executeQuery(selectSql.toString());
			// Get Result Meta Data
			ResultSetMetaData metaData = resultSet.getMetaData();
			// If Result isn't empty
			while (resultSet.next()) {
				statusCode = 200;
				// Element of Returned Object (JSON)
				JSONObject json = new JSONObject();
				// Returned Object (JSON) constructor
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					// Add Field Value to Response Object depends of Value Type
					if (numericSqlDataTypes.contains(metaData.getColumnTypeName(i)))
						json.put(metaData.getColumnName(i), resultSet.getInt(i));
					else
						json.put(metaData.getColumnName(i), resultSet.getString(i));
				}
				result.add(json);
			}
			// Cannot connect to DB
		} catch (SQLException e) {
			e.printStackTrace();
			statusCode = 401;
		}
		return Response.status(statusCode).entity(result.toString()).build();
	}

	@GET
	@Path("/employee")
	// Type of Response body
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings({ "unchecked", })
	public Response getEmployeeById(@DefaultValue("-1") @QueryParam("id") String id) {

		// Returned Object
		JSONArray result = new JSONArray();
		// Returned Status Code
		int statusCode = 400;

		if (id.equals("-1") || !id.matches("\\d+"))
			return Response.status(statusCode).entity(result.toString()).build();

		// Set of non-string SQL data type
		Set<String> numericSqlDataTypes = new HashSet<>(
				Arrays.asList("int", "bigint", "decimal", "numeric", "smallint", "tinyint", "float", "real"));

		// SQL Query Constructor
		StringBuilder selectSql = new StringBuilder("SELECT * FROM Employees WHERE id = " + id);

		// Connection to DB
		try (Connection connection = DataSource.dataSourceFactory().getConnection();
				Statement statement = connection.createStatement();) {
			// Result of query execution
			ResultSet resultSet = statement.executeQuery(selectSql.toString());
			// Get Result Meta Data
			ResultSetMetaData metaData = resultSet.getMetaData();
			// If Result is empty
			statusCode = 404;
			// If Result isn't empty
			while (resultSet.next()) {
				statusCode = 200;
				// Element of Returned Object (JSON)
				JSONObject json = new JSONObject();
				// Returned Object (JSON) constructor
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					// Add Field Value to Response Object depends of Value Type
					if (numericSqlDataTypes.contains(metaData.getColumnTypeName(i)))
						json.put(metaData.getColumnName(i), resultSet.getInt(i));
					else
						json.put(metaData.getColumnName(i), resultSet.getString(i));
				}
				result.add(json);
			}
			// Cannot connect to DB
		} catch (SQLException e) {
			e.printStackTrace();
			statusCode = 400;
		}
		return Response.status(statusCode).entity(result.toString()).build();
	}

	@POST
	@Path("/employee")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postEmployee(@DefaultValue("-1") @QueryParam("id") String id,
			@DefaultValue("") @QueryParam("first_name") String first_name,
			@DefaultValue("") @QueryParam("last_name") String last_name) {
		// Returned Status Code
		int statusCode = 400;
		String result = "";

		if (id.equals("-1") || !id.matches("\\d+"))
			return Response.status(statusCode).entity("ID should be a positive integer").build();
		if (first_name.length() == 0 || !first_name.matches("^[A-Z]+[a-z]*$"))
			return Response.status(statusCode)
					.entity("First Name can not be empty, can contain only letters and need to start with capital leter").build();
		if (last_name.length() == 0 || !last_name.matches("^[A-Z]+[a-z]*$"))
			return Response.status(statusCode)
					.entity("Last Name can not be empty, can contain only letters and need to start with capital leter")
					.build();

		// Connection to DB
		try (Connection connection = DataSource.dataSourceFactory().getConnection();
				Statement statement = connection.createStatement();) {

			// SQL Insert-Query Constructor
			StringBuilder insertSql = new StringBuilder(
					"INSERT INTO Employees VALUES (" + id + ",'" + first_name + "','" + last_name + "')");
			// If Insert-query was successful
			if (statement.executeUpdate(insertSql.toString()) == 1) {
				statusCode = 201;
				result = "Created";
			}

			// Cannot connect to DB
		} catch (SQLException e) {
			statusCode = 400;
			result = "Can not insert the record. The record already exists.";
		}
		return Response.status(statusCode).entity(result).build();
	}

	@PATCH
	@Path("/employee")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateEmployee(@DefaultValue("-1") @QueryParam("id") String id,
			@DefaultValue("") @QueryParam("first_name") String first_name,
			@DefaultValue("") @QueryParam("last_name") String last_name) {
		// Returned Status Code
		int statusCode = 400;
		String result = "";
		
		if (id.equals("-1") || !id.matches("\\d+"))
			return Response.status(statusCode).entity("ID should be a positive integer").build();
		if (first_name.length() != 0 && !first_name.matches("^[A-Z]+[a-z]*$"))
			return Response.status(statusCode)
					.entity("First Name can contain only letters and need to start with capital leter").build();
		if (last_name.length() != 0 && !last_name.matches("^[A-Z]+[a-z]*$"))
			return Response.status(statusCode)
					.entity("Last Name can contain only letters and need to start with capital leter")
					.build();

		// Connection to DB
		try (Connection connection = DataSource.dataSourceFactory().getConnection();
				Statement statement = connection.createStatement();) {

			// SQL Insert-Query Constructor
			StringBuilder updateSql = new StringBuilder("UPDATE Employees SET ");
			if (first_name.length() != 0)
				updateSql.append("first_name = '" + first_name);
			if (first_name.length() != 0 && last_name.length() != 0)
				updateSql.append("', ");
			if (last_name.length() != 0)
				updateSql.append("last_name = '" + last_name);
			updateSql.append("' WHERE id = " + id);
			
			statusCode = 404;
			result = "Can not update the record. The record doesn't exist.";
			
			// If Insert-query was successful
			if (statement.executeUpdate(updateSql.toString()) == 1) {
				statusCode = 200;
				result = "Updated";
			}

			// Cannot connect to DB
		} catch (SQLException e) {
			statusCode = 400;
			result = "Check availability and credentials of Database";
		}
		return Response.status(statusCode).entity(result).build();
	}

	@DELETE
	@Path("/employee")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteEmployee(@DefaultValue("-1") @QueryParam("id") String id) {
		// Returned Status Code
		int statusCode = 400;
		String result = "ID should be a positive integer";

		if (id.equals("-1") || !id.matches("\\d+"))
			return Response.status(statusCode).entity(result).build();

		// Connection to DB
		try (Connection connection = DataSource.dataSourceFactory().getConnection();
				Statement statement = connection.createStatement();) {

			// SQL Insert-Query Constructor
			StringBuilder deleteSql = new StringBuilder("DELETE FROM Employees WHERE id = " + id);

			statusCode = 404;
			result = "Can not delete the record. The record doesn't exist";

			// If Delete query was successful
			if (statement.executeUpdate(deleteSql.toString()) == 1) {
				statusCode = 200;
				result = "Deleted";
			}

			// Cannot connect to DB
		} catch (SQLException e) {
			e.printStackTrace();
			statusCode = 401;
			result = "Check availability and credentials of Database";
		}
		return Response.status(statusCode).entity(result).build();
	}

}
