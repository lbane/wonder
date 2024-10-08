// DO NOT EDIT.  Make changes to PlotSummary.java instead.
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
public abstract class _PlotSummary extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "PlotSummary";

  // Attribute Keys
  public static final ERXKey<String> SUMMARY = new ERXKey<String>("summary", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<er.distribution.example.server.eo.Movie> MOVIE = new ERXKey<er.distribution.example.server.eo.Movie>("movie", Type.ToOneRelationship);

  // Attributes
  public static final String SUMMARY_KEY = SUMMARY.key();

  // Relationships
  public static final String MOVIE_KEY = MOVIE.key();

  private static final Logger log = LoggerFactory.getLogger(_PlotSummary.class);

  public PlotSummary localInstanceIn(EOEditingContext editingContext) {
    PlotSummary localInstance = (PlotSummary)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String summary() {
    return (String) storedValueForKey(_PlotSummary.SUMMARY_KEY);
  }

  public void setSummary(String value) {
    log.debug( "updating summary from {} to {}", summary(), value);
    takeStoredValueForKey(value, _PlotSummary.SUMMARY_KEY);
  }

  public er.distribution.example.server.eo.Movie movie() {
    return (er.distribution.example.server.eo.Movie)storedValueForKey(_PlotSummary.MOVIE_KEY);
  }

  public void setMovie(er.distribution.example.server.eo.Movie value) {
    takeStoredValueForKey(value, _PlotSummary.MOVIE_KEY);
  }

  public void setMovieRelationship(er.distribution.example.server.eo.Movie value) {
    log.debug("updating movie from {} to {}", movie(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setMovie(value);
    }
    else if (value == null) {
      er.distribution.example.server.eo.Movie oldValue = movie();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _PlotSummary.MOVIE_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _PlotSummary.MOVIE_KEY);
    }
  }


  public static PlotSummary createPlotSummary(EOEditingContext editingContext, er.distribution.example.server.eo.Movie movie) {
    PlotSummary eo = (PlotSummary) EOUtilities.createAndInsertInstance(editingContext, _PlotSummary.ENTITY_NAME);
    eo.setMovieRelationship(movie);
    return eo;
  }

  public static ERXFetchSpecification<PlotSummary> fetchSpec() {
    return new ERXFetchSpecification<PlotSummary>(_PlotSummary.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<PlotSummary> fetchAllPlotSummaries(EOEditingContext editingContext) {
    return _PlotSummary.fetchAllPlotSummaries(editingContext, null);
  }

  public static NSArray<PlotSummary> fetchAllPlotSummaries(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _PlotSummary.fetchPlotSummaries(editingContext, null, sortOrderings);
  }

  public static NSArray<PlotSummary> fetchPlotSummaries(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<PlotSummary> fetchSpec = new ERXFetchSpecification<PlotSummary>(_PlotSummary.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<PlotSummary> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static PlotSummary fetchPlotSummary(EOEditingContext editingContext, String keyName, Object value) {
    return _PlotSummary.fetchPlotSummary(editingContext, ERXQ.equals(keyName, value));
  }

  public static PlotSummary fetchPlotSummary(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<PlotSummary> eoObjects = _PlotSummary.fetchPlotSummaries(editingContext, qualifier, null);
    PlotSummary eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one PlotSummary that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static PlotSummary fetchRequiredPlotSummary(EOEditingContext editingContext, String keyName, Object value) {
    return _PlotSummary.fetchRequiredPlotSummary(editingContext, ERXQ.equals(keyName, value));
  }

  public static PlotSummary fetchRequiredPlotSummary(EOEditingContext editingContext, EOQualifier qualifier) {
    PlotSummary eoObject = _PlotSummary.fetchPlotSummary(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no PlotSummary that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static PlotSummary localInstanceIn(EOEditingContext editingContext, PlotSummary eo) {
    PlotSummary localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
