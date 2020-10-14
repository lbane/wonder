// DO NOT EDIT.  Make changes to Post.java instead.
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
public abstract class _Post extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "Post";

  // Attribute Keys
  public static final ERXKey<String> BODY = new ERXKey<String>("body", Type.Attribute);
  public static final ERXKey<String> BODY_HTML = new ERXKey<String>("bodyHtml", Type.Attribute);
  public static final ERXKey<NSTimestamp> CREATED_AT = new ERXKey<NSTimestamp>("createdAt", Type.Attribute);
  public static final ERXKey<NSTimestamp> UPDATED_AT = new ERXKey<NSTimestamp>("updatedAt", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<se.caboo.beast.model.Forum> FORUM = new ERXKey<se.caboo.beast.model.Forum>("forum", Type.ToOneRelationship);
  public static final ERXKey<se.caboo.beast.model.Topic> TOPIC = new ERXKey<se.caboo.beast.model.Topic>("topic", Type.ToOneRelationship);
  public static final ERXKey<se.caboo.beast.model.User> USER = new ERXKey<se.caboo.beast.model.User>("user", Type.ToOneRelationship);

  // Attributes
  public static final String BODY_KEY = BODY.key();
  public static final String BODY_HTML_KEY = BODY_HTML.key();
  public static final String CREATED_AT_KEY = CREATED_AT.key();
  public static final String UPDATED_AT_KEY = UPDATED_AT.key();

  // Relationships
  public static final String FORUM_KEY = FORUM.key();
  public static final String TOPIC_KEY = TOPIC.key();
  public static final String USER_KEY = USER.key();

  private static final Logger log = LoggerFactory.getLogger(_Post.class);

  public Post localInstanceIn(EOEditingContext editingContext) {
    Post localInstance = (Post)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String body() {
    return (String) storedValueForKey(_Post.BODY_KEY);
  }

  public void setBody(String value) {
    log.debug( "updating body from {} to {}", body(), value);
    takeStoredValueForKey(value, _Post.BODY_KEY);
  }

  public String bodyHtml() {
    return (String) storedValueForKey(_Post.BODY_HTML_KEY);
  }

  public void setBodyHtml(String value) {
    log.debug( "updating bodyHtml from {} to {}", bodyHtml(), value);
    takeStoredValueForKey(value, _Post.BODY_HTML_KEY);
  }

  public NSTimestamp createdAt() {
    return (NSTimestamp) storedValueForKey(_Post.CREATED_AT_KEY);
  }

  public void setCreatedAt(NSTimestamp value) {
    log.debug( "updating createdAt from {} to {}", createdAt(), value);
    takeStoredValueForKey(value, _Post.CREATED_AT_KEY);
  }

  public NSTimestamp updatedAt() {
    return (NSTimestamp) storedValueForKey(_Post.UPDATED_AT_KEY);
  }

  public void setUpdatedAt(NSTimestamp value) {
    log.debug( "updating updatedAt from {} to {}", updatedAt(), value);
    takeStoredValueForKey(value, _Post.UPDATED_AT_KEY);
  }

  public se.caboo.beast.model.Forum forum() {
    return (se.caboo.beast.model.Forum)storedValueForKey(_Post.FORUM_KEY);
  }

  public void setForum(se.caboo.beast.model.Forum value) {
    takeStoredValueForKey(value, _Post.FORUM_KEY);
  }

  public void setForumRelationship(se.caboo.beast.model.Forum value) {
    log.debug("updating forum from {} to {}", forum(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setForum(value);
    }
    else if (value == null) {
      se.caboo.beast.model.Forum oldValue = forum();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Post.FORUM_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Post.FORUM_KEY);
    }
  }

  public se.caboo.beast.model.Topic topic() {
    return (se.caboo.beast.model.Topic)storedValueForKey(_Post.TOPIC_KEY);
  }

  public void setTopic(se.caboo.beast.model.Topic value) {
    takeStoredValueForKey(value, _Post.TOPIC_KEY);
  }

  public void setTopicRelationship(se.caboo.beast.model.Topic value) {
    log.debug("updating topic from {} to {}", topic(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setTopic(value);
    }
    else if (value == null) {
      se.caboo.beast.model.Topic oldValue = topic();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Post.TOPIC_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Post.TOPIC_KEY);
    }
  }

  public se.caboo.beast.model.User user() {
    return (se.caboo.beast.model.User)storedValueForKey(_Post.USER_KEY);
  }

  public void setUser(se.caboo.beast.model.User value) {
    takeStoredValueForKey(value, _Post.USER_KEY);
  }

  public void setUserRelationship(se.caboo.beast.model.User value) {
    log.debug("updating user from {} to {}", user(), value);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      setUser(value);
    }
    else if (value == null) {
      se.caboo.beast.model.User oldValue = user();
      if (oldValue != null) {
        removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _Post.USER_KEY);
      }
    } else {
      addObjectToBothSidesOfRelationshipWithKey(value, _Post.USER_KEY);
    }
  }


  public static Post createPost(EOEditingContext editingContext, String body
, String bodyHtml
, NSTimestamp createdAt
, NSTimestamp updatedAt
, se.caboo.beast.model.Forum forum, se.caboo.beast.model.Topic topic, se.caboo.beast.model.User user) {
    Post eo = (Post) EOUtilities.createAndInsertInstance(editingContext, _Post.ENTITY_NAME);
    eo.setBody(body);
    eo.setBodyHtml(bodyHtml);
    eo.setCreatedAt(createdAt);
    eo.setUpdatedAt(updatedAt);
    eo.setForumRelationship(forum);
    eo.setTopicRelationship(topic);
    eo.setUserRelationship(user);
    return eo;
  }

  public static ERXFetchSpecification<Post> fetchSpec() {
    return new ERXFetchSpecification<Post>(_Post.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<Post> fetchAllPosts(EOEditingContext editingContext) {
    return _Post.fetchAllPosts(editingContext, null);
  }

  public static NSArray<Post> fetchAllPosts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Post.fetchPosts(editingContext, null, sortOrderings);
  }

  public static NSArray<Post> fetchPosts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<Post> fetchSpec = new ERXFetchSpecification<Post>(_Post.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<Post> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static Post fetchPost(EOEditingContext editingContext, String keyName, Object value) {
    return _Post.fetchPost(editingContext, ERXQ.equals(keyName, value));
  }

  public static Post fetchPost(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Post> eoObjects = _Post.fetchPosts(editingContext, qualifier, null);
    Post eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Post that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Post fetchRequiredPost(EOEditingContext editingContext, String keyName, Object value) {
    return _Post.fetchRequiredPost(editingContext, ERXQ.equals(keyName, value));
  }

  public static Post fetchRequiredPost(EOEditingContext editingContext, EOQualifier qualifier) {
    Post eoObject = _Post.fetchPost(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Post that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Post localInstanceIn(EOEditingContext editingContext, Post eo) {
    Post localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
