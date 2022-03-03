// DO NOT EDIT.  Make changes to Employee.java instead.
package er.erxtest.model;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;

import er.extensions.eof.*;
import er.extensions.eof.ERXKey.Type;
import er.extensions.foundation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public abstract class _Employee extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Employee";

  // Attribute Keys
  public static final ERXKey<String> ADDRESS1 = new ERXKey<String>("address1", Type.Attribute);
  public static final ERXKey<String> ADDRESS2 = new ERXKey<String>("address2", Type.Attribute);
  public static final ERXKey<java.math.BigDecimal> BEST_SALES_TOTAL = new ERXKey<java.math.BigDecimal>("bestSalesTotal", Type.Attribute);
  public static final ERXKey<String> CITY = new ERXKey<String>("city", Type.Attribute);
  public static final ERXKey<String> FIRST_NAME = new ERXKey<String>("firstName", Type.Attribute);
  public static final ERXKey<String> LAST_NAME = new ERXKey<String>("lastName", Type.Attribute);
  public static final ERXKey<Boolean> MANAGER = new ERXKey<Boolean>("manager", Type.Attribute);
  public static final ERXKey<String> STATE = new ERXKey<String>("state", Type.Attribute);
  public static final ERXKey<String> ZIPCODE = new ERXKey<String>("zipcode", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.erxtest.model.Company> COMPANY = new ERXKey<er.erxtest.model.Company>("company", Type.ToOneRelationship);
  public static final ERXKey<er.erxtest.model.Department> DEPARTMENT = new ERXKey<er.erxtest.model.Department>("department", Type.ToOneRelationship);
  public static final ERXKey<er.erxtest.model.Paycheck> PAYCHECKS = new ERXKey<er.erxtest.model.Paycheck>("paychecks", Type.ToManyRelationship);
  public static final ERXKey<er.erxtest.model.Role> ROLES = new ERXKey<er.erxtest.model.Role>("roles", Type.ToManyRelationship);

  // Attributes
  public static final String ADDRESS1_KEY = ADDRESS1.key();
  public static final String ADDRESS2_KEY = ADDRESS2.key();
  public static final String BEST_SALES_TOTAL_KEY = BEST_SALES_TOTAL.key();
  public static final String CITY_KEY = CITY.key();
  public static final String FIRST_NAME_KEY = FIRST_NAME.key();
  public static final String LAST_NAME_KEY = LAST_NAME.key();
  public static final String MANAGER_KEY = MANAGER.key();
  public static final String STATE_KEY = STATE.key();
  public static final String ZIPCODE_KEY = ZIPCODE.key();

  // Relationships
  public static final String COMPANY_KEY = COMPANY.key();
  public static final String DEPARTMENT_KEY = DEPARTMENT.key();
  public static final String PAYCHECKS_KEY = PAYCHECKS.key();
  public static final String ROLES_KEY = ROLES.key();

  private static final Logger log = LoggerFactory.getLogger(_Employee.class);

  public Employee localInstanceIn(EOEditingContext editingContext) {
    Employee localInstance = (Employee)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String address1() {
    return (String) storedValueForKey(_Employee.ADDRESS1_KEY);
  }

  public void setAddress1(String value) {
    log.debug( "updating address1 from {} to {}", address1(), value);
    takeStoredValueForKey(value, _Employee.ADDRESS1_KEY);
  }

  public String address2() {
    return (String) storedValueForKey(_Employee.ADDRESS2_KEY);
  }

  public void setAddress2(String value) {
    log.debug( "updating address2 from {} to {}", address2(), value);
    takeStoredValueForKey(value, _Employee.ADDRESS2_KEY);
  }

  public java.math.BigDecimal bestSalesTotal() {
    return (java.math.BigDecimal) storedValueForKey(_Employee.BEST_SALES_TOTAL_KEY);
  }

  public void setBestSalesTotal(java.math.BigDecimal value) {
    log.debug( "updating bestSalesTotal from {} to {}", bestSalesTotal(), value);
    takeStoredValueForKey(value, _Employee.BEST_SALES_TOTAL_KEY);
  }

  public String city() {
    return (String) storedValueForKey(_Employee.CITY_KEY);
  }

  public void setCity(String value) {
    log.debug( "updating city from {} to {}", city(), value);
    takeStoredValueForKey(value, _Employee.CITY_KEY);
  }

  public String firstName() {
    return (String) storedValueForKey(_Employee.FIRST_NAME_KEY);
  }

  public void setFirstName(String value) {
    log.debug( "updating firstName from {} to {}", firstName(), value);
    takeStoredValueForKey(value, _Employee.FIRST_NAME_KEY);
  }

  public String lastName() {
    return (String) storedValueForKey(_Employee.LAST_NAME_KEY);
  }

  public void setLastName(String value) {
    log.debug( "updating lastName from {} to {}", lastName(), value);
    takeStoredValueForKey(value, _Employee.LAST_NAME_KEY);
  }

  public Boolean manager() {
    return (Boolean) storedValueForKey(_Employee.MANAGER_KEY);
  }

  public void setManager(Boolean value) {
    log.debug( "updating manager from {} to {}", manager(), value);
    takeStoredValueForKey(value, _Employee.MANAGER_KEY);
  }

  public String state() {
    return (String) storedValueForKey(_Employee.STATE_KEY);
  }

  public void setState(String value) {
    log.debug( "updating state from {} to {}", state(), value);
    takeStoredValueForKey(value, _Employee.STATE_KEY);
  }

  public String zipcode() {
    return (String) storedValueForKey(_Employee.ZIPCODE_KEY);
  }

  public void setZipcode(String value) {
    log.debug( "updating zipcode from {} to {}", zipcode(), value);
    takeStoredValueForKey(value, _Employee.ZIPCODE_KEY);
  }

  public er.erxtest.model.Company company() {
    return (er.erxtest.model.Company)storedValueForKey(_Employee.COMPANY_KEY);
  }

  public void setCompany(er.erxtest.model.Company value) {
    takeStoredValueForKey(value, _Employee.COMPANY_KEY);
  }

  public void setCompanyRelationship(er.erxtest.model.Company value) {
    log.debug("updating company from {} to {}", company(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setCompany(value);
    }
    else if (value == null) {
      er.erxtest.model.Company oldValue = company();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Employee.COMPANY_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Employee.COMPANY_KEY);
    }
  }

  public er.erxtest.model.Department department() {
    return (er.erxtest.model.Department)storedValueForKey(_Employee.DEPARTMENT_KEY);
  }

  public void setDepartment(er.erxtest.model.Department value) {
    takeStoredValueForKey(value, _Employee.DEPARTMENT_KEY);
  }

  public void setDepartmentRelationship(er.erxtest.model.Department value) {
    log.debug("updating department from {} to {}", department(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setDepartment(value);
    }
    else if (value == null) {
      er.erxtest.model.Department oldValue = department();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Employee.DEPARTMENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Employee.DEPARTMENT_KEY);
    }
  }

  public NSArray<er.erxtest.model.Paycheck> paychecks() {
    return (NSArray<er.erxtest.model.Paycheck>)storedValueForKey(_Employee.PAYCHECKS_KEY);
  }

  public NSArray<er.erxtest.model.Paycheck> paychecks(EOQualifier qualifier) {
    return paychecks(qualifier, null, false);
  }

  public NSArray<er.erxtest.model.Paycheck> paychecks(EOQualifier qualifier, boolean fetch) {
    return paychecks(qualifier, null, fetch);
  }

  public NSArray<er.erxtest.model.Paycheck> paychecks(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.erxtest.model.Paycheck> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.erxtest.model.Paycheck.EMPLOYEE_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.erxtest.model.Paycheck.fetchPaychecks(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = paychecks();
      if (qualifier != null) {
        results = (NSArray<er.erxtest.model.Paycheck>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.erxtest.model.Paycheck>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToPaychecks(er.erxtest.model.Paycheck object) {
    includeObjectIntoPropertyWithKey(object, _Employee.PAYCHECKS_KEY);
  }

  public void removeFromPaychecks(er.erxtest.model.Paycheck object) {
    excludeObjectFromPropertyWithKey(object, _Employee.PAYCHECKS_KEY);
  }

  public void addToPaychecksRelationship(er.erxtest.model.Paycheck object) {
    log.debug("adding {} to paychecks relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToPaychecks(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Employee.PAYCHECKS_KEY);
    }
  }

  public void removeFromPaychecksRelationship(er.erxtest.model.Paycheck object) {
    log.debug("removing {} from paychecks relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromPaychecks(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Employee.PAYCHECKS_KEY);
    }
  }

  public er.erxtest.model.Paycheck createPaychecksRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.erxtest.model.Paycheck.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Employee.PAYCHECKS_KEY);
    return (er.erxtest.model.Paycheck) eo;
  }

  public void deletePaychecksRelationship(er.erxtest.model.Paycheck object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Employee.PAYCHECKS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllPaychecksRelationships() {
    Enumeration<er.erxtest.model.Paycheck> objects = paychecks().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePaychecksRelationship(objects.nextElement());
    }
  }

  public NSArray<er.erxtest.model.Role> roles() {
    return (NSArray<er.erxtest.model.Role>)storedValueForKey(_Employee.ROLES_KEY);
  }

  public NSArray<er.erxtest.model.Role> roles(EOQualifier qualifier) {
    return roles(qualifier, null);
  }

  public NSArray<er.erxtest.model.Role> roles(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.erxtest.model.Role> results;
      results = roles();
      if (qualifier != null) {
        results = (NSArray<er.erxtest.model.Role>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.erxtest.model.Role>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }

  public void addToRoles(er.erxtest.model.Role object) {
    includeObjectIntoPropertyWithKey(object, _Employee.ROLES_KEY);
  }

  public void removeFromRoles(er.erxtest.model.Role object) {
    excludeObjectFromPropertyWithKey(object, _Employee.ROLES_KEY);
  }

  public void addToRolesRelationship(er.erxtest.model.Role object) {
    log.debug("adding {} to roles relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToRoles(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Employee.ROLES_KEY);
    }
  }

  public void removeFromRolesRelationship(er.erxtest.model.Role object) {
    log.debug("removing {} from roles relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromRoles(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Employee.ROLES_KEY);
    }
  }

  public er.erxtest.model.Role createRolesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.erxtest.model.Role.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Employee.ROLES_KEY);
    return (er.erxtest.model.Role) eo;
  }

  public void deleteRolesRelationship(er.erxtest.model.Role object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Employee.ROLES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllRolesRelationships() {
    Enumeration<er.erxtest.model.Role> objects = roles().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRolesRelationship(objects.nextElement());
    }
  }


  public static Employee createEmployee(EOEditingContext editingContext, String firstName
, String lastName
, Boolean manager
, er.erxtest.model.Company company) {
    Employee eo = (Employee) EOUtilities.createAndInsertInstance(editingContext, _Employee.ENTITY_NAME);
    eo.setFirstName(firstName);
    eo.setLastName(lastName);
    eo.setManager(manager);
    eo.setCompanyRelationship(company);
    return eo;
  }

  public static ERXFetchSpecification<Employee> fetchSpec() {
    return new ERXFetchSpecification<Employee>(_Employee.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Employee> fetchAllEmployees(EOEditingContext editingContext) {
    return _Employee.fetchAllEmployees(editingContext, null);
  }

  public static NSArray<Employee> fetchAllEmployees(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Employee.fetchEmployees(editingContext, null, sortOrderings);
  }

  public static NSArray<Employee> fetchEmployees(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Employee> fetchSpec = new ERXFetchSpecification<Employee>(_Employee.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Employee> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Employee fetchEmployee(EOEditingContext editingContext, String keyName, Object value) {
    return _Employee.fetchEmployee(editingContext, ERXQ.equals(keyName, value));
  }

  public static Employee fetchEmployee(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Employee> eoObjects = _Employee.fetchEmployees(editingContext, qualifier, null);
    Employee eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Employee that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Employee fetchRequiredEmployee(EOEditingContext editingContext, String keyName, Object value) {
    return _Employee.fetchRequiredEmployee(editingContext, ERXQ.equals(keyName, value));
  }

  public static Employee fetchRequiredEmployee(EOEditingContext editingContext, EOQualifier qualifier) {
    Employee eoObject = _Employee.fetchEmployee(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Employee that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Employee localInstanceIn(EOEditingContext editingContext, Employee eo) {
    Employee localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<er.erxtest.model.Employee> fetchPlebs(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("plebs", _Employee.ENTITY_NAME);
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<er.erxtest.model.Employee>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<er.erxtest.model.Employee> fetchPlebs(EOEditingContext editingContext)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("plebs", _Employee.ENTITY_NAME);
    return (NSArray<er.erxtest.model.Employee>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

}
