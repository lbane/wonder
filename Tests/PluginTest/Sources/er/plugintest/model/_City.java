// DO NOT EDIT.  Make changes to City.java instead.
package er.plugintest.model;

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
public abstract class _City extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "City";

  // Attribute Keys
  public static final ERXKey<String> DESCRIPTION = new ERXKey<String>("description", Type.Attribute);
  public static final ERXKey<String> DISTICT = new ERXKey<String>("distict", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<Integer> POPULATION = new ERXKey<Integer>("population", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.plugintest.model.Country> COUNTRY = new ERXKey<er.plugintest.model.Country>("country", Type.ToOneRelationship);

  // Attributes
  public static final String DESCRIPTION_KEY = DESCRIPTION.key();
  public static final String DISTICT_KEY = DISTICT.key();
  public static final String NAME_KEY = NAME.key();
  public static final String POPULATION_KEY = POPULATION.key();

  // Relationships
  public static final String COUNTRY_KEY = COUNTRY.key();

  private static final Logger log = LoggerFactory.getLogger(_City.class);

  public City localInstanceIn(EOEditingContext editingContext) {
    City localInstance = (City)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String description() {
    return (String) storedValueForKey(_City.DESCRIPTION_KEY);
  }

  public void setDescription(String value) {
    log.debug( "updating description from {} to {}", description(), value);
    takeStoredValueForKey(value, _City.DESCRIPTION_KEY);
  }

  public String distict() {
    return (String) storedValueForKey(_City.DISTICT_KEY);
  }

  public void setDistict(String value) {
    log.debug( "updating distict from {} to {}", distict(), value);
    takeStoredValueForKey(value, _City.DISTICT_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_City.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _City.NAME_KEY);
  }

  public Integer population() {
    return (Integer) storedValueForKey(_City.POPULATION_KEY);
  }

  public void setPopulation(Integer value) {
    log.debug( "updating population from {} to {}", population(), value);
    takeStoredValueForKey(value, _City.POPULATION_KEY);
  }

  public er.plugintest.model.Country country() {
    return (er.plugintest.model.Country)storedValueForKey(_City.COUNTRY_KEY);
  }

  public void setCountry(er.plugintest.model.Country value) {
    takeStoredValueForKey(value, _City.COUNTRY_KEY);
  }

  public void setCountryRelationship(er.plugintest.model.Country value) {
    log.debug("updating country from {} to {}", country(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setCountry(value);
    }
    else if (value == null) {
      er.plugintest.model.Country oldValue = country();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _City.COUNTRY_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _City.COUNTRY_KEY);
    }
  }


  public static City createCity(EOEditingContext editingContext, String name
) {
    City eo = (City) EOUtilities.createAndInsertInstance(editingContext, _City.ENTITY_NAME);
    eo.setName(name);
    return eo;
  }

  public static ERXFetchSpecification<City> fetchSpec() {
    return new ERXFetchSpecification<City>(_City.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<City> fetchAllCities(EOEditingContext editingContext) {
    return _City.fetchAllCities(editingContext, null);
  }

  public static NSArray<City> fetchAllCities(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _City.fetchCities(editingContext, null, sortOrderings);
  }

  public static NSArray<City> fetchCities(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<City> fetchSpec = new ERXFetchSpecification<City>(_City.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<City> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static City fetchCity(EOEditingContext editingContext, String keyName, Object value) {
    return _City.fetchCity(editingContext, ERXQ.equals(keyName, value));
  }

  public static City fetchCity(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<City> eoObjects = _City.fetchCities(editingContext, qualifier, null);
    City eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one City that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static City fetchRequiredCity(EOEditingContext editingContext, String keyName, Object value) {
    return _City.fetchRequiredCity(editingContext, ERXQ.equals(keyName, value));
  }

  public static City fetchRequiredCity(EOEditingContext editingContext, EOQualifier qualifier) {
    City eoObject = _City.fetchCity(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no City that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static City localInstanceIn(EOEditingContext editingContext, City eo) {
    City localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
