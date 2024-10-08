// DO NOT EDIT.  Make changes to User.java instead.
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
public abstract class _User extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "User";

  // Attribute Keys
  public static final ERXKey<String> BIO = new ERXKey<String>("bio", Type.Attribute);
  public static final ERXKey<String> BIO_HTML = new ERXKey<String>("bioHtml", Type.Attribute);
  public static final ERXKey<NSTimestamp> CREATED_AT = new ERXKey<NSTimestamp>("createdAt", Type.Attribute);
  public static final ERXKey<String> DISPLAY_NAME = new ERXKey<String>("displayName", Type.Attribute);
  public static final ERXKey<NSTimestamp> LAST_LOGIN_AT = new ERXKey<NSTimestamp>("lastLoginAt", Type.Attribute);
  public static final ERXKey<NSTimestamp> LAST_SEEN_AT = new ERXKey<NSTimestamp>("lastSeenAt", Type.Attribute);
  public static final ERXKey<String> LOGIN = new ERXKey<String>("login", Type.Attribute);
  public static final ERXKey<Integer> POSTS_COUNT = new ERXKey<Integer>("postsCount", Type.Attribute);
  public static final ERXKey<NSTimestamp> UPDATED_AT = new ERXKey<NSTimestamp>("updatedAt", Type.Attribute);
  public static final ERXKey<String> WEBSITE = new ERXKey<String>("website", Type.Attribute);

  // Relationship Keys
  public static final ERXKey<se.caboo.beast.model.Post> POSTS = new ERXKey<se.caboo.beast.model.Post>("posts", Type.ToManyRelationship);
  public static final ERXKey<se.caboo.beast.model.Topic> REPLIED_TO_TOPICS = new ERXKey<se.caboo.beast.model.Topic>("repliedToTopics", Type.ToManyRelationship);
  public static final ERXKey<se.caboo.beast.model.Topic> TOPICS = new ERXKey<se.caboo.beast.model.Topic>("topics", Type.ToManyRelationship);

  // Attributes
  public static final String BIO_KEY = BIO.key();
  public static final String BIO_HTML_KEY = BIO_HTML.key();
  public static final String CREATED_AT_KEY = CREATED_AT.key();
  public static final String DISPLAY_NAME_KEY = DISPLAY_NAME.key();
  public static final String LAST_LOGIN_AT_KEY = LAST_LOGIN_AT.key();
  public static final String LAST_SEEN_AT_KEY = LAST_SEEN_AT.key();
  public static final String LOGIN_KEY = LOGIN.key();
  public static final String POSTS_COUNT_KEY = POSTS_COUNT.key();
  public static final String UPDATED_AT_KEY = UPDATED_AT.key();
  public static final String WEBSITE_KEY = WEBSITE.key();

  // Relationships
  public static final String POSTS_KEY = POSTS.key();
  public static final String REPLIED_TO_TOPICS_KEY = REPLIED_TO_TOPICS.key();
  public static final String TOPICS_KEY = TOPICS.key();

  private static final Logger log = LoggerFactory.getLogger(_User.class);

  public User localInstanceIn(EOEditingContext editingContext) {
    User localInstance = (User)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String bio() {
    return (String) storedValueForKey(_User.BIO_KEY);
  }

  public void setBio(String value) {
    log.debug( "updating bio from {} to {}", bio(), value);
    takeStoredValueForKey(value, _User.BIO_KEY);
  }

  public String bioHtml() {
    return (String) storedValueForKey(_User.BIO_HTML_KEY);
  }

  public void setBioHtml(String value) {
    log.debug( "updating bioHtml from {} to {}", bioHtml(), value);
    takeStoredValueForKey(value, _User.BIO_HTML_KEY);
  }

  public NSTimestamp createdAt() {
    return (NSTimestamp) storedValueForKey(_User.CREATED_AT_KEY);
  }

  public void setCreatedAt(NSTimestamp value) {
    log.debug( "updating createdAt from {} to {}", createdAt(), value);
    takeStoredValueForKey(value, _User.CREATED_AT_KEY);
  }

  public String displayName() {
    return (String) storedValueForKey(_User.DISPLAY_NAME_KEY);
  }

  public void setDisplayName(String value) {
    log.debug( "updating displayName from {} to {}", displayName(), value);
    takeStoredValueForKey(value, _User.DISPLAY_NAME_KEY);
  }

  public NSTimestamp lastLoginAt() {
    return (NSTimestamp) storedValueForKey(_User.LAST_LOGIN_AT_KEY);
  }

  public void setLastLoginAt(NSTimestamp value) {
    log.debug( "updating lastLoginAt from {} to {}", lastLoginAt(), value);
    takeStoredValueForKey(value, _User.LAST_LOGIN_AT_KEY);
  }

  public NSTimestamp lastSeenAt() {
    return (NSTimestamp) storedValueForKey(_User.LAST_SEEN_AT_KEY);
  }

  public void setLastSeenAt(NSTimestamp value) {
    log.debug( "updating lastSeenAt from {} to {}", lastSeenAt(), value);
    takeStoredValueForKey(value, _User.LAST_SEEN_AT_KEY);
  }

  public String login() {
    return (String) storedValueForKey(_User.LOGIN_KEY);
  }

  public void setLogin(String value) {
    log.debug( "updating login from {} to {}", login(), value);
    takeStoredValueForKey(value, _User.LOGIN_KEY);
  }

  public Integer postsCount() {
    return (Integer) storedValueForKey(_User.POSTS_COUNT_KEY);
  }

  public void setPostsCount(Integer value) {
    log.debug( "updating postsCount from {} to {}", postsCount(), value);
    takeStoredValueForKey(value, _User.POSTS_COUNT_KEY);
  }

  public NSTimestamp updatedAt() {
    return (NSTimestamp) storedValueForKey(_User.UPDATED_AT_KEY);
  }

  public void setUpdatedAt(NSTimestamp value) {
    log.debug( "updating updatedAt from {} to {}", updatedAt(), value);
    takeStoredValueForKey(value, _User.UPDATED_AT_KEY);
  }

  public String website() {
    return (String) storedValueForKey(_User.WEBSITE_KEY);
  }

  public void setWebsite(String value) {
    log.debug( "updating website from {} to {}", website(), value);
    takeStoredValueForKey(value, _User.WEBSITE_KEY);
  }

  public NSArray<se.caboo.beast.model.Post> posts() {
    return (NSArray<se.caboo.beast.model.Post>)storedValueForKey(_User.POSTS_KEY);
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
      EOQualifier inverseQualifier = ERXQ.equals(se.caboo.beast.model.Post.USER_KEY, this);

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
    includeObjectIntoPropertyWithKey(object, _User.POSTS_KEY);
  }

  public void removeFromPosts(se.caboo.beast.model.Post object) {
    excludeObjectFromPropertyWithKey(object, _User.POSTS_KEY);
  }

  public void addToPostsRelationship(se.caboo.beast.model.Post object) {
    log.debug("adding {} to posts relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToPosts(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _User.POSTS_KEY);
    }
  }

  public void removeFromPostsRelationship(se.caboo.beast.model.Post object) {
    log.debug("removing {} from posts relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromPosts(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _User.POSTS_KEY);
    }
  }

  public se.caboo.beast.model.Post createPostsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  se.caboo.beast.model.Post.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _User.POSTS_KEY);
    return (se.caboo.beast.model.Post) eo;
  }

  public void deletePostsRelationship(se.caboo.beast.model.Post object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _User.POSTS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllPostsRelationships() {
    Enumeration<se.caboo.beast.model.Post> objects = posts().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePostsRelationship(objects.nextElement());
    }
  }

  public NSArray<se.caboo.beast.model.Topic> repliedToTopics() {
    return (NSArray<se.caboo.beast.model.Topic>)storedValueForKey(_User.REPLIED_TO_TOPICS_KEY);
  }

  public NSArray<se.caboo.beast.model.Topic> repliedToTopics(EOQualifier qualifier) {
    return repliedToTopics(qualifier, null, false);
  }

  public NSArray<se.caboo.beast.model.Topic> repliedToTopics(EOQualifier qualifier, boolean fetch) {
    return repliedToTopics(qualifier, null, fetch);
  }

  public NSArray<se.caboo.beast.model.Topic> repliedToTopics(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<se.caboo.beast.model.Topic> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = ERXQ.equals(se.caboo.beast.model.Topic.REPLIED_BY_KEY, this);

      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        fullQualifier = ERXQ.and(qualifier, inverseQualifier);
      }

      results = se.caboo.beast.model.Topic.fetchTopics(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = repliedToTopics();
      if (qualifier != null) {
        results = (NSArray<se.caboo.beast.model.Topic>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<se.caboo.beast.model.Topic>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }

  public void addToRepliedToTopics(se.caboo.beast.model.Topic object) {
    includeObjectIntoPropertyWithKey(object, _User.REPLIED_TO_TOPICS_KEY);
  }

  public void removeFromRepliedToTopics(se.caboo.beast.model.Topic object) {
    excludeObjectFromPropertyWithKey(object, _User.REPLIED_TO_TOPICS_KEY);
  }

  public void addToRepliedToTopicsRelationship(se.caboo.beast.model.Topic object) {
    log.debug("adding {} to repliedToTopics relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToRepliedToTopics(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _User.REPLIED_TO_TOPICS_KEY);
    }
  }

  public void removeFromRepliedToTopicsRelationship(se.caboo.beast.model.Topic object) {
    log.debug("removing {} from repliedToTopics relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromRepliedToTopics(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _User.REPLIED_TO_TOPICS_KEY);
    }
  }

  public se.caboo.beast.model.Topic createRepliedToTopicsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  se.caboo.beast.model.Topic.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _User.REPLIED_TO_TOPICS_KEY);
    return (se.caboo.beast.model.Topic) eo;
  }

  public void deleteRepliedToTopicsRelationship(se.caboo.beast.model.Topic object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _User.REPLIED_TO_TOPICS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllRepliedToTopicsRelationships() {
    Enumeration<se.caboo.beast.model.Topic> objects = repliedToTopics().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteRepliedToTopicsRelationship(objects.nextElement());
    }
  }

  public NSArray<se.caboo.beast.model.Topic> topics() {
    return (NSArray<se.caboo.beast.model.Topic>)storedValueForKey(_User.TOPICS_KEY);
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
      EOQualifier inverseQualifier = ERXQ.equals(se.caboo.beast.model.Topic.USER_KEY, this);

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
    includeObjectIntoPropertyWithKey(object, _User.TOPICS_KEY);
  }

  public void removeFromTopics(se.caboo.beast.model.Topic object) {
    excludeObjectFromPropertyWithKey(object, _User.TOPICS_KEY);
  }

  public void addToTopicsRelationship(se.caboo.beast.model.Topic object) {
    log.debug("adding {} to topics relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      addToTopics(object);
    }
    else {
      addObjectToBothSidesOfRelationshipWithKey(object, _User.TOPICS_KEY);
    }
  }

  public void removeFromTopicsRelationship(se.caboo.beast.model.Topic object) {
    log.debug("removing {} from topics relationship", object);
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
      removeFromTopics(object);
    }
    else {
      removeObjectFromBothSidesOfRelationshipWithKey(object, _User.TOPICS_KEY);
    }
  }

  public se.caboo.beast.model.Topic createTopicsRelationship() {
    EOEnterpriseObject eo = EOUtilities.createAndInsertInstance(editingContext(),  se.caboo.beast.model.Topic.ENTITY_NAME );
    addObjectToBothSidesOfRelationshipWithKey(eo, _User.TOPICS_KEY);
    return (se.caboo.beast.model.Topic) eo;
  }

  public void deleteTopicsRelationship(se.caboo.beast.model.Topic object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _User.TOPICS_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllTopicsRelationships() {
    Enumeration<se.caboo.beast.model.Topic> objects = topics().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteTopicsRelationship(objects.nextElement());
    }
  }


  public static User createUser(EOEditingContext editingContext, String bio
, String bioHtml
, NSTimestamp createdAt
, String displayName
, NSTimestamp lastLoginAt
, NSTimestamp lastSeenAt
, String login
, Integer postsCount
, NSTimestamp updatedAt
, String website
) {
    User eo = (User) EOUtilities.createAndInsertInstance(editingContext, _User.ENTITY_NAME);
    eo.setBio(bio);
    eo.setBioHtml(bioHtml);
    eo.setCreatedAt(createdAt);
    eo.setDisplayName(displayName);
    eo.setLastLoginAt(lastLoginAt);
    eo.setLastSeenAt(lastSeenAt);
    eo.setLogin(login);
    eo.setPostsCount(postsCount);
    eo.setUpdatedAt(updatedAt);
    eo.setWebsite(website);
    return eo;
  }

  public static ERXFetchSpecification<User> fetchSpec() {
    return new ERXFetchSpecification<User>(_User.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<User> fetchAllUsers(EOEditingContext editingContext) {
    return _User.fetchAllUsers(editingContext, null);
  }

  public static NSArray<User> fetchAllUsers(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _User.fetchUsers(editingContext, null, sortOrderings);
  }

  public static NSArray<User> fetchUsers(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<User> fetchSpec = new ERXFetchSpecification<User>(_User.ENTITY_NAME, qualifier, sortOrderings);
    NSArray<User> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static User fetchUser(EOEditingContext editingContext, String keyName, Object value) {
    return _User.fetchUser(editingContext, ERXQ.equals(keyName, value));
  }

  public static User fetchUser(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<User> eoObjects = _User.fetchUsers(editingContext, qualifier, null);
    User eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one User that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static User fetchRequiredUser(EOEditingContext editingContext, String keyName, Object value) {
    return _User.fetchRequiredUser(editingContext, ERXQ.equals(keyName, value));
  }

  public static User fetchRequiredUser(EOEditingContext editingContext, EOQualifier qualifier) {
    User eoObject = _User.fetchUser(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no User that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static User localInstanceIn(EOEditingContext editingContext, User eo) {
    User localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
