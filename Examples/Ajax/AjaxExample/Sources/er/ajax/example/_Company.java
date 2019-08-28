// DO NOT EDIT.  Make changes to Company.java instead.
package er.ajax.example;

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
public abstract class _Company extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "Company";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.ajax.example.Employee> EMPLOYEES = new ERXKey<er.ajax.example.Employee>("employees", Type.ToManyRelationship);

  // Attributes
  public static final String NAME_KEY = NAME.key();

  // Relationships
  public static final String EMPLOYEES_KEY = EMPLOYEES.key();

  private static final Logger log = LoggerFactory.getLogger(_Company.class);

  public Company localInstanceIn(EOEditingContext editingContext) {
    Company localInstance = (Company)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_Company.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _Company.NAME_KEY);
  }

  public NSArray<er.ajax.example.Employee> employees() {
    return (NSArray<er.ajax.example.Employee>)storedValueForKey(_Company.EMPLOYEES_KEY);
  }

  public NSArray<er.ajax.example.Employee> employees(EOQualifier qualifier) {
    return employees(qualifier, null, false);
  }

  public NSArray<er.ajax.example.Employee> employees(EOQualifier qualifier, boolean fetch) {
    return employees(qualifier, null, fetch);
  }

  public NSArray<er.ajax.example.Employee> employees(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.ajax.example.Employee> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.ajax.example.Employee.COMPANY_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.ajax.example.Employee.fetchEmployees(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = employees();
      if (qualifier != null) {
        results = (NSArray<er.ajax.example.Employee>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.ajax.example.Employee>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToEmployees(er.ajax.example.Employee object) {
    includeObjectIntoPropertyWithKey(object, _Company.EMPLOYEES_KEY);
  }

  public void removeFromEmployees(er.ajax.example.Employee object) {
    excludeObjectFromPropertyWithKey(object, _Company.EMPLOYEES_KEY);
  }

  public void addToEmployeesRelationship(er.ajax.example.Employee object) {
    log.debug("adding {} to employees relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToEmployees(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Company.EMPLOYEES_KEY);
    }
  }

  public void removeFromEmployeesRelationship(er.ajax.example.Employee object) {
    log.debug("removing {} from employees relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromEmployees(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Company.EMPLOYEES_KEY);
    }
  }

  public er.ajax.example.Employee createEmployeesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.ajax.example.Employee.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Company.EMPLOYEES_KEY);
    return (er.ajax.example.Employee) eo;
  }

  public void deleteEmployeesRelationship(er.ajax.example.Employee object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Company.EMPLOYEES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllEmployeesRelationships() {
    Enumeration<er.ajax.example.Employee> objects = employees().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteEmployeesRelationship(objects.nextElement());
    }
  }


  public static Company createCompany(EOEditingContext editingContext, String name
) {
    Company eo = (Company) EOUtilities.createAndInsertInstance(editingContext, _Company.ENTITY_NAME);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<Company> fetchSpec() {
    return new ERXFetchSpecification<Company>(_Company.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Company> fetchAllCompanies(EOEditingContext editingContext) {
    return _Company.fetchAllCompanies(editingContext, null);
  }

  public static NSArray<Company> fetchAllCompanies(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Company.fetchCompanies(editingContext, null, sortOrderings);
  }

  public static NSArray<Company> fetchCompanies(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Company> fetchSpec = new ERXFetchSpecification<Company>(_Company.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Company> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Company fetchCompany(EOEditingContext editingContext, String keyName, Object value) {
    return _Company.fetchCompany(editingContext, ERXQ.equals(keyName, value));
  }

  public static Company fetchCompany(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Company> eoObjects = _Company.fetchCompanies(editingContext, qualifier, null);
    Company eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Company that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Company fetchRequiredCompany(EOEditingContext editingContext, String keyName, Object value) {
    return _Company.fetchRequiredCompany(editingContext, ERXQ.equals(keyName, value));
  }

  public static Company fetchRequiredCompany(EOEditingContext editingContext, EOQualifier qualifier) {
    Company eoObject = _Company.fetchCompany(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Company that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Company localInstanceIn(EOEditingContext editingContext, Company eo) {
    Company localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
