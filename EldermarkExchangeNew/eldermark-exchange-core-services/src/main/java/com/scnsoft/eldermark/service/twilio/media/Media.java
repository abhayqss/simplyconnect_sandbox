package com.scnsoft.eldermark.service.twilio.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.base.Resource;
import com.twilio.converter.DateConverter;
import com.twilio.converter.Promoter;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * For some reason Twilio SDK doesn't include ability to work with media similar to other Conversation resources.
 * This Resource is implemented in a way similar to Resources in Twilio SDK.
 */
public class Media extends Resource {

    public enum WebhookEnabledType {
        TRUE("true"),
        FALSE("false");

        private final String value;

        private WebhookEnabledType(final String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        /**
         * Generate a WebhookEnabledType from a string.
         *
         * @param value string value
         * @return generated WebhookEnabledType
         */
        @JsonCreator
        public static Media.WebhookEnabledType forValue(final String value) {
            return Promoter.enumFromString(value, Media.WebhookEnabledType.values());
        }
    }


    /**
     * Create a MediaFetcher to execute fetch.
     *
     * @param pathChatServiceSid The SID of the Conversation Service that the
     *                           resource is associated with.
     * @param pathSid            A 34 character string that uniquely identifies this resource.
     * @return MediaFetcher capable of executing the fetch
     */
    public static MediaFetcher fetcher(final String pathChatServiceSid,
                                       final String pathSid) {
        return new MediaFetcher(pathChatServiceSid, pathSid);
    }

    public static MediaCreator creator(final String pathChatServiceSid) {
        return new MediaCreator(pathChatServiceSid);
    }

    public static MediaContentDownloader contentDownloader(final Media media) {
        return new MediaContentDownloader(media);
    }

    /**
     * Converts a JSON String into a Media object using the provided
     * ObjectMapper.
     *
     * @param json         Raw JSON String
     * @param objectMapper Jackson ObjectMapper
     * @return Media object represented by the provided JSON
     */
    public static Media fromJson(final String json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, Media.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }

    /**
     * Converts a JSON InputStream into a Media object using the provided
     * ObjectMapper.
     *
     * @param json         Raw JSON InputStream
     * @param objectMapper Jackson ObjectMapper
     * @return Media object represented by the provided JSON
     */
    public static Media fromJson(final InputStream json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, Media.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }

    private final String sid;
    private final String chatServiceSid;
    private final ZonedDateTime dateCreated;
    private final ZonedDateTime dateUploadUpdated;
    private final ZonedDateTime dateUpdated;
    private final Map<String, String> links;
    private final Long size;
    private final String contentType;
    private final String filename;
    private final String author;
    private final String category;
    private final String messageSid;
    private final String channelSid;
    private final URI url;
    private final Boolean isMultipartUpstream;

    @JsonCreator
    public Media(@JsonProperty("sid") final String sid,
                 @JsonProperty("service_sid") final String chatServiceSid,
                 @JsonProperty("date_created") final String dateCreated,
                 @JsonProperty("date_upload_updated") final String dateUploadUpdated,
                 @JsonProperty("date_updated") final String dateUpdated,
                 @JsonProperty("links") final Map<String, String> links,
                 @JsonProperty("size") final Long size,
                 @JsonProperty("content_type") final String contentType,
                 @JsonProperty("filename") final String filename,
                 @JsonProperty("author") final String author,
                 @JsonProperty("category") final String category,
                 @JsonProperty("message_sid") final String messageSid,
                 @JsonProperty("channel_sid") final String channelSid,
                 @JsonProperty("url") final URI url,
                 @JsonProperty("is_multipart_upstream") final Boolean isMultipartUpstream) {
        this.sid = sid;
        this.chatServiceSid = chatServiceSid;
        this.dateCreated = DateConverter.iso8601DateTimeFromString(dateCreated);
        this.dateUploadUpdated = DateConverter.iso8601DateTimeFromString(dateUploadUpdated);
        this.dateUpdated = DateConverter.iso8601DateTimeFromString(dateUpdated);
        this.links = links;
        this.size = size;
        this.contentType = contentType;
        this.filename = filename;
        this.author = author;
        this.category = category;
        this.messageSid = messageSid;
        this.channelSid = channelSid;
        this.url = url;
        this.isMultipartUpstream = isMultipartUpstream;
    }

    public String getSid() {
        return sid;
    }

    public String getChatServiceSid() {
        return chatServiceSid;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public ZonedDateTime getDateUploadUpdated() {
        return dateUploadUpdated;
    }

    public ZonedDateTime getDateUpdated() {
        return dateUpdated;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public Long getSize() {
        return size;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFilename() {
        return filename;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getMessageSid() {
        return messageSid;
    }

    public String getChannelSid() {
        return channelSid;
    }

    public URI getUrl() {
        return url;
    }

    public Boolean getMultipartUpstream() {
        return isMultipartUpstream;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return Objects.equals(sid, media.sid) &&
                Objects.equals(chatServiceSid, media.chatServiceSid) &&
                Objects.equals(dateCreated, media.dateCreated) &&
                Objects.equals(dateUploadUpdated, media.dateUploadUpdated) &&
                Objects.equals(dateUpdated, media.dateUpdated) &&
                Objects.equals(links, media.links) &&
                Objects.equals(size, media.size) &&
                Objects.equals(contentType, media.contentType) &&
                Objects.equals(filename, media.filename) &&
                Objects.equals(author, media.author) &&
                Objects.equals(category, media.category) &&
                Objects.equals(messageSid, media.messageSid) &&
                Objects.equals(channelSid, media.channelSid) &&
                Objects.equals(url, media.url) &&
                Objects.equals(isMultipartUpstream, media.isMultipartUpstream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid,
                chatServiceSid,
                dateCreated,
                dateUploadUpdated,
                dateUpdated,
                links,
                size,
                contentType,
                filename,
                author,
                category,
                messageSid,
                channelSid,
                url,
                isMultipartUpstream
        );
    }
}
