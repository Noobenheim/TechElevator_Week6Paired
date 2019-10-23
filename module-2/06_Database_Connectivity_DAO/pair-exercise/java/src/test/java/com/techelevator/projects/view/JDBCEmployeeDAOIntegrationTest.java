package com.techelevator.projects.view;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

public class JDBCEmployeeDAOIntegrationTest {

	private static SingleConnectionDataSource projects;
	private static JDBCEmployeeDAO dao;

	@BeforeClass
	public static void setupDataSource() {
		projects = DBTestHelper.getConnection();
		dao = DBTestHelper.getEmployeeDAO();
	}

	@AfterClass
	public static void closeDataSource() {
		projects.destroy();
	}

	@After
	public void walmart() throws SQLException {
		projects.getConnection().rollback();
	}

	@Test
	public void get_all_employees() {
		List<Employee> oldEmployees = dao.getAllEmployees();
		
		DBTestHelper.makeFakeEmployee("Jim", "Jones", "1975-05-10", 'M', "2008-03-11");

		List<Employee> newEmployees = dao.getAllEmployees();
		Assert.assertEquals(oldEmployees.size() + 1, newEmployees.size());

	}

	@Test
	public void search_employees_by_name() {
		List<Employee> oldEmployees = dao.searchEmployeesByName("Jim", "Jones");

		DBTestHelper.makeFakeEmployee("Jim", "Jones", "1975-05-10", 'M', "2008-03-11");

		List<Employee> newEmployees = dao.searchEmployeesByName("Jim", "Jones");
		Assert.assertEquals(oldEmployees.size() + 1, newEmployees.size());

	}

	@Test
	public void get_employees_without_projects() {
		List<Employee> oldEmployees = dao.getEmployeesWithoutProjects();

		DBTestHelper.makeFakeEmployee("Jim", "Jones", "1975-05-10", 'M', "2008-03-11");

		List<Employee> newEmployees = dao.getEmployeesWithoutProjects();
		Assert.assertEquals(oldEmployees.size() + 1, newEmployees.size());

	}

	@Test
	public void get_employees_by_department_id() {
		long employeeID = DBTestHelper.makeFakeEmployee("Mark", "Jones", "1975-05-10", 'M', "2008-03-11");
		
		long departmentID = DBTestHelper.makeFakeDepartment("Quality Assurance");

		dao.changeEmployeeDepartment(employeeID, departmentID);

		List<Employee> employeesByDepartment = dao.getEmployeesByDepartmentId(departmentID);
		Employee employeeByDepartment = DBTestHelper.findEmployeeInListByID(employeesByDepartment, employeeID);
		Assert.assertNotNull(employeeByDepartment);
		
		List<Employee> employeesByName = dao.searchEmployeesByName("Mark", "Jones");
		Employee employeeByName = DBTestHelper.findEmployeeInListByID(employeesByName, employeeID);
		Assert.assertNotNull(employeeByName);
		
		DBTestHelper.assertEmployeesAreEqual(employeeByName, employeeByDepartment);
	}

	@Test
	public void get_employees_by_project_id() {
		long employeeID = DBTestHelper.makeFakeEmployee("Mark", "Jones", "1975-05-10", 'M', "2008-03-11");
		
		long projectID = DBTestHelper.makeFakeProject("Operation Northwoods");
		
		List<Employee> employeesByProjectOld = dao.getEmployeesByProjectId(projectID);

		JDBCProjectDAO projectDAO = DBTestHelper.getProjectDAO();
		projectDAO.addEmployeeToProject(projectID, employeeID);
		
		List<Employee> employeesByProjectNew = dao.getEmployeesByProjectId(projectID);
		
		Assert.assertEquals(employeesByProjectOld.size() + 1, employeesByProjectNew.size());
	}
}
