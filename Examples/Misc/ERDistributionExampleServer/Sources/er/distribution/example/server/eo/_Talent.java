// DO NOT EDIT.  Make changes to Talent.java instead.
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
public abstract class _Talent extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "Talent";

  // Attribute Keys
  public static final ERXKey<String> FIRST_NAME = new ERXKey<String>("firstName", Type.Attribute);
  public static final ERXKey<String> LAST_NAME = new ERXKey<String>("lastName", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.distribution.example.server.eo.Movie> MOVIES_DIRECTED = new ERXKey<er.distribution.example.server.eo.Movie>("moviesDirected", Type.ToManyRelationship);
  public static final ERXKey<er.distribution.example.server.eo.TalentPhoto> PHOTO = new ERXKey<er.distribution.example.server.eo.TalentPhoto>("photo", Type.ToOneRelationship);
  public static final ERXKey<er.distribution.example.server.eo.MovieRole> ROLES = new ERXKey<er.distribution.example.server.eo.MovieRole>("roles", Type.ToManyRelationship);

  // Attributes
  public static final String FIRST_NAME_KEY = FIRST_NAME.key();
  public static final String LAST_NAME_KEY = LAST_NAME.key();

  // Relationships
  public static final String MOVIES_DIRECTED_KEY = MOVIES_DIRECTED.key();
  public static final String PHOTO_KEY = PHOTO.key();
  public static final String ROLES_KEY = ROLES.key();

  private static final Logger log = LoggerFactory.getLogger(_Talent.class);

  public Talent localInstanceIn(EOEditingContext editingContext) {
    Talent localInstance = (Talent)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String firstName() {
    return (String) storedValueForKey(_Talent.FIRST_NAME_KEY);
  }

  public void setFirstName(String value) {
    log.debug( "updating firstName from {} to {}", firstName(), value);
    takeStoredValueForKey(value, _Talent.FIRST_NAME_KEY);
  }

  public String lastName() {
    return (String) storedValueForKey(_Talent.LAST_NAME_KEY);
  }

  public void setLastName(String value) {
    log.debug( "updating lastName from {} to {}", lastName(), value);
    takeStoredValueForKey(value, _Talent.LAST_NAME_KEY);
  }

  public er.distribution.example.server.eo.TalentPhoto photo() {
    return (er.distribution.example.server.eo.TalentPhoto)storedValueForKey(_Talent.PHOTO_KEY);
  }

  public void setPhoto(er.distribution.example.server.eo.TalentPhoto value) {
    takeStoredValueForKey(value, _Talent.PHOTO_KEY);
  }

  public void setPhotoRelationship(er.distribution.example.server.eo.TalentPhoto value) {
    log.debug("updating photo from {} to {}", photo(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setPhoto(value);
    }
    else if (value == null) {
      er.distribution.example.server.eo.TalentPhoto oldValue = photo();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Talent.PHOTO_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Talent.PHOTO_KEY);
    }
  }

  public NSArray<er.distribution.example.server.eo.Movie> moviesDirected() {
    return (NSArray<er.distribution.example.server.eo.Movie>)storedValueForKey(_Talent.MOVIES_DIRECTED_KEY);
  }

  public NSArray<er.distribution.example.server.eo.Movie> moviesDirected(EOQualifier qualifier) {
    return moviesDirected(qualifier, null);
  }

  public NSArray<er.distribution.example.server.eo.Movie> moviesDirected(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    NSArray<er.distribution.example.server.eo.Movie> results;
      results = moviesDirected();
      if (qualifier != null) {
        results = (NSArray<er.distribution.example.server.eo.Movie>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.distribution.example.server.eo.Movie>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    return results;
  }

  public void addToMoviesDirected(er.distribution.example.server.eo.Movie object) {
    includeObjectIntoPropertyWithKey(object, _Talent.MOVIES_DIRECTED_KEY);
  }

  public void removeFromMoviesDirected(er.distribution.example.server.eo.Movie object) {
    excludeObjectFromPropertyWithKey(object, _Talent.MOVIES_DIRECTED_KEY);
  }

  public void addToMoviesDirectedRelationship(er.distribution.example.server.eo.Movie object) {
    log.debug("adding {} to moviesDirected relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToMoviesDirected(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Talent.MOVIES_DIRECTED_KEY);
    }
  }

  public void removeFromMoviesDirectedRelationship(er.distribution.example.server.eo.Movie object) {
    log.debug("removing {} from moviesDirected relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromMoviesDirected(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Talent.MOVIES_DIRECTED_KEY);
    }
  }

  public er.distribution.example.server.eo.Movie createMoviesDirectedRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.distribution.example.server.eo.Movie.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Talent.MOVIES_DIRECTED_KEY);
    return (er.distribution.example.server.eo.Movie) eo;
  }

  public void deleteMoviesDirectedRelationship(er.distribution.example.server.eo.Movie object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Talent.MOVIES_DIRECTED_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllMoviesDirectedRelationships() {
    Enumeration<er.distribution.example.server.eo.Movie> objects = moviesDirected().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteMoviesDirectedRelationship(objects.nextElement());
    }
  }

  public NSArray<er.distribution.example.server.eo.MovieRole> roles() {
    return (NSArray<er.distribution.example.server.eo.MovieRole>)storedValueForKey(_Talent.ROLES_KEY);
  }

  public NSArray<er.distribution.example.server.eo.MovieRole> roles(EOQualifier qualifier) {
    return roles(qualifier, null, false);
  }

  public NSArray<er.distribution.example.server.eo.MovieRole> roles(EOQualifier qualifier, boolean fetch) {
    return roles(qualifier, null, fetch);
  }

  public NSArray<er.distribution.example.server.eo.MovieRole> roles(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<er.distribution.example.server.eo.MovieRole> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(er.distribution.example.server.eo.MovieRole.TALENT_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = er.distribution.example.server.eo.MovieRole.fetchMovieRoles(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = roles();
      if (qualifier != null) {
        results = (NSArray<er.distribution.example.server.eo.MovieRole>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<er.distribution.example.server.eo.MovieRole>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToRoles(er.distribution.example.server.eo.MovieRole object) {
    includeObjectIntoPropertyWithKey(object, _Talent.ROLES_KEY);
  }

  public void removeFromRoles(er.distribution.example.server.eo.MovieRole object) {
    excludeObjectFromPropertyWithKey(object, _Talent.ROLES_KEY);
  }

  public void addToRolesRelationship(er.distribution.example.server.eo.MovieRole object) {
    log.debug("adding {} to roles relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToRoles(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Talent.ROLES_KEY);
    }
  }

  public void removeFromRolesRelationship(er.distribution.example.server.eo.MovieRole object) {
    log.debug("removing {} from roles relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromRoles(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Talent.ROLES_KEY);
    }
  }

  public er.distribution.example.server.eo.MovieRole createRolesRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  er.distribution.example.server.eo.MovieRole.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Talent.ROLES_KEY);
    return (er.distribution.example.server.eo.MovieRole) eo;
  }

  public void deleteRolesRelationship(er.distribution.example.server.eo.MovieRole object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Talent.ROLES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllRolesRelationships() {
    Enumeration<er.distribution.example.server.eo.MovieRole> objects = roles().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRolesRelationship(objects.nextElement());
    }
  }


  public static Talent createTalent(EOEditingContext editingContext, String firstName
, String lastName
) {
    Talent eo = (Talent) EOUtilities.createAndInsertInstance(editingContext, _Talent.ENTITY_NAME);
    eo.setFirstName(firstName);
    eo.setLastName(lastName);
    return eo;
  }

  public static ERXFetchSpecification<Talent> fetchSpec() {
    return new ERXFetchSpecification<Talent>(_Talent.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Talent> fetchAllTalents(EOEditingContext editingContext) {
    return _Talent.fetchAllTalents(editingContext, null);
  }

  public static NSArray<Talent> fetchAllTalents(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Talent.fetchTalents(editingContext, null, sortOrderings);
  }

  public static NSArray<Talent> fetchTalents(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Talent> fetchSpec = new ERXFetchSpecification<Talent>(_Talent.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Talent> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Talent fetchTalent(EOEditingContext editingContext, String keyName, Object value) {
    return _Talent.fetchTalent(editingContext, ERXQ.equals(keyName, value));
  }

  public static Talent fetchTalent(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Talent> eoObjects = _Talent.fetchTalents(editingContext, qualifier, null);
    Talent eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Talent that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Talent fetchRequiredTalent(EOEditingContext editingContext, String keyName, Object value) {
    return _Talent.fetchRequiredTalent(editingContext, ERXQ.equals(keyName, value));
  }

  public static Talent fetchRequiredTalent(EOEditingContext editingContext, EOQualifier qualifier) {
    Talent eoObject = _Talent.fetchTalent(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Talent that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Talent localInstanceIn(EOEditingContext editingContext, Talent eo) {
    Talent localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
