// DO NOT EDIT.  Make changes to TalentPhoto.java instead.
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
public abstract class _TalentPhoto extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "TalentPhoto";

  // Attribute Keys

  // Relationship Keys
  public static final ERXKey<er.distribution.example.server.eo.Talent> TALENT = new ERXKey<er.distribution.example.server.eo.Talent>("talent", Type.ToOneRelationship);

  // Attributes

  // Relationships
  public static final String TALENT_KEY = TALENT.key();

  private static final Logger log = LoggerFactory.getLogger(_TalentPhoto.class);

  public TalentPhoto localInstanceIn(EOEditingContext editingContext) {
    TalentPhoto localInstance = (TalentPhoto)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public er.distribution.example.server.eo.Talent talent() {
    return (er.distribution.example.server.eo.Talent)storedValueForKey(_TalentPhoto.TALENT_KEY);
  }

  public void setTalent(er.distribution.example.server.eo.Talent value) {
    takeStoredValueForKey(value, _TalentPhoto.TALENT_KEY);
  }

  public void setTalentRelationship(er.distribution.example.server.eo.Talent value) {
    log.debug("updating talent from {} to {}", talent(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setTalent(value);
    }
    else if (value == null) {
      er.distribution.example.server.eo.Talent oldValue = talent();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _TalentPhoto.TALENT_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _TalentPhoto.TALENT_KEY);
    }
  }


  public static TalentPhoto createTalentPhoto(EOEditingContext editingContext, er.distribution.example.server.eo.Talent talent) {
    TalentPhoto eo = (TalentPhoto) EOUtilities.createAndInsertInstance(editingContext, _TalentPhoto.ENTITY_NAME);
    eo.setTalentRelationship(talent);
    return eo;
  }

  public static ERXFetchSpecification<TalentPhoto> fetchSpec() {
    return new ERXFetchSpecification<TalentPhoto>(_TalentPhoto.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<TalentPhoto> fetchAllTalentPhotos(EOEditingContext editingContext) {
    return _TalentPhoto.fetchAllTalentPhotos(editingContext, null);
  }

  public static NSArray<TalentPhoto> fetchAllTalentPhotos(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _TalentPhoto.fetchTalentPhotos(editingContext, null, sortOrderings);
  }

  public static NSArray<TalentPhoto> fetchTalentPhotos(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<TalentPhoto> fetchSpec = new ERXFetchSpecification<TalentPhoto>(_TalentPhoto.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<TalentPhoto> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static TalentPhoto fetchTalentPhoto(EOEditingContext editingContext, String keyName, Object value) {
    return _TalentPhoto.fetchTalentPhoto(editingContext, ERXQ.equals(keyName, value));
  }

  public static TalentPhoto fetchTalentPhoto(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<TalentPhoto> eoObjects = _TalentPhoto.fetchTalentPhotos(editingContext, qualifier, null);
    TalentPhoto eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one TalentPhoto that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TalentPhoto fetchRequiredTalentPhoto(EOEditingContext editingContext, String keyName, Object value) {
    return _TalentPhoto.fetchRequiredTalentPhoto(editingContext, ERXQ.equals(keyName, value));
  }

  public static TalentPhoto fetchRequiredTalentPhoto(EOEditingContext editingContext, EOQualifier qualifier) {
    TalentPhoto eoObject = _TalentPhoto.fetchTalentPhoto(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no TalentPhoto that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static TalentPhoto localInstanceIn(EOEditingContext editingContext, TalentPhoto eo) {
    TalentPhoto localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
