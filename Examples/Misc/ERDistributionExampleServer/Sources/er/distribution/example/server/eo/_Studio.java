// DO NOT EDIT.  Make changes to Studio.java instead.
package er.distribution.example.server.eo;

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
public abstract class _Studio extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "Studio";

  // Attribute Keys
  public static final ERXKey<java.math.BigDecimal> BUDGET = new ERXKey<java.math.BigDecimal>("budget", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.distribution.example.server.eo.Movie> MOVIES = new ERXKey<er.distribution.example.server.eo.Movie>("movies", Type.ToManyRelationship);

  // Attributes
  public static final String BUDGET_KEY = BUDGET.key();
  public static final String NAME_KEY = NAME.key();

  // Relationships
  public static final String MOVIES_KEY = MOVIES.key();

  private static final Logger log = LoggerFactory.getLogger(_Studio.class);

  public Studio localInstanceIn(EOEditingContext editingContext) {
    Studio localInstance = (Studio)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public java.math.BigDecimal budget() {
    return (java.math.BigDecimal) storedValueForKey(_Studio.BUDGET_KEY);
  }

  public void setBudget(java.math.BigDecimal value) {
    log.debug( "updating budget from {} to {}", budget(), value);
    takeStoredValueForKey(value, _Studio.BUDGET_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Studio.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _Studio.NAME_KEY);
  }

  public NSArray<er.distribution.example.server.eo.Movie> movies() {
    return (NSArray<er.distribution.example.server.eo.Movie>)storedValueForKey(_Studio.MOVIES_KEY);
  }

  public NSArray<er.distribution.example.server.eo.Movie> movies(EOQualifier qualifier) {
    return movies(qualifier, null, false);
  }

  public NSArray<er.distribution.example.server.eo.Movie> movies(EOQualifier qualifier, boolean fetch) {
    return movies(qualifier, null, fetch);
  }

  public NSArray<er.distribution.example.server.eo.Movie> movies(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.distribution.example.server.eo.Movie> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.distribution.example.server.eo.Movie.STUDIO_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.distribution.example.server.eo.Movie.fetchMovies(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = movies();
      if (qualifier != null) {
        results = (NSArray<er.distribution.example.server.eo.Movie>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.distribution.example.server.eo.Movie>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToMovies(er.distribution.example.server.eo.Movie object) {
    includeObjectIntoPropertyWithKey(object, _Studio.MOVIES_KEY);
  }

  public void removeFromMovies(er.distribution.example.server.eo.Movie object) {
    excludeObjectFromPropertyWithKey(object, _Studio.MOVIES_KEY);
  }

  public void addToMoviesRelationship(er.distribution.example.server.eo.Movie object) {
    log.debug("adding {} to movies relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToMovies(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Studio.MOVIES_KEY);
    }
  }

  public void removeFromMoviesRelationship(er.distribution.example.server.eo.Movie object) {
    log.debug("removing {} from movies relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromMovies(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Studio.MOVIES_KEY);
    }
  }

  public er.distribution.example.server.eo.Movie createMoviesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.distribution.example.server.eo.Movie.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Studio.MOVIES_KEY);
    return (er.distribution.example.server.eo.Movie) eo;
  }

  public void deleteMoviesRelationship(er.distribution.example.server.eo.Movie object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Studio.MOVIES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllMoviesRelationships() {
    Enumeration<er.distribution.example.server.eo.Movie> objects = movies().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteMoviesRelationship(objects.nextElement());
    }
  }


  public static Studio createStudio(EOEditingContext editingContext, java.math.BigDecimal budget
, String name
) {
    Studio eo = (Studio) EOUtilities.createAndInsertInstance(editingContext, _Studio.ENTITY_NAME);
    eo.setBudget(budget);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<Studio> fetchSpec() {
    return new ERXFetchSpecification<Studio>(_Studio.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Studio> fetchAllStudios(EOEditingContext editingContext) {
    return _Studio.fetchAllStudios(editingContext, null);
  }

  public static NSArray<Studio> fetchAllStudios(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Studio.fetchStudios(editingContext, null, sortOrderings);
  }

  public static NSArray<Studio> fetchStudios(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Studio> fetchSpec = new ERXFetchSpecification<Studio>(_Studio.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Studio> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Studio fetchStudio(EOEditingContext editingContext, String keyName, Object value) {
    return _Studio.fetchStudio(editingContext, ERXQ.equals(keyName, value));
  }

  public static Studio fetchStudio(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Studio> eoObjects = _Studio.fetchStudios(editingContext, qualifier, null);
    Studio eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Studio that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Studio fetchRequiredStudio(EOEditingContext editingContext, String keyName, Object value) {
    return _Studio.fetchRequiredStudio(editingContext, ERXQ.equals(keyName, value));
  }

  public static Studio fetchRequiredStudio(EOEditingContext editingContext, EOQualifier qualifier) {
    Studio eoObject = _Studio.fetchStudio(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Studio that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Studio localInstanceIn(EOEditingContext editingContext, Studio eo) {
    Studio localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
  public static NSArray<NSDictionary> fetchRawFetchAllStudios(EOEditingContext editingContext, NSDictionary<String, Object> bindings) {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("RawFetchAllStudios", _Studio.ENTITY_NAME);
    fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
    return (NSArray<NSDictionary>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

  public static NSArray<NSDictionary> fetchRawFetchAllStudios(EOEditingContext editingContext)
  {
    EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("RawFetchAllStudios", _Studio.ENTITY_NAME);
    return (NSArray<NSDictionary>)editingContext.objectsWithFetchSpecification(fetchSpec);
  }

}
