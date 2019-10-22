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

public class JDBCEmployeeDAOIntegrationTest {

	private static SingleConnectionDataSource projects;
	private JDBCEmployeeDAO dao;

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
		dao = new JDBCEmployeeDAO(projects);// object that allows us to access database
	}

	@Test
	public void get_all_employees() {
		List<Employee> oldEmployees = dao.getAllEmployees();
		String sqlInsert = "INSERT INTO employee (first_name,last_name,birth_date,gender,hire_date) VALUES(?,?,?,?,?)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		jdbcTemplate.update(sqlInsert, "Jim", "Jones", Date.valueOf("1975-05-10"), 'M', Date.valueOf("2008-03-11"));

		List<Employee> newEmployees = dao.getAllEmployees();
		Assert.assertEquals(oldEmployees.size() + 1, newEmployees.size());

	}

	@Test
	public void search_employees_by_name() {
		List<Employee> oldEmployees = dao.searchEmployeesByName("Jim", "Jones");
		String sqlInsert = "INSERT INTO employee (first_name,last_name,birth_date,gender,hire_date) VALUES(?,?,?,?,?)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		jdbcTemplate.update(sqlInsert, "Jim", "Jones", Date.valueOf("1975-05-10"), 'M', Date.valueOf("2008-03-11"));

		List<Employee> newEmployees = dao.searchEmployeesByName("Jim", "Jones");
		Assert.assertEquals(oldEmployees.size() + 1, newEmployees.size());

	}

	@Test
	public void get_employees_without_projects() {
		List<Employee> oldEmployees = dao.getEmployeesWithoutProjects();
		String sqlInsert = "INSERT INTO employee (first_name,last_name,birth_date,gender,hire_date) VALUES(?,?,?,?,?)";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		jdbcTemplate.update(sqlInsert, "Jim", "Jones", Date.valueOf("1975-05-10"), 'M', Date.valueOf("2008-03-11"));

		List<Employee> newEmployees = dao.getEmployeesWithoutProjects();
		Assert.assertEquals(oldEmployees.size() + 1, newEmployees.size());

	}

	@Test
	public void get_employees_by_department_id() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		String sqlInsertEmployee = "INSERT INTO employee (employee_id, first_name,last_name,birth_date,gender,hire_date) VALUES (?,?,?,?,?,?)";
		long employee_id = getNextEmployeeId();// make helper method
		jdbcTemplate.update(sqlInsertEmployee, employee_id, "Mark", "Jones", Date.valueOf("1975-05-10"), 'M',
				Date.valueOf("2008-03-11"));

		String sqlInsertDepartment = "INSERT INTO department (department_id, name) VALUES (?,?)";
		long department_id = getNextDepartmentId();// make helper method
		jdbcTemplate.update(sqlInsertDepartment, department_id, "Quality Assurance");
		dao.changeEmployeeDepartment(employee_id, department_id);

		List<Employee> newDepartmentos = dao.getEmployeesByDepartmentId(department_id);
		List<Employee> newPersonList = dao.searchEmployeesByName("Mark", "Jones");
		assertEmployeesAreEqual(newPersonList.get(0), newDepartmentos.get(0));
	}

	@Test
	public void get_employees_by_project_id() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		String sqlInsertEmployee = "INSERT INTO employee (employee_id, first_name,last_name,birth_date,gender,hire_date) VALUES (?,?,?,?,?,?)";
		long employee_id = getNextEmployeeId();// make helper method
		jdbcTemplate.update(sqlInsertEmployee, employee_id, "Mark", "Jones", Date.valueOf("1975-05-10"), 'M',
				Date.valueOf("2008-03-11"));

		String sqlInsertProject = "INSERT INTO project (project_id, name) VALUES (?,?)";
		long project_id = getNextProjectId();// make helper method
		jdbcTemplate.update(sqlInsertProject, project_id, "Operation Northwoods");
		List<Employee> oldProjectos = dao.getEmployeesByProjectId(project_id);
		JDBCProjectDAO projectDao = new JDBCProjectDAO(projects);
		projectDao.addEmployeeToProject(project_id, employee_id);
		List<Employee> newProjectos = dao.getEmployeesByProjectId(project_id);
		Assert.assertEquals(oldProjectos.size() + 1, newProjectos.size());
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

	private long getNextEmployeeId() {// helper method to get employees by department id
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_employee_id')");
		if (nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new employee");
		}

	}

	private long getNextDepartmentId() {// helper method to get employees by department id
		JdbcTemplate jdbcTemplate = new JdbcTemplate(projects);
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_department_id')");
		if (nextIdResult.next()) {
			long nextID = nextIdResult.getLong(1);
			return nextID;
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new department");
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
