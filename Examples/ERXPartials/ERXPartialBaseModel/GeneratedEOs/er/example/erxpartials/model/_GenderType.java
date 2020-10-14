// DO NOT EDIT.  Make changes to GenderType.java instead.
package er.example.erxpartials.model;

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
public abstract class _GenderType extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "GenderType";

  // Attribute Keys
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.example.erxpartials.model.Person> PERSONS = new ERXKey<er.example.erxpartials.model.Person>("persons", Type.ToManyRelationship);

  // Attributes
  public static final String NAME_KEY = NAME.key();

  // Relationships
  public static final String PERSONS_KEY = PERSONS.key();

  private static final Logger log = LoggerFactory.getLogger(_GenderType.class);

  public GenderType localInstanceIn(EOEditingContext editingContext) {
    GenderType localInstance = (GenderType)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey(_GenderType.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _GenderType.NAME_KEY);
  }

  public NSArray<er.example.erxpartials.model.Person> persons() {
    return (NSArray<er.example.erxpartials.model.Person>)storedValueForKey(_GenderType.PERSONS_KEY);
  }

  public NSArray<er.example.erxpartials.model.Person> persons(EOQualifier qualifier) {
    return persons(qualifier, null, false);
  }

  public NSArray<er.example.erxpartials.model.Person> persons(EOQualifier qualifier, boolean fetch) {
    return persons(qualifier, null, fetch);
  }

  public NSArray<er.example.erxpartials.model.Person> persons(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.example.erxpartials.model.Person> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.example.erxpartials.model.Person.GENDER_TYPE_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.example.erxpartials.model.Person.fetchPersons(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = persons();
      if (qualifier != null) {
        results = (NSArray<er.example.erxpartials.model.Person>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.example.erxpartials.model.Person>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToPersons(er.example.erxpartials.model.Person object) {
    includeObjectIntoPropertyWithKey(object, _GenderType.PERSONS_KEY);
  }

  public void removeFromPersons(er.example.erxpartials.model.Person object) {
    excludeObjectFromPropertyWithKey(object, _GenderType.PERSONS_KEY);
  }

  public void addToPersonsRelationship(er.example.erxpartials.model.Person object) {
    log.debug("adding {} to persons relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToPersons(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _GenderType.PERSONS_KEY);
    }
  }

  public void removeFromPersonsRelationship(er.example.erxpartials.model.Person object) {
    log.debug("removing {} from persons relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromPersons(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _GenderType.PERSONS_KEY);
    }
  }

  public er.example.erxpartials.model.Person createPersonsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.example.erxpartials.model.Person.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _GenderType.PERSONS_KEY);
    return (er.example.erxpartials.model.Person) eo;
  }

  public void deletePersonsRelationship(er.example.erxpartials.model.Person object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _GenderType.PERSONS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllPersonsRelationships() {
    Enumeration<er.example.erxpartials.model.Person> objects = persons().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePersonsRelationship(objects.nextElement());
    }
  }


  public static GenderType createGenderType(EOEditingContext editingContext, String name
) {
    GenderType eo = (GenderType) EOUtilities.createAndInsertInstance(editingContext, _GenderType.ENTITY_NAME);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<GenderType> fetchSpec() {
    return new ERXFetchSpecification<GenderType>(_GenderType.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<GenderType> fetchAllGenderTypes(EOEditingContext editingContext) {
    return _GenderType.fetchAllGenderTypes(editingContext, null);
  }

  public static NSArray<GenderType> fetchAllGenderTypes(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _GenderType.fetchGenderTypes(editingContext, null, sortOrderings);
  }

  public static NSArray<GenderType> fetchGenderTypes(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<GenderType> fetchSpec = new ERXFetchSpecification<GenderType>(_GenderType.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<GenderType> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static GenderType fetchGenderType(EOEditingContext editingContext, String keyName, Object value) {
    return _GenderType.fetchGenderType(editingContext, ERXQ.equals(keyName, value));
  }

  public static GenderType fetchGenderType(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<GenderType> eoObjects = _GenderType.fetchGenderTypes(editingContext, qualifier, null);
    GenderType eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one GenderType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static GenderType fetchRequiredGenderType(EOEditingContext editingContext, String keyName, Object value) {
    return _GenderType.fetchRequiredGenderType(editingContext, ERXQ.equals(keyName, value));
  }

  public static GenderType fetchRequiredGenderType(EOEditingContext editingContext, EOQualifier qualifier) {
    GenderType eoObject = _GenderType.fetchGenderType(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no GenderType that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static GenderType localInstanceIn(EOEditingContext editingContext, GenderType eo) {
    GenderType localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
