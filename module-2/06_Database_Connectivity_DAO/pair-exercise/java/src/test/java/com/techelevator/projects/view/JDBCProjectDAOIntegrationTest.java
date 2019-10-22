package com.techelevator.projects.view;

import java.sql.Date;
import java.sql.SQLException;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;


import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

public class JDBCProjectDAOIntegrationTest {

	private static SingleConnectionDataSource projects;
	private JDBCProjectDAO dao;

	@BeforeClass
	public static void setupDataSource() {
		projects = new SingleConnectionDataSource();
		projects.setUrl("jdbc:postgresql://localhost:5432/projects");
		projects.setUsername("postgres");
		projects.setPassword("postgres1");

		projects.setAutoCommit(false);

	}

	@AfterClass
	public static void closeDataSource() {
		projects.destroy();

	}

	@After
	public void walmart() throws SQLException {
		projects.getConnection().rollback();
	}

	@Before
	public void makeDao() {
		dao = new JDBCProjectDAO(projects);// object that allows us to access database
	}

	@Test
	public void getting_active_new_projects() {
		// create helper method that inserts new project into database
		List<Project> oldProjects = dao.getAllActiveProjects();// using the object created above to test
		//              ^---- contains the old projects, let's say there's 3 old projects
		
		// we'll add a new active project into the database directly without the DAO
		String sqlInsert = "INSERT INTO project (name, from_date, to_date) VALUES(?,?,?)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		jdbcTemplate.update(sqlInsert, "Project Veritas", Date.valueOf("2009-01-25"), Date.valueOf("2030-4-24"));// this
																													// is
																													// actually
																													// being
																													// inserted
																													// if
																													// we
																													// ran
																													// the
																													// test
																													// now
		List<Project> newProjects = dao.getAllActiveProjects();
		//              ^---- contains the new project list, hopefully with our newly added project
		//					  If oldProjects was 3 we're hoping it's 4 now
		Assert.assertEquals(oldProjects.size() + 1, newProjects.size());
	}

	@Test
	public void remove_employee_from_project() {
		// ARRANGE
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects); // our own "SQL Commander"
		// make our query, put question marks for the values
		String sqlInsertProject = "INSERT INTO project (project_id, name, from_date, to_date) VALUES(?,?,?,?)";
		// get the project ID
		long project_id = getNextProjectId();
		// run the query
		jdbcTemplate.update(sqlInsertProject,project_id, "Project Unicorn",Date.valueOf("2009-01-25"),Date.valueOf("2030-4-24"));
		
		// repeat the above with employee
		String sqlInsertEmployee = "INSERT INTO employee (employee_id,first_name,last_name,birth_date,gender,hire_date) VALUES(?,?,?,?,?,?)";
		long employee_id = getNextEmployeeId();
		jdbcTemplate.update(sqlInsertEmployee, employee_id, "Mark", "Jones", Date.valueOf("1975-05-10"), 'M', Date.valueOf("2008-03-11"));
		
		
		
		
		// ACT
		// ask the DAO to assign the employee to the project using our new employee_id and project_id
		dao.addEmployeeToProject(project_id, employee_id);
		
		
		// ASSERT
		// now let's double check it
		// we make a new employee dao
		JDBCEmployeeDAO newEmployee = new JDBCEmployeeDAO(projects);
		// a list of all the employees named Mark Jones
		List<Employee> newPersonList = newEmployee.searchEmployeesByName("Mark", "Jones");
		// a list of all employees on the project project_id
		List<Employee> newProjectList = newEmployee.getEmployeesByProjectId(project_id);
		// make sure those two employees are the same person
		assertEmployeesAreEqual(newPersonList.get(0), newProjectList.get(0));
		
		
		
		
		
		
		
		dao.removeEmployeeFromProject(project_id, employee_id);
		newProjectList = newEmployee.getEmployeesByProjectId(project_id);
		Assert.assertEquals(0, newProjectList.size());
	}

	private long getNextProjectId() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects); // SQL Commander
		String sql = "SELECT nextval('seq_project_id')"; // query
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet(sql);
		if (nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new project");
		}

	}

	private long getNextEmployeeId() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_employee_id')");
		if (nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new employee");
		}

		
	}
	private void assertEmployeesAreEqual(Employee expected, Employee actual) {
		Assert.assertEquals(expected.getId(), actual.getId());
		Assert.assertEquals(expected.getDepartmentId(), actual.getDepartmentId());
		Assert.assertEquals(expected.getFirstName(), actual.getFirstName());
		Assert.assertEquals(expected.getLastName(), actual.getLastName());
		Assert.assertEquals(expected.getBirthDay(), actual.getBirthDay());
		Assert.assertEquals(expected.getGender(), actual.getGender());
		Assert.assertEquals(expected.getHireDate(), actual.getHireDate());
	}
	
}