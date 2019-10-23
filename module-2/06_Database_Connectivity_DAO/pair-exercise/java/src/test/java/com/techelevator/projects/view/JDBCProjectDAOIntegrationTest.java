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
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

public class JDBCProjectDAOIntegrationTest {

	private static SingleConnectionDataSource projects;
	private static JDBCProjectDAO dao;

	@BeforeClass // Run this on the start of all our testing (just once before anything)
	public static void setupDataSource() {
		projects = DBTestHelper.getConnection();
		
		dao = DBTestHelper.getProjectDAO();							  // object that allows us to access database
	}

	@AfterClass // Run this at the end of all our testing (just once after everything)
	public static void closeDataSource() {
		projects.destroy();											  // close our connection 
	}

	@After // Run this after every test
	public void rollback() throws SQLException {
		projects.getConnection().rollback();						  // Rollback our transaction so nothing is saved
	}

	@Test
	public void getting_active_new_projects() {
		List<Project> oldProjects = dao.getAllActiveProjects();		  // using the DAO, get the active projects
		
		// we'll add a new active project into the database directly without the DAO
		DBTestHelper.makeFakeProject("Project Veritas", "2009-01-25", "2030-04-24");
		
		List<Project> newProjects = dao.getAllActiveProjects();		  // using the DAO, get the projects including
																	  // the one we added manually
		// make sure the newProjects is higher now
		Assert.assertEquals(oldProjects.size() + 1, newProjects.size());
	}

	@Test
	public void remove_and_add_employee_from_project() {
		// we'll add a fake project and get the project ID
		long projectID = DBTestHelper.makeFakeProject("Project Unicorn", "2009-01-25", "2030-04-24");
		// repeat the above with employee
		long employeeID = DBTestHelper.makeFakeEmployee("Mark", "Jones", "1975-05-10", 'M', "2008-03-11");
		
		// ask the DAO to assign the employee to the project using our new employee_id and project_id
		dao.addEmployeeToProject(projectID, employeeID);
		
		// now let's double check it
		// we make a new employee DAO
		JDBCEmployeeDAO newEmployee = DBTestHelper.getEmployeeDAO();
		// a list of all the employees named Mark Jones
		List<Employee> newPersonList = newEmployee.searchEmployeesByName("Mark", "Jones");
		// let's loop through newPersonList to make sure the ID matches (
		Employee employeeByName = DBTestHelper.findEmployeeInListByID(newPersonList, employeeID); 
		// make sure we found it in the loop
		Assert.assertNotNull(employeeByName);
		// a list of all employees on the project project_id
		List<Employee> newProjectList = newEmployee.getEmployeesByProjectId(projectID);
		// let's loop through newProjectList to make sure the ID matches
		Employee employeeByProject = DBTestHelper.findEmployeeInListByID(newProjectList, employeeID);
		// make sure we found it in the loop
		Assert.assertNotNull(employeeByProject);
		// make sure those two employees are the same person
		DBTestHelper.assertEmployeesAreEqual(employeeByName, employeeByProject);
		
		// now let's remove from project
		dao.removeEmployeeFromProject(projectID, employeeID);
		// the newProjectList should now be empty
		newProjectList = newEmployee.getEmployeesByProjectId(projectID);
		Assert.assertEquals(0, newProjectList.size());
	}
}