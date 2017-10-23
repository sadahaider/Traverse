package com.traverse.data.cloud;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.traverse.data.Audio;
import com.traverse.utils.TimeUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;


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


    public boolean upload(MultipartFile multipartFile, Audio audio) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        if (!extension.equals("wav") && !extension.equals("mp3")){
            System.out.println(extension + " is not accepted!");
            return false;
        }

        objectMetadata.addUserMetadata("type", extension);
        dbClient.getS3client().putObject(new PutObjectRequest(dbClient.getBucketName(), audio.getId(), multipartFile.getInputStream(), objectMetadata));
        return true;
    }

    public boolean uploadImage(MultipartFile multipartFile, Audio audio) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        if (extension == null){
            return false;
        }



        if (!Arrays.asList("jpeg", "jpg", "png").contains(extension.toLowerCase())){
            return false;
        }

        objectMetadata.addUserMetadata("type", extension);
        dbClient.getS3client().putObject(new PutObjectRequest(dbClient.getBucketName(), "image/" + audio.getId(), multipartFile.getInputStream(), objectMetadata));
        return true;
    }



    public String getURL(String audioID) throws IOException {
        return dbClient.getS3client().getUrl(dbClient.getBucketName(), audioID).toString();
    }

    public S3Object getAudioS3Object(String audioID) throws IOException {
        return dbClient.getS3client().getObject(new GetObjectRequest(dbClient.getBucketName(), audioID));
    }

    public S3Object getAudioS3ObjectImage(String audioID) throws IOException {
        return dbClient.getS3client().getObject(new GetObjectRequest(dbClient.getBucketName(), "image/" + audioID));
    }


    /**
     *
     * @param limit limit to search for.
     * @param startingMillis starting time to search
     * @return
     */
    public String list(int limit, long startingMillis){
        int results = 0;

        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Table table = dbClient.getDynamoDB().getTable(tableName);
        Index index = table.getIndex(Audio.DB_IDENTIFIER_UPLOAD_DATE);

        long
                startingUnixTime = startingMillis != -1 ? startingMillis : System.currentTimeMillis(),
                startingDate = startingUnixTime - (startingUnixTime % TimeUtils.MILLIS_IN_DAY),
                offsetDays = startingMillis == -1 ? -1 : 0;

        while (results < limit && offsetDays < 14){
            long dateInMillis = startingDate - (offsetDays * TimeUtils.MILLIS_IN_DAY);
            QuerySpec spec = new QuerySpec().withHashKey(Audio.DB_IDENTIFIER_UPLOAD_DATE, dateInMillis).withScanIndexForward(false);
            ItemCollection<QueryOutcome> items = index.query(spec);
            Iterator<Item> iter = items.iterator();

            while (iter.hasNext() && results < limit) {
                Item item = iter.next();
                Audio audio = Audio.fromJSON(item.toJSON());
                long uploadTimeUnix = audio.getUploadTime() + audio.getUploadDate();
                if (startingMillis != -1 && uploadTimeUnix >= startingMillis){
                    System.out.println("Song: " + audio.getName() + " was uploaded " + uploadTimeUnix + " vs " + startingMillis);
                    continue;
                }
                jsonArray.put(new JSONObject(item.toJSON()));
                results++;
            }

            offsetDays++;
        }

        return object.put("results", jsonArray).toString();
    }

}
