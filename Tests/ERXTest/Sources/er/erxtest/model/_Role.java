// DO NOT EDIT.  Make changes to Role.java instead.
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
public abstract class _Role extends er.extensions.eof.ERXGenericRecord {
  public static final String ENTITY_NAME = "Role";

  // Attribute Keys

  // Relationship Keys
  public static final ERXKey<er.erxtest.model.Employee> EMPLOYEES = new ERXKey<er.erxtest.model.Employee>("employees", Type.ToManyRelationship);

  // Attributes

  // Relationships
  public static final String EMPLOYEES_KEY = EMPLOYEES.key();

  private static final Logger log = LoggerFactory.getLogger(_Role.class);

  public Role localInstanceIn(EOEditingContext editingContext) {
    Role localInstance = (Role)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSArray<er.erxtest.model.Employee> employees() {
    return (NSArray<er.erxtest.model.Employee>)storedValueForKey(_Role.EMPLOYEES_KEY);
  }

  public NSArray<er.erxtest.model.Employee> employees(EOQualifier qualifier) {
    return employees(qualifier, null);
  }

  public NSArray<er.erxtest.model.Employee> employees(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.erxtest.model.Employee> results;
      results = employees();
      if (qualifier != null) {
        results = (NSArray<er.erxtest.model.Employee>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.erxtest.model.Employee>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }

  public void addToEmployees(er.erxtest.model.Employee object) {
    includeObjectIntoPropertyWithKey(object, _Role.EMPLOYEES_KEY);
  }

  public void removeFromEmployees(er.erxtest.model.Employee object) {
    excludeObjectFromPropertyWithKey(object, _Role.EMPLOYEES_KEY);
  }

  public void addToEmployeesRelationship(er.erxtest.model.Employee object) {
    log.debug("adding {} to employees relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToEmployees(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Role.EMPLOYEES_KEY);
    }
  }

  public void removeFromEmployeesRelationship(er.erxtest.model.Employee object) {
    log.debug("removing {} from employees relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromEmployees(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Role.EMPLOYEES_KEY);
    }
  }

  public er.erxtest.model.Employee createEmployeesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.erxtest.model.Employee.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Role.EMPLOYEES_KEY);
    return (er.erxtest.model.Employee) eo;
  }

  public void deleteEmployeesRelationship(er.erxtest.model.Employee object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Role.EMPLOYEES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllEmployeesRelationships() {
    Enumeration<er.erxtest.model.Employee> objects = employees().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteEmployeesRelationship(objects.nextElement());
    }
  }


  public static Role createRole(EOEditingContext editingContext) {
    Role eo = (Role) EOUtilities.createAndInsertInstance(editingContext, _Role.ENTITY_NAME);
    return eo;
  }

  public static ERXFetchSpecification<Role> fetchSpec() {
    return new ERXFetchSpecification<Role>(_Role.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Role> fetchAllRoles(EOEditingContext editingContext) {
    return _Role.fetchAllRoles(editingContext, null);
  }

  public static NSArray<Role> fetchAllRoles(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Role.fetchRoles(editingContext, null, sortOrderings);
  }

  public static NSArray<Role> fetchRoles(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Role> fetchSpec = new ERXFetchSpecification<Role>(_Role.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Role> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Role fetchRole(EOEditingContext editingContext, String keyName, Object value) {
    return _Role.fetchRole(editingContext, ERXQ.equals(keyName, value));
  }

  public static Role fetchRole(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Role> eoObjects = _Role.fetchRoles(editingContext, qualifier, null);
    Role eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Role that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Role fetchRequiredRole(EOEditingContext editingContext, String keyName, Object value) {
    return _Role.fetchRequiredRole(editingContext, ERXQ.equals(keyName, value));
  }

  public static Role fetchRequiredRole(EOEditingContext editingContext, EOQualifier qualifier) {
    Role eoObject = _Role.fetchRole(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Role that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Role localInstanceIn(EOEditingContext editingContext, Role eo) {
    Role localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
