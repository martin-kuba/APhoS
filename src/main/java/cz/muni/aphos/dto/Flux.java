package cz.muni.aphos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cz.muni.aphos.helper.ViewField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Data Transfer Object for storing the information about flux containing
 * right ascension, declination, apertures, deviations, username and photo
 */
@Validated
//@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-12-23T18:56:03.993Z[GMT]")
//generated in early stages of project by swagger, changed a lot since then
public class Flux {
    @JsonProperty(value = "rightAsc", required = true)
    @JsonView(ViewField.Public.class)
    private String rightAsc = null;

    @JsonProperty(value = "declination", required = true)
    @JsonView(ViewField.Public.class)
    private String declination = null;

    @JsonProperty(value = "addedBy", required = true)
    @JsonView(ViewField.Public.class)
    // username from User
    private String addedBy = null;

    @JsonProperty(value = "apAuto", required = true)
    @JsonView(ViewField.Public.class)
    private Double apAuto = null;

    @JsonProperty(value = "apertures", required = true)
    @JsonView(ViewField.Public.class)
    @Valid
    private List<Double> apertures = null;

    @JsonProperty(value = "photo", required = true)
    @JsonView(ViewField.Public.class)
    private PhotoProperties photo = null;

    @JsonProperty(value = "apAutoDev", required = true)
    @JsonView(ViewField.Public.class)
    @Nullable
    private Double apAutoDev = null;

    @JsonProperty(value = "apertureDevs", required = true)
    @JsonView(ViewField.Public.class)
    @Nullable
    private List<Double> apertureDevs = null;

    public Flux rightAsc(String rightAsc) {
        this.rightAsc = rightAsc;
        return this;
    }

    /**
     * Get rightAsc
     *
     * @return rightAsc
     **/
    @Schema(description = "")

    public String getRightAsc() {
        return rightAsc;
    }

    public void setRightAsc(String rightAsc) {
        this.rightAsc = rightAsc;
    }

    public Flux declination(String declination) {
        this.declination = declination;
        return this;
    }

    /**
     * Get declination
     *
     * @return declination
     **/
    @Schema(description = "")

    public String getDeclination() {
        return declination;
    }

    public void setDeclination(String declination) {
        this.declination = declination;
    }

    public Flux addedBy(String addedBy) {
        this.addedBy = addedBy;
        return this;
    }

    /**
     * Get addedBy
     *
     * @return addedBy
     **/
    @Schema(description = "")

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Flux apAuto(Double apAuto) {
        this.apAuto = apAuto;
        return this;
    }

    /**
     * Get apAuto
     *
     * @return apAuto
     **/
    @Schema(description = "")

    public Double getApAuto() {
        return apAuto;
    }

    public void setApAuto(Double apAuto) {
        this.apAuto = apAuto;
    }

    public Flux apertures(List<Double> apertures) {
        this.apertures = apertures;
        return this;
    }

    public Flux addAperturesItem(Double aperturesItem) {
        if (this.apertures == null) {
            this.apertures = new ArrayList<Double>();
        }
        this.apertures.add(aperturesItem);
        return this;
    }

    /**
     * Get apertures
     *
     * @return apertures
     **/
    @Schema(description = "")

    public List<Double> getApertures() {
        return apertures;
    }

    public void setApertures(List<Double> apertures) {
        this.apertures = apertures;
    }

    public Flux photo(PhotoProperties photo) {
        this.photo = photo;
        return this;
    }

    /**
     * Get photo
     *
     * @return photo
     **/
    @Schema(description = "")

    @Valid
    public PhotoProperties getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoProperties photo) {
        this.photo = photo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Flux flux = (Flux) o;
        return Objects.equals(this.rightAsc, flux.rightAsc) &&
                Objects.equals(this.declination, flux.declination) &&
                Objects.equals(this.addedBy, flux.addedBy) &&
                Objects.equals(this.apAuto, flux.apAuto) &&
                Objects.equals(this.apertures, flux.apertures) &&
                Objects.equals(this.photo, flux.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rightAsc, declination, addedBy, apAuto, apertures, photo);
    }

    @Override
    public String toString() {

      String sb = "class Flux {\n" +
              "    rightAsc: " + toIndentedString(rightAsc) + "\n" +
              "    declination: " + toIndentedString(declination) + "\n" +
              "    addedBy: " + toIndentedString(addedBy) + "\n" +
              "    apAuto: " + toIndentedString(apAuto) + "\n" +
              "    apertures: " + toIndentedString(apertures) + "\n" +
              "    photo: " + toIndentedString(photo) + "\n" +
              "}";
        return sb;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public Double getApAutoDev() {
        return apAutoDev;
    }

    public void setApAutoDev(Double apAutoDev) {
        this.apAutoDev = apAutoDev;
    }

    public List<Double> getApertureDevs() {
        return apertureDevs;
    }

    public void setApertureDevs(List<Double> apertureDevs) {
        this.apertureDevs = apertureDevs;
    }
}
