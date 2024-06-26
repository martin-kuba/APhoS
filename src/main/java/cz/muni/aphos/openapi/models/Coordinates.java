package cz.muni.aphos.openapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Set;

/**
 * Coordinates object for right ascension (hour, minute, second), declination (degrees), radius (degrees) with
 * regexp restrictions and validator.
 */
@Schema(example = "{\"rightAsc\":\"21:41:55.291\",\"declination\":\"71:18:41.12\",\"radius\":0.05}")
@Validated
public class Coordinates {


    //Coordinates.class.getAnnotation(Schema.class).example()
    public static final String example = "{\"rightAsc\":\"21:41:55.291\",\"declination\":\"71:18:41.12\",\"radius\":0.05}";

    private static final ValidatorFactory fac = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = fac.getValidator();

    @Pattern(regexp = "\\d{1,2}:\\d{2}:\\d{2}([.]\\d+)?|^$")
    @JsonProperty(required = true)
    private String rightAsc = "";

    @Pattern(regexp = "[+-]?\\d{1,3}:\\d{2}:\\d{2}([.]\\d+)?|^$")
    @JsonProperty(required = true)
    private String declination = "";

    @Min(0)
    @NotNull
    @JsonProperty(required = true)
    private Double radius = 0.0;

    public Coordinates() {
    }


    public Coordinates(String rightAsc, String declination, Double radius) {
        this.rightAsc = rightAsc;
        this.declination = declination;
        this.radius = radius;
        if (!validator.validate(this).isEmpty()) {
            throw new ConstraintViolationException(null);
        }
    }

    /**
     * Get rightAsc
     *
     * @return rightAsc
     **/
    @Schema(description = "Right ascension")

    public String getRightAsc() {
        return rightAsc;
    }


    public Coordinates declination(String declination) {
        this.declination = declination;
        return this;
    }

    /**
     * Get declination
     *
     * @return declination
     **/
    @Schema(description = "Declination")

    public String getDeclination() {
        return declination;
    }

    public Coordinates radius(Double radius) {
        this.radius = radius;
        return this;
    }

    /**
     * Get radius
     *
     * @return radius
     **/
    @Schema(description = "Radius")

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        if (radius < 0) {
            throw new ValidationException("Radius must be positive");
        }
        this.radius = radius;
    }

    public boolean isValid() {
        Set<ConstraintViolation<Coordinates>> violations = validator.validate(this);
        return violations.isEmpty();
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinates coordinates = (Coordinates) o;
        return Objects.equals(this.rightAsc, coordinates.rightAsc) &&
                Objects.equals(this.declination, coordinates.declination) &&
                Objects.equals(this.radius, coordinates.radius);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rightAsc, declination, radius);
    }

    @Override
    public String toString() {

        String sb = "class Coordinates {\n" +
                "    rightAsc: " + toIndentedString(rightAsc) + "\n" +
                "    declination: " + toIndentedString(declination) + "\n" +
                "    radius: " + toIndentedString(radius) + "\n" +
                "}";
        return sb;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
