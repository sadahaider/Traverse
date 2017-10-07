package com.traverse.data.cloud;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.traverse.data.Audio;
import com.traverse.exceptions.UserDoesNotExistException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class AudioDatabase {

    @Autowired
    private DBClient dbClient;

    private String tableName;

    public AudioDatabase(@Value("${cloud.dynamoDB_table_name_audio}") String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void create(Audio audio){
        dbClient.getMapper().save(audio);
    }

    public void update(Audio audio){
        dbClient.getMapper().save(audio);
    }

    public String getAudioJson(String audioID) {
        Item item = dbClient.getDynamoDB().getTable(tableName).getItem(new GetItemSpec().withPrimaryKey(new KeyAttribute(Audio.DB_IDENTIFIER_AUDIO_ID, audioID)));
        if (item == null){
            return null;
        }
        return item.toJSON();
    }

    public Audio getAudio(String audioID) throws UserDoesNotExistException {
        return dbClient.getMapper().load(Audio.class, audioID);
    }
}
