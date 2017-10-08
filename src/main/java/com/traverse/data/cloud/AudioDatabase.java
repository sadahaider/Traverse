package com.traverse.data.cloud;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.traverse.data.Audio;
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
        update(audio);
    }

    public String update(Audio audio){
        return dbClient.getDynamoDB().getTable(tableName).putItem(Item.fromJSON(audio.toJson())).toString();
    }

    public String getAudioJson(String audioID) {
        Item item = dbClient.getDynamoDB().getTable(tableName).getItem(Audio.DB_IDENTIFIER_AUDIO_ID, audioID);
        if (item == null){
            return null;
        }
        return item.toJSON();
    }

    public Audio getAudio(String audioID) {
        return Audio.fromJSON(getAudioJson(audioID));
    }
}
