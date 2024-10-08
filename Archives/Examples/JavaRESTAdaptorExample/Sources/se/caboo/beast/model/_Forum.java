// DO NOT EDIT.  Make changes to Forum.java instead.
package se.caboo.beast.model;

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
public abstract class _Forum extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "Forum";

  // Attribute Keys
  public static final ERXKey<String> DESCRIPTION = new ERXKey<String>("description", Type.Attribute);
  public static final ERXKey<String> DESCRIPTION_HTML = new ERXKey<String>("descriptionHtml", Type.Attribute);
  public static final ERXKey<String> NAME = new ERXKey<String>("name", Type.Attribute);
  public static final ERXKey<Integer> POSITION = new ERXKey<Integer>("position", Type.Attribute);
  public static final ERXKey<Integer> POSTS_COUNT = new ERXKey<Integer>("postsCount", Type.Attribute);
  public static final ERXKey<Integer> TOPICS_COUNT = new ERXKey<Integer>("topicsCount", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<se.caboo.beast.model.Post> POSTS = new ERXKey<se.caboo.beast.model.Post>("posts", Type.ToManyRelationship);
  public static final ERXKey<se.caboo.beast.model.Topic> TOPICS = new ERXKey<se.caboo.beast.model.Topic>("topics", Type.ToManyRelationship);

  // Attributes
  public static final String DESCRIPTION_KEY = DESCRIPTION.key();
  public static final String DESCRIPTION_HTML_KEY = DESCRIPTION_HTML.key();
  public static final String NAME_KEY = NAME.key();
  public static final String POSITION_KEY = POSITION.key();
  public static final String POSTS_COUNT_KEY = POSTS_COUNT.key();
  public static final String TOPICS_COUNT_KEY = TOPICS_COUNT.key();

  // Relationships
  public static final String POSTS_KEY = POSTS.key();
  public static final String TOPICS_KEY = TOPICS.key();

  private static final Logger log = LoggerFactory.getLogger(_Forum.class);

  public Forum localInstanceIn(EOEditingContext editingContext) {
    Forum localInstance = (Forum)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String description() {
    return (String) storedValueForKey(_Forum.DESCRIPTION_KEY);
  }

  public void setDescription(String value) {
    log.debug( "updating description from {} to {}", description(), value);
    takeStoredValueForKey(value, _Forum.DESCRIPTION_KEY);
  }

  public String descriptionHtml() {
    return (String) storedValueForKey(_Forum.DESCRIPTION_HTML_KEY);
  }

  public void setDescriptionHtml(String value) {
    log.debug( "updating descriptionHtml from {} to {}", descriptionHtml(), value);
    takeStoredValueForKey(value, _Forum.DESCRIPTION_HTML_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_Forum.NAME_KEY);
  }

  public void setName(String value) {
    log.debug( "updating name from {} to {}", name(), value);
    takeStoredValueForKey(value, _Forum.NAME_KEY);
  }

  public Integer position() {
    return (Integer) storedValueForKey(_Forum.POSITION_KEY);
  }

  public void setPosition(Integer value) {
    log.debug( "updating position from {} to {}", position(), value);
    takeStoredValueForKey(value, _Forum.POSITION_KEY);
  }

  public Integer postsCount() {
    return (Integer) storedValueForKey(_Forum.POSTS_COUNT_KEY);
  }

  public void setPostsCount(Integer value) {
    log.debug( "updating postsCount from {} to {}", postsCount(), value);
    takeStoredValueForKey(value, _Forum.POSTS_COUNT_KEY);
  }

  public Integer topicsCount() {
    return (Integer) storedValueForKey(_Forum.TOPICS_COUNT_KEY);
  }

  public void setTopicsCount(Integer value) {
    log.debug( "updating topicsCount from {} to {}", topicsCount(), value);
    takeStoredValueForKey(value, _Forum.TOPICS_COUNT_KEY);
  }

  public NSArray<se.caboo.beast.model.Post> posts() {
    return (NSArray<se.caboo.beast.model.Post>)storedValueForKey(_Forum.POSTS_KEY);
  }

  public NSArray<se.caboo.beast.model.Post> posts(EOQualifier qualifier) {
    return posts(qualifier, null, false);
  }

  public NSArray<se.caboo.beast.model.Post> posts(EOQualifier qualifier, boolean fetch) {
    return posts(qualifier, null, fetch);
  }

  public NSArray<se.caboo.beast.model.Post> posts(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<se.caboo.beast.model.Post> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(se.caboo.beast.model.Post.FORUM_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = se.caboo.beast.model.Post.fetchPosts(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = posts();
      if (qualifier != null) {
        results = (NSArray<se.caboo.beast.model.Post>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<se.caboo.beast.model.Post>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToPosts(se.caboo.beast.model.Post object) {
    includeObjectIntoPropertyWithKey(object, _Forum.POSTS_KEY);
  }

  public void removeFromPosts(se.caboo.beast.model.Post object) {
    excludeObjectFromPropertyWithKey(object, _Forum.POSTS_KEY);
  }

  public void addToPostsRelationship(se.caboo.beast.model.Post object) {
    log.debug("adding {} to posts relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToPosts(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Forum.POSTS_KEY);
    }
  }

  public void removeFromPostsRelationship(se.caboo.beast.model.Post object) {
    log.debug("removing {} from posts relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromPosts(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Forum.POSTS_KEY);
    }
  }

  public se.caboo.beast.model.Post createPostsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  se.caboo.beast.model.Post.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Forum.POSTS_KEY);
    return (se.caboo.beast.model.Post) eo;
  }

  public void deletePostsRelationship(se.caboo.beast.model.Post object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Forum.POSTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllPostsRelationships() {
    Enumeration<se.caboo.beast.model.Post> objects = posts().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePostsRelationship(objects.nextElement());
    }
  }

  public NSArray<se.caboo.beast.model.Topic> topics() {
    return (NSArray<se.caboo.beast.model.Topic>)storedValueForKey(_Forum.TOPICS_KEY);
  }

  public NSArray<se.caboo.beast.model.Topic> topics(EOQualifier qualifier) {
    return topics(qualifier, null, false);
  }

  public NSArray<se.caboo.beast.model.Topic> topics(EOQualifier qualifier, boolean fetch) {
    return topics(qualifier, null, fetch);
  }

  public NSArray<se.caboo.beast.model.Topic> topics(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<se.caboo.beast.model.Topic> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(se.caboo.beast.model.Topic.FORUM_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = se.caboo.beast.model.Topic.fetchTopics(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = topics();
      if (qualifier != null) {
        results = (NSArray<se.caboo.beast.model.Topic>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<se.caboo.beast.model.Topic>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToTopics(se.caboo.beast.model.Topic object) {
    includeObjectIntoPropertyWithKey(object, _Forum.TOPICS_KEY);
  }

  public void removeFromTopics(se.caboo.beast.model.Topic object) {
    excludeObjectFromPropertyWithKey(object, _Forum.TOPICS_KEY);
  }

  public void addToTopicsRelationship(se.caboo.beast.model.Topic object) {
    log.debug("adding {} to topics relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToTopics(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _Forum.TOPICS_KEY);
    }
  }

  public void removeFromTopicsRelationship(se.caboo.beast.model.Topic object) {
    log.debug("removing {} from topics relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromTopics(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _Forum.TOPICS_KEY);
    }
  }

  public se.caboo.beast.model.Topic createTopicsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  se.caboo.beast.model.Topic.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _Forum.TOPICS_KEY);
    return (se.caboo.beast.model.Topic) eo;
  }

  public void deleteTopicsRelationship(se.caboo.beast.model.Topic object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _Forum.TOPICS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllTopicsRelationships() {
    Enumeration<se.caboo.beast.model.Topic> objects = topics().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteTopicsRelationship(objects.nextElement());
    }
  }


  public static Forum createForum(EOEditingContext editingContext, String description
, String descriptionHtml
, String name
, Integer position
, Integer postsCount
, Integer topicsCount
) {
    Forum eo = (Forum) EOUtilities.createAndInsertInstance(editingContext, _Forum.ENTITY_NAME);
    eo.setDescription(description);
    eo.setDescriptionHtml(descriptionHtml);
    eo.setName(name);
    eo.setPosition(position);
    eo.setPostsCount(postsCount);
    eo.setTopicsCount(topicsCount);
    return eo;
  }

  public static ERXFetchSpecification<Forum> fetchSpec() {
    return new ERXFetchSpecification<Forum>(_Forum.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Forum> fetchAllForums(EOEditingContext editingContext) {
    return _Forum.fetchAllForums(editingContext, null);
  }

  public static NSArray<Forum> fetchAllForums(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Forum.fetchForums(editingContext, null, sortOrderings);
  }

  public static NSArray<Forum> fetchForums(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Forum> fetchSpec = new ERXFetchSpecification<Forum>(_Forum.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Forum> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Forum fetchForum(EOEditingContext editingContext, String keyName, Object value) {
    return _Forum.fetchForum(editingContext, ERXQ.equals(keyName, value));
  }

  public static Forum fetchForum(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Forum> eoObjects = _Forum.fetchForums(editingContext, qualifier, null);
    Forum eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Forum that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Forum fetchRequiredForum(EOEditingContext editingContext, String keyName, Object value) {
    return _Forum.fetchRequiredForum(editingContext, ERXQ.equals(keyName, value));
  }

  public static Forum fetchRequiredForum(EOEditingContext editingContext, EOQualifier qualifier) {
    Forum eoObject = _Forum.fetchForum(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Forum that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Forum localInstanceIn(EOEditingContext editingContext, Forum eo) {
    Forum localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
