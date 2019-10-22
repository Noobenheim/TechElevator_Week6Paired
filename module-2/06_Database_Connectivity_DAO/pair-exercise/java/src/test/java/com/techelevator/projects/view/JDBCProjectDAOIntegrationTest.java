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

import com.techelevator.city.City;
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
		Assert.assertEquals(oldProjects.size() + 1, newProjects.size());

	}

	@Test
	public void remove_employee_from_project() {
		String sqlInsertProject = "INSERT INTO project (project_id, name, from_date, to_date) VALUES(?,?,?,?)";
		long project_id = getNextProjectId();
		String sqlInsertEmployee = "INSERT INTO employee (employee_id,first_name,last_name,birth_date,gender,hire_date) VALUES(?,?,?,?,?,?)";
		long employee_id = getNextEmployeeId();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		jdbcTemplate.update(sqlInsertProject,project_id, "Project Unicorn",Date.valueOf("2009-01-25"),Date.valueOf("2030-4-24"));
		jdbcTemplate.update(sqlInsertEmployee, employee_id, "Mark", "Jones", Date.valueOf("1975-05-10"), 'M', Date.valueOf("2008-03-11"));
		dao.addEmployeeToProject(project_id, employee_id);
		JDBCEmployeeDAO newEmployee = new JDBCEmployeeDAO(projects);
		List<Employee> newPersonList = newEmployee.searchEmployeesByName("Mark", "Jones");
		List<Employee> newProjectList = newEmployee.getEmployeesByProjectId(project_id);
		assertEmployeesAreEqual(newPersonList.get(0), newProjectList.get(0));
		dao.removeEmployeeFromProject(project_id, employee_id);
		newProjectList = newEmployee.getEmployeesByProjectId(project_id);
		Assert.assertEquals(0, newProjectList.size());
	}

	private long getNextProjectId() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_project_id')");
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