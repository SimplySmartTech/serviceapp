package com.simplysmart.service.database;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by shailendrapsp on 7/11/16.
 */

public class MigrationRealm implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if(oldVersion ==0){
//            RealmObjectSchema
        }
    }
}
