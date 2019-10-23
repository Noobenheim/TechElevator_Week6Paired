package com.techelevator.projects.view;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

public final class DBTestHelper {
	private static SingleConnectionDataSource projects;
	private static JDBCProjectDAO projectDAO;
	private static JDBCEmployeeDAO employeeDAO;
	private static JDBCDepartmentDAO departmentDAO;
	private static JdbcTemplate jdbcTemplate;
	
	static {
		setup();
	}
	private static void setup() {
		projects = new SingleConnectionDataSource(); // create database connection object
		projects.setUrl("jdbc:postgresql://localhost:5432/projects"); 			// connect it to our local database
		projects.setUsername("postgres");									    // set the username
		projects.setPassword("postgres1");									    // set the password
		
		projects.setAutoCommit(false);			// make this connection a transaction so we
												// can rollback
		
		projectDAO = new JDBCProjectDAO(projects);
		employeeDAO = new JDBCEmployeeDAO(projects);
		departmentDAO = new JDBCDepartmentDAO(projects);
		
		jdbcTemplate = new JdbcTemplate(projects);
	}
	
	public static final SingleConnectionDataSource getConnection() {
		try {
			if( projects.getConnection().isClosed() ) {
				setup();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return projects;
	}
	public static final JDBCProjectDAO getProjectDAO() {
		return projectDAO;
	}
	public static final JDBCEmployeeDAO getEmployeeDAO() {
		return employeeDAO;
	}
	public static final JDBCDepartmentDAO getDepartmentDAO() {
		return departmentDAO;
	}
	
	private static long getNextProjectId() {
		String sql = "SELECT nextval('seq_project_id')"; // query
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet(sql);
		if (nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new project");
		}
	}

	private static long getNextEmployeeId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_employee_id')");
		if (nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new employee");
		}
	}
	
	private static long getNextDepartmentId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_department_id')");
		if (nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new department");
		}

	}
	
	public static void assertEmployeesAreEqual(Employee expected, Employee actual) {
		Assert.assertEquals(expected.getId(), actual.getId());
		Assert.assertEquals(expected.getDepartmentId(), actual.getDepartmentId());
		Assert.assertEquals(expected.getFirstName(), actual.getFirstName());
		Assert.assertEquals(expected.getLastName(), actual.getLastName());
		Assert.assertEquals(expected.getBirthDay(), actual.getBirthDay());
		Assert.assertEquals(expected.getGender(), actual.getGender());
		Assert.assertEquals(expected.getHireDate(), actual.getHireDate());
	}
	
	public static long makeFakeProject(String name) {
		return makeFakeProject(name, null, null);
	}
	public static long makeFakeProject(String name, String from_date, String to_date) {
		long projectID = getNextProjectId();

		String sqlInsert = "INSERT INTO project (project_id, name, from_date, to_date) VALUES(?,?,?,?)";
		jdbcTemplate.update(sqlInsert, projectID, name, from_date==null?null:Date.valueOf(from_date), to_date==null?null:Date.valueOf(to_date));
		
		return projectID;
	}
	
	public static long makeFakeDepartment(String name) {
		long departmentID = getNextDepartmentId();
		
		String sqlInsert = "INSERT INTO department (department_id, name) VALUES(?, ?)";
		jdbcTemplate.update(sqlInsert, departmentID, name);
		
		return departmentID;
	}
	
	public static long makeFakeEmployee(String firstName, String lastName, String birthDate, char gender, String hireDate) {
		long employeeID = getNextEmployeeId();
		
		String sqlInsertEmployee = "INSERT INTO employee (employee_id,first_name,last_name,birth_date,gender,hire_date) VALUES(?,?,?,?,?,?)";
		jdbcTemplate.update(sqlInsertEmployee, employeeID, firstName, lastName, Date.valueOf(birthDate), gender, Date.valueOf(hireDate));
		
		return employeeID;
	}
	
	public static Employee findEmployeeInListByID(List<Employee> list, long id) {
		Employee found = null;
		
		for( Employee e : list ) {
			if( e.getId() == id ) {
				found = e;
			}
		}
		
		return found;
	}
}
