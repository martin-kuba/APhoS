package cz.muni.aphos.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * The Hibernate entity representing a user.
 */
@Validated
@Entity
@Table(name = "users", schema = "public")
public class User {

    /**
     * the Google internal id of the user used also as an id here
     */
    @JsonIgnore
    @Id
    private String googleSub;
    @JsonProperty(value = "username", required = true)
    private String username;

    /**
     * the description of the user's profile
     */
    @JsonProperty(value = "description", required = true)
    @Nullable
    private String description;

    public User(String googleSub) {
        this.googleSub = googleSub;
    }

    public User(String googleSub, String username) {
    }

    public User() {
    }

    public String getGoogleSub() {
        return googleSub;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(googleSub, user.googleSub)) return false;
        if (!Objects.equals(username, user.username)) return false;
        return Objects.equals(description, user.description);
    }

    @Override
    public int hashCode() {
        int result = googleSub != null ? googleSub.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
