/* 
    PostgresqlPlugin v1.2
    Copyright (C) 2001 Kenny Leung

    This bundle is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License version 2.1 as published by the Free Software Foundation.
 
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.webobjects.jdbcadaptor;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
//import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.eoaccess.synchronization.EOSchemaSynchronizationFactory;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

public class PostgresqlSchemaSynchronizationFactory extends EOSchemaSynchronizationFactory {

    public PostgresqlSchemaSynchronizationFactory(EOAdaptor adaptor) {
        super(adaptor);
    }
    /**
     * <code>PostgresqlExpression</code> factory method.
     * 
     * @param entity
     *            the entity to which <code>PostgresqlExpression</code> is to
     *            be rooted
     * @param statement
     *            the SQL statement
     * @return a <code>PostgresqlExpression</code> rooted to
     *         <code>entity</code>
     */
    private static PostgresqlExpression createExpression(EOEntity entity, String statement) {
        PostgresqlExpression result = new PostgresqlExpression(entity);
        result.setStatement(statement);
        return result;
    }

    @Override
    public boolean  supportsSchemaSynchronization() {
        return false;
    }

    @SuppressWarnings("unused")
    private String getPrimaryKeyConstraintName(EOEntity entity) {
        NSArray<EOAttribute> pks = entity.primaryKeyAttributes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pks.count(); ++i) {
            EOAttribute attr = pks.objectAtIndex(i);
            String attrName = attr.columnName();
            if (attrName == null) {
                attrName = attr.name();
            }
            sb.append(attrName);
            sb.append("_");
        }
        sb.append("pk");
        return sb.toString();
    }
    
    private String getSequenceName(EOEntity entity) {
        String sequenceName;
        
        if (entity.userInfo() != null) {
            sequenceName = (String)entity.userInfo().objectForKey("sequenceName");
        }
        else {
            sequenceName = null;
        }
        if (sequenceName == null) {
            sequenceName = entity.primaryKeyRootName() + "_seq";
        }
        return sequenceName;
    }
    
    private void addAttributeNames(NSArray<EOAttribute> attributes, StringBuilder sb) {
        for (int i = 0; i < attributes.count(); ++i) {
            if (i > 0) sb.append(", ");
            sb.append(attributes.objectAtIndex(i).columnName());
        }
    }
    
    @Override
    public NSArray<EOSQLExpression> dropTableStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
        NSMutableArray<EOSQLExpression> results = new NSMutableArray<EOSQLExpression>();
        int count = entityGroup.count();
        for (int i = 0 ; i < count ; i++ ) {
            EOEntity entity = entityGroup.objectAtIndex(i);
            results.addObject(createExpression(entity, "DROP TABLE " + entity.externalName()));
        }
        return results;
    }
    
    @Override
    public NSArray<EOSQLExpression> primaryKeyConstraintStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
        NSMutableArray<EOSQLExpression> results = new NSMutableArray<EOSQLExpression>();
        int count = entityGroup.count();
        for (int i = 0 ; i < count ; i++ ) {
            EOEntity entity = entityGroup.objectAtIndex(i);
            StringBuilder statement = new StringBuilder("ALTER TABLE ").append(entity.externalName()).append(" ADD PRIMARY KEY (");
                // Der Constraint-Name verursacht Probleme, bzw. muss aus mehr Eiheiten bestehen, als zur Zeit
                // also lassen wir ihn ganz weg
                // " ADD CONSTRAINT "+ getPrimaryKeyConstraintName(entity) +" PRIMARY KEY (";
            NSArray<EOAttribute> priKeyAttributes = entity.primaryKeyAttributes();
            int priKeyAttributeCount = priKeyAttributes.count();
            for (int j = 0 ; j < priKeyAttributeCount ; j++ ) {
                EOAttribute priKeyAttribute = priKeyAttributes.objectAtIndex(j);
                statement.append(priKeyAttribute.columnName());
                if ( j < priKeyAttributeCount - 1 ) {
                    statement.append(", ");
                } else {
                    statement.append(")");
                }
            }
            results.addObject(createExpression(entity, statement.toString()));
        }
        return results;
    }

    @Override
    public NSArray<EOSQLExpression> primaryKeySupportStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
        NSMutableArray<EOSQLExpression> results = new NSMutableArray<EOSQLExpression>();
        int count = entityGroup.count();
        for (int i = 0 ; i < count ; i++ ) {
            EOEntity entity = entityGroup.objectAtIndex(i);
            NSArray<EOAttribute> priKeyAttributes = entity.primaryKeyAttributes();
            boolean noSequenceCreate = false;
            if (entity.userInfo() != null) {
                noSequenceCreate = entity.userInfo().objectForKey("noSequenceCreation") != null;
            }
            if ( priKeyAttributes.count() == 1 && !noSequenceCreate) {
                // If you have a single primary key, we can add some smarts. If not, you're on your own.
                // We'd like to add comments into the stream, but EOModeler freezes when trying to execute comments to PosgreSQL.
                //results.addObject(new PostgresqlExpression("/* Create primary key support for " + entity.name() + " */"));
                EOAttribute priKeyAttribute = priKeyAttributes.objectAtIndex(0);
                results.addObject(createExpression(entity, "CREATE FUNCTION ID_MAX() RETURNS " + priKeyAttribute.externalType() + "\n    AS 'SELECT MAX(" + priKeyAttribute.columnName() +") FROM " + entity.externalName() + "'\n    LANGUAGE 'sql'"));
                
                String sequenceName = getSequenceName(entity);
                
                results.addObject(createExpression(entity, "CREATE SEQUENCE " + sequenceName));
                results.addObject(createExpression(entity, "SELECT SETVAL('" + sequenceName + "', ID_MAX()) INTO TEMP TMP_TABLE"));
                results.addObject(createExpression(entity, "DROP TABLE TMP_TABLE"));
                // We'd like to add comments into the stream, but EOModeler freezes when trying to execute comments to PosgreSQL.
                //results.addObject(new PostgresqlExpression("/* End create primary key support for " + entity.name() + " */"));
                results.addObject(createExpression(entity, "DROP FUNCTION ID_MAX()"));
                
                NSDictionary<String, Object> userInfo = entity.userInfo();
                if (userInfo != null) {
                    String grant = (String)userInfo.objectForKey("grantAll");
                    if (grant != null) {
                        String statement = "GRANT ALL ON "+sequenceName+" TO "+grant;
                        results.addObject(createExpression(entity, statement));
                    }
                }
            }
        }
        return results;
    }

    @Override
    public NSArray<EOSQLExpression> dropPrimaryKeySupportStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
        NSMutableArray<EOSQLExpression> results = new NSMutableArray<EOSQLExpression>();
        int count = entityGroup.count();
        for (int i = 0 ; i < count ; i++ ) {
            EOEntity entity = entityGroup.objectAtIndex(i);
            boolean noSequenceCreate = false;
            if (entity.userInfo() != null) {
                noSequenceCreate = entity.userInfo().objectForKey("noSequenceCreation") != null;
            }
            if (entity.primaryKeyAttributes().count() == 1 && !noSequenceCreate) {
                String sequenceName = getSequenceName(entity);
                results.addObject(createExpression(entity, "DROP SEQUENCE " + sequenceName));
            }
        }
        return results;
    }
    
    /**
     * Die geerbte Implementierung erzeugt u.U. doppelte Namen fuer den Constraint
     * Da mit PgSQL der Name optional ist, wird er weggelassen. Der einfachste
     * Weg dahin ist die Expression selber aufzubauen.
     */
    @Override
    public NSArray<EOSQLExpression> foreignKeyConstraintStatementsForRelationship(EORelationship relationship) {
        /*
        NSMutableArray results = new NSMutableArray();
        NSArray superResults = super.foreignKeyConstraintStatementsForRelationship(relationship);
        int count = superResults.count();
        for (int i = 0 ; i < count ; i++ ) {
            EOSQLExpression expression = (EOSQLExpression)superResults.objectAtIndex(i);
            results.addObject(new PostgresqlExpression(expression.statement() + " INITIALLY DEFERRED"));
        }
        return results;
         */
        
        // ALTER TABLE prostata4.bogen ADD 
        // CONSTRAINT bogen_biopsie_FK 
        // FOREIGN KEY (ref_biopsie) 
        // REFERENCES prostata4.biopsie (nr) 
        // INITIALLY DEFERRED;

        if (!relationship.isToMany() && !relationship.isFlattened()) {
            StringBuilder sb = new StringBuilder();
            sb.append("ALTER TABLE ");
            sb.append(relationship.entity().externalName());
            sb.append(" ADD FOREIGN KEY (");
            addAttributeNames(relationship.sourceAttributes(), sb);
            sb.append(") REFERENCES ");
            sb.append(relationship.destinationEntity().externalName());
            sb.append(" (");
            addAttributeNames(relationship.destinationAttributes(), sb);
            sb.append(") INITIALLY DEFERRED");
            return new NSArray<EOSQLExpression>(createExpression(relationship.entity(), sb.toString()));
        }
        return new NSArray<EOSQLExpression>();
    }

    /**
     * GRANT ALL Support einbauen.
     * Um den Schluessel "grantAll" im UserInfo verwenden zu koennen muss
     * die Tabellen-Definition erweitert werden.
     */
    @Override
    public NSArray<EOSQLExpression> createTableStatementsForEntityGroup(NSArray<EOEntity> entityGroup) {
        final NSMutableArray<EOSQLExpression> statements = new NSMutableArray<EOSQLExpression>(super.createTableStatementsForEntityGroup(entityGroup));
        for (int i = 0; i < entityGroup.count(); ++i) {
            final EOEntity entity = entityGroup.objectAtIndex(i);
            final NSDictionary<String, Object> userInfo = entity.userInfo();
            if (userInfo != null) {
                final String grant = (String)userInfo.objectForKey("grantAll");
                if (grant != null) {
                    final String statement = "GRANT ALL ON "+entity.externalName()+" TO "+grant;
                    statements.addObject(createExpression(entity, statement));
                }
            }
        }
        return statements;
    }
}
